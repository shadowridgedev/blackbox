package com.stefanini.blackbox.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.stefanini.blackbox.exception.AcceptanceTestException;

public final class DatabaseLoader {
	
	private static final String DRIVER_PROP = "database.jdbc.driver";
	private static final String URL_PROP = "database.connection.url";
	private static final String USER_PROP = "database.user";
	private static final String PASSWORD_PROP = "database.password";
	
	public void executeSqlFile(Properties properties, InputStream is) {
		if (is != null) {
			try (Connection connection = getConnection(properties)) {
				List<String> lines = getLines(is);
				
				for (String line : lines) {
					executeSqlLine(connection, line);
				}
			} catch (Exception e) {
				throw new AcceptanceTestException(e);
			}
		}
	}
	
	private List<String> getLines(InputStream is) throws IOException {
		List<String> lines = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
			String line;
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
		}
		return lines;
	}
	
	private void executeSqlLine(Connection connection, String sql) throws SQLException {
		try (Statement statement = connection.createStatement()) {
			statement.execute(sql);
		}
	}

	private Connection getConnection(Properties properties) throws SQLException, ClassNotFoundException {
		Connection connection = null;
		Class.forName(properties.getProperty(DRIVER_PROP));
		connection = DriverManager.getConnection(properties.getProperty(URL_PROP), 
												 properties.getProperty(USER_PROP), 
												 properties.getProperty(PASSWORD_PROP));
		return connection;
	}
	
}
