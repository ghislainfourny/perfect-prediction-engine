package ch.ethz.gametheory.gamecreator.visual;

import javafx.scene.layout.HBox;

import javax.annotation.Nullable;

public class Forest extends HBox {

    @Nullable
    public TreePane rotateClockwise(@Nullable TreePane treePane) {
        if (!getChildren().isEmpty() && treePane != null) {
            TreePane temp = (TreePane) getChildren().remove(getChildren().size() - 1);
            getChildren().add(0, treePane);
            return temp;
        }
        return null;
    }

    @Nullable
    public TreePane rotateAnticlockwise(@Nullable TreePane treePane) {
        if (!getChildren().isEmpty() && treePane != null) {
            this.getChildren().add(treePane);
            return (TreePane) getChildren().remove(0);
        }
        return null;
    }

}
