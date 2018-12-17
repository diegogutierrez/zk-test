
# Cluster scripts

In this directory there are three scripts to create, start and stop a zk cluster easily.

TODO: scripts need lot of work, exception handling, reporting, etc, :)

In order to work, first call the **create_cluster.sh** script. And then call the **start_cluster.sh**. 


## Create a ZK cluster

For example, to create a 3-node cluster installation(zk data directory, myid file, conf directory and zoo.cfg), run the following script.
The parameter is the number of zk nodes to install:

```sh
cluster$ ./create_cluster.sh 3
===============================
clearing zk cluster - init
clearing zk cluster - done
===============================
generating ips - init
Generated ips for 3 node(s)
===============================
generating zk installation for node 1 - init
ZK Node 1
	generated dir: ./nodes/zk-1
	generated data dir: ./nodes/zk-1/data
	generated myid file: ./nodes/zk-1/data/myid
	generated conf dir: ./nodes/zk-1/conf
	config file: ./nodes/zk-1/conf/zk-1.cfg
	generated configuration file: ./nodes/zk-1/conf/zk-1.cfg
generating zk installation for node 1 - done
===============================
generating zk installation for node 2 - init
ZK Node 2
	generated dir: ./nodes/zk-2
	generated data dir: ./nodes/zk-2/data
	generated myid file: ./nodes/zk-2/data/myid
	generated conf dir: ./nodes/zk-2/conf
	config file: ./nodes/zk-2/conf/zk-2.cfg
	generated configuration file: ./nodes/zk-2/conf/zk-2.cfg
generating zk installation for node 2 - done
===============================
generating zk installation for node 3 - init
ZK Node 3
	generated dir: ./nodes/zk-3
	generated data dir: ./nodes/zk-3/data
	generated myid file: ./nodes/zk-3/data/myid
	generated conf dir: ./nodes/zk-3/conf
	config file: ./nodes/zk-3/conf/zk-3.cfg
	generated configuration file: ./nodes/zk-3/conf/zk-3.cfg
generating zk installation for node 3 - done

```


then check the cluster installation:

```sh
cluster$ tree . -I zookeeper
.
├── create_cluster.sh
├── ips
│   └── ips
├── nodes
│   ├── zk-1
│   │   ├── conf
│   │   │   └── zk-1.cfg
│   │   └── data
│   │       └── myid
│   ├── zk-2
│   │   ├── conf
│   │   │   └── zk-2.cfg
│   │   └── data
│   │       └── myid
│   └── zk-3
│       ├── conf
│       │   └── zk-3.cfg
│       └── data
│           └── myid
├── scripts
│   ├── clear.sh
│   ├── generate_node.sh
│   ├── ips.sh
│   ├── start_node.sh
│   └── stop_node.sh
├── start_cluster.sh
└── stop_cluster.sh

- Main scritps : create_cluster.sh, start_cluster.sh and stop_cluster.sh
- Configuraion IPs: the script generates a list of ZK node ips necessary for the configuration in the /ips directory 
- ZK nodes: the directory /nodes contains the ZK node installation directory for each node in the cluster
- The /scrips directory contains helper scripts

```
## Start a ZK cluster

After the cluster nodes were installed, the following script will start each node
```sh
cluster$ ./start_cluster.sh 3
============================
starting zk node 1
/home/diego/zk-test/cluster
/home/diego/zk-test/cluster/nodes/zk-1
ZooKeeper JMX enabled by default
Using config: ./conf/zk-1.cfg
Starting zookeeper ... STARTED
ZK node  started
============================
starting zk node 2
/home/diego/zk-test/cluster
/home/diego/zk-test/cluster/nodes/zk-2
ZooKeeper JMX enabled by default
Using config: ./conf/zk-2.cfg
Starting zookeeper ... STARTED
ZK node  started
============================
starting zk node 3
/home/diego/zk-test/cluster
/home/diego/zk-test/cluster/nodes/zk-3
ZooKeeper JMX enabled by default
Using config: ./conf/zk-3.cfg
Starting zookeeper ... STARTED
ZK node  started


```

Then check all nodes were started:

