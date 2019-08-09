package io.asecta.rest;

import org.eclipse.jetty.server.Server;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import io.asecta.core.dependencyinjection.Initializable;
import io.asecta.core.dependencyinjection.annotations.Bean;
import io.asecta.core.dependencyinjection.annotations.Inject;

public class RestDataServer implements Initializable {
    
    private int port;
    private Server server;
    
    @Inject
    private SessionRouter sessionRouter;
    
    public RestDataServer(int port) {
        this.port = port;
    }
    
    @Override
    public void initialize() {
        server = new Server(port);
        server.setHandler(sessionRouter);
    }
    
    public void startServer() {
        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Bean
    private SessionFactory createSessionFactory() {
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        
        try {
            return configuration.buildSessionFactory();
        } catch (Exception e) {
            System.err.println("Cannot start the database service");
            e.printStackTrace();
            System.exit(0);
        }
        return null;
    }
}
