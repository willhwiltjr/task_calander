package ui;
import model.LocalEvent;

import javax.swing.*;
import java.awt.*;

public class EventCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(
            JList<?> list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {

        JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);

        if (value instanceof LocalEvent localEvent) {
            String timeRange = localEvent.getStartTime() + " - " + localEvent.getEndTime();
            label.setText("<html><b>" + localEvent.getTitle() + "</b><br><small>" + timeRange + "</small></html>");
        }

        return label;
    }
}

