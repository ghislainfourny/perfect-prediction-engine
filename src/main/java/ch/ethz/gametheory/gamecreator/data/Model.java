package ch.ethz.gametheory.gamecreator.data;

import ch.ethz.gametheory.gamecreator.DragAndDropOperation;
import ch.ethz.gametheory.gamecreator.visual.ChoiceNodeShape;
import ch.ethz.gametheory.gamecreator.visual.TreeNodeShape;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;

import static javafx.collections.FXCollections.observableArrayList;
import static javafx.collections.FXCollections.observableSet;

public class Model {

    private int numberOfPlayers = 1;
    private int numberOfInformationSets = 1;

    @Nonnull
    private final ObservableList<Player> players;
    @Nonnull
    private final ObservableList<InformationSet> informationSets;
    @Nonnull
    private final ObservableList<Tree> trees;
    @Nonnull
    private final ObservableSet<Outcome> selectedOutcomes;
    @Nonnull
    private final ObservableSet<ChoiceNode> selectedChoiceNodes;
    @Nonnull
    private final DoubleProperty scale;
    @Nonnull
    private final BooleanProperty showPlayers;
    @Nonnull
    private final BooleanProperty showPayouts;

    @Nonnull
    private final ObjectProperty<TreeNodeShape<?, ?>> originNodeShape;
    @Nonnull
    private final ObjectProperty<TreeNodeShape<?, ?>> targetNodeShape;

    private DragAndDropOperation dragAndDropOperation = DragAndDropOperation.CONNECT;

    public Model() {
        players = observableArrayList(e -> new Observable[]{e.nameProperty()});
        informationSets = observableArrayList(e -> new Observable[]{e.playerProperty()});
        trees = observableArrayList();
        selectedOutcomes = observableSet(new HashSet<>());
        selectedChoiceNodes = observableSet(new HashSet<>());
        scale = new SimpleDoubleProperty(1.0);
        showPlayers = new SimpleBooleanProperty(false);
        showPayouts = new SimpleBooleanProperty(false);
        originNodeShape = new SimpleObjectProperty<>(null);
        targetNodeShape = new SimpleObjectProperty<>(null);
    }

    @Nonnull
    public ObservableList<Player> getPlayers() {
        return players;
    }

    @Nonnull
    public Player newPlayer() {
        Player newPlayer = new Player(numberOfPlayers++);
        players.add(newPlayer);
        return newPlayer;
    }

    public void removePlayer(@Nonnull Player player) {
        player.setDeleted();
        players.remove(player);
    }

    @Nonnull
    public ObservableList<InformationSet> getInformationSets() {
        return informationSets;
    }

    @Nonnull
    public InformationSet newInformationSet() {
        InformationSet newInformationSet = new InformationSet(numberOfInformationSets++);
        informationSets.add(newInformationSet);
        return newInformationSet;
    }

    public void removeInformationSet(@Nonnull InformationSet informationSet) {
        informationSet.setDeleted();
        informationSets.remove(informationSet);
    }

    @Nonnull
    public Outcome newOutcome() {
        cleanSelectedNodes();
        Outcome outcome = new Outcome(players);
        setSelectedNode(outcome);
        Tree tree = new Tree(outcome);
        trees.add(tree);
        return outcome;
    }

    @Nonnull
    public ChoiceNode newChoiceNode() {
        cleanSelectedNodes();
        ChoiceNode choiceNode = new ChoiceNode();
        setSelectedNode(choiceNode);
        Tree tree = new Tree(choiceNode);
        trees.add(tree);
        return choiceNode;
    }

    @Nonnull
    public ObservableSet<ChoiceNode> getSelectedChoiceNodes() {
        return selectedChoiceNodes;
    }

    @Nonnull
    public ObservableSet<Outcome> getSelectedOutcomes() {
        return selectedOutcomes;
    }

    public void cleanSelectedNodes() {
        for (ChoiceNode c : selectedChoiceNodes) {
            c.setSelected(false);
        }
        for (Outcome o : selectedOutcomes) {
            o.setSelected(false);
        }
        selectedChoiceNodes.clear();
        selectedOutcomes.clear();
    }

    public void setSelectedNode(@Nonnull TreeNode treeNode) {
        cleanSelectedNodes();
        if (treeNode instanceof ChoiceNode) {
            selectedChoiceNodes.add((ChoiceNode) treeNode);
        } else {
            selectedOutcomes.add((Outcome) treeNode);
        }
        treeNode.setSelected(true);
    }

    public void toggleSelectedNode(@Nonnull TreeNode treeNode) {
        if (treeNode instanceof ChoiceNode) {
            if (!selectedChoiceNodes.contains(treeNode)) {
                selectedChoiceNodes.add((ChoiceNode) treeNode);
                treeNode.setSelected(true);

            } else {
                selectedChoiceNodes.remove(treeNode);
                treeNode.setSelected(false);
            }
        } else if (treeNode instanceof Outcome) {
            if (!selectedOutcomes.contains(treeNode)) {
                selectedOutcomes.add((Outcome) treeNode);
                treeNode.setSelected(true);
            } else {
                selectedOutcomes.remove(treeNode);
                treeNode.setSelected(false);
            }
        }
    }

    public void deleteSelectedNodes() {
        deleteSelectedOutcomes();
        deleteSelectedChoiceNodes();
    }

    private void deleteSelectedOutcomes() {
        for (Outcome o : selectedOutcomes) {
            o.deleteNode();
        }
        selectedOutcomes.clear();
    }

