/**
 * <h1>Lims All Naturalis files Plugin</h1> 
 */
package nl.naturalis.lims2.ab1.importer;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jebl.util.ProgressListener;
import nl.naturalis.lims2.utils.Dummy;
import nl.naturalis.lims2.utils.LimsAB1Fields;
import nl.naturalis.lims2.utils.LimsDatabaseChecker;
import nl.naturalis.lims2.utils.LimsImportNotes;
import nl.naturalis.lims2.utils.LimsImporterUtil;
import nl.naturalis.lims2.utils.LimsNotes;
import nl.naturalis.lims2.utils.LimsReadGeneiousFieldsValues;
import nl.naturalis.lims2.utils.LimsReplaceDummyNotes;
import nl.naturalis.lims2.utils.LimsSQL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biomatters.geneious.publicapi.components.Dialogs;
import com.biomatters.geneious.publicapi.databaseservice.QueryField;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentField;
import com.biomatters.geneious.publicapi.plugin.DocumentFileImporter;
import com.biomatters.geneious.publicapi.plugin.DocumentImportException;
import com.biomatters.geneious.publicapi.plugin.Options;
import com.biomatters.geneious.publicapi.plugin.PluginUtilities;

/**
 * <table>
 * <tr>
 * <td>
 * Date: 24 august 2016</td>
 * </tr>
 * <tr>
 * <td>
 * Company: Naturalis Biodiversity Center</td>
 * </tr>
 * <tr>
 * <td>
 * City: Leiden</td>
 * </tr>
 * <tr>
 * <td>
 * Country: Netherlands</td>
 * </tr>
 * <tr>
 * <td>
 * Description:<br>
 * Start Geneious.<br>
 * Click on the "Import" button. <br>
 * Go to the AB1/Fasta files you want to import. <br>
 * Choose "All Naturalis files" from the dropdown menu of "Files of Type" in the
 * dialog screen and press "Import". <br>
 * Import process is started. <br>
 * A dialog window appears with a progress bar with a message.</td>
 * </tr>
 * </table>
 * 
 * @author Reinier.Kartowikromo
 * @version: 1.0
 */
public class LimsImportAB1 extends DocumentFileImporter {

	private LimsAB1Fields limsAB1Fields = new LimsAB1Fields();
	private LimsImporterUtil limsImporterUtil = new LimsImporterUtil();
	private LimsReadGeneiousFieldsValues readGeneiousFieldsValues = new LimsReadGeneiousFieldsValues();
	private LimsFileSelector fileselector = new LimsFileSelector();
	private LimsSQL limsSQL = new LimsSQL();
	// private LimsImportNotes limsImportNotes = new LimsImportNotes();
	private LimsReplaceDummyNotes limsReplaceDummyNotes = new LimsReplaceDummyNotes();
	// private LimsNotes limsNotes = new LimsNotes();

	private AnnotatedPluginDocument documentAnnotatedPlugin;
	private int count = 0;
	private static final Logger logger = LoggerFactory
			.getLogger(LimsImportAB1.class);

	public static ArrayList<DocumentField> displayFields;
	public static QueryField[] searchFields;
	private boolean fastaFileExists = false;
	private int versienummer = 0;
	private boolean isDeleted = false;
	private String extractAb1FastaFileName = "";

	private String dummyFilename = "";
	private String annotatedDocumentID = null;
	private boolean ab1fileExists = false;

	private ArrayList<AnnotatedPluginDocument> docs = null;

	private long startBeginTime = 0;
	private boolean dummyExists = false;
	private String extractID = "";
	public static List<Dummy> dummiesRecords = new ArrayList<Dummy>(100);

	private static ArrayList<String> recordList = new ArrayList<String>(100);

	// List<String> files = null;
	// private int selectedTotal = 1;
	private int selectedCount = 0;
	private List<String> deleteDummyList = new ArrayList<String>(100);
	private List<String> deleteExtractList = new ArrayList<String>(100);

	LimsDatabaseChecker dbchk = new LimsDatabaseChecker();

	public LimsImportAB1() {

		if (readGeneiousFieldsValues.activeDB != null) {
			try {
				if (!limsSQL.tableExist("tblDocumentImport")) {
					limsSQL.createTableDocumentImport();
					limsSQL.createIndexInTableDocumentImport();
				}

				readGeneiousFieldsValues.activeDB = readGeneiousFieldsValues
						.getServerDatabaseServiceName();
				if (readGeneiousFieldsValues.activeDB == "") {
					readGeneiousFieldsValues.activeDB = limsImporterUtil
							.getDatabasePropValues("databasename");

					setDummyValues();
					writeDummyRecord();
				}

			} catch (SQLException e1) {
				throw new RuntimeException(e1);
			}
		}

	}

	/**
	 * Return the file type for the import plugin
	 * 
	 * @return Return File Type Description
	 * @see String
	 * */
	@Override
	public String getFileTypeDescription() {
		return "All Naturalis Files";
	}

	/**
	 * Empty extension to show all files in the Open dialog screen
	 * 
	 * @return Return no extension
	 * @see String
	 * */
	@Override
	public String[] getPermissibleExtensions() {
		return new String[] { "" };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.biomatters.geneious.publicapi.plugin.DocumentFileImporter#
	 * getFractionOfTimeToSaveResults()
	 */
	@Override
	public double getFractionOfTimeToSaveResults() {

		return super.getFractionOfTimeToSaveResults();
	}

	@Override
	public File getPrimaryFileForMultipleFileImporter(List<File> list) {
		selectedCount = list.size();
		logger.info("Total selected import files: " + selectedCount);
		return super.getPrimaryFileForMultipleFileImporter(list);
	}

	/**
	 * Import AB1 and fasta files
	 * 
	 * @param file
	 * @param importCallback
	 * @param progessListener
	 * */
	@Override
	public void importDocuments(File file, ImportCallback importCallback,
			ProgressListener progressListener) throws IOException,
			DocumentImportException {

		if (!dbchk.checkDBName()) {
			Dialogs.showMessageDialog(dbchk.msg);
			dbchk.restartGeneious();
		}

		/* Get Databasename */
		if (readGeneiousFieldsValues.activeDB.length() == 0) {
			readGeneiousFieldsValues.activeDB = readGeneiousFieldsValues
					.getServerDatabaseServiceName();

			setDummyValues();
			writeDummyRecord();
		}

		if (readGeneiousFieldsValues.activeDB != null) {

			/* Start time of the process */
			startBeginTime = System.nanoTime();

			/* Split the filename and extract the ID */

			extractAb1FastaFileName = file.getName();
			limsAB1Fields.extractAB1_FastaFileName(extractAb1FastaFileName);
			extractID = limsAB1Fields.getExtractID();
			String pcrPlateId = limsAB1Fields.getPcrPlaatID();
			String marker = limsAB1Fields.getMarker();

			/* Check if Dummy file exists in the database */
			if (extractAb1FastaFileName.contains(".ab1")) {
				insertFileNameIntoTableDocumentImport(extractAb1FastaFileName);
			} else /* FAS check */
			if (extractAb1FastaFileName.contains(".fas")) {
				/* Get the file name from the Fasta file content */
				extractAb1FastaFileName = fileselector.readFastaContent(file);
				insertFileNameIntoTableDocumentImport(extractAb1FastaFileName);
			}

			progressListener
					.setMessage("Importing sequence data"
							+ "\n"
							+ "\n"
							+ "Warning:"
							+ "\n"
							+ "Geneious is currently processing the selected file(s)."
							+ "\n"
							+ "Please wait for the import process to finish."
							+ "\n"
							+ "You should preferably not start another action or change maps.");

			docs = (ArrayList<AnnotatedPluginDocument>) PluginUtilities
					.importDocuments(file, ProgressListener.EMPTY);

			// getFiles(extractID + ".dum");

			progressListener.setProgress(0, 10);

			count += docs.size();

			documentAnnotatedPlugin = importCallback.addDocument(docs
					.iterator().next());

			if (file.getName() != null && !isDeleted
					&& !limsSQL.documentNameExist(extractID + ".dum")) {
				setAB1_FastaImportNotes();
			}

			else { /* Get dummy values */

				Dummy found = searchForDummyRecords(file);
				enrichAB1_FastaImportDocumentsWithNotes(pcrPlateId, marker,
						found);
			}

			logger.info("Total of document(s) filename extracted: " + count);
			logger.info("----------------------------E N D ---------------------------------");
			logger.info("Done with extracting/imported Ab1 files. ");

			limsImporterUtil.calculateTimeForAddingNotes(startBeginTime);

		}
	}

	/*
	 * private List<String> getFiles(String fileName) { files = new
	 * ArrayList<String>(); if (limsSQL.documentNameExist(extractID + ".dum")) {
	 * files.add(fileName); } return files; }
	 */

	@Override
	public List<File> importDocumentsFromMultipleFilesReturningUnimported(
			Options options, List<File> file, ImportCallback importCallback,
			ProgressListener progressListener) throws IOException,
			DocumentImportException {
		return super.importDocumentsFromMultipleFilesReturningUnimported(
				options, file, importCallback, progressListener);
	}

	/**
	 * @param fileName
	 */
	private void insertFileNameIntoTableDocumentImport(String fileName) {
		boolean ab1DocExists = limsSQL.documentNameExist(fileName);
		if (!ab1DocExists) {
			try {
				limsSQL.insertIntoTableDocumentImport(fileName,
						limsSQL.importcounter);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}/*
		 * else { int counterAB1 = limsSQL.importcounter + 1;
		 * limsSQL.updateImportCount(counterAB1, fileName); }
		 */

		versienummer = readGeneiousFieldsValues
				.getLastVersionFromDocument(fileName);

		if (versienummer == 0) {
			ab1fileExists = false;
			fastaFileExists = false;
		} else {
			ab1fileExists = true;
			fastaFileExists = true;
		}
	}

	/**
	 * @param pcrPlateId
	 * @param marker
	 * @param found
	 */
	private void enrichAB1_FastaImportDocumentsWithNotes(String pcrPlateId,
			String marker, Dummy found) {
		logger.info("Get dummy notes and enrich notes to ab1/fasta document import.");
		/* Set version number */
		setVersionNumber();

		/*
		 * When Dummy file exists and the AB1 imported document match the Dummy
		 * filename then the dummy notes will be replaced with the AB1 notes.
		 * After all the matching dummy document will be deleted from the
		 * Geneious Folder/Database. Most of the notes from the Dummy documents
		 * will be inherited and add to the AB1 files
		 */

		if (found != null) {

			/*
			 * replaceDummyNotesWithAB1Notes(documentAnnotatedPlugin, found,
			 * pcrPlateId, marker, extractID);
			 */

			limsReplaceDummyNotes.enrichAb1DocumentWithDummyNotes(
					documentAnnotatedPlugin, found, versienummer, pcrPlateId,
					marker, extractID);

			/*
			 * if (count > 0) { selectedTotal = 0; }
			 */

			// found = null;
			// if (selectedTotal == 0) {
			deleteRecordsFromTable();
			// }
		}
	}

	/**
	 * @throws IOException
	 */
	private void setAB1_FastaImportNotes() throws IOException {
		logger.info("Add notes to ab1/fasta document import.");
		/* Set version number */
		setVersionNumber();

		/* Add Notes to AB1/Fasta document */
		if (documentAnnotatedPlugin != null) {

			/*
			 * setNotes_To_AB1_Fasta(documentAnnotatedPlugin,
			 * extractAb1FastaFileName);
			 */

			LimsImportNotes limsImportNotes = new LimsImportNotes();
			limsImportNotes.setImportNotes(documentAnnotatedPlugin,
					extractAb1FastaFileName, limsAB1Fields.getExtractID(),
					limsAB1Fields.getPcrPlaatID(), limsAB1Fields.getMarker(),
					versienummer,
					limsImporterUtil.getPropValues("seqsequencestaff"));
		}

	}

	/**
	 * @param file
	 * @return
	 */
	private Dummy searchForDummyRecords(File file) {
		// dummiesRecords = readDummyRecord();
		Dummy found = null;
		for (Dummy dummy : dummiesRecords) {
			if (dummy.getExtractID().equals(extractID)) {
				found = dummy;
				annotatedDocumentID = String.valueOf(found.getId());
				setDummyFilename(found.getName());
				// writeOneDummyRecord();
				deleteDummyList.add(annotatedDocumentID);
				deleteExtractList.add(found.getName());
				isDeleted = true;
				break;
			}
		}

		if (found != null) {
			setDummyExists(true);
		} else {
			setDummyExists(false);
			try {
				setAB1_FastaImportNotes();
				deleteRecordsFromTable();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			// logger.info("No match found for: " + file.getName());
		}
		return found;
	}

	/**
	 * 
	 */
	private void deleteRecordsFromTable() {
		if (count == selectedCount && isDeleted) {
			try {
				/*
				 * Delete dummy records after it match with the AB1 filename.
				 * Most of the notes from the Dummy documents will be inherited
				 * and add to the AB1 files
				 */
				for (int j = 0; j < deleteDummyList.size(); j++) {
					String obj = deleteDummyList.get(j);

					readGeneiousFieldsValues
							.DeleteDummyRecordFromTableAnnotatedtDocument(obj);

				}
				for (int i = 0; i < deleteExtractList.size(); i++) {
					String extract = deleteExtractList.get(i);
					limsSQL.DeleteDummyRecordFromTableAnnotatedtDocument(extract);
					// deleteElementFromDummyList(extract);
				}
				isDeleted = false;
				selectedCount = 0;
				count = 0;

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}
	}

	/**
	 * Search for a character in a Fasta file
	 * 
	 * @param file
	 * @param fileContentsStart
	 * */
	@Override
	public AutoDetectStatus tentativeAutoDetect(File file,
			String fileContentsStart) {
		if (fileContentsStart.startsWith(">")) {
			return AutoDetectStatus.MAYBE;
		} else {
			return AutoDetectStatus.ACCEPT_FILE;
		}

	}

	private void setVersionNumber() {
		if (ab1fileExists || fastaFileExists) {
			versienummer++;
		} else {
			versienummer = 1;
		}
	}

	private void setDummyValues() {
		dummiesRecords.clear();
		dummiesRecords = readGeneiousFieldsValues.getDummySamplesValues(".dum");
	}

	public List<Dummy> readDummyRecord() {
		List<Dummy> list = new ArrayList<Dummy>(100);
		for (int i = 0; i < recordList.size(); i++) {

			Dummy dummy = new Dummy();
			String[] data = recordList.get(i).toString().split(",");

			dummy.setId(Integer.valueOf(data[0]));
			dummy.setName(data[1]);
			dummy.setPcrplateid(data[2]);
			dummy.setMarker(data[3]);
			dummy.setRegistrationnumber(data[4]);
			dummy.setScientificName(data[5]);
			dummy.setSamplePlateId(data[6]);
			dummy.setPosition(data[7]);
			dummy.setExtractID(data[8]);
			dummy.setSeqStaff(data[9]);
			dummy.setExtractPlateNumberIDSamples(data[10]);
			dummy.setExtractMethod(data[11]);
			dummy.setRegistrationScientificName(data[12]);
			list.add(dummy);
		}
		return list;
	}

	/*
	 * public List<Dummy> readOneDummyRecord() { List<Dummy> list = new
	 * ArrayList<Dummy>(100); for (int i = 0; i < onerecordList.size(); i++) {
	 * 
	 * Dummy dummy = new Dummy(); String[] data =
	 * onerecordList.get(i).toString().split(",");
	 * 
	 * dummy.setId(Integer.valueOf(data[0])); dummy.setName(data[1]);
	 * dummy.setPcrplateid(data[2]); dummy.setMarker(data[3]);
	 * dummy.setRegistrationnumber(data[4]); dummy.setScientificName(data[5]);
	 * dummy.setSamplePlateId(data[6]); dummy.setPosition(data[7]);
	 * dummy.setExtractID(data[8]); dummy.setSeqStaff(data[9]);
	 * dummy.setExtractPlateNumberIDSamples(data[10]);
	 * dummy.setExtractMethod(data[11]);
	 * dummy.setRegistrationScientificName(data[12]); list.add(dummy); } return
	 * list;
	 * 
	 * }
	 */

	public List<String> writeDummyRecord() {

		for (Dummy dm : dummiesRecords) {
			recordList.add(dm.getId() + "," + dm.getName() + ","
					+ dm.getPcrplateid() + "," + dm.getMarker() + ","
					+ dm.getRegistrationnumber() + "," + dm.getScientificName()
					+ "," + dm.getSamplePlateId() + "," + dm.getPosition()
					+ "," + dm.getExtractID() + "," + dm.getSeqStaff() + ","
					+ dm.getExtractPlateNumberIDSamples() + ","
					+ dm.getExtractMethod() + ","
					+ dm.getRegistrationScientificName());
		}
		return recordList;
	}

	/*
	 * public List<String> writeOneDummyRecord() { onerecordList = new
	 * ArrayList<String>(100); for (Dummy dm : dummiesRecords) {
	 * onerecordList.add(dm.getId() + "," + dm.getName() + "," +
	 * dm.getPcrplateid() + "," + dm.getMarker() + "," +
	 * dm.getRegistrationnumber() + "," + dm.getScientificName() + "," +
	 * dm.getSamplePlateId() + "," + dm.getPosition() + "," + dm.getExtractID()
	 * + "," + dm.getSeqStaff() + "," + dm.getExtractPlateNumberIDSamples() +
	 * "," + dm.getExtractMethod() + "," + dm.getRegistrationScientificName());
	 * } return onerecordList; }
	 */

	private void setNotes_To_AB1_Fasta(
			AnnotatedPluginDocument documentAnnotated, String fileName)
			throws IOException {
		logger.info("----------------------------S T A R T ---------------------------------");
		logger.info("Start extracting value from file: " + fileName);

		LimsNotes limsNotes = new LimsNotes();
		/* set note for Extract-ID */
		limsNotes.setImportNotes(documentAnnotated, "ExtractIDCode_Seq",
				"Extract ID (Seq)", "Extract ID (Seq)",
				limsAB1Fields.getExtractID());

		/* set note for PCR Plaat-ID */
		limsNotes.setImportNotes(documentAnnotated, "PCRplateIDCode_Seq",
				"PCR plate ID (Seq)", "PCR plate ID (Seq)",
				limsAB1Fields.getPcrPlaatID());

		/* set note for Marker */
		limsNotes.setImportNotes(documentAnnotated, "MarkerCode_Seq",
				"Marker (Seq)", "Marker (Seq)", limsAB1Fields.getMarker());

		/* set note for Marker */
		limsNotes.setImportNotes(documentAnnotated, "DocumentVersionCode_Seq",
				"Document version", "Document version",
				Integer.toString(versienummer));

		/* set note for SequencingStaffCode_FixedValue_Seq */
		limsNotes.setImportNotes(documentAnnotated,
				"SequencingStaffCode_FixedValue_Seq", "Seq-staff (Seq)",
				"Seq-staff (Seq)",
				limsImporterUtil.getPropValues("seqsequencestaff"));

		/* set note for ConsensusSeqPassCode_Seq */
		limsNotes.setImportConsensusSeqPassNotes(documentAnnotated,
				limsNotes.ConsensusSeqPass, "ConsensusSeqPassCode_Seq",
				"Pass (Seq)", "Pass (Seq)", null);
	}

	private void replaceDummyNotesWithAB1Notes(
			AnnotatedPluginDocument documentAnnotated, Dummy found,
			String pcrPlateID, String marker, String extractID)
			throws IOException {

		LimsNotes limsNotes = new LimsNotes();

		/* set note for PCR Plaat-ID */
		limsNotes.setImportNotes(documentAnnotated, "PCRplateIDCode_Seq",
				"PCR plate ID (Seq)", "PCR plate ID (Seq)", pcrPlateID);
		// found.getPcrplateid()); // limsAB1Fields.getPcrPlaatID());

		/* set note for Marker */
		limsNotes.setImportNotes(documentAnnotated, "MarkerCode_Seq",
				"Marker (Seq)", "Marker (Seq)", marker);
		// found.getMarker());
		// limsAB1Fields.getMarker());

		/* set note for Extract-ID */
		limsNotes.setImportNotes(documentAnnotated, "ExtractIDCode_Seq",
				"Extract ID (Seq)", "Extract ID (Seq)", extractID);

		/* set note for Extract-ID */
		limsNotes.setImportNotes(documentAnnotated, "ExtractIDCode_Samples",
				"Extract ID (Samples)", "Extract ID (Samples)",
				found.getExtractID());

		/* set note for Project Plate number */
		limsNotes.setImportNotes(documentAnnotated,
				"ProjectPlateNumberCode_Samples", "Sample plate ID (Samples)",
				"Sample plate ID (Samples)", found.getSamplePlateId());

		/* set note for Taxon name */
		limsNotes.setImportNotes(documentAnnotated, "TaxonName2Code_Samples",
				"[Scientific name] (Samples)", "[Scientific name] (Samples)",
				found.getScientificName());

		/* set note for Registration number */
		limsNotes.setImportNotes(documentAnnotated,
				"RegistrationNumberCode_Samples", "Registr-nmbr (Samples)",
				"Registr-nmbr (Samples)", found.getRegistrationnumber());

		/* set note for Plate position */
		limsNotes.setImportNotes(documentAnnotated,
				"PlatePositionCode_Samples", "Position (Samples)",
				"Position (Samples)", found.getPosition());

		/* SequencingStaffCode_FixedValue_Seq */
		limsNotes.setImportNotes(documentAnnotated,
				"SequencingStaffCode_FixedValue_Seq", "Seq-staff (Seq)",
				"Seq-staff (Seq)",
				limsImporterUtil.getPropValues("seqsequencestaff"));

		/* AmplicificationStaffCode_FixedValue_Samples */
		limsNotes.setImportNotes(documentAnnotated,
				"AmplicificationStaffCode_FixedValue_Samples",
				"Ampl-staff (Samples)", "Ampl-staff (Samples)",
				limsImporterUtil.getPropValues("samplesamplicification"));

		/* set note for Extract Plate ID Samples */
		limsNotes.setImportNotes(documentAnnotated,
				"ExtractPlateNumberCode_Samples", "Extract plate ID (Samples)",
				"Extract plate ID (Samples)",
				found.getExtractPlateNumberIDSamples());

		/* set note for Extract Method */
		limsNotes.setImportNotes(documentAnnotated, "SampleMethodCode_Samples",
				"Extraction method (Samples)", "Extraction method (Samples)",
				found.getExtractMethod());

		/* set note for RegistrationNumberCode_TaxonName2Code_Samples */

		limsNotes.setImportNotes(documentAnnotated,
				"RegistrationNumberCode_TaxonName2Code_Samples",
				"Registr-nmbr_[Scientific_name] (Samples)",
				"Registr-nmbr_[Scientific_name] (Samples)",
				found.getRegistrationScientificName());

		/* set note for Version */
		limsNotes.setImportNotes(documentAnnotated, "DocumentVersionCode_Seq",
				"Document version", "Document version",
				Integer.toString(versienummer));
	}

	/*
	 * private void deleteElementFromDummyList(String extractID) {
	 * 
	 * for (int j = 0; j < dummiesRecords.size(); j++) { Dummy obj =
	 * dummiesRecords.get(j);
	 * 
	 * if (obj.getExtractID().equals(extractID)) { // found, delete.
	 * dummiesRecords.remove(j); break; }
	 * 
	 * }
	 * 
	 * }
	 */

	public boolean isDummyExists() {
		return dummyExists;
	}

	public void setDummyExists(boolean dummyExists) {
		this.dummyExists = dummyExists;
	}

	public String getDummyFilename() {
		return dummyFilename;
	}

	public void setDummyFilename(String dummyFilename) {
		this.dummyFilename = dummyFilename;
	}
}
