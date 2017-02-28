package de.example.kafka.tools;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.security.JaasUtils;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import kafka.admin.AdminClient;
import kafka.api.OffsetRequest;
import kafka.utils.ZkUtils;


/**
 * Only tested on KAFKA 0.10.1.1 
 *
 * Based on {@link https://github.com/apache/kafka/blob/trunk/core/src/main/scala/kafka/tools/StreamsResetter.java}
 *
 */
public class OffsetManagement {
    private static final int EXIT_CODE_SUCCESS = 0;
    private static final int EXIT_CODE_ERROR = 1;

    private static OptionSpec<String> bootstrapServerOption;
    private static OptionSpec<String> zookeeperOption;
    private static OptionSpec<String> applicationIdOption;
    private static OptionSpec<String> inputTopicsOption;
    private static OptionSpec<Long> offsetOption;
    private static OptionSpec<Integer> partitionOption;


    private OptionSet options;
    private final List<String> allTopics = new LinkedList<>();

    public static void main(final String[] args) {
        System.exit(new OffsetManagement().run(args));
    }
    
    public int run(final String[] args) {
        return run(args, new Properties());
    }

    public int run(final String[] args, final Properties config) {
        int exitCode = EXIT_CODE_SUCCESS;

        AdminClient adminClient = null;
        ZkUtils zkUtils = null;
        try {
            parseArguments(args);

            adminClient = AdminClient.createSimplePlaintext(this.options.valueOf(bootstrapServerOption));
            
            zkUtils = ZkUtils.apply(options.valueOf(zookeeperOption),
                30000,
                30000,
                JaasUtils.isZkSecurityEnabled());

            allTopics.addAll(scala.collection.JavaConversions.seqAsJavaList(zkUtils.getAllTopics()));

            resetInputAndSeekToEndIntermediateTopicOffsets();
        } catch (final Throwable e) {
            exitCode = EXIT_CODE_ERROR;
            System.err.println("ERROR: " + e.getMessage());
        } finally {
            if (adminClient != null) {
                adminClient.close();
            }
            if (zkUtils != null) {
                zkUtils.close();
            }
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
        inputTopicsOption = optionParser.accepts("input-topics", "Comma-separated list of user input topics")
            .withRequiredArg()
            .ofType(String.class)
            .withValuesSeparatedBy(',')
            .describedAs("list");
        offsetOption = optionParser.accepts("offset", "The offset id to consume from, default to -2 which means from beginning; while value -1 means from end")
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
        final List<String> inputTopics = options.valuesOf(inputTopicsOption);

        if (inputTopics.size() == 0) {
            System.out.println("No input or intermediate topics specified. Skipping seek.");
            return;
        } else {
            if (inputTopics.size() != 0) {
                System.out.println("Resetting offsets to zero for input topics " + inputTopics);
            }
        }

        final Properties config = new Properties();
        config.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, options.valueOf(bootstrapServerOption));
        config.setProperty(ConsumerConfig.GROUP_ID_CONFIG, options.valueOf(applicationIdOption));
        config.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        final Set<String> topicsToSubscribe = new HashSet<>(inputTopics.size());
        for (final String topic : inputTopics) {
            if (!allTopics.contains(topic)) {
                System.out.println("Input topic " + topic + " not found. Skipping.");
            } else {
                topicsToSubscribe.add(topic);
            }
        }

        try (final KafkaConsumer<byte[], byte[]> client = new KafkaConsumer<>(config, new ByteArrayDeserializer(), new ByteArrayDeserializer())) {
            client.subscribe(topicsToSubscribe);
            client.poll(1);

            final Set<TopicPartition> partitions = client.assignment();
            final Set<TopicPartition> inputTopicPartitions = new HashSet<>();

            for (final TopicPartition partition : partitions) {
                final String topic = partition.topic();
                if (isInputTopic(topic)) {
                    inputTopicPartitions.add(partition);
                } else {
                    System.out.println("Skipping partition: " + partition);
                }
            }

            if (inputTopicPartitions.size() > 0) {
                client.seekToBeginning(inputTopicPartitions);
            }

            Integer partition = options.valueOf(partitionOption);
            if (partition == Integer.MIN_VALUE) {
            	
            	for (final TopicPartition p : partitions) {
            		client.position(p);
            		
            		Long offset = options.valueOf(offsetOption);
            		if (offset != OffsetRequest.EarliestTime()) {
            			client.seek(p, options.valueOf(offsetOption));
            		}
            	}
            	
            } else {
            	for (final TopicPartition p : partitions) {
            		
            		if (partition == p.partition()) {
                		client.position(p);
                		
                		Long offset = options.valueOf(offsetOption);
                		if (offset != OffsetRequest.EarliestTime()) {
                			client.seek(p, options.valueOf(offsetOption));
                		}
            		}
            		
            	}
            }
            client.commitSync();
            
            
        } catch (final RuntimeException e) {
            System.err.println("ERROR: Resetting offsets failed.");
            throw e;
        }

        System.out.println("Done.");
    }

    private boolean isInputTopic(final String topic) {
        return options.valuesOf(inputTopicsOption).contains(topic);
    }

}

