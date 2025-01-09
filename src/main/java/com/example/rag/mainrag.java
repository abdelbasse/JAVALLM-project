package com.example.rag;

import com.example.Prompt;
import com.example.rag.datatype.QueryResult;
import java.util.List;
import java.util.Map;

public class mainrag {

    private ModalClassifier modalClassifier;
    private DatabaseVectoreClient snippetsDBClient;
    private DatabaseVectoreClient debuggingDBClient;

    public mainrag(String modalApiUrl, String vectorDbUrl) {
        // Initialize the ModalClassifier with the API URL
        this.modalClassifier = new ModalClassifier(modalApiUrl);

        // Initialize the vector database clients with their respective collection names
        this.snippetsDBClient = new DatabaseVectoreClient(vectorDbUrl, "JavaCodeSnippetsDB");
        this.debuggingDBClient = new DatabaseVectoreClient(vectorDbUrl, "JavaCodeDebuggingDB");
    }

    /**
     * Process a given prompt using ModalClassifier and DatabaseVectoreClient.
     *
     * @param prompt The prompt to process
     */
    public boolean processPrompt(Prompt prompt) {
        // Step 1: Classify the prompt using the ModalClassifier
        Map<String, Object> classificationResults = modalClassifier.classifyPrompt(prompt);
        if (classificationResults == null || !classificationResults.containsKey("Predicted Category")) {
            System.out.println("Classification failed or no relevant category found.");
            return false;
        }

        String predictedCategory = (String) classificationResults.get("Predicted Category");
        System.out.println("Predicted Category: " + predictedCategory);

        // Step 2: Query the appropriate database
        List<QueryResult> queryResults = queryDatabase(predictedCategory, prompt);

        // Step 3: Update the prompt with the query results
        if (queryResults != null) {
            prompt.setQueryRAGResults(queryResults);
            System.out.println("Query results added to the prompt.");
            return true;
        } else {
            System.out.println("No query results found.");
            return false;
        }
    }

    private List<QueryResult> queryDatabase(String predictedCategory, Prompt prompt) {
        List<QueryResult> queryResults = null;

        switch (predictedCategory) {
            case "Generating Code":
                System.out.println("Querying JavaCodeSnippetsDB...");
                queryResults = snippetsDBClient.getQueryResults(prompt.getContent());
                break;
            case "Debugging or Explaining Concept":
                System.out.println("Querying JavaCodeDebuggingDB...");
                queryResults = debuggingDBClient.getQueryResults(prompt.getContent());
                break;
            case "Generate and Explain Code":
                System.out.println("Querying JavaCodeDebuggingDB...");
                queryResults = snippetsDBClient.getQueryResults(prompt.getContent());
                queryResults.addAll(debuggingDBClient.getQueryResults(prompt.getContent()));
                break;
            case "None":
                System.out.println("No category matched; skipping database query.");
                break;
            default:
                System.out.println("Unknown category; no action taken.");
        }

        return queryResults;
    }
}

