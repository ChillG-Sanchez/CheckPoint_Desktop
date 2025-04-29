package org.redible.checkpoint.checkpoint.auth;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class AuthService {

    private String accessToken; // Az access token tárolása

    /**
     * Bejelentkezés a backendre, és az access token tárolása.
     *
     * @param email    A felhasználó email címe.
     * @param password A felhasználó jelszava.
     * @throws Exception Ha a bejelentkezés sikertelen.
     */
    public void authenticateUser(String email, String password) throws Exception {
        URL url = new URL("http://localhost:3000/auth/login");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Connection", "close");
        conn.setDoOutput(true);

        JSONObject json = new JSONObject();
        json.put("email", email);
        json.put("password", password);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = json.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        if (responseCode == 201) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                JSONObject responseObject = new JSONObject(response.toString());

                if (responseObject.has("access_token")) {
                    this.accessToken = responseObject.getString("access_token");
                    System.out.println("Sikeres bejelentkezés. Access Token: " + this.accessToken);
                } else {
                    throw new Exception("Access token not found in response");
                }
            }
        } else {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                StringBuilder errorResponse = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    errorResponse.append(responseLine.trim());
                }
                System.err.println("Error Response: " + errorResponse);
            }
            throw new Exception("Failed to authenticate user. HTTP Code: " + responseCode);
        }
    }

    /**
     * Az access token lekérdezése.
     *
     * @return Az access token.
     */
    public String getAccessToken() {
        return accessToken;
    }
}