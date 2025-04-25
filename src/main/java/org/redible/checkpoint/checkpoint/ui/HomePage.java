package org.redible.checkpoint.checkpoint.ui;

import org.redible.checkpoint.checkpoint.ui.PageByRole.Dashboard;
import org.redible.checkpoint.checkpoint.ui.PageByRole.Profile;
import org.redible.checkpoint.checkpoint.ui.PageByRole.Reports;

import javax.swing.*;

public class HomePage extends JFrame {

    public HomePage() {
        setTitle("Home Page");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        add(panel);

        JButton dashboardButton = new JButton("Dashboard");
        dashboardButton.setBounds(50, 50, 120, 30);
        panel.add(dashboardButton);

        JButton profileButton = new JButton("Profile");
        profileButton.setBounds(200, 50, 120, 30);
        panel.add(profileButton);

        JButton settingsButton = new JButton("Settings");
        settingsButton.setBounds(50, 100, 120, 30);
        panel.add(settingsButton);

        JButton reportsButton = new JButton("Reports");
        reportsButton.setBounds(200, 100, 120, 30);
        panel.add(reportsButton);

        // Add action listeners to navigate to respective screens
        dashboardButton.addActionListener(e -> new Dashboard().setVisible(true));
        profileButton.addActionListener(e -> new Profile().setVisible(true));
        settingsButton.addActionListener(e -> new Settings().setVisible(true));
        reportsButton.addActionListener(e -> new Reports().setVisible(true));
    }
}