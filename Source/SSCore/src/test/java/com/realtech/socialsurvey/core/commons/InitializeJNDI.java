package com.realtech.socialsurvey.core.commons;

import javax.naming.Context;
import javax.naming.InitialContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

/**
 * Test class to initialize JNDI
 */
public class InitializeJNDI {

	private static final Logger LOG = LoggerFactory.getLogger(InitializeJNDI.class);
	/**
	 * Initializes JNDI for injection
	 * 
	 * @throws Exception
	 */
	public static void initializeJNDIforTest() throws Exception {
		LOG.info("Initializing JNDI");
		System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
		System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming");
		InitialContext ic = new InitialContext();
		ic.createSubcontext("java:");
		ic.createSubcontext("java:/env");
		ic.createSubcontext("java:/env/datasources");
		MysqlConnectionPoolDataSource mysqlds = new MysqlConnectionPoolDataSource();
		mysqlds.setUrl("jdbc:mysql://localhost:3306/ss_user");
		mysqlds.setUser("root");
		mysqlds.setPassword("");
		ic.bind("java:/env/datasources/ss_user", mysqlds);
		LOG.info("JNDI initialized");
	}
}
