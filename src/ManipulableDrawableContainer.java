/**
 * Created by naomikoo on 2017-07-07.
 */
public class ManipulableDrawableContainer {
    public AbstractManipulableDrawable drawable;
    public double scale;

    public ManipulableDrawableContainer(AbstractManipulableDrawable drawable, double scale) {
        this.scale = scale;
        this.drawable = drawable;
    }
}
