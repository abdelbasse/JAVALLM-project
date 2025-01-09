package com.example.rag;

import java.io.*;
import java.math.BigDecimal;
import java.net.*;

import com.example.rag.datatype.*;
import org.json.*;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseVectoreClient {
    private String url;
    private String collectionName;

    // Constructor to initialize the URL and collection name
    public DatabaseVectoreClient(String url, String collectionName) {
        this.url = url;
        this.collectionName = collectionName;
    }

    // Method to send the POST request and get the response for any query
    public List<QueryResult> getQueryResults(String query) {
        List<QueryResult> queryResults = new ArrayList<>();

        try {
            // Create a JSON object to hold the data for the POST request
            JSONObject payload = new JSONObject();
            payload.put("collection_name", collectionName);
            payload.put("query", query);

            // Open a connection to the Flask API
            URL urlObj = new URL(this.url + "/query");
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

            // Check the response code and handle the response
            if (statusCode == HttpURLConnection.HTTP_OK) {
                // Successfully received response, now parse it
                JSONObject responseJson = new JSONObject(response.toString());
                JSONObject resultsData = responseJson.getJSONObject("results");
                // Extract the documents and metadatas data
                JSONArray documentsData = resultsData.optJSONArray("documents");
                JSONArray distancesData = resultsData.optJSONArray("distances");
                System.out.println("distant : " + distancesData.toString());

                JSONArray metadatasData = resultsData.optJSONArray("metadatas");

                // Check for JavaCodeSnippetsDB collection name
                if ("JavaCodeSnippetsDB".equals(collectionName)) {
                    if (documentsData != null && metadatasData != null) {
                        // Iterate over the documents and metadata
                        for (int i = 0; i < documentsData.length(); i++) {
                            // Access the nested array inside each element of documentsData
                            JSONArray documentArray = documentsData.getJSONArray(i);
                            // Access the corresponding metadata for the document
                            JSONArray metadataArray = metadatasData.getJSONArray(i);
                            JSONArray distanceArray = distancesData.getJSONArray(i);

                            
                            for (int j = 0; j < documentArray.length(); j++) {
                                String document = documentArray.getString(j);

                                BigDecimal distance = distanceArray.getBigDecimal(j);
                                float distanceValue = distance.floatValue();
                                // Parse metadata for this document
                                Map<String, String> parsedMetadata = parseMetadata(metadataArray.getJSONObject(j));

                                // Now you can process each document and metadata pair
                                QueryResultCodeSnippets codeSnippet = new QueryResultCodeSnippets(
                                        parsedMetadata.get("repo"),            // Placeholder link
                                        document,          // The document
                                        parsedMetadata     // Pass the parsed metadata map for this document
                                        ,distanceValue
                                );

                                // If needed, add the codeSnippet to a list or process it further
                                queryResults.add(codeSnippet);
                            }
                        }
                    } else {
                        System.out.println("Error: Missing 'documents' or 'metadatas' in the response.");
                    }
                } else if ("JavaCodeDebuggingDB".equals(collectionName)) {
                    if (documentsData != null && metadatasData != null) {
                        // Iterate over the documents and metadata
                        for (int i = 0; i < documentsData.length(); i++) {
                            // Access the nested array inside each element of documentsData
                            JSONArray documentArray = documentsData.getJSONArray(i);
                            // Access the corresponding metadata for the document
                            JSONArray metadataArray = metadatasData.getJSONArray(i);
                            JSONArray distanceArray = distancesData.getJSONArray(i);

                            for (int j = 0; j < documentArray.length(); j++) {
                                String document = documentArray.getString(j);

                                BigDecimal distance = distanceArray.getBigDecimal(j);
                                float distanceValue = distance.floatValue();
                                // Parse metadata for this document
                                Map<String, String> parsedMetadata = parseMetadata(metadataArray.getJSONObject(j));
                                
                                List<String> solutions = new ArrayList<>();

                                // Iterate through the solution keys and add non-empty solutions to the list
                                for (int i_solution = 1; i_solution <= 3; i_solution++) {
                                    String solutionKey = "solution" + i_solution;
                                    String solution = parsedMetadata.get(solutionKey);

                                    // Only add non-empty solutions
                                    if (!solution.isEmpty() && !solution.equalsIgnoreCase("N/A")) {
                                        solutions.add(solution);
                                    }
                                }

                               // Now you can process each document and metadata pair
                               QueryResultCodeProblems codeSnippet = new QueryResultCodeProblems(
                                       parsedMetadata.get("title"),  // Placeholder title
                                       parsedMetadata.get("description"),     // Placeholder description
                                       parsedMetadata.get("link"),
                                       solutions ,
                                       parsedMetadata    // Pass the parsed metadata map for this document
                                      ,distanceValue
                               );

                               // If needed, add the codeSnippet to a list or process it further
                               queryResults.add(codeSnippet);
                            }
                        }
                    } else {
                        System.out.println("Error: Missing 'documents' or 'metadatas' in the response.");
                    }
                }
            } else {
                // Handle error responses
                System.out.println("Error: " + statusCode + "\n" + response.toString());
            }

        } catch (IOException e) {
            System.out.println("Request failed: " + e.getMessage());
        }

        return queryResults;
    }

    // Helper method to parse metadata into a map
    private Map<String, String> parseMetadata(JSONObject metadataJsonObject) {
        Map<String, String> metadata = new HashMap<>();

        // Iterate over each key-value pair in the metadata and store it in the map
        // Iterate over each key-value pair in the metadata and store it in the map
        for (String key : metadataJsonObject.keySet()) {
            Object value = metadataJsonObject.get(key);

            // Convert value to String (handling different types of values)
            if (value instanceof Integer) {
                metadata.put(key, String.valueOf(value));
            } else {
                metadata.put(key, value.toString());
            }
        }
        return metadata;
    }


    // Method to send a simple GET request to test the connection
    public boolean getConnection() {
        try {
            // Open a connection to the Flask API
            URL urlObj = new URL(this.url);
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

            // Print the response code and body
            System.out.println("Response Code: " + statusCode);
            System.out.println("Response Body: " + response.toString());
            return true;
        } catch (IOException e) {
            System.out.println("Request failed: " + e.getMessage());
            return false;
        }
    }
}
