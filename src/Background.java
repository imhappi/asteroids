import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Background {
    private BufferedImage image;

    private int x;
    private int y;
    private int movementSpeed = 0;

    public Background(int x, int y) {
        this.x = x;
        this.y = y;

        // Try to open the image file background.png
        try {
            image = ImageIO.read(new File("images/background.png"));
        }
        catch (Exception e) { System.out.println(e); }
    }

    /**
     * Method that draws the image onto the Graphics object passed
     */
    public void draw(Graphics g, int width, int height) {

        // Draw the image onto the Graphics reference
        g.drawImage(image, getX(), 0, width, height, null);

        // Move the x position left for next time
        this.x -= movementSpeed;

        // Check to see if the image has gone off stage left
        if (this.x <= -1 * width) {

            // If it has, line it back up so that its left edge is
            // lined up to the right side of the other background image
            this.x = this.x + width * 2;
        }

    }

    public void setX(int x) {
        this.x = x;
    }
    public int getX() {
        return this.x;
    }

    public int getImageWidth() {
        return image.getWidth();
    }

    public void setMoveSpeed(int movementSpeed) {
        this.movementSpeed = movementSpeed;
    }

}