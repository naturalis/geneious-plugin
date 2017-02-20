/**
 * 
 */
package nl.naturalis.lims2.utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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

	public int importcounter = 1;
	public boolean truefalse = false;
	public String documentname = "";
	public String dummyID = "";
	public String dummyName = "";

	/*
	 * Create Table tblDocumentImport
	 */
	public void createTableDocumentImport() throws SQLException {
		Connection conn = null;
		Statement stmt = null;
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
		Connection conn = null;
		Statement stmt = null;
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
		Connection conn = null;
		Statement stmt = null;
		String sqlInsert = "INSERT INTO tblDocumentImport(Documentname, Importcount)"
				+ "\n" + "VALUES('" + documentname + "','" + count + "')";
		try {
			// Open the connection
			conn = DriverManager.getConnection(DB_URL, user, password);

			// Execute a query
			// conn.setAutoCommit(true);
			stmt = conn.createStatement();
			stmt.executeUpdate(sqlInsert);
			// conn.commit();
			logger.debug("Record inserted succesfull into the table...");
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
				throw new RuntimeException(se);
			}// end finally try
		}// end try
	}

	public List<LimsDocumentName> getDocumentNameForAB1Fasta() {
		Connection conn = null;
		Statement stmt = null;
		try {
			// Open a connection
			conn = DriverManager.getConnection(DB_URL, user, password);

			// Execute a query
			stmt = conn.createStatement();

			String sql = "SELECT Documentname FROM tblDocumentImport";

			ResultSet rs = stmt.executeQuery(sql);
			List<LimsDocumentName> documentNames = new ArrayList<>(100);
			if (rs.next()) {
				do {
					LimsDocumentName docName = new LimsDocumentName();
					docName.setDocumentFileName(rs.getString("Documentname"));
				} while (rs.next());
			}
			rs.close();
			return documentNames;
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

	/* Check if document exists in the table */
	public boolean documentNameExist(String documentName) {
		Connection conn = null;
		Statement stmt = null;
		boolean exists = false;
		try {
			// Open a connection
			conn = DriverManager.getConnection(DB_URL, user, password);

			// Execute a query
			stmt = conn.createStatement();

			String sql = "SELECT * FROM tblDocumentImport" + "\n"
					+ "WHERE Documentname =  '" + documentName + "'" + "\n";
			// + "ORDER BY Documentname DESC ";
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				do {
					int cnt = rs.getInt(1);
					if (cnt == 0)
						exists = false;
					else
						exists = true;
					importcounter = rs.getInt("Importcount");
					documentname = rs.getString("Documentname");

				} while (rs.next());
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
		return exists;
	}

	public int getVersionFromDocumentName(String documentName) {
		Connection conn = null;
		Statement stmt = null;
		int result = 0;
		try {
			// Open a connection
			conn = DriverManager.getConnection(DB_URL, user, password);

			// Execute a query
			stmt = conn.createStatement();

			String sql = "SELECT Importcount as version FROM tblDocumentImport"
					+ "\n" + "WHERE Documentname =  '" + documentName + "'";
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				do {
					result = rs.getInt(1);
					importcounter = result;
				} while (rs.next());
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
		return result;
	}

	/* Check if document exists in the table */
	public String getdocumentName(String documentName) {
		Connection conn = null;
		Statement stmt = null;
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
		Connection conn = null;
		Statement stmt = null;
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
		Connection conn = null;
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
			if (conn != null) {
				conn.close();
			}
		}

		return bExists;
	}

	public void DeleteDummyRecordFromTableAnnotatedtDocument(Object docName)
			throws IOException {
		Connection conn = null;
		Statement stmt = null;
		try {
			// Open a connection
			conn = DriverManager.getConnection(DB_URL, user, password);

			// Execute a query
			stmt = conn.createStatement();

			String sqlUpdate = "DELETE FROM tblDocumentImport" + "\n"
					+ "WHERE Documentname =  '" + docName + "' ";
			stmt.executeUpdate(sqlUpdate);
			logger.info("Dummy record: "
					+ docName
					+ " has been deleted from table tblDocumentImport (Naturalis tussen tabel)");

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

	public boolean checkIfSampleDocExistsInTableAnnotatedDocument(
			Object filename) throws IOException {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean result = false;

		try {
			final String SQL = "SELECT TRIM(EXTRACTVALUE(document_xml,  '//document/hiddenFields/cache_name')) AS name"
					+ "\n"
					+ "FROM annotated_document"
					+ "\n"
					+ " WHERE document_xml like '%"
					+ filename
					+ "%' "
					+ "ORDER BY ID DESC" + "\n" + " LIMIT 1";

			conn = DriverManager.getConnection(DB_URL, user, password);

			conn.clearWarnings();
			pst = conn.prepareStatement(SQL);
			rs = pst.executeQuery();
			if (rs.next()) {
				do {
					String name = rs.getString(1);
					if (name != null) {
						result = true;
						break;
					}
				} while (rs.next());
			}
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}
				if (conn != null) {
					conn.close();
				}

			} catch (SQLException ex) {
				throw new RuntimeException(ex);
			}
		}
		return result;
	}

	public boolean getImportDummyDocument(Object filename) throws IOException {

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean truefalse = false;

		try {

			final String SQL = "SELECT ID" + "\n" + "FROM annotated_document"
					+ "\n" + "WHERE document_xml like  '%" + filename + "%' "
					+ "\n" + "ORDER BY ID DESC" + "\n" + "LIMIT 1";
			try {
				conn = DriverManager.getConnection(DB_URL, user, password);

				conn.clearWarnings();
			} catch (SQLException e) {
				logger.warn("Cannot connect the database!", e);
			}

			pst = conn.prepareStatement(SQL);

			rs = pst.executeQuery();
			if (rs.next()) {
				do {
					int cnt = rs.getInt(1);
					if (cnt == 0)
						truefalse = false;
					else
						truefalse = true;

				} while (rs.next());
			}
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}
				if (conn != null) {
					conn.close();
				}

			} catch (SQLException ex) {
				throw new RuntimeException(ex);
			}
		}
		return truefalse;
	}

	public String getIDFromTableAnnotatedDocument(Object filename,
			String xmlNotesName) throws IOException {

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String result = "";

		try {

			final String SQL = " SELECT DISTINCT a.id, a.name" + " FROM "
					+ " ( "
					+ " SELECT id as ID, TRIM(EXTRACTVALUE(document_xml,  ' "
					+ xmlNotesName + " ')) AS name "
					+ " FROM annotated_document" + " ) AS a "
					+ " WHERE a.name =?" + "\n" + " LIMIT 1";

			conn = DriverManager.getConnection(DB_URL, user, password);
			conn.clearWarnings();
			pst = conn.prepareStatement(SQL);
			pst.setString(1, (String) filename);
			rs = pst.executeQuery();

			dummyName = "";
			if (rs.next()) {
				do {
					result = rs.getObject(1).toString();
					dummyName = rs.getObject(2).toString();
				} while (rs.next());
			}
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}
				if (conn != null) {
					conn.close();
				}

			} catch (SQLException ex) {
				throw new RuntimeException(ex);
			}
		}
		return result;
	}

}
