package ui;

import model.ViewMode;

import javax.swing.*;
import java.awt.*;

public class setupButtons extends JPanel {
    private final JButton AddEventBtn = new JButton("Add Event");
    private final JButton ZoomInBtn = new JButton("Zoom In");
    private final JButton ZoomOutBtn = new JButton("Zoom Out");
    private final JButton dailyViewBtn = new JButton("Daily View");
    private final JButton weeklyViewBtn = new JButton("Weekly View");
    private final JButton monthlyViewBtn = new JButton("Monthly View");
    private ButtonPanelListener listener;



    public void setButtonPanelListener() {
        this.listener = listener;
    }

    public setupButtons(JPanel buttonPanel) {
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.setPreferredSize(new Dimension(200, 600));

        // Add components
        for (JButton btn : new JButton[]{AddEventBtn, dailyViewBtn, weeklyViewBtn, monthlyViewBtn, ZoomInBtn, ZoomOutBtn}) {
            buttonPanel.add(btn);
            buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        // Assign actions
        //daily
        dailyViewBtn.addActionListener(e -> { if (listener != null) listener.onDailyViewClicked(); });
        //weekly
        weeklyViewBtn.addActionListener(e -> { if (listener != null) listener.onWeeklyViewClicked(); });
        //monthly
        monthlyViewBtn.addActionListener(e -> { if (listener != null) listener.onMonthlyViewClicked(); });
        //addevent
        AddEventBtn.addActionListener(e -> { if (listener != null) listener.onAdd(); });
        //zoomin
        ZoomInBtn.addActionListener(e ->{ if (listener != null) listener.onZoomInClicked(); });
        //zoomout
        ZoomOutBtn.addActionListener(e -> { if (listener != null) listener.onZoomOutClicked(); });
    }

    public void setButtonPanelListener(ButtonPanelListener listener){ this.listener = listener; }

    private void setViewButtonsVisible(ViewMode mode)
    {
        switch (mode) {
            case DAILY -> {
                dailyViewBtn.setVisible(false);
                weeklyViewBtn.setVisible(true);
                monthlyViewBtn.setVisible(true);
            }
            case WEEKLY -> {
                dailyViewBtn.setVisible(true);
                weeklyViewBtn.setVisible(false);
                monthlyViewBtn.setVisible(true);
            }
            case MONTHLY -> {
                dailyViewBtn.setVisible(true);
                weeklyViewBtn.setVisible(true);
                monthlyViewBtn.setVisible(false);
            }
        }
    }
}
