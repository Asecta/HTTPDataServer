package io.asecta.rest.authentication;

public interface Protected {
    public boolean canAccess(Accessor accessor);
}
