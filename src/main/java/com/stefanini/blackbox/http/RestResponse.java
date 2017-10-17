package com.stefanini.blackbox.http;

import javax.ws.rs.core.Response;

public class RestResponse {

    private Response.Status status;
    private String body;

    public Response.Status getStatus() {
        return status;
    }

    public void setStatus(Response.Status status) {
        this.status = status;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
