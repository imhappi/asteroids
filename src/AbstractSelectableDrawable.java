import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

/**
 * Convenience base abstract class for SelectableDrawable objects that can have a transformation matrix applied before drawing.
 * Created by Gustavo on 2017-06-17.
 */
public abstract class AbstractSelectableDrawable implements SelectableDrawable {

    /**
     * The selection flag for this Drawable.
     */
    protected boolean selected;

    /**
     * The current transformation matrix;
     */
    protected AffineTransform transform;

    public AbstractSelectableDrawable() {
        this.selected = false;
    }

    public AbstractSelectableDrawable(AffineTransform transform) {
        this.selected = false;
        this.transform = transform;
    }

    /**
     * Marks this SelectableDrawable as selected.
     */
    public void select() {
        this.selected = true;
    }

    /**
     * Marks this SelectableDrawable as not selected.
     */
    public void deselect() {
        this.selected = false;
    }

    /**
     * Returns true if this SelectableDrawable is currently marked as selected.
     */
    public boolean isSelected() {
        return this.selected;
    }

    /**
     * Returns the current transformation matrix for this SelectableDrawable.
     * This matrix can be further manipulated to change the transformation.
     * @return
     */
    public AffineTransform getTransform() {
        return transform;
    }

    /**
     * Sets the transformation matrix for this SelectableDrawable.
     * @param transform
     */
    public void setTransform(AffineTransform transform) {
        this.transform = transform;
    }

    @Override
    public boolean contains(Point2D point) throws NoninvertibleTransformException {
        return this.contains(point.getX(), point.getY());
    }

    /**
     * Transforms mouse coordinates of a point to model coordinates considering the current transformation
     * @param point
     * @return
     */
    public Point2D transformMousePointToModel(Point2D point) throws NoninvertibleTransformException {
        Point2D transformedPoint = new Point2D.Double(point.getX(), point.getY());
        if (this.transform != null) {
            this.transform.inverseTransform(point, transformedPoint); // use inverse transformation!
        }
        return transformedPoint;
    }
}
