package com.test.zk.watchers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Date;

public class AdminClient implements Watcher {

  private static Logger logger = LogManager.getLogger(Client.class);

  private ZooKeeper zk;
  private String connectString;

  public AdminClient(String connectString) {
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

  public void listState() throws KeeperException {
    try {
      try {
        Stat stat = new Stat();
        byte masterData[] = zk.getData("/master", false, stat);
        Date startDate = new Date(stat.getCtime());
        System.out.println("Master: " + new String(masterData) +
          " since " + startDate);
      } catch (NoNodeException e) {
        System.out.println("No Master");
      }
      System.out.println("Workers:");
      for (String w : zk.getChildren("/workers", false)) {
        byte data[] = zk.getData("/workers/" + w, false, null);
        String state = new String(data);
        System.out.println("\t" + w + ": " + state);
      }
      System.out.println("Tasks:");
      for (String t : zk.getChildren("/assign", false)) {
        System.out.println("\t" + t);
      }
    } catch (InterruptedException e) {
      logger.warn(e);
    }
  }

  /**
   * main
   */
  public static void main(String args[]) throws Exception {
    String zkConnectString = "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183";
    AdminClient adminClient = new AdminClient(zkConnectString);
    adminClient.startZK();
    adminClient.listState();
  }
}
