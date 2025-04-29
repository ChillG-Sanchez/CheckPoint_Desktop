package org.redible.checkpoint.checkpoint.ui.PageByRole.Roles;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import okhttp3.*;

public class AdminPage extends JFrame {

    private final JPanel navigationPanel;
    private final JPanel contentPanel;
    private final String accessToken;
    private final OkHttpClient httpClient = new OkHttpClient();

    public AdminPage(String accessToken) {
        this.accessToken = accessToken;
        setTitle("CheckPoint");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        navigationPanel = new JPanel();
        navigationPanel.setLayout(new BoxLayout(navigationPanel, BoxLayout.Y_AXIS));
        navigationPanel.setBackground(new Color(70, 130, 180));
        navigationPanel.setPreferredSize(new Dimension(200, getHeight()));
        addNavButton("Profil", "C:/Users/ChillG/Desktop/Vizsgamunka/CheckPoint_Desktop/src/main/resources/icons/profile-svgrepo-com.svg", e -> showProfile());
        addNavButton("Keresés", "C:/Users/ChillG/Desktop/Vizsgamunka/CheckPoint_Desktop/src/main/resources/icons/search-svgrepo-com.svg", e -> showSearch());
        addNavButton("Beállítások", "C:/Users/ChillG/Desktop/Vizsgamunka/CheckPoint_Desktop/src/main/resources/icons/settings-svgrepo-com.svg", e -> showSettings());
        addNavButton("Események", "C:/Users/ChillG/Desktop/Vizsgamunka/CheckPoint_Desktop/src/main/resources/icons/list-svgrepo-com.svg", e -> showEvents());
        addNavButton("Kilépés", "C:/Users/ChillG/Desktop/Vizsgamunka/CheckPoint_Desktop/src/main/resources/icons/logout-multimedia-ui-svgrepo-com.svg", e -> logout());
        add(navigationPanel, BorderLayout.WEST);
        contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setLayout(new BorderLayout());
        add(contentPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    public static AdminPage createInstance(String accessToken) {
        return new AdminPage(accessToken);
    }

    private void addNavButton(String text, String iconPath, ActionListener action) {
        try {
            ImageIcon icon = loadSvgIcon(iconPath, 32, 32);
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

    private ImageIcon loadSvgIcon(String filePath, int width, int height) {
        try {
            PNGTranscoder transcoder = new PNGTranscoder();
            transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, (float) width);
            transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, (float) height);
            TranscoderInput input = new TranscoderInput(new FileInputStream(filePath));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            TranscoderOutput output = new TranscoderOutput(outputStream);
            transcoder.transcode(input, output);
            outputStream.flush();
            return new ImageIcon(outputStream.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Hiba történt az SVG ikon betöltésekor: " + filePath, e);
        }
    }

    private void showProfile() {
        System.out.println("showProfile: Kezdődik a profil adatok betöltése.");
        contentPanel.removeAll();
        JLabel titleLabel = new JLabel("Profil adatok");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField roleField = new JTextField();
        nameField.setEditable(false);
        emailField.setEditable(false);
        roleField.setEditable(false);

        formPanel.add(new JLabel("Név:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("E-mail:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Szerepkör:"));
        formPanel.add(roleField);

        JButton editButton = new JButton("Szerkesztés");
        editButton.setFont(new Font("Arial", Font.BOLD, 16));
        editButton.setBackground(new Color(70, 130, 180));
        editButton.setForeground(Color.WHITE);
        editButton.setFocusPainted(false);

        contentPanel.add(formPanel, BorderLayout.CENTER);
        contentPanel.add(editButton, BorderLayout.SOUTH);

        new Thread(() -> {
            try {
                String userId = extractUserIdFromToken(accessToken);
                String response = makeApiCall("http://localhost:3000/users/" + userId, "GET", null);

                SwingUtilities.invokeLater(() -> {
                    try {
                        org.json.JSONObject jsonResponse = new org.json.JSONObject(response);
                        nameField.setText(jsonResponse.getString("name"));
                        emailField.setText(jsonResponse.getString("email"));
                        roleField.setText(jsonResponse.getString("role"));
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this, "Hiba történt az adatok feldolgozása során!", "Hiba", JOptionPane.ERROR_MESSAGE);
                    }
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Hiba történt az adatok lekérése során!", "Hiba", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();

        editButton.addActionListener(e -> showEditProfile(nameField.getText(), emailField.getText()));
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showEditProfile(String currentName, String currentEmail) {
        contentPanel.removeAll();
        JLabel titleLabel = new JLabel("Profil szerkesztése");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField nameField = new JTextField(currentName);
        JTextField emailField = new JTextField(currentEmail);
        JPasswordField passwordField = new JPasswordField();

        formPanel.add(new JLabel("Név:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("E-mail:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Új jelszó:"));
        formPanel.add(passwordField);

        JButton saveButton = new JButton("Mentés");
        saveButton.setFont(new Font("Arial", Font.BOLD, 16));
        saveButton.setBackground(new Color(70, 130, 180));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);

        contentPanel.add(formPanel, BorderLayout.CENTER);
        contentPanel.add(saveButton, BorderLayout.SOUTH);

        saveButton.addActionListener(e -> {
            new Thread(() -> {
                try {
                    String userId = extractUserIdFromToken(accessToken);
                    org.json.JSONObject updateData = new org.json.JSONObject();
                    updateData.put("name", nameField.getText());
                    updateData.put("email", emailField.getText());
                    String newPassword = new String(passwordField.getPassword());
                    if (!newPassword.isEmpty()) {
                        updateData.put("password", newPassword);
                    }

                    System.out.println("PATCH kérés küldése: " + updateData.toString());
                    String response = makeApiCall("http://localhost:3000/users/" + userId, "PATCH", updateData.toString());

                    SwingUtilities.invokeLater(() -> {
                        try {
                            org.json.JSONObject jsonResponse = new org.json.JSONObject(response);
                            JOptionPane.showMessageDialog(this, "Adatok sikeresen frissítve!", "Siker", JOptionPane.INFORMATION_MESSAGE);
                            showProfile();
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(this, "Hiba történt a válasz feldolgozása során!", "Hiba", JOptionPane.ERROR_MESSAGE);
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "Hiba történt az adatok frissítése során!", "Hiba", JOptionPane.ERROR_MESSAGE);
                    });
                }
            }).start();
        });

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private String extractUserIdFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new RuntimeException("Az accessToken nem megfelelő formátumú.");
            }
            String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
            org.json.JSONObject jsonPayload = new org.json.JSONObject(payload);

            return String.valueOf(jsonPayload.get("sub"));
        } catch (Exception e) {
            throw new RuntimeException("Hiba történt a token feldolgozása során: " + e.getMessage(), e);
        }
    }

    private void showSearch() {
        contentPanel.removeAll();
        JLabel titleLabel = new JLabel("Felhasználók keresése");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BorderLayout());
        JTextField searchField = new JTextField();
        JButton searchButton = new JButton("Keresés");
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        JTable resultsTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(resultsTable);

        searchButton.addActionListener(e -> {
            try {
                String query = java.net.URLEncoder.encode(searchField.getText(), "UTF-8");
                System.out.println("showSearch: Lekérdezés = " + query);
                String response = makeApiCall("http://localhost:3000/users?search=" + query, "GET", null);
                System.out.println("showSearch: API válasz = " + response);

                org.json.JSONArray jsonArray = new org.json.JSONArray(response);
                String[][] data = new String[jsonArray.length()][4];
                String[] columns = {"ID", "Név", "E-mail", "Szerepkör"};

                for (int i = 0; i < jsonArray.length(); i++) {
                    org.json.JSONObject user = jsonArray.getJSONObject(i);
                    data[i][0] = String.valueOf(user.getInt("id"));
                    data[i][1] = user.has("ADMIN") ? user.getJSONObject("ADMIN").getString("name") :
                            user.has("TEACHER") ? user.getJSONObject("TEACHER").getString("name") :
                                    user.has("STUDENT") ? user.getJSONObject("STUDENT").getString("name") : "N/A";
                    data[i][2] = user.getString("email");
                    data[i][3] = user.getString("role");
                }

                resultsTable.setModel(new DefaultTableModel(data, columns));
            } catch (Exception ex) {
                System.err.println("showSearch: Hiba történt a keresés során: " + ex.getMessage());
                JOptionPane.showMessageDialog(this, "Hiba történt a keresés során!", "Hiba", JOptionPane.ERROR_MESSAGE);
            }
        });

        contentPanel.add(searchPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showSettings() {
        contentPanel.removeAll();
        JCheckBox darkModeToggle = new JCheckBox("Sötét mód");
        darkModeToggle.setFont(new Font("Arial", Font.BOLD, 16));
        darkModeToggle.addActionListener(e -> toggleDarkMode(darkModeToggle.isSelected()));
        contentPanel.add(darkModeToggle, BorderLayout.NORTH);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showEvents() {
        contentPanel.removeAll();
        JLabel eventsLabel = new JLabel("Események betöltése...");
        eventsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        eventsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(eventsLabel, BorderLayout.NORTH);
        JTable eventsTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(eventsTable);
        new Thread(() -> {
            try {
                String response = makeApiCall("http://localhost:3000/events", "GET", null);
                String[][] data = {};
                String[] columns = {"ID", "Név", "Dátum", "Helyszín"};
                SwingUtilities.invokeLater(() -> eventsTable.setModel(new DefaultTableModel(data, columns)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void toggleDarkMode(boolean enable) {
        if (enable) {
            UIManager.put("control", new Color(50, 50, 50));
            UIManager.put("info", new Color(50, 50, 50));
            UIManager.put("nimbusBase", new Color(18, 30, 49));
            UIManager.put("nimbusAlertYellow", new Color(248, 187, 0));
            UIManager.put("nimbusDisabledText", new Color(128, 128, 128));
            UIManager.put("nimbusFocus", new Color(115, 164, 209));
            UIManager.put("nimbusGreen", new Color(176, 179, 50));
            UIManager.put("nimbusInfoBlue", new Color(66, 139, 221));
            UIManager.put("nimbusLightBackground", new Color(18, 30, 49));
            UIManager.put("nimbusOrange", new Color(191, 98, 4));
            UIManager.put("nimbusRed", new Color(169, 46, 34));
            UIManager.put("nimbusSelectedText", new Color(255, 255, 255));
            UIManager.put("nimbusSelectionBackground", new Color(104, 93, 156));
            UIManager.put("text", new Color(230, 230, 230));
        } else {
            UIManager.put("control", null);
            UIManager.put("info", null);
            UIManager.put("nimbusBase", null);
            UIManager.put("nimbusAlertYellow", null);
            UIManager.put("nimbusDisabledText", null);
            UIManager.put("nimbusFocus", null);
            UIManager.put("nimbusGreen", null);
            UIManager.put("nimbusInfoBlue", null);
            UIManager.put("nimbusLightBackground", null);
            UIManager.put("nimbusOrange", null);
            UIManager.put("nimbusRed", null);
            UIManager.put("nimbusSelectedText", null);
            UIManager.put("nimbusSelectionBackground", null);
            UIManager.put("text", null);
        }
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void logout() {
        dispose();
        JOptionPane.showMessageDialog(this, "Sikeres kijelentkezés!", "Kijelentkezés", JOptionPane.INFORMATION_MESSAGE);
        new org.redible.checkpoint.checkpoint.ui.SignIn().setVisible(true);
    }

    private String makeApiCall(String apiUrl, String method, String payload) throws Exception {
        System.out.println("makeApiCall (OkHttp): apiUrl = " + apiUrl + ", method = " + method + ", payload = " + payload);

        if (accessToken == null || accessToken.isEmpty()) {
            System.err.println("makeApiCall: Az accessToken üres vagy null.");
            throw new RuntimeException("Az accessToken üres vagy null.");
        }

        RequestBody body = payload != null ? RequestBody.create(
                payload, MediaType.get("application/json; charset=utf-8")) : null;

        Request.Builder requestBuilder = new Request.Builder()
                .url(apiUrl)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("Content-Type", "application/json");

        switch (method.toUpperCase()) {
            case "GET":
                requestBuilder.get();
                break;
            case "POST":
                requestBuilder.post(body);
                break;
            case "PUT":
                requestBuilder.put(body);
                break;
            case "PATCH":
                requestBuilder.patch(body);
                break;
            case "DELETE":
                requestBuilder.delete(body);
                break;
            default:
                throw new IllegalArgumentException("Unsupported method: " + method);
        }

        Request request = requestBuilder.build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response + "\n" + response.body().string());
            }

            return response.body().string();
        }
    }
}