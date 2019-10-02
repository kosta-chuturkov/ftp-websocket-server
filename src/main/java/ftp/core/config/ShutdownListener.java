package ftp.core.config;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShutdownListener implements ServletContextListener {

    private final Logger logger = LoggerFactory.getLogger(ShutdownListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        this.logger.info("Context initialized.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        deregisterJdbcDrivers();
    }

    private void deregisterJdbcDrivers() {
        final Enumeration<java.sql.Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            final Driver driver = drivers.nextElement();
            if (this.getClass().getClassLoader().equals(getClass().getClassLoader())) {
                try {
                    DriverManager.deregisterDriver(driver);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    this.logger.error("Deregistered '{}' JDBC driver.");
                } catch (SQLException e) {
                    this.logger.error("Failed to deregister '{}' JDBC driver.");
                }
            }
        }
    }
}