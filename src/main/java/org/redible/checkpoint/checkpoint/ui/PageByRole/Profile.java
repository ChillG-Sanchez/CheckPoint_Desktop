package org.redible.checkpoint.checkpoint.ui.PageByRole;

import javax.swing.*;

public class Profile extends JFrame {

    public Profile() {
        setTitle("Profile");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel label = new JLabel("This is the Profile page.");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        add(label);
    }
}