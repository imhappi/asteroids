import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;
import javax.swing.ImageIcon;

// NOT USED according to specs
public class LevelViewPane extends JPanel {

    public LevelViewPane() {
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        ImageIcon icon = new ImageIcon("images/1.jpg");
        Image image = icon.getImage(); // transform it
        Image newimg = image.getScaledInstance(160, 120,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
        icon = new ImageIcon(newimg);
        JLabel picLabel = new JLabel("", icon, JLabel.CENTER);
        this.add(picLabel, BorderLayout.CENTER);

        JLabel title = new JLabel("level name", JLabel.CENTER);
//        title.setFont(new Font("Serif", Font.PLAIN, 40));
        title.setBorder(new EmptyBorder(20, 0, 20, 20));
        this.add(title, BorderLayout.SOUTH);
        this.setBorder(new EmptyBorder(20, 20, 20, 20));
    }
}