import javax.swing.*;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.*;

/**
 * Created by naomikoo on 2017-07-02.
 */
public class EditorView extends JPanel implements EditorPanelListener {

    JButton undo;
    JButton redo;
    JButton deleteButton;
    JButton cut;
    JButton paste;
    GameEditorPanel gameEditorPanel;

    public EditorView(int heightBlocks, int widthBlocks, JFrame frame, ChangeScreenListener listener) {
        this.setLayout(new BorderLayout());
        // always has block of same size in obstacle panel
        JButton helpButton = new JButton("Help");
        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String helpMessage = "Click and drag the obstacles on the left panel to where you want them to be in the level on the right!\n" +
                        "You can resize and move them around on the level.\n" +
                        "Your custom level will be saved as the best approximation to your level in blocks.";
                JOptionPane.showMessageDialog(EditorView.this, helpMessage);
            }
        });
        JButton backbutton = new JButton("Back to Menu");
        backbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.changeScreenToMainMenu();
            }
        });

        JPanel obstaclePanel = new JPanel();

        obstaclePanel.setLayout(new BoxLayout(obstaclePanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(obstaclePanel);
        obstaclePanel.add(backbutton, BorderLayout.CENTER);
        obstaclePanel.add(helpButton, BorderLayout.CENTER);

        DraggableColumn column1 = new DraggableColumn(1, 1);
        DraggableColumn column2 = new DraggableColumn(2, 1);
        DraggableColumn column3 = new DraggableColumn(1, 2);
        obstaclePanel.add(column1);
        obstaclePanel.add(column2);
        obstaclePanel.add(column3);

        gameEditorPanel = new GameEditorPanel(heightBlocks, widthBlocks, frame, this);
        gameEditorPanel.setTransferHandler(new TransferHandler("text"));

        JScrollPane gameScrollpane = new JScrollPane(gameEditorPanel);

//        gameEditorPanel.addComponentListener(new ComponentAdapter() {
//            public void componentResized(ComponentEvent e) {
//                gameEditorPanel.resetSize(gameScrollpane.getHeight(), gameScrollpane.getWidth());
//            }
//        });


        JButton saveButton = new JButton("Save Level");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // todo convert gameeditorpanel's obstacle positions to correct placements

                JFileChooser fileChooser = new JFileChooser();

// let the user choose the destination file
                if (fileChooser.showSaveDialog(EditorView.this) == JFileChooser.APPROVE_OPTION) {
                    // indicates whether the user still wants to export the settings
                    boolean doExport = true;

                    // indicates whether to override an already existing file
                    boolean overrideExistingFile = false;

                    // get destination file
                    File destinationFile = new File(fileChooser.getSelectedFile().getAbsolutePath());

                    // check if file already exists
                    while (doExport && destinationFile.exists() && !overrideExistingFile) {
                        // let the user decide whether to override the existing file
                        overrideExistingFile = (JOptionPane.showConfirmDialog(null, "Replace file?", "Save Level", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION);

                        // let the user choose another file if the existing file shall not be overridden
                        if (!overrideExistingFile) {
                            if (fileChooser.showSaveDialog(EditorView.this) == JFileChooser.APPROVE_OPTION) {
                                // get new destination file
                                destinationFile = new File(fileChooser.getSelectedFile().getAbsolutePath());
                            } else {
                                // seems like the user does not want to export the settings any longer
                                doExport = false;
                            }
                        }
                    }

                    // perform the actual export
                    if (doExport) {
                        try {
                            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(destinationFile + ".txt")); //use its name
                            bufferedWriter.write(gameEditorPanel.getObstaclesInText());
                            bufferedWriter.close();
                            listener.changeScreenToMainMenu(); // after saving file
                        } catch (FileNotFoundException e1) {
                            e1.printStackTrace();
                            System.out.println("file not found");
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }

            }
        });
        deleteButton = new JButton("Delete Obstacle");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameEditorPanel.deleteCurrentSelection();
            }
        });
        deleteButton.setEnabled(false); // enable only when obstacle is selected
        undo = new JButton("Undo");
        undo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameEditorPanel.undo();
            }
        });
        undo.setEnabled(false); // enable only when undo stack is not empty
        redo = new JButton("Redo");
        redo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // todo keep undo redo stack
            }
        });

        cut = new JButton("Cut");
        cut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameEditorPanel.cutCurrentSelection();
            }
        });
        cut.setEnabled(false);

        paste = new JButton("Paste");
        paste.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameEditorPanel.pasteItem();
            }
        });
        paste.setEnabled(false);

        JButton zoomin = new JButton("Zoom in");
        JButton zoomout = new JButton("Zoom out");
        zoomin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                zoomin.setEnabled(false);
                zoomout.setEnabled(true);
                gameEditorPanel.setScale(2);
                gameScrollpane.setViewportView(gameEditorPanel);
            }
        });
        zoomout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                zoomin.setEnabled(true);
                zoomout.setEnabled(false);
                gameEditorPanel.setScale(0.5);
                gameScrollpane.setViewportView(gameEditorPanel);
            }
        });
        zoomout.setEnabled(false);

        helpButton.setAlignmentX(CENTER_ALIGNMENT);
        backbutton.setAlignmentX(CENTER_ALIGNMENT);
        deleteButton.setAlignmentX(CENTER_ALIGNMENT);
        undo.setAlignmentX(CENTER_ALIGNMENT);
        redo.setAlignmentX(CENTER_ALIGNMENT);
        zoomin.setAlignmentX(CENTER_ALIGNMENT);
        zoomout.setAlignmentX(CENTER_ALIGNMENT);
        saveButton.setAlignmentX(CENTER_ALIGNMENT);
        cut.setAlignmentX(CENTER_ALIGNMENT);
        paste.setAlignmentX(CENTER_ALIGNMENT);

        obstaclePanel.add(deleteButton);
        obstaclePanel.add(undo);
        obstaclePanel.add(cut);
        obstaclePanel.add(paste);
        obstaclePanel.add(zoomin);
        obstaclePanel.add(zoomout);
        obstaclePanel.add(saveButton);

        this.add(gameScrollpane, BorderLayout.CENTER);
        this.add(scrollPane, BorderLayout.WEST);
    }

    @Override
    public void userActioned(boolean undosAvailable, boolean redosAvailable, int obstacleNum) {
        if (undosAvailable) {
            undo.setEnabled(true);
        } else {
            undo.setEnabled(false);
        }

        if (redosAvailable) {
            redo.setEnabled(true);
        } else {
            redo.setEnabled(false);
        }

//        if (obstacleNum > 0) {
//            deleteButton.setEnabled(true);
//        } else {
//            deleteButton.setEnabled(false);
//        }
    }

    @Override
    public void redraw() {
        gameEditorPanel.repaint();
    }

    @Override
    public double getScale() {
        return gameEditorPanel.scale;
    }

    @Override
    public Plane getPlane() {
        return gameEditorPanel.plane;
    }

    @Override
    public void itemSelected(boolean selected) {
        if (selected) {
            deleteButton.setEnabled(true);
            cut.setEnabled(true);
        } else {
            deleteButton.setEnabled(false);
            cut.setEnabled(false);
        }
    }

    @Override
    public void itemCut() {
        paste.setEnabled(true);
    }

    @Override
    public JPanel getComponent() {
        return this;
    }

    public static class DraggableColumn extends Component implements DragGestureListener, DragSourceListener {
        DragSource dragSource;
        int blockwidth = 1; // for now, 1x1 block
        int blockheight = 1;

        public DraggableColumn(int blockwidth, int blockheight) {
            this.blockwidth = blockwidth;
            this.blockheight = blockheight;
            dragSource = new DragSource();
            setPreferredSize(new Dimension(blockheight + 30, blockheight + 30));

            dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, this);
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);

            g.setColor(Color.green);
            int centerx = getWidth() / 2 - blockwidth * 20 / 2;
            int centery = getHeight() / 2 - blockheight * 20 / 2;
            g.fillRect(centerx, centery, blockwidth * 20, blockheight * 20);
        }

        public void dragGestureRecognized(DragGestureEvent evt) {

            DragDropTransferable transferable = new DragDropTransferable(0, 0, blockwidth, blockheight);

            dragSource.startDrag(evt, DragSource.DefaultCopyDrop, transferable, this);
        }

        public void dragEnter(DragSourceDragEvent evt) {

        }

        public void dragOver(DragSourceDragEvent evt) {
        }

        public void dragExit(DragSourceEvent evt) {
        }

        public void dropActionChanged(DragSourceDragEvent evt) {
        }

        public void dragDropEnd(DragSourceDropEvent evt) {
        }

    }

}
