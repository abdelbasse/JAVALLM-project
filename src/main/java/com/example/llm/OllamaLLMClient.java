// package com.example.llm;

// import com.example.Prompt;
// import com.example.llm.response.Response;
// import com.example.llm.response.ResponseChunk;
// import org.json.JSONArray;
// import org.json.JSONObject;

// import java.io.*;
// import java.net.HttpURLConnection;
// import java.net.URL;
// import java.util.ArrayList;
// import java.util.List;

// public class OllamaLLMClient {
//     private static final String API_URL = "http://104.248.27.51:11434/api/generate";  // Ollama API URL

//     // Method to interact with the Ollama API and generate a response in real-time or collect all chunks
//     public static Response generateResponse(Prompt prompt, boolean realTime) {
//         List<ResponseChunk> chunks = new ArrayList<>();
//         List<Integer> context = new ArrayList<>(); // To hold the context values
//         StringBuilder fullResponse = new StringBuilder();

//         try {
//             // Prepare the request payload (JSON format)
//             JSONObject payload = new JSONObject();
//             payload.put("model", "llama3.2");  // Specify the model
//             payload.put("prompt", prompt.toString());  // Set the prompt from the Prompt class

//             // Establish HTTP connection
//             URL url = new URL(API_URL);
//             HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//             connection.setRequestMethod("POST");
//             connection.setRequestProperty("Content-Type", "application/json");
//             connection.setDoOutput(true);

//             // Send request
//             try (OutputStream os = connection.getOutputStream()) {
//                 byte[] input = payload.toString().getBytes("utf-8");
//                 os.write(input, 0, input.length);
//             }

//             // Get the response from the API
//             int statusCode = connection.getResponseCode();
//             if (statusCode == HttpURLConnection.HTTP_OK) {
//                 try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
//                     String line;
//                     boolean done = false; // Flag to track if response is complete

//                     while ((line = reader.readLine()) != null) {
//                         try {
//                             // Parse the JSON chunk
//                             JSONObject jsonResponse = new JSONObject(line);
//                             String responseText = jsonResponse.optString("response", "");
//                             done = jsonResponse.optBoolean("done", false);

//                             // Add context to the list (only collect context from the last chunk)
//                             if (jsonResponse.has("context")) {
//                                 JSONArray contextArray = jsonResponse.getJSONArray("context");
//                                 List<Integer> chunkContext = new ArrayList<>();
//                                 for (int i = 0; i < contextArray.length(); i++) {
//                                     chunkContext.add(contextArray.getInt(i));  // Adding integers to the context list
//                                 }
//                                 context.addAll(chunkContext);  // Merge the new context into the existing context
//                             }

//                             // Create ResponseChunk and add it to the list
//                             ResponseChunk chunk = new ResponseChunk(responseText, done);
//                             chunks.add(chunk);
//                             fullResponse.append(responseText);

//                             // Print chunk (real-time feedback) if enabled
//                             if (realTime) {
//                                 System.out.print(responseText);  // Print in real-time without newlines
//                             }

//                             // If done, break the loop
//                             if (done) {
//                                 break;
//                             }
//                         } catch (Exception e) {
//                             System.out.println("Error parsing chunk: " + e.getMessage());
//                         }
//                     }
//                 }

//                 // Create the Response object with the gathered data
//                 JSONObject lastResponse = new JSONObject();
//                 if (!chunks.isEmpty()) {
//                     lastResponse.put("done_reason", "stop"); // Assuming 'done' reason is "stop" when it finishes
//                     lastResponse.put("context", context); // Pass the full context collected
//                     lastResponse.put("total_duration", 1000L); // Example value
//                     lastResponse.put("load_duration", 100L); // Example value
//                     lastResponse.put("prompt_eval_count", 10); // Example value
//                     lastResponse.put("eval_count", 5); // Example value
//                 }

//                 // If real-time mode is off, print all collected chunks at the end
//                 if (!realTime) {
//                     System.out.println("\nFull Response (Collected):");
//                     System.out.println(fullResponse.toString());
//                 }

