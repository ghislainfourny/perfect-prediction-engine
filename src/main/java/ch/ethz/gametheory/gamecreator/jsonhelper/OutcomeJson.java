package ch.ethz.gametheory.gamecreator.jsonhelper;

import ch.ethz.gametheory.gamecreator.data.Model;
import ch.ethz.gametheory.gamecreator.data.Outcome;
import ch.ethz.gametheory.gamecreator.data.TreeNode;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class OutcomeJson {
    public int nodeId;
    public Map<Integer, Integer> payouts;

    public static OutcomeJson convert(Outcome outcome, Map<TreeNode, Integer> uniqueIdentifier, AtomicInteger counter, Model model) {
        OutcomeJson outcomeJson = new OutcomeJson();
        outcomeJson.nodeId = uniqueIdentifier.getOrDefault(outcome, counter.getAndIncrement());
        uniqueIdentifier.put(outcome, outcomeJson.nodeId);
        outcomeJson.payouts = new HashMap<>();
        model.getPlayers().forEach(player -> {
            outcomeJson.payouts.put(player.getId(), outcome.getPlayerOutcome(player));
        });
        return outcomeJson;
    }
}
