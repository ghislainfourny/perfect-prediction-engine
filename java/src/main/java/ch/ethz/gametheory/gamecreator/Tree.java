package ch.ethz.gametheory.gamecreator;

import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Tree {

    private TreeNode root;
    private Set<TreeNode> nodes;
    private BooleanProperty integrityChange;
    private InvalidationListener nodeListener;
    private InvalidationListener outcomeListener;

    Tree(TreeNode root) {
        this(root, new HashSet<>(Collections.singletonList(root)));
    }

    Tree(TreeNode root, Set<TreeNode> nodes) {
        this.root = root;
        this.nodes = nodes;
        this.integrityChange = new SimpleBooleanProperty();
        this.nodeListener = (n) -> {
            invalidateSolution();
            toggleIntegrityChange();
        };
        this.outcomeListener = (n) -> invalidateSolution();
        initializeNodes(nodes);
        toggleIntegrityChange();
    }


    public TreeNode getRoot() {
        return root;
    }

    public Set<TreeNode> getNodes() {
        return nodes;
    }

    BooleanProperty integrityChangeProperty() {
        return this.integrityChange;
    }

    private void toggleIntegrityChange() {
        this.integrityChange.set(!this.integrityChange.get());
    }

    private void cleanupNodes(Set<TreeNode> subgraph) {
        subgraph.forEach((node -> {
            node.setTree(null);
            if (node instanceof ChoiceNode)
                node.changedProperty().removeListener(nodeListener);
            else
                node.changedProperty().removeListener(outcomeListener);
        }));
    }

    private void initializeNodes(Set<TreeNode> subgraph) {
        subgraph.forEach((node -> {
            node.setTree(this);
            if (node instanceof ChoiceNode)
                node.changedProperty().addListener(nodeListener);
            else
                node.changedProperty().addListener(outcomeListener);
        }));
    }

    public void printGraph(Set<TreeNode> graph) {
        for (TreeNode n : graph) {
            System.out.print("Node: " + n);
            if (n instanceof ChoiceNode) {
                System.out.println(((ChoiceNode) n).getGraphChildren());
            } else {
                System.out.println();
            }
        }
    }

    // remove node n and return subtree with n as the root
    public Set<TreeNode> removeSubtree(TreeNode node) {
        Set<TreeNode> subgraph = new HashSet<>();
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(node);
        while (!queue.isEmpty()) {
            TreeNode n = queue.poll();
            subgraph.add(n);
            nodes.remove(n);
            toggleIntegrityChange();
            if (n instanceof ChoiceNode)
                queue.addAll(((ChoiceNode) n).getGraphChildren());
        }

        nodes.forEach((n) -> {
            if (n instanceof ChoiceNode)
                ((ChoiceNode) n).removeChild(node);
        });

        cleanupNodes(subgraph);
        return subgraph;
    }

    public List<Tree> deleteNode(TreeNode node) {
        List<Tree> childrenTrees = null;
        if (node instanceof ChoiceNode) {
            List<TreeNode> children = ((ChoiceNode) node).getGraphChildren();
            childrenTrees = new LinkedList<>();
            int size = children.size();
            for (int i = 0; i < size; i++) {
                TreeNode child = children.get(0);
                Set<TreeNode> subtree = removeSubtree(child);
                Tree newTree = new Tree(child, subtree);
                childrenTrees.add(newTree);
            }
        }
        nodes.forEach(n -> {
            if (n instanceof ChoiceNode) ((ChoiceNode) n).removeChild(node);
        });
        nodes.remove(node);
        cleanupNodes(Collections.singleton(node));
        toggleIntegrityChange();

        return childrenTrees;
    }

    // append subtree with root as child to node parent
    public void appendSubgraph(Set<TreeNode> subgraph, TreeNode parent, TreeNode child, int pos) {
        if (parent instanceof ChoiceNode)
            ((ChoiceNode) parent).addChild(child, pos);

        initializeNodes(subgraph);
        nodes.addAll(subgraph);
    }

    private boolean isDescendantOf(TreeNode parent, TreeNode descendant) {
        boolean isDescendant = false;
        if (parent instanceof ChoiceNode) {
            Queue<TreeNode> queue = new LinkedList<>(((ChoiceNode) parent).getGraphChildren());
            while (!queue.isEmpty() && !isDescendant) {
                TreeNode n = queue.poll();
                isDescendant = n == descendant;
                if (n instanceof ChoiceNode)
                    queue.addAll(((ChoiceNode) n).getGraphChildren());
            }
        }
        return isDescendant;
    }

    public void swap(TreeNode originNode, TreeNode targetNode) {
        boolean related = false;
        if (nodes.contains(targetNode)) {
            related = isDescendantOf(originNode, targetNode) || isDescendantOf(targetNode, originNode);
        }

        if (!related) {
            AtomicReference<TreeNode> parent = new AtomicReference<>();
            AtomicInteger position = new AtomicInteger(0);
            if (originNode != root) {
                nodes.forEach((n) -> {
                    if (n instanceof ChoiceNode) {
                        List<TreeNode> children = ((ChoiceNode) n).getGraphChildren();
                        if (children.contains(originNode)) {
                            parent.compareAndSet(null, n);
                            position.set(children.indexOf(originNode));
                        }
                    }
                });
                Set<TreeNode> originSubtree = removeSubtree(originNode);
                Set<TreeNode> targetSubtree = targetNode.getTree().appendAndRemove(targetNode, originNode, originSubtree);
                appendSubgraph(targetSubtree, parent.get(), targetNode, position.get());
            } else {
                if (targetNode != targetNode.getTree().getRoot()) {
                    targetNode.getTree().swap(targetNode, originNode);
                } else {
                    cleanupNodes(getNodes());
                    Set<TreeNode> newNodes = targetNode.getTree().getNodes();
                    TreeNode newRoot = targetNode.getTree().getRoot();
                    targetNode.getTree().setTree(getRoot(), getNodes());
                    this.nodes = newNodes;
                    this.root = newRoot;
                    initializeNodes(getNodes());
                    toggleIntegrityChange();
                }
            }
        }
    }

    private void setTree(TreeNode root, Set<TreeNode> nodes) {
        cleanupNodes(this.nodes);
        this.root = root;
        this.nodes = nodes;
        initializeNodes(nodes);
        toggleIntegrityChange();
    }

    private Set<TreeNode> appendAndRemove(TreeNode targetNode, TreeNode originNode, Set<TreeNode> originSubtree) {
        AtomicReference<TreeNode> parent = new AtomicReference<>();
        AtomicInteger position = new AtomicInteger(0);
        if (targetNode != root) {
            nodes.forEach((n) -> {
                if (n instanceof ChoiceNode) {
                    int index = ((ChoiceNode) n).removeChild(targetNode);
                    if (-1 < index) {
                        parent.set(n);
                        position.set(index);
                    }
                }
            });
            appendSubgraph(originSubtree, parent.get(), originNode, position.get());
        } else {
            this.root = originNode;
            this.nodes = originSubtree;
            initializeNodes(originSubtree);
        }
        return removeSubtree(targetNode);
    }

    private void invalidateSolution() {
        this.nodes.forEach(node -> {
            if (node instanceof Outcome)
                node.setSolution(false);
        });
    }

    public void setSolution(Integer[][] outcomes, Player[] players) {
        this.nodes.forEach(node -> {
            if (node instanceof Outcome) {
                boolean isSolution = false;
                for (Integer[] o : outcomes) {
                    boolean sameOutcome = true;
                    for (int i = 0; i < players.length; i++) {
                        sameOutcome = sameOutcome && ((Outcome) node).getPlayerOutcome(players[i]) == o[i];
                    }
                    isSolution = isSolution || sameOutcome;
                }
                node.setSolution(isSolution);
            }
        });
    }

}