    private void deleteSelectedChoiceNodes() {
        for (ChoiceNode t : selectedChoiceNodes) {
            List<Tree> childrenTrees = t.deleteNode();
            trees.addAll(childrenTrees);
        }
        selectedChoiceNodes.clear();
    }

    @Nonnull
    public ObservableList<Tree> getTrees() {
        return trees;
    }

    public void addForestListener(ListChangeListener<? super Tree> listener) {
        getTrees().addListener(listener);
    }

    public void removeTree(Tree tree) {
        trees.remove(tree);
    }

    @Nonnull
    public DoubleProperty scaleProperty() {
        return scale;
    }

    @Nonnull
    public BooleanProperty showPlayersProperty() {
        return showPlayers;
    }

    @Nonnull
    public BooleanProperty showPayoutsProperty() {
        return showPayouts;
    }

    public void setOriginNodeShape(@Nullable TreeNodeShape<?, ?> originNodeShape) {
        this.originNodeShape.set(originNodeShape);
    }

    @Nullable
    public TreeNodeShape<?, ?> getOriginNodeShape() {
        return originNodeShape.get();
    }

    @Nonnull
    public ObjectProperty<TreeNodeShape<?, ?>> originNodeShapeProperty() {
        return originNodeShape;
    }

    public void setTargetNodeShape(@Nullable TreeNodeShape<?, ?> targetNodeShape) {
        if (targetNodeShape != originNodeShape.get()) {
            this.targetNodeShape.set(targetNodeShape);
        }
    }

    @Nullable
    public TreeNodeShape<?, ?> getTargetNodeShape() {
        return targetNodeShape.get();
    }

    @Nonnull
    public ObjectProperty<TreeNodeShape<?, ?>> targetNodeShapeProperty() {
        return targetNodeShape;
    }

    public void connectNodes(@Nonnull ChoiceNode parent, @Nonnull TreeNode child) {
        if (!child.isDescendant(parent)) {
            Tree subtree = child.detachFromTree();
            parent.addChild(subtree.getRoot(), -1);
        }
    }

    private void swapSubtrees(TreeNode treeNodeA, TreeNode treeNodeB) {
        if (!treeNodeA.isDescendant(treeNodeB) && !treeNodeB.isDescendant(treeNodeA)) {
            Tree treeA = treeNodeA.getTree();
            Tree treeB = treeNodeB.getTree();
            int treeIndexA = trees.indexOf(treeA);
            int treeIndexB = trees.indexOf(treeB);
            ChoiceNode parentNodeA = treeNodeA.getParentNode();
            ChoiceNode parentNodeB = treeNodeB.getParentNode();
            if (parentNodeA == null) {
                if (parentNodeB == null) {
                    swapTwoRoot(treeA, treeB, treeIndexA, treeIndexB);
                } else {
                    swapOneRoot(treeNodeB, treeNodeA, treeA, treeIndexA, parentNodeB);
                }
            } else {
                if (parentNodeB == null) {
                    swapOneRoot(treeNodeA, treeNodeB, treeB, treeIndexB, parentNodeA);
                } else {
                    swapZeroRoot(treeNodeA, treeNodeB, parentNodeA, parentNodeB);
                }
            }
        }
    }

    private void swapZeroRoot(TreeNode treeNodeA, TreeNode treeNodeB, ChoiceNode parentNodeA, ChoiceNode parentNodeB) {
        int posA = parentNodeA.indexOfChild(treeNodeA);
        int posB = parentNodeB.indexOfChild(treeNodeB);
        Tree newTreeA = treeNodeA.detachFromTree();
        Tree newTreeB = treeNodeB.detachFromTree();
        parentNodeB.addChild(newTreeA.getRoot(), posB);
        parentNodeA.addChild(newTreeB.getRoot(), posA);
    }

    private void swapOneRoot(TreeNode treeNodeA, TreeNode treeNodeB, Tree treeB, int treeIndexB, ChoiceNode parentNodeA) {
        removeTree(treeB);
        int posA = parentNodeA.indexOfChild(treeNodeA);
        Tree tree = treeNodeA.detachFromTree();
        trees.add(treeIndexB, tree);
        parentNodeA.addChild(treeNodeB, posA);
    }

    private void swapTwoRoot(Tree treeA, Tree treeB, int treeIndexA, int treeIndexB) {
        if (treeIndexA < treeIndexB) {
            trees.remove(treeIndexB);
            trees.remove(treeIndexA);
            trees.add(treeIndexA, treeB);
            trees.add(treeIndexB, treeA);
        } else {
            trees.remove(treeIndexA);
            trees.remove(treeIndexB);
            trees.add(treeIndexB, treeA);
            trees.add(treeIndexA, treeB);
        }
    }


    public void setDragAndDropOperation(@Nonnull DragAndDropOperation dragAndDropOperation) {
        this.dragAndDropOperation = dragAndDropOperation;
    }

    public void commitDragAndDropAction() {
        TreeNodeShape<?, ?> originNode = originNodeShape.get();
        TreeNodeShape<?, ?> targetNode = targetNodeShape.get();
        if (originNode != targetNode && originNode != null && targetNode != null) {
            switch (dragAndDropOperation) {
                case CONNECT:
                    assert originNode instanceof ChoiceNodeShape;
                    connectNodes((ChoiceNode) originNode.getData(), targetNode.getData());
                    break;
                case SWAP:
                    swapSubtrees(originNode.getData(), targetNode.getData());
                    break;
            }
        }
        setOriginNodeShape(null);
        setTargetNodeShape(null);
    }

}
