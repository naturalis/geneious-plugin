/**
 * 
 */
package nl.naturalis.lims2.utils;

import java.awt.EventQueue;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biomatters.geneious.publicapi.components.Dialogs;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentNote;
import com.biomatters.geneious.publicapi.documents.DocumentNoteType;
import com.biomatters.geneious.publicapi.documents.DocumentNoteUtilities;
import com.biomatters.geneious.publicapi.plugin.PluginUtilities;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsReadGeneiousFieldsValues {

	private String url = "";
	private String user = "";
	private String password = "";
	private String ssl = "";
	public String dummyName = "";
	private final Logger logger = LoggerFactory
			.getLogger(LimsReadGeneiousFieldsValues.class);
	private LimsImporterUtil limsImporterUtil = new LimsImporterUtil();
	private LimsLogList limsLogList = new LimsLogList();
	private String dummyPcrPlateIdSeqValue = "";
	private String dummyMarkerSeqValue = "";
	private String dummyRegistrNmbrSamplesValue = "";
	private String dummyScientificNameSamplesValue = "";
	private String dummySamplePlateIdSamplesValue = "";
	private String dummyPositionSamplesValue = "";
	private String dummyExtractIDSamplesValue = "";
	private String dummySeqStaffSamplesValue = "";
	private List<String> listDummyValues = new ArrayList<String>();
	public String activeDB = "";
	public String extractidSamplesFromDummy;
	public String samplePlateIdSamplesFromDummy;
	public String scientificNameSamplesFromDummy;
	public String registrnmbrSamplesFromDummy;
	public String positionSamplesFromDummy;
	public String extractPlateIDSamples;
	public String extractionMethodSamples;
	public String registrationScientificName;
	private SQLException exception = null;
	public int recordcount = 0;

	private Connection con = null;
	private PreparedStatement pst = null;
	private ResultSet rs = null;

	private LimsReadGeneiousFieldsValues instance;

	public LimsReadGeneiousFieldsValues getInstance() {
		if (instance == null)
			instance = new LimsReadGeneiousFieldsValues();
		return instance;
	}

	public LimsReadGeneiousFieldsValues() {

		if (url.toString().length() == 0) {
			ssl = limsImporterUtil.getDatabasePropValues("ssl");
			url = limsImporterUtil.getDatabasePropValues("url");
			user = limsImporterUtil.getDatabasePropValues("user");
			password = limsImporterUtil.getDatabasePropValues("password");
		}

	}

	/*
	 * file Return Boolean value = true or false Used in LimsImportAB1Update and
	 * (Depricated)LimsReaddataFromSamples
	 */
	public boolean getValueFromAnnotatedPluginDocument(
			AnnotatedPluginDocument annotatedPluginDocuments, String noteCode,
			Object fieldName) {

		/** noteCode = "DocumentNoteUtilities-Registration number"; */
		DocumentNoteType noteType = DocumentNoteUtilities.getNoteType(noteCode);
		// Object fieldValue = "";
		boolean truefalse = false;

		if (noteType != null) {
			AnnotatedPluginDocument.DocumentNotes documentNotes = annotatedPluginDocuments
					.getDocumentNotes(true);

			DocumentNote bos = documentNotes.getNote(noteCode);
			/** example: FieldName = "BasisOfRecordCode" */
			if (bos != null) {
				truefalse = true;
			} else {
				truefalse = false;
			}
		}
		return truefalse;
	}

	/**
	 * Get value from Notes from a selected Document file Return a string
	 * version value;
	 * 
	 * Used in LimsImportSamples and (Depricated)LimsReaddataFromSamples
	 * 
	 * @param annotatedPluginDocuments
	 *            Documents
	 * @param noteCode
	 *            Add Field Code
	 * @param fieldName
	 *            add field name
	 * @param i
	 *            Document count
	 * @return Return a string value
	 * @see String
	 */
	public String getVersionValueFromAnnotatedPluginDocument(
			AnnotatedPluginDocument[] annotatedPluginDocuments,
			String noteCode, String fieldName, int i) {

		/** noteCode = "DocumentNoteUtilities-Registration number"; */
		DocumentNoteType noteType = DocumentNoteUtilities.getNoteType(noteCode);
		String fieldValue = null;

		if (noteType != null) {
			AnnotatedPluginDocument.DocumentNotes documentNotes = annotatedPluginDocuments[i]
					.getDocumentNotes(true);

			DocumentNote bos = documentNotes.getNote(noteCode);
			/** example: FieldName = "BasisOfRecordCode" */
			if (bos != null) {
				fieldValue = (String) bos.getFieldValue(fieldName);
			} else {
				fieldValue = "0";
			}
		}

		return fieldValue;
	}

	private HashSet<String> fastaAb1Cache = new HashSet<>(100);

	/**
	 * Check if Fasts filename exists in the Database Used in LimsImportAB1 and
	 * LimsImportAB1Update
	 * 
	 * @param fileName
	 *            Set a filename
	 * @param fieldName
	 *            Set the field name
	 * @param xmlnotes
	 *            Set the xml string
	 * @return Return boolean value true or false
	 * @see boolean
	 * */
	public boolean checkOfFastaOrAB1Exists(String fileName, String fieldName,
			String xmlnotes) {

		if (fastaAb1Cache.contains(fileName + '|' + fieldName))
			return true;

		boolean truefalse = false;

		String result = "";

		try {

			final String SQL = "SELECT ID FROM annotated_document" + "\n"
					+ "WHERE document_xml like  '%" + fileName + "%' " + "\n"
					+ "ORDER BY ID DESC" + "\n" + "LIMIT 1";

			/*
			 * final String SQL = " SELECT a.name" + " FROM " + " ( " +
			 * " SELECT	TRIM(EXTRACTVALUE(" + fieldName + ", ' " + xmlnotes +
			 * " ')) AS name " + " FROM annotated_document" + " ) AS a " +
			 * " WHERE a.name =?" + "\n" + " LIMIT 1";
			 */

			con = DriverManager.getConnection(url + activeDB + ssl, user,
					password);
			con.clearWarnings();
			pst = con.prepareStatement(SQL);
			// pst.setString(1, fileName);
			rs = pst.executeQuery();

			if (rs.next()) {
				do {
					result = rs.getObject(1).toString();
					if (rs.wasNull())
						truefalse = false;
					else
						truefalse = true;
					limsLogList.UitvalList.add("Filename: " + result
							+ " already exists in the geneious database."
							+ "\n");
				} while (rs.next());
			}
		} catch (SQLException ex) {
			logger.info(ex.getMessage(), ex);
			exception = ex;

			EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					Dialogs.showMessageDialog("Check of fasta/Ab1 file exists: "
							+ exception.getMessage());
				}
			});

		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException ex) {
				logger.warn(ex.getMessage(), ex);
			}
		}
		if (truefalse)
			fastaAb1Cache.add(fileName + '|' + fieldName);
		return truefalse;
	}

	/**
	 * Check if AB1 filename exists in the Database Used in LimsImportAB1 and
	 * LimsImportAB1Update
	 * 
	 * @param filename
	 *            Set parameter fielname
	 * @return Return boolean value true or false
	 * @see boolean
	 * */
	public boolean fileNameExistsInGeneiousDatabase(String filename) {

		boolean truefalse = false;
		/* String result = ""; */

		try {

			/*
			 * final String SQL =
			 * "SELECT SQL_CALC_FOUND_ROWS(count(a.name)) as count" + " FROM " +
			 * " ( " +
			 * " SELECT	TRIM(EXTRACTVALUE(UNCOMPRESS(plugin_document_xml), '//ABIDocument/name')) AS name "
			 * + " FROM annotated_document" + " ) AS a " +
			 * " WHERE UNCOMPRESS(a.name) = ?";
			 */

			/*
			 * final String SQL =
			 * "SELECT ExtractValue(plugin_document_xml, '//ABIDocument/name') as name, Count(*) As Count"
			 * + " FROM annotated_document" +
			 * " WHERE ExtractValue(plugin_document_xml,'//ABIDocument/name')= ?"
			 * + " AND match (plugin_document_xml) against (" + "'" + filename +
			 * "'" + ")";
			 */

			final String SQL = "SELECT ID FROM annotated_document" + "\n"
					+ "WHERE document_xml like  '%" + filename + "%' " + "\n"
					+ "ORDER BY ID DESC" + "\n" + "LIMIT 1";

			con = DriverManager.getConnection(url + activeDB + ssl, user,
					password);
			con.clearWarnings();
			pst = con.prepareStatement(SQL);
			// pst.setString(1, filename);
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
			/*
			 * while (rs.next()) { // result = rs.getString("name"); recordcount
			 * = rs.getInt("count");
			 * 
			 * if (rs.wasNull()) truefalse = false; else truefalse = true; }
			 */} catch (SQLException ex) {
			logger.info(ex.getMessage(), ex);
			exception = ex;

			EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					Dialogs.showMessageDialog("SQL Exception Information: "
							+ exception.getMessage());
				}
			});

		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (SQLException ex) {
				logger.warn(ex.getMessage(), ex);
			}
		}
		return truefalse;
	}

	/**
	 * Get Name from Samples files in the database Used LimsImportSamples and
	 * LimsReadDataFromSamples
	 * 
	 * @param extractid
	 *            Set parameter ExtractID
	 * @return Return String value
	 * @throws IOException
	 *             Throws a exception message
	 * @see String
	 * */
	public String getFastaIDForSamples_GeneiousDB(String extractid)
			throws IOException {

		String result = "";
		try {

			final String SQL = " SELECT DISTINCT a.name, COUNT(*) as Count"
					+ " FROM "
					+ " ( "
					+ " SELECT DISTINCT	TRIM(EXTRACTVALUE(document_xml, ' //document/hiddenFields/cache_name')) AS name "
					+ " FROM annotated_document" + " ) AS a "
					+ " WHERE a.name like ?" + "\n" + "LIMIT 1";
			// final String SQL = " SELECT DISTINCT a.name, COUNT(*) as Count"
			// + " FROM "
			// + " ( "
			// +
			// " SELECT DISTINCT	TRIM(EXTRACTVALUE(plugin_document_xml, '//XMLSerialisableRootElement/name')) AS name "
			// + " FROM annotated_document" + " ) AS a "
			// + " WHERE a.name like ?" + "\n" + "LIMIT 1";

			con = DriverManager.getConnection(url + activeDB + ssl, user,
					password);
			con.clearWarnings();
			pst = con.prepareStatement(SQL);
			pst.setString(1, "%" + extractid + "%");
			rs = pst.executeQuery();
			if (rs.next()) {
				do {
					result = rs.getString("name"); // .getObject(1).toString();
					logger.info("Extract ID: " + result
							+ " already exsist in the geneious database.");
				} while (rs.next());
			}

		} catch (SQLException ex) {
			logger.info(ex.getMessage(), ex);

		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException ex) {
				logger.warn(ex.getMessage(), ex);
			}
		}
		return result;
	}

	/**
	 * Get Cache name from XML field document_xml table annotated_document
	 * //document/hiddenFields/override_cache_name Used in LimsImportAB1 and
	 * LimsReadDataFromBold
	 * 
	 * @param filename
	 *            Set param Filename
	 * @param xmlNotesName
	 *            Set the xml note tag
	 * @return Return cachename
	 * @throws IOException
	 *             Throws a exception message
	 * @see String
	 * */
	public String getCacheNameFromGeneiousDatabase(Object filename,
			String xmlNotesName) throws IOException {

		String result = "";

		try {

			final String SQL = " SELECT a.name" + " FROM " + " ( "
					+ " SELECT	TRIM(EXTRACTVALUE(document_xml,  ' "
					+ xmlNotesName + " ')) AS name "
					+ " FROM annotated_document" + " ) AS a "
					+ " WHERE a.name =?" + "\n" + " LIMIT 1 ";
			try {
				con = DriverManager.getConnection(url + activeDB + ssl, user,
						password);
				con.clearWarnings();
			} catch (SQLException e) {
				logger.warn("Cannot connect the database!", e);
			}

			pst = con.prepareStatement(SQL);
			pst.setString(1, (String) filename);
			rs = pst.executeQuery();
			if (rs.next()) {
				do {
					result = rs.getObject(1).toString();
					logger.debug("Filename: " + result
							+ " already exists in the geneious database.");
					limsLogList.UitvalList.add("Filename: " + result
							+ " already exists in the geneious database."
							+ "\n");
				} while (rs.next());
			}
		} catch (SQLException ex) {
			logger.info(ex.getMessage(), ex);
			exception = ex;

			EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					Dialogs.showMessageDialog("Get cachename from DB: "
							+ exception.getMessage());
				}
			});

		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException ex) {
				logger.warn(ex.getMessage(), ex);
			}
		}
		return result;
	}

	public boolean getImportDummyDocument(Object filename) throws IOException {

		boolean truefalse = false;

		try {

			final String SQL = "SELECT ID" + "\n" + "FROM annotated_document"
					+ "\n" + "WHERE document_xml like  '%" + filename + "%' "
					+ "\n" + "ORDER BY ID DESC" + "\n" + "LIMIT 1";
			try {
				con = DriverManager.getConnection(url + activeDB + ssl, user,
						password);
				con.clearWarnings();
			} catch (SQLException e) {
				logger.warn("Cannot connect the database!", e);
			}

			pst = con.prepareStatement(SQL);

			rs = pst.executeQuery();
			if (rs.next()) {
				do {
					int cnt = rs.getInt(1);
					if (cnt == 0)
						truefalse = false;
					else
						truefalse = true;

					limsLogList.UitvalList.add("Filename: " + filename
							+ " already exists in the geneious database."
							+ "\n");
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
				if (con != null) {
					con.close();
				}

			} catch (SQLException ex) {
				throw new RuntimeException(ex);
			}
		}
		return truefalse;
	}

	public int checkIfSampleDocExistsInTableAnnotatedDocument(Object filename,
			String xmlNotesName) throws IOException {

		int result = 0;

		try {

			final String SQL = " SELECT EXISTS(SELECT 1 FROM " + " ( "
					+ " SELECT id as ID, TRIM(EXTRACTVALUE(document_xml,  ' "
					+ xmlNotesName + " ')) AS name "
					+ " FROM annotated_document" + " ) AS a "
					+ " WHERE a.name like '%" + filename + "%' " + ") As Count"
					+ "\n" + " LIMIT 1";

			con = DriverManager.getConnection(url + activeDB + ssl, user,
					password);
			con.clearWarnings();
			pst = con.prepareStatement(SQL);
			// pst.setString(1, (String) filename);
			rs = pst.executeQuery();
			if (rs.next()) {
				do {
					result = rs.getInt(1);
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
				if (con != null) {
					con.close();
				}

			} catch (SQLException ex) {
				throw new RuntimeException(ex);
			}
		}
		return result;
	}

	public int checkIfDocExistsInTableAnnotatedDocument(Object filename,
			String xmlNotesName) throws IOException {

		int result = 0;

		try {

			final String SQL = " SELECT EXISTS(SELECT 1 FROM " + " ( "
					+ " SELECT id as ID, TRIM(EXTRACTVALUE(document_xml,  ' "
					+ xmlNotesName + " ')) AS name "
					+ " FROM annotated_document" + " ) AS a "
					+ " WHERE a.name =?" + ") As Count" + "\n" + " LIMIT 1";

			con = DriverManager.getConnection(url + activeDB + ssl, user,
					password);
			con.clearWarnings();
			pst = con.prepareStatement(SQL);
			pst.setString(1, (String) filename);
			rs = pst.executeQuery();
			if (rs.next()) {
				do {
					result = rs.getInt(1);
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
				if (con != null) {
					con.close();
				}

			} catch (SQLException ex) {
				throw new RuntimeException(ex);
			}
		}
		return result;
	}

	/**
	 * Get the ID and Name from a file(Parameter fileName) in the Database Used
	 * in LimsImportAB1
	 * 
	 * @param filename
	 *            Set parameter filename
	 * @param xmlNotesName
	 *            Set String XmlNotes Tag
	 * @return Return ID value from the database
	 * @throws IOException
	 *             Throws a exception message
	 * @see String
	 * */
	public String getIDFromTableAnnotatedDocument(Object filename,
			String xmlNotesName) throws IOException {

		String result = "";

		try {

			final String SQL = " SELECT DISTINCT a.id, a.name" + " FROM "
					+ " ( "
					+ " SELECT id as ID, TRIM(EXTRACTVALUE(document_xml,  ' "
					+ xmlNotesName + " ')) AS name "
					+ " FROM annotated_document" + " ) AS a "
					+ " WHERE a.name =?" + "\n" + " LIMIT 1";

			con = DriverManager.getConnection(url + activeDB + ssl, user,
					password);
			con.clearWarnings();
			pst = con.prepareStatement(SQL);
			pst.setString(1, (String) filename);
			rs = pst.executeQuery();
			/*
			 * while (rs.next()) { result = rs.getObject(1).toString();
			 * dummyName = rs.getObject(2).toString(); }
			 */

			dummyName = "";
			if (rs.next()) {
				do {
					result = rs.getObject(1).toString();
					dummyName = rs.getObject(2).toString();
				} while (rs.next());
			}
		} catch (SQLException ex) {
			logger.info(ex.getMessage(), ex);
			exception = ex;

			EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					Dialogs.showMessageDialog("Get ID from annotateddocument table: "
							+ exception.getMessage());
				}
			});

		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException ex) {
				logger.warn(ex.getMessage(), ex);
			}
		}
		return result;
	}

	/**
	 * Delete Dummy documents when Import file match Dummy filename Used in
	 * LimsImportAB1
	 * 
	 * @param ID
	 *            Set parameter ID
	 * @throws IOException
	 *             Throw a exception message
	 * */
	public void DeleteDummyRecordFromTableAnnotatedtDocument(Object ID)
			throws IOException {

		try {

			final String SQL = "DELETE FROM annotated_document"
					+ System.lineSeparator() + "WHERE id =?";

			try {
				con = DriverManager.getConnection(url + activeDB + ssl, user,
						password);
				// con.clearWarnings();
			} catch (SQLException e) {
				throw new IllegalStateException("Cannot connect the database!",
						e);
			}
			pst = con.prepareStatement(SQL);
			pst.setString(1, (String) ID);
			pst.executeUpdate();
			logger.debug("Delete Dummy Annotated document id : " + ID
					+ "From tabel annotated_document ");
		} catch (SQLException ex) {
			logger.info(ex.getMessage(), ex);
			exception = ex;

			EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					Dialogs.showMessageDialog("Delete dummy: "
							+ exception.getMessage());
				}
			});

		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException ex) {
				logger.warn(ex.getMessage(), ex);
			}
		}
	}

	/**
	 * Used in LimsImportAB1Update Get Version for FAsta and AB1 files
	 * 
	 * @param fileName
	 *            Set parameter filename
	 * @return Return integer value (Version number)
	 * @see int
	 * */
	public int getLastVersion_For_AB1_Fasta(String fileName) {
		int result = 0;

		try {

			final String SQL = " SELECT MAX(CAST(a.version as UNSIGNED)) as version, a.name, a.reference_count "
					+ " FROM "
					+ " ( "
					+ " SELECT	TRIM(EXTRACTVALUE(document_xml, '//document/notes/note/DocumentVersionCode_Seq')) AS version, "
					+ " TRIM(EXTRACTVALUE(document_xml, '//document/hiddenFields/cache_name')) AS name, "
					+ " TRIM(EXTRACTVALUE(document_xml, '//document/hiddenFields/strong_referenced_documents/strong_referenced_documents')) AS reference_count "
					+ " FROM annotated_document) AS a "
					+ " WHERE a.name =?"
					+ " AND   a.reference_count = 0 " + " AND   a.version > 0";
			con = DriverManager.getConnection(url + activeDB + ssl, user,
					password);
			con.clearWarnings();
			pst = con.prepareStatement(SQL);
			pst.setString(1, (String) fileName);
			rs = pst.executeQuery();
			if (rs.next()) {
				do {
					result = rs.getInt(1);
					logger.debug("Versionnumber : " + result);
				} while (rs.next());
			}
		} catch (SQLException ex) {
			logger.info(ex.getMessage(), ex);
			exception = ex;

			EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					Dialogs.showMessageDialog("Get last version: "
							+ exception.getMessage());
				}
			});
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (SQLException ex) {
				logger.warn(ex.getMessage(), ex);
			}
		}
		return result;
	}

	/**
	 * Used in LimsImportAB1Update
	 * 
	 * @param fileName
	 *            Set param filename
	 * @return Return result value
	 * @see int
	 */
	public int getLastVersionFromDocument(String fileName) {
		int result = 0;

		try {

			final String SQL = " SELECT MAX(CAST(a.version as UNSIGNED)) as version"
					+ " FROM "
					+ " ( "
					+ " SELECT	TRIM(EXTRACTVALUE(document_xml, '//document/notes/note/DocumentVersionCode_Seq')) AS version, "
					+ " TRIM(EXTRACTVALUE(document_xml, '//document/hiddenFields/cache_name')) AS name "
					+ " FROM annotated_document) AS a " + " WHERE a.name =?";
			con = DriverManager.getConnection(url + activeDB + ssl, user,
					password);
			con.clearWarnings();
			pst = con.prepareStatement(SQL);
			pst.setString(1, (String) fileName);
			rs = pst.executeQuery();
			if (rs.next()) {
				do {
					result = rs.getInt(1);
					logger.debug("Versionnumber : " + result);
				} while (rs.next());
			}
		} catch (SQLException ex) {
			logger.info(ex.getMessage(), ex);
			exception = ex;

			EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					Dialogs.showMessageDialog("Get last version: "
							+ exception.getMessage());
				}
			});

		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException ex) {
				logger.warn(ex.getMessage(), ex);
			}
		}
		return result;
	}

	/**
	 * Get Dummy PCR Plate ID Sequence
	 * 
	 * @return Return a PCR plate Sequence value
	 * @see String
	 * */
	public String getDummyPcrPlateIdSeqValue() {
		return dummyPcrPlateIdSeqValue;
	}

	/**
	 * Set Dummy PCR Plate ID Sequence
	 * 
	 * @param dummyPcrPlateIdSeqValue
	 *            Set parameter PCRPlate Sequence Value
	 * */
	public void setDummyPcrPlateIdSeqValue(String dummyPcrPlateIdSeqValue) {
		this.dummyPcrPlateIdSeqValue = dummyPcrPlateIdSeqValue;
	}

	/**
	 * Get Dummy Marker Sequence
	 * 
	 * @return Return Marker sequence value
	 * @see String
	 * */
	public String getDummyMarkerSeqValue() {
		return dummyMarkerSeqValue;
	}

	/**
	 * Set Dummy Marker Sequence
	 * 
	 * @param dummyMarkerSeqValue
	 *            Set param dummyMarkerSeqValue
	 * */
	public void setDummyMarkerSeqValue(String dummyMarkerSeqValue) {
		this.dummyMarkerSeqValue = dummyMarkerSeqValue;
	}

	/**
	 * Get Dummy Registration number for samples
	 * 
	 * @return Return String value
	 * @see String
	 * */
	public String getDummyRegistrNmbrSamplesValue() {
		return dummyRegistrNmbrSamplesValue;
	}

	/**
	 * Set Dummy Registration number for samples
	 * 
	 * @param dummyRegistrNmbrSamplesValue
	 *            Set param dummyRegistrNmbrSamplesValue
	 * */
	public void setDummyRegistrNmbrSamplesValue(
			String dummyRegistrNmbrSamplesValue) {
		this.dummyRegistrNmbrSamplesValue = dummyRegistrNmbrSamplesValue;
	}

	/**
	 * Get Dummy ScientificName for Samples
	 * 
	 * @return Return Dummy ScientificName Samples Value
	 * @see String
	 * */
	public String getDummyScientificNameSamplesValue() {
		return dummyScientificNameSamplesValue;
	}

	/**
	 * Set Dummy ScientificName for Samples
	 * 
	 * @param dummyScientificNameSamplesValue
	 *            Set param
	 * */
	public void setDummyScientificNameSamplesValue(
			String dummyScientificNameSamplesValue) {
		this.dummyScientificNameSamplesValue = dummyScientificNameSamplesValue;
	}

	/**
	 * Get Dummy Plate ID for Samples
	 * 
	 * @return Return dummy Sample PlateId Value
	 * @see String
	 * */
	public String getDummySamplePlateIdSamplesValue() {
		return dummySamplePlateIdSamplesValue;
	}

	/**
	 * Set Dummy Plate ID for Samples
	 * 
	 * @param dummySamplePlateIdSamplesValue
	 *            Set param dummySamplePlateIdSamplesValue
	 **/
	public void setDummySamplePlateIdSamplesValue(
			String dummySamplePlateIdSamplesValue) {
		this.dummySamplePlateIdSamplesValue = dummySamplePlateIdSamplesValue;
	}

	/**
	 * Get Dummy Position for samples
	 * 
	 * @return Return dummy Position Samples value
	 * @see String
	 * */
	public String getDummyPositionSamplesValue() {
		return dummyPositionSamplesValue;
	}

	/**
	 * Set Dummy Position for samples
	 * 
	 * @param dummyPositionSamplesValue
	 *            Set param dummyPositionSamplesValue
	 */
	public void setDummyPositionSamplesValue(String dummyPositionSamplesValue) {
		this.dummyPositionSamplesValue = dummyPositionSamplesValue;
	}

	/**
	 * Get Dummy Extract ID for Samples
	 * 
	 * @return Return dummy ExtractID Samples value
	 * @see String
	 * */
	public String getDummyExtractIDSamplesValue() {
		return dummyExtractIDSamplesValue;
	}

	/**
	 * Set Dummy Extract ID for Samples
	 * 
	 * @param dummyExtractIDSamplesValue
	 *            Set param dummyExtractIDSamplesValue
	 * */
	public void setDummyExtractIDSamplesValue(String dummyExtractIDSamplesValue) {
		this.dummyExtractIDSamplesValue = dummyExtractIDSamplesValue;
	}

	/**
	 * Get Dummy Sequence Staff for Samples
	 * 
	 * @return Return dummy SeqStaff Samples value
	 * @see String
	 * */
	public String getDummySeqStaffSamplesValue() {
		return dummySeqStaffSamplesValue;
	}

	/**
	 * Set Dummy Sequence Staff for Samples
	 * 
	 * @param dummySeqStaffSamplesValue
	 *            Set param dummySeqStaffSamplesValue
	 * */
	public void setDummySeqStaffSamplesValue(String dummySeqStaffSamplesValue) {
		this.dummySeqStaffSamplesValue = dummySeqStaffSamplesValue;
	}

	/**
	 * Get Dummy Samples Values from the Database
	 * 
	 * @param filename
	 *            Set param Filename
	 * @return Return list Dummy values
	 * @see LimsReadGeneiousFieldsValues
	 * */
	public List<String> getDummySamplesValues(Object filename) {
		String result = "";

		try {

			final String SQL = " SELECT a.name, a.pcrplateid, a.marker, a.Registrationnumber, a.ScientificName, "
					+ " a.SamplePlateId, a.Position, a.ExtractID, a.Seqstaff, a.extractPlateNumberIDSamples, a.extractMethod, a.registrationScientificName "
					+ " FROM( "
					+ " SELECT "
					+ " TRIM(EXTRACTVALUE(document_xml,  '//document/hiddenFields/cache_name')) AS name, "
					+ " TRIM(EXTRACTVALUE(document_xml, '//document/notes/note/PCRplateIDCode_Seq')) AS pcrplateid, "
					+ " TRIM(EXTRACTVALUE(document_xml, '//document/notes/note/MarkerCode_Seq')) AS marker, "
					+ " TRIM(EXTRACTVALUE(document_xml, '//document/notes/note/RegistrationNumberCode_Samples')) AS registrationnumber, "
					+ " TRIM(EXTRACTVALUE(document_xml, '//document/notes/note/TaxonName2Code_Samples')) AS scientificName, "
					+ " TRIM(EXTRACTVALUE(document_xml, '//document/notes/note/ProjectPlateNumberCode_Samples')) AS samplePlateId, "
					+ " TRIM(EXTRACTVALUE(document_xml, '//document/notes/note/PlatePositionCode_Samples')) AS position, "
					+ " TRIM(EXTRACTVALUE(document_xml, '//document/notes/note/ExtractIDCode_Samples')) AS extractID, "
					+ " TRIM(EXTRACTVALUE(document_xml, '//document/notes/note/SequencingStaffCode_FixedValue_Samples')) AS seqStaff, "
					+ " TRIM(EXTRACTVALUE(document_xml, '//document/notes/note/ExtractPlateNumberCode_Samples')) AS extractPlateNumberIDSamples, "
					+ " TRIM(EXTRACTVALUE(document_xml, '//document/notes/note/SampleMethodCode_Samples')) AS extractMethod, "
					+ " TRIM(EXTRACTVALUE(document_xml, '//document/notes/note/RegistrationNumberCode_TaxonName2Code_Samples')) AS registrationScientificName "
					+ " FROM annotated_document) AS a "
					+ " WHERE a.name =?"
					+ " LIMIT 1";

			con = DriverManager.getConnection(url + activeDB + ssl, user,
					password);
			con.clearWarnings();
			pst = con.prepareStatement(SQL);
			pst.setString(1, (String) filename);
			rs = pst.executeQuery();
			ResultSetMetaData metadata = rs.getMetaData();
			int numberOfColumns = metadata.getColumnCount();
			listDummyValues.clear();
			while (rs.next()) {
				registrnmbrSamplesFromDummy = rs
						.getString("Registrationnumber");
				scientificNameSamplesFromDummy = rs.getString("ScientificName");
				samplePlateIdSamplesFromDummy = rs.getString("SamplePlateId");
				positionSamplesFromDummy = rs.getString("Position");
				extractidSamplesFromDummy = rs.getString("ExtractID");
				extractPlateIDSamples = rs
						.getString("extractPlateNumberIDSamples");
				extractionMethodSamples = rs.getString("extractMethod");
				registrationScientificName = rs
						.getString("registrationScientificName");
				int i = 1;
				while (i <= numberOfColumns) {
					listDummyValues.add(rs.getString(i++));
				}
			}
		} catch (SQLException ex) {
			logger.info(ex.getMessage(), ex);
			exception = ex;

			EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					Dialogs.showMessageDialog("Get samples Value: "
							+ exception.getMessage());
				}
			});

		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException ex) {
				logger.warn(ex.getMessage(), ex);
			}
		}
		return listDummyValues;
	}

	/**
	 * Get database name
	 * 
	 * @return Return names of the database
	 * @see LimsReadGeneiousFieldsValues
	 * */
	public String getServerDatabaseServiceName() {
		List<String> lstdb = new ArrayList<String>();

		String output = "";
		String dbResult = "";

		ListIterator<?> itr = PluginUtilities.getWritableDatabaseServiceRoots()
				.listIterator();

		while (itr.hasNext()) {
			String dbsvc = itr.next().toString();
			if (dbsvc.contains("geneious")) {
				String st[] = dbsvc.split("name=");
				Map<String, Integer> mp = new TreeMap<String, Integer>();
				for (int i = 0; i < st.length; i++) {
					Integer count = mp.get(st[i]);
					if (count == null) {
						count = 0;
					}
					mp.put(st[i], ++count);
					String strGeneious = st[i];

					if (strGeneious.contains("geneious")) {
						int indexEnd = strGeneious.indexOf(",");
						output = strGeneious.substring(0, indexEnd);
						lstdb.add(output);
					}
				}
			}
		}

		Iterator<String> lstitr = lstdb.listIterator();
		while (lstitr.hasNext()) {
			dbResult = lstitr.next().toString();
		}
		return dbResult; // databaseName[0];
	}
}