```sh
r$ ps aux | grep java | grep zk
diego    26527  0.5  0.3 7299532 50992 pts/1   Sl   21:40   0:00 java -Dzookeeper.log.dir=. -Dzookeeper.root.logger=INFO,CONSOLE -cp /home/diego/zk-test/cluster/zookeeper/zookeeper-3.4.12/bin/../build/classes:/home/diego/zk-test/cluster/zookeeper/zookeeper-3.4.12/bin/../build/lib/*.jar:/home/diego/zk-test/cluster/zookeeper/zookeeper-3.4.12/bin/../lib/slf4j-log4j12-1.7.25.jar:/home/diego/zk-test/cluster/zookeeper/zookeeper-3.4.12/bin/../lib/slf4j-api-1.7.25.jar:/home/diego/zk-test/cluster/zookeeper/zookeeper-3.4.12/bin/../lib/netty-3.10.6.Final.jar:/home/diego/zk-test/cluster/zookeeper/zookeeper-3.4.12/bin/../lib/log4j-1.2.17.jar:/home/diego/zk-test/cluster/zookeeper/zookeeper-3.4.12/bin/../lib/jline-0.9.94.jar:/home/diego/zk-test/cluster/zookeeper/zookeeper-3.4.12/bin/../lib/audience-annotations-0.5.0.jar:/home/diego/zk-test/cluster/zookeeper/zookeeper-3.4.12/bin/../zookeeper-3.4.12.jar:/home/diego/zk-test/cluster/zookeeper/zookeeper-3.4.12/bin/../src/java/lib/*.jar:/home/diego/zk-test/cluster/zookeeper/zookeeper-3.4.12/bin/../conf: -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.local.only=false org.apache.zookeeper.server.quorum.QuorumPeerMain ./conf/zk-1.cfg
diego    26560  0.5  0.3 7567844 51224 pts/1   Sl   21:40   0:00 java -Dzookeeper.log.dir=. -Dzookeeper.root.logger=INFO,CONSOLE -cp /home/diego/zk-test/cluster/zookeeper/zookeeper-3.4.12/bin/../build/classes:/home/diego/zk-test/cluster/zookeeper/zookeeper-3.4.12/bin/../build/lib/*.jar:/home/diego/zk-test/cluster/zookeeper/zookeeper-3.4.12/bin/../lib/slf4j-log4j12-1.7.25.jar:/home/diego/zk-test/cluster/zookeeper/zookeeper-3.4.12/bin/../lib/slf4j-api-1.7.25.jar:/home/diego/zk-test/cluster/zookeeper/zookeeper-3.4.12/bin/../lib/netty-3.10.6.Final.jar:/home/diego/zk-test/cluster/zookeeper/zookeeper-3.4.12/bin/../lib/log4j-1.2.17.jar:/home/diego/zk-test/cluster/zookeeper/zookeeper-3.4.12/bin/../lib/jline-0.9.94.jar:/home/diego/zk-test/cluster/zookeeper/zookeeper-3.4.12/bin/../lib/audience-annotations-0.5.0.jar:/home/diego/zk-test/cluster/zookeeper/zookeeper-3.4.12/bin/../zookeeper-3.4.12.jar:/home/diego/zk-test/cluster/zookeeper/zookeeper-3.4.12/bin/../src/java/lib/*.jar:/home/diego/zk-test/cluster/zookeeper/zookeeper-3.4.12/bin/../conf: -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.local.only=false org.apache.zookeeper.server.quorum.QuorumPeerMain ./conf/zk-2.cfg
diego    26607  0.5  0.3 7299532 50444 pts/1   Sl   21:40   0:00 java -Dzookeeper.log.dir=. -Dzookeeper.root.logger=INFO,CONSOLE -cp /home/diego/zk-test/cluster/zookeeper/zookeeper-3.4.12/bin/../build/classes:/home/diego/zk-test/cluster/zookeeper/zookeeper-3.4.12/bin/../build/lib/*.jar:/home/diego/zk-test/cluster/zookeeper/zookeeper-3.4.12/bin/../lib/slf4j-log4j12-1.7.25.jar:/home/diego/zk-test/cluster/zookeeper/zookeeper-3.4.12/bin/../lib/slf4j-api-1.7.25.jar:/home/diego/zk-test/cluster/zookeeper/zookeeper-3.4.12/bin/../lib/netty-3.10.6.Final.jar:/home/diego/zk-test/cluster/zookeeper/zookeeper-3.4.12/bin/../lib/log4j-1.2.17.jar:/home/diego/zk-test/cluster/zookeeper/zookeeper-3.4.12/bin/../lib/jline-0.9.94.jar:/home/diego/zk-test/cluster/zookeeper/zookeeper-3.4.12/bin/../lib/audience-annotations-0.5.0.jar:/home/diego/zk-test/cluster/zookeeper/zookeeper-3.4.12/bin/../zookeeper-3.4.12.jar:/home/diego/zk-test/cluster/zookeeper/zookeeper-3.4.12/bin/../src/java/lib/*.jar:/home/diego/zk-test/cluster/zookeeper/zookeeper-3.4.12/bin/../conf: -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.local.only=false org.apache.zookeeper.server.quorum.QuorumPeerMain ./conf/zk-3.cfg

```


## Stop a ZK cluster

```sh
cluster$ ./stop_cluster.sh 3
============================
stopping zk node 1
ZooKeeper JMX enabled by default
Using config: ./conf/zk-1.cfg
Stopping zookeeper ... STOPPED
ZK node  stopped
============================
stopping zk node 2
ZooKeeper JMX enabled by default
Using config: ./conf/zk-2.cfg
Stopping zookeeper ... STOPPED
ZK node  stopped
============================
stopping zk node 3
ZooKeeper JMX enabled by default
Using config: ./conf/zk-3.cfg
Stopping zookeeper ... STOPPED
ZK node  stopped

```

Then check the processes are not running with 
```
ps aux | grep java | grep zk
```