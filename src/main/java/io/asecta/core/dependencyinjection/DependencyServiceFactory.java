package io.asecta.core.dependencyinjection;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

final class DependencyServiceFactory {
    private final Map<Type, TypeRegistration> types;
    final DependencyService service;
    
    DependencyServiceFactory() {
        this(null);
    }
    
    DependencyServiceFactory(DependencyService dependencyService) {
        this.types = new HashMap<>();
        if (dependencyService == null) {
            this.service = new DependencyService(this);
        } else {
            this.service = dependencyService;
        }
        
        this.types.put(DependencyService.class, new ProvidedTypeRegistration(this, DependencyService.class, new CachedDependencyProvider(this.service)));
        
        if (dependencyService != null && dependencyService.dependencyCache != null) {
            for (Map.Entry<Type, DependencyProvider> entry : dependencyService.dependencyCache.entrySet()) {
                types.put(entry.getKey(), new ProvidedTypeRegistration(this, entry.getKey(), entry.getValue()));
            }
        }
    }
    
    TypeRegistration getRegistration(Type type) {
        TypeRegistration target;
        if ((target = this.types.get(type)) == null) {
            this.types.put(type, target = new UnresolvedTypeRegistration(this, type));
            ((UnresolvedTypeRegistration) target).addDefaultResolvers();
        }
        return target;
    }
    
    boolean tryResolveType(Class<?> type) {
        TypeRegistration target = getRegistration(type);
        
        if (service.dependencyCache == null) {
            service.dependencyCache = makeNewDependencyMap();
        } else {
            addMissingProviders(service.dependencyCache);
        }
        
        return target.isResolved();
    }
    
    private Map<Type, DependencyProvider> makeNewDependencyMap() {
        Map<Type, DependencyProvider> dependencyProviders = new HashMap<>(this.types.size());
        dependencyProviders.put(DependencyService.class, new CachedDependencyProvider(this.service));
        
        for (Map.Entry<Type, TypeRegistration> entry : this.types.entrySet()) {
            dependencyProviders.put(entry.getKey(), entry.getValue().getDependencyProvider());
        }
        
        return dependencyProviders;
    }
    
    private void addMissingProviders(Map<Type, DependencyProvider> map) {
        map.put(DependencyService.class, new CachedDependencyProvider(this.service));
        for (Type key : this.types.keySet()) {
            map.computeIfAbsent(key, k -> this.types.get(k).getDependencyProvider());
        }
    }
    
    DependencyService getDependencyService() {
        return service;
    }
    
}




