package vahy.testDomain.model;

import vahy.api.model.Action;

public enum TestAction implements Action {

    A(0, 3,  'A', 1),
    B(1, 3, 'B', 2),
    C(2, 3, 'C', 3),

    X(0, 3, 'X', -1),
    Y(1, 3, 'Y', -2),
    Z(2, 3, 'Z', -3);

    private final int localIndex;
    private final int sameEntityCount;
    private final char charRepresentation;
    private final double reward;

    TestAction(int localIndex, int sameEntityCount, char charRepresentation, double reward) {
        this.localIndex = localIndex;
        this.sameEntityCount = sameEntityCount;
        this.charRepresentation = charRepresentation;
        this.reward = reward;
    }

    public char getCharRepresentation() {
        return charRepresentation;
    }

    public double getReward() {
        return reward;
    }

    @Override
    public int getLocalIndex() {
        return localIndex;
    }

    @Override
    public int getCountOfAllActionsFromSameEntity() {
        return sameEntityCount;
    }

    @Override
    public boolean isShadowAction() {
        return false;
    }
}
