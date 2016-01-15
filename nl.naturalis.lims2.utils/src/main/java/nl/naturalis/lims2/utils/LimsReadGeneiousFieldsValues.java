/**
 * 
 */
package nl.naturalis.lims2.utils;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentNote;
import com.biomatters.geneious.publicapi.documents.DocumentNoteType;
import com.biomatters.geneious.publicapi.documents.DocumentNoteUtilities;
import com.biomatters.geneious.publicapi.documents.sequence.SequenceDocument;
import com.biomatters.geneious.publicapi.implementations.sequence.DefaultNucleotideSequence;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsReadGeneiousFieldsValues {
	private String url = "";
	private String user = "";
	private String password = "";
	private static final Logger logger = LoggerFactory
			.getLogger(LimsReadGeneiousFieldsValues.class);
	private LimsImporterUtil limsImporterUtil = new LimsImporterUtil();
	private String logFileName = "";
	private LimsLogList limsLogList = new LimsLogList();

	public LimsReadGeneiousFieldsValues() {

	}

	public Object readValueFromAnnotatedPluginDocument(
			AnnotatedPluginDocument annotatedPluginDocument, String noteCode,
			String fieldName) {

		/** noteCode = "DocumentNoteUtilities-Registration number"; */
		DocumentNoteType noteType = DocumentNoteUtilities.getNoteType(noteCode);
		Object fieldValue = null;

		if (noteType != null) {
			AnnotatedPluginDocument.DocumentNotes documentNotes = annotatedPluginDocument
					.getDocumentNotes(true);

			DocumentNote bos = documentNotes.getNote(noteCode);
			/** example: FieldName = "BasisOfRecordCode" */
			fieldValue = bos.getFieldValue(fieldName);
		}

		return fieldValue;
	}

	public Object object(AnnotatedPluginDocument annotatedPluginDocument) {

		/** noteCode = "DocumentNoteUtilities-Version number"; */
		DocumentNoteType noteType = DocumentNoteUtilities
				.getNoteType("DocumentNoteUtilities-VersieCode");
		Object fieldValue = null;

		if (noteType != null) {
			AnnotatedPluginDocument.DocumentNotes documentNotes = annotatedPluginDocument
					.getDocumentNotes(true);

			DocumentNote bos = documentNotes
					.getNote("DocumentNoteUtilities-VersieCode");
			/** example: FieldName = "Version number" */
			fieldValue = bos.getFieldValue("Version number");
		}

		return fieldValue;
	}

	public String getFileNameFromGeneiousDatabase(String filename)
			throws IOException {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		url = limsImporterUtil.getPropValues("url");
		user = limsImporterUtil.getPropValues("user");
		password = limsImporterUtil.getPropValues("password");

		String result = "";

		try {

			final String SQL = " SELECT a.name"
					+ " FROM "
					+ " ( "
					+ " SELECT	TRIM(EXTRACTVALUE(plugin_document_xml, '//ABIDocument/name')) AS name "
					+ " FROM annotated_document" + " ) AS a "
					+ " WHERE a.name =?";
			// + " WHERE a.name = '" + filename + "'"; //

			con = DriverManager.getConnection(url, user, password);
			pst = con.prepareStatement(SQL);
			pst.setString(1, filename);
			rs = pst.executeQuery();
			while (rs.next()) {
				result = rs.getObject(1).toString();
				logger.info("Filename: " + result
						+ " already exists in the geneious database.");
				limsLogList.UitvalList.add("Filename: " + result
						+ " already exists in the geneious database." + "\n");
				// limsLogList.msgUitvalList.add("Filename: " + result
				// + " already exists in the geneious database." + "\n");
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

	// this utility method returns a SequenceDocument based on the given name
	// , if they match the search parameters
	// or null if there is no match
	private SequenceDocument match(ArrayList<String> namesToMatch, String name,
			boolean matchBoth) {
		boolean nameMatch = false;
		if (namesToMatch.size() > 0) {
			for (String nameToMatch : namesToMatch) {
				if (name.toUpperCase().contains(nameToMatch)) {
					nameMatch = true;
				}
			}
		}

		boolean match;
		if (matchBoth) {
			match = nameMatch;
		} else {
			match = nameMatch;
		}
		if (match) {
			return new DefaultNucleotideSequence(name.substring(0,
					name.indexOf(" ")), name, "NNNNNNNNNN", new Date());
		}
		return null;
	}

	private void createLogFile(String fileName, List<String> list) {
		logFileName = limsImporterUtil.getLogPath() + File.separator + fileName
				+ limsImporterUtil.getLogFilename();
		File logfile = new File(logFileName);
		if (!logfile.exists()) {
			LimsLogger limsLogger = new LimsLogger(logFileName);
			limsLogger.logToFile(logFileName, list.toString());
		}
	}
}
