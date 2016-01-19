/**
 * 
 */
package nl.naturalis.lims2.ab1.importer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jebl.util.ProgressListener;
import nl.naturalis.lims2.utils.LimsAB1Fields;
import nl.naturalis.lims2.utils.LimsImporterUtil;
import nl.naturalis.lims2.utils.LimsLogList;
import nl.naturalis.lims2.utils.LimsLogger;
import nl.naturalis.lims2.utils.LimsNotes;
import nl.naturalis.lims2.utils.LimsReadGeneiousFieldsValues;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biomatters.geneious.publicapi.databaseservice.QueryField;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentField;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.documents.URN;
import com.biomatters.geneious.publicapi.documents.sequence.NucleotideSequenceDocument;
import com.biomatters.geneious.publicapi.implementations.sequence.DefaultNucleotideSequence;
import com.biomatters.geneious.publicapi.plugin.DocumentFileImporter;
import com.biomatters.geneious.publicapi.plugin.DocumentImportException;
import com.biomatters.geneious.publicapi.plugin.PluginUtilities;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsImportAB1 extends DocumentFileImporter {

	private LimsAB1Fields limsAB1Fields = new LimsAB1Fields();
	private LimsNotes limsNotes = new LimsNotes();
	private LimsImporterUtil limsImporterUtil = new LimsImporterUtil();
	private LimsReadGeneiousFieldsValues ReadGeneiousFieldsValues = new LimsReadGeneiousFieldsValues();
	private AnnotatedPluginDocument document;
	private int count = 0;
	private static final Logger logger = LoggerFactory
			.getLogger(LimsImportAB1.class);

	public static List<DocumentField> displayFields;
	public static QueryField[] searchFields;
	private String logFileName = "";
	private LimsLogList limsLogList = new LimsLogList();
	int cnt = 0;

	public LimsImportAB1() {

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

		ArrayList<Integer> listcnt = new ArrayList<Integer>();

		listcnt.add(cnt++);
		System.out.println("Count: " + Integer.toString(cnt));

		if (!ReadGeneiousFieldsValues.getFileNameFromGeneiousDatabase(
				file.getName()).equals(file.getName())) {
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

			String dummyName = "New_Sequence_" + file.getName();

			limsLogList.msgUitvalList.add("Username: "
					+ System.getProperty("user.name") + "\n");
			limsLogList.msgUitvalList.add("Type action: Import AB1 file(s) "
					+ "\n");

			// msgUitvalList.clear();

			ArrayList<AnnotatedPluginDocument> sequenceList = new ArrayList<AnnotatedPluginDocument>();

			NucleotideSequenceDocument sequence = new DefaultNucleotideSequence(
					dummyName, "A new dummy Sequence", "NNNNNNNNNN",
					new Date(), URN.generateUniqueLocalURN("Dummy"));

			sequenceList.add(DocumentUtilities
					.createAnnotatedPluginDocument(sequence));
			try {
				limsNotes.setImportNotes(sequenceList.iterator().next(),
						"VersieCode", "Version number", "Version number", "0");
				limsLogList.UitvalList.add("Filename: " + file.getName()
						+ " already exists in the geneious database." + "\n");
				logger.info("New Dummy: " + dummyName + " file added.");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			DocumentUtilities.addGeneratedDocuments(sequenceList, false);

		}
		// boolean last = false;
		// // int size = listcnt.size();
		//
		// /*
		// * for (int j = 0; j < listcnt.size(); j++) { // Integer i =
		// * listcnt.get(j); if (j == (listcnt.size() - 1)) { last = true; } }
		// */
		//
		// for (Iterator it = listcnt.iterator(); it.hasNext();) {
		//
		// if (!it.hasNext()) {
		// last = true;
		// System.out.println("Last: " + it.next());
		// }
		// }

		createLogFile("Import AB1 uitvallijst", limsLogList.UitvalList);
		/*
		 * File logfile = new File(logFileName); if (!logfile.exists()) {
		 * 
		 * } else { // createLogFile("Import AB1 uitvallijst", msgUitvalList);
		 * limsLogList.msgUitvalList.add("Filename: " + file.getName() +
		 * " already exists in the geneious database." + "\n"); }
		 */

	}

	@Override
	public AutoDetectStatus tentativeAutoDetect(File file,
			String fileContentsStart) {
		return AutoDetectStatus.ACCEPT_FILE;
	}

	private void createLogFile(String fileName, List<String> list) {
		logFileName = limsImporterUtil.getLogPath() + File.separator + fileName
				+ limsImporterUtil.getLogFilename();
		LimsLogger limsLogger = new LimsLogger(logFileName);
		limsLogger.logToFile(logFileName, list.toString());
	}
}
