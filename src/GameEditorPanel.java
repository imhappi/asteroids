import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotUndoException;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Stack;

/**
 * Created by naomikoo on 2017-07-04.
 */
public class GameEditorPanel extends JPanel implements DropTargetListener, Observer {
    private EditorPanelListener editorPanelListener;
    int blockHeight = 20;
    int blockWidth = 20;

    private EditorModel editorModel;
    public Plane plane;

    // default size:
    int heightBlocks = 20;
    int totalWidthBlocks = 10;
    public int WIDTH = 800, HEIGHT = 800;
    public int totalWidthInPixels;
    private Image backgroundImage;
    JFrame frame;

    public ArrayList<ArrayList<Tile>> grid;

    public Stack<UndoTransform> undoTransformStack;
    public double scale = 1;
    private ManipulableDrawableContainer cutItem;

    public GameEditorPanel(int heightBlocks, int widthBlocks, JFrame frame, EditorPanelListener editorPanelListener) {
        this.frame = frame;
        undoTransformStack = new Stack<>();
        this.editorPanelListener = editorPanelListener;
        this.heightBlocks = heightBlocks;
        this.totalWidthBlocks = widthBlocks;
        this.editorModel = new EditorModel(this, blockHeight, editorPanelListener);
        this.editorModel.addObserver(this);

        try {
            backgroundImage = ImageIO.read(new File("images/background.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Controller controller = new Controller(editorModel);
        this.addMouseListener(controller);
        this.addMouseMotionListener(controller);

        new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE,
                this, true, null);
        totalWidthInPixels = totalWidthBlocks * blockWidth;
        grid = new ArrayList<>();
        for (int i = 0; i < heightBlocks; i++) {
            grid.add(i, new ArrayList<>());
            for (int j = 0; j < totalWidthBlocks; j++) {
                Tile rec = new Tile(j * blockWidth, i * blockHeight, blockWidth, blockHeight);
                grid.get(i).add(j, rec);
            }
        }

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);

//        for (int i = 0; i < grid.size(); i++) {
//            for (int j = 0; j < grid.get(i).size(); j++) {
//                Rectangle rec = grid.get(i).get(j).rec;
//
//                if (grid.get(i).get(j).filled) {
//                    g.setColor(Color.green);
//                    g.fillRect(rec.x, rec.y, rec.width, rec.height);
//                } else {
//                    g.setColor(Color.black);
//                    g.drawRect(rec.x, rec.y, rec.width, rec.height);
//                }
//            }
//        }

        float heightRatio = ((float) frame.getContentPane().getHeight()) / HEIGHT;

        if (HEIGHT != frame.getContentPane().getHeight()) {
            HEIGHT = frame.getContentPane().getHeight();

            blockHeight = HEIGHT / heightBlocks;
            blockWidth = blockHeight;
            editorModel.updateBlockSize(blockHeight);
            totalWidthInPixels = totalWidthBlocks * blockWidth;
            setPreferredSize(new Dimension((int) Math.round(getWidth() * scale), (int) Math.round(HEIGHT * scale)));

            plane = new Plane(blockWidth, HEIGHT/2 - blockHeight, blockHeight, blockWidth); // in the vertical middle and one block from left

            for (AbstractManipulableDrawable drawable : editorModel.getDrawables()) {

                if (drawable.getTransform() != null) {
                    ((Obstacle) drawable).getTransform().translate((heightRatio - 1) *
                                    ((Obstacle) drawable).getTransformedShape().getBounds().getMinX(),
                            (heightRatio - 1) * ((Obstacle) drawable).getTransformedShape().getBounds().getMinY());
                }
                ((Obstacle) drawable).resetSize(WIDTH, HEIGHT);
                drawable.getTransform().scale(heightRatio, heightRatio);

            }

        }
        Graphics2D g2 = (Graphics2D) g;
        for (SelectableDrawable drawable : this.editorModel.getDrawables()) {
            drawable.paint(g2);
        }

        if (plane != null) {
            plane.paint(g);
        }
    }

//    public void resetSize(int height, int width) {
//        System.out.println("oldheight - " + HEIGHT);
//        System.out.println("height - " + height);
//        float heightRatio = ((float) height)/HEIGHT;
//        float widthRatio = ((float) width)/WIDTH;
//
//        WIDTH = width;
//        HEIGHT = height;
//        blockHeight = HEIGHT / heightBlocks;
//        blockWidth = blockHeight;
//        totalWidthInPixels = totalWidthBlocks * blockWidth;
//        setPreferredSize(new Dimension(totalWidthInPixels, HEIGHT));
////
////        grid.clear();
////
////        for (int i = 0; i < heightBlocks; i++) {
////            grid.add(i, new ArrayList<>());
////            for (int j = 0; j < totalWidthBlocks; j++) {
////                Tile rec = new Tile(j * blockWidth, i * blockHeight, blockWidth, blockHeight);
////                grid.get(i).add(j, rec);
////            }
////        }
//
//        for (AbstractManipulableDrawable drawable : editorModel.getDrawables()) {
//
//            if (drawable.getTransform() != null) {
//                ((Obstacle) drawable).getTransform().translate((heightRatio - 1) *
//                                ((Obstacle) drawable).getTransformedShape().getBounds().getMinX(),
//                        (heightRatio - 1) * ((Obstacle) drawable).getTransformedShape().getBounds().getMinY());
//            }
//            ((Obstacle) drawable).resetSize(WIDTH, HEIGHT);
////            System.out.println("first scalex - " + drawable.getTransform().getScaleX());
////            drawable.getTransform().scale(widthRatio, heightRatio);
////            System.out.println("second scalex - " + drawable.getTransform().getScaleX());
//
////            (drawable).getTransform().setToScale(drawable.getTransform().getScaleX() * widthRatio,
////                    drawable.getTransform().getScaleY() * heightRatio); // problem is probably that you're not supposed to multiply scales!
//            drawable.getTransform().scale(heightRatio, heightRatio);
////            (drawable).getTransform().setToTranslation(drawable.getTransform().getTranslateX() * widthRatio,
////                    drawable.getTransform().getTranslateY() * heightRatio);
//
//
//        }
//
////        paintComponent(getGraphics());
//
//    }

    public void dragEnter(DropTargetDragEvent evt) {
    }

    public void dragOver(DropTargetDragEvent evt) {
    }

    public void dragExit(DropTargetEvent evt) {
    }

    public void dropActionChanged(DropTargetDragEvent evt) {
    }

    public void drop(DropTargetDropEvent evt) {
        // update grid:
        try {

            Transferable transferable = evt.getTransferable();
            Rectangle rectangle = (Rectangle) transferable.getTransferData(DataFlavor.stringFlavor);
            evt.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
            int width = rectangle.width;
            int height = rectangle.height;
            int x = evt.getLocation().x / blockWidth;
            int y = evt.getLocation().y / blockHeight;

            AffineTransform obstacleTransform = AffineTransform.getScaleInstance(1.0, 1.0);
            obstacleTransform.translate(evt.getLocation().x, evt.getLocation().y);
            Obstacle obstacle = new Obstacle(obstacleTransform, height, width, blockHeight);

            if (scale == 2) {
                obstacleTransform.scale(2, 2);
            }

            boolean noConflict = true;

            for (AbstractManipulableDrawable drawable : editorModel.getDrawables()) {
                if (((Obstacle) drawable).getTransformedShape().getBounds().intersects(obstacle.getTransformedShape().getBounds())) {
                    noConflict = false;
                }
            }

            if (plane.rectangle.intersects(obstacle.getTransformedShape().getBounds())) {
                noConflict = false;
            }

            if (noConflict) {
                editorModel.addDrawable(obstacle);
            }

//            for (int i = y; i < y + width; i++) {
//                for (int j = x; j < x + height; j++) {
//                    grid.get(i).get(j).filled = true;
//                }
//            }

            repaint();

        } catch (IOException e) {
            evt.rejectDrop();

        } catch (UnsupportedFlavorException e) {

            evt.rejectDrop();

        }
    }

    @Override
    public void update(Observable o, Object arg) {
        this.repaint();
    }

    public String getObstaclesInText() {
        String obstaclesInText = "";
        obstaclesInText += heightBlocks + " " + totalWidthBlocks + "\n";

        for (AbstractManipulableDrawable drawable : editorModel.getDrawables()) {
            Rectangle rectangle;
            if (scale == 2) { // zoomed in

                // zoom out drawable
                drawable.getTransform().scale(0.5, 0.5);
                drawable.getTransform().translate(-(drawable.getTransform().getTranslateX() / 2 / drawable.getTransform().getScaleX()), -(drawable.getTransform().getTranslateY() / 2 / drawable.getTransform().getScaleY()));

                rectangle = new Rectangle(((Obstacle) drawable).getTransformedShape().getBounds());

                // zoom back in
                drawable.getTransform().translate(drawable.getTransform().getTranslateX() / drawable.getTransform().getScaleX(), drawable.getTransform().getTranslateY() / drawable.getTransform().getScaleY());
                drawable.getTransform().scale(2, 2);

            } else { // scale is 1
                rectangle = ((Obstacle) drawable).getTransformedShape().getBounds();
            }
            double minX = rectangle.getX();
            double maxX = rectangle.getMaxX();
            double minY = rectangle.getMinY();
            double maxY = rectangle.getMaxY();

            minX = (Math.round(minX / blockHeight));
            maxX = (Math.round(maxX / blockHeight)) - 1;
            minY = (Math.round(minY / blockHeight));
            maxY = (Math.round(maxY / blockHeight)) - 1;

            obstaclesInText += (int) minX + " " + (int) minY + " " + (int) maxX + " " + (int) maxY + "\n";
        }

        return obstaclesInText;
    }

    public void undo() {
        editorModel.undo();
    }

    public void deleteCurrentSelection() {
        if (editorModel.getCurrentSelection() != null) {
            editorModel.removeDrawable(new ManipulableDrawableContainer(editorModel.getCurrentSelection(), scale));
            editorModel.setCurrentSelection(null);
        }
    }

    public void setScale(double scale) {
        this.scale = this.scale * scale;

        if (scale == 2) { // zoom in

            for (AbstractManipulableDrawable drawable : editorModel.getDrawables()) {

                drawable.getTransform().translate(drawable.getTransform().getTranslateX() / drawable.getTransform().getScaleX(), drawable.getTransform().getTranslateY() / drawable.getTransform().getScaleY());

                drawable.getTransform().scale(scale, scale);

            }

            // zoom in out plane as well
            plane.translate(plane.rectangle.x, plane.rectangle.y);
            plane.scale(scale);

            for (UndoTransform undoTransform : undoTransformStack) {
                undoTransform.transform.translate(undoTransform.transform.getTranslateX() / undoTransform.transform.getScaleX(), undoTransform.transform.getTranslateY() / undoTransform.transform.getScaleY());
                undoTransform.transform.scale(2, 2);
            }

        } else { // zoom out
            for (AbstractManipulableDrawable drawable : editorModel.getDrawables()) {

                drawable.getTransform().scale(scale, scale);
                drawable.getTransform().translate(-(drawable.getTransform().getTranslateX() / 2 / drawable.getTransform().getScaleX()), -(drawable.getTransform().getTranslateY() / 2 / drawable.getTransform().getScaleY()));

//                drawable.getTransform().translate(-drawable.getTransform().getTranslateX()/2, -drawable.getTransform().getTranslateY()/2);
            }

            plane.scale(scale);
            plane.translate(-plane.rectangle.x/2, -plane.rectangle.y/2);
            for (UndoTransform undoTransform : undoTransformStack) {
                undoTransform.transform.scale(0.5, 0.5);
                undoTransform.transform.translate(-undoTransform.transform.getTranslateX() / 2 / undoTransform.transform.getScaleX(), -undoTransform.transform.getTranslateY() / 2 / undoTransform.transform.getScaleY());

            }

        }

//        HEIGHT = frame.getContentPane().getHeight();
//
//        blockHeight = HEIGHT / heightBlocks;
//        blockWidth = blockHeight;
//        editorModel.updateBlockSize(blockHeight);
//        totalWidthInPixels = totalWidthBlocks * blockWidth;
        setPreferredSize(new Dimension((int) Math.round(getWidth() * scale), (int) Math.round(HEIGHT * scale)));
        repaint();
    }

    public void cutCurrentSelection() {
        if (editorModel.getCurrentSelection() != null) {
            cutItem = new ManipulableDrawableContainer(editorModel.getCurrentSelection(), scale);
            editorModel.removeDrawable(new ManipulableDrawableContainer(editorModel.getCurrentSelection(), scale));
            editorModel.setCurrentSelection(null);
            editorPanelListener.itemCut();
        }
    }

    public void pasteItem() {
        if (cutItem != null) {
            editorModel.addDrawable(cutItem);
        }
    }

    class Controller extends MouseAdapter {
        private EditorModel editorModel;
        private boolean isMoving;
        private BoundingBoxBorder isResizing;
        private Point lastPoint;

        public Controller(EditorModel editorModel) {
            this.editorModel = editorModel;
            this.isMoving = false;
            this.isResizing = BoundingBoxBorder.NONE;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            // Select the clicked drawable (if any) and deselect all others
            try {
                this.editorModel.setCurrentSelection(null);
                for (AbstractManipulableDrawable drawable : this.editorModel.getDrawables()) {
                    if (drawable.contains(e.getPoint())) {
                        drawable.select();
                        this.editorModel.setCurrentSelection(drawable);
                    } else {
                        drawable.deselect();
                    }
                }
            } catch (NoninvertibleTransformException e1) {
                e1.printStackTrace();
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            // If the mouse is over a selected component, begin dragging (moving or resizing); select if not selected
            try {
                this.editorModel.setCurrentSelection(null);
                for (AbstractManipulableDrawable drawable : this.editorModel.getDrawables()) {
                    if (drawable.contains(e.getPoint())) {
                        drawable.select();
                        this.editorModel.setCurrentSelection(drawable);
                    } else {
                        drawable.deselect();
                    }
                }

                if (this.editorModel.getCurrentSelection() != null) {
                    this.isResizing = this.editorModel.getCurrentSelection().isInBoundingBox(e.getPoint());
                    this.isMoving = (this.isResizing == BoundingBoxBorder.NONE)
                            && this.editorModel.getCurrentSelection().contains(e.getPoint());
                    this.lastPoint = e.getPoint();
                    undoTransformStack.add(new UndoTransform(editorModel.getCurrentSelection(), new AffineTransform(editorModel.getCurrentSelection().transform), scale));
                }
            } catch (NoninvertibleTransformException e1) {
                e1.printStackTrace();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            // Stop moving and resizing
            this.isResizing = BoundingBoxBorder.NONE;
            this.isMoving = false;
            editorModel.addUndoableEdit(new AbstractUndoableEdit() {
                @Override
                public void undo() throws CannotUndoException {
                    super.undo();
                    UndoTransform undoTransform = undoTransformStack.pop();

                    if (undoTransform.transform.equals(undoTransform.drawable.transform)) {
                        editorModel.removeEdit();
                        return;
                    }

                    if (undoTransform != null) {
                        undoTransform.drawable.transform = undoTransform.transform;

                        // if scale changed, transform should change
//                        if (undoTransform.scale != scale && scale == 2) {
//                            System.out.println("undos should be in x2");
//                            undoTransform.drawable.transform.scale(2, 2);
//                            undoTransform.drawable.transform.translate(undoTransform.drawable.getTransform().getTranslateX()/2 / undoTransform.drawable.getTransform().getScaleX(), undoTransform.drawable.getTransform().getTranslateY()/2 / undoTransform.drawable.getTransform().getScaleY());
//                        } else if (undoTransform.scale != scale) { // scale is not 2
//                            System.out.println("undos should be in x1");
//                            undoTransform.drawable.getTransform().translate(undoTransform.drawable.getTransform().getTranslateX() / undoTransform.drawable.getTransform().getScaleX(), undoTransform.drawable.getTransform().getTranslateY() / undoTransform.drawable.getTransform().getScaleY());
//                            undoTransform.drawable.getTransform().scale(0.5, 0.5);
//                        }
                        repaint();
                    }
                }
            });
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            try {
                if (this.editorModel.getCurrentSelection() != null) {
                    // Move (translate) drawable
                    if (this.isMoving) {
                        double tx = e.getPoint().getX() - this.lastPoint.getX();
                        double ty = e.getPoint().getY() - this.lastPoint.getY();
                        this.editorModel.translateSelected(tx, ty, scale);
                    }
                    // Resize (scale) drawable
                    switch (this.isResizing) {
                        case NORTH:
                            this.editorModel.scaleSelectedNorth(lastPoint, e.getPoint());
                            break;
                        case SOUTH:
                            this.editorModel.scaleSelectedSouth(lastPoint, e.getPoint());
                            break;
                        case WEST:
                            this.editorModel.scaleSelectedWest(lastPoint, e.getPoint());
                            break;
                        case EAST:
                            this.editorModel.scaleSelectedEast(lastPoint, e.getPoint());
                            break;
                    }
                    // Save last point
                    this.lastPoint = e.getPoint();
                }
            } catch (NoninvertibleTransformException e1) {
                e1.printStackTrace();
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            // If the mouse is over a selected component, change the mouse cursor accordingly
            try {
                GameEditorPanel.this.setCursor(Cursor.getDefaultCursor());

                if (this.editorModel.getCurrentSelection() != null) {
                    // Move cursor
                    if (this.editorModel.getCurrentSelection().contains(e.getPoint())) {
                        GameEditorPanel.this.setCursor(new Cursor(Cursor.MOVE_CURSOR));
                    }
                    // Scale cursor
                    switch (this.editorModel.getCurrentSelection().isInBoundingBox(e.getPoint())) {
                        case NORTH:
                            GameEditorPanel.this.setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));
                            break;
                        case SOUTH:
                            GameEditorPanel.this.setCursor(new Cursor(Cursor.S_RESIZE_CURSOR));
                            break;
                        case WEST:
                            GameEditorPanel.this.setCursor(new Cursor(Cursor.W_RESIZE_CURSOR));
                            break;
                        case EAST:
                            GameEditorPanel.this.setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
                            break;
                    }
                }
            } catch (NoninvertibleTransformException e1) {
                e1.printStackTrace();
            }
        }
    }

}
