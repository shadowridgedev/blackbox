package com.stefanini.blackbox.engine;

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

import com.stefanini.blackbox.annotation.Api;
import com.stefanini.blackbox.annotation.Dataset;
import com.stefanini.blackbox.annotation.Request;
import com.stefanini.blackbox.annotation.Response;
import com.stefanini.blackbox.database.DatabaseLoader;
import com.stefanini.blackbox.exception.AcceptanceTestException;
import com.stefanini.blackbox.http.RestClient;
import com.stefanini.blackbox.http.RestResponse;

public class AcceptanceTest {
	
	private static final String TEARDOWN_FILE = "teardown.sql";
	private static final String SETUP_FILE = "setup.sql";
	private static final String PROPERTIES_FILE = "blackbox.properties";
	private static final String ENDPOINT_BASE_URL_PROP = "endpoint.base.url";
	private static final String SCENARIOS_PATH_PROP = "scenarios.base.path";
	private static final String RESOURCE_ENCODING = "UTF-8";
	
	private Properties properties = new Properties();
	
	@Rule
	public TestName testName = new TestName();
	
	@Before
	public void setup() throws Exception {
		setupDatabase();
		
		Request requestAnnotation = getTestMethod().getAnnotation(Request.class);
		Api apiAnnotation = getClass().getAnnotation(Api.class);
		Response responseAnnotation = getTestMethod().getAnnotation(Response.class);
		Dataset datasetAnnotation = getTestMethod().getAnnotation(Dataset.class);
		
		String api = apiAnnotation.value();
		String path = requestAnnotation.path();
		
		loadProperties();
		loadDataset(api, datasetAnnotation);
		
		String endpoint = properties.getProperty(ENDPOINT_BASE_URL_PROP) + api + path;
		RestResponse restResponse = RestClient.call(endpoint, getResourceAsString(api, requestAnnotation.body()), requestAnnotation.method());

		String expectedBody = getResourceAsString(api, responseAnnotation.body());
		Status expectedStatus = responseAnnotation.httpStatus();

		if (expectedBody != null) {
			JSONAssert.assertEquals(restResponse.getBody(), expectedBody, false);
		}

		Assert.assertEquals(restResponse.getStatus().getStatusCode(), expectedStatus.getStatusCode());
	}
	
	@After
	public void teardown() {
		teardownDatabase();
	}
	
	private void setupDatabase() {
		DatabaseLoader loader = new DatabaseLoader();
		loader.executeSqlFile(properties, getClass().getResourceAsStream(getFileInBasePath(SETUP_FILE)));
	}
	
	private void teardownDatabase() {
		DatabaseLoader loader = new DatabaseLoader();
		loader.executeSqlFile(properties, getClass().getResourceAsStream(getFileInBasePath(TEARDOWN_FILE)));
	}
	
	private void loadDataset(String api, Dataset datasetAnnotation) {
		if (datasetAnnotation != null) {
			DatabaseLoader loader = new DatabaseLoader();
			loader.executeSqlFile(properties, getClass().getResourceAsStream(getFileInResourcePath(api, datasetAnnotation.value())));
		}
	}
	
	private String getResourceAsString(String api, String file) throws IOException {
		String resourceAsString = null;
		
		if (StringUtils.isNotBlank(file)) {
			resourceAsString = IOUtils.toString(getClass().getResourceAsStream(getFileInResourcePath(api, file)), RESOURCE_ENCODING);
		}
		
		return resourceAsString;
	}
	
	private String getFileInResourcePath(String api, String resource) {
		return String.format("/%s%s/%s", properties.getProperty(SCENARIOS_PATH_PROP), api, resource);
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
