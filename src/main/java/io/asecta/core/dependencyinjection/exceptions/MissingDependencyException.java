package io.asecta.core.dependencyinjection.exceptions;

public class MissingDependencyException extends DependencyInjectionException {
    private static final long serialVersionUID = 2678150022701220592L;
    
    public MissingDependencyException(String message) {
        super(message);
    }
    
}
