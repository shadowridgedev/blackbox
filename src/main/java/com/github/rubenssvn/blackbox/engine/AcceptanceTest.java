package com.github.rubenssvn.blackbox.engine;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Properties;

import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.skyscreamer.jsonassert.JSONAssert;

import com.github.rubenssvn.blackbox.annotation.Api;
import com.github.rubenssvn.blackbox.annotation.Dataset;
import com.github.rubenssvn.blackbox.annotation.Request;
import com.github.rubenssvn.blackbox.annotation.Response;
import com.github.rubenssvn.blackbox.database.DatabaseLoader;
import com.github.rubenssvn.blackbox.exception.AcceptanceTestException;
import com.github.rubenssvn.blackbox.http.RestResponse;

public abstract class AcceptanceTest extends Blackbox {
	
	private static final String TEARDOWN_FILE = "teardown.sql";
	private static final String SETUP_FILE = "setup.sql";
	private static final String ENDPOINT_BASE_URL_PROP = "endpoint.base.url";
	private static final String SCENARIOS_PATH_PROP = "scenarios.base.path";
	private static final String RESOURCE_ENCODING = "UTF-8";
	private static final String PROPERTIES_FILE = "blackbox.properties";
	
	private Properties properties = new Properties();
	
	@Rule
	public TestName testName = new TestName();
	
	@Before
	public void setup() throws Exception {
		loadProperties();
		databaseSetup();
		
		loadDataset(getApi(), getTestMethod().getAnnotation(Dataset.class));
		
		Request requestAnnotation = getTestMethod().getAnnotation(Request.class);
		Response responseAnnotation = getTestMethod().getAnnotation(Response.class);
		
		if (requestAnnotation != null && responseAnnotation != null) {
			RestResponse restResponse = requestHandler()
											.method(requestAnnotation.method())
											.body(requestAnnotation.body())
											.path(requestAnnotation.path())
											.call();
			
			String expectedBody = getJsonAsString(responseAnnotation.body());
			Status expectedStatus = responseAnnotation.httpStatus();
			
			if (expectedBody != null) {
				JSONAssert.assertEquals(expectedBody, restResponse.getBody(), false);
			}
			
			Assert.assertEquals(expectedStatus.getStatusCode(), restResponse.getStatus().getStatusCode());
		}
	}
	
	@After
	public void teardown() {
		databaseTeardown();
	}
	
	@Override
	public String getApi() {
		Api apiAnnotation = getClass().getAnnotation(Api.class);
		String api = apiAnnotation.value();
		return api;
	}
	
	@Override
	public String getEndpoint() {
		return properties.getProperty(ENDPOINT_BASE_URL_PROP);
	}
	
	@Override
	public String getJsonAsString(String file) {
		String resourceAsString = null;
		String api = getApi();
		
		if (StringUtils.isNotBlank(file)) {
			try {
				resourceAsString = IOUtils.toString(getClass().getResourceAsStream(getFileInResourcePath(api, file)), RESOURCE_ENCODING);
			} catch (IOException e) {
				throw new AcceptanceTestException(e);
			}
		}
		
		return resourceAsString;
	}
	
	private void databaseSetup() {
		DatabaseLoader loader = new DatabaseLoader();
		loader.executeSqlFile(properties, getClass().getResourceAsStream(getFileInBasePath(SETUP_FILE)));
	}
	
	private void databaseTeardown() {
		DatabaseLoader loader = new DatabaseLoader();
		loader.executeSqlFile(properties, getClass().getResourceAsStream(getFileInBasePath(TEARDOWN_FILE)));
	}
	
	private void loadDataset(String api, Dataset datasetAnnotation) {
		if (datasetAnnotation != null) {
			DatabaseLoader loader = new DatabaseLoader();
			loader.executeSqlFile(properties, getClass().getResourceAsStream(getFileInResourcePath(api, datasetAnnotation.value())));
		}
	}
	
	private String getFileInResourcePath(String api, String file) {
		return String.format("/%s%s/%s", properties.getProperty(SCENARIOS_PATH_PROP), api, file);
	}
	
	private String getFileInBasePath(String file) {
		return String.format("/%s/%s", properties.getProperty(SCENARIOS_PATH_PROP), file);
	}
	
	private Method getTestMethod() {
		try {
			Class<? extends AcceptanceTest> clazz = getClass();
			Method method;
			method = clazz.getMethod(testName.getMethodName());
			return method;
		} catch (NoSuchMethodException | SecurityException e) {
			throw new AcceptanceTestException(e);
		}
	}
	
	private void loadProperties() {
		InputStream is = ClassLoader.getSystemResourceAsStream(PROPERTIES_FILE);
		try {
			properties.load(is);
		} catch (IOException e) {
			throw new AcceptanceTestException(e);
		}
	}
	
}
