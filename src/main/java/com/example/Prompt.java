package com.example;

import com.example.rag.datatype.QueryResult;
import com.example.rag.datatype.QueryResultCodeProblems;
import com.example.rag.datatype.QueryResultCodeSnippets;

import java.util.Arrays;
import java.util.List;

public class Prompt {
    private String content;
    private List<QueryResult> queryRAGResults;

    // Constructor
    public Prompt(String content) {
        this.content = content;
    }

    // Getter for content
    public String getContent() {
        return content;
    }

    // Setter for content
    public void setContent(String content) {
        this.content = content;
    }

    // Getter for query results
    public List<QueryResult> getQueryResults() {
        return queryRAGResults;
    }

    // Setter for query results
    public void setQueryRAGResults(List<QueryResult> queryRAGResults) {
        this.queryRAGResults = queryRAGResults;
    }

    // Method to check if there are any query results
    public boolean hasRAGResults() {
        return queryRAGResults != null && !queryRAGResults.isEmpty();
    }

    private boolean isJavaOrProgrammingRelated(String content) {
        String[] programmingKeywords = {
                "java", "code", "programming", "debug", "error", "class", "method",
                "function", "compile", "exception", "variable", "interface", "implement",
                "extends", "override", "public", "private", "protected", "static"
        };

        String lowercaseContent = content.toLowerCase();
        return Arrays.stream(programmingKeywords)
                .anyMatch(lowercaseContent::contains);
    }

    private boolean isOtherProgrammingLanguage(String content) {
        String[] otherLanguages = {
                "python", "javascript", "c++", "ruby", "php", "golang", "rust",
                "swift", "kotlin", "typescript"
        };

        String lowercaseContent = content.toLowerCase();
        return Arrays.stream(otherLanguages)
                .anyMatch(lowercaseContent::contains);
    }

    @Override
    public String toString() {
        StringBuilder resultString = new StringBuilder();

        // Check if prompt is Java-related or about another programming language
        if (!isJavaOrProgrammingRelated(content)) {
            if (isOtherProgrammingLanguage(content)) {
                return "Tell Directly to the user that \"I can only assist with Java programming questions and tasks. Please rephrase your question for Java programming.\"";
            }
            return "Tell Directly to the user that \"I can only help with Java programming related questions, such as code generation, debugging, or problem-solving. Please ask a programming-related question.\"";
        }

        // Build the main instruction string
        resultString.append("Prompt{content='").append(content).append('\'');

        // Add relevant query results if they exist
        if (hasRAGResults()) {
            resultString.append(", queryResults=[");
            for (QueryResult queryResult : queryRAGResults) {
                if (queryResult instanceof QueryResultCodeProblems) {
                    resultString.append(((QueryResultCodeProblems) queryResult).toString()).append(", ");
                } else if (queryResult instanceof QueryResultCodeSnippets) {
                    resultString.append(((QueryResultCodeSnippets) queryResult).toString()).append(", ");
                }
            }
            // Remove the last comma and space if results were added
            if (resultString.charAt(resultString.length() - 2) == ',') {
                resultString.setLength(resultString.length() - 2);
            }
            resultString.append(']');
        }

        // Add processing instructions
        resultString.append(", instructions='''")
                .append("Provide a direct and focused response based on the user's Java programming query. ")
                .append("If code generation is requested, provide clean, well-documented Java code. ")
                .append("If debugging/error fixing is needed, explain the solution clearly. ")
                .append("If explaining concepts, be concise and use relevant examples. ")
                .append("Use provided code examples or solutions only if directly relevant to the query. ")
                .append("Focus on practical, actionable answers without unnecessary context.")
                .append("'''");

        resultString.append('}');
        return resultString.toString();
    }
}