package de.example.kafka.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.security.JaasUtils;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import kafka.api.OffsetRequest;
import kafka.utils.ZkUtils;


/**
 * Only tested on KAFKA 0.10.1.1 
 *
 * Originally taken from {@link https://github.com/apache/kafka/blob/trunk/core/src/main/scala/kafka/tools/StreamsResetter.java}
 *
 */
public class OffsetManagement {
    private static final int EXIT_CODE_SUCCESS = 0;
    private static final int EXIT_CODE_ERROR = 1;

    private static OptionSpec<String> bootstrapServerOption;
    private static OptionSpec<String> zookeeperOption;
    private static OptionSpec<String> applicationIdOption;
    private static OptionSpec<String> inputTopicOption;
    private static OptionSpec<Long> offsetOption;
    private static OptionSpec<Integer> partitionOption;


    private OptionSet options;

    public static void main(final String[] args) {
        System.exit(new OffsetManagement().run(args));
    }

    public int run(final String[] args) {
        int exitCode = EXIT_CODE_SUCCESS;

        try {
            parseArguments(args);
            resetInputAndSeekToEndIntermediateTopicOffsets();
        } catch (final Throwable e) {
            exitCode = EXIT_CODE_ERROR;
            
            System.err.println("ERROR: " + e.getMessage());
        }

        return exitCode;
    }

    private void parseArguments(final String[] args) throws IOException {
        final OptionParser optionParser = new OptionParser();
        applicationIdOption = optionParser.accepts("application-id", "The Kafka Streams application ID (application.id)")
            .withRequiredArg()
            .ofType(String.class)
            .describedAs("id")
            .required();
        bootstrapServerOption = optionParser.accepts("bootstrap-servers", "Comma-separated list of broker urls with format: HOST1:PORT1,HOST2:PORT2")
            .withRequiredArg()
            .ofType(String.class)
            .defaultsTo("localhost:9092")
            .describedAs("urls");
        zookeeperOption = optionParser.accepts("zookeeper", "Format: HOST:POST")
            .withRequiredArg()
            .ofType(String.class)
            .defaultsTo("localhost:2181")
            .describedAs("url");
        inputTopicOption = optionParser.accepts("input-topic", "Topic name")
            .withRequiredArg()
            .ofType(String.class)
            .withValuesSeparatedBy(',')
            .required()
            .describedAs("topic name");
        offsetOption = optionParser.accepts("offset", "The new offset value, default to -2 which means from beginning; while value -1 means from end")
                .withRequiredArg()
                .describedAs("consume offset")
                .ofType(Long.class)
                .defaultsTo(OffsetRequest.EarliestTime());
        partitionOption = optionParser.accepts("partition", "The partition number. All partitions by default.")
                .withRequiredArg()
                .describedAs("partition number")
                .ofType(Integer.class)
                .defaultsTo(Integer.MIN_VALUE);

        try {
            options = optionParser.parse(args);
        } catch (final OptionException e) {
            optionParser.printHelpOn(System.err);
            throw e;
        }
    }

    private void resetInputAndSeekToEndIntermediateTopicOffsets() {
        final String inputTopic = options.valueOf(inputTopicOption);

        System.out.println("Resetting offsets for input topic " + inputTopic);
 
        if (!topicExist(inputTopic)) {
            System.err.println("ERROR: chosen topic does not exist: " + inputTopic);
            
            return;
        }
        

        final Properties config = new Properties();
        config.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, options.valueOf(bootstrapServerOption));
        config.setProperty(ConsumerConfig.GROUP_ID_CONFIG, options.valueOf(applicationIdOption));
        config.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        try (final KafkaConsumer<byte[], byte[]>  client = new KafkaConsumer<>(config, new ByteArrayDeserializer(), new ByteArrayDeserializer())) {

            Collection<String> topicsToSubscribe = new ArrayList<>();
            topicsToSubscribe.add(inputTopic);
            client.subscribe(topicsToSubscribe);
            client.poll(1);

            final Collection<TopicPartition> partitions = client.assignment();
            final Collection<TopicPartition> inputTopicPartitions = filterTopicPartition(partitions);
            if (inputTopicPartitions.isEmpty()) {
            	System.err.println("ERROR: no topics with the chosen name: " + inputTopic);
            	return;
            }
            
            final Collection<TopicPartition> filteredTopicPartitions = filterInputTopicPartition(inputTopicPartitions);
            if (filteredTopicPartitions.isEmpty()) {
            	System.err.println("ERROR: no partitions with the chosen value: " + options.valueOf(partitionOption));
            	
            	return;
            }
            
            
            Long offset = options.valueOf(offsetOption);
            if (offset.equals(OffsetRequest.EarliestTime())) {
            	
                client.seekToBeginning(filteredTopicPartitions);
                for (final TopicPartition p : filteredTopicPartitions) {
            		client.position(p);
            	}

            } else {
            	
            	for (final TopicPartition p : filteredTopicPartitions) {
            		client.seek(p, offset);
            		client.position(p);
            	}
            }

            client.commitSync();
            
        } catch (final RuntimeException e) {
            System.err.println("ERROR: Resetting offsets failed.");
            throw e;
        }

        System.out.println("Done.");
    }

    private boolean isInputTopic(final TopicPartition partition) {
        final String topic = partition.topic();
        if (options.valueOf(inputTopicOption).compareTo(topic) == 0) {
        	return true;
        }
        
        return false;
    }
    
    private boolean isInputPartition(final TopicPartition partition) {
    	Integer partitionNumber = partition.partition();
	
    	Integer inputPartitionNumber = options.valueOf(partitionOption);
    	if (inputPartitionNumber == Integer.MIN_VALUE) {
    		return true;
    	}
        
    	return inputPartitionNumber.equals(partitionNumber);
    }

    
    private boolean topicExist(String inputTopic) {
        List<String> allTopics = retrieveAllTopics();
		if (!allTopics.contains(inputTopic)) {
			return false;
		}
		
		return true;
    }
    
    private List<String> retrieveAllTopics() {
        List<String> allTopics = new LinkedList<>();
        ZkUtils zkUtils = null;
        try {
            zkUtils = ZkUtils.apply(options.valueOf(zookeeperOption),
            						30000,
            						30000,
            						JaasUtils.isZkSecurityEnabled());
            allTopics.addAll(scala.collection.JavaConversions.seqAsJavaList(zkUtils.getAllTopics()));
        } finally {
        	
            if (zkUtils != null) {
                zkUtils.close();
            }
        }
        
        return allTopics;
    }
    
    private Collection<TopicPartition> filterTopicPartition(Collection<TopicPartition> partitions) {
        final Collection<TopicPartition> inputTopicPartitions = new HashSet<>();
        for (final TopicPartition partition : partitions) {
            if (isInputTopic(partition)) {
                inputTopicPartitions.add(partition);
            } else {
                System.out.println("Skipping partition: " + partition);
            }
        }
        
        return inputTopicPartitions;
    }
    
    private Collection<TopicPartition> filterInputTopicPartition(Collection<TopicPartition> partitions) {
        final Collection<TopicPartition> inputTopicPartitions = new HashSet<>();
        for (final TopicPartition partition : partitions) {
            if (isInputPartition(partition)) {
                inputTopicPartitions.add(partition);
            } else {
                System.out.println("Skipping partition: " + partition);
            }
        }
        
        return inputTopicPartitions;
    }
}

