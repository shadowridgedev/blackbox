package com.github.rubenssvn.blackbox.engine;

import org.apache.commons.lang3.StringUtils;

import com.github.rubenssvn.blackbox.enums.RequestMethod;
import com.github.rubenssvn.blackbox.exception.AcceptanceTestException;
import com.github.rubenssvn.blackbox.http.RestClient;
import com.github.rubenssvn.blackbox.http.RestResponse;

public abstract class Blackbox {
	
	private RequestMethod method;
	private String body;
	private String path = "";
	
	public abstract String getApi();
	public abstract String getEndpoint();
	public abstract String getJsonAsString(String file);
	
	public Blackbox requestHandler() {
		return this;
	}
	
	public Blackbox method(RequestMethod method) {
		this.method = method;
		return this;
	}
	
	public Blackbox body(String body) {
		if (StringUtils.isNotBlank(body)) {
			this.body = getJsonAsString(body);
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
