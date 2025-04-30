package org.redible.checkpoint.checkpoint.ui;

import com.formdev.flatlaf.FlatLightLaf;
import org.redible.checkpoint.checkpoint.auth.AuthService;
import org.redible.checkpoint.checkpoint.ui.PageByRole.Roles.AdminPage;
import org.redible.checkpoint.checkpoint.ui.PageByRole.Roles.PortaPage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@SuppressWarnings("ALL")
public class SignIn extends JFrame {

    public SignIn() {
        super("Sign In");
        System.setProperty("apple.awt.application.name", "CheckPoint");

        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        setTitle("Sign In");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BufferedImage bgImage = null;
        try {
            bgImage = ImageIO.read(new File("C:/Users/ChillG/Desktop/Vizsgamunka/src/IT-infrastructure-security.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

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

        JPanel loginWrapper = new JPanel(new GridBagLayout());
        loginWrapper.setOpaque(false);
        loginWrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 50));
        backgroundPanel.add(loginWrapper, BorderLayout.EAST);

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

        JButton loginButton = new JButton("Login") {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.GRAY);
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 20, 20));
            }
        };
        loginButton.setFont(new Font("Arial", Font.BOLD, 18));
        loginButton.setPreferredSize(new Dimension(300, 50));
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        gbc.gridy = 4;
        loginPanel.add(loginButton, gbc);

        loginButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                loginButton.setBackground(new Color(30, 100, 180));
            }

            public void mouseExited(MouseEvent evt) {
                loginButton.setBackground(new Color(70, 130, 180));
            }
        });

        loginButton.addActionListener(e -> {
            String email = userText.getText();
            String password = new String(passwordText.getPassword());

            try {
                AuthService authService = new AuthService();
                authService.authenticateUser(email, password);
                String accessToken = authService.getAccessToken();

                String role = extractRoleFromToken(accessToken);

                switch (role.toUpperCase()) {
                    case "ADMIN":
                        new AdminPage(accessToken).setVisible(true);
                        break;
                    case "PORTA":
                        new PortaPage(accessToken).setVisible(true);
                        break;
                    // további szerepkörök, ha szükséges:
                    // case "TEACHER":
                    // case "STUDENT":
                    default:
                        JOptionPane.showMessageDialog(null, "Ismeretlen szerepkör: " + role, "Hiba", JOptionPane.ERROR_MESSAGE);
                        return;
                }

                dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Hiba történt a bejelentkezés során!", "Hiba", JOptionPane.ERROR_MESSAGE);
            }
        });

    }

    private String extractRoleFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new RuntimeException("Az accessToken nem megfelelő formátumú.");
            }
            String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
            org.json.JSONObject jsonPayload = new org.json.JSONObject(payload);

            return jsonPayload.getString("role");
        } catch (Exception e) {
            throw new RuntimeException("Hiba a role kiolvasása közben: " + e.getMessage(), e);
        }
    }

}