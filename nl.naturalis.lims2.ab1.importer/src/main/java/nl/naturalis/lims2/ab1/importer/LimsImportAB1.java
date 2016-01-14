/**
 * 
 */
package nl.naturalis.lims2.ab1.importer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import jebl.util.ProgressListener;
import nl.naturalis.lims2.utils.LimsAB1Fields;
import nl.naturalis.lims2.utils.LimsNotes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biomatters.geneious.publicapi.databaseservice.AdvancedSearchQueryTerm;
import com.biomatters.geneious.publicapi.databaseservice.BasicSearchQuery;
import com.biomatters.geneious.publicapi.databaseservice.CompoundSearchQuery;
import com.biomatters.geneious.publicapi.databaseservice.DatabaseServiceException;
import com.biomatters.geneious.publicapi.databaseservice.Query;
import com.biomatters.geneious.publicapi.databaseservice.QueryField;
import com.biomatters.geneious.publicapi.databaseservice.RetrieveCallback;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.Condition;
import com.biomatters.geneious.publicapi.documents.DocumentField;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.documents.URN;
import com.biomatters.geneious.publicapi.documents.sequence.NucleotideSequenceDocument;
import com.biomatters.geneious.publicapi.documents.sequence.SequenceDocument;
import com.biomatters.geneious.publicapi.implementations.sequence.DefaultNucleotideSequence;
import com.biomatters.geneious.publicapi.plugin.DocumentFileImporter;
import com.biomatters.geneious.publicapi.plugin.DocumentImportException;
import com.biomatters.geneious.publicapi.plugin.DocumentOperationException;
import com.biomatters.geneious.publicapi.plugin.PluginUtilities;

