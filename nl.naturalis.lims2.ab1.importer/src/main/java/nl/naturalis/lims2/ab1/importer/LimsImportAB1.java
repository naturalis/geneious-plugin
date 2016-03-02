/**
 * 
 */
package nl.naturalis.lims2.ab1.importer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jebl.util.ProgressListener;
import nl.naturalis.lims2.utils.LimsAB1Fields;
import nl.naturalis.lims2.utils.LimsImporterUtil;
import nl.naturalis.lims2.utils.LimsLogger;
import nl.naturalis.lims2.utils.LimsNotes;
import nl.naturalis.lims2.utils.LimsReadGeneiousFieldsValues;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biomatters.geneious.publicapi.databaseservice.QueryField;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentField;
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
	private LimsFileSelector fileselector = new LimsFileSelector();
	private AnnotatedPluginDocument document;
	private int count = 0;
	private static final Logger logger = LoggerFactory
			.getLogger(LimsImportAB1.class);

	public static List<DocumentField> displayFields;
	public static QueryField[] searchFields;
	private String logFileName = "";

	int cnt = 0;

	public LimsImportAB1() {

	}

	@Override
	public String getFileTypeDescription() {
		// return "Naturalis Extract AB1 Filename Importer";
		return "All Naturalis Files";
	}

	@Override
	public String[] getPermissibleExtensions() {
		return new String[] { "" };
		// return new String[] { "ab1", "scf", "fasta", "fa" };
	}

	@Override
	public void importDocuments(File file, ImportCallback importCallback,
			ProgressListener progressListener) throws IOException,
			DocumentImportException {

		/* Get the filename and extract the ID */
		String[] ab1FileName = StringUtils.split(file.getName(), "_");

		/* Check if Dummy file exists in the database */
		String dummyFilename = ReadGeneiousFieldsValues
				.getCacheNameFromGeneiousDatabase(ab1FileName[0] + ".dum",
						"//document/hiddenFields/cache_name");

		/* if exists then get the ID from the dummy file */
		String annotatedDocumentID = ReadGeneiousFieldsValues
				.getIDFromTableAnnotatedtDocument(ab1FileName[0] + ".dum",
						"//document/hiddenFields/cache_name");
		/*
		 * if filename is equal to the dummy file name then delete the dummy
		 * record.
		 */
		if (dummyFilename.equals(ab1FileName[0] + ".dum")) {
			ReadGeneiousFieldsValues
					.DeleteDummyRecordFromTableAnnotatedtDocument(annotatedDocumentID);
			logger.info("Filename: " + ab1FileName[0]
					+ ".dum has been deleted from table annotated_document");
		}

		String extractAb1FastaFileName = "";
		ArrayList<Integer> listcnt = new ArrayList<Integer>();

		listcnt.add(cnt++);
		System.out.println("Count: " + Integer.toString(cnt));

		boolean fileExists = ReadGeneiousFieldsValues
				.fileNameExistsInGeneiousDatabase(file.getName());

		if (!fileExists) {

			extractAb1FastaFileName = file.getName();

			if (file.getName().contains("fas")) {
				extractAb1FastaFileName = fileselector.readFastaContent(file,
						extractAb1FastaFileName);
			}
			progressListener.setMessage("Importing sequence data");

			List<AnnotatedPluginDocument> docs = PluginUtilities
					.importDocuments(file, ProgressListener.EMPTY);

			progressListener.setProgress(0, 10);
			System.out.println("Database Services: "
					+ PluginUtilities.getWritableDatabaseServiceRoots());

			count += docs.size();

			document = importCallback.addDocument(docs.listIterator().next());

			if (file.getName() != null) {
				limsAB1Fields
						.setFieldValuesFromAB1FileName(extractAb1FastaFileName);

				logger.info("----------------------------S T A R T ---------------------------------");
				logger.info("Start extracting value from file: "
						+ file.getName());

				/* set note for Extract-ID */
				try {
					limsNotes.setImportNotes(document, "ExtractIDCode_Seq",
							"Extract ID (Seq)", "Extract ID (Seq)",
							limsAB1Fields.getExtractID());
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				/* set note for PCR Plaat-ID */
				try {
					limsNotes.setImportNotes(document, "PCRplateIDCode_Seq",
							"PCR plate ID (Seq)", "PCR plate ID (Seq)",
							limsAB1Fields.getPcrPlaatID());
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				/* set note for Marker */
				try {
					limsNotes.setImportNotes(document, "MarkerCode_Seq",
							"Marker (Seq)", "Marker (Seq)",
							limsAB1Fields.getMarker());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				/* set note for Marker */
				try {
					limsNotes.setImportNotes(document, "DocumentVersionCode",
							"Document version", "Document version",
							limsAB1Fields.getVersieNummer());
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				/* set note for AmplicificationStaffCode_FixedValue */
				try {
					limsNotes.setImportNotes(document,
							"AmplicificationStaffCode_FixedValue_Seq",
							"Ampl-staff (Seq)", "Ampl-staff (Seq)",
							limsImporterUtil
									.getPropValues("seqamplicification"));
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				limsNotes.setImportConsensusSeqPassNotes(document,
						limsNotes.ConsensusSeqPass,
						"ConsensusSeqPass_Code_Seq", "Pass (Seq)",
						"Pass (Seq)", null);

				// limsNotes.setImportTrueFalseNotes(document, "CRSCode_CRS",
				// "CRS (CRS)", "CRS (CRS)", true);
			}
			logger.info("Total of document(s) filename extracted: " + count);
			logger.info("----------------------------E N D ---------------------------------");
			logger.info("Done with extracting/imported Ab1 files. ");
		}

		// else {
		// EventQueue.invokeLater(new Runnable() {
		//
		// @Override
		// public void run() {
		// Dialogs.showMessageDialog("File: " + file.getName()
		// + " already exists in the database");
		// return;
		// }
		// });
		// }
	}

	@Override
	public AutoDetectStatus tentativeAutoDetect(File file,
			String fileContentsStart) {
		if (fileContentsStart.startsWith(">")) {
			return AutoDetectStatus.MAYBE;
		} else {
			return AutoDetectStatus.ACCEPT_FILE;
		}

	}

	private void createLogFile(String fileName, List<String> list) {
		logFileName = limsImporterUtil.getLogPath() + File.separator + fileName
				+ limsImporterUtil.getLogFilename();
		LimsLogger limsLogger = new LimsLogger(logFileName);
		limsLogger.logToFile(logFileName, list.toString());
	}
}
