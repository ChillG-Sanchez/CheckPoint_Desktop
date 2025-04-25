package org.redible.checkpoint.checkpoint.ui;

import javax.swing.*;

public class Settings extends JFrame {

    public Settings() {
        setTitle("Settings");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel label = new JLabel("This is the Settings page.");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        add(label);
    }
}