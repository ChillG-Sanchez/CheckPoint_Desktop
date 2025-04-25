package org.redible.checkpoint.checkpoint.ui.PageByRole.Roles;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

@SuppressWarnings("ALL")
public class AdminPage extends JFrame {

    private JPanel navigationPanel;
    private JPanel contentPanel;
    private boolean isMenuVisible = true;

    public AdminPage() {
        setTitle("Admin Page");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Set FlatLaf look and feel
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        // Navigation Panel
        navigationPanel = new JPanel();
        navigationPanel.setLayout(new BoxLayout(navigationPanel, BoxLayout.Y_AXIS));
        navigationPanel.setBackground(new Color(70, 130, 180));
        navigationPanel.setPreferredSize(new Dimension(200, getHeight()));

        // Add navigation buttons
        addNavButton("Dashboard");
        addNavButton("Users");
        addNavButton("Reports");
        addNavButton("Settings");

        // Toggle Menu Button
        JButton toggleButton = new JButton("☰");
        toggleButton.setFocusPainted(false);
        toggleButton.setBackground(new Color(30, 100, 180));
        toggleButton.setForeground(Color.WHITE);
        toggleButton.addActionListener(this::toggleMenu);

        navigationPanel.add(Box.createVerticalGlue());
        navigationPanel.add(toggleButton);

        // Content Panel
        contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Welcome to the Admin Page!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        contentPanel.add(welcomeLabel, BorderLayout.CENTER);

        // Add panels to frame
        add(navigationPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void addNavButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(new Color(30, 100, 180));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 40));
        button.addActionListener(e -> showContent(text));
        navigationPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        navigationPanel.add(button);
    }

    private void toggleMenu(ActionEvent e) {
        new Thread(() -> {
            int targetWidth = isMenuVisible ? 0 : 200;
            int step = isMenuVisible ? -10 : 10;
            int delay = 10;

            for (int width = navigationPanel.getWidth(); isMenuVisible ? width > targetWidth : width < targetWidth; width += step) {
                navigationPanel.setPreferredSize(new Dimension(width, getHeight()));
                navigationPanel.revalidate();
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            isMenuVisible = !isMenuVisible;
        }).start();
    }

    private void showContent(String page) {
        contentPanel.removeAll();
        JLabel label = new JLabel("You selected: " + page, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        contentPanel.add(label, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminPage().setVisible(true));
    }
}