/*import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;*/

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsImportAB1 extends DocumentFileImporter {

	private final File ab1;
	private LimsAB1Fields limsAB1Fields = new LimsAB1Fields();
	private LimsNotes limsNotes = new LimsNotes();
	// private LimsImporterUtil limsImporterUtil = new LimsImporterUtil();
	private AnnotatedPluginDocument document;
	private int count = 0;
	private static final Logger logger = LoggerFactory
			.getLogger(LimsImportAB1.class);

	public static List<DocumentField> displayFields;
	public static QueryField[] searchFields;
	private LimsExcelFields limsExcelFields = new LimsExcelFields();

	public LimsImportAB1(File ab1File) {
		this.ab1 = ab1File;
	}

	@Override
	public String getFileTypeDescription() {
		return "Naturalis Extract AB1 Filename Importer";
	}

	@Override
	public String[] getPermissibleExtensions() {
		return new String[] { "ab1", "abi" };
	}

	@Override
	public void importDocuments(File file, ImportCallback importCallback,
			ProgressListener progressListener) throws IOException,
			DocumentImportException {

		/*
		 * String logFileName = limsImporterUtil.getLogPath() + File.separator +
		 * limsImporterUtil.getLogFilename();
		 * 
		 * LimsLogger limsLogger = new LimsLogger(logFileName);
		 */
		// Query query = null;

		// retrieve(query, importCallback, null);

		if (!getFileNameFromGeneiousDatabase(file.getName()).equals(
				file.getName())) {
			progressListener.setMessage("Importing sequence data");
			List<AnnotatedPluginDocument> docs = PluginUtilities
					.importDocuments(file, ProgressListener.EMPTY);

			count += docs.size();

			document = importCallback.addDocument(docs.iterator().next());

			if (file.getName() != null) {
				limsAB1Fields.setFieldValuesFromAB1FileName(file.getName());

				logger.info("----------------------------S T A R T ---------------------------------");
				logger.info("Start extracting value from file: "
						+ file.getName());

				/* set note for Extract-ID */
				try {
					limsNotes.setImportNotes(document, "ExtractIdCode",
							"Extract ID", "Extract-ID",
							limsAB1Fields.getExtractID());
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				/* set note for PCR Plaat-ID */
				try {
					limsNotes.setImportNotes(document, "PcrPlaatIdCode",
							"PCR plaat ID", "PCR plaat ID",
							limsAB1Fields.getPcrPlaatID());
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				/* set note for Marker */
				try {
					limsNotes.setImportNotes(document, "MarkerCode", "Marker",
							"Marker", limsAB1Fields.getMarker());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				/* set note for Marker */
				try {
					limsNotes.setImportNotes(document, "VersieCode",
							"Version number", "Version number",
							limsAB1Fields.getVersieNummer());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			logger.info("Total of document(s) filename extracted: " + count);
			logger.info("----------------------------E N D ---------------------------------");
			logger.info("Done with extracting Ab1 file name. ");
		} else {

			AnnotatedPluginDocument docs = null;
			ArrayList<AnnotatedPluginDocument> sequenceList = new ArrayList<AnnotatedPluginDocument>();

			NucleotideSequenceDocument sequence = new DefaultNucleotideSequence(
					"New Sequence", "A new dummy Sequence", "NNNNNNNNNN",
					new Date(), URN.generateUniqueLocalURN("Dummy"));
			importCallback.addDocument(sequence);

			sequenceList.add(DocumentUtilities
					.createAnnotatedPluginDocument(sequence));

			docs = sequenceList.iterator().next();
			try {
				docs.getDocument();
			} catch (DocumentOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				limsNotes.setImportNotes(docs, "VersieCode", "Version number",
						"Version number", "0");
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}
	}

	@Override
	public AutoDetectStatus tentativeAutoDetect(File file,
			String fileContentsStart) {
		System.out.println("FileName: " + file.getName());
		return AutoDetectStatus.ACCEPT_FILE;
	}

	public QueryField[] getSearchFields(String name) {
		return new QueryField[] { new QueryField(new DocumentField("name",
				"The sequence name", name, String.class, false, false),
				new Condition[] { Condition.CONTAINS }) };
	}

	// we find results based on the given query, and return them using the
	// callback supplied.
	public void retrieve(Query query, RetrieveCallback callback, File fileName)
			throws DatabaseServiceException {
		// some basic error handling
		if (!fileName.exists()) {
			throw new DatabaseServiceException(
					"AB1 file does not exist (file name=" + ab1 + ")", false);
		}

		System.out.println("text=" + fileName);
		// System.out.println(FileUtilities.getTextFromFile(fileName));

		try {
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			String currentLine = "";
			String name = "";
			ArrayList<String> nameToMatch = new ArrayList<String>();
			boolean matchEverything = false;
			// we will store a list of the queries
			List<Query> queries;

			// a compoundSearchQuery consists of a number of queries
			// we'll put them in the list
			if (query instanceof CompoundSearchQuery) {
				CompoundSearchQuery cQuery = (CompoundSearchQuery) query;
				matchEverything = cQuery.getOperator() == CompoundSearchQuery.Operator.AND;
				queries = (List<Query>) cQuery.getChildren();
			}
			// if the query is not a CompoundSearchQuery, then we can create a
			// one-element list containing the query
			else {
				queries = new ArrayList<Query>();
				queries.add(query);
			}

			// we'll loop through all the queries, and set the nameToMatch and
			// residuesToMatch
			for (Query q : queries) {
				// we have the sequence and name, do the searching
				if (q instanceof AdvancedSearchQueryTerm) {
					AdvancedSearchQueryTerm advancedQuery = (AdvancedSearchQueryTerm) q;
					if (advancedQuery.getField().getCode().equals("name")) {
						nameToMatch.add(advancedQuery.getValues()[0].toString()
								.toUpperCase());
					}
				}

				// a {@link BasicSearchQuery} consists of one field (search
				// text)
				// you can extend a basic query, for example using a {@link
				// CompoundSearchQuery}
				else if (q instanceof BasicSearchQuery) {
					// set both the name and the residue searches to the query
					// entered
					BasicSearchQuery bq = (BasicSearchQuery) query;
					nameToMatch.add(bq.getSearchText().toUpperCase());
				} else {
					// do nothing
				}
			}

			// if neither nameToMatch are set at this point,
			// the search will return no results.

			// now lets loop through the ab1 file
			while ((currentLine = in.readLine()) != null) {
				if (currentLine.startsWith("ABIF")) {
					// if (currentLine != null) {
					if (!name.equals("")) {
						// we get to this part of the code once we have read in
						// one sequence (a name line and the residue lines)
						// so we must now do a match on the name and residues
						// that we have read in
						SequenceDocument doc = match(nameToMatch, name,
								matchEverything);
						if (doc != null) {
							// add a search result if there is one
							callback.add(doc,
									Collections.<String, Object> emptyMap());
						}

					}

					// set the name variable to the new sequence name
					name = currentLine.substring(0, currentLine.length());
					System.out.println("name=" + name);
				}
			}
			in.close();
			// we need to do the match one last time once we reach the end of
			// the file
			SequenceDocument doc = match(nameToMatch, name, matchEverything);
			if (doc != null) {
				callback.add(doc, Collections.<String, Object> emptyMap());
			}
		} catch (IOException e) {
			// pass on any exceptions we get reading the file
			throw new DatabaseServiceException(e, e.getMessage(), false);
		}
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
					name.indexOf(" ")), name, "NNNNNNNNNN", new Date(
					ab1.lastModified()));
		}
		return null;
	}

	private String getFileNameFromGeneiousDatabase(String filename) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		String url = "jdbc:mysql://localhost:3306/Geneious";
		String user = "root";
		String password = "Blakapae1964@";
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

	public List<AnnotatedPluginDocument> performOperation(
			AnnotatedPluginDocument annotatedPluginDocument,
			ProgressListener progress) {
		// lets create the list that we're going to return...
		ArrayList<AnnotatedPluginDocument> sequenceList = new ArrayList<AnnotatedPluginDocument>();

		// The options that we created in the getOptions() method above
		// has been
		// passed to us, hopefully the user has filled in their sequence.
		// We get the option we added by using its name.
		// MultiLineStringOption
		// has a String ValueType, so we can safely cast to a String
		// object.
		String residues = "NNNNNNNNNN";// (String)//
										// options.getValue("residues");

		// lets construct a new sequence document from the residues that
		// the
		// user entered
		NucleotideSequenceDocument sequence = new DefaultNucleotideSequence(
				"New Sequence", "A new dummy Sequence", residues, new Date(),
				URN.generateUniqueLocalURN("Dummy"));

		// and add it to the list
		sequenceList.add(DocumentUtilities
				.createAnnotatedPluginDocument(sequence));

		// AnnotatedPluginDocument annotatedPluginDocument =
		// (AnnotatedPluginDocument) sequence;

		try {
			limsNotes.setImportNotes(sequenceList.iterator().next(),
					"VersieCode", "Version number", "Version number", "0");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		// normally we would set the progress incrementally as we went,
		// but this
		// operation is quick so we just set it to finished when we're
		// done.
		(progress).setProgress(1.0);

		// return the list containing the sequence we just created, and
		// we're
		// done!
		return sequenceList;
	}

}
