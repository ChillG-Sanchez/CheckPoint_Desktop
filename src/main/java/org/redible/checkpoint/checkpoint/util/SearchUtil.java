package org.redible.checkpoint.checkpoint.util;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SearchUtil {

    public static void showSearch(JPanel contentPanel, String accessToken) {
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

        List<JSONObject> allUsers = new ArrayList<>();
        SwingUtilities.invokeLater(() -> {
            try {
                String response = ApiUtil.makeApiCall("http://localhost:3000/users", "GET", null, accessToken);
                JSONArray jsonArray = new JSONArray(response);
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

    private static void updateTable(List<JSONObject> users, JTable table, String query) {
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

    private static String getNameFromUser(JSONObject user) {
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
}
