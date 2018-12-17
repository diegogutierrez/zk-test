#!/bin/bash

#Scrip to start a single zk node


ZK_NODE=$1

echo "============================"
echo "stopping zk node $ZK_NODE"

cd "./nodes/zk-$ZK_NODE"
../../zookeeper/zookeeper-3.4.12/bin/zkServer.sh stop ./conf/zk-$ZK_NODE.cfg

echo "ZK node $COUNTER stopped"
