package wghlmg1210.flink.tools.grok;

import io.krakens.grok.api.Grok;
import io.krakens.grok.api.GrokCompiler;
import org.apache.commons.text.StringSubstitutor;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.ConfigurationUtils;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * Created by huangxingbiao@github.com on 09/03/2020.
 */
public class GrokFormatMain {

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        InputStream stream = GrokFormatMain.class.getClassLoader().getResourceAsStream("grok-format.conf");
        Properties props = new Properties();
        props.load(stream);

        Configuration config = ConfigurationUtils.createConfiguration(props);
        env.getConfig().setGlobalJobParameters(config);

        configCHK(config, env);

        int sourceParallelism = config.getInteger("source.parallelism", 3);
        int grokFormatParallelism = config.getInteger("grok-format.parallelism", 3);
        int sinkParallelism = config.getInteger("sink.parallelism", 3);
        String jobName = config.getString("job.name", "");

        env.addSource(createSource(config)).name("source").setParallelism(sourceParallelism)
                .flatMap(new GrokFlatMapFunction()).name("grok-format").setParallelism(grokFormatParallelism)
                .addSink(createSink(config)).name("sink").setParallelism(sinkParallelism);

        env.execute(jobName);
    }

    private static void configCHK(Configuration config, StreamExecutionEnvironment env) {
        long interval = config.getLong("checkpoint.interval", 5000);
        CheckpointConfig chkConfig = env.getCheckpointConfig();

        env.enableCheckpointing(interval);
        env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);

        chkConfig.setMaxConcurrentCheckpoints(1);
        chkConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        chkConfig.setCheckpointTimeout(60000);
        chkConfig.setMinPauseBetweenCheckpoints(5000);
        chkConfig.enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
    }

    private static SourceFunction<String> createSource(Configuration config) {
        String inputTopic = config.getString("kafka.input.topic", null);
        String bootstrap = config.getString("kafka.server", null);

        Properties props = new Properties();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, inputTopic);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);

        return new FlinkKafkaConsumer<>(inputTopic, new SimpleStringSchema(), props);
    }

    private static SinkFunction<String> createSink(Configuration config) {
        String outputTopic = config.getString("kafka.output.topic", null);
        String bootstrap = config.getString("kafka.server", null);

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        props.put(ProducerConfig.RETRIES_CONFIG, "3");
        props.put(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG, "60000");

        KafkaSerializationSchema<String> serializationSchema = (values, timestamp) -> new ProducerRecord<>(outputTopic, values.getBytes());
        return new FlinkKafkaProducer<>(outputTopic, serializationSchema, props, FlinkKafkaProducer.Semantic.EXACTLY_ONCE);
    }

    static class GrokFlatMapFunction extends RichFlatMapFunction<String, String> {

        private transient Logger logger = LoggerFactory.getLogger(this.getClass());

        private transient Grok grok;
        private transient GrokFormatMetrics metrics;
        private String format;
        private String regex;


        @Override
        public void open(Configuration parameters) throws Exception {
            super.open(parameters);
            Configuration configs = (Configuration) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();

            metrics = new GrokFormatMetrics(getRuntimeContext().getMetricGroup());

            this.format = configs.getString("grok.format", null);
            this.regex = configs.getString("grok.regex", null);

            GrokCompiler compiler = GrokCompiler.newInstance();
            compiler.registerDefaultPatterns();

            this.grok = compiler.compile(regex);
        }

        @Override
        public void flatMap(String content, Collector<String> collector) {
            long start = System.currentTimeMillis();

            String result = null;
            try {
                Map<String, Object> temp = grok.capture(content);
                result = StringSubstitutor.replace(this.format, temp);

                this.metrics.getSuccessCounter().inc();
                this.metrics.getConsumeHistogram().update(System.currentTimeMillis() - start);
            } catch (Exception ex) {
                logger.error("content: {}", content);
                this.metrics.getFailureCounter().inc();
            }

            if (result != null) collector.collect(result);
        }
    }

}
