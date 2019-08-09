package io.asecta.core.dependencyinjection.exceptions;

import java.lang.reflect.Type;

public class DependencyInjectionException extends RuntimeException {
    private static final long serialVersionUID = -3683550934111780037L;
    
    public DependencyInjectionException(String message) {
        super(message);
    }
    
    public DependencyInjectionException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static DependencyInjectionException missingInheritance() {
        return new DependencyInjectionException("Provided object does not inherit the provided class");
    }
    
    public static DependencyInjectionException badConstructor(Exception cause) {
        return new BadConstructorInjectionException("Provided class does not have a public no-arg constructor or it threw an exception", cause);
    }
    
    public static DependencyInjectionException badInstantiator() {
        return new DependencyInjectionException("Provided instantiator is invalid. It must be a method annotated with @Bean or a constructor, with all parameters annotated by @Inject");
    }
    
    public static DependencyInjectionException unresolvedType(Type type) {
        return new MissingDependencyException("Unable to resolve the type " + type.getTypeName());
    }
    
}