//                 // Return the complete Response object
//                 return new Response(chunks, lastResponse.optString("done_reason"),
//                         context, // Pass the correct context list
//                         lastResponse.optLong("total_duration"),
//                         lastResponse.optLong("load_duration"),
//                         lastResponse.optInt("prompt_eval_count"),
//                         lastResponse.optInt("eval_count"));
//             } else {
//                 System.out.println("Request failed with status code: " + statusCode);
//             }
//         } catch (IOException e) {
//             System.out.println("Request failed: " + e.getMessage());
//         }
//         return null;  // If no response is generated
//     }
// }

package com.example.llm;

import com.example.Prompt;
import com.example.llm.response.Response;
import com.example.llm.response.ResponseChunk;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class OllamaLLMClient {
    private static final String API_URL = "http://104.248.27.51:11434/api/generate";  // Ollama API URL

    // Method to interact with the Ollama API and generate a response in real-time or collect all chunks
    public static Response generateResponse(Prompt prompt, boolean realTime) {
        List<ResponseChunk> chunks = new ArrayList<>();
        List<Integer> context = new ArrayList<>(); // To hold the context values
        StringBuilder fullResponse = new StringBuilder();

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

                            // Add context to the list (only collect context from the last chunk)
                            if (jsonResponse.has("context")) {
                                JSONArray contextArray = jsonResponse.getJSONArray("context");
                                List<Integer> chunkContext = new ArrayList<>();
                                for (int i = 0; i < contextArray.length(); i++) {
                                    chunkContext.add(contextArray.getInt(i));  // Adding integers to the context list
                                }
                                context.addAll(chunkContext);  // Merge the new context into the existing context
                            }

                            // Create ResponseChunk and add it to the list
                            ResponseChunk chunk = new ResponseChunk(responseText, done);
                            chunks.add(chunk);
                            fullResponse.append(responseText);

                            // Print chunk (real-time feedback) if enabled
                            if (realTime) {
                                System.out.print(responseText);  // Print in real-time without newlines
                            }

                            // If done, break the loop
                            if (done) {
                                break;
                            }
                        } catch (Exception e) {
                            System.out.println("Error parsing chunk: " + e.getMessage());
                        }
                    }
                }

                // Create the Response object with the gathered data
                JSONObject lastResponse = new JSONObject();
                if (!chunks.isEmpty()) {
                    lastResponse.put("done_reason", "stop"); // Assuming 'done' reason is "stop" when it finishes
                    lastResponse.put("context", context); // Pass the full context collected
                    lastResponse.put("total_duration", 1000L); // Example value
                    lastResponse.put("load_duration", 100L); // Example value
                    lastResponse.put("prompt_eval_count", 10); // Example value
                    lastResponse.put("eval_count", 5); // Example value
                }

                // If real-time mode is off, print all collected chunks at the end
                if (!realTime) {
                    System.out.println("\nFull Response (Collected):");
                    System.out.println(fullResponse.toString());
                }

                // Return the complete Response object
                return new Response(chunks, lastResponse.optString("done_reason"),
                        context, // Pass the correct context list
                        lastResponse.optLong("total_duration"),
                        lastResponse.optLong("load_duration"),
                        lastResponse.optInt("prompt_eval_count"),
                        lastResponse.optInt("eval_count"));
            } else {
                System.out.println("Request failed with status code: " + statusCode);
            }
        } catch (IOException e) {
            System.out.println("Request failed: " + e.getMessage());
        }
        return null;  // If no response is generated
    }

    public static void generateResponseStream(Prompt prompt, PrintWriter clientWriter) {
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

                            // Send the token (responseText) to the client in real-time
                            if (!responseText.isEmpty()) {
                                // Write the JSON to the output stream
                                // os.write(responseText.getBytes());
                                // os.write("\n".getBytes()); // Adding newline between items
                                // os.flush();  
                                System.out.println(">> Send chunk : " + jsonResponse.toString());
                                clientWriter.print(jsonResponse.toString());
                                clientWriter.flush();  // Immediately send the response to the client
                            }

                            // If done, break the loop
                            if (done) {
                                break;
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
