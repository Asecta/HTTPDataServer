package io.asecta.core.dependencyinjection;

abstract class StagedDependencyProvider implements DependencyProvider {
    
    abstract Object instantiate();
    
    abstract void postInstantiate(Object instance);
    
    @Override
    public Object get() {
        Object result = instantiate();
        postInstantiate(result);
        return result;
    }
}
