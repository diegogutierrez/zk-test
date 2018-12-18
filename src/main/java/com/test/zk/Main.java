package com.test.zk;

import com.test.zk.watchers.Master;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

  private static Logger logger = LogManager.getLogger(Main.class);

  public static void main(String[] args) throws Exception {
    String zkConnectString = "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183";
    Master master = new Master(zkConnectString);

    master.startZK();

    master.runForMaster();

    Thread.sleep(10000);

    master.stopZK();
  }
}
