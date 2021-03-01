package vahy.integration.alphazero;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import vahy.api.experiment.CommonAlgorithmConfigBase;
import vahy.api.experiment.ProblemConfig;
import vahy.api.experiment.SystemConfig;
import vahy.examples.simplifiedHallway.SHAction;
import vahy.examples.simplifiedHallway.SHConfigBuilder;
import vahy.examples.simplifiedHallway.SHInstance;
import vahy.examples.simplifiedHallway.SHInstanceSupplier;
import vahy.examples.simplifiedHallway.SHState;
import vahy.impl.RoundBuilder;
import vahy.impl.learning.dataAggregator.FirstVisitMonteCarloDataAggregator;
import vahy.impl.learning.trainer.PredictorTrainingSetup;
import vahy.impl.model.observation.DoubleVector;
import vahy.impl.policy.alphazero.AlphaZeroDataMaker_V1;
import vahy.impl.policy.alphazero.AlphaZeroDataTablePredictor;
import vahy.impl.policy.alphazero.AlphaZeroPolicyDefinitionSupplier;
import vahy.impl.runner.PolicyDefinition;
import vahy.test.ConvergenceAssert;
import vahy.utils.JUnitParameterizedTestHelper;
import vahy.utils.StreamUtils;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Stream;

public class AlphaZeroSH05Test {

    private PolicyDefinition<SHAction, DoubleVector, SHState> getPolicyDefinition(ProblemConfig config, int treeExpansionCount) {
        var playerId = 1;

        var actionClass = SHAction.class;
        var totalEntityCount = 2;
        var totalActionCount = actionClass.getEnumConstants().length;
        var defaultPrediction = new double[totalEntityCount + totalActionCount];
        for (int i = totalEntityCount; i < defaultPrediction.length; i++) {
            defaultPrediction[i] = 1.0 / totalActionCount;
        }

        var trainablePredictor = new AlphaZeroDataTablePredictor(defaultPrediction, 0.25, totalEntityCount);
        var dataAggregator = new FirstVisitMonteCarloDataAggregator(new LinkedHashMap<>());
        var episodeDataMaker = new AlphaZeroDataMaker_V1<SHAction, SHState>(playerId, totalActionCount, 1, dataAggregator);

        var predictorTrainingSetup = new PredictorTrainingSetup<SHAction, DoubleVector, SHState>(
            playerId,
            trainablePredictor,
            episodeDataMaker,
            dataAggregator
        );

        return new AlphaZeroPolicyDefinitionSupplier<SHAction, SHState>(SHAction.class, totalEntityCount, config).getPolicyDefinition(
            playerId,
            1,
            1,
            () -> 0.5,
            treeExpansionCount,
            predictorTrainingSetup
        );
    }

    public static Stream<Arguments> params() {
        return JUnitParameterizedTestHelper.cartesian(
            JUnitParameterizedTestHelper.cartesian(
                Stream.of(
                    Arguments.of(0.0, 288.0, 290.0),
                    Arguments.of(1.0, 288.0, 290.0),
                    Arguments.of(0.5, 288.0, 290.0)
                ),
                Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)),
            StreamUtils.getSeedStream(947,5)
        );
    }

    @ParameterizedTest(name = "Trap probability {0} to reach [{1}, {2}] expectedPayoff with tree update count {3} and seed {4}")
    @MethodSource("params")
    public void convergence05Test(double trapProbability, double expectedMin, double expectedMax, int treeUpdateCount, long seed) {
        var config = new SHConfigBuilder()
            .isModelKnown(true)
            .reward(100)
            .gameStringRepresentation(SHInstance.BENCHMARK_05)
            .maximalStepCountBound(100)
            .stepPenalty(1)
            .trapProbability(trapProbability)
            .buildConfig();

        var systemConfig = new SystemConfig(
            seed,
            false,
            ConvergenceAssert.TEST_THREAD_COUNT,
            false,
            10000,
            0,
            false,
            false,
            false,
            Path.of("TEST_PATH"));

        var algorithmConfig = new CommonAlgorithmConfigBase(100, 150);

        var playerSupplier = getPolicyDefinition(config, treeUpdateCount);
        var roundBuilder = RoundBuilder.getRoundBuilder("AlphaZeroSh05Test", config, systemConfig, algorithmConfig, List.of(playerSupplier), SHInstanceSupplier::new);
        var result = roundBuilder.execute();

        ConvergenceAssert.assertConvergenceResult(expectedMin, expectedMax, result.getEvaluationStatistics().getTotalPayoffAverage().get(playerSupplier.getPolicyId()), "Payoff");
    }


}
