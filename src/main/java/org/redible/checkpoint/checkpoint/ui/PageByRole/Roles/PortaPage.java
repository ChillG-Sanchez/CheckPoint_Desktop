package org.redible.checkpoint.checkpoint.ui.PageByRole.Roles;

import org.json.JSONArray;
import org.json.JSONObject;
import org.redible.checkpoint.checkpoint.util.ApiUtil;
import org.redible.checkpoint.checkpoint.util.ProfileUtil;
import org.redible.checkpoint.checkpoint.util.EventsUtil;
import org.redible.checkpoint.checkpoint.ui.NavigationUI;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("ALL")
public class PortaPage extends JFrame {
    private final String accessToken;
    private final JPanel contentPanel = new JPanel();

    public PortaPage(String accessToken) {
        this.accessToken = accessToken;

        setTitle("CheckPoint - Porta");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel navigationPanel = new JPanel();
        navigationPanel.setLayout(new BoxLayout(navigationPanel, BoxLayout.Y_AXIS));
        navigationPanel.setBackground(new Color(70, 130, 180));
        navigationPanel.setPreferredSize(new Dimension(200, getHeight()));

        NavigationUI.addStyledNavButton(navigationPanel, "Profil", "/icons/profile-svgrepo-com.svg", e -> showProfile());
        NavigationUI.addStyledNavButton(navigationPanel, "Események", "/icons/list-svgrepo-com.svg", e -> showEvents());
        NavigationUI.addStyledNavButton(navigationPanel, "Kártyabeolvasás", "/icons/barcode-svgrepo-com.svg", e -> showScanPanel());
        NavigationUI.addStyledNavButton(navigationPanel, "Kilépés", "/icons/logout-multimedia-ui-svgrepo-com.svg", e -> logout());

        add(navigationPanel, BorderLayout.WEST);

        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        add(contentPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void showScanPanel() {
        contentPanel.removeAll();

        JLabel titleLabel = new JLabel("Kártyaazonosító beolvasás");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new FlowLayout());
        JLabel label = new JLabel("Diákigazolvány szám:");
        JTextField cardField = new JTextField(20);
        inputPanel.add(label);
        inputPanel.add(cardField);
        contentPanel.add(inputPanel, BorderLayout.CENTER);

        DefaultListModel<String> eventLogModel = new DefaultListModel<>();
        JList<String> eventList = new JList<>(eventLogModel);
        eventList.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(eventList);
        scrollPane.setPreferredSize(new Dimension(contentPanel.getWidth(), 200));
        contentPanel.add(scrollPane, BorderLayout.SOUTH);

        final Timer[] debounceTimer = {null};
        final int idleThreshold = 300;

        cardField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void restartDebounce() {
                if (debounceTimer[0] != null) {
                    debounceTimer[0].stop();
                }

                debounceTimer[0] = new Timer(idleThreshold, e -> {
                    String cardNumber = cardField.getText().trim();
                    if (!cardNumber.isEmpty()) {
                        processCardScan(cardNumber, eventLogModel);
                        cardField.setText("");
                    }
                });

                debounceTimer[0].setRepeats(false);
                debounceTimer[0].start();
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                restartDebounce();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                restartDebounce();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                restartDebounce();
            }
        });

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void processCardScan(String cardNumber, DefaultListModel<String> eventLogModel) {
        new Thread(() -> {
            try {
                String userId = ApiUtil.extractUserIdFromToken(accessToken);

                org.json.JSONObject payload = new org.json.JSONObject();
                payload.put("studentCardNumber", cardNumber);
                payload.put("recordedByPortaId", Integer.parseInt(userId));

                String response = ApiUtil.makeApiCall(
                        "http://localhost:3000/events/register",
                        "POST",
                        payload.toString(),
                        accessToken
                );

                org.json.JSONObject json = new org.json.JSONObject(response);
                String status = json.optString("status", "ismeretlen");
                String message = json.optString("message", "Nincs visszajelzés");

                String timestamp = java.time.LocalDateTime.now().toString().replace("T", " ").substring(0, 19);
                String logEntry = String.format("[%s] %s → %s", timestamp, cardNumber, status.replace("_", " ").toUpperCase());

                SwingUtilities.invokeLater(() -> {
                    eventLogModel.addElement(logEntry);
                    fetchSmokingStatuses(eventLogModel);
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Hiba történt a rögzítés során: " + ex.getMessage(), "Hiba", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    private void fetchSmokingStatuses(DefaultListModel<String> eventLogModel) {
        new Thread(() -> {
            try {
                String response = ApiUtil.makeApiCall(
                        "http://localhost:3000/events/smoking-statuses",
                        "GET",
                        null,
                        accessToken
                );

                JSONArray statuses = new JSONArray(response);
                StringBuilder statusSummary = new StringBuilder();
                statusSummary.append("--- Napi dohányzási állapotok ---");

                for (int i = 0; i < statuses.length(); i++) {
                    JSONObject obj = statuses.getJSONObject(i);
                    String name = obj.getString("name");
                    String card = obj.getString("studentCardNumber");
                    double minutes = obj.getDouble("totalMinutes");
                    String status = obj.getString("status");

                    String statusLine = String.format("%s (%s) – %.0f perc – %s",
                            name, card, minutes,
                            switch (status) {
                                case "overlimit" -> "❌ TÚLLÉPÉS";
                                case "ok" -> "✅ OK";
                                default -> "⚠ ISMERETLEN";
                            });

                    statusSummary.append("\n").append(statusLine);
                }

                SwingUtilities.invokeLater(() -> {
                    eventLogModel.addElement(statusSummary.toString());
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }




    private void showProfile() {
        contentPanel.removeAll();
        JPanel profilePanel = ProfileUtil.buildProfilePanel(accessToken);
        contentPanel.add(profilePanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showEvents() {
        contentPanel.removeAll();
        contentPanel.add(EventsUtil.buildEventsPanel(accessToken), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }


    private void logout() {
        dispose();
        JOptionPane.showMessageDialog(this, "Sikeres kijelentkezés!", "Kijelentkezés", JOptionPane.INFORMATION_MESSAGE);
        new org.redible.checkpoint.checkpoint.ui.SignIn().setVisible(true);
    }
}