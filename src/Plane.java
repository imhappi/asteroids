import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Plane {
    // all of these dimens are in blocks
    public double realX;
    public double realY;
    public double velx;
    public double vely;

    public Rectangle rectangle;
    public Image image;

    public Plane(int x, int y, int blockWidth, int blockHeight) {
        rectangle = new Rectangle(x, y, blockWidth, blockHeight);
        realX = x;
        realY = y;
        try {
            image = ImageIO.read(new File("images/spaceship.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    public void resetSize(int blockHeight, int blockWidth, float heightRatio, float widthRatio) {
        realX = realX * widthRatio;
        realY = realY * heightRatio;

        rectangle.x = (int) realX;
        rectangle.y = (int) realY;
        rectangle.width = blockWidth;
        rectangle.height = blockHeight;
    }

    public void paint(Graphics g) {
        //prepare a original Image source
        g.drawImage(image, rectangle.x, rectangle.y, rectangle.height, rectangle.width, null);
    }

    public void moveX(int x, int screenWidth) {
        realX += x + velx;
        rectangle.x += x + velx;

        if (rectangle.x > screenWidth - rectangle.width) {
            rectangle.x = screenWidth - rectangle.width;
        }

        if (realX > screenWidth - rectangle.width) {
            realX = screenWidth - rectangle.width;
        }
    }

    public void moveY(int screenHeight) {
        realY += vely;
        rectangle.y += vely;

        if (rectangle.y < 0) {
            rectangle.y = 0;
        } else if (rectangle.y > screenHeight - rectangle.height) {
            rectangle.y = screenHeight - rectangle.height;
        }

        if (realY < 0) {
            realY = 0;
        } else if (realY > screenHeight - rectangle.height) {
            realY = screenHeight - rectangle.height;
        }

    }

    public void scale(double scale) {
        rectangle.width *= scale;
        rectangle.height *= scale;
    }

    public void translate(int translatex, int translatey) {
        rectangle.translate(translatex, translatey);
    }

    public void broken() {
        velx = 0;
        vely = 0;
        try {
            image = ImageIO.read(new File("images/broken_spaceship.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}