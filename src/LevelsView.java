import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;
import javax.swing.ImageIcon;

// NOT USED ;-;
public class LevelsView extends JPanel {

    public LevelsView(ChangeScreenListener listener) {
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BorderLayout());
        this.add(titlePanel, BorderLayout.NORTH);

        JLabel title = new JLabel("Levels");
        title.setFont(new Font("Serif", Font.PLAIN, 40));
        title.setBorder(new EmptyBorder(20, 0, 20, 20));
        titlePanel.add(title);
        titlePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel levelsPanel = new JPanel();
        levelsPanel.setLayout(new GridLayout(2,3));

        LevelViewPane level1 = new LevelViewPane();
        LevelViewPane level2 = new LevelViewPane();
        LevelViewPane level3 = new LevelViewPane();
        LevelViewPane level4 = new LevelViewPane();
        LevelViewPane level5 = new LevelViewPane();
        LevelViewPane level6 = new LevelViewPane();

        levelsPanel.add(level1);
        levelsPanel.add(level2);
        levelsPanel.add(level3);
        levelsPanel.add(level4);
        levelsPanel.add(level5);
        levelsPanel.add(level6);

        this.add(levelsPanel, BorderLayout.NORTH);

    }
}