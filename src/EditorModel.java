import javax.swing.*;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Observable;

/**
 * EditorModel for the direct manipulation demo.
 * Created by Gustavo on 2017-06-18.
 */
public class EditorModel extends Observable {

    private EditorPanelListener editorPanelListener;
    JPanel panel;
    int blockSize;
    private UndoManager undoManager;

    /**
     * The list of drawables.
     */
    private ArrayList<AbstractManipulableDrawable> drawables;

    /**
     * A convenient reference to the currently selected drawable.
     */
    private AbstractManipulableDrawable currentSelection;

    public EditorModel(JPanel panel, int blockSize, EditorPanelListener listener) {
        this.blockSize = blockSize;
        this.panel = panel;
        this.editorPanelListener = listener;
        this.drawables = new ArrayList<AbstractManipulableDrawable>();
        undoManager = new UndoManager();
    }

    /**
     * Returns the list of drawables.
     *
     * @return
     */
    public ArrayList<AbstractManipulableDrawable> getDrawables() {
        return drawables;
    }

    /**
     * Adds a drawable to the list and notify the Views.
     *
     * @param drawable
     */
    public void addDrawable(AbstractManipulableDrawable drawable) {
        this.drawables.add(drawable);

        UndoableEdit undoableEdit = new AbstractUndoableEdit() {
            public void undo() throws CannotUndoException {
                super.undo();
                if (drawables.size() >= 1) {
                    drawables.remove(drawables.size() - 1);
                }
                editorPanelListener.redraw();
            }
        };

        // Add this undoable edit to the undo manager
        undoManager.addEdit(undoableEdit);

        editorPanelListener.userActioned(undoManager.canUndo(), false, drawables.size());

        this.setChanged();
        this.notifyObservers();
    }

    /**
     * Removes a drawable from the list and notify the Views.
     *
     * @param container
     */
    public void removeDrawable(ManipulableDrawableContainer container) {
        container.drawable.deselect();
        this.drawables.remove(container.drawable);

        UndoableEdit undoableEdit = new AbstractUndoableEdit() {
            public void undo() throws CannotUndoException {
                super.undo();
                drawables.add(container.drawable);
                System.out.println("scale - " + editorPanelListener.getScale());
                if (editorPanelListener.getScale() != container.scale && editorPanelListener.getScale() == 2) {
                    container.drawable.getTransform().translate((container.drawable.getTransform().getTranslateX() / container.drawable.getTransform().getScaleX()), (container.drawable.getTransform().getTranslateY() / container.drawable.getTransform().getScaleY()));
                    container.drawable.getTransform().scale(2, 2);

                } else if (editorPanelListener.getScale() != container.scale) {
                    container.drawable.getTransform().scale(0.5, 0.5);
                    container.drawable.getTransform().translate(-(container.drawable.getTransform().getTranslateX() / 2 / container.drawable.getTransform().getScaleX()), -(container.drawable.getTransform().getTranslateY() / 2 / container.drawable.getTransform().getScaleY()));
                }
                editorPanelListener.redraw();
            }
        };

        // Add this undoable edit to the undo manager
        undoManager.addEdit(undoableEdit);

        editorPanelListener.userActioned(undoManager.canUndo(), false, drawables.size());

        this.setChanged();
        this.notifyObservers();
    }

    /**
     * Removes all drawables from the list and notify the Views.
     */
    public void removeAll() {
        this.drawables.clear();
        this.setChanged();
        this.notifyObservers();
    }

    /**
     * Returns the currently selected drawable.
     *
     * @return
     */
    public AbstractManipulableDrawable getCurrentSelection() {
        return currentSelection;
    }

    /**
     * Sets the currently selected drawable and notify the Views.
     *
     * @param currentSelection
     */
    public void setCurrentSelection(AbstractManipulableDrawable currentSelection) {
        if (currentSelection != null) {
            currentSelection.select();
            editorPanelListener.itemSelected(true);
        } else {
            editorPanelListener.itemSelected(false);
        }
        this.currentSelection = currentSelection;
        this.setChanged();
        this.notifyObservers();
    }

