import java.io.*;
import java.util.*;
import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.*;
import java.awt.event.*;
import javax.swing.ImageIcon;
import javax.swing.filechooser.*;

public class MainMenuView extends JPanel implements ActionListener {
    JButton button;
    ScrollingBackground background;
    private Timer time;

    public MainMenuView(ChangeScreenListener listener) {
        this.setLayout(new BorderLayout());
//        time = new Timer(1000 / 30, this);
        // for scrolling background
//        background = new ScrollingBackground(getWidth(), getHeight());
//        background.setMovementSpeed(5);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        JPanel leftpanel = new JPanel();
        leftpanel.setOpaque(false);
//        leftpanel.setLayout(new BoxLayout(leftpanel, BoxLayout.Y_AXIS));
        leftpanel.setLayout(new GridBagLayout());
        leftpanel.setAlignmentY(CENTER_ALIGNMENT);
//        this.add(leftpanel, gbc);
        this.add(leftpanel, BorderLayout.WEST);

        leftpanel.setBorder(new EmptyBorder(40, 40, 40, 10));
        JLabel title = new JLabel("Asteroids");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Serif", Font.PLAIN, 30));
        title.setBorder(new EmptyBorder(20, 0, 20, 20));
        leftpanel.add(title, gbc);

        JLabel menutitle = new JLabel("Main Menu");
        menutitle.setFont(new Font("Serif", Font.PLAIN, 20));
        menutitle.setBorder(new EmptyBorder(0,0,10,10));
        menutitle.setForeground(Color.WHITE);
        leftpanel.add(menutitle, gbc);

        button = new JButton("Play");
        button.setFont(new Font("Serif", Font.PLAIN, 15));
        button.setMaximumSize(new Dimension(200, 75));
        button.setPreferredSize(new Dimension(200, 75));
        button.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                listener.changeScreenToGame();
            }
        });

        leftpanel.add(button, gbc);

        JButton button2 = new JButton("Create Custom Level");
        button2.setFont(new Font("Serif", Font.PLAIN, 15));
        button2.setMaximumSize(new Dimension(200, 75));
        button2.setPreferredSize(new Dimension(200, 75));
        button2.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                listener.changeScreenToDimenPicker();
            }
        });

        leftpanel.add(button2, gbc);

        JButton button3 = new JButton("Load Custom Level");
        button3.setFont(new Font("Serif", Font.PLAIN, 15));
        button3.setMaximumSize(new Dimension(200, 75));
        button3.setPreferredSize(new Dimension(200, 75));
        button3.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser =new JFileChooser();
                javax.swing.filechooser.FileFilter filefilter = new FileNameExtensionFilter("","txt");
                chooser.setFileFilter(filefilter);
                int returnVal = chooser.showOpenDialog(chooser);
                if(returnVal==JFileChooser.APPROVE_OPTION) {
                    try {
                        FileReader fr = new FileReader(chooser.getSelectedFile());
                        BufferedReader br = new BufferedReader(fr);

                        String regex = "\\s+";

                        String[] dimens = br.readLine().split(regex);
                        int customHeight = Integer.parseInt(dimens[0]);
                        int customWidth = Integer.parseInt(dimens[1]);

                        ArrayList<Column> customColumns = new ArrayList<>();

                        String s;

                        while((s = br.readLine()) != null) {
                            String[] columnString = s.split(regex);
                            int topleftx = Integer.parseInt(columnString[0]);
                            int toplefty = Integer.parseInt(columnString[1]);
                            int bottomrightx = Integer.parseInt(columnString[2]);
                            int bottomrighty = Integer.parseInt(columnString[3]);

                            Column column = new Column(toplefty, topleftx, bottomrighty, bottomrightx);
                            customColumns.add(column);
                        }

                        listener.changeScreenToCustomGame(customColumns, customHeight, customWidth); // pass in file here

                        fr.close();
                    } catch (FileNotFoundException e1) {
                        System.out.println("file not found");
                        e1.printStackTrace();
                        JOptionPane.showMessageDialog(MainMenuView.this, "File not found");

                    } catch (Exception e1) {
                        JOptionPane.showMessageDialog(MainMenuView.this, "File is not in right format");
                    }
                }
            }
        });

        leftpanel.add(button3, gbc);

        JButton helpButton = new JButton("Help");
        helpButton.setFont(new Font("Serif", Font.PLAIN, 15));
        helpButton.setMaximumSize(new Dimension(200, 75));
        helpButton.setPreferredSize(new Dimension(200, 75));
        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String helpMessage = "This game is a side-scroller that loses if you hit any obstacle or let your spacecraft fall off-screen.\n" +
                        "You win this game by moving your spacecraft to the end of the level.\n" +
                        "Controls are to use arrow keys to move your spacecraft.\n"+
                        "Score is determined by how many obstacles your spacecraft passes.\n"+
                        "Enhancements are custom images, smooth movement of aircraft, and a scrolling background at a different speed.";
                JOptionPane.showMessageDialog(MainMenuView.this, helpMessage);
            }
        });

        leftpanel.add(helpButton, gbc);


        ImageIcon icon = new ImageIcon("images/spaceship.png");
        JLabel picLabel = new JLabel("", icon, JLabel.CENTER);
        picLabel.setAlignmentY(CENTER_ALIGNMENT);
//        this.add(picLabel, gbc);
        this.add(picLabel, BorderLayout.CENTER);

        // for scrolling background
//        this.addComponentListener(new ComponentAdapter() {
//            public void componentResized(ComponentEvent e) {
//                background.resetSize(getWidth(), getHeight());
//            }
//        });

//        time.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
//        background.paint(g);

        try {
            Image image = ImageIO.read(new File("images/background.png"));
            g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }
}