package ftp.core.config;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

public class ShutdownListener implements ServletContextListener {
	private final Logger logger = Logger.getLogger(ShutdownListener.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		logger.info("Context initialized.");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		deregisterJdbcDrivers();
		// more clean-up tasks here
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
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					logger.error("Deregistered '{}' JDBC driver.");
				} catch (SQLException e) {
					logger.error("Failed to deregister '{}' JDBC driver.");
				}
			}
		}
	}
}