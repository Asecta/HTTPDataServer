package io.asecta.core.exception;

public class ApplicationAlreadyRunningException extends RuntimeException {
    
    /**
     *
     */
    private static final long serialVersionUID = 7228088274517030810L;
    
    @Override
    public String getMessage() {
        return "The application has already been launched";
    }
}
