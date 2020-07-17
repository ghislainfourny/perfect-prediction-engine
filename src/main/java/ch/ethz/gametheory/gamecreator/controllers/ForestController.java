package ch.ethz.gametheory.gamecreator.controllers;

import ch.ethz.gametheory.gamecreator.data.Model;
import ch.ethz.gametheory.gamecreator.data.Tree;
import ch.ethz.gametheory.gamecreator.visual.Forest;
import ch.ethz.gametheory.gamecreator.visual.TreePane;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import javax.annotation.Nullable;

public class ForestController {

    private ObjectProperty<TreePane> mainTree;
    private Model model;

    @FXML
    private Forest BoxUnassignedNodes;
    @FXML
    private HBox MainTreePane;

    @Nullable
    public Tree getMainTree() {
        if (mainTree.getValue() == null) return null;
        return mainTree.getValue().getTree();
    }

    ObjectProperty<TreePane> mainTreeProperty() {
        return mainTree;
    }

    void setMainTreePane(@Nullable TreePane treePane) {
        if (treePane != null) {
            TreePane oldMainTree = mainTree.get();
            if (oldMainTree != null) {
                MainTreePane.getChildren().clear();
                BoxUnassignedNodes.getChildren().add(0, oldMainTree);
            }
            mainTree.set(treePane);
            MainTreePane.getChildren().add(treePane);
        }
    }

    void addTreeNode(Tree tree, int pos) {
        TreePane treePane = new TreePane(model, tree);
        if (pos == 0) {
            setMainTreePane(treePane);
        } else {
            BoxUnassignedNodes.getChildren().add(pos - 1, treePane);
        }
    }

    void rotateClockwise() {
        ObservableList<Tree> trees = model.getTrees();
        if (trees.size() > 1) {
            Tree oldMainTree = trees.remove(0);
            trees.add(oldMainTree);
        }
    }

    void rotateAnticlockwise() {
        ObservableList<Tree> trees = model.getTrees();
        if (trees.size() > 1) {
            Tree oldLastTree = trees.remove(trees.size() - 1);
            trees.add(0, oldLastTree);
        }
    }

    void removeTree(Tree tree) {
        if (tree == getMainTree()) {
            mainTree.set(null);
            MainTreePane.getChildren().clear();
            if (!BoxUnassignedNodes.getChildren().isEmpty()) {
                TreePane newMainTree = (TreePane) BoxUnassignedNodes.getChildren().remove(0);
                mainTree.set(newMainTree);
                setMainTreePane(newMainTree);
            }
        } else {
            ObservableList<Node> children = BoxUnassignedNodes.getChildren();
            TreePane toDelete = null;
            for (Node n : children) {
                TreePane treePane = (TreePane) n;
                if (treePane.getTree() == tree) {
                    toDelete = treePane;
                }
            }
            children.remove(toDelete);
        }
    }

    void init(Model model) {
        this.model = model;
        this.model.addForestListener(change -> {
            while (change.next()) {
                change.getRemoved().forEach(this::removeTree);
                change.getAddedSubList().forEach(tree -> addTreeNode(tree, model.getTrees().indexOf(tree)));
            }
        });
        this.mainTree = new SimpleObjectProperty<>(null);
        this.BoxUnassignedNodes.setBackground(new Background(new BackgroundFill(Color.LIGHTGREY, CornerRadii.EMPTY, Insets.EMPTY)));
    }
}
