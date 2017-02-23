/**
 * <h1>Lims All Naturalis files Plugin</h1> 
 */
package nl.naturalis.lims2.ab1.importer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jebl.util.ProgressListener;
import nl.naturalis.lims2.utils.Dummy;
import nl.naturalis.lims2.utils.LimsDatabaseChecker;
import nl.naturalis.lims2.utils.LimsImporterUtil;
import nl.naturalis.lims2.utils.LimsLogger;
import nl.naturalis.lims2.utils.LimsNotes;
import nl.naturalis.lims2.utils.LimsNotesAB1FastaImport;
import nl.naturalis.lims2.utils.LimsReadGeneiousFieldsValues;
import nl.naturalis.lims2.utils.LimsSQL;

import org.apache.commons.lang3.StringUtils;
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

	private LimsImporterUtil limsImporterUtil = new LimsImporterUtil();
	private LimsReadGeneiousFieldsValues readGeneiousFieldsValues = new LimsReadGeneiousFieldsValues();
	private LimsFileSelector fileselector = new LimsFileSelector();
	private LimsSQL limsSQL = new LimsSQL();

	private LimsNotesAB1FastaImport limsNotesIAB1FastaImp = new LimsNotesAB1FastaImport();

	private AnnotatedPluginDocument documentAnnotatedPlugin;
	private int count = 0;
	private static final Logger logger = LoggerFactory
			.getLogger(LimsImportAB1.class);
	private LimsLogger limsLogger = null;
	private String logImportAllNaturalisFiles;

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
	private ArrayList<String> listDummy = new ArrayList<String>();

	private long startBeginTime = 0;
	private boolean dummyExists = false;

	public static List<Dummy> dummiesRecords = new ArrayList<Dummy>(100);

	private static ArrayList<String> recordList = new ArrayList<String>(100);
	private List<String> uitValList = new ArrayList<String>();

	private int selectedCount = 0;
	private List<String> deleteDummyList = new ArrayList<String>(100);
	private List<String> deleteExtractList = new ArrayList<String>(100);
	private String[] ab1FileName = null;
	private String fastaFileName;
	private int counter;

	LimsDatabaseChecker dbchk = new LimsDatabaseChecker();

	public LimsImportAB1() {

		if (readGeneiousFieldsValues.activeDB != null) {
			readGeneiousFieldsValues.activeDB = readGeneiousFieldsValues
					.getServerDatabaseServiceName();
			if (readGeneiousFieldsValues.activeDB == "") {
				readGeneiousFieldsValues.activeDB = limsImporterUtil
						.getDatabasePropValues("databasename");

				setDummyValues();
				writeDummyRecord();
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

			fastaFileName = file.getName();
			ab1FileName = StringUtils.split(file.getName(), "_");
			extractAb1FastaFileName = file.getName();

			versienummer = readGeneiousFieldsValues
					.getLastVersionFromDocument(extractAb1FastaFileName);
			if (versienummer == 0) {
				ab1fileExists = false;
				fastaFileExists = false;
			} else {
				ab1fileExists = true;
				fastaFileExists = true;
			}

			if (extractAb1FastaFileName.contains("ab1")) {

				limsNotesIAB1FastaImp
						.set_AB1_Fasta_DocumentFileName(extractAb1FastaFileName);

				dummyExists = limsSQL.getImportDummyDocument(ab1FileName[0]
						+ ".dum");
				/*
				 * if (!dummyExists) { count = 0; }
				 */
				if (dummyExists) {
					annotatedDocumentID = limsSQL
							.getIDFromTableAnnotatedDocument(ab1FileName[0]
									+ ".dum",
									"//document/hiddenFields/cache_name");

					if (limsSQL.dummyName.length() > 0) {
						listDummy.clear();
						listDummy.add((limsSQL.dummyName));
					}
					setDummyValues();
				}

			} else

			/* Check if Dummy file exists in the database */
			/* FAS check */
			if (extractAb1FastaFileName.contains(".fas")) {
				/* Get the file name from the Fasta file content */
				extractAb1FastaFileName = fileselector.readFastaContent(file);

				limsNotesIAB1FastaImp
						.setFastaDocumentFileName(extractAb1FastaFileName);

				dummyExists = limsSQL
						.getImportDummyDocument(limsNotesIAB1FastaImp.extractID
								+ ".dum");
				if (dummyExists) {
					annotatedDocumentID = limsSQL
							.getIDFromTableAnnotatedDocument(
									limsNotesIAB1FastaImp.extractID + ".dum",
									"//document/hiddenFields/cache_name");

					if (limsSQL.dummyName.length() > 0) {
						listDummy.clear();
						listDummy.add((limsSQL.dummyName));
					}
					setDummyValues();
					writeDummyRecord();
				}
			} else { /* Uitvallijst */
				logImportAllNaturalisFiles = limsImporterUtil.getLogPath()
						+ "Import_All_Naturalis-Uitvallijst-"
						+ limsImporterUtil.getLogFilename();

				/* Create logfile */
				limsLogger = new LimsLogger(logImportAllNaturalisFiles);
				limsLogger.logToFile(logImportAllNaturalisFiles,
						uitValList.toString());
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

			progressListener.setProgress(0, 10);

			count += docs.size();//
			if (count == selectedCount) {
				counter = selectedCount;
				System.out.println("Size: " + count);
			}

			documentAnnotatedPlugin = importCallback.addDocument(docs
					.listIterator().next());

			/* listDummy.size() == 0) */

			if (file.getName() != null && !isDeleted && !dummyExists) {
				setAB1_FastaImportNotes();
				count = 0;
			} else { /* Get dummy values */
				Dummy found = searchForDummyRecords(file);
				enrichAB1_FastaImportDocumentsWithNotes(
						limsNotesIAB1FastaImp.pcrPlateID,
						limsNotesIAB1FastaImp.marker, found);
			}

			logger.info("Total of document(s) filename extracted: " + count);
			logger.info("----------------------------E N D ---------------------------------");
			logger.info("Done with extracting/imported Ab1 files. ");

			limsImporterUtil.calculateTimeForAddingNotes(startBeginTime);
		}
	}

	@Override
	public List<File> importDocumentsFromMultipleFilesReturningUnimported(
			Options options, List<File> file, ImportCallback importCallback,
			ProgressListener progressListener) throws IOException,
			DocumentImportException {
		return super.importDocumentsFromMultipleFilesReturningUnimported(
				options, file, importCallback, progressListener);
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
			try {
				replaceDummyNotesWithAB1Notes(documentAnnotatedPlugin, found,
						pcrPlateId, marker, limsNotesIAB1FastaImp.extractID);
				/* Delete dummy record from the table annotated document */
				deleteRecordsFromTable();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
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

			limsNotesIAB1FastaImp.enrich_AB1_And_Fasta_DocumentsWithNotes(
					documentAnnotatedPlugin, extractAb1FastaFileName,
					versienummer);
		}

	}

	/**
	 * @param file
	 * @return
	 */
	private Dummy searchForDummyRecords(File file) {
		Dummy found = null;
		for (Dummy dummy : dummiesRecords) {
			if (dummy.getExtractID().equals(limsNotesIAB1FastaImp.extractID)) {
				found = dummy;
				annotatedDocumentID = String.valueOf(found.getId());
				setDummyFilename(found.getName());
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
				// setAB1_FastaImportNotes();
				replaceDummyNotesWithAB1Notes(documentAnnotatedPlugin, found,
						limsNotesIAB1FastaImp.pcrPlateID,
						limsNotesIAB1FastaImp.marker,
						limsNotesIAB1FastaImp.extractID);
				deleteRecordsFromTable();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return found;
	}

	/**
	 * 
	 */
	private void deleteRecordsFromTable() {
		/*
		 * if (count == selectedCount && isDeleted &&
		 * extractAb1FastaFileName.contains(".ab1")) {
		 */
		if (selectedCount == counter && isDeleted
				&& extractAb1FastaFileName.contains(".ab1")) {
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
				if (counter == selectedCount) {
					isDeleted = false;
					selectedCount = 0;
					count = 0;
					counter = 0;
					listDummy.clear();
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		} else if (fastaFileName.contains(".fas") && isDeleted) {
			for (int j = 0; j < deleteDummyList.size(); j++) {
				String obj = deleteDummyList.get(j);

				try {
					readGeneiousFieldsValues
							.DeleteDummyRecordFromTableAnnotatedtDocument(obj);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}

			}
			isDeleted = false;
			selectedCount = 0;
			count = 0;
			listDummy.clear();
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
		dummiesRecords = readGeneiousFieldsValues
				.getDummySamplesValues(limsNotesIAB1FastaImp.extractID + ".dum");
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

	private void replaceDummyNotesWithAB1Notes(
			AnnotatedPluginDocument documentAnnotated, Dummy found,
			String pcrPlateID, String marker, String extractID)
			throws IOException {

		LimsNotes limsNotes = new LimsNotes();

		/* set note for PCR Plaat-ID */
		limsNotes.setImportNotes(documentAnnotated, "PCRplateIDCode_Seq",
				"PCR plate ID (Seq)", "PCR plate ID (Seq)", pcrPlateID);

		/* set note for Marker */
		limsNotes.setImportNotes(documentAnnotated, "MarkerCode_Seq",
				"Marker (Seq)", "Marker (Seq)", marker);

		/* set note for Extract-ID Sequence */
		limsNotes.setImportNotes(documentAnnotated, "ExtractIDCode_Seq",
				"Extract ID (Seq)", "Extract ID (Seq)", extractID);

		/* set note for Extract-ID Samples */
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
