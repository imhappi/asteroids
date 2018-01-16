import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class GameView extends JPanel implements GameStatusListener{

    GamePanel gamePanel;
    boolean paused;

    // todo: extract into game class
    JLabel scoreLabel;
    JLabel gameStatusLabel;
    private ArrayList<Column> columns;
    private int customHeight;
    private int customWidth;

    public GameView(ChangeScreenListener listener, ArrayList<Column> columns, int customHeight, int customWidth) {
        this.setLayout(new BorderLayout());
        this.columns = columns;
        this.customHeight = customHeight;
        this.customWidth = customWidth;

        JPanel quitPanel = new JPanel();
        quitPanel.setLayout(new BoxLayout(quitPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(quitPanel);


        JButton quitButton = new JButton("Quit");
        JButton pauseButton = new JButton("Pause");
        JButton restartButton = new JButton("Restart");

        JSlider scrollSpeed = new JSlider(JSlider.HORIZONTAL, 1, 5, 1);
        scrollSpeed.setBorder(BorderFactory.createTitledBorder("Scroll Speed"));
        scrollSpeed.setMajorTickSpacing(1);
        scrollSpeed.setPaintTicks(true);
        scrollSpeed.setPaintLabels(true);
        scrollSpeed.setLabelTable(scrollSpeed.createStandardLabels(1));

        JSlider fpsSpeed = new JSlider(JSlider.HORIZONTAL, 15, 60, 30);
        fpsSpeed.setBorder(BorderFactory.createTitledBorder("FPS"));
        fpsSpeed.setMinorTickSpacing(5);
        fpsSpeed.setMajorTickSpacing(20);
        fpsSpeed.setPaintTicks(true);
        fpsSpeed.setPaintLabels(true);
        fpsSpeed.setLabelTable(fpsSpeed.createStandardLabels(10));

        quitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        pauseButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        restartButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        scrollSpeed.setAlignmentX(Component.CENTER_ALIGNMENT);
        fpsSpeed.setAlignmentX(Component.CENTER_ALIGNMENT);

        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                listener.changeScreenToMainMenu();
            }
        });

        pauseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (paused) {
                    gamePanel.resume();
                    paused = false;
                    pauseButton.setText("Pause");
                } else {
                    gamePanel.pause();
                    paused = true;
                    pauseButton.setText("Resume");
                }
            }
        });

        restartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (columns != null) {
                    listener.changeScreenToCustomGame(columns, customHeight, customWidth);
                } else {
                    listener.changeScreenToGame();
                }
            }
        });

        scrollSpeed.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                gamePanel.setScrollSpeed(scrollSpeed.getValue());
            }
        });

        fpsSpeed.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                gamePanel.setFPS(fpsSpeed.getValue());
            }
        });

        if (columns != null) {
            gamePanel = new GamePanel(columns, customHeight, customWidth, fpsSpeed.getValue());
        } else {
            gamePanel = new GamePanel(fpsSpeed.getValue());
        }
        gamePanel.setStatusListener(this);

        gamePanel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                gamePanel.resetSize();
            }
        });

        gamePanel.setFocusable(true);
        gamePanel.addKeyListener(gamePanel);
        paused = false;

        gamePanel.setLayout(new GridBagLayout());


        quitPanel.add(quitButton);
        quitPanel.add(pauseButton);
        quitPanel.add(restartButton);
        quitPanel.add(scrollSpeed);
        quitPanel.add(fpsSpeed);

        addLabels();

        this.add(gamePanel, BorderLayout.CENTER);
        this.add(scrollPane, BorderLayout.EAST);
    }

    @Override
    public void scoreChanged(int score) {
        scoreLabel.setText("Score: " + score);
    }

    @Override
    public void gameStatusChanged(boolean won, int score) {
        if (won) {
            gameStatusLabel.setText("You win!\nScore: " + score);
        } else {
            gameStatusLabel.setText("Game Over!\nScore: " + score);
        }
    }

    @Override
    public void addLabels() {
        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setFont(new Font("Serif", Font.PLAIN, 30));
        gameStatusLabel = new JLabel("");
        gameStatusLabel.setForeground(Color.WHITE);
        gameStatusLabel.setFont(new Font("Serif", Font.PLAIN, 30));

        GridBagConstraints gc = new GridBagConstraints();

        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.gridwidth = 1;
        gc.anchor = GridBagConstraints.NORTHWEST;
        gc.insets = new Insets(0, 0, 0, 0);
        gamePanel.add(scoreLabel, gc);

        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.gridwidth = 1;
        gc.anchor = GridBagConstraints.CENTER;
        gc.insets = new Insets(0, 0, 0, 0);
        gamePanel.add(gameStatusLabel, gc);
    }
}