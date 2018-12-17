#!/bin/bash

#Main script to start up a zk cluster


ZK_NODES=$1

scripts/clear.sh $ZK_NODES

scripts/ips.sh $ZK_NODES

mkdir "./nodes"
COUNTER=1
while [ $COUNTER -le $ZK_NODES ];
do
	scripts/generate_node.sh "$COUNTER"
        COUNTER=$(($COUNTER+1))
done
