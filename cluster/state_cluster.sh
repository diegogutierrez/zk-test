#!/bin/bash

#Script to get the status of all nodes in the  cluster


ZK_NODES=$1

COUNTER=1
while [ $COUNTER -le $ZK_NODES ];
do
	scripts/status_node.sh "$COUNTER"
        COUNTER=$(($COUNTER+1))
done
