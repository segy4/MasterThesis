package Suppliers;

import java.util.function.Supplier;

public class LinearSupplier implements Supplier<Double> {
    private final int batchSize;
    private final double highValue;
    private final double lowValue;
    private final int numberOfEpisodesToBeHigh;
    private final int numberOfEpisodesToDecrease;
    private int callCount;
    private int episodeCount;

    public LinearSupplier(int batchSize, double highValue, double lowValue, int numberOfEpisodesToBeHigh, int numberOfEpisodesToDecrease) {
        this.batchSize = batchSize;
        this.highValue = highValue;
        this.lowValue = lowValue;
        this.numberOfEpisodesToBeHigh = numberOfEpisodesToBeHigh;
        this.numberOfEpisodesToDecrease = numberOfEpisodesToDecrease;
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
        if (episodeCount >= numberOfEpisodesToBeHigh + numberOfEpisodesToDecrease) {
            return lowValue;
        }
        if (episodeCount < numberOfEpisodesToBeHigh) {
            return highValue;
        }
        return highValue - ((highValue-lowValue)/numberOfEpisodesToDecrease) * episodeCount;
    }
}
