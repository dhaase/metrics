package eu.dirk.haase.metric;

import java.util.concurrent.TimeUnit;

public class Performance {

    private final static double ONE_MILLI_TO_NANO_FACTOR = TimeUnit.MILLISECONDS.toNanos(1);

    private long aggregateBusyTimeNanos;
    private long departingCount;
    private long observationTimeNanos;

    public Performance() {
    }

    public double aggregateBusyTimeMillis() {
        return aggregateBusyTimeNanos / ONE_MILLI_TO_NANO_FACTOR;
    }

    public long departingCount() {
        return departingCount;
    }

    public void init(final long departingCount,
                     final long aggregateBusyTimeNanos,
                     final long observationTimeNanos) {
        this.departingCount = departingCount;
        this.aggregateBusyTimeNanos = aggregateBusyTimeNanos;
        this.observationTimeNanos = observationTimeNanos;
    }

    public double observationTimeMillis() {
        return observationTimeNanos / ONE_MILLI_TO_NANO_FACTOR;
    }

    public double queueLength() {
        final double utilization = utilization();
        return utilization / (1 - utilization);
    }

    public double averageResidenceTimeMillis() {
        return averageServiceTimeMillis() / (1 - utilization());
    }

    public double averageServiceTimeMillis() {
        return aggregateBusyTimeMillis() / (double) this.departingCount;
    }

    public double throughputPerMilli() {
        return (double) this.departingCount / observationTimeMillis();
    }

    public double utilization() {
        return (double) this.aggregateBusyTimeNanos / (double) this.observationTimeNanos;
    }

}