package com.github.rubenssvn.blackbox.http;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

import com.github.rubenssvn.blackbox.enums.RequestMethod;

public class RestClient {
	
	public static RestResponse call(String resource, String body, RequestMethod method) throws Exception {
		ClientRequest request = new ClientRequest(resource);
		
		if (StringUtils.isNotBlank(body)) {
			request.body(MediaType.APPLICATION_JSON, body);
		}
		
		ClientResponse<String> response = RestClient.getResponse(request, method);

		RestResponse restResponse = new RestResponse();
		restResponse.setStatus(response.getResponseStatus());
		restResponse.setBody(new String(response.getEntity().getBytes(), "UTF-8"));

		return restResponse;
	}
	
	private static ClientResponse<String> getResponse(ClientRequest request, RequestMethod method) throws Exception {
		ClientResponse<String> response = null;
		
		switch (method) {
			case GET:
				response = request.get(String.class);
				break;
			case POST:
				response = request.post(String.class);
				break;
			case PUT:
				response = request.put(String.class);
				break;
			case DELETE:
				response = request.delete(String.class);
				break;
		}
		
		return response;
	}
	
}
