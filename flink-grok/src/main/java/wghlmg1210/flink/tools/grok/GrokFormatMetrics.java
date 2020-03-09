package wghlmg1210.flink.tools.grok;

import com.codahale.metrics.SlidingTimeWindowReservoir;
import org.apache.flink.dropwizard.metrics.DropwizardHistogramWrapper;
import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.Histogram;
import org.apache.flink.metrics.MetricGroup;

import java.util.concurrent.TimeUnit;

/**
 * Created by huangxingbiao@github.com on 09/03/2020.
 */
public class GrokFormatMetrics {

    private Histogram consumeHistogram;

    private Counter successCounter;
    private Counter failureCounter;

    public GrokFormatMetrics(MetricGroup group) {
        com.codahale.metrics.Histogram internal =
                new com.codahale.metrics.Histogram(new SlidingTimeWindowReservoir(10, TimeUnit.SECONDS));
        this.consumeHistogram = new DropwizardHistogramWrapper(internal);
        this.consumeHistogram = group.histogram("", this.consumeHistogram);

        this.successCounter = group.counter("grok-format.success");
        this.failureCounter = group.counter("grok-format.failure");
    }

    public Histogram getConsumeHistogram() {
        return consumeHistogram;
    }

    public Counter getSuccessCounter() {
        return successCounter;
    }

    public Counter getFailureCounter() {
        return failureCounter;
    }
}
