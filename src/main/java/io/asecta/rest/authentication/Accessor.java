package io.asecta.rest.authentication;

public interface Accessor {

	public boolean isAnonymous();

	public String getUsername();

	public String getRole();

	public int getId();

}