    /**
     * Translate the currently selected drawable by the specified screen amounts,
     * considering the current transformation of the selected drawable.
     *
     * @param tx
     * @param ty
     */
    public void translateSelected(double tx, double ty, double scale) {
        if (this.currentSelection != null) {
            Rectangle rectangle = ((Obstacle) currentSelection).getTransformedShape().getBounds();
            if (this.currentSelection.getTransform() != null) {
                // We need to apply the inverse of the scale factors to the translation amounts
                // to transform screen amounts to model amounts
                double stx = tx / this.currentSelection.getTransform().getScaleX();
                double sty = ty / this.currentSelection.getTransform().getScaleY();

                // attempt at 'snapping'; not good
//                double xdistance = (((stx + rectangle.getX()) + blockSize/2) / blockSize) * blockSize;
//                double ydistance = (((sty + rectangle.getY()) + blockSize/2) / blockSize) * blockSize;
//
//                stx = xdistance - rectangle.getX();
//                sty = ydistance - rectangle.getY();


                if ((rectangle.getMinX() + stx >= 0 &&
                        rectangle.getMaxX() + stx <= panel.getWidth()) &&
                        (rectangle.getMinY() + sty >= 0 &&
                                rectangle.getMaxY() + sty <= panel.getHeight()) &&
                        !overlapsAnyObstacleTransformed(new Rectangle((int) (rectangle.getMinX() + stx), (int) (rectangle.getMinY() + sty), rectangle.width, rectangle.height))) {
                    this.currentSelection.getTransform().translate(stx, sty);
                }
            } else {
                this.currentSelection.setTransform(AffineTransform.getTranslateInstance(tx, ty));
            }
            this.setChanged();
            this.notifyObservers();
        }
    }

    /**
     * Scale the currently selected drawable about the West border, according to the specified screen points,
     * considering the current transformation of the selected drawable.
     *
     * @param oldPoint
     * @param newPoint
     * @throws NoninvertibleTransformException
     */
    public void scaleSelectedWest(Point oldPoint, Point newPoint) throws NoninvertibleTransformException {
        if (this.currentSelection != null) {
            if (this.currentSelection.getTransform() != null) {
                // We need to transform screen coordinates to model coordinates
                Point2D oldPointTransformed = this.currentSelection.transformMousePointToModel(oldPoint);
                Point2D newPointTransformed = this.currentSelection.transformMousePointToModel(newPoint);
                double width = this.currentSelection.getBoundingBox().getWidth();
                double topRight = oldPointTransformed.getX() + width;
                double scale = (topRight - newPointTransformed.getX()) / (width);
                double sx = (oldPointTransformed.getX() + width) / (newPointTransformed.getX() + width);
                Rectangle rect = ((Obstacle) currentSelection).getTransformedShape().getBounds();

                if (this.currentSelection.getTransform().getTranslateX() +
                        this.currentSelection.getTransform().getScaleX() *
                                (newPointTransformed.getX() - oldPointTransformed.getX()) >= 0 &&
                        !overlapsAnyObstacleTransformed(new Rectangle((int) Math.round(this.currentSelection.getTransform().getTranslateX() +
                                this.currentSelection.getTransform().getScaleX() *
                                        (newPointTransformed.getX() - oldPointTransformed.getX())),
                                (int) Math.round(rect.getMinY()),
                                (int) Math.round(rect.getWidth()
                                        * scale),
                                (int) Math.round(rect.getHeight())))) {

                    this.currentSelection.getTransform().scale(sx, 1.0);
                    this.currentSelection.getTransform().translate(newPointTransformed.getX() - oldPointTransformed.getX(), 0.0);
                }
            } else {
                double width = this.currentSelection.getBoundingBox().getWidth();
                double sx = (oldPoint.getX() + width) / (newPoint.getX() + width);
                this.currentSelection.setTransform(AffineTransform.getScaleInstance(sx, 1.0));
            }
            this.setChanged();
            this.notifyObservers();
        }
    }

    private boolean overlapsAnyObstacleTransformed(Rectangle rectangle) {
        boolean overlaps = false;
        for (AbstractManipulableDrawable drawable : drawables) {
            // not the same one
            if (drawable != currentSelection) {
                overlaps = (overlaps(((Obstacle) drawable).getTransformedShape().getBounds(), rectangle) || overlaps);
            }
        }

        overlaps = overlaps || editorPanelListener.getPlane().rectangle.intersects(rectangle);

        return overlaps;
    }

