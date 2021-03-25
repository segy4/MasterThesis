package vahy.impl.model.observation;

import vahy.api.model.observation.Observation;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;

public final class DoubleVector implements Observation<DoubleVector> {

//    private static final double[] PRIME_NUMBERS = {
//        30402457, 32582657, 37156667, 42643801, 43112609,
//        6972593, 13466917, 20996011, 24036583, 25964951,
//        859433, 1257787, 1398269, 2976221, 3021377,
//        86243, 110503, 132049, 216091, 756839,
//        11213, 19937, 21701, 23209, 44497,
//        3217, 4253, 4423, 9689, 9941,
//        521, 607, 1279, 2203, 2281,
//        666649, 946669, 60000049, 66000049, 66600049,
//        10619863, 6620830889d, 80630964769d, 228204732751d, 1171432692373d,
//        1398341745571d, 10963707205259d, 15285151248481d
//    };

    private final double[] observedVector;
    private int hash;
    private boolean isHashCalculated;

    public DoubleVector(double[] observedVector) {
        this.observedVector = observedVector;
        this.isHashCalculated = false;
    }

    public final double[] getObservedVector() {
        return observedVector;
    }

//    @Override
//    public final boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        DoubleVector that = (DoubleVector) o;
//
//        return Arrays.equals(getObservedVector(), that.getObservedVector());
//    }
//
////    @Override
////    public int hashCode() {
////        return Arrays.hashCode(getObservedVector());
////    }
//
//
//    @Override
//    public final int hashCode() {
////        if(!isHashCalculated) {
//////            var doubleArrayForHash = new double[observedVector.length];
//////
//////            for (int i = 0; i < doubleArrayForHash.length; i++) {
//////                doubleArrayForHash[i] = observedVector[i] * PRIME_NUMBERS[i % PRIME_NUMBERS.length];
//////            }
////            this.hash = Arrays.hashCode(observedVector);
////            this.isHashCalculated = true;
////        }
////        return hash;
//        return Arrays.hashCode(observedVector);
//    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DoubleVector that = (DoubleVector) o;

        return Arrays.equals(observedVector, that.observedVector);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(observedVector);
    }

    @Override
    public DoubleVector groupArrayOfObservations(DoubleVector[] observationArray) {
        int finalLength = 0;
        for (DoubleVector array : observationArray) {
            finalLength += array.observedVector.length;
        }

        double[] dest = null;
        int destPos = 0;

        for (DoubleVector array : observationArray)
        {
            double[] observedVector = array.observedVector;
            if (dest == null) {
                dest = Arrays.copyOf(observedVector, finalLength);
                destPos = observedVector.length;
            } else {
                System.arraycopy(observedVector, 0, dest, destPos, observedVector.length);
                destPos += observedVector.length;
            }
        }
        return new DoubleVector(dest);
    }

    @Override
    public DoubleVector groupArrayOfObservations(ArrayDeque<DoubleVector> observationArray) {
        int finalLength = 0;
        for (DoubleVector array : observationArray) {
            finalLength += array.observedVector.length;
        }

        double[] dest = null;
        int destPos = 0;

        for (DoubleVector array : observationArray)
        {
            double[] observedVector = array.observedVector;
            if (dest == null) {
                dest = Arrays.copyOf(observedVector, finalLength);
                destPos = observedVector.length;
            } else {
                System.arraycopy(observedVector, 0, dest, destPos, observedVector.length);
                destPos += observedVector.length;
            }
        }
        return new DoubleVector(dest);
    }

    @Override
    public DoubleVector groupListOfObservations(List<DoubleVector> observationArray) {
        return groupArrayOfObservations(observationArray.toArray(DoubleVector[]::new));
    }
}
