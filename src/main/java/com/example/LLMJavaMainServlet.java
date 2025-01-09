package com.example;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import com.example.llm.OllamaLLMClient;
import com.example.llm.response.Response;
import com.example.llm.response.ResponseChunk;
import com.example.Prompt;
import com.example.rag.mainrag;

@WebServlet("/*")
public class LLMJavaMainServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        out.print("{\"message\": \"The Backend of JavaLLM is running!\"}");
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Set the response to allow streaming (real-time response)
        response.setHeader("Transfer-Encoding", "chunked");

        // Read the user input (prompt) from the request
        String userPrompt = request.getReader().readLine(); // Example to read input

        // Define API URLs for classifier and database
        String classifierApiUrl = "http://161.35.26.211:6000"; // Replace with actual API URL
        String dbVectorUrl = "http://161.35.26.211:5000"; // Replace with actual API URL
        
        // Create a Prompt object
        Prompt prompt = new Prompt(userPrompt);

        // Initialize the main RAG processor
        mainrag ragProcessor = new mainrag(classifierApiUrl, dbVectorUrl);

        // Process the prompt through the RAG system
        boolean isRagSuccess = ragProcessor.processPrompt(prompt);

        // Step 2: Generate a response using the LLM client
        if (isRagSuccess) {
            // Once RAG processing is done, use the OllamaLLMClient to generate a real-time response
            OllamaLLMClient.generateResponseStream(prompt, response.getWriter());  // Stream directly to the client

        } else {
            // Handle the case where RAG processing failed
            response.getWriter().print("{\"status\": \"error\", \"message\": \"Failed to process the prompt through RAG system.\"}");
            response.getWriter().flush();
        }
    }
}
