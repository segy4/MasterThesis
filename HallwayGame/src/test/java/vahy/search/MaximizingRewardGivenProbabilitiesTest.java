package vahy.search;

import org.testng.annotations.Test;
import vahy.api.model.State;
import vahy.environment.ActionType;
import vahy.impl.model.observation.DoubleVectorialObservation;
import vahy.impl.model.reward.DoubleScalarReward;

import java.util.Map;

public class MaximizingRewardGivenProbabilitiesTest {

    public class MaximizingRewardGivenProbabilitiesUnderTest extends MaximizingRewardGivenProbabilities {

        public DoubleScalarReward resolveRewardHandle(State<ActionType, DoubleScalarReward, DoubleVectorialObservation> state,
                                                      Map<ActionType, AbstractStateActionMetadata<DoubleScalarReward>> stateActionMap) {
            return resolveReward(state, stateActionMap);
        }
    }

    @Test
    public void DummyTest() {

    }

}
