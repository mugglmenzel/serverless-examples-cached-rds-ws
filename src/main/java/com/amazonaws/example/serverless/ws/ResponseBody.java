package com.amazonaws.example.serverless.ws;

public class ResponseBody {

    private String message;
    private Request request;

    public ResponseBody(String message, Request request) {
        this.message = message;
        this.request = request;
    }

    public ResponseBody() {
    }

    public String getMessage() {
        return this.message;
    }

    public Request getRequest() {
        return this.request;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

}
