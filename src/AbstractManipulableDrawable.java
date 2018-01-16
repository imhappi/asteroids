import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Convenience base abstract class for SelectableDrawable objects that can be manipulated (translated and scaled).
 * Created by Gustavo on 2017-06-18.
 */
public abstract class AbstractManipulableDrawable extends AbstractSelectableDrawable {

    /**
     * Tolerance for comparison of double coordinates for the bounding box test.
     */
    private static double TOLERANCE = 3.0;

    public AbstractManipulableDrawable() {
        super();
    }

    public AbstractManipulableDrawable(AffineTransform transform) {
        super(transform);
    }

    /**
     * Paint this SelectableDrawable using the specified Graphics2D object.
     * First, this method will call paintItself() for the actual drawing.
     * Next, if this SelectableDrawable is selected, it will draw a bounding box;
     * @param g2
     */
    @Override
    public void paint(Graphics2D g2) {
        // Paint this drawable
        this.paintItself(g2);

        // Paint the bounding box if selected
        if (this.isSelected()) {
            g2.setColor(Color.LIGHT_GRAY);
            Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_BEVEL, 0, new float[]{2}, 0); // create dashed stroke
            g2.setStroke(dashed);
            Shape boundingBox = this.transform == null ? this.getBoundingBox()
                    : this.transform.createTransformedShape(this.getBoundingBox()); // transform coordinates
            g2.draw(boundingBox); // draw rectangle
        }
    }

    /**
     * Paint this SelectableDrawable using the specified Graphics2D object.
     * Override this method and implement the algorithm for the actual drawing.
     * @param g2
     */
    protected abstract void paintItself(Graphics2D g2);

    /**
     * Returns the bounding box for this SelectableDrawable.
     */
    protected abstract Rectangle2D getBoundingBox();

    /**
     * Translates this SelectedDrawable by the specified amount
     * (this is cumulative to the current transformation.)
     * @param tx
     * @param ty
     */
    public void translate(double tx, double ty) {
        if (this.transform == null) {
            this.transform = AffineTransform.getTranslateInstance(tx, ty);
        } else {
            this.transform.translate(tx, ty);
        }
    }

    /**
     * Scales this SelectedDrawable by the specified amount
     * (this is cumulative to the current transformation.)
     * @param sx
     * @param sy
     */
    public void scale(double sx, double sy) {
        if (this.transform == null) {
            this.transform = AffineTransform.getScaleInstance(sx, sy);
        } else {
            this.transform.scale(sx, sy);
        }
    }

    /**
     * Test if the given point is on top of one of the lines of the bounding box.
     * @param point
     * @return
     */
    public BoundingBoxBorder isInBoundingBox(Point2D point) throws NoninvertibleTransformException {
        return this.isInBoundingBox(point.getX(), point.getY());
    }

    /**
     * Test if the given point (px, py) is on top of one of the lines of the bounding box.
     * @param px
     * @param py
     * @return
     */
    public BoundingBoxBorder isInBoundingBox(double px, double py) throws NoninvertibleTransformException {
        // Transform mouse coordinates to model coordinates before testing
        Point2D transformedPoint = this.transformMousePointToModel(new Point2D.Double(px, py));
        Rectangle2D boundingBox = this.getBoundingBox();
        // Test each one of the four borders
        if (this.withinToleranceInside(transformedPoint.getX(), boundingBox.getMinX())) {
            if (transformedPoint.getY() > boundingBox.getMinY() && transformedPoint.getY() < boundingBox.getMaxY()) {
                return BoundingBoxBorder.WEST;
            }
        } else if (this.withinToleranceOutside(transformedPoint.getX(), boundingBox.getMaxX())) {
            if (transformedPoint.getY() > boundingBox.getMinY() && transformedPoint.getY() < boundingBox.getMaxY()) {
                return BoundingBoxBorder.EAST;
            }
        } else if (this.withinToleranceInside(transformedPoint.getY(), boundingBox.getMinY())) {
            if (transformedPoint.getX() > boundingBox.getMinX() && transformedPoint.getX() < boundingBox.getMaxX()) {
                return BoundingBoxBorder.NORTH;
            }
        } else if (this.withinToleranceOutside(transformedPoint.getY(), boundingBox.getMaxY())) {
            if (transformedPoint.getX() > boundingBox.getMinX() && transformedPoint.getX() < boundingBox.getMaxX()) {
                return BoundingBoxBorder.SOUTH;
            }
        }
        return BoundingBoxBorder.NONE;
    }

    /*
     * Convenience method to test a distance within a tolerance limit.
     */
    private boolean withinToleranceInside(double v1, double v2) {
        return v1 - v2 < AbstractManipulableDrawable.TOLERANCE && v1 - v2 >= 0;
    }

    private boolean withinToleranceOutside(double v1, double v2) {
        return v2 - v1 < AbstractManipulableDrawable.TOLERANCE && v2 - v1 >= 0;
    }
}

/**
 * Identification of one of the four directions for resizing.
 */
enum BoundingBoxBorder {
    NORTH, SOUTH, WEST, EAST, NONE;
}
