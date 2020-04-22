package Suppliers;

import java.util.function.Supplier;

public class SinSupplier implements Supplier<Double> {

    private final int numberOfEpisodesForPeriod;
    private final int batchSize;
    private final double value;
    private int callCount;
    private int episodeCount;

    public SinSupplier(int numberOfEpisodesForPeriod, int batchSize, double value, int callCount, int episodeCount) {
        this.numberOfEpisodesForPeriod = numberOfEpisodesForPeriod;
        this.batchSize = batchSize;
        this.value = value;
        this.callCount = callCount;
        this.episodeCount = episodeCount;
    }

    @Override
    public Double get() {
        callCount++;
        if (callCount % batchSize == 0) {
            episodeCount++;
            callCount = 0;
        }
        return 0.5 * (1 + Math.sin(Math.PI*episodeCount/(numberOfEpisodesForPeriod/2.0)));
    }
}
