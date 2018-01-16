import javax.swing.*;
import java.awt.*;
import java.util.Stack;

/**
 * Created by naomikoo on 2017-07-06.
 */
public interface EditorPanelListener {
    void userActioned(boolean undosAvailable, boolean redosAvailable, int obstacleNum);
    void redraw();

    double getScale();

    Plane getPlane();

    void itemSelected(boolean selected);

    void itemCut();

    JPanel getComponent();
}
