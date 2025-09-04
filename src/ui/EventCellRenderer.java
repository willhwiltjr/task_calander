package ui;
import model.Event;

import javax.swing.*;
import java.awt.*;

public class EventCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(
            JList<?> list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {

        JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);

        if (value instanceof Event event) {
            String timeRange = event.getStartTime() + " - " + event.getEndTime();
            label.setText("<html><b>" + event.getTitle() + "</b><br><small>" + timeRange + "</small></html>");
        }

        return label;
    }
}

