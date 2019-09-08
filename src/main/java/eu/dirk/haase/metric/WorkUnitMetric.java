package eu.dirk.haase.metric;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAdder;

public class WorkUnitMetric {

    private final AtomicReference<WorkUnit> current;
    private final WorkUnitPerformance performance;
    private final WorkUnit workUnitOne;
    private final WorkUnit workUnitTwo;

    public WorkUnitMetric() {
        this.workUnitOne = new WorkUnit();
        this.workUnitTwo = new WorkUnit();
        this.performance = new WorkUnitPerformance();
        this.workUnitOne.reset();
        this.current = new AtomicReference<>(this.workUnitOne);
    }

    public Performance get() {
        if (this.current.get() == this.workUnitOne) {
            return nextObservationPeriod(this.workUnitOne, this.workUnitTwo);
        } else {
            // if (this.current.get() == this.workUnitTwo)
            return nextObservationPeriod(this.workUnitTwo, this.workUnitOne);
        }
    }

    private Performance nextObservationPeriod(final WorkUnit currWorkUnitOne, final WorkUnit nextWorkUnit) {
        nextWorkUnit.reset();
        this.current.set(nextWorkUnit);
        performance.init(currWorkUnitOne);
        return performance;
    }

    public void record(final int count, final long duration) {
        current.get().record(count, duration);
    }

    static class WorkUnit {

        private final LongAdder departingCount;
        private final AtomicLong observationStartNanos;
        private final LongAdder aggregateBusyTimeNanos;


        WorkUnit() {
            this.observationStartNanos = new AtomicLong(0);
            this.aggregateBusyTimeNanos = new LongAdder();
            this.departingCount = new LongAdder();
        }

        void record(final int count, final long duration) {
            this.aggregateBusyTimeNanos.add(duration);
            this.departingCount.add(count);
        }

        void reset() {
            this.observationStartNanos.set(System.nanoTime());
            this.departingCount.reset();
            this.aggregateBusyTimeNanos.reset();
        }
    }

    static class WorkUnitPerformance extends Performance {

        WorkUnitPerformance() {
        }

        void init(final WorkUnit workUnit) {
            init(workUnit.departingCount.sum(),
                    workUnit.aggregateBusyTimeNanos.sum(),
                    (System.nanoTime() - workUnit.observationStartNanos.get()));
        }
    }

}
