package com.example.rag.datatype;

public abstract class QueryResult {
    public float distance;

    public QueryResult(float distance){
        this.distance = distance;
    }

    public QueryResult() {
        this.distance = -1.f; // or some default value
    }

    public abstract String getLink();

    
    @Override
    public String toString() {
        return "distance : " + this.distance;
    }
}