    private boolean overlapsAnyObstacleAdd(Rectangle rectangle) {
        boolean overlaps = false;
        for (AbstractManipulableDrawable drawable : drawables) {
            overlaps = (overlaps(((Obstacle) drawable).getTransformedShape().getBounds(), rectangle) || overlaps);
        }

        overlaps = overlaps || editorPanelListener.getPlane().rectangle.intersects(rectangle);

        return overlaps;
    }

    /**
     * Scale the currently selected drawable about the East border, according to the specified screen points,
     * considering the current transformation of the selected drawable.
     *
     * @param oldPoint
     * @param newPoint
     * @throws NoninvertibleTransformException
     */
    public void scaleSelectedEast(Point oldPoint, Point newPoint) throws NoninvertibleTransformException {
        if (this.currentSelection != null) {
            if (this.currentSelection.getTransform() != null) {
                // We need to transform screen coordinates to model coordinates
                Point2D oldPointTransformed = this.currentSelection.transformMousePointToModel(oldPoint);
                Point2D newPointTransformed = this.currentSelection.transformMousePointToModel(newPoint);

                Rectangle rect = ((Obstacle) currentSelection).getTransformedShape().getBounds();

                if (this.currentSelection.getTransform().getTranslateX() +
                        this.currentSelection.getTransform().getScaleX() *
                                (newPointTransformed.getX() - oldPointTransformed.getX()) + rect.width <= panel.getWidth() &&
                        !overlapsAnyObstacleTransformed(new Rectangle((int) Math.round(this.currentSelection.getTransform().getTranslateX()),
                                (int) Math.round(rect.getMinY()),
                                (int) Math.round(rect.getWidth() +
                                        this.currentSelection.getTransform().getScaleX() *
                                                (newPointTransformed.getX() - oldPointTransformed.getX())),
                                (int) Math.round(rect.getHeight())))) {
                    this.currentSelection.getTransform().scale(newPointTransformed.getX() / oldPointTransformed.getX(), 1.0);
                }
            } else {
                // should do same check as above if transform was ever null, but we don't expect this
                this.currentSelection.setTransform(AffineTransform.getScaleInstance(newPoint.getX() / oldPoint.getX(), 1.0));
            }
            this.setChanged();
            this.notifyObservers();
        }
    }

    /**
     * Scale the currently selected drawable about the North border, according to the specified screen points,
     * considering the current transformation of the selected drawable.
     *
     * @param oldPoint
     * @param newPoint
     * @throws NoninvertibleTransformException
     */
    public void scaleSelectedNorth(Point oldPoint, Point newPoint) throws NoninvertibleTransformException {
        if (this.currentSelection != null) {
            // We need to transform screen coordinates to model coordinates
            Point2D oldPointTransformed = this.currentSelection.transformMousePointToModel(oldPoint);
            Point2D newPointTransformed = this.currentSelection.transformMousePointToModel(newPoint);
            double height = this.currentSelection.getBoundingBox().getHeight();
            double sy = (oldPointTransformed.getY() + height) / (newPointTransformed.getY() + height);

            Rectangle rect = ((Obstacle) currentSelection).getTransformedShape().getBounds();

            if (this.currentSelection.getTransform().getTranslateY() +
                    this.currentSelection.getTransform().getScaleY() *
                            (newPointTransformed.getY() - oldPointTransformed.getY()) >= 0 &&
                    !overlapsAnyObstacleTransformed(new Rectangle((int) Math.round(rect.getMinX()),
                            (int) Math.round(this.currentSelection.getTransform().getTranslateY() +
                                    this.currentSelection.getTransform().getScaleY() *
                                            (newPointTransformed.getY() - oldPointTransformed.getY())),
                            (int) Math.round(rect.getWidth()),
                            (int) Math.round(rect.getHeight() +
                                    (newPointTransformed.getY() - oldPointTransformed.getY()))))) {
                this.currentSelection.getTransform().scale(1.0, sy);
                this.currentSelection.getTransform().translate(0.0, newPointTransformed.getY() - oldPointTransformed.getY());
            }
            this.setChanged();
            this.notifyObservers();
        }
    }

