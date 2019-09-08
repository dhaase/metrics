package eu.dirk.haase.metric;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(BlockJUnit4ClassRunner.class)
public class PerformanceTest {

    private static final long AGGREGATE_BUSY_TIME_NANOS = calcAggregateBusyTimeNanos();
    private static final int DEPARTING_COUNT = 10;
    private static final long OBSERVATION_TIME_NANOS = 3000;

    private static long calcAggregateBusyTimeNanos() {
        long[] busyData = {123, 201, 311, 102, 154, 269, 341, 287, 222, 283};
        long aggregateBusyTimeNanos = 0;
        for (long d : busyData) {
            aggregateBusyTimeNanos += d;
        }
        assertThat(aggregateBusyTimeNanos).isEqualTo(2293L);
        return aggregateBusyTimeNanos;
    }

    @Test
    public void test_aggregateBusyTimeMillis() {
        // Given
        Performance performance = new Performance();
        performance.init(DEPARTING_COUNT, AGGREGATE_BUSY_TIME_NANOS, OBSERVATION_TIME_NANOS);
        // When
        double aggregateBusyTimeMillis = performance.aggregateBusyTimeMillis();
        // Then
        assertThat(aggregateBusyTimeMillis).isEqualTo(0.002293);
    }

    @Test
    public void test_observationTimeMillis() {
        // Given
        Performance performance = new Performance();
        performance.init(DEPARTING_COUNT, AGGREGATE_BUSY_TIME_NANOS, OBSERVATION_TIME_NANOS);
        // When
        double observationTimeMillis = performance.observationTimeMillis();
        // Then
        assertThat(observationTimeMillis).isEqualTo(0.003);
    }

    @Test
    public void test_queueLength() {
        // Given
        Performance performance = new Performance();
        performance.init(DEPARTING_COUNT, AGGREGATE_BUSY_TIME_NANOS, OBSERVATION_TIME_NANOS);
        // When
        double queueLength = performance.queueLength();
        // Then
        assertThat(queueLength).isEqualTo(3.243281471004243);
    }

    @Test
    public void test_residenceTime() {
        // Given
        Performance performance = new Performance();
        performance.init(DEPARTING_COUNT, AGGREGATE_BUSY_TIME_NANOS, OBSERVATION_TIME_NANOS);
        // When
        double residenceTime = performance.averageResidenceTimeMillis();
        // Then
        assertThat(residenceTime).isEqualTo(0.0009729844413012728);
    }

    @Test
    public void test_serviceTime() {
        // Given
        Performance performance = new Performance();
        performance.init(DEPARTING_COUNT, AGGREGATE_BUSY_TIME_NANOS, OBSERVATION_TIME_NANOS);
        // When
        double serviceTime = performance.averageServiceTimeMillis();
        // Then
        assertThat(serviceTime).isEqualTo(0.0002293);
    }

    @Test
    public void test_throughput() {
        // Given
        Performance performance = new Performance();
        performance.init(DEPARTING_COUNT, AGGREGATE_BUSY_TIME_NANOS, OBSERVATION_TIME_NANOS);
        // When
        double throughput = performance.throughputPerMilli();
        // Then
        assertThat(throughput).isEqualTo(3333.3333333333335);
    }

    @Test
    public void test_utilization() {
        // Given
        Performance performance = new Performance();
        performance.init(DEPARTING_COUNT, AGGREGATE_BUSY_TIME_NANOS, OBSERVATION_TIME_NANOS);
        // When
        double utilization = performance.utilization();
        // Then
        assertThat(utilization).isEqualTo(0.7643333333333333);
    }

}
