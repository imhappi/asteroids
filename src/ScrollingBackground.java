import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ScrollingBackground extends Canvas {

    // Two copies of the background image to scroll
    private Background backOne;
    private Background backTwo;

    private BufferedImage back;

    private int width, height;

    public ScrollingBackground(int width, int height) {
        this.width = width;
        this.height = height;
        backOne = new Background(0, 0);
        backTwo = new Background(width, 0);
    }

    public void paint(Graphics g) {
        if (width != 0 && height != 0) {
            if (back == null) {
                back = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            }

            // Create a buffer to draw to
            Graphics buffer = back.createGraphics();

            // Put the two copies of the background image onto the buffer
            backOne.draw(buffer, width, height);
            backTwo.draw(buffer, width, height);

            // Draw the image onto the window
            g.drawImage(back, 0, 0, width, height, null);
        }
    }

    public void resetSize(int width, int height) {
        this.width = width;
        this.height = height;
        backTwo.setX(width);
    }

    public void setMovementSpeed(int movementSpeed) {
        backOne.setMoveSpeed(movementSpeed);
        backTwo.setMoveSpeed(movementSpeed);
    }

}