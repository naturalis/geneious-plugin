/**
 * 
 */
package nl.naturalis.lims2.utils;

/**
 * @author Reinier.Kartowikromo
 *
 */

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Lims2Connectie {

	private static final Logger logger = LoggerFactory
			.getLogger(Lims2Connectie.class);
	private LimsImporterUtil limsImporterUtil = new LimsImporterUtil();

	String DB_CONN_STRING = "";
	String DRIVER_CLASS_NAME = "org.gjt.mm.mysql.Driver";
	String USER_NAME = "";
	String PASSWORD = "";

	public Connection getSimpleConnection() {
		// See your driver documentation for the proper format of this string :
		String DB_CONN_STRING = "jdbc:mysql://localhost:3306/geneioustest";
		// Provided by your driver documentation. In this case, a MySql driver
		// is used :
		String DRIVER_CLASS_NAME = "org.gjt.mm.mysql.Driver";
		String USER_NAME = "geneious";
		String PASSWORD = "mypass";

		Connection result = null;
		try {
			Class.forName(DRIVER_CLASS_NAME).newInstance();
		} catch (Exception ex) {
			logger.info("Check classpath. Cannot load db driver: "
					+ DRIVER_CLASS_NAME);
		}

		try {
			result = DriverManager.getConnection(DB_CONN_STRING, USER_NAME,
					PASSWORD);
		} catch (SQLException e) {
			logger.info("Driver loaded, but cannot connect to db: "
					+ DB_CONN_STRING);
		}
		return result;
	}

	public Connection getSimpleConnectionServer(String dbName) {
		// See your driver documentation for the proper format of this string :

		try {
			DB_CONN_STRING = limsImporterUtil.getDatabasePropValues("url")
					+ dbName;
			USER_NAME = limsImporterUtil.getDatabasePropValues("user");
			PASSWORD = limsImporterUtil.getDatabasePropValues("password");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		Connection result = null;
		try {
			Class.forName(DRIVER_CLASS_NAME).newInstance();
		} catch (Exception ex) {
			logger.info("Check classpath. Cannot load db driver: "
					+ DRIVER_CLASS_NAME);
		}

		try {
			result = DriverManager.getConnection(DB_CONN_STRING, USER_NAME,
					PASSWORD);
		} catch (SQLException e) {
			logger.info("Driver loaded, but cannot connect to db: "
					+ DB_CONN_STRING);
		}
		return result;
	}

	public Connection getSimpleConnectionLocalHost() {
		// See your driver documentation for the proper format of this string :
		String DB_CONN_STRING = "jdbc:mysql://localhost:3306/geneioustest";
		// Provided by your driver documentation. In this case, a MySql driver
		// is used :
		String DRIVER_CLASS_NAME = "org.gjt.mm.mysql.Driver";
		String USER_NAME = "geneious";
		String PASSWORD = "mypass";

		Connection result = null;
		try {
			Class.forName(DRIVER_CLASS_NAME).newInstance();
		} catch (Exception ex) {
			logger.info("Check classpath. Cannot load db driver: "
					+ DRIVER_CLASS_NAME);
		}

		try {
			result = DriverManager.getConnection(DB_CONN_STRING, USER_NAME,
					PASSWORD);
		} catch (SQLException e) {
			logger.info("Driver loaded, but cannot connect to db: "
					+ DB_CONN_STRING);
		}
		return result;
	}

}
