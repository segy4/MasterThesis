package vahy.examples.bomberman;

import vahy.api.episode.PolicyShuffleStrategy;
import vahy.api.experiment.CommonAlgorithmConfigBase;
import vahy.api.experiment.SystemConfig;
import vahy.api.policy.PolicyMode;
import vahy.impl.RoundBuilder;
import vahy.impl.episode.InvalidInstanceSetupException;
import vahy.impl.learning.dataAggregator.FirstVisitMonteCarloDataAggregator;
import vahy.impl.learning.trainer.PredictorTrainingSetup;
import vahy.impl.learning.trainer.ValueDataMaker;
import vahy.impl.model.observation.DoubleVector;
import vahy.impl.policy.ValuePolicy;
import vahy.impl.predictor.DataTablePredictor;
import vahy.impl.runner.PolicyDefinition;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Example02 {

    private Example02() {}

    public static void main(String[] args) throws IOException, InvalidInstanceSetupException {

        var config = new BomberManConfig(500, true, 100, 1, 1, 4, 3, 1, 2, 0.1, BomberManInstance.BM_00, PolicyShuffleStrategy.CATEGORY_SHUFFLE);
        var systemConfig = new SystemConfig(987567, true, 7, true, 10000, 0, false, false, false, Path.of("TEST_PATH"));

        var algorithmConfig = new CommonAlgorithmConfigBase(1000, 1000);
        double discountFactor = 1;

        var playerCount = config.getPlayerCount();
        var environmentPolicyCount = config.getEnvironmentPolicyCount();

        var policyArgumentsList = new ArrayList<PolicyDefinition<BomberManAction, DoubleVector, BomberManState>>();

        for (int i = 0; i < playerCount; i++) {
            policyArgumentsList.add(createPolicyArgument(discountFactor, i + environmentPolicyCount, 1));
        }
//        policyArgumentsList.add(createPolicyArgument(discountFactor, environmentPolicyCount, 1));
//        policyArgumentsList.add(new PolicyDefinition<BomberManAction, DoubleVector, BomberManState>(
//            environmentPolicyCount + 1,
//            1,
//            (policyId, categoryId, random) -> new AbstractPolicySupplier<>(policyId, categoryId, random) {
//                @Override
//                protected Policy<BomberManAction, DoubleVector, BomberManState> createState_inner(StateWrapper<BomberManAction, DoubleVector, BomberManState> initialState, PolicyMode policyMode, int policyId, SplittableRandom random) {
//                    return new UniformRandomWalkPolicy<>(random.split(), policyId);
//                }
//            },
//            new ArrayList<>()
//        ));

        var roundBuilder = RoundBuilder.getRoundBuilder("BomberManExample01", config, systemConfig, algorithmConfig, policyArgumentsList, BomberManInstanceInitializer::new);
        var result = roundBuilder.execute();

        System.out.println(result.getEvaluationStatistics().getTotalPayoffAverage().get(1));

    }

    private static PolicyDefinition<BomberManAction, DoubleVector, BomberManState> createPolicyArgument(double discountFactor, int policyId, int categoryId) {
        var predictor = new DataTablePredictor(new double[]{0.0});
        var dataAggregator = new FirstVisitMonteCarloDataAggregator(new LinkedHashMap<>());
        var dataMaker = new ValueDataMaker<BomberManAction, BomberManState>(discountFactor, policyId, dataAggregator);

        var predictorTrainingSetup = new PredictorTrainingSetup<>(
            policyId,
            predictor,
            dataMaker,
            dataAggregator
        );

        return new PolicyDefinition<>(
            policyId,
            categoryId,
            (initialState, policyMode, policyId1, random) -> {
                if (policyMode == PolicyMode.INFERENCE) {
                    return new ValuePolicy<>(random.split(), policyId, predictor, 0.0);
                }
                return new ValuePolicy<>(random.split(), policyId, predictor, 0.2);
            },
            List.of(predictorTrainingSetup)
        );
    }

}
