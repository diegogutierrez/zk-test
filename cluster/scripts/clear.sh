#!/bin/bash

#clear all directories in the cluster

echo "==============================="
echo "clearing zk cluster - init"
ZK_NODES=$1

rm -rf "./ips"
rm -rf "./nodes"

echo "clearing zk cluster - done"
