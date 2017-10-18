package com.github.rubenssvn.blackbox.http;

import javax.ws.rs.core.Response;

import org.codehaus.jackson.map.ObjectMapper;

import com.github.rubenssvn.blackbox.exception.AcceptanceTestException;

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
    
    @SuppressWarnings("unchecked")
	public <T> T getBodyAsObject(Class<?> clazz) {
    	try {
    		ObjectMapper mapper = new ObjectMapper();
    		return (T) mapper.readValue(body, clazz);
    	} catch (Exception e) {
    		throw new AcceptanceTestException(e);
    	}
    }
}
