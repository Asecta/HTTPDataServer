package io.asecta.core.dependencyinjection;

import java.lang.reflect.*;

import io.asecta.core.dependencyinjection.exceptions.DependencyInjectionException;

final class ReflectivelyInstantiatingDependencyProvider extends StagedDependencyProvider {
    private DependencyService dependencyService;
    private Executable instantiator;
    private Field[] fields;
    private Class<?>[] parameters;
    
    ReflectivelyInstantiatingDependencyProvider(DependencyService dependencyService, Executable instantiator, Field[] fields) {
        this.dependencyService = dependencyService;
        this.instantiator = instantiator;
        this.fields = fields;
        this.parameters = instantiator.getParameterTypes();
    }
    
    @SuppressWarnings("rawtypes")
	@Override
    Object instantiate() {
        DependencyService dependencyService = this.dependencyService;
        Executable instantiator = this.instantiator;
        
        Object result;
        try {
            Class<?>[] parameters = this.parameters;
            Object[] arguments = new Object[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                Class<?> parameter = parameters[i];
                arguments[i] = dependencyService.getComponent(parameter);
            }
            
            if (instantiator instanceof Method) {
                Method method = (Method) instantiator;
                Object instance;
                if (Modifier.isStatic(method.getModifiers())) {
                    instance = null;
                } else {
                    instance = dependencyService.getComponent(method.getDeclaringClass());
                }
                
                result = method.invoke(instance, arguments);
            } else if (instantiator instanceof Constructor) {
                result = ((Constructor) instantiator).newInstance(arguments);
            } else {
                throw new IllegalStateException();
            }
            
        } catch (InvocationTargetException e) {
            Throwable target = e.getTargetException();
            throw target instanceof RuntimeException ? (RuntimeException) target : new DependencyInjectionException("Instantiation failure", target);
        } catch (ReflectiveOperationException e) {
            throw new DependencyInjectionException("Instantiation failure", e);
        }
        
        return result;
    }
    
    @Override
    void postInstantiate(Object instance) {
        try {
            for (Field field : this.fields) {
                field.set(instance, dependencyService.getComponent(field.getType()));
            }
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
        
        /*
        if (this.instantiator instanceof Method) {
            Class<?> returnType = instance.getClass();
            
        }
        */
        
        DependencyService.onObjectInstantiated(instance);
    }
    
}
