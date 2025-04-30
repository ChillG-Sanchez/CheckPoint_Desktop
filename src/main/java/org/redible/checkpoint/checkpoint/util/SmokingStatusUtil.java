package org.redible.checkpoint.checkpoint.util;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.time.LocalDateTime;

public class SmokingStatusUtil {

    public static void fetchSmokingStatuses(DefaultListModel<String> eventLogModel, String accessToken) {
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
}