package org.redible.checkpoint.checkpoint.util;

import org.json.JSONObject;

import javax.swing.*;
import java.time.LocalDateTime;

public class CardScannerUtil {
    public static void processCardScan(String cardNumber, String accessToken, DefaultListModel<String> eventLogModel) {
        new Thread(() -> {
            try {
                String userId = ApiUtil.extractUserIdFromToken(accessToken);

                JSONObject payload = new JSONObject();
                payload.put("studentCardNumber", cardNumber);
                payload.put("recordedByPortaId", Integer.parseInt(userId));

                String response = ApiUtil.makeApiCall(
                        "http://localhost:3000/events/register",
                        "POST",
                        payload.toString(),
                        accessToken
                );

                JSONObject json = new JSONObject(response);
                String status = json.optString("status", "ismeretlen");
                String message = json.optString("message", "Nincs visszajelzés");

                String timestamp = LocalDateTime.now().toString().replace("T", " ").substring(0, 19);
                String logEntry = String.format("[%s] %s → %s", timestamp, cardNumber, status.replace("_", " ").toUpperCase());

                SwingUtilities.invokeLater(() -> {
                    eventLogModel.addElement(logEntry);
                    SmokingStatusUtil.fetchSmokingStatuses(eventLogModel, accessToken);
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null, "Hiba történt a rögzítés során: " + ex.getMessage(), "Hiba", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }
}
