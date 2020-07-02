package vahy.paperGenerics.policy.riskSubtree;

import com.quantego.clp.CLPVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vahy.paperGenerics.PaperStateWrapper;
import vahy.api.model.Action;
import vahy.api.model.observation.Observation;
import vahy.api.search.node.SearchNode;
import vahy.paperGenerics.PaperState;
import vahy.paperGenerics.metadata.PaperMetadata;
import vahy.paperGenerics.policy.linearProgram.AbstractLinearProgramOnTree;
import vahy.paperGenerics.policy.linearProgram.NoiseStrategy;

import java.util.List;

public class MinimalRiskReachAbilityCalculator<
    TAction extends Enum<TAction> & Action,
    TObservation extends Observation,
    TSearchNodeMetadata extends PaperMetadata<TAction>,
    TState extends PaperState<TAction, TObservation, TState>>
    implements SubtreeRiskCalculator<TAction, TObservation, TSearchNodeMetadata, TState> {

    private static final Logger logger = LoggerFactory.getLogger(MinimalRiskReachAbilityCalculator.class.getName());

    @Override
    public double calculateRisk(SearchNode<TAction, TObservation, TSearchNodeMetadata, TState> subtreeRoot) {

        if(subtreeRoot.isLeaf()) {
            return ((PaperStateWrapper<TAction, TObservation, TState>)subtreeRoot.getStateWrapper()).isRiskHit() ?  1.0 : 0.0;
        }

        var sum = 0.0;
        if(subtreeRoot.isPlayerTurn()) {
            return resolveNode(subtreeRoot);
        } else {
            for (var entry : subtreeRoot.getChildNodeMap().values()) {
                sum += entry.getSearchNodeMetadata().getPriorProbability() * resolveNode(entry);
            }
        }
        return sum;
    }

    private double resolveNode(SearchNode<TAction, TObservation, TSearchNodeMetadata, TState> node) {
        var linProgram = new AbstractLinearProgramOnTree<TAction, TObservation, TSearchNodeMetadata, TState>(false, null, NoiseStrategy.NONE) {

            @Override
            protected void setLeafObjective(SearchNode<TAction, TObservation, TSearchNodeMetadata, TState> node) {
                var inGameEntityId = node.getStateWrapper().getInGameEntityId();
                if(((PaperStateWrapper<TAction, TObservation, TState>)node.getStateWrapper()).isRiskHit()) {
                    model.setObjectiveCoefficient(node.getSearchNodeMetadata().getNodeProbabilityFlow(), 1.0);
                } else {
                    model.setObjectiveCoefficient(node.getSearchNodeMetadata().getNodeProbabilityFlow(), node.getSearchNodeMetadata().getExpectedRisk()[inGameEntityId]);
                }
            }

            @Override
            protected void setLeafObjectiveWithFlow(List<SearchNode<TAction, TObservation, TSearchNodeMetadata, TState>> searchNodes, CLPVariable parentFlow) {
                double sum = 0.0;
                for (var entry : searchNodes) {
                    var inGameEntityId = entry.getStateWrapper().getInGameEntityId();
                    var metadata = entry.getSearchNodeMetadata();
                    sum += (((PaperStateWrapper<TAction, TObservation, TState>)entry.getStateWrapper()).isRiskHit() ? 1.0 : metadata.getExpectedRisk()[inGameEntityId]) * metadata.getPriorProbability();
                }
                model.setObjectiveCoefficient(parentFlow, sum);
            }

            @Override
            protected void finalizeHardConstraints() {
                // this is it
            }
        };

        var isFeasible = linProgram.optimizeFlow(node);
        if(!isFeasible) {
            throw new IllegalStateException("Minimal risk reachAbility is not feasible. Should not happen. Investigate.");
        }
        return linProgram.getObjectiveValue();
    }

    @Override
    public String toLog() {
        return "SUBTREE_RISK_MINIMAL_RISK_REACHABILITY";
    }
}
