package com.amazonaws.example.serverless.ws;

/**
 * Created by menzelmi on 27/02/2017.
 */
public class Response {

    private String statusCode;

    private String body;

    public Response(String statusCode, String body) {
        this.statusCode = statusCode;
        this.body = body;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
