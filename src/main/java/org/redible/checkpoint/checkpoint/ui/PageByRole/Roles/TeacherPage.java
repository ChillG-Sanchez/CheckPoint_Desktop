package org.redible.checkpoint.checkpoint.ui.PageByRole.Roles;

import javax.swing.*;

public class TeacherPage extends JFrame {

    public TeacherPage() {
        setTitle("Teacher Page");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel label = new JLabel("Welcome to the Teacher Page!");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        add(label);
    }
}