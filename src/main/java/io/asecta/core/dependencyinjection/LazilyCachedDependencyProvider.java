package io.asecta.core.dependencyinjection;

final class LazilyCachedDependencyProvider implements DependencyProvider {
    private StagedDependencyProvider delegate;
    private Object cache;
    
    public LazilyCachedDependencyProvider(StagedDependencyProvider delegate) {
        this.delegate = delegate;
    }
    
    boolean isSet() {
        return this.delegate == null;
    }
    
    @Override
    public synchronized Object get() {
        if (this.delegate == null) {
            return this.cache;
        }
        
        StagedDependencyProvider delegate = this.delegate;
        this.delegate = null;
        this.cache = delegate.instantiate();
        delegate.postInstantiate(this.cache);
        return this.cache;
    }
}
