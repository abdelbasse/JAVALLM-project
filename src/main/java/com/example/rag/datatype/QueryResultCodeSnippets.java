package com.example.rag.datatype;
import java.util.Map;

public class QueryResultCodeSnippets extends QueryResult {
    private String link;
    private String documents;
    private Map<String, String> metadatas;

    // Constructor
    public QueryResultCodeSnippets(String link, String documents, Map<String, String> metadatas) {
        super();
        this.link = link;
        this.documents = documents;
        this.metadatas = metadatas;
    }

    public QueryResultCodeSnippets(String link, String documents, Map<String, String> metadatas , float distance) {
        super(distance);
        this.link = link;
        this.documents = documents;
        this.metadatas = metadatas;
    }

    public String getLink() {
        return link;
    }

    public String getDocuments() {
        return documents;
    }

    public Map<String, String> getMetadatas() {
        return metadatas;
    }

    @Override
    public String toString() {
        return "QueryResultCodeSnippets{" +
                super.toString() +
                ", link='" + link + '\'' +
                ", documents=" + documents +
                ", metadatas=" + metadatas +
                '}';
    }
}
