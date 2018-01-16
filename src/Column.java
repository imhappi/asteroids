import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Column {
	// all of these dimens are in blocks
	public int toplefty;
	public int topleftx;
	public int bottomrighty;
	public int bottomrightx;

	public Rectangle rectangle;
	public int heightBlocks;
	public int widthBlocks;
	public boolean passed = false; // points are accumulated based on whether or not column has been passed
	private Image image;

	public Column(int toplefty, int topleftx, int bottomrighty, int bottomrightx) {
		this.toplefty = toplefty;
		this.topleftx = topleftx;
		this.bottomrighty = bottomrighty;
		this.bottomrightx = bottomrightx;

        heightBlocks = bottomrighty - toplefty + 1;
        widthBlocks = bottomrightx - topleftx + 1;

		try {
			Random ran = new Random();
			int x = ran.nextInt(3) ;

			if (x == 0) {
				image = ImageIO.read(new File("images/asteroid1.png"));
			} else if (x == 1) {
				image = ImageIO.read(new File("images/asteroid2.png"));
			} else {
				image = ImageIO.read(new File("images/asteroid3.png"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    public void setRectangle(Rectangle rectangle) {
	    this.rectangle = rectangle;
    }

    public void resetSize(int blockHeight, int blockWidth, float heightRatio, float widthRatio) {
        rectangle.height = heightBlocks * blockHeight;
        rectangle.width = widthBlocks * blockWidth;

		rectangle.x = Math.round(rectangle.x * widthRatio);
        rectangle.y = Math.round(rectangle.y * heightRatio);
    }

	public void paint(Graphics g) {
		g.drawImage(image, rectangle.x, rectangle.y, rectangle.width, rectangle.height, null);
	}

	public void moveX(int x) {
		rectangle.x += x;
	}

	public void passed() {
		passed = true;
	}
}