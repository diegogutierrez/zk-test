#!/bin/bash

#generate the IPs for the zk cluster

echo "==============================="
echo "generating ips - init"

ZK_NODES=$1

COUNTER=1
rm -rf "./ips"
mkdir "./ips"

echo "#zk nodes IPs" > "./ips/ips"

while [ $COUNTER -le $ZK_NODES ];
do
	QUORUM_PORT=$((2000+$COUNTER))
	COMMUNICATION_PORT=$((3000+$COUNTER))
	echo "server.$COUNTER=127.0.0.1:$QUORUM_PORT:$COMMUNICATION_PORT" >> "./ips/ips"
        COUNTER=$(($COUNTER+1))
done

echo "Generated ips for $ZK_NODES node(s)"
