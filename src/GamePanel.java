import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.Timer;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    // each block is 20 by 20 pixels

    int blockHeight = 20;
    int blockWidth = 20;

    private Timer time;

    // default size:
    int heightBlocks = 20;
    int totalWidthBlocks = 100;
    int pixelsTraversed = 0;
    boolean finishedScrolling = false;

    int scrollSpeed = 1; // in blocks per second
    int frameRate = 30;

    public int WIDTH = 800, HEIGHT = 800;

    public Plane plane;

    public ArrayList<Column> columns;

    public int score;

    public boolean gameOver;
    public boolean doneGame;
    public boolean paused;

    public Random rand;

    public ArrayList<Column> customColumns;

    private MouseAdapter restartListener = new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            restart();
        }
    };
    private GameStatusListener statusListener;

    private ScrollingBackground background;

    public GamePanel(ArrayList<Column> customColumns, int customHeightBlocks, int customWidthBlocks, int fps) {
        heightBlocks = customHeightBlocks;
        totalWidthBlocks = customWidthBlocks;
        this.customColumns = customColumns;
        setup(customColumns, fps);
    }

    public GamePanel(int fps) {
        setup(null, fps);
    }

    public void setup(ArrayList<Column> customColumns, int fps) {
        blockHeight = HEIGHT / heightBlocks;
        blockWidth = blockHeight;
        setLayout(null);
        this.frameRate = fps;
        time = new Timer(1000 / fps, this); // starting a timer and passing the
        // actionlistener for the running animation
        background = new ScrollingBackground(getWidth(), getHeight());
        rand = new Random();

        plane = new Plane(blockWidth, HEIGHT / 2 - blockHeight, blockHeight, blockWidth); // in the vertical middle and one block from left
        columns = new ArrayList<Column>();

        if (customColumns == null) {
            // instantiate columns
            for (int i = 0; i < 10; i++) { // i want to add 10 columns each for default level
                addColumn();
            }

        } else {
            columns = new ArrayList(customColumns);
            for (Column column : columns) {
                column.setRectangle(getColumnRectangle(column));
            }
        }

        addKeyListener(this);
        doneGame = false;
        paused = false;

        time.start(); // starting
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int speed = (int) Math.ceil(1.0 / frameRate * scrollSpeed * blockWidth);

        if (pixelsTraversed == 0) {
            pixelsTraversed = WIDTH;
        }
        pixelsTraversed += speed;

        if (pixelsTraversed >= totalWidthBlocks * blockWidth) {
            speed = 0;
            finishedScrolling = true;
        }
        if (!paused) {

            // change x value of everything to minus speed
            plane.moveX(-speed, WIDTH);
            plane.moveY(HEIGHT);
            // keep track of how many blocks plane has passed and when it hits 0, success
            for (int i = 0; i < columns.size(); i++) {
                columns.get(i).moveX(-speed);
            }

            boolean tempDoneGame = false;//true;

            if (finishedScrolling && plane.rectangle.x >= WIDTH - plane.rectangle.width) {
                doneGame = true;
            }

            for (Column column : columns) {
                if (column.rectangle.intersects(plane.rectangle)) {
                    gameOver = true;
                    plane.broken();

//                    if (plane.rectangle.x + plane.rectangle.width >= column.rectangle.x) { // don't let plane go over columns
//                        plane.rectangle.x = column.rectangle.x - plane.rectangle.width;
//                    }

                    if (plane.velx > 0) { // going right
                        plane.rectangle.x = column.rectangle.x - plane.rectangle.width;
                    } else if (plane.velx < 0) { // going left
                        plane.rectangle.x = column.rectangle.x + column.rectangle.width;
                    } else if (plane.vely > 0) { // going down
                        plane.rectangle.y = column.rectangle.y - plane.rectangle.height;
                    } else if (plane.vely < 0) {
                        plane.rectangle.y = column.rectangle.y + column.rectangle.height;
                    }
                }

                if (!column.passed && column.rectangle.x + column.rectangle.width < plane.rectangle.x) {
                    score++;
                    column.passed();
                }

                // to use if game should end after last column
//                if (column.rectangle.x + column.rectangle.width >= plane.rectangle.x) {
//                    tempDoneGame = false;
//                }
            }

//            doneGame = tempDoneGame;

            if (plane.rectangle.x < 0) {
                plane.broken();
                gameOver = true;
            }

            background.setMovementSpeed((int) Math.round(speed * 1.5));
            this.repaint();
        }
    }

    public void restart() {
        removeAll();
        if (statusListener != null) {
            statusListener.addLabels();
        }
        score = 0;
        gameOver = false;
        doneGame = false;
        paused = false;
        revalidate();
        repaint();
        removeMouseListener(restartListener);

        plane = new Plane(blockWidth, HEIGHT / 2 - blockHeight, blockHeight, blockWidth); // in the vertical middle and one block from left

        columns = new ArrayList<Column>();

        if (customColumns == null) {
            for (int i = 0; i < 10; i++) { // i want to add 10 columns top/bottom each for default level
                addColumn();
            }
        } else {
            System.out.println("columns are custom");
            columns = customColumns;
        }
    }

    public void addColumn() {
        int blockx = rand.nextInt(totalWidthBlocks - 10) + 10;//(WIDTH + 100 + columns.size() * 300)/blockWidth;
        int blocky = rand.nextInt(heightBlocks);
        int width = rand.nextInt(5) + 2;
        int height = rand.nextInt(8) + 3;

        Column column = new Column(blocky, blockx, blocky + height - 1, blockx + width - 1);
        column.setRectangle(getColumnRectangle(column));
        columns.add(column);
    }

    // do this in gameview
    public void addCustomColumn(int topleftx, int toplefty, int bottomrightx, int bottomrighty) {
        int height = bottomrighty - toplefty + 1;
        int width = bottomrightx - topleftx + 1;
        Rectangle rectangle = new Rectangle((topleftx - 1) * blockWidth, (toplefty - 1) * blockHeight, width * blockWidth, height * blockHeight);
        Column column = new Column(topleftx, toplefty, bottomrightx, bottomrighty);
        column.setRectangle(rectangle);
        columns.add(column);
    }

    @Override
    protected void paintComponent(Graphics g) {
        // background
//        try {
//            Image image = ImageIO.read(new File("images/background.png"));
//            g.drawImage(image, 0, 0, getWidth(), getHeight(),null);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        background.paint(g);

        plane.paint(g);

        for (Column column : columns) {
            column.paint(g);
        }

        if (doneGame && statusListener != null) {
            statusListener.gameStatusChanged(true, score);
            addMouseListener(restartListener);
        } else {

            if (gameOver && statusListener != null) {
                statusListener.gameStatusChanged(false, score);
                addMouseListener(restartListener);
            }

            if (!gameOver && statusListener != null) {
                statusListener.scoreChanged(score);
            }
        }

        this.requestFocus();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!gameOver && !paused && !doneGame) {
            int keyCode = e.getKeyCode();
            int moveSize = (int) Math.ceil(1.0 / frameRate * scrollSpeed * blockWidth) * 2;

            switch (keyCode) {
                case KeyEvent.VK_W:
//                    plane.rectangle.y = Math.max(0, plane.rectangle.y - moveSize);
//                    plane.realY = Math.max(0, plane.realY - moveSize);
                    plane.vely = -moveSize;
                    break;
                case KeyEvent.VK_A:
//                    plane.rectangle.x = Math.max(0, plane.rectangle.x - moveSize);
//                    plane.realX = Math.max(0, plane.realX - moveSize);
                    plane.velx = -moveSize;
                    break;
                case KeyEvent.VK_S:
//                    plane.rectangle.y = Math.min(plane.rectangle.y + moveSize, HEIGHT - plane.rectangle.height);
//                    plane.realY = Math.min(plane.realY + moveSize, HEIGHT - plane.realY);
                    plane.vely = moveSize;
                    break;
                case KeyEvent.VK_D:
//                    if (finishedScrolling && plane.rectangle.x >= WIDTH - plane.rectangle.width) {
//                        doneGame = true;
//                    }
//                    plane.rectangle.x = Math.min(plane.rectangle.x + moveSize, WIDTH - plane.rectangle.width);
//                    plane.realX = Math.min(plane.realX + moveSize, WIDTH - plane.realX);
                    plane.velx = moveSize;
                    break;
                case KeyEvent.VK_UP:
                    // handle up
//                    plane.rectangle.y = Math.max(0, plane.rectangle.y - moveSize);
//                    plane.realY = Math.max(0, plane.realY - moveSize);

                    plane.vely = -moveSize;
                    break;
                case KeyEvent.VK_DOWN:
                    // handle down
//                    plane.rectangle.y = Math.min(plane.rectangle.y + moveSize, HEIGHT - plane.rectangle.height);
//                    plane.realY = Math.min(plane.realY + moveSize, HEIGHT - plane.realY);
                    plane.vely = moveSize;
                    break;
                case KeyEvent.VK_RIGHT:
                    // handle right
//                    if (finishedScrolling && plane.rectangle.x >= WIDTH - plane.rectangle.width) {
//                        doneGame = true;
//                    }
//
//                    plane.rectangle.x = Math.min(plane.rectangle.x + moveSize, WIDTH - plane.rectangle.width);
//                    plane.realX = Math.min(plane.realX + moveSize, WIDTH - plane.realX);

                    plane.velx = moveSize;
                    break;
                case KeyEvent.VK_LEFT:
                    // handle left
//                    plane.rectangle.x = Math.max(0, plane.rectangle.x - moveSize);
//                    plane.realX = Math.max(0, plane.realX - moveSize);
                    plane.velx = -moveSize;
                    break;
            }

            for (Column column : columns) {
                if (column.rectangle.intersects(plane.rectangle)) {
                    gameOver = true;
                    plane.broken();

                    switch (keyCode) {
                        case KeyEvent.VK_W:
                            plane.rectangle.y = column.rectangle.y + column.rectangle.height;
                            plane.realY = column.rectangle.y + column.rectangle.height;
                            break;
                        case KeyEvent.VK_A:
                            plane.rectangle.x = column.rectangle.x + column.rectangle.width;
                            plane.realX = column.rectangle.x + column.rectangle.width;
                            break;
                        case KeyEvent.VK_S:
                            plane.rectangle.y = column.rectangle.y - plane.rectangle.height;
                            plane.realY = column.rectangle.y - plane.rectangle.height;
                            break;
                        case KeyEvent.VK_D:
                            plane.rectangle.x = column.rectangle.x - plane.rectangle.width;
                            plane.realX = column.rectangle.x - plane.rectangle.width;
                            break;
                        case KeyEvent.VK_UP:
                            plane.rectangle.y = column.rectangle.y + column.rectangle.height;
                            plane.realY = column.rectangle.y + column.rectangle.height;
                            break;
                        case KeyEvent.VK_DOWN:
                            plane.rectangle.y = column.rectangle.y - plane.rectangle.height;
                            plane.realY = column.rectangle.y - plane.rectangle.height;
                            break;
                        case KeyEvent.VK_RIGHT:
                            plane.rectangle.x = column.rectangle.x - plane.rectangle.width;
                            plane.realX = column.rectangle.x - plane.rectangle.width;
                            break;
                        case KeyEvent.VK_LEFT:
                            plane.rectangle.x = column.rectangle.x + column.rectangle.width;
                            plane.realX = column.rectangle.x + column.rectangle.width;
                            break;
                    }
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (!gameOver && !paused && !doneGame) {
            int keyCode = e.getKeyCode();

            switch (keyCode) {
                case KeyEvent.VK_W:
                    plane.vely = 0;
                    break;
                case KeyEvent.VK_A:
                    plane.velx = 0;
                    break;
                case KeyEvent.VK_S:
                    plane.vely = 0;
                    break;
                case KeyEvent.VK_D:
                    plane.velx = 0;
                    break;
                case KeyEvent.VK_UP:
                    plane.vely = 0;
                    break;
                case KeyEvent.VK_DOWN:
                    plane.vely = 0;
                    break;
                case KeyEvent.VK_RIGHT:
                    plane.velx = 0;
                    break;
                case KeyEvent.VK_LEFT:
                    plane.velx = 0;
                    break;
            }

        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        paused = false;
    }

    public void resetSize() {
        int oldHeight = HEIGHT;
        int oldWidth = WIDTH;
        WIDTH = getWidth();
        HEIGHT = getHeight();
        background.resetSize(WIDTH, HEIGHT);
        blockHeight = HEIGHT / heightBlocks;
        blockWidth = blockHeight;

        for (Column column : columns) {
            if (((float) HEIGHT) / oldHeight != 0 && ((float) WIDTH) / oldWidth != 0) {
                column.resetSize(blockHeight, blockWidth, ((float) HEIGHT) / oldHeight, ((float) WIDTH) / oldWidth);
            }
        }

        plane.resetSize(blockHeight, blockWidth, ((float) HEIGHT) / oldHeight, ((float) WIDTH) / oldWidth);
    }

    public Rectangle getColumnRectangle(Column column) {
        int height = column.bottomrighty - column.toplefty + 1;
        int width = column.bottomrightx - column.topleftx + 1;
        Rectangle columnRectangle = new Rectangle((column.topleftx - 1) * blockWidth, (column.toplefty - 1) * blockHeight, width * blockWidth, height * blockHeight);

        return columnRectangle;
    }

    public void setScrollSpeed(int scrollSpeed) {
        this.scrollSpeed = scrollSpeed;
    }

    public void setFPS(int FPS) {
        time.setDelay(1000 / FPS);
        this.frameRate = FPS;
    }

    public void setStatusListener(GameStatusListener statusListener) {
        this.statusListener = statusListener;
    }

}