#!/bin/bash

#Scrip to stop a zk cluster


ZK_NODES=$1

COUNTER=1
while [ $COUNTER -le $ZK_NODES ];
do
	scripts/stop_node.sh "$COUNTER"
        COUNTER=$(($COUNTER+1))
done
