#!/bin/bash

#Scrip to start a single zk node


ZK_NODE=$1

echo "============================"

echo "Node $ZK_NODE status:"

cd "./nodes/zk-$ZK_NODE"
../../zookeeper/zookeeper-3.4.12/bin/zkServer.sh status ./conf/zk-$ZK_NODE.cfg

