package io.asecta.service.config;

import java.util.Properties;

import org.hibernate.cfg.Configuration;

public class DatabaseConfig {
    private String url = "jdbc:mysql://localhost/test";
    private String username = "root";
    private String password = "";
    private Dialect dialect = Dialect.PostgreSQL9;
    private boolean debug = false;

    public DatabaseConfig() {
    }

    public DatabaseConfig(String url, String username, String password, Dialect dialect) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.dialect = dialect;
    }

    public Dialect getDialect() {
        return dialect;
    }

    public boolean isDebugEnabled() {
        return debug;
    }

    public void setDebugEnabled(boolean debug) {
        this.debug = debug;
    }

    public void apply(Configuration config) {
        Properties properties = new Properties();
        properties.setProperty("hibernate.connection.driver_class", dialect.getDriverClass());
        properties.setProperty("hibernate.dialect", dialect.getDialect());
        properties.setProperty("hibernate.connection.url", url);
        properties.setProperty("hibernate.connection.username", username);
        properties.setProperty("hibernate.connection.password", password);
        // TODO add properties based on debug flag
        config.addProperties(properties);
    }

    public enum Dialect {
        MariaDB53,
        MariaDB,
        MySQL5,
        MySQL55,
        MySQL57,
        MySQL,
        Oracle8i,
        Oracle9i,
        Oracle10g,
        Oracle12c,
        PostgreSQL9,
        PostgreSQL81,
        PostgreSQL82,
        PostgreSQL91,
        PostgreSQL92,
        PostgreSQL93,
        PostgreSQL94,
        PostgreSQL95,
        SQLServer2005,
        SQLServer2008,
        SQLServer2012,
        SQLServer,
        H2,
        HSQL,;

        private String dialectValue = "org.hibernate.dialect." + name() + "Dialect";

        public String getDialect() {
            return dialectValue;
        }

        public String getDriverClass() {
            //TODO
            return "com.mysql.jdbc.Driver";
        }

    }
}
