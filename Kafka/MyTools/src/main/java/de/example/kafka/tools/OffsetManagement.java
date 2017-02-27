package de.example.kafka.tools;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import kafka.api.OffsetRequest;

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
import kafka.admin.TopicCommand;
import kafka.utils.ZkUtils;


public class OffsetManagement {
    private static final int EXIT_CODE_SUCCESS = 0;
    private static final int EXIT_CODE_ERROR = 1;

    private static OptionSpec<String> bootstrapServerOption;
    private static OptionSpec<String> zookeeperOption;
    private static OptionSpec<String> applicationIdOption;
    private static OptionSpec<String> inputTopicsOption;
    private static OptionSpec<Long> offsetOption;
    private static OptionSpec<Integer> partitionOption;


    private OptionSet options = null;
    private final Properties consumerConfig = new Properties();
    private final List<String> allTopics = new LinkedList<>();

    public int run(final String[] args) {
        return run(args, new Properties());
    }

    public int run(final String[] args, final Properties config) {
        consumerConfig.clear();
        consumerConfig.putAll(config);

        int exitCode = EXIT_CODE_SUCCESS;

        AdminClient adminClient = null;
        ZkUtils zkUtils = null;
        try {
            parseArguments(args);

            adminClient = AdminClient.createSimplePlaintext(this.options.valueOf(bootstrapServerOption));
            final String groupId = this.options.valueOf(applicationIdOption);

            zkUtils = ZkUtils.apply(options.valueOf(zookeeperOption),
                30000,
                30000,
                JaasUtils.isZkSecurityEnabled());

            allTopics.clear();
            allTopics.addAll(scala.collection.JavaConversions.seqAsJavaList(zkUtils.getAllTopics()));

            resetInputAndInternalAndSeekToEndIntermediateTopicOffsets();
            deleteInternalTopics(zkUtils);
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

    private void resetInputAndInternalAndSeekToEndIntermediateTopicOffsets() {
        final List<String> inputTopics = options.valuesOf(inputTopicsOption);

        if (inputTopics.size() == 0) {
            System.out.println("No input or intermediate topics specified. Skipping seek.");
            return;
        } else {
            if (inputTopics.size() != 0) {
                System.out.println("Resetting offsets to zero for input topics " + inputTopics + " and all internal topics.");
            }
        }

        final Properties config = new Properties();
        config.putAll(consumerConfig);
        config.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, options.valueOf(bootstrapServerOption));
        config.setProperty(ConsumerConfig.GROUP_ID_CONFIG, options.valueOf(applicationIdOption));
        config.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        final Set<String> topicsToSubscribe = new HashSet<>(inputTopics.size());
        for (final String topic : inputTopics) {
            if (!allTopics.contains(topic)) {
                System.err.println("Input topic " + topic + " not found. Skipping.");
            } else {
                topicsToSubscribe.add(topic);
            }
        }
        for (final String topic : allTopics) {
            if (isInternalTopic(topic)) {
                topicsToSubscribe.add(topic);
            }
        }

        try (final KafkaConsumer<byte[], byte[]> client = new KafkaConsumer<>(config, new ByteArrayDeserializer(), new ByteArrayDeserializer())) {
            client.subscribe(topicsToSubscribe);
            client.poll(1);

            final Set<TopicPartition> partitions = client.assignment();
            final Set<TopicPartition> inputAndInternalTopicPartitions = new HashSet<>();

            for (final TopicPartition p : partitions) {
                final String topic = p.topic();
                if (isInputTopic(topic) || isInternalTopic(topic)) {
                    inputAndInternalTopicPartitions.add(p);
                } else {
                    System.err.println("Skipping invalid partition: " + p);
                }
            }

            if (inputAndInternalTopicPartitions.size() > 0) {
                client.seekToBeginning(inputAndInternalTopicPartitions);
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
    
    private void deleteInternalTopics(final ZkUtils zkUtils) {
        System.out.println("Deleting all internal/auto-created topics for application " + options.valueOf(applicationIdOption));

        for (final String topic : allTopics) {
            if (isInternalTopic(topic)) {
                final TopicCommand.TopicCommandOptions commandOptions = new TopicCommand.TopicCommandOptions(new String[]{
                    "--zookeeper", options.valueOf(zookeeperOption),
                    "--delete", "--topic", topic});
                try {
                    TopicCommand.deleteTopic(zkUtils, commandOptions);
                } catch (final RuntimeException e) {
                    System.err.println("ERROR: Deleting topic " + topic + " failed.");
                    throw e;
                }
            }
        }

        System.out.println("Done.");
    }

    private boolean isInternalTopic(final String topicName) {
        return topicName.startsWith(options.valueOf(applicationIdOption) + "-")
            && (topicName.endsWith("-changelog") || topicName.endsWith("-repartition"));
    }

    public static void main(final String[] args) {
        System.exit(new OffsetManagement().run(args));
    }

}

