package com.example.llm.response;

public class ResponseChunk {
    private String response; // The actual response chunk
    private boolean done;    // Flag to indicate if it's the last chunk

    // Constructor
    public ResponseChunk(String response, boolean done) {
        this.response = response;
        this.done = done;
    }

    // Getter and Setter methods
    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
