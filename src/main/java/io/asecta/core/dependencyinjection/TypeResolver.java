package io.asecta.core.dependencyinjection;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;

import io.asecta.core.dependencyinjection.annotations.Bean;
import io.asecta.core.dependencyinjection.annotations.Inject;
import io.asecta.core.dependencyinjection.annotations.NotCached;
import io.asecta.core.dependencyinjection.exceptions.DependencyInjectionException;

final class TypeResolver implements Comparable<TypeResolver> {
    final UnresolvedTypeRegistration typeRegistration;
    private final Executable instantiator;
    private Map<TypeRegistration, DependencyType> dependencies;
    private Field[] injectableFields;
    private int numParametersUnresolved;
    
    TypeResolver(UnresolvedTypeRegistration typeRegistration, Executable instantiator) {
        this.typeRegistration = typeRegistration;
        this.instantiator = instantiator;
        this.dependencies = new HashMap<>();
        
        Field[] injectableFields = typeRegistration.getInjectableFields();
        for (Field field : injectableFields) {
            addDependency(field.getType(), DependencyType.FIELD);
        }
        
        if (!(instantiator instanceof Constructor)) {
            if (!(instantiator instanceof Method) || !instantiator.isAnnotationPresent(Bean.class)) {
                throw DependencyInjectionException.badInstantiator();
            }
            
            Method method = (Method) instantiator;
            if (!Modifier.isStatic(method.getModifiers())) {
                this.numParametersUnresolved++;
                addDependency(method.getDeclaringClass(), DependencyType.INSTANCE);
            }
        }
        
        Parameter[] parameters = instantiator.getParameters();
        for (Parameter parameter : parameters) {
            if (!parameter.isAnnotationPresent(Inject.class)) {
                throw DependencyInjectionException.badInstantiator();
            }
            
            this.numParametersUnresolved++;
            addDependency(parameter.getType(), DependencyType.PARAMETER);
        }
        
        this.injectableFields = injectableFields;
        typeRegistration.addResolver(this);
    }
    
    int parameterCount() {
        return this.instantiator.getParameterCount();
    }
    
    @Override
    public int compareTo(TypeResolver o) {
        if (this.instantiator instanceof Constructor) {
            if (o.instantiator instanceof Method) {
                return 1;
            }
            
            return Integer.compare(o.parameterCount(), parameterCount());
        }
        if (o.instantiator instanceof Constructor) {
            return -1;
        }
        return Integer.compare(o.parameterCount(), parameterCount());
    }
    
    boolean isResolved() {
        return this.dependencies.isEmpty();
    }
    
    boolean canInstantiate() {
        return this.numParametersUnresolved == 0;
    }
    
    private void addDependency(Class<?> type, DependencyType dependencyType) {
        TypeRegistration target = this.typeRegistration.factory.getRegistration(type);
        this.dependencies.put(target, dependencyType);
        target.addDependant(this);
    }
    
    void onDependencyAbleToInstantiate(TypeRegistration dependency) {
        DependencyType type = this.dependencies.get(dependency);
        if (type == null) return;
        
        if (type == DependencyType.INSTANCE && !canInstantiate()) {
            this.numParametersUnresolved--;
            if (this.canInstantiate()) {
                this.typeRegistration.onAbleToInstantiate(this);
                onDependencyResolved(dependency);
            }
        }
    }
    
    void onDependencyResolved(TypeRegistration dependency) {
        boolean canInstantiateBefore = canInstantiate();
        if (this.dependencies.remove(dependency) != null) {
            for (Class<?> parameterType : this.instantiator.getParameterTypes()) {
                if (parameterType == dependency.type) {
                    this.numParametersUnresolved--;
                }
            }
            
            if (isResolved()) {
                this.typeRegistration.onResolved(this);
            } else if (!canInstantiateBefore && canInstantiate()) {
                this.typeRegistration.onAbleToInstantiate(this);
            }
        }
    }
    
    DependencyProvider makeDependencyProvider() {
        DependencyProvider result = new ReflectivelyInstantiatingDependencyProvider(this.typeRegistration.factory.service, this.instantiator, this.injectableFields);
        if (!this.instantiator.isAnnotationPresent(NotCached.class)) {
            result = new LazilyCachedDependencyProvider((StagedDependencyProvider) result);
        }
        return result;
    }
    
    private enum DependencyType {
        FIELD, PARAMETER, INSTANCE
    }
    
}
