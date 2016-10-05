/**
 * 
 */
package nl.naturalis.lims2.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsSQL {

	private static LimsImporterUtil limsImporterUtil = new LimsImporterUtil();
	private static final Logger logger = LoggerFactory.getLogger(LimsSQL.class);

	static final String databaseName = limsImporterUtil
			.getDatabasePropValues("databasename");
	static final String JDBC_DRIVER = limsImporterUtil
			.getDatabasePropValues("jdbcdriver");
	static final String DB_URL = limsImporterUtil.getDatabasePropValues("url")
			+ databaseName;
	static final String user = limsImporterUtil.getDatabasePropValues("user");
	static final String password = limsImporterUtil
			.getDatabasePropValues("password");

	private static Connection conn = null;
	private Statement stmt = null;

	public int importcounter = 1;
	public boolean truefalse = false;
	public String documentname = "";
	public String dummyID = "";

	/*
	 * Create Table tblDocumentImport
	 */
	public void createTableDocumentImport() throws SQLException {

		String sqlCreateTable = "CREATE TABLE tblDocumentImport" + "\n"
				+ "(ID int(11) NOT NULL AUTO_INCREMENT," + "\n"
				+ "Documentname varchar(255) NOT NULL," + "\n"
				+ "Importcount int(11)," + "\n" + "PRIMARY KEY (ID))";

		try {

			// Open the connection
			logger.info("Connecting to a selected database...");
			conn = DriverManager.getConnection(DB_URL, user, password);
			logger.info("Connected database successfully...");

			// Execute a query
			logger.info("Creating the documentImport table...");
			stmt = conn.createStatement();
			stmt.executeUpdate(sqlCreateTable);
			logger.info("Table DocumentImport created succesful...");
		} catch (SQLException se) {
			throw new SQLException(se);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {

			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				throw new RuntimeException(se);
			}// end finally try
		}// end try
	}// end create table

	/*
	 * Create index for column name DocumentName
	 */
	public void createIndexInTableDocumentImport() throws SQLException {
		String sqlCreateIndex = "CREATE INDEX IDXDocumentName" + "\n"
				+ "ON tblDocumentImport(Documentname)";
		try {
			// Open the connection
			logger.info("Connecting to a selected database...");
			conn = DriverManager.getConnection(DB_URL, user, password);
			logger.info("Connected database successfully...");

			// Execute a query
			logger.info("Creating the index for documentImport table...");
			stmt = conn.createStatement();
			stmt.executeUpdate(sqlCreateIndex);
			logger.info("Index DocumentImport created succesful...");
		} catch (SQLException se) {
			throw new SQLException(se);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {

			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				throw new RuntimeException(se);
			}// end finally try
		}// end try
	}

	/* Insert data to the table */
	public void insertIntoTableDocumentImport(String documentname, int count)
			throws SQLException {

		String sqlInsert = "INSERT INTO tblDocumentImport(Documentname, Importcount)"
				+ "\n" + "VALUES('" + documentname + "','" + count + "')";
		try {
			// Open the connection
			conn = DriverManager.getConnection(DB_URL, user, password);

			// Execute a query
			conn.setAutoCommit(true);
			stmt = conn.createStatement();
			stmt.executeUpdate(sqlInsert);
			// conn.commit();
			logger.info("Record inserted succesfull into the table...");
		} catch (SQLException e) {
			// conn.rollback();
			throw new RuntimeException(e);
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			}// do nothing
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}// end finally try
		}// end try
	}

	/* Check if document exists in the table */
	public boolean documentNameExist(String documentName) {

		try {
			// Open a connection
			conn = DriverManager.getConnection(DB_URL, user, password);

			// Execute a query
			stmt = conn.createStatement();

			String sql = "SELECT Documentname, Importcount FROM tblDocumentImport"
					+ "\n" + "WHERE Documentname =  '" + documentName + "' ";
			ResultSet rs = stmt.executeQuery(sql);
			// Extract data from result set
			while (rs.next()) {
				// Retrieve by column name
				documentname = rs.getString("documentName");
				importcounter = rs.getInt("Importcount");

				if (documentname.isEmpty())
					truefalse = false;
				else
					truefalse = true;

				/*
				 * if (rs.getInt("Importcount") == 0 || rs.getInt("Importcount")
				 * > 0) importcounter++;
				 */
			}
			rs.close();
		} catch (SQLException se) {
			// Handle errors for JDBC
			throw new RuntimeException(se);
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			}// do nothing
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				throw new RuntimeException(se);
			}// end finally try
		}// end try
		return truefalse;
	}

	/* Check if document exists in the table */
	public String getdocumentName(String documentName) {

		try {
			// Open a connection
			conn = DriverManager.getConnection(DB_URL, user, password);

			// Execute a query
			stmt = conn.createStatement();

			String sql = "SELECT ID, Documentname FROM tblDocumentImport"
					+ "\n" + "WHERE Documentname like  '%" + documentName
					+ "%' ";
			ResultSet rs = stmt.executeQuery(sql);
			// Extract data from result set
			while (rs.next()) {
				// Retrieve by column name
				dummyID = rs.getString("ID");
				documentname = rs.getString("documentName");
			}
			rs.close();
		} catch (SQLException se) {
			// Handle errors for JDBC
			throw new RuntimeException(se);
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			}// do nothing
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				throw new RuntimeException(se);
			}// end finally try
		}// end try
		return documentname;
	}

	public void updateImportCount(int cnt, String docName) {

		try {
			// Open a connection
			conn = DriverManager.getConnection(DB_URL, user, password);

			// Execute a query
			stmt = conn.createStatement();

			String sqlUpdate = "UPDATE tblDocumentImport" + "\n"
					+ "SET importcount =  '" + cnt + "' " + "\n"
					+ "WHERE Documentname =  '" + docName + "' ";
			stmt.executeUpdate(sqlUpdate);

		} catch (SQLException se) {
			// Handle errors for JDBC
			throw new RuntimeException(se);
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			}// do nothing
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				throw new RuntimeException(se);
			}// end finally try
		}// end try
	}

	/* Check if table exists in the database */
	public boolean tableExist(String tableName) throws SQLException {
		boolean bExists = false;
		// Open the connection
		conn = DriverManager.getConnection(DB_URL, user, password);
		try (ResultSet rs = conn.getMetaData().getTables(null, null, tableName,
				null)) {
			while (rs.next()) {
				String tName = rs.getString("TABLE_NAME").toLowerCase();
				if (tName != null && tName.equals(tableName.toLowerCase())) {
					bExists = true;
					break;
				}
			}
		}
		return bExists;
	}

}
