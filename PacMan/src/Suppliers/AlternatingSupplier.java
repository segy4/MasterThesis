package Suppliers;

import java.util.function.Supplier;

public class AlternatingSupplier implements Supplier<Double> {
    private final int batchSize;
    private final double numberOfEpisodesWithHighValue;
    private final double numberOfEpisodesWithLowValue;
    private final double highValue;
    private final double lowValue;
    private boolean isHigh;
    private int callCount;
    private int episodeCount;

    public AlternatingSupplier(int batchSize, int numberOfEpisodesHigh, int numberOfEpisodesLow, double highSupply, double lowSupply) {
        this.batchSize = batchSize;
        this.numberOfEpisodesWithHighValue = numberOfEpisodesHigh;
        this.numberOfEpisodesWithLowValue = numberOfEpisodesLow;
        this.highValue = highSupply;
        this.lowValue = lowSupply;
        isHigh = false;
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
        if (isHigh && episodeCount % numberOfEpisodesWithHighValue == 0) {
            isHigh = false;
        }
        if (!isHigh && episodeCount % numberOfEpisodesWithLowValue == 0) {
            isHigh = true;
        }
        return isHigh ? highValue : lowValue;
    }
}
