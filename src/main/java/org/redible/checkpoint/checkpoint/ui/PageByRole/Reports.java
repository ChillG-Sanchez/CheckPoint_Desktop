package org.redible.checkpoint.checkpoint.ui.PageByRole;

import javax.swing.*;

public class Reports extends JFrame {

    public Reports() {
        setTitle("Reports");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel label = new JLabel("This is the Reports page. View detailed analytics here.");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        add(label);
    }
}