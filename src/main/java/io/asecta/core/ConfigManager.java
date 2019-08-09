package io.asecta.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ConfigManager<T> {

	private static final Charset UTF8 = Charset.forName("UTF-8");
	private static final Gson GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
			.setPrettyPrinting().create();

	private String configName;
	private T currentConfig;
	private Class<T> confClass;
	private File configFile;

	public ConfigManager(String configName, Class<T> confClass) {
		this.configName = configName;
		this.confClass = confClass;
	}

	public void init() {
		File configFile = new File(configName);
		this.configFile = configFile;
		if (!configFile.exists()) {
			saveDefaultConfig();
		}
		reload();
	}

	public void saveDefaultConfig() {
		try {
			configFile.getParentFile().mkdirs();
			configFile.createNewFile();

			T configObj = confClass.newInstance();
			String gsonString = GSON.toJson(configObj);

			System.out.println(confClass.getName());

			FileOutputStream fileOut = new FileOutputStream(configFile);
			OutputStreamWriter out = new OutputStreamWriter(fileOut, UTF8);
			out.write(gsonString);

			out.close();
			fileOut.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void reload() {
		try {
			FileInputStream fileIn = new FileInputStream(configFile);
			InputStreamReader in = new InputStreamReader(fileIn, UTF8);
			this.currentConfig = GSON.fromJson(in, confClass);

			in.close();
			fileIn.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public T getCurrentConfig() {
		return currentConfig;
	}

	public void setCurrentConfig(T currentConfig) {
		this.currentConfig = currentConfig;
	}
}