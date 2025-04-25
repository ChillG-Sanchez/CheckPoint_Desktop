package org.redible.checkpoint.checkpoint.ui.PageByRole.Roles;

import javax.swing.*;

public class StudentPage extends JFrame {

    public StudentPage() {
        setTitle("Student Page");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel label = new JLabel("Welcome to the Student Page!");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        add(label);
    }
}