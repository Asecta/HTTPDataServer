package io.asecta.core.dependencyinjection.exceptions;

public class InvalidDependencyAnnotationException extends DependencyInjectionException {
    private static final long serialVersionUID = -5387732648101490196L;
    
    public InvalidDependencyAnnotationException() {
        super("You cannot inject a dependency on that field. Check the field modifiers.");
    }
    
}
