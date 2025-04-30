package org.redible.checkpoint.checkpoint.ui;

import org.json.JSONArray;
import org.json.JSONObject;
import org.redible.checkpoint.checkpoint.util.ApiUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import static org.redible.checkpoint.checkpoint.util.ApiUtil.makeApiCall;

public class UserManagementUI {

    public static JPanel createUserManagementPanel(JTable usersTable, String accessToken, JFrame parentFrame) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Felhasználók kezelése");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(usersTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Hozzáadás");
        JButton deleteButton = new JButton("Törlés");
        JButton editButton = new JButton("Módosítás");

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> showAddUserDialog(usersTable, accessToken, parentFrame));
        deleteButton.addActionListener(e -> deleteUser(usersTable, accessToken, parentFrame));
        editButton.addActionListener(e -> showEditUserDialog(usersTable, accessToken, parentFrame));

        loadUsers(usersTable, accessToken);

        return panel;
    }


    private static void loadUsers(JTable usersTable, String accessToken) {
        new Thread(() -> {
            try {
                String response = makeApiCall("http://localhost:3000/users", "GET", null, accessToken);
                JSONArray jsonArray = new JSONArray(response);

                String[][] data = new String[jsonArray.length()][3];
                String[] columns = {"ID", "Email", "Szerepkör"};

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject user = jsonArray.getJSONObject(i);
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

    private static void showAddUserDialog(JTable usersTable, String accessToken, JFrame parentFrame) {
        JDialog dialog = new JDialog(parentFrame, "Új felhasználó hozzáadása", true);
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
                        LocalDate date = LocalDate.parse(birthDateText);
                        Instant instant = date.atStartOfDay().toInstant(ZoneOffset.UTC);
                        String isoString = instant.toString();
                        payload.put("birthDate", isoString);
                    }

                    String studentCardNumber = studentCardNumberField.getText();
                    if (!studentCardNumber.isEmpty()) {
                        payload.put("studentCardNumber", studentCardNumber);
                    }
                }

                makeApiCall("http://localhost:3000/users", "POST", payload.toString(), accessToken);
                dialog.dispose();
                loadUsers(usersTable, accessToken);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Hiba: " + ex.getMessage(), "Hiba", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }


    private static void deleteUser(JTable usersTable, String accessToken, JFrame parentFrame) {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(parentFrame, "Válasszon ki egy felhasználót a törléshez!", "Figyelmeztetés", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = Integer.parseInt(usersTable.getValueAt(selectedRow, 0).toString());
        int confirm = JOptionPane.showConfirmDialog(parentFrame, "Biztosan törölni szeretné a felhasználót?", "Megerősítés", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            new Thread(() -> {
                try {
                    makeApiCall("http://localhost:3000/users/" + userId, "DELETE", null, accessToken);
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(parentFrame, "A felhasználó sikeresen törölve lett.", "Siker", JOptionPane.INFORMATION_MESSAGE);
                        loadUsers(usersTable, accessToken);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(parentFrame, "Hiba történt a felhasználó törlése során: " + e.getMessage(), "Hiba", JOptionPane.ERROR_MESSAGE);
                    });
                }
            }).start();
        }
    }


    private static void showEditUserDialog(JTable usersTable, String accessToken, JFrame parentFrame) {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(parentFrame, "Válasszon ki egy felhasználót a módosításhoz!", "Figyelmeztetés", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = Integer.parseInt(usersTable.getValueAt(selectedRow, 0).toString());
        String currentEmail = usersTable.getValueAt(selectedRow, 1).toString();
        String currentRole = usersTable.getValueAt(selectedRow, 2).toString();

        JDialog dialog = new JDialog(parentFrame, "Felhasználó módosítása", true);
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
                        LocalDate date = LocalDate.parse(birthDateText);
                        Instant instant = date.atStartOfDay().toInstant(ZoneOffset.UTC);
                        String isoString = instant.toString();
                        payload.put("birthDate", isoString);
                    }

                    String studentCardNumber = studentCardNumberField.getText();
                    if (!studentCardNumber.isEmpty()) {
                        payload.put("studentCardNumber", studentCardNumber);
                    }
                }

                makeApiCall("http://localhost:3000/users/" + userId, "PATCH", payload.toString(), accessToken);
                dialog.dispose();
                loadUsers(usersTable, accessToken);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Hiba: " + ex.getMessage(), "Hiba", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }

}
