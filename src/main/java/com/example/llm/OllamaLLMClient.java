package com.example.llm;

import com.example.Prompt;
import com.example.llm.response.Response;
import com.example.llm.response.ResponseChunk;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class OllamaLLMClient {
    private static final String API_URL = "http://104.248.27.51:11434/api/generate";  // Ollama API URL

    public static void generateResponseStream(Prompt prompt, PrintWriter clientWriter) {
        // Create a JSONObject to hold the entire response data
        JSONObject finalResponse = new JSONObject();
        JSONArray chunksArray = new JSONArray();  // Array to accumulate chunks of data

        try {
            // Prepare the request payload (JSON format)
            JSONObject payload = new JSONObject();
            payload.put("model", "llama3.2");  // Specify the model
            payload.put("prompt", prompt.toString());  // Set the prompt from the Prompt class
    
            // Establish HTTP connection
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
    
            // Send request
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = payload.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }
    
            // Get the response from the API
            int statusCode = connection.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                    String line;
                    boolean done = false; // Flag to track if response is complete
    
                    while ((line = reader.readLine()) != null) {
                        try {
                            // Parse the JSON chunk
                            JSONObject jsonResponse = new JSONObject(line);
                            String responseText = jsonResponse.optString("response", "");
                            done = jsonResponse.optBoolean("done", false);
                            JSONArray contextArray = jsonResponse.optJSONArray("context");  // Get the context array

                            // Log the context array if it exists
                            if (contextArray != null) {
                                System.out.println(">> Context Array: " + contextArray.toString());  // Log the context array
                            }

                            // Log other data fields
                            long totalDuration = jsonResponse.optLong("total_duration");
                            long loadDuration = jsonResponse.optLong("load_duration");
                            int promptEvalCount = jsonResponse.optInt("prompt_eval_count");
                            long promptEvalDuration = jsonResponse.optLong("prompt_eval_duration");
                            int evalCount = jsonResponse.optInt("eval_count");
                            long evalDuration = jsonResponse.optLong("eval_duration");

                            // Log other data fields
                            // System.out.println(">> Total Duration: " + totalDuration);
                            // System.out.println(">> Load Duration: " + loadDuration);
                            // System.out.println(">> Prompt Eval Count: " + promptEvalCount);
                            // System.out.println(">> Prompt Eval Duration: " + promptEvalDuration);
                            // System.out.println(">> Eval Count: " + evalCount);
                            // System.out.println(">> Eval Duration: " + evalDuration);

                            // Accumulate the chunk data
                            if (!responseText.isEmpty()) {
                                                             
                                System.out.println(">> Response: " + jsonResponse.toString());
                                clientWriter.print(jsonResponse.toString());
                                clientWriter.flush(); 
                            }
    
                            // If done, ensure the last chunk is sent with "done": true
                            if (done) {
                                // Finalize the response
                                finalResponse.put("chunks", chunksArray);  // Attach the accumulated chunks
                                finalResponse.put("done", done); // Optionally specify the reason for completion
                                finalResponse.put("done_reason", "stop"); // Optionally specify the reason for completion
                                
                                finalResponse.put("total-duration" , totalDuration);
                                finalResponse.put("load-duration" , loadDuration);
                                finalResponse.put("prompt-eval-count" , promptEvalCount);
                                finalResponse.put("prompt-eval-duration" , promptEvalDuration);
                                finalResponse.put("eval-count" , evalCount);
                                finalResponse.put("eval-duration" , evalDuration);
                                finalResponse.put("context" , contextArray.toString());
                                
                                // Log the final chunk
                                System.out.println(">> Final Response: " + finalResponse.toString());

                                // Send the accumulated result to the client in real-time (or at the end)
                                clientWriter.print(finalResponse.toString());
                                clientWriter.flush();  // Immediately send the response to the client
                                break;  // Break the loop after sending the final chunk
                            }
                        } catch (Exception e) {
                            System.out.println("Error parsing chunk: " + e.getMessage());
                        }
                    }
                }
            } else {
                System.out.println("Request failed with status code: " + statusCode);
            }
        } catch (IOException e) {
            System.out.println("Request failed: " + e.getMessage());
        }
    }
}
