package io.asecta.core.dependencyinjection;

final class CachedDependencyProvider implements DependencyProvider {
    private Object value;
    
    public CachedDependencyProvider(Object value) {
        this.value = value;
    }
    
    @Override
    public Object get() {
        return this.value;
    }
    
}
