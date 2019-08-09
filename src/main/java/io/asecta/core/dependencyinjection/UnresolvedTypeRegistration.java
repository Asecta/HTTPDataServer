package io.asecta.core.dependencyinjection;

import java.lang.reflect.*;
import java.util.*;

import io.asecta.core.dependencyinjection.annotations.Bean;
import io.asecta.core.dependencyinjection.annotations.Inject;
import io.asecta.core.dependencyinjection.exceptions.DependencyInjectionException;
import io.asecta.core.utils.Reflect;

final class UnresolvedTypeRegistration extends TypeRegistration {
	private final Set<TypeResolver> resolvers;
	private Collection<TypeResolver> dependants;
	private Field[] fieldCache;
	private boolean canInstantiate;

	UnresolvedTypeRegistration(DependencyServiceFactory factory, Type type) {
		super(factory, type);
		this.resolvers = new HashSet<>();
		this.dependants = new ArrayList<>();
	}

	@SuppressWarnings("rawtypes")
	void addDefaultResolvers() {
		Class<?> clazz = (Class<?>) this.type;

		for (Constructor constructor : Reflect.getAllConstructors(clazz)) {
			if (isValidExecutable(constructor)) {
				addResolver(new TypeResolver(this, constructor));
			}
		}

		List<Method> methods = Reflect.getAnnotatedMethods(clazz, Bean.class);
		Reflect.makeAccessible(methods);
		for (Method method : methods) {
			if (isValidExecutable(method)) {
				TypeRegistration target = factory.getRegistration(method.getReturnType());
				if (target instanceof UnresolvedTypeRegistration) {
					((UnresolvedTypeRegistration) target)
							.addResolver(new TypeResolver((UnresolvedTypeRegistration) target, method));
				}
			} else {
				throw DependencyInjectionException.badInstantiator();
			}
		}
	}

	Field[] getInjectableFields() {
		if (this.fieldCache == null) {
			List<Field> injectableFieldList = Reflect.getAnnotatedFields((Class<?>) this.type, Inject.class);
			Reflect.makeAccessible(injectableFieldList);
			this.fieldCache = injectableFieldList.toArray(new Field[injectableFieldList.size()]);
		}
		return this.fieldCache;
	}

	private static boolean isValidExecutable(Executable executable) {
		/*
		 * if (executable instanceof Method) { if
		 * (!executable.isAnnotationPresent(Bean.class)) { return false; } } else if
		 * (!(executable instanceof Constructor)) { return false; }
		 */

		for (Parameter parameter : executable.getParameters()) {
			if (!parameter.isAnnotationPresent(Inject.class)) {
				return false;
			}
		}

		return true;
	}

	void onAbleToInstantiate(TypeResolver resolver) {
		if (resolver.typeRegistration != this) {
			throw new IllegalArgumentException();
		}

		if (this.canInstantiate) {
			return;
		}

		this.canInstantiate = true;
		if (this.dependants != null) {
			for (TypeResolver dependant : this.dependants) {
				dependant.onDependencyAbleToInstantiate(this);
			}
		}
	}

	void onResolved(TypeResolver resolver) {
		if (resolver.typeRegistration != this) {
			throw new IllegalArgumentException();
		}

		this.canInstantiate = true;
		Collection<TypeResolver> dependants = this.dependants;
		this.dependants = null;

		if (dependants != null) {
			for (TypeResolver dependant : dependants) {
				dependant.onDependencyResolved(this);
			}
		}
	}

	@Override
	void addDependant(TypeResolver dependant) {
		if (this.isResolved()) {
			dependant.onDependencyResolved(this);
		} else {
			this.dependants.add(dependant);
			if (this.canInstantiate()) {
				dependant.onDependencyAbleToInstantiate(this);
			}
		}
	}

	void addResolver(TypeResolver resolver) {
		this.resolvers.add(resolver);
		if (resolver.isResolved()) {
			onResolved(resolver);
		} else if (resolver.canInstantiate()) {
			onAbleToInstantiate(resolver);
		}
	}

	@Override
	boolean canInstantiate() {
		return this.canInstantiate;
	}

	@Override
	boolean isResolved() {
		return this.dependants == null;
	}

	@Override
	DependencyProvider getDependencyProvider() {
		Optional<TypeResolver> main = this.resolvers.stream().filter(TypeResolver::isResolved).sorted().findFirst();
		if (main.isPresent()) {
			return main.get().makeDependencyProvider();
		}
		throw DependencyInjectionException.unresolvedType(this.type);
	}

}
