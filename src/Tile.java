import java.awt.*;

/**
 * Created by naomikoo on 2017-07-04.
 */
public class Tile {
    Rectangle rec;
    boolean filled;

    public Tile(int x, int y, int width, int height) {
        rec = new Rectangle(x,y,width,height);
        filled = false;
    }
}
