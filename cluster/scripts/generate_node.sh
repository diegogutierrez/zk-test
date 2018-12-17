#!/bin/bash

#generates a zk installation: data, myid and configuration files

ZK_NODE=$1

echo "==============================="
echo "generating zk installation for node $ZK_NODE - init"

#main zk node path
ZK_PATH="./nodes/zk-$ZK_NODE"
mkdir $ZK_PATH
echo "ZK Node $ZK_NODE"
echo -e "\tgenerated dir: $ZK_PATH"

# data dirs and files

ZK_DATA_PATH="$ZK_PATH/data"
echo -e "\tgenerated data dir: $ZK_DATA_PATH"
mkdir $ZK_DATA_PATH
ZK_MYID="$ZK_PATH/data/myid"
echo "$ZK_NODE" > $ZK_MYID
echo -e "\tgenerated myid file: $ZK_MYID"

# conf files
ZK_CONF_PATH="$ZK_PATH/conf"
mkdir $ZK_CONF_PATH
echo -e "\tgenerated conf dir: $ZK_CONF_PATH"
ZK_CONF_FILE="$ZK_PATH/conf/zk-$ZK_NODE.cfg"

echo "#configuration for zk node $ZK_NODE" > $ZK_CONF_FILE
CLIENT_PORT=$(($ZK_NODE+2180))
echo  -e "\tconfig file: $ZK_CONF_FILE"
echo "#config file zk node $COUNTER" >> $ZK_CONF_FILE
echo "tickTime=2000" >> $ZK_CONF_FILE
echo "initLimit=10" >> $ZK_CONF_FILE
echo "syncLimit=5" >> $ZK_CONF_FILE
echo "dataDir=./data" >> $ZK_CONF_FILE
echo "clientPort=$CLIENT_PORT" >> $ZK_CONF_FILE
echo "$(cat ./ips/ips)" >> $ZK_CONF_FILE

echo -e "\tgenerated configuration file: $ZK_CONF_FILE"

echo "generating zk installation for node $ZK_NODE - done"
