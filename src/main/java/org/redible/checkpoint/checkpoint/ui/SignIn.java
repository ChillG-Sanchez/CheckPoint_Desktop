package org.redible.checkpoint.checkpoint.ui;

import com.formdev.flatlaf.FlatLightLaf;
import org.redible.checkpoint.checkpoint.auth.AuthService;
import org.redible.checkpoint.checkpoint.ui.PageByRole.Roles.AdminPage;
import org.redible.checkpoint.checkpoint.ui.PageByRole.Roles.PortaPage;
import org.redible.checkpoint.checkpoint.ui.PageByRole.Roles.StudentPage;
import org.redible.checkpoint.checkpoint.ui.PageByRole.Roles.TeacherPage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SignIn extends JFrame {

    public SignIn() {
        super("Sign In");
        System.setProperty("apple.awt.application.name", "CheckPoint");

        // Set FlatLaf look and feel
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        setTitle("Sign In");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Load background image
        BufferedImage bgImage = null;
        try {
            bgImage = ImageIO.read(new File("/Users/csillagcsaba/Desktop/Petrikes/CheckPoint/src/IT-infrastructure-security.jpg"));
            if (bgImage == null) {
                System.err.println("Image not found or could not be loaded.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Custom panel with background image
        BufferedImage finalBgImage = bgImage;
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (finalBgImage != null) {
                    g.drawImage(finalBgImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        add(backgroundPanel);

        // Panel for login inputs (right side)
        JPanel loginWrapper = new JPanel(new GridBagLayout());
        loginWrapper.setOpaque(false);
        loginWrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 50));
        backgroundPanel.add(loginWrapper, BorderLayout.EAST);

        // Login panel (semi-transparent background)
        JPanel loginPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(230, 230, 230, 180));
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30));
            }
        };
        loginPanel.setPreferredSize(new Dimension(450, 400));
        loginPanel.setOpaque(false);
        loginWrapper.add(loginPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 15, 15, 15);

        Font labelFont = new Font("Arial", Font.BOLD, 18);
        Font inputFont = new Font("Arial", Font.PLAIN, 16);

        JLabel userLabel = new JLabel("Email:");
        userLabel.setForeground(Color.BLACK);
        userLabel.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginPanel.add(userLabel, gbc);

        RoundedTextField userText = new RoundedTextField(20);
        userText.setFont(inputFont);
        userText.setPreferredSize(new Dimension(300, 40));
        gbc.gridy = 1;
        loginPanel.add(userText, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.BLACK);
        passwordLabel.setFont(labelFont);
        gbc.gridy = 2;
        loginPanel.add(passwordLabel, gbc);

        RoundedPasswordField passwordText = new RoundedPasswordField(20);
        passwordText.setFont(inputFont);
        passwordText.setPreferredSize(new Dimension(300, 40));
        gbc.gridy = 3;
        loginPanel.add(passwordText, gbc);

        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 18));
        loginButton.setPreferredSize(new Dimension(300, 50));
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        gbc.gridy = 4;
        loginPanel.add(loginButton, gbc);

        // Hover effect
        loginButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                loginButton.setBackground(new Color(30, 100, 180));
            }

            public void mouseExited(MouseEvent evt) {
                loginButton.setBackground(new Color(70, 130, 180));
            }
        });

        // Login button action
        loginButton.addActionListener(e -> {
            String email = userText.getText();
            String password = new String(passwordText.getPassword());

            try {
                String role = AuthService.authenticateUser(email, password);
                if (role != null) {
                    switch (role) {
                        case "admin":
                            new AdminPage().setVisible(true);
                            break;
                        case "teacher":
                            new TeacherPage().setVisible(true);
                            break;
                        case "student":
                            new StudentPage().setVisible(true);
                        case "porta":
                            new PortaPage().setVisible(true);
                            break;
                        default:
                            new HomePage().setVisible(true);
                            break;
                    }
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid email or password!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "An error occurred during login!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}