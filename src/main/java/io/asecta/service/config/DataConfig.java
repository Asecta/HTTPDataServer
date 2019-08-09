package io.asecta.service.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

public class DataConfig {
	private static final ObjectMapper objectMapper;

	static {
		ObjectMapper objectMapper1 = new YAMLMapper();
		objectMapper1.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
		objectMapper1.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		objectMapper = objectMapper1;
	}

	private DatabaseConfig database;

	public DataConfig() {
	}

	public DataConfig(DatabaseConfig database) {
		this.database = database;
	}

	public DatabaseConfig getDatabase() {
		return database;
	}



	public static void save(DataConfig config, OutputStream stream) throws IOException {
		objectMapper.writer(new DefaultPrettyPrinter()).writeValue(stream, config);
	}

	public static DataConfig load(InputStream stream) throws IOException {
		return objectMapper.readValue(stream, DataConfig.class);
	}

	public static DataConfig defaultConfig() {
		return new DataConfig(new DatabaseConfig());
	}

}
