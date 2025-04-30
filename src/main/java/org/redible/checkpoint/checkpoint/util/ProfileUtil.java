package org.redible.checkpoint.checkpoint.util;

import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;

public class ProfileUtil {
    public static JPanel buildProfilePanel(String accessToken) {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Profil adatok");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
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

        panel.add(formPanel, BorderLayout.CENTER);

        new Thread(() -> {
            try {
                String userId = ApiUtil.extractUserIdFromToken(accessToken);
                String response = ApiUtil.makeApiCall("http://localhost:3000/users/" + userId, "GET", null, accessToken);

                JSONObject json = new JSONObject(response);
                SwingUtilities.invokeLater(() -> {
                    nameField.setText(json.optString("name"));
                    emailField.setText(json.optString("email"));
                    roleField.setText(json.optString("role"));
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null, "Hiba a profil betöltésekor: " + e.getMessage(), "Hiba", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();

        return panel;
    }
}
