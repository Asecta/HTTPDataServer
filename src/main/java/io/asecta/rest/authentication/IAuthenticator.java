package io.asecta.rest.authentication;

import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IAuthenticator {
    
    public Accessor authenticate(Request baseRequest, HttpServletRequest request, HttpServletResponse response);
    
    public Accessor getAccessor(String token);
    
}
