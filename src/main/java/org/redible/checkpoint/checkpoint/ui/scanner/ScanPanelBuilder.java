package org.redible.checkpoint.checkpoint.ui.scanner;

import javax.swing.*;
import java.awt.*;

public class ScanPanelBuilder {

    public static JPanel createScanPanel(JTextField cardField, DefaultListModel<String> logModel) {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Kártyaazonosító beolvasás");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(new JLabel("Diákigazolvány szám:"));
        inputPanel.add(cardField);
        panel.add(inputPanel, BorderLayout.CENTER);

        JList<String> eventList = new JList<>(logModel);
        eventList.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(eventList);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        panel.add(scrollPane, BorderLayout.SOUTH);

        return panel;
    }
}
