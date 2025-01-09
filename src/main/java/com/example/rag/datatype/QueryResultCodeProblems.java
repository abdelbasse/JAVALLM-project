package com.example.rag.datatype;

import java.util.List;
import java.util.Map;

public class QueryResultCodeProblems extends QueryResult {
    private String title;
    private String description;
    private String link;
    private List<String> solutions;
    private Map<String, String> metadatas;

    // Constructor
    public QueryResultCodeProblems(String title, String description, String link, List<String> solutions, Map<String, String> metadatas) {
        super();
        this.title = title;
        this.description = description;
        this.link = link;
        this.solutions = solutions;
        this.metadatas = metadatas;
    }

    public QueryResultCodeProblems(String title, String description, String link, List<String> solutions, Map<String, String> metadatas , float distance) {
        super(distance);
        this.title = title;
        this.description = description;
        this.link = link;
        this.solutions = solutions;
        this.metadatas = metadatas;
    }

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }

    public List<String> getSolutions() {
        return solutions;
    }

    public Map<String, String> getMetadatas() {
        return metadatas;
    }

    @Override
    public String toString() {
        return "QueryResultCodeProblems{" +
                 super.toString() +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", link='" + link + '\'' +
                ", solutions=" + solutions +
                ", metadatas=" + metadatas +
                '}';
    }
}
