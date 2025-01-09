package com.example.llm.response;

import java.util.List;

public class Response {
    private List<ResponseChunk> chunks;  // List to hold each chunk of response
    private String doneReason;           // Reason why it's done (e.g., "stop")
    private List<Integer> context;        // Changed to List<Object> to handle any type
    private long totalDuration;          // Total duration in nanoseconds
    private long loadDuration;           // Load duration in nanoseconds
    private int promptEvalCount;         // Number of times prompt was evaluated
    private int evalCount;               // Number of times evaluation was performed

    // Constructor
    public Response(List<ResponseChunk> chunks, String doneReason, List<Integer> context,
                    long totalDuration, long loadDuration, int promptEvalCount, int evalCount) {
        this.chunks = chunks;
        this.doneReason = doneReason;
        this.context = context;
        this.totalDuration = totalDuration;
        this.loadDuration = loadDuration;
        this.promptEvalCount = promptEvalCount;
        this.evalCount = evalCount;
    }

    // Getter and Setter methods
    public List<ResponseChunk> getChunks() {
        return chunks;
    }

    public void setChunks(List<ResponseChunk> chunks) {
        this.chunks = chunks;
    }

    public String getDoneReason() {
        return doneReason;
    }

    public void setDoneReason(String doneReason) {
        this.doneReason = doneReason;
    }

    public List<Integer> getContext() {
        return context;
    }

    public void setContext(List<Integer> context) {
        this.context = context;
    }

    public long getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(long totalDuration) {
        this.totalDuration = totalDuration;
    }

    public long getLoadDuration() {
        return loadDuration;
    }

    public void setLoadDuration(long loadDuration) {
        this.loadDuration = loadDuration;
    }

    public int getPromptEvalCount() {
        return promptEvalCount;
    }

    public void setPromptEvalCount(int promptEvalCount) {
        this.promptEvalCount = promptEvalCount;
    }

    public int getEvalCount() {
        return evalCount;
    }

    public void setEvalCount(int evalCount) {
        this.evalCount = evalCount;
    }
}
