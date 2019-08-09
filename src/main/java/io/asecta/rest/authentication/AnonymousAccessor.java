package io.asecta.rest.authentication;

public class AnonymousAccessor extends UserAccessor {
    
    public AnonymousAccessor() {
        super("ANONYMOUS", "ANONYMOUS");
    }
    
    @Override
    public boolean isAnonymous() {
        return true;
    }

	@Override
	public int getId() {
		return -1; 
	}
}
