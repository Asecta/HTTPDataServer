package io.asecta.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.asecta.core.ApplicationService;
import io.asecta.core.dependencyinjection.DependencyService;
import io.asecta.core.dependencyinjection.Initializable;
import io.asecta.core.dependencyinjection.annotations.Bean;
import io.asecta.core.dependencyinjection.annotations.Inject;
import io.asecta.rest.RestDataServer;
import io.asecta.rest.SessionRouter;
import io.asecta.rest.authentication.IAuthenticator;
import io.asecta.rest.authentication.TokenAuthenticator;
import io.asecta.rest.router.responsehandler.IResponseHandler;
import io.asecta.rest.router.responsehandler.IResponseHeaderHandler;
import io.asecta.rest.router.responsehandler.JsonResponseHandler;
import io.asecta.rest.router.responsehandler.cors.BasicCorsResponseHeaderHandler;
import io.asecta.service.config.DataConfig;
import io.asecta.service.controllers.ImageController;
import io.asecta.service.controllers.PingController;
import io.asecta.service.controllers.UserController;

public class HttpDataApplication implements Initializable {

	public static void main(String[] args) throws Exception {
		ApplicationService.launch(HttpDataApplication.class);
	}

	@Inject
	private DataConfig config;

	@Inject
	private DependencyService dependencyService;

	@Inject
	private RestDataServer dataServer;

	@Override
	public void initialize() {
		dataServer.startServer();
	}

	@Bean
	private Gson getGson() {
		return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	}

	@Bean
	private IAuthenticator createAuthenticator() {
		return dependencyService.resolve(TokenAuthenticator.class);
	}

	@Bean
	private IResponseHandler createResponseHandler() {
		return dependencyService.resolve(JsonResponseHandler.class);
	}

	@Bean
	private IResponseHeaderHandler createResponseHeaderHandler() {
		BasicCorsResponseHeaderHandler responseHeaderHandler = dependencyService
				.resolve(BasicCorsResponseHeaderHandler.class);
		responseHeaderHandler.addACAHeader("CoAuth");
		responseHeaderHandler.setACAOrigin("*");
		return responseHeaderHandler;
	}

	@Bean
	private SessionRouter createSessionRouter() {
		SessionRouter sessionRouter = new SessionRouter();
		sessionRouter.addRoute(dependencyService.resolve(UserController.class));
		sessionRouter.addRoute(dependencyService.resolve(ImageController.class));
		sessionRouter.addRoute(dependencyService.resolve(PingController.class));
		return sessionRouter;
	}

	@Bean
	private DataConfig loadConfig() throws IOException {
		File file = new File("config.yml");
		if (!file.exists()) {
			File parent = file.getParentFile();
			if ((parent == null || parent.exists() || parent.mkdirs()) && !file.createNewFile()) {
				throw new IOException("Failed to create file config.yml");
			}

			try (OutputStream out = new FileOutputStream(file)) {
				DataConfig.save(DataConfig.defaultConfig(), out);
			}

			throw new IOException("Cosyn data config config.yml did not yet exist, please edit and relaunch");
		} else if (file.isDirectory()) {
			throw new IOException("File config.yml is a directory");
		}

		try (InputStream in = new FileInputStream("config.yml")) {
			return DataConfig.load(in);
		}
	}

	@Bean
	public RestDataServer createCosynDataServer() {
		return new RestDataServer(4201);
	}
}