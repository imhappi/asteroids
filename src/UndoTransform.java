import java.awt.geom.AffineTransform;

/**
 * Created by naomikoo on 2017-07-06.
 */
public class UndoTransform {
    public AbstractManipulableDrawable drawable;
    public AffineTransform transform;
    public double scale;

    public UndoTransform(AbstractManipulableDrawable drawable, AffineTransform transform, double scale) {
        this.drawable = drawable;
        this.transform = transform;
        this.scale = scale;
    }
}
