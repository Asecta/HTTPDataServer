package io.asecta.core.dependencyinjection.exceptions;

public class BadConstructorInjectionException extends DependencyInjectionException {
    private static final long serialVersionUID = 2318278529055431823L;
    
    public BadConstructorInjectionException(String message, Exception cause) {
        super(message, cause);
    }
    
}
