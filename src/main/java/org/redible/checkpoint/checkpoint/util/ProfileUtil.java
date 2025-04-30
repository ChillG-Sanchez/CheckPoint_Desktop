package org.redible.checkpoint.checkpoint.util;

import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ProfileUtil {

    public static JPanel buildProfilePanel(String accessToken) {
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Profil adatok");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        profilePanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

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

        profilePanel.add(formPanel, BorderLayout.CENTER);
        profilePanel.add(editButton, BorderLayout.SOUTH);

        loadProfileData(accessToken, nameField, emailField, roleField);
        return profilePanel;
    }

    private static void loadProfileData(String accessToken, JTextField nameField, JTextField emailField, JTextField roleField) {
        new Thread(() -> {
            try {
                String userId = ApiUtil.extractUserIdFromToken(accessToken);
                String response = ApiUtil.makeApiCall("http://localhost:3000/users/" + userId, "GET", null, accessToken);

                SwingUtilities.invokeLater(() -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        nameField.setText(jsonResponse.getString("name"));
                        emailField.setText(jsonResponse.getString("email"));
                        roleField.setText(jsonResponse.getString("role"));
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, "Hiba történt az adatok feldolgozása során!", "Hiba", JOptionPane.ERROR_MESSAGE);
                    }
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null, "Hiba történt az adatok lekérése során!", "Hiba", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    public static void showEditProfilePanel(String currentName, String currentEmail, String accessToken, JPanel contentPanel) {
        contentPanel.removeAll();
        JLabel titleLabel = new JLabel("Profil szerkesztése");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

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
                    String userId = ApiUtil.extractUserIdFromToken(accessToken);
                    JSONObject updateData = new JSONObject();
                    updateData.put("name", nameField.getText());
                    updateData.put("email", emailField.getText());
                    String newPassword = new String(passwordField.getPassword());
                    if (!newPassword.isEmpty()) {
                        updateData.put("password", newPassword);
                    }

                    String response = ApiUtil.makeApiCall("http://localhost:3000/users/" + userId, "PATCH", updateData.toString(), accessToken);

                    SwingUtilities.invokeLater(() -> {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JOptionPane.showMessageDialog(null, "Adatok sikeresen frissítve!", "Siker", JOptionPane.INFORMATION_MESSAGE);
                            loadProfileData(accessToken, nameField, emailField, new JTextField());
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "Hiba történt a válasz feldolgozása során!", "Hiba", JOptionPane.ERROR_MESSAGE);
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(null, "Hiba történt az adatok frissítése során!", "Hiba", JOptionPane.ERROR_MESSAGE);
                    });
                }
            }).start();
        });

        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
