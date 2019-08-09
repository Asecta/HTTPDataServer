package io.asecta.rest.repository;

import org.hibernate.Session;

import java.util.stream.Stream;

@FunctionalInterface
public interface RepoQuery<T> {
    public Stream<T> execute(Session session) throws Exception;
}