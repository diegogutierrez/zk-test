package com.test.zk.watchers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.apache.zookeeper.AsyncCallback.ChildrenCallback;
import org.apache.zookeeper.AsyncCallback.DataCallback;
import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class Master implements Watcher {

  private static Logger logger = LogManager.getLogger(Master.class);

  private ZooKeeper zk;
  private String connectString;
  private boolean isLeader = false;
  private ChildrenCache workersCache;

  private static final String NODE_ID = "Master_" + UUID.randomUUID().toString();

  public Master(String connectString) {
    this.connectString = connectString;
  }

  /**
   * Start ZK
   *
   * @throws IOException
   */
  public void startZK() throws IOException {
    logger.info("Starting ZK");
    try {
      zk = new ZooKeeper(connectString, 15000, this);
      logger.info("ZK started");
    } catch (IOException e) {
      logger.error("Can not connect to zk", e);
      throw e;
    }
  }

  /**
   * Stop ZK
   *
   * @throws Exception
   */
  public void stopZK() throws Exception {
    logger.info("ZK closed");
    zk.close();
  }


  private DataCallback checkMasterCallback = (rc, path, ctx, data, stat) -> {
    logger.info("Response from checking master: [{}]", rc);
    switch (Code.get(rc)) {
      case CONNECTIONLOSS:
        checkMaster();
        return;
      case NONODE:
        runForMaster();
        return;
      case OK:
        break;
      default:
        logger.warn("Unexpected response code: {}", Code.get(rc));
    }
  };

  /**
   * Checks if this node is the leader.
   *
   * @return True if the current worker is the leader. False otherwise
   */
  public void checkMaster() {
    logger.info("Checking master");
    zk.getData("/master", false, checkMasterCallback, null);
  }

  private StringCallback masterCreateCallback = (rc, path, ctx, name) -> {
    logger.info("Response of create master: [{}]", rc);
    switch (Code.get(rc)) {
      case CONNECTIONLOSS:
        logger.info("Connection loss, trying again");
        checkMaster();
        return;
      case OK:
        isLeader = true;
        logger.info("I'm the leader now");
        //TODO take leadership
        break;

      case NODEEXISTS:
        logger.info("Master already exists");
        isLeader = false;
        masterExists();
        break;

      default:
        isLeader = false;
    }
    logger.info(("I'm " + (isLeader ? "" : "not ") + "the leader"));
  };

  /**
   * Runs until become the master worker, or another worker becomes the master
   *
   * @throws InterruptedException
   * @throws KeeperException
   */
  public void runForMaster() {
    logger.info("Running for master");
    zk.create("/master", NODE_ID.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL, masterCreateCallback, null);
  }

  @Override
  public void process(WatchedEvent event) {
    logger.info(event);
  }

  public boolean isLeader() {
    return isLeader;
  }

  StringCallback createParentCallback = (rc, path, ctx, name) -> {
    switch (Code.get(rc)) {
      case CONNECTIONLOSS:
        createParent(path, (byte[]) ctx);
        break;
      case OK:
        logger.info("Parent created");
        break;
      case NODEEXISTS:
        logger.warn("Parent already registered: " + path);
        break;
      default:
        logger.error("Something went wrong: ",
          KeeperException.create(Code.get(rc), path));
    }
  };

  /**
   * bootstrap method
   */
  public void boostrap() {
    createParent("/workers", new byte[0]);
    createParent("/assign", new byte[0]);
    createParent("/tasks", new byte[0]);
    createParent("/status", new byte[0]);
  }

  void createParent(String path, byte[] data) {
    zk.create(path,
      data,
      Ids.OPEN_ACL_UNSAFE,
      CreateMode.PERSISTENT,
      createParentCallback,
      data);//passing the data again just in case, to handle the CONNECTIONLOSS exceptions, we need to retry the
    // create step
  }


  /**
   * Watcher to get delete events on /master znode. If so, then try to become the leader
   */
  Watcher masterExistsWatcher = new Watcher() {
    public void process(WatchedEvent e) {
      if (e.getType() == EventType.NodeDeleted) {
        assert "/master".equals(e.getPath());
        runForMaster();
      }
    }
  };


  StatCallback masterExistsCallback = (rc, path, ctx, stat) -> {
    switch (Code.get(rc)) {
      case CONNECTIONLOSS:
        masterExists();
        break;
      case OK:
        if (stat == null) {
          runForMaster();
        }
        break;
      default:
        checkMaster();
        break;
    }
  };


  /**
   * Sets a watcher to /master znode
   */
  void masterExists() {
    zk.exists("/master",
      masterExistsWatcher,
      masterExistsCallback,
      null);
  }

  /**
   *
   */

  Watcher workersChangeWatcher = (WatchedEvent e) -> {
    if (e.getType() == EventType.NodeChildrenChanged) {
      getWorkers();
    }
  };

  ChildrenCallback workersGetChildrenCallback = (rc, path, ctx, children) -> {
    switch (Code.get(rc)) {
      case CONNECTIONLOSS:
        getWorkers();
        break;
      case OK:
        logger.info("Successfully got a list of workers: " + children.size() + " workers");
        reassignAndSet(children);
      default:
        logger.error("getChildren failed", KeeperException.create(Code.get(rc), path));
    }
  };

  private void getWorkers() {
    zk.getChildren("/workers",
      workersChangeWatcher,
      workersGetChildrenCallback,
      null);
  }

  private void reassignAndSet(List<String> children) {
    List<String> toProcess;
    if (workersCache == null) {
      workersCache = new ChildrenCache(children);
      toProcess = null;
    } else {
      logger.info("Removing and setting");
      toProcess = workersCache.removedAndSet(children);//removed workers(I think? :) )
    }

    if (toProcess != null) {
      for (String worker : toProcess) {
        //TODO: getAbsentWorkerTasks(worker);
      }
    }
  }


  /**
   * Main
   */
  public static void main(String[] args) throws Exception {
    String zkConnectString = "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183";
    Master master = new Master(zkConnectString);
    try {
      master.startZK();
      master.runForMaster();
      Thread.sleep(10000);
    } finally {
      master.stopZK();
    }
  }
}