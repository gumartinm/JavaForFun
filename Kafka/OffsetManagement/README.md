### WARNING!! Consumers must not be running while using this tool.

#### 1. The following examples are using one Kafka cluster with one Zookeeper server:
##### Kafka cluster with 3 brokers:
**broker 0:** kafka:9092  
**broker 1:** kafka:9093  
**broker 2:** kafka:9094    
##### Zookeeper server: kafka:2181

#### 2. Find out offset ranges in topic called *mytopic*:
Kafka enables us to retrieve offset information from one topic using the following command:  
```bash
kafka-run-class.sh kafka.tools.GetOffsetShell --broker-list kafka:9092,kafka:9093,kafka:9094  --topic mytopic --offsets 100000
mytopic:0:27,19
```

**0:** partition number (in this case *mytopic* only had one partition)  
**27:** latest offset  
**19:** the first available offset (the other offsets must have been purged, see server configuration **log.retention.hours**)  


#### 3. How to build this tool?: 
You will need a java virtual machine on your computer. Once you have downloaded it you can build this tool by means of the following command:
```bash
./gradlew clean build
```

#### 4. Running OffsetManagement tool from CLI: 
* By default this tool will reset offsets to the earliest value in every topic's partition:
```bash
java -jar build/libs/kafka-tool-offset-management-0.1.0-SNAPSHOT.jar --bootstrap-servers kafka:9092,kafka:9093,kafka:9094 --zookeeper kafka:2181 --group-id mygroup --input-topic mytopic
```

* We can do the same for just one topic's partition (the partition 0):
```bash
java -jar build/libs/kafka-tool-offset-management-0.1.0-SNAPSHOT.jar --bootstrap-servers kafka:9092,kafka:9093,kafka:9094 --zookeeper kafka:2181 --group-id mygroup --input-topic mytopic --partition 0
```

* Choosing the offset value for just one topic's partition:
```bash
java -jar build/libs/kafka-tool-offset-management-0.1.0-SNAPSHOT.jar --bootstrap-servers kafka:9092,kafka:9093,kafka:9094 --zookeeper kafka:2181 --group-id mygroup --input-topic mytopic --offset 24 --partition 0
```

#### 5. What if you choose an offset value out of the current range? :
```bash
kafka-run-class.sh kafka.tools.GetOffsetShell --broker-list kafka:9092,kafka:9093,kafka:9094  --topic mytopic --offsets 100000
mytopic:0:27,19
```
```bash
java -jar build/libs/kafka-tool-offset-management-0.1.0-SNAPSHOT.jar --bootstrap-servers kafka:9092,kafka:9093,kafka:9094 --zookeeper kafka:2181 --group-id mygroup --input-topic mytopic --offset 18 --partition 0
```

Consumers will complain with this **INFO level** message (nothing will go wrong):  
```java
2017-03-01 09:54:33.800  INFO 11748 --- [afka-consumer-1] o.a.k.c.consumer.internals.Fetcher       : Fetch offset 18 is out of range for partition mytopic-0, resetting offset
```