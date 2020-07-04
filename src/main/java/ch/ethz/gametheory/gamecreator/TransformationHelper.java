package ch.ethz.gametheory.gamecreator;

import ch.ethz.gametheory.gamecreator.visual.TreeNodeShape;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;

public class TransformationHelper {

    public static ObjectBinding<Point2D> getTransformedBinding(TreeNodeShape<?, ?> node, Pane pane) {
        return Bindings.createObjectBinding(() -> {
                    Bounds nodeLocal = node.getBoundsInLocal();
                    double x = node.getCenterX() - nodeLocal.getMinX();
                    double y = node.getCenterY() - nodeLocal.getMinY();
                    Bounds nodeScene = node.getLocalToSceneTransform().transform(nodeLocal);
                    Bounds newLocal = pane.sceneToLocal(nodeScene);
                    return new Point2D(newLocal.getMinX() + x, newLocal.getMinY() + y);
                }, node.boundsInLocalProperty(), node.centerXProperty(), node.centerYProperty(), node.localToSceneTransformProperty(),
                pane.localToSceneTransformProperty(), node.localToParentTransformProperty()
        );
    }

}
