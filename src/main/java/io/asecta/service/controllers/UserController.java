package io.asecta.service.controllers;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import io.asecta.core.dependencyinjection.annotations.Inject;
import io.asecta.rest.authentication.Accessor;
import io.asecta.rest.authentication.TokenAuthenticator;
import io.asecta.rest.router.ResponsePayload;
import io.asecta.rest.router.Status;
import io.asecta.rest.router.autorouter.AutoRouter;
import io.asecta.rest.router.autorouter.Mapping;
import io.asecta.rest.router.requesthandler.RequestBody;
import io.asecta.rest.router.requesthandler.RequestPayload;
import io.asecta.rest.router.requesthandler.exception.InvalidBodyException;
import io.asecta.service.model.User;
import io.asecta.service.repository.UserRepository;

public class UserController extends AutoRouter {

	@Inject private Gson gson;
	@Inject private TokenAuthenticator authenticator;
	@Inject private UserRepository userRepository;

	@Override
	public String getBaseRoute() {
		return "/user";
	}

	@Override
	public void handleBase(String[] pathParams, Accessor accessor, RequestPayload request, RequestBody body,
			ResponsePayload payload) {
		payload.setStatus(Status.NOT_FOUND);
	}

	@Mapping("update")
	public void handleUpdateUser(String[] pathParams, Accessor accessor, RequestPayload request, RequestBody body,
			ResponsePayload payload) throws InvalidBodyException {
		if (accessor.isAnonymous() || !(accessor instanceof User)) {
			payload.setStatus(Status.UNAUTHORIZED);
			return;
		}

		body.allowNull();

		String username = body.getString("username");
		String password = body.getString("password");
		String avatarBlob = body.getString("avatar");

		if (username == null && password == null && avatarBlob == null) {
			payload.setStatus(Status.BAD_REQUEST);
			return;
		}

		User user = userRepository.getItemById(((User) accessor).getId());

		if (username != null && !username.isEmpty()) {
			user.setUsername(username);
		}

		if (password != null && !username.isEmpty()) {
			user.setPassword(password);
		}

		if (avatarBlob != null && !avatarBlob.isEmpty()) {
			String uid = ImageController.saveFile(avatarBlob);
			System.out.println(uid);
			user.setAvatar(uid);
		}

		userRepository.saveItem(user);

		payload.setMessage("User updated Succesfully");
	}

	@Mapping("getcurrentuser")
	public void handleGetByToken(String[] pathParams, Accessor accessor, RequestPayload request, RequestBody body,
			ResponsePayload payload) throws InvalidBodyException {
		Map<String, Object> map = new HashMap<>();

		if (!(accessor instanceof User)) {
			map.put("email", "");
			map.put("token", "");
			map.put("role", accessor.getRole().toString());
			payload.setContent(map);
			return;
		}
		User user = userRepository.getItemById(accessor.getId());

		map.put("email", user.getEmail());
		map.put("token", authenticator.getActiveToken(accessor));
		map.put("role", user.getRole().toString());
		map.put("username", user.getUsername());
		map.put("avatarURL", user.getAvatar());
		map.put("id", user.getId());
		payload.setContent(map);
	}

	@Mapping("login")
	public void handleLogin(String[] pathParams, Accessor accessor, RequestPayload request, RequestBody body,
			ResponsePayload payload) throws InvalidBodyException {
		if (!accessor.isAnonymous()) {
			payload.setMessage("Already Logged In");
			return;
		}

		String email = body.getString("email");
		String password = body.getString("password");

		System.out.println("Attempting Login with " + email + " : " + password);

		// Find user with Email
		User user = userRepository.getItemByEmail(email);

		// Check if user is already Logged in
		String activeToken = authenticator.getActiveToken(user);
		if (activeToken != null) {
			payload.setMessage("Already Logged In");
			payload.setContent(activeToken);
			payload.setStatus(Status.OK);
			return;
		}

		// Incorrect email check, if user is null
		if (user == null) {
			System.out.println("Incorrect Email");
			payload.setMessage("Incorrect login");
			payload.setStatus(Status.NOT_ACCEPTABLE);
			return;
		}

		// Password check
		if (!user.checkPassword(password)) {
			System.out.println("Incorrect Password");
			payload.setMessage("Incorrect login");
			return;
		}

		payload.setMessage("User Login Success");
		System.out.println("User Login Succesful for " + user.getEmail());
		System.out.println(user.getEmail() + "'s role is " + user.getRole().toString());

		String token = authenticator.createSession(user);
		System.out.println(user.getEmail() + " designated token " + token);

		payload.setStatus(Status.OK);
		payload.setContent(token);
	}

	@Mapping("register")
	public void handleRegister(String[] pathParams, Accessor accessor, RequestPayload request, RequestBody body,
			ResponsePayload payload) throws InvalidBodyException {
		String email = body.getString("email");
		String password = body.getString("password");

		User user = userRepository.getItemByEmail(email);
		if (user != null) {
			payload.setStatus(Status.NOT_ACCEPTABLE);
			payload.setMessage("Email in use");
			return;
		}

		User newUser = new User(email, password);

		userRepository.saveItem(newUser);
		payload.setMessage("Success");
	}

	@Mapping("logout")
	public void handleLoginReg(String[] pathParams, Accessor accessor, RequestPayload request, RequestBody body,
			ResponsePayload payload) {
		System.out.println(accessor.getUsername() + " logged out");
		authenticator.removeSession(accessor);
		payload.setMessage("Logout Successful");
	}

	@Override
	public boolean isAcceptable(RequestPayload request) {
		return request.getMethod().toUpperCase().equals("POST");
	}

}
