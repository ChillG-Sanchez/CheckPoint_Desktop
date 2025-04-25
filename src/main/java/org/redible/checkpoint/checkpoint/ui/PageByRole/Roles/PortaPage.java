package org.redible.checkpoint.checkpoint.ui.PageByRole.Roles;

import javax.swing.*;

public class PortaPage extends JFrame {

    public PortaPage() {
        setTitle("Porta Page");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel label = new JLabel("Welcome to the Porta Page!");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        add(label);
    }
}