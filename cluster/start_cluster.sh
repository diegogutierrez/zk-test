#!/bin/bash

#Scrip to start a created zk cluster


ZK_NODES=$1

COUNTER=1
while [ $COUNTER -le $ZK_NODES ];
do
	scripts/start_node.sh "$COUNTER"
        COUNTER=$(($COUNTER+1))
done
