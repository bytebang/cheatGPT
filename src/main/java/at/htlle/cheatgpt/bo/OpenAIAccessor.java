package at.htlle.cheatgpt.bo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class OpenAIAccessor implements Savant {

    private final String apiKey; // OpenAI API Schlüssel
    private final URL endpoint;

    public OpenAIAccessor(String apiKey, String apiURL) {
        this.apiKey = apiKey;
        try {
            this.endpoint = new URL(apiURL);
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Erstellen der URL", e);
        }
    }

    @Override
    public Answer ask(String question) {
        try {
            HttpURLConnection conn = (HttpURLConnection) endpoint.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey); // Hinzufügen des API-Schlüssels

            JSONObject requestBody = new JSONObject();
            requestBody.put("prompt", question);
            requestBody.put("max_tokens", 50); // Beispiel für eine maximale Tokenanzahl

            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.toString().getBytes());
                os.flush();
            }

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            StringBuilder responseText = new StringBuilder();
            String output;
            JSONObject jsonObject;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                while ((output = br.readLine()) != null) {
                    jsonObject = new JSONObject(output);
                    responseText.append(jsonObject.getJSONArray("choices").getJSONObject(0).getString("text")); // Anpassung an die OpenAI Antwortstruktur
                }
            }

            conn.disconnect();
            return new Answer(responseText.toString(), 0); // Hier können Sie weitere Daten aus der Antwort extrahieren und in Ihr Answer-Objekt einfügen

        } catch (Exception e) {
            return new Answer("Internal Error: " + e.getMessage(), 0);
        }
    }
}
