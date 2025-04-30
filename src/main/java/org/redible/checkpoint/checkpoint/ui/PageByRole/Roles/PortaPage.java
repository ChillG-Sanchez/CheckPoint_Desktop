package org.redible.checkpoint.checkpoint.ui.PageByRole.Roles;

import org.json.JSONObject;
import org.redible.checkpoint.checkpoint.util.ApiUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class PortaPage extends JFrame {
    private final String accessToken;
    private final JPanel navigationPanel;
    private final JPanel contentPanel;

    public PortaPage(String accessToken) {
        this.accessToken = accessToken;

        setTitle("CheckPoint - Porta");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        navigationPanel = new JPanel();
        navigationPanel.setLayout(new BoxLayout(navigationPanel, BoxLayout.Y_AXIS));
        navigationPanel.setBackground(new Color(70, 130, 180));
        navigationPanel.setPreferredSize(new Dimension(200, getHeight()));

        addNavButton("Profil", "C:/Users/ChillG/Desktop/Vizsgamunka/CheckPoint_Desktop/src/main/resources/icons/profile-svgrepo-com.svg", e -> showProfile());
        addNavButton("Események", "C:/Users/ChillG/Desktop/Vizsgamunka/CheckPoint_Desktop/src/main/resources/icons/list-svgrepo-com.svg", e -> showEvents());
        addNavButton("Kártyabeolvasás", "C:/Users/ChillG/Desktop/Vizsgamunka/CheckPoint_Desktop/src/main/resources/icons/barcode-svgrepo-com.svg", e -> showScanPanel());
        addNavButton("Kilépés", "C:/Users/ChillG/Desktop/Vizsgamunka/CheckPoint_Desktop/src/main/resources/icons/logout-multimedia-ui-svgrepo-com.svg", e -> logout());

        add(navigationPanel, BorderLayout.WEST);

        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        add(contentPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void addNavButton(String text, String iconPath, ActionListener action) {
        try {
            ImageIcon icon = new ImageIcon(iconPath);
            JButton button = new JButton(text, icon) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                    super.paintComponent(g);
                }

                @Override
                protected void paintBorder(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                }
            };
            button.setFocusPainted(false);
            button.setBackground(new Color(30, 100, 180));
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Arial", Font.BOLD, 16));
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setMaximumSize(new Dimension(180, 50));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            navigationPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            navigationPanel.add(button);
            button.addActionListener(action);
        } catch (Exception e) {
            throw new IllegalArgumentException("Ikon betöltése sikertelen: " + iconPath, e);
        }
    }

    private void showScanPanel() {
        contentPanel.removeAll();

        JLabel titleLabel = new JLabel("Kártyaazonosító beolvasás");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());

        JLabel label = new JLabel("Diákigazolvány szám:");
        JTextField cardField = new JTextField(20);
        JButton submitButton = new JButton("Rögzítés");

        inputPanel.add(label);
        inputPanel.add(cardField);
        inputPanel.add(submitButton);

        contentPanel.add(inputPanel, BorderLayout.CENTER);

        submitButton.addActionListener(e -> {
            String cardNumber = cardField.getText().trim();
            if (cardNumber.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Kérlek add meg a diákigazolvány számot!", "Hiányzó adat", JOptionPane.WARNING_MESSAGE);
                return;
            }

            new Thread(() -> {
                try {
                    String userId = ApiUtil.extractUserIdFromToken(accessToken);

                    JSONObject payload = new JSONObject();
                    payload.put("studentCardNumber", cardNumber);
                    payload.put("recordedByPortaId", Integer.parseInt(userId));

                    String response = ApiUtil.makeApiCall("http://localhost:3000/events/register", "POST", payload.toString(), accessToken);

                    JSONObject json = new JSONObject(response);
                    String status = json.optString("status", "ismeretlen");
                    String message = json.optString("message", "Nincs visszajelzés");

                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(this, message + " (" + status + ")", "Siker", JOptionPane.INFORMATION_MESSAGE)
                    );
                } catch (Exception ex) {
                    ex.printStackTrace();
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(this, "Hiba történt a rögzítés során: " + ex.getMessage(), "Hiba", JOptionPane.ERROR_MESSAGE)
                    );
                }
            }).start();
        });

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showProfile() {
        // TODO: Profil megjelenítése portás felületen
    }

    private void showEvents() {
        // TODO: Eseménylista betöltése
    }

    private void logout() {
        dispose();
        JOptionPane.showMessageDialog(this, "Sikeres kijelentkezés!", "Kijelentkezés", JOptionPane.INFORMATION_MESSAGE);
        new org.redible.checkpoint.checkpoint.ui.SignIn().setVisible(true);
    }
}