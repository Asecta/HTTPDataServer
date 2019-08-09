package io.asecta.core.dependencyinjection;

import java.lang.reflect.Type;
import java.util.Map;

import io.asecta.core.dependencyinjection.exceptions.DependencyInjectionException;

public final class DependencyService {
	DependencyServiceFactory factory;

	// set by the factory
	Map<Type, DependencyProvider> dependencyCache;

	DependencyService(DependencyServiceFactory factory) {
		this.factory = factory;
	}

	public void uncacheFactory() {
		this.factory = null;
	}

	public static DependencyService create() {
		return new DependencyService(null);
	}

	public static <T> T start(Class<T> applicationClass) {
		DependencyServiceFactory factory = new DependencyServiceFactory();
		factory.tryResolveType(applicationClass);
		return factory.getDependencyService().getComponent(applicationClass);
	}

	public <T> T resolve(Class<T> clazz) {
		return getComponent(clazz);
	}

	@SuppressWarnings("unchecked")
	<T> T getComponent(Class<T> clazz) {
		DependencyProvider provider;

		if (dependencyCache != null) {
			provider = dependencyCache.get(clazz);
			if (provider != null) {
				// noinspection unchecked
				return (T) provider.get();
			}
		}

		getFactory().tryResolveType(clazz);

		provider = dependencyCache.get(clazz);
		if (provider != null) {
			// noinspection unchecked
			return (T) provider.get();
		}

		throw DependencyInjectionException.unresolvedType(clazz);
	}

	DependencyServiceFactory getFactory() {
		if (this.factory == null) {
			this.factory = new DependencyServiceFactory(this);
		}
		return this.factory;
	}

	static void onObjectInstantiated(Object object) {
		if (object instanceof Initializable) {
			((Initializable) object).initialize();
		}
	}

}
