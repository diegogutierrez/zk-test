package com.test.zk.watchers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.UUID;

public class Worker implements Watcher {

  private static Logger logger = LogManager.getLogger(Worker.class);

  private ZooKeeper zk;
  private String connectString;
  private String status;

  private static final String NODE_ID = "Worker_" + UUID.randomUUID().toString();

  public Worker(String connectString) {
    this.connectString = connectString;
  }

  @Override
  public void process(WatchedEvent event) {
    logger.info(event);
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
  public void stopZK() throws Exception { zk.close(); }


  private StringCallback createWorkerCallback = (rc, path, ctx, name) -> {
    switch (Code.get(rc)) {
      case CONNECTIONLOSS:
        register();
        break;
      case OK:
        logger.info("Worker registered successfully: {}", NODE_ID);
        break;
      case NODEEXISTS:
        logger.info("Worker already registered");
      default:
        logger.error("Something went wrong: " + KeeperException.create(Code.get(rc), path));
    }
  };

  /**
   * register
   */
  public void register() {
    zk.create("/workers/" + NODE_ID, "idle".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL,
      createWorkerCallback, null);
  }


  StatCallback statusUpdateCallback = (rc, path, ctx, stat) -> {
    switch (Code.get(rc)) {
      case CONNECTIONLOSS:
        updateStatus((String) ctx);
        return;
    }
  };

  synchronized private void updateStatus(String status) {
    if (status == this.status) {
      zk.setData("/workers/" + NODE_ID, status.getBytes(), -1, statusUpdateCallback, status);
    }
  }

  public void setStatus(String status) {
    this.status = status;
    updateStatus(status);
  }

  /**
   * Main
   */
  public static void main(String[] args) throws Exception {
    String zkConnectString = "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183";
    Worker worker = new Worker(zkConnectString);
    try {
      worker.startZK();
      worker.register();
      Thread.sleep(10000);
    } finally {
      worker.stopZK();
    }
  }
}