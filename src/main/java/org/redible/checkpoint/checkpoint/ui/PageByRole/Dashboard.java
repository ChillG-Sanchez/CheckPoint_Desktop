package org.redible.checkpoint.checkpoint.ui.PageByRole;

import javax.swing.*;

public class Dashboard extends JFrame {

    public Dashboard() {
        setTitle("Dashboard");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel label = new JLabel("Welcome to the Dashboard! Here are your key metrics.");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        add(label);
    }
}