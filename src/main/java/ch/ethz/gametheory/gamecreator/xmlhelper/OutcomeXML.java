package ch.ethz.gametheory.gamecreator.xmlhelper;

import ch.ethz.gametheory.gamecreator.data.Model;
import ch.ethz.gametheory.gamecreator.data.Outcome;
import ch.ethz.gametheory.gamecreator.data.TreeNode;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class OutcomeXML {
    public int nodeID;
    public Map<Integer, Integer> payouts;

    public static OutcomeXML convert(Outcome outcome, Map<TreeNode, Integer> uniqueIdentifier, AtomicInteger counter, Model model) {
        OutcomeXML outcomeXML = new OutcomeXML();
        outcomeXML.nodeID = uniqueIdentifier.getOrDefault(outcome, counter.getAndIncrement());
        uniqueIdentifier.put(outcome, outcomeXML.nodeID);
        outcomeXML.payouts = new HashMap<>();
        model.getPlayers().forEach(player -> {
            outcomeXML.payouts.put(player.getId(), outcome.getPlayerOutcome(player));
        });
        return outcomeXML;
    }

}
