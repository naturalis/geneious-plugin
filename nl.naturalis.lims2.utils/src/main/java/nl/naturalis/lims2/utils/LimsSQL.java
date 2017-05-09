package nl.naturalis.lims2.utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsSQL {

	private static LimsImporterUtil limsImporterUtil = new LimsImporterUtil();
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
	public String documentname;
	public String dummyID;
	public String dummyName;

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

	/* Use in Class LimsimportAB1 */
	public String getIDFromTableAnnotatedDocument(Object filename,
			String xmlNotesName) throws IOException {

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String result = "";

		try {
			final String SQL = " SELECT ID, TRIM(EXTRACTVALUE(document_xml, '"
					+ xmlNotesName + "')) AS name "
					+ " FROM annotated_document"
					+ " WHERE document_xml like  '%<cache_name>" + filename
					+ "</cache_name>%' " + "\n" + " LIMIT 1";

			conn = DriverManager.getConnection(DB_URL, user, password);
			conn.clearWarnings();
			pst = conn.prepareStatement(SQL);
			rs = pst.executeQuery();

			dummyName = "";
			if (rs.next()) {
				do {
					result = rs.getObject(1).toString();
					dummyID = rs.getString("ID");
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

	/* Use in LimsImportSamples */
	public String getDocumentCacheName(Object fileName, String xmlNotesName)
			throws IOException {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String result = "";

		try {
			final String SQL = "SELECT TRIM(EXTRACTVALUE(document_xml,  '"
					+ xmlNotesName + "')) AS CacheName" + "\n"
					+ " FROM annotated_document" + "\n "
					+ " WHERE document_xml like  '%<cache_name>" + fileName
					+ "%' " + "\n" + " LIMIT 1";

			conn = DriverManager.getConnection(DB_URL, user, password);
			conn.clearWarnings();
			pst = conn.prepareStatement(SQL);
			rs = pst.executeQuery();

			if (rs.next()) {
				do {
					result = rs.getObject(1).toString();
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

	/* Use in LimsImportSamples */
	public String getDocumentOverrideCacheName(Object fileName,
			String xmlNotesName) throws IOException {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String result = "";

		try {

			final String SQL = "SELECT TRIM(EXTRACTVALUE(document_xml,  '"
					+ xmlNotesName
					+ "')) AS CacheName"
					+ "\n"
					+ " FROM annotated_document"
					+ "\n "
					+ " WHERE document_xml like  '%<override_cache_name>Reads Assembly "
					+ fileName + "%' " + "\n" + " LIMIT 1";

			conn = DriverManager.getConnection(DB_URL, user, password);
			conn.clearWarnings();
			pst = conn.prepareStatement(SQL);
			rs = pst.executeQuery();

			if (rs.next()) {
				do {
					result = rs.getObject(1).toString();
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