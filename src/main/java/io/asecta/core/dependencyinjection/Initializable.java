package io.asecta.core.dependencyinjection;

public interface Initializable {
    
    /**
     * Called from OldDependencyService after construction and injection of dependencies.
     */
    void initialize();
}
