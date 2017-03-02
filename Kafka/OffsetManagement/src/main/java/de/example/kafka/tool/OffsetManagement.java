package de.example.kafka.tool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

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
 * 
 * Only tested on KAFKA 0.10.1.1 
 *
 */
public class OffsetManagement {
    private static final int EXIT_CODE_SUCCESS = 0;
    private static final int EXIT_CODE_ERROR = 1;

    private static OptionSpec<String> bootstrapServerOption;
    private static OptionSpec<String> zookeeperOption;
    private static OptionSpec<String> groupIdOption;
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
            manageOffsets();
        } catch (final Throwable ex) {
            exitCode = EXIT_CODE_ERROR;
            
            System.err.println("ERROR: ");
            ex.printStackTrace();
        }

        return exitCode;
    }

    private void parseArguments(final String[] args) throws IOException {
        final OptionParser optionParser = new OptionParser();
        groupIdOption = optionParser.accepts("group-id", "Consumers group ID")
            .withRequiredArg()
            .ofType(String.class)
            .describedAs("consumers group id")
            .required();
        bootstrapServerOption = optionParser.accepts("bootstrap-servers", "Comma-separated list of broker urls with format: HOST1:PORT1,HOST2:PORT2")
            .withRequiredArg()
            .ofType(String.class)
            .defaultsTo("localhost:9092")
            .describedAs("urls")
            .required();
        zookeeperOption = optionParser.accepts("zookeeper", "Format: HOST:POST")
            .withRequiredArg()
            .ofType(String.class)
            .defaultsTo("localhost:2181")
            .describedAs("url")
            .required();
        inputTopicOption = optionParser.accepts("input-topic", "Topic name")
            .withRequiredArg()
            .ofType(String.class)
            .withValuesSeparatedBy(',')
            .required()
            .describedAs("topic name");
        offsetOption = optionParser.accepts("offset", "The new offset value, default to -2 which means from beginning; while value -1 means from end")
                .withRequiredArg()
                .describedAs("new offset")
                .ofType(Long.class)
                .defaultsTo(OffsetRequest.EarliestTime());
        partitionOption = optionParser.accepts("partition", "The partition number. All partitions by default.")
                .withRequiredArg()
                .describedAs("partition number")
                .ofType(Integer.class)
                .defaultsTo(Integer.MIN_VALUE);

        try {
            options = optionParser.parse(args);
        } catch (final OptionException ex) {
            optionParser.printHelpOn(System.err);
            throw ex;
        }
    }

    private void manageOffsets() {
        final String inputTopic = options.valueOf(inputTopicOption);
        if (!isAvailable(inputTopic)) {
            System.err.println("Chosen topic does not exist: " + inputTopic);
            return;
        }
        

        final Properties config = new Properties();
        config.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, options.valueOf(bootstrapServerOption));
        config.setProperty(ConsumerConfig.GROUP_ID_CONFIG, options.valueOf(groupIdOption));
        config.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        try (final KafkaConsumer<byte[], byte[]> client = new KafkaConsumer<>(config, new ByteArrayDeserializer(), new ByteArrayDeserializer())) {

            Collection<String> topicsToSubscribe = new ArrayList<>();
            topicsToSubscribe.add(inputTopic);
            client.subscribe(topicsToSubscribe);
            client.poll(1);

            final Collection<TopicPartition> partitions = client.assignment();
            if (partitions.isEmpty()) {
            	System.err.println("No partitions for input topic: " + inputTopic);
            	return;
            }
            
            final Collection<TopicPartition> filteredTopicPartitions = filterTopicPartition(partitions);
            if (filteredTopicPartitions.isEmpty()) {
            	System.err.println("No partitions with the chosen value: " + options.valueOf(partitionOption));
            	
            	return;
            }
            
            
            Long offset = options.valueOf(offsetOption);
            if (offset.equals(OffsetRequest.EarliestTime())) {
            	
                client.seekToBeginning(filteredTopicPartitions);
                for (final TopicPartition partition : filteredTopicPartitions) {
            		client.position(partition);
            	}

            } else {
            	
            	for (final TopicPartition partition : filteredTopicPartitions) {
            		client.seek(partition, offset);
            		client.position(partition);
            	}
            }

            client.commitSync();
        }
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
    
    private boolean isAvailable(String inputTopic) {
        List<String> allTopics = retrieveAllTopics();
		if (!allTopics.contains(inputTopic)) {
			return false;
		}
		
		return true;
    }
    
    private Collection<TopicPartition> filterTopicPartition(Collection<TopicPartition> partitions) {        
        return partitions
        		.stream() 			
        		.filter(this::isInputPartition)	
        		.collect(Collectors.toList());
    }
    
    private boolean isInputPartition(final TopicPartition partition) {
    	Integer inputPartitionNumber = options.valueOf(partitionOption);
    	if (inputPartitionNumber == Integer.MIN_VALUE) {
    		return true;
    	}
        
    	Integer partitionNumber = partition.partition();

    	return inputPartitionNumber.equals(partitionNumber);
    }
}

