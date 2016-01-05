/**
 * 
 */
package nl.naturalis.lims2.ab1.importer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
import com.biomatters.geneious.publicapi.documents.sequence.SequenceDocument;
import com.biomatters.geneious.publicapi.implementations.sequence.DefaultNucleotideSequence;
import com.biomatters.geneious.publicapi.plugin.DocumentFileImporter;
import com.biomatters.geneious.publicapi.plugin.DocumentImportException;
import com.biomatters.geneious.publicapi.plugin.PluginUtilities;
import com.biomatters.geneious.publicapi.utilities.FileUtilities;

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

		progressListener.setMessage("Importing sequence data");
		List<AnnotatedPluginDocument> docs = PluginUtilities.importDocuments(
				file, ProgressListener.EMPTY);

		System.out.println("Sequence name: " + file.getName());
		count += docs.size();

		document = importCallback.addDocument(docs.iterator().next());

		QueryField[] queryField = getSearchFields(file.getName());
		System.out.println(queryField.toString());

		displayFields = new ArrayList<DocumentField>();

		DocumentField extractName = DocumentField.createStringField(
				"Extract ID", "The sequence name", "Extract-ID");

		searchFields = new QueryField[1];

		Condition[] eqCond = { Condition.EQUAL };
		QueryField queryField2 = new QueryField(extractName, eqCond);

		searchFields[0] = queryField2;

		// BasicSearchQuery bs = (BasicSearchQuery) queryField2;

		Query query = Query.Factory.createFieldQuery(extractName,
				Condition.EQUAL, file.getName());

		try {
			retrieve(query, null, file.getAbsoluteFile());
		} catch (DatabaseServiceException e) {
			e.printStackTrace();
		}

		if (file.getName() != null) {
			limsAB1Fields.setFieldValuesFromAB1FileName(file.getName());

			logger.info("----------------------------S T A R T ---------------------------------");
			logger.info("Start extracting value from file: " + file.getName());

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
		}
		logger.info("Total of document(s) filename extracted: " + count);
		logger.info("----------------------------E N D ---------------------------------");
		logger.info("Done with extracting Ab1 file name. ");
		// limsLogger.flushCloseFileHandler();
		// limsLogger.removeConsoleHandler();
	}

	@Override
	public AutoDetectStatus tentativeAutoDetect(File file,
			String fileContentsStart) {
		return AutoDetectStatus.ACCEPT_FILE;
	}

	public QueryField[] getSearchFields(String name) {
		return new QueryField[] { new QueryField(new DocumentField("Name",
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

		try {
			System.out.println("text=" + fileName);
			System.out.println(FileUtilities.getTextFromFile(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
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
				if (currentLine != null) {
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
					name = currentLine.substring(1, currentLine.length());
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
}
