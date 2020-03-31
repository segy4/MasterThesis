package Template;

import vahy.api.experiment.ProblemConfig;

/**
 * Class that holds initial information about the problem
 * Because of the amount of information problem usually needs (tens, maybe more fields)
 * it is adviced to create a Builder class for this one.
 *
 * This class will most likely hold more information that is nessesary for
 * any given State so its adviced to use it only to create the initial State and
 * only copy information that you need.
 */
public class TemplateProblemConfig extends ProblemConfig {

    // Any number of fields that you will need for creating initial instance of the problem

    /**
     * Constructor with maximal step count bound to create a ceiling that stops our algorithm.
     * @param maximalStepCountBound ceiling on the number of steps (PLAYER actions) algorithm can make.
     */
    protected TemplateProblemConfig(int maximalStepCountBound) {
        super(maximalStepCountBound);
    }

    /**
     * Function for documenting config to a Log.
     * @return String contain information that you want in a Log.
     */
    @Override
    public String toLog() {
        return null;
    }

    /**
     * Function for documenting config to a File.
     * @return String contain information that you want in a File.
     */
    @Override
    public String toFile() {
        return null;
    }
}
