package org.redible.checkpoint.checkpoint.util;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EventsUtil {
    public static JPanel buildEventsPanel(String accessToken) {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Események", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);

        JTable table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        new Thread(() -> {
            try {
                String response = ApiUtil.makeApiCall("http://localhost:3000/events", "GET", null, accessToken);
                JSONArray array = new JSONArray(response);
                List<String[]> data = new ArrayList<>();

                for (int i = 0; i < array.length(); i++) {
                    JSONObject event = array.getJSONObject(i);
                    String id = String.valueOf(event.getInt("id"));
                    String name = event.optString("name", "Ismeretlen");
                    String timestamp = event.getString("timestamp").replace("T", " ").substring(0, 16);
                    String action = event.getString("action");
                    data.add(new String[]{id, name, timestamp, action});
                }

                String[][] tableData = data.toArray(new String[0][]);
                String[] columns = {"ID", "Név", "Dátum", "Cselekmény"};

                SwingUtilities.invokeLater(() -> table.setModel(new DefaultTableModel(tableData, columns)));
            } catch (Exception e) {
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(null, "Hiba az események betöltésekor: " + e.getMessage(), "Hiba", JOptionPane.ERROR_MESSAGE)
                );
            }
        }).start();

        return panel;
    }
}
