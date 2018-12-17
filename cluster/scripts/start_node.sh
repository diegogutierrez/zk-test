#!/bin/bash

#Scrip to start a single zk node


ZK_NODE=$1

echo "============================"
echo "starting zk node $ZK_NODE"
echo $PWD

cd "./nodes/zk-$ZK_NODE"
echo $PWD
../../zookeeper/zookeeper-3.4.12/bin/zkServer.sh start ./conf/zk-$ZK_NODE.cfg

echo "ZK node $COUNTER started"
