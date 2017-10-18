package com.github.rubenssvn.blackbox.http;

import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

import com.github.rubenssvn.blackbox.enums.RequestMethod;

public class RestClient {
	
	public static RestResponse call(String resource, Object body, RequestMethod method) throws Exception {
		ClientRequest request = new ClientRequest(resource);
		
		if (body != null) {
			request.body(MediaType.APPLICATION_JSON, getJson(body));
		}
		
		ClientResponse<String> response = RestClient.getResponse(request, method);

		RestResponse restResponse = new RestResponse();
		restResponse.setStatus(response.getResponseStatus());
		
		if (response.getEntity() != null) {
			restResponse.setBody(new String(response.getEntity().getBytes(), "UTF-8"));
		}

		return restResponse;
	}
	
	private static String getJson(Object body) throws JsonGenerationException, JsonMappingException, IOException {
		if (body instanceof String) {
			return (String) body;
		}
		
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(body);
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
