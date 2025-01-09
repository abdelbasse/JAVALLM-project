package com.example.rag;

import com.example.Prompt;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

// Class to classify a prompt using a modal API
public class ModalClassifier {
    private String apiUrl;

    // Constructor to initialize the API URL
    public ModalClassifier(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    // Method to classify a prompt and return the classification results
    public Map<String, Object> classifyPrompt(Prompt prompt) {
        Map<String, Object> classificationResults = new HashMap<>();

        try {
            // Create a JSON object with the prompt content
            JSONObject payload = new JSONObject();
            payload.put("prompt", prompt.getContent());

            // Open a connection to the Flask API
            URL urlObj = new URL(apiUrl + "/text-classifier/prompt");
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();

            // Set the request method and headers
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Send the POST request
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = payload.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Read the response from the server
            int statusCode = connection.getResponseCode();
            StringBuilder response = new StringBuilder();

            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), "utf-8"))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }

            // Check the response code and parse the JSON response
            if (statusCode == HttpURLConnection.HTTP_OK) {
                JSONObject responseJson = new JSONObject(response.toString());
                // Populate the results map with data from the response JSON
                classificationResults.put("Predicted Category", responseJson.getString("Predicted Category"));
                classificationResults.put("Probabilities", responseJson.getJSONObject("Probabilities").toMap());
            } else {
                System.out.println("Error: " + statusCode + "\n" + response.toString());
            }

        } catch (IOException e) {
            System.out.println("Request failed: " + e.getMessage());
        }

        return classificationResults;
    }

    // Method to test connectivity with /text-classifier
    public boolean testConnectivity() {
        try {
            // Open a connection to the Flask API
            URL urlObj = new URL(apiUrl + "/text-classifier");
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();

            // Set the request method to GET
            connection.setRequestMethod("GET");

            // Get the response code
            int statusCode = connection.getResponseCode();
            StringBuilder response = new StringBuilder();

            // Read the response from the server
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), "utf-8"))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }

            // Print the response code and response body
            System.out.println("Text Classifier Connectivity Response Code: " + statusCode);
            System.out.println("Text Classifier Connectivity Response Body: " + response.toString());
            return true;
        } catch (IOException e) {
            System.out.println("Text Classifier connectivity test failed: " + e.getMessage());
            return false;
        }
    }
}