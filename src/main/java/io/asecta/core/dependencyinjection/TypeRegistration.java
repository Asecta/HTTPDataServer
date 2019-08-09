package io.asecta.core.dependencyinjection;

import java.lang.reflect.Type;

abstract class TypeRegistration {
    final DependencyServiceFactory factory;
    final Type type;
    
    protected TypeRegistration(DependencyServiceFactory factory, Type type) {
        this.factory = factory;
        this.type = type;
    }
    
    abstract DependencyProvider getDependencyProvider();
    
    abstract void addDependant(TypeResolver dependant);
    
    abstract boolean isResolved();
    
    abstract boolean canInstantiate();
}
