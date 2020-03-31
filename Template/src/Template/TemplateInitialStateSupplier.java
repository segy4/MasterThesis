package Template;

import vahy.impl.episode.AbstractInitialStateSupplier;
import vahy.impl.model.observation.DoubleVector;

import java.util.SplittableRandom;

/**
 * This supplier is the builder of initial state for your game
 * Other states are reached through State class
 * Because of this, everything you will need to reach different states needs to be send to the first State
 */
public class TemplateInitialStateSupplier extends AbstractInitialStateSupplier<TemplateProblemConfig, TemplateAction, DoubleVector, TemplateEnvironmentObservation, TemplateState> {

    /**
     * Constructor for supplier
     * You can't add more paramethers as this is only created inside the algorithm with these two
     * @param problemConfig instance of ProblemConfig class, that holds all the information you gave it.
     * @param random instance of SplittableRandom class. Use this class to create random numbers if you need them.
     */
    protected TemplateInitialStateSupplier(TemplateProblemConfig problemConfig, SplittableRandom random) {
        super(problemConfig, random);
    }

    /**
     * Method called at the start of the algorithm to create initial state
     * Then never called again
     * @param problemConfig instance of ProblemConfig class, that holds all the information you gave it. Same as constructor.
     * @param random instance of SplittableRandom class. Use this class to create random numbers if you need them. Same as constructor.
     * @return The initial state. Instance of State class.
     */
    @Override
    protected TemplateState createState_inner(TemplateProblemConfig problemConfig, SplittableRandom random) {
        return new TemplateState();
    }
}
