import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Created by Gustavo on 2017-06-18.
 */
public class Obstacle extends AbstractManipulableDrawable {

    public Polygon shape;
    public Polygon transformedShape;
    public int height;
    public int width;
    public int heightBlocks;
    public int widthBlocks;
    public int blockSize;

    public Obstacle(AffineTransform transform, int heightBlock, int widthBlock, int blockSize) {
        super(transform);
        this.heightBlocks = heightBlock;
        this.widthBlocks = widthBlock;
        this.blockSize = blockSize;
        this.initializeShape(blockSize, heightBlock, widthBlock);
    }

    private void initializeShape(int blockSize, int heightBlock, int widthBlock) {
        this.shape = new Polygon();
        this.shape.addPoint(0, 0);
        this.shape.addPoint(widthBlock*blockSize, 0);
        this.shape.addPoint(widthBlock*blockSize, heightBlock*blockSize);
        this.shape.addPoint(0, heightBlock*blockSize);
    }

    @Override
    public boolean contains(double px, double py) throws NoninvertibleTransformException {
        // Transform mouse coordinates to model coordinates before testing
        Point2D transformedPoint = this.transformMousePointToModel(new Point2D.Double(px, py));
        return transformedPoint.getX() >= shape.getBounds().x && transformedPoint.getX() <= shape.getBounds().x + shape.getBounds().width &&
                transformedPoint.getY() >= shape.getBounds().y && transformedPoint.getY() <= shape.getBounds().y + shape.getBounds().height;
//        return this.shape.contains(transformedPoint);
    }

    @Override
    protected void paintItself(Graphics2D g2) {
        // Transform the basic shape using the current Affine Transform before drawing
        Shape transformedShape = this.transform == null ? this.shape
                : this.transform.createTransformedShape(this.shape);

        int []xcoords = {(int) Math.round(transformedShape.getBounds().getX()),
        (int) Math.round(transformedShape.getBounds().getX() + transformedShape.getBounds().getWidth()),
                (int) Math.round(transformedShape.getBounds().getX() + transformedShape.getBounds().getWidth()),
                (int) Math.round(transformedShape.getBounds().getX())};
        int []ycoords = {(int) Math.round(transformedShape.getBounds().getY() + transformedShape.getBounds().getHeight()),
                (int) Math.round(transformedShape.getBounds().getY() + transformedShape.getBounds().getHeight()),
                (int) Math.round(transformedShape.getBounds().getY()),
                (int) Math.round(transformedShape.getBounds().getY())};

        this.transformedShape = new Polygon(xcoords, ycoords, 4);
//        System.out.println("x - " + transformedShape.getBounds().x);
        transformedShape.getBounds().x = Math.max((int) transformedShape.getBounds2D().getX(), 0);
        transformedShape.getBounds().y = Math.max((int) transformedShape.getBounds2D().getY(), 0);
        transformedShape.getBounds().x = Math.min((int) Math.round(transformedShape.getBounds().x + transformedShape.getBounds2D().getWidth()), width);
        transformedShape.getBounds().y = Math.min((int) Math.round(transformedShape.getBounds().y + transformedShape.getBounds2D().getHeight()), height);

        // Draw outline
        g2.setColor(Color.DARK_GRAY);
        g2.setStroke(new BasicStroke(2));
        g2.draw(transformedShape);
        // Fill shape
        g2.setColor(Color.GREEN);
        g2.fill(transformedShape);
    }

    @Override
    protected Rectangle2D getBoundingBox() {
        return this.shape.getBounds2D();
    }

    public Polygon getTransformedShape() {
        Shape transformedShape = this.transform == null ? this.shape
                : this.transform.createTransformedShape(this.shape);

        int []xcoords = {(int) Math.round(transformedShape.getBounds().getX()),
                (int) Math.round(transformedShape.getBounds().getX() + transformedShape.getBounds().getWidth()),
                (int) Math.round(transformedShape.getBounds().getX() + transformedShape.getBounds().getWidth()),
                (int) Math.round(transformedShape.getBounds().getX())};
        int []ycoords = {(int) Math.round(transformedShape.getBounds().getY() + transformedShape.getBounds().getHeight()),
                (int) Math.round(transformedShape.getBounds().getY() + transformedShape.getBounds().getHeight()),
                (int) Math.round(transformedShape.getBounds().getY()),
                (int) Math.round(transformedShape.getBounds().getY())};

        this.transformedShape = new Polygon(xcoords, ycoords, 4);
        return this.transformedShape;
    }

    public void resetSize(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
