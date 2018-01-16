import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;

/**
 * Created by naomikoo on 2017-07-05.
 */
public class CustomGameDimenPickerView extends JPanel {
    public CustomGameDimenPickerView(ChangeScreenListener changeScreenListener) {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel label = new JLabel("Pick the dimensions of your level in blocks");
        label.setAlignmentX(CENTER_ALIGNMENT);

        JSlider heightPicker = new JSlider(10, 50, 10);
        JSlider widthPicker = new JSlider(30, 150, 30);

        JPanel centerGridBagLayoutPanel = new JPanel(new GridBagLayout());

        heightPicker.setBorder(BorderFactory.createTitledBorder("Number of Height Blocks"));

        heightPicker.setMinorTickSpacing(5);
        heightPicker.setMajorTickSpacing(20);
        heightPicker.setPaintTicks(true);
        heightPicker.setPaintLabels(true);
        heightPicker.setLabelTable(heightPicker.createStandardLabels(10));
        heightPicker.setAlignmentX(CENTER_ALIGNMENT);

        widthPicker.setBorder(BorderFactory.createTitledBorder("Number of Width Blocks"));
        widthPicker.setMinorTickSpacing(5);
        widthPicker.setMajorTickSpacing(20);
        widthPicker.setPaintTicks(true);
        widthPicker.setPaintLabels(true);
        widthPicker.setLabelTable(widthPicker.createStandardLabels(10));
        widthPicker.setAlignmentX(CENTER_ALIGNMENT);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout());//buttonsPanel, BoxLayout.X_AXIS));

        JButton makelevel = new JButton("Make Custom Level");
        JButton goback = new JButton("Back to Main Menu");

        buttonsPanel.add(goback);
        buttonsPanel.add(makelevel);

        this.add(Box.createVerticalGlue());
        this.add(label);
        this.add(Box.createVerticalGlue());
        this.add(heightPicker, BorderLayout.CENTER);
        this.add(widthPicker, BorderLayout.CENTER);
        this.add(Box.createVerticalGlue());
        this.add(buttonsPanel, new GridBagConstraints());

        makelevel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeScreenListener.changeScreenToLevelEditor(heightPicker.getValue(), widthPicker.getValue());
            }
        });

        goback.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeScreenListener.changeScreenToMainMenu();
            }
        });

    }


}
