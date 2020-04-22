package Suppliers;

import java.util.function.Supplier;

public class ExponentionalDecreaseSupplier implements Supplier<Double> {

    private final double numberOfEpisodesWithHighValue;
    private final int batchSize;
    private int callCount;
    private int episodeCount;

    public ExponentionalDecreaseSupplier(int numberOfEpisodes, int batchSize) {
        this.numberOfEpisodesWithHighValue = numberOfEpisodes;
        this.batchSize = batchSize;
        this.callCount = 0;
        this.episodeCount = 0;
    }

    @Override
    public Double get() {
        callCount++;
        if (callCount % batchSize == 0) {
            episodeCount++;
            callCount = 0;
        }
        return Math.exp(-episodeCount/(numberOfEpisodesWithHighValue*2.0));
    }
}
