package org.redible.checkpoint.checkpoint.util;

import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Base64;

public class ApiUtil {
    private static final OkHttpClient httpClient = new OkHttpClient();

    public static String makeApiCall(String apiUrl, String method, String payload, String accessToken) throws Exception {
        if (accessToken == null || accessToken.isEmpty()) {
            throw new RuntimeException("Az accessToken üres vagy null.");
        }

        RequestBody body = payload != null ? RequestBody.create(
                payload, MediaType.get("application/json; charset=utf-8")) : null;

        Request.Builder requestBuilder = new Request.Builder()
                .url(apiUrl)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("Content-Type", "application/json");

        switch (method.toUpperCase()) {
            case "GET": requestBuilder.get(); break;
            case "POST": requestBuilder.post(body); break;
            case "PUT": requestBuilder.put(body); break;
            case "PATCH": requestBuilder.patch(body); break;
            case "DELETE": requestBuilder.delete(body); break;
            default: throw new IllegalArgumentException("Unsupported method: " + method);
        }

        Request request = requestBuilder.build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response + "\n" + response.body().string());
            }
            return response.body().string();
        }
    }

    public static String extractUserIdFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new RuntimeException("Az accessToken nem megfelelő formátumú.");
            }
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            JSONObject jsonPayload = new JSONObject(payload);
            return String.valueOf(jsonPayload.get("sub"));
        } catch (Exception e) {
            throw new RuntimeException("Hiba történt a token feldolgozása során: " + e.getMessage(), e);
        }
    }
}
