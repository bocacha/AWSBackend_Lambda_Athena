package org.bootcamp.latam.handlers;

public class LambdaRequest {

    private String query;
    public LambdaRequest(){}
    public LambdaRequest(String query){
        this.query = query;
    }
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }


}