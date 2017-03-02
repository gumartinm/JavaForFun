### WARNING!! Consumers must stop before using this tool.


#### 1. Retrieve offset ranges in topic:
```bash
kafka-run-class.sh kafka.tools.GetOffsetShell --broker-list kafka:9092,kafka:9093,kafka:9094  --topic mytopic --offsets 100000
mytopic:0:27,19
```

**0:** partition number (in this case *mytopic* only had one partition)  
**27:** latest offset  
**19:** the first available offset (the other ones must have been purged, see server configuration **log.retention.hours**)  

#### 2. Running on CLI: 
* By default reset offsets to the earliest value in every topic's partition:
```bash
java -jar build/libs/kafka-my-tools-0.1.0-SNAPSHOT.jar --bootstrap-servers kafka:9092,kafka:9093,kafka:9094 --zookeeper kafka:2181 --group-id mygroup --input-topic mytopic
```

* We can do the same for just one topic's partition:
```bash
java -jar build/libs/kafka-my-tools-0.1.0-SNAPSHOT.jar --bootstrap-servers kafka:9092,kafka:9093,kafka:9094 --zookeeper kafka:2181 --group-id mygroup --input-topic mytopic --partition 0
```

* Choosing the offset value for just one topic's partition:
```bash
java -jar build/libs/kafka-my-tools-0.1.0-SNAPSHOT.jar --bootstrap-servers kafka:9092,kafka:9093,kafka:9094 --zookeeper kafka:2181 --group-id mygroup --input-topic mytopic --offset 24 --partition 0
```




bin/kafka-run-class.sh kafka.tools.GetOffsetShell --broker-list kafka:9092,kafka:9093,kafka:9094  --topic mytopic --offsets 100000
mytopic:0:27,19

Si ponemos menos de 19 en offset:
java -jar build/libs/kafka-my-tools-0.1.0-SNAPSHOT.jar --bootstrap-servers kafka:9092,kafka:9093,kafka:9094 --zookeeper kafka:2181 --group-id mygroup --input-topic mytopic --offset 18 --partition 0

El consumidor al arrancar da este error:
2017-03-01 09:54:33.800  INFO 11748 --- [afka-consumer-1] o.a.k.c.consumer.internals.Fetcher       : Fetch offset 18 is out of range for partition mytopic-0, resetting offset

El consumidor ve que 18 no existe y resetea. El reset en mi caso apuntaría al latest offset: startOffset: latest
Como había forzado el offset 18, parece ser que el reset hace que apunte al primer offset disponible (aunque no sea el último) y todo funciona ok :)