    /**
     * Scale the currently selected drawable about the South border, according to the specified screen points,
     * considering the current transformation of the selected drawable.
     *
     * @param oldPoint
     * @param newPoint
     * @throws NoninvertibleTransformException
     */
    public void scaleSelectedSouth(Point oldPoint, Point newPoint) throws NoninvertibleTransformException {
        if (this.currentSelection != null) {
            if (this.currentSelection.getTransform() != null) {
                // We need to transform screen coordinates to model coordinates
                Point2D oldPointTransformed = this.currentSelection.transformMousePointToModel(oldPoint);
                Point2D newPointTransformed = this.currentSelection.transformMousePointToModel(newPoint);

                Rectangle rect = ((Obstacle) currentSelection).getTransformedShape().getBounds();

                if (this.currentSelection.getTransform().getTranslateY() +
                        this.currentSelection.getTransform().getScaleY() *
                                (newPointTransformed.getY() - oldPointTransformed.getY()) + rect.height <= panel.getHeight() &&
                        !overlapsAnyObstacleTransformed(new Rectangle((int) Math.round(rect.getMinX()),
                                (int) Math.round(this.currentSelection.getTransform().getTranslateY()),
                                (int) Math.round(rect.getWidth()),
                                (int) Math.round(rect.getHeight() +
                                        this.currentSelection.getTransform().getScaleY() *
                                                (newPointTransformed.getY() - oldPointTransformed.getY()))))) {
                    this.currentSelection.getTransform().scale(1.0, newPointTransformed.getY() / oldPointTransformed.getY());
                }
            } else {
                this.currentSelection.setTransform(AffineTransform.getScaleInstance(1.0, newPoint.getY() / oldPoint.getY()));
            }
            this.setChanged();
            this.notifyObservers();
        }
    }

    public boolean overlaps(Rectangle r1, Rectangle r2) {
        System.out.println("r1 x - " + r1.x + ", r2 x = " + r2.x);
        return r1.x < r2.x + r2.width && r1.x + r1.width > r2.x && r1.y < r2.y + r2.height && r1.y + r1.height > r2.y;
    }

    public void updateBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }

    public void undo() {
        undoManager.undo();
        editorPanelListener.userActioned(undoManager.canUndo(), false, drawables.size()); // can't redo for now
    }

    public void addUndoableEdit(AbstractUndoableEdit undoableEdit) {
        undoManager.addEdit(undoableEdit);
        editorPanelListener.redraw();
    }

    // should only be called when we know this does nothing
    public void removeEdit() {
        undoManager.undo();
    }

    public void addDrawable(ManipulableDrawableContainer container) {
        // if pasted drawable is not intruding with other drawables
        Obstacle obstacle = new Obstacle(new AffineTransform(container.drawable.getTransform()), ((Obstacle) container.drawable).heightBlocks, ((Obstacle) container.drawable).widthBlocks, blockSize);


        if (editorPanelListener.getScale() != container.scale && editorPanelListener.getScale() == 2) {
            obstacle.getTransform().translate((obstacle.getTransform().getTranslateX() / obstacle.getTransform().getScaleX()), (obstacle.getTransform().getTranslateY() / obstacle.getTransform().getScaleY()));
            obstacle.getTransform().scale(2, 2);

        } else if (editorPanelListener.getScale() != container.scale) {
            obstacle.getTransform().scale(0.5, 0.5);
            obstacle.getTransform().translate(-(obstacle.getTransform().getTranslateX() / 2 / obstacle.getTransform().getScaleX()), -(obstacle.getTransform().getTranslateY() / 2 / obstacle.getTransform().getScaleY()));
        }

        if (!overlapsAnyObstacleAdd(obstacle.getTransformedShape().getBounds())) {
            drawables.add(obstacle);
            editorPanelListener.redraw();

            UndoableEdit undoableEdit = new AbstractUndoableEdit() {
                public void undo() throws CannotUndoException {
                    super.undo();
                    if (drawables.size() >= 1) {
                        drawables.remove(drawables.size() - 1);
                    }
                    editorPanelListener.redraw();
                }
            };

            // Add this undoable edit to the undo manager
            undoManager.addEdit(undoableEdit);
        } else {
            JOptionPane.showMessageDialog(editorPanelListener.getComponent(), "Cannot paste item from where it was cut! Make sure there are no obstacles in the way before attempting to paste.");
        }
    }
}
