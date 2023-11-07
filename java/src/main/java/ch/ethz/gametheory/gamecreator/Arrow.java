package ch.ethz.gametheory.gamecreator;

import javafx.scene.Group;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

public class Arrow extends Group {

    private final Line line;
    private final Polygon head;
    private final Polygon revHead;
    private boolean doubled;

    public Arrow(boolean doubled){
        this(doubled, new Line(), new Polygon(), new Polygon());
    }

    private Arrow(boolean doubled, Line line, Polygon head, Polygon revHead){
        super(line, head, revHead);
        this.doubled = doubled;
        this.line = line;
        this.line.setStrokeWidth(2.0);
        this.head = head;
        this.revHead = revHead;
    }

    private void updateHead(boolean reversed) {

        double sx;
        double sy;
        double ex;
        double ey;

        if (reversed){
            sx = line.getEndX();
            sy = line.getEndY();
            ex = line.getStartX();
            ey = line.getStartY();
        } else {
            sx = line.getStartX();
            sy = line.getStartY();
            ex = line.getEndX();
            ey = line.getEndY();
        }

        double headLength = 15.0;
        double headWidth = 10.0;
        double direction = (ex-sx)/(ey-sy);
        double yLength = Math.signum(sy-ey) * Math.sqrt((Math.pow(headLength, 2)/(1+Math.pow(direction, 2))));
        double xLength = Math.signum(sx-ex) * Math.sqrt((Math.pow(headLength, 2)/(1+Math.pow(1/direction, 2))));
        double yWidth = Math.signum(sx-ex) * Math.sqrt((Math.pow(headWidth, 2)/(1+Math.pow(1/direction, 2))));
        double xWidth = Math.signum(sy-ey) * Math.sqrt((Math.pow(headWidth, 2)/(1+Math.pow(direction, 2)))) ;

        if (reversed){
            revHead.getPoints().clear();
            revHead.getPoints().addAll(ex, ey,
                    ex+xLength+xWidth, ey+yLength-yWidth,
                    ex+xLength-xWidth, ey+yLength+yWidth);
        } else {
            head.getPoints().clear();
            head.getPoints().addAll(ex, ey,
                    ex+xLength+xWidth, ey+yLength-yWidth,
                    ex+xLength-xWidth, ey+yLength+yWidth);
        }

    }

    public void setFill(Paint value){
        this.line.setStroke(value);
        this.head.setFill(value);
        this.revHead.setFill(value);
    }

    public void setStartPoint(double startX, double startY){
        this.line.setStartX(startX);
        this.line.setStartY(startY);
        updateHead(false);
        if (doubled)
            updateHead(true);
    }

    public void setEndPoint(double endX, double endY){
        this.line.setEndX(endX);
        this.line.setEndY(endY);

        if (getStartX()==getEndX()&&getStartY()==getEndY()){
            this.line.setVisible(false);
            this.head.setVisible(false);
        } else {
            this.line.setVisible(true);
            this.head.setVisible(true);
            updateHead(false);
            if (doubled)
                updateHead(true);
        }
    }

    public double getStartX() {
        return line.getStartX();
    }

    public double getStartY() {
        return line.getStartY();
    }

    public double getEndX() {
        return line.getEndX();
    }

    public double getEndY() {
        return line.getEndY();
    }
}
