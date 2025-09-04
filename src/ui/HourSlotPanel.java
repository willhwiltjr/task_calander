package ui;

import javax.swing.*;
import java.awt.*;

public class HourSlotPanel extends JPanel {
    public HourSlotPanel(int hour) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.GRAY));

        String label = String.format("%02d:00", hour);
        add(new JLabel(label), BorderLayout.WEST);
        setPreferredSize(new Dimension(800, 50));
    }
}

