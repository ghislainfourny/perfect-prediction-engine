package ch.ethz.gametheory.gamecreator.xmlhelper;

import ch.ethz.gametheory.gamecreator.Outcome;
import ch.ethz.gametheory.gamecreator.TreeNode;
import ch.ethz.gametheory.gamecreator.data.DataModel;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class OutcomeXML {
    public int nodeID;
    public Map<Integer, Integer> payouts;

    public static OutcomeXML convert(Outcome outcome, Map<TreeNode, Integer> uniqueIdentifier, AtomicInteger counter, DataModel dataModel) {
        OutcomeXML outcomeXML = new OutcomeXML();
        outcomeXML.nodeID = uniqueIdentifier.getOrDefault(outcome, counter.getAndIncrement());
        uniqueIdentifier.put(outcome, outcomeXML.nodeID);
        outcomeXML.payouts = new HashMap<>();
        dataModel.getPlayers().forEach(player -> {
            outcomeXML.payouts.put(player.getId(), outcome.getPlayerOutcome(player));
        });
        return outcomeXML;
    }

}
