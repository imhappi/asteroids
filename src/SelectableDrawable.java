import java.awt.*;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

/**
 * Base interface for drawable graphics that can also be selected for manipulation.
 * Created by Gustavo on 2017-06-17.
 */
public interface SelectableDrawable {
    /**
     * Paint this SelectableDrawable using the specified Graphics2D object.
     * @param g2
     */
    void paint(Graphics2D g2);

    /**
     * Marks this SelectableDrawable as selected.
     */
    void select();

    /**
     * Marks this SelectableDrawable as not selected.
     */
    void deselect();

    /**
     * Returns true if this SelectableDrawable is currently marked as selected.
     */
    boolean isSelected();

    /**
     * Returns true if the point is contained within this drawable's shape.
     * @param point
     * @return
     */
    boolean contains(Point2D point) throws NoninvertibleTransformException;

    /**
     * Returns true if the point (px, py) is contained within this drawable's shape.
     * @param px
     * @param py
     * @return
     */
    boolean contains(double px, double py) throws NoninvertibleTransformException;
}
