package Template;

import vahy.api.model.observation.FixedModelObservation;
import vahy.utils.ImmutableTuple;

import java.util.List;

/**
 * This class holds map between possible OPPONENT actions and their probabilities
 * This template is the easiest implementation.
 * You can add as many fields as you want as the only one creating instances of this class and using it are you.
 * The only nessesary thing is the method getProbabilities()
 * to create a map between possible OPPONENT actions and their probabilities
 */
public class TemplateEnvironmentObservation implements FixedModelObservation<TemplateAction> {

    /*
    private final ImmutableTuple<List<TemplateAction>, List<Double>> probabilities;

    public TemplateEnvironmentObservation(ImmutableTuple<List<TemplateAction>, List<Double>> probabilities) {
        this.probabilities = probabilities;
    }
    */

    /**
     * Method to create a map between OPPONENT actions and their probabilities
     * @return map between OPPONENT actions and their probabilities
     */
    @Override
    public ImmutableTuple<List<TemplateAction>, List<Double>> getProbabilities() {
        return null /* probabilities */;
    }
}
