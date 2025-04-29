package org.redible.checkpoint.checkpoint.ui.PageByRole.Roles;

import javax.swing.*;
import java.awt.*;

public class TeacherPage extends JFrame {
    public TeacherPage() {
        setTitle("Teacher Page");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel label = new JLabel("Welcome, Teacher!", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        add(label);

        setVisible(true);
    }
}