package io.asecta.core;

import io.asecta.core.dependencyinjection.DependencyService;
import io.asecta.core.exception.ApplicationAlreadyRunningException;

public final class ApplicationService<T> {
	
    private static ApplicationService<?> applicationHolder;
    private Class<T> applicationClass;
    private DependencyService dependencyService;
    private T applicationInstance;
    
    private ApplicationService(Class<T> clazz) {
        this.applicationClass = clazz;
        dependencyService = DependencyService.create();
        initialize();
    }
    
    public static <T> void launch(Class<T> clazz) {
        if (applicationHolder != null) {
            throw new ApplicationAlreadyRunningException();
        }
        applicationHolder = new ApplicationService<T>(clazz);
    }
    
    private void initialize() {
        applicationInstance = dependencyService.resolve(applicationClass);
    }
    
    public T getApplication() {
        return applicationInstance;
    }
}
