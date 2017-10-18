package com.github.rubenssvn.blackbox.engine;

import org.apache.commons.lang3.StringUtils;

import com.github.rubenssvn.blackbox.enums.RequestMethod;
import com.github.rubenssvn.blackbox.exception.AcceptanceTestException;
import com.github.rubenssvn.blackbox.http.RestClient;
import com.github.rubenssvn.blackbox.http.RestResponse;

public abstract class Blackbox {
	
	private RequestMethod method;
	private Object body;
	private String path = "";
	
	public abstract String getApi();
	public abstract String getEndpoint();
	public abstract String getJsonAsString(String file);
	
	public Blackbox requestHandler() {
		method = null;
		body = null;
		path = "";
		
		return this;
	}
	
	public Blackbox method(RequestMethod method) {
		this.method = method;
		return this;
	}
	
	public Blackbox body(Object body) {
		if (body instanceof String && StringUtils.isNotBlank((String) body)) {
			this.body = getJsonAsString((String) body);
		} else if (!(body instanceof String)) {
			this.body = body;
		}
		return this;
	}
	
	public Blackbox path(String path) {
		if (path != null) {
			this.path = path;
		}
		return this;
	}
	
	public RestResponse call() {
		try {
			String resource = getEndpoint() + getApi() + path;
			return RestClient.call(resource, body, method);
		} catch (Exception e) {
			throw new AcceptanceTestException(e);
		}
	}
	
}
