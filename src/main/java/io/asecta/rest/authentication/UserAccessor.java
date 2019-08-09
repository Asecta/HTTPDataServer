package io.asecta.rest.authentication;

public abstract class UserAccessor implements Accessor {

	private String username;
	private String role;

	public UserAccessor(String username, String role) {
		this.username = username;
		this.role = role;
	}

	@Override
	public boolean isAnonymous() {
		return false;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public String getRole() {
		return role;
	}
}
