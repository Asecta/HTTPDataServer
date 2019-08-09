package io.asecta.core.dependencyinjection;

import java.lang.reflect.Type;

final class ProvidedTypeRegistration extends TypeRegistration {
    private DependencyProvider provider;
    
    public ProvidedTypeRegistration(DependencyServiceFactory factory, Type type, DependencyProvider provider) {
        super(factory, type);
        this.provider = provider;
    }
    
    @Override
    DependencyProvider getDependencyProvider() {
        if (provider instanceof LazilyCachedDependencyProvider && ((LazilyCachedDependencyProvider) provider).isSet()) {
            this.provider = new CachedDependencyProvider(provider.get());
        }
        return this.provider;
    }
    
    @Override
    void addDependant(TypeResolver dependant) {
        dependant.onDependencyResolved(this);
    }
    
    @Override
    boolean isResolved() {
        return true;
    }
    
    @Override
    boolean canInstantiate() {
        return true;
    }
}
