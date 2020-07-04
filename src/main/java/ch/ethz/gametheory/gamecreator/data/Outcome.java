package ch.ethz.gametheory.gamecreator.data;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Outcome extends TreeNode {

    @Nonnull
    private final Map<Player, IntegerProperty> outcomeNumbers;
    @Nonnull
    private final ObservableList<Player> players;

    public Outcome(@Nonnull ObservableList<Player> players) {
        super();
        this.outcomeNumbers = new HashMap<>();
        this.players = players;
    }

    private IntegerProperty getPlayerOutcomeProperty(Player player) {
        if (!outcomeNumbers.containsKey(player)) {
            IntegerProperty t = new SimpleIntegerProperty(0);
            t.addListener(((observableValue, number, t1) -> toggleChanged()));
            toggleChanged();
            outcomeNumbers.put(player, t);
        }
        return outcomeNumbers.get(player);
    }

    public int getPlayerOutcome(Player player) {
        return getPlayerOutcomeProperty(player).get();
    }

    public void setPlayerOutcome(Player player, int value) {
        getPlayerOutcomeProperty(player).setValue(value);
    }

    @Override
    void invalidateSolution() {
        setSolution(false);
    }

    @Override
    void findSolution(int[][] outcomes, Player[] players) {
        for (int[] o : outcomes) {
            boolean sameOutcome = true;
            for (int i = 0; i < players.length; i++) {
                sameOutcome = sameOutcome && getPlayerOutcome(players[i]) == o[i];
            }
            if (sameOutcome) {
                setSolution(true);
                return;
            }
        }
    }

    @Override
    void assignTree(@Nonnull Tree tree) {
        setTree(tree);
    }

    @Override
    boolean isDescendant(TreeNode treeNode) {
        return treeNode == this;
    }

    @Override
    public List<Tree> deleteNode() {
        ChoiceNode parentNode = getParentNode();
        if (parentNode != null) {
            parentNode.removeChild(this);
        }
        Tree tree = getTree();
        tree.toggleIntegrityChange();
        return new LinkedList<>();
    }

    private void setOutputText() {
        if (!this.players.isEmpty()) {
            StringBuilder s = new StringBuilder();
            for (Player p : players) {
                s.append(getPlayerOutcome(p)).append(", ");
            }
            s.setLength(s.length() - 2);
            getOutputText().setValue(s.toString());
        } else {
            getOutputText().setValue("");
        }
    }

    private void toggleChanged() {
        getTree().invalidateSolution();
    }

}
