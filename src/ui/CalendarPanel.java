package ui;

import javax.swing.*;
import java.awt.*;

public class CalendarPanel extends JPanel {

    public CalendarPanel() {
        setLayout(new GridLayout(24, 1)); // 24 rows for 24 hours

        for (int hour = 0; hour < 24; hour++) {
            add(new HourSlotPanel(hour));
        }
    }
}
