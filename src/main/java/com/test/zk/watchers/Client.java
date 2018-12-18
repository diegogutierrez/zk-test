package com.test.zk.watchers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException.ConnectionLossException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public class Client implements Watcher {

  private static Logger logger = LogManager.getLogger(Client.class);

  private ZooKeeper zk;
  private String connectString;

  public Client(String connectString) {
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
  public void stopZK() throws Exception { zk.close(); }

  @Override
  public void process(WatchedEvent event) {
    logger.info(event);
  }

  /**
   * Queueing tasks
   */
  String queueCommand(String command) throws Exception {
    while (true) {
      try {
        String taskName = zk.create("/tasks/task-", command.getBytes(), Ids.OPEN_ACL_UNSAFE,
          CreateMode.PERSISTENT_SEQUENTIAL);
        logger.info("Task [{}] created", taskName);
        return taskName;
      } catch (NodeExistsException e) {
        throw new Exception("Task already exists", e);
      } catch (ConnectionLossException e) {
      }
    }
  }


  /**
   * Main
   */
  public static void main(String[] args) throws Exception {
    String zkConnectString = "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183";
    Client client = new Client(zkConnectString);
    try {
      client.startZK();
      client.queueCommand("command 2");
      Thread.sleep(10000);
    } finally {
      client.stopZK();
    }
  }
}