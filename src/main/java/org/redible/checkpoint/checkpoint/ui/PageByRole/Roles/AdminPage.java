package org.redible.checkpoint.checkpoint.ui.PageByRole.Roles;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;


import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import okhttp3.*;
import org.redible.checkpoint.checkpoint.util.ApiUtil;

public class AdminPage extends JFrame {

    private final JPanel navigationPanel;
    private final JPanel contentPanel;
    private final String accessToken;
    private final OkHttpClient httpClient = new OkHttpClient();
    private boolean isDarkModeEnabled = false;
    private JTable eventsTable;

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
        addNavButton("Felhasználók", "C:/Users/ChillG/Desktop/Vizsgamunka/CheckPoint_Desktop/src/main/resources/icons/user-pen-alt-svgrepo-com.svg" , e -> showUsersManagement());
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
        return ApiUtil.extractUserIdFromToken(token);
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
        searchPanel.add(searchField, BorderLayout.CENTER);

        JTable resultsTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(resultsTable);

        List<org.json.JSONObject> allUsers = new ArrayList<>();
        SwingUtilities.invokeLater(() -> {
            try {
                String response = makeApiCall("http://localhost:3000/users", "GET", null);
                org.json.JSONArray jsonArray = new org.json.JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    allUsers.add(jsonArray.getJSONObject(i));
                }
                updateTable(allUsers, resultsTable, "");
            } catch (Exception ex) {
                System.err.println("showSearch: Hiba történt az alapértelmezett lista betöltése során: " + ex.getMessage());
            }
        });

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            private void filterTable() {
                String query = searchField.getText().toLowerCase();
                updateTable(allUsers, resultsTable, query);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                filterTable();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterTable();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterTable();
            }
        });

        contentPanel.add(searchPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void updateTable(List<org.json.JSONObject> users, JTable table, String query) {
        String[][] data = users.stream()
                .filter(user -> {
                    String name = getNameFromUser(user).toLowerCase();
                    String email = user.optString("email", "").toLowerCase();
                    String role = user.optString("role", "").toLowerCase();
                    return name.startsWith(query) || email.startsWith(query) || role.startsWith(query);
                })
                .map(user -> new String[]{
                        String.valueOf(user.getInt("id")),
                        getNameFromUser(user),
                        user.optString("email", "N/A"),
                        user.optString("role", "N/A")
                })
                .toArray(String[][]::new);

        String[] columns = {"ID", "Név", "E-mail", "Szerepkör"};
        table.setModel(new DefaultTableModel(data, columns));
    }


    private String getNameFromUser(org.json.JSONObject user) {
        if (user.optJSONObject("admin") != null) {
            return user.optJSONObject("admin").optString("name", "N/A");
        } else if (user.optJSONObject("teacher") != null) {
            return user.optJSONObject("teacher").optString("name", "N/A");
        } else if (user.optJSONObject("student") != null) {
            return user.optJSONObject("student").optString("name", "N/A");
        } else if (user.optJSONObject("porta") != null) {
            return user.optJSONObject("porta").optString("name", "N/A");
        }
        return "N/A";
    }

    private void showUsersManagement() {
        contentPanel.removeAll();
        JLabel titleLabel = new JLabel("Felhasználók kezelése");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel usersPanel = new JPanel();
        usersPanel.setLayout(new BorderLayout());

        JTable usersTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(usersTable);

        JButton addButton = new JButton("Hozzáadás");
        JButton deleteButton = new JButton("Törlés");
        JButton editButton = new JButton("Módosítás");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);

        usersPanel.add(scrollPane, BorderLayout.CENTER);
        usersPanel.add(buttonPanel, BorderLayout.SOUTH);

        contentPanel.add(usersPanel, BorderLayout.CENTER);

        addButton.addActionListener(e -> showAddUserDialog(usersTable));
        deleteButton.addActionListener(e -> deleteUser(usersTable));
        editButton.addActionListener(e -> showEditUserDialog(usersTable));

        loadUsers(usersTable);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void loadUsers(JTable usersTable) {
        new Thread(() -> {
            try {
                String response = makeApiCall("http://localhost:3000/users", "GET", null);
                org.json.JSONArray jsonArray = new org.json.JSONArray(response);

                String[][] data = new String[jsonArray.length()][3];
                String[] columns = {"ID", "Email", "Szerepkör"};

                for (int i = 0; i < jsonArray.length(); i++) {
                    org.json.JSONObject user = jsonArray.getJSONObject(i);
                    data[i][0] = String.valueOf(user.getInt("id"));
                    data[i][1] = user.getString("email");
                    data[i][2] = user.getString("role");
                }

                SwingUtilities.invokeLater(() -> usersTable.setModel(new DefaultTableModel(data, columns)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void showAddUserDialog(JTable usersTable) {
        JDialog dialog = new JDialog(this, "Új felhasználó hozzáadása", true);
        dialog.setSize(400, 500);
        dialog.setLayout(new GridLayout(0, 2, 10, 10));

        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"ADMIN", "TEACHER", "STUDENT", "PORTA"});
        JTextField classField = new JTextField();
        JTextField birthDateField = new JTextField();
        JTextField studentCardNumberField = new JTextField();

        dialog.add(new JLabel("Név:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Email:"));
        dialog.add(emailField);
        dialog.add(new JLabel("Jelszó:"));
        dialog.add(passwordField);
        dialog.add(new JLabel("Szerepkör:"));
        dialog.add(roleComboBox);

        JLabel classLabel = new JLabel("Osztály (Csak diákok esetén):");
        dialog.add(classLabel);
        dialog.add(classField);

        JLabel birthDateLabel = new JLabel("Születési dátum (YYYY-MM-DD):");
        dialog.add(birthDateLabel);
        dialog.add(birthDateField);

        JLabel studentCardNumberLabel = new JLabel("Diákigazolvány szám (Csak diákok esetén):");
        dialog.add(studentCardNumberLabel);
        dialog.add(studentCardNumberField);

        classLabel.setVisible(false);
        classField.setVisible(false);
        birthDateLabel.setVisible(false);
        birthDateField.setVisible(false);
        studentCardNumberLabel.setVisible(false);
        studentCardNumberField.setVisible(false);

        roleComboBox.addActionListener(e -> {
            boolean isStudent = roleComboBox.getSelectedItem().toString().equals("STUDENT");
            classLabel.setVisible(isStudent);
            classField.setVisible(isStudent);
            birthDateLabel.setVisible(isStudent);
            birthDateField.setVisible(isStudent);
            studentCardNumberLabel.setVisible(isStudent);
            studentCardNumberField.setVisible(isStudent);
        });

        JButton saveButton = new JButton("Mentés");
        dialog.add(saveButton);

        saveButton.addActionListener(e -> {
            try {
                org.json.JSONObject payload = new org.json.JSONObject();
                payload.put("name", nameField.getText());
                payload.put("email", emailField.getText());
                payload.put("password", new String(passwordField.getPassword()));
                payload.put("role", roleComboBox.getSelectedItem().toString());

                if (roleComboBox.getSelectedItem().toString().equals("STUDENT")) {
                    payload.put("class", classField.getText());

                    String birthDateText = birthDateField.getText();
                    if (!birthDateText.isEmpty()) {
                        LocalDate date = LocalDate.parse(birthDateText); // YYYY-MM-DD
                        Instant instant = date.atStartOfDay().toInstant(ZoneOffset.UTC);
                        String isoString = instant.toString();
                        payload.put("birthDate", isoString);
                    }

                    String studentCardNumber = studentCardNumberField.getText();
                    if (!studentCardNumber.isEmpty()) {
                        payload.put("studentCardNumber", studentCardNumber);
                    }
                }

                makeApiCall("http://localhost:3000/users", "POST", payload.toString());
                dialog.dispose();
                loadUsers(usersTable);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Hiba: " + ex.getMessage(), "Hiba", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }

    private void deleteUser(JTable usersTable) {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Válasszon ki egy felhasználót a törléshez!", "Figyelmeztetés", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = Integer.parseInt(usersTable.getValueAt(selectedRow, 0).toString());
        int confirm = JOptionPane.showConfirmDialog(this, "Biztosan törölni szeretné a felhasználót?", "Megerősítés", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            new Thread(() -> {
                try {
                    makeApiCall("http://localhost:3000/users/" + userId, "DELETE", null);
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "A felhasználó sikeresen törölve lett.", "Siker", JOptionPane.INFORMATION_MESSAGE);
                        loadUsers(usersTable);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "Hiba történt a felhasználó törlése során: " + e.getMessage(), "Hiba", JOptionPane.ERROR_MESSAGE);
                    });
                }
            }).start();
        }
    }

    private void showEditUserDialog(JTable usersTable) {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Válasszon ki egy felhasználót a módosításhoz!", "Figyelmeztetés", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = Integer.parseInt(usersTable.getValueAt(selectedRow, 0).toString());
        String currentEmail = usersTable.getValueAt(selectedRow, 1).toString();
        String currentRole = usersTable.getValueAt(selectedRow, 2).toString();

        JDialog dialog = new JDialog(this, "Felhasználó módosítása", true);
        dialog.setSize(400, 500);
        dialog.setLayout(new GridLayout(0, 2, 10, 10));

        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField(currentEmail);
        JPasswordField passwordField = new JPasswordField();
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"ADMIN", "TEACHER", "STUDENT", "PORTA"});
        roleComboBox.setSelectedItem(currentRole);
        JTextField classField = new JTextField();
        JTextField birthDateField = new JTextField();
        JTextField studentCardNumberField = new JTextField();

        dialog.add(new JLabel("Név (Csak szerepkör-specifikus):"));
        dialog.add(nameField);
        dialog.add(new JLabel("Email:"));
        dialog.add(emailField);
        dialog.add(new JLabel("Jelszó (Hagyja üresen, ha nem változik):"));
        dialog.add(passwordField);
        dialog.add(new JLabel("Szerepkör:"));
        dialog.add(roleComboBox);

        JLabel classLabel = new JLabel("Osztály (Csak diákok esetén):");
        dialog.add(classLabel);
        dialog.add(classField);

        JLabel birthDateLabel = new JLabel("Születési dátum (YYYY-MM-DD):");
        dialog.add(birthDateLabel);
        dialog.add(birthDateField);

        JLabel studentCardNumberLabel = new JLabel("Diákigazolvány szám (Csak diákok esetén):");
        dialog.add(studentCardNumberLabel);
        dialog.add(studentCardNumberField);

        classLabel.setVisible(false);
        classField.setVisible(false);
        birthDateLabel.setVisible(false);
        birthDateField.setVisible(false);
        studentCardNumberLabel.setVisible(false);
        studentCardNumberField.setVisible(false);

        roleComboBox.addActionListener(e -> {
            boolean isStudent = roleComboBox.getSelectedItem().toString().equals("STUDENT");
            classLabel.setVisible(isStudent);
            classField.setVisible(isStudent);
            birthDateLabel.setVisible(isStudent);
            birthDateField.setVisible(isStudent);
            studentCardNumberLabel.setVisible(isStudent);
            studentCardNumberField.setVisible(isStudent);
        });

        JButton saveButton = new JButton("Mentés");
        dialog.add(saveButton);

        saveButton.addActionListener(e -> {
            try {
                org.json.JSONObject payload = new org.json.JSONObject();
                payload.put("email", emailField.getText());
                payload.put("role", roleComboBox.getSelectedItem().toString());

                if (!nameField.getText().isEmpty()) {
                    payload.put("name", nameField.getText());
                }

                if (passwordField.getPassword().length > 0) {
                    payload.put("password", new String(passwordField.getPassword()));
                }

                if (roleComboBox.getSelectedItem().toString().equals("STUDENT")) {
                    payload.put("class", classField.getText());

                    String birthDateText = birthDateField.getText();
                    if (!birthDateText.isEmpty()) {
                        LocalDate date = LocalDate.parse(birthDateText); // YYYY-MM-DD
                        Instant instant = date.atStartOfDay().toInstant(ZoneOffset.UTC);
                        String isoString = instant.toString(); // ISO 8601: 2005-04-29T00:00:00Z
                        payload.put("birthDate", isoString);
                    }

                    String studentCardNumber = studentCardNumberField.getText();
                    if (!studentCardNumber.isEmpty()) {
                        payload.put("studentCardNumber", studentCardNumber);
                    }
                }

                makeApiCall("http://localhost:3000/users/" + userId, "PATCH", payload.toString());
                dialog.dispose();
                loadUsers(usersTable);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Hiba: " + ex.getMessage(), "Hiba", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }

    private void showSettings() {
        contentPanel.removeAll();

        JCheckBox darkModeToggle = new JCheckBox("Sötét mód");
        darkModeToggle.setFont(new Font("Arial", Font.BOLD, 16));
        darkModeToggle.setSelected(isDarkModeEnabled);
        darkModeToggle.addActionListener(e -> toggleDarkMode(darkModeToggle.isSelected()));

        contentPanel.add(darkModeToggle, BorderLayout.NORTH);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showEvents() {
        contentPanel.removeAll();
        JLabel eventsLabel = new JLabel("Események");
        eventsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        eventsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(eventsLabel, BorderLayout.NORTH);

        eventsTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(eventsTable);

        JPanel filterPanel = new JPanel();
        JButton allEventsButton = new JButton("Összes esemény");
        JButton nonSmokingEventsButton = new JButton("Nem dohányzó események");
        JButton smokingEventsButton = new JButton("Dohányzó események");

        filterPanel.add(allEventsButton);
        filterPanel.add(nonSmokingEventsButton);
        filterPanel.add(smokingEventsButton);

        contentPanel.add(filterPanel, BorderLayout.SOUTH);

        allEventsButton.addActionListener(e -> loadEvents(null));
        nonSmokingEventsButton.addActionListener(e -> loadEvents("non-smoking"));
        smokingEventsButton.addActionListener(e -> loadEvents("smoking"));

        loadEvents(null);

        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void loadEvents(String filter) {
        new Thread(() -> {
            try {
                String apiUrl = "http://localhost:3000/events";
                if (filter != null) {
                    apiUrl += "?filter=" + filter;
                }

                String response = makeApiCall(apiUrl, "GET", null);
                org.json.JSONArray jsonArray = new org.json.JSONArray(response);

                String[][] data = new String[jsonArray.length()][4];
                String[] columns = {"ID", "Név", "Dátum", "Cselekmény"};

                for (int i = 0; i < jsonArray.length(); i++) {
                    org.json.JSONObject event = jsonArray.getJSONObject(i);
                    data[i][0] = String.valueOf(event.getInt("id"));
                    data[i][1] = event.optString("name", "Ismeretlen");
                    data[i][2] = event.getString("timestamp").replace("T", " ").substring(0, 16);
                    data[i][3] = event.getString("action");
                }

                SwingUtilities.invokeLater(() -> eventsTable.setModel(new DefaultTableModel(data, columns)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void toggleDarkMode(boolean enable) {
        isDarkModeEnabled = enable;

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }

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
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Hiba történt a kinézet frissítése során!", "Hiba", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void logout() {
        dispose();
        JOptionPane.showMessageDialog(this, "Sikeres kijelentkezés!", "Kijelentkezés", JOptionPane.INFORMATION_MESSAGE);
        new org.redible.checkpoint.checkpoint.ui.SignIn().setVisible(true);
    }

    private String makeApiCall(String apiUrl, String method, String payload) throws Exception {
        return ApiUtil.makeApiCall(apiUrl, method, payload, accessToken);
    }
}