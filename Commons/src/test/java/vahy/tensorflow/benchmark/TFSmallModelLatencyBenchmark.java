package vahy.tensorflow.benchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.RunnerException;
import vahy.tensorflow.TFHelper;
import vahy.tensorflow.TFModelImproved;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.SplittableRandom;
import java.util.concurrent.TimeUnit;

@Fork(value = 3, jvmArgs = {"-Xms4G", "-Xmx4G"})
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 3)
@Measurement(iterations = 5)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class TFSmallModelLatencyBenchmark {

    public static void main(String[] args) throws IOException, RunnerException {
        org.openjdk.jmh.Main.main(args);
    }

    private TFModelImproved model;
    private double[] dummyInput_0;
    private double[] dummyInput_1;

    private double[][] dummyBatchedInput_0;
    private double[][] dummyBatchedInput_1;

    @Setup
    public void setUp() throws IOException, InterruptedException {
        SplittableRandom random = new SplittableRandom(0);

        int instanceCount = 1024;
        int inputDim = 2;
        int outputDim = 1;

        double[][] inputTrainData = {{0.0, 0.0}, {1.0, 0.0}, {0.0, 1.0}, {1.0, 1.0}};
        double[][] targetTrainData = {{-1}, {-1}, {-1}, {1}};

        int trainingIterations_IN_MODEL = 1;
        int trainingIterations_OUTSIDE_MODEL = 1000;
        int batchSize = 1;

        var modelPath = Paths.get(TFSmallModelLatencyBenchmark.class.getClassLoader().getResource("tfModelPrototypes/AND.py").getPath());

        var modelRepresentation = TFHelper.loadTensorFlowModel(modelPath, random.nextLong(), inputDim, outputDim, 0);
        model = new TFModelImproved(inputDim, outputDim, batchSize, trainingIterations_IN_MODEL, 0.7, 0.0001, modelRepresentation, 1, random);

        for (int i = 0; i < trainingIterations_OUTSIDE_MODEL; i++) {
            TFHelper.trainingLoop(inputTrainData, targetTrainData, model);
        }

        dummyInput_0 = random.doubles(inputDim).toArray();
        dummyInput_1 = random.doubles(inputDim).toArray();


        dummyBatchedInput_0 = new double[instanceCount][];
        dummyBatchedInput_1 = new double[instanceCount][];

        for (int i = 0; i < instanceCount; i++) {
            dummyBatchedInput_0[i] = random.doubles(inputDim).toArray();
            dummyBatchedInput_1[i] = random.doubles(inputDim).toArray();
        }

    }

    @TearDown
    public void tearDown() {
        model.close();
    }

    @Benchmark
    public void singlePrediction_0(Blackhole bh) {
        bh.consume(model.predict(dummyInput_0));
    }

    @Benchmark
    public void singlePrediction_1(Blackhole bh) {
        bh.consume(model.predict(dummyInput_1));
    }

    @Benchmark
    public void batchedPrediction_0(Blackhole bh) {
        bh.consume(model.predict(dummyBatchedInput_0));
    }

    @Benchmark
    public void batchedPrediction_1(Blackhole bh) {
        bh.consume(model.predict(dummyBatchedInput_1));
    }

}
