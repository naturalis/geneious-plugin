/**
 * <h1>Lims All Naturalis files Plugin</h1> 
 */
package nl.naturalis.lims2.ab1.importer;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biomatters.geneious.publicapi.components.Dialogs;
import com.biomatters.geneious.publicapi.databaseservice.QueryField;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentField;
import com.biomatters.geneious.publicapi.plugin.DocumentFileImporter;
import com.biomatters.geneious.publicapi.plugin.DocumentImportException;
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
	private LimsImportNotes limsImportNotes = new LimsImportNotes();
	private LimsReplaceDummyNotes limsReplaceDummyNotes = new LimsReplaceDummyNotes();
	private LimsNotes limsNotes = new LimsNotes();

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

	private String[] ab1FileName = null;
	private String dummyFilename = "";
	private String annotatedDocumentID = "";
	private boolean ab1fileExists = false;
	private int selectedTotal = 1;

	private ArrayList<AnnotatedPluginDocument> docs = null;
	public List<Dummy> dummies = null;

	private long startBeginTime = 0;
	private boolean dummyExists = false;
	private String extractID = "";

	LimsDatabaseChecker dbchk = new LimsDatabaseChecker();

	public LimsImportAB1() {

		if (readGeneiousFieldsValues.activeDB != null) {
			try {
				if (!limsSQL.tableExist("tblDocumentImport")) {
					limsSQL.createTableDocumentImport();
					limsSQL.createIndexInTableDocumentImport();
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
		}

		/*
		 * if (ReadGeneiousFieldsValues.activeDB != null) { setDummyValues(); }
		 */
		if (readGeneiousFieldsValues.activeDB != null) {

			/* Start time of the process */
			startBeginTime = System.nanoTime();
			/* Get the filename and extract the ID */
			ab1FileName = StringUtils.split(file.getName(), "_");

			/* Check if Dummy file exists in the database */
			if (file.getName().contains(".ab1")) {

				boolean ab1DocExists = limsSQL
						.documentNameExist(file.getName());
				if (!ab1DocExists) {
					try {
						limsSQL.insertIntoTableDocumentImport(file.getName(),
								limsSQL.importcounter);
					} catch (SQLException e) {
						throw new RuntimeException(e);
					}
				} else {
					int counterAB1 = limsSQL.importcounter + 1;

					limsSQL.updateImportCount(counterAB1, file.getName());
				}
			}

			extractAb1FastaFileName = file.getName();
			/* FAS check */
			if (extractAb1FastaFileName.contains(".fas")) {

				/* Get the file name from the Fasta file content */
				extractAb1FastaFileName = fileselector.readFastaContent(file);

				boolean fastaExists = limsSQL
						.documentNameExist(extractAb1FastaFileName);
				if (!fastaExists) {
					try {
						limsSQL.insertIntoTableDocumentImport(
								extractAb1FastaFileName, limsSQL.importcounter);
					} catch (SQLException e) {
						throw new RuntimeException(e);
					}
				} else {

					int counter = limsSQL.importcounter + 1;
					limsSQL.updateImportCount(counter, extractAb1FastaFileName);
				}

				/*
				 * Get the version number from the last inserted document that
				 * match the criteria
				 */
				versienummer = limsSQL
						.getVersionFromDocumentName(extractAb1FastaFileName);
				// ReadGeneiousFieldsValues.getLastVersionFromDocument(extractAb1FastaFileName);
				if (versienummer == 0) {
					fastaFileExists = false;
				} else {
					fastaFileExists = true;
				}
			} else {/* AB1 version check */

				versienummer = limsSQL
						.getVersionFromDocumentName(extractAb1FastaFileName);
				/*
				 * versienummer = ReadGeneiousFieldsValues
				 * .getLastVersionFromDocument(extractAb1FastaFileName);
				 */

				if (versienummer == 0) {
					ab1fileExists = false;
				} else {
					ab1fileExists = true;
				}
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

			count += docs.size();

			documentAnnotatedPlugin = importCallback.addDocument(docs.stream()
					.iterator().next());

			limsAB1Fields.extractAB1_FastaFileName(extractAb1FastaFileName);
			extractID = limsAB1Fields.getExtractID();
			String pcrPlateId = limsAB1Fields.getPcrPlaatID();
			String marker = limsAB1Fields.getMarker();

			// boolean dummyExists = limsSQL.documentNameExist(extractID +
			// ".dum");
			/*
			 * try {
			 * readDummyRecords("C:\\Git\\GeneiousFiles\\dummyRecords.txt"); }
			 * catch (ClassNotFoundException e1) { throw new
			 * RuntimeException(e1); }
			 */

			Dummy found = null;

			for (Dummy dummy : dummies) {
				if (dummy.getExtractID().equals(extractID)) {
					found = dummy;
					annotatedDocumentID = String.valueOf(found.getId());
					dummyFilename = found.getName();
					break;
				}
			}

			if (found != null) {
				dummyExists = true;
			} else {
				dummyExists = false;
			}

			if (file.getName() != null && !dummyExists && !isDeleted) {

				/* Set version number */
				setVersionNumber();

				/* Add Notes to AB1/Fasta document */
				if (documentAnnotatedPlugin != null) {

					setNotes_To_AB1_Fasta(documentAnnotatedPlugin,
							extractAb1FastaFileName);

					/*
					 * limsImportNotes.setImportNotes(documentAnnotatedPlugin,
					 * extractAb1FastaFileName, limsAB1Fields.getExtractID(),
					 * limsAB1Fields.getPcrPlaatID(), limsAB1Fields.getMarker(),
					 * versienummer,
					 * limsImporterUtil.getPropValues("seqsequencestaff"));
					 */

				}
			}

			else { /* Get dummy values */
				/* Set version number */
				setVersionNumber();

				if (found == null) {
					logger.info("No match found for: " + file.getName());
				}

				/*
				 * When Dummy file exists and the AB1 imported document match
				 * the Dummy filename then the dummy notes will be replaced with
				 * the AB1 notes. After all the matching dummy document will be
				 * deleted from the Geneious Folder/Database. Most of the notes
				 * from the Dummy documents will be inherited and add to the AB1
				 * files
				 */

				if (found != null) {
					limsReplaceDummyNotes.enrichAb1DocumentWithDummyNotes(
							documentAnnotatedPlugin, found, versienummer,
							pcrPlateId, marker, extractID);
				}

				if (count > 0) {
					selectedTotal = 0;
				}

			}
			if (docs != null) {
				docs.clear();
			}

			logger.info("Total of document(s) filename extracted: " + count);
			logger.info("----------------------------E N D ---------------------------------");
			logger.info("Done with extracting/imported Ab1 files. ");
			isDeleted = false;

			limsImporterUtil.calculateTimeForAddingNotes(startBeginTime);

			if (selectedTotal == 0) {
				EventQueue.invokeLater(new Runnable() {

					/** Delete a dummy file */
					@Override
					public void run() {
						if (dummyFilename.equals(ab1FileName[0] + ".dum")) {
							try {
								/*
								 * Delete dummy records after it match with the
								 * AB1 filename. Most of the notes from the
								 * Dummy documents will be inherited and add to
								 * the AB1 files
								 */
								readGeneiousFieldsValues
										.DeleteDummyRecordFromTableAnnotatedtDocument(annotatedDocumentID);
								limsSQL.DeleteDummyRecordFromTableAnnotatedtDocument(ab1FileName[0]
										+ ".dum");
							} catch (IOException e) {
								e.printStackTrace();
							}
							isDeleted = true;
						}
					}
				});
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
		dummies = readGeneiousFieldsValues.getDummySamplesValues(".dum");
		try {
			saveDummyFile("dummyRecords.txt");
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}

	}

	private void saveDummyFile(String filename) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(new FileOutputStream(filename));
		for (Dummy dm : dummies) {
			pw.println(dm.getId());
			pw.println(dm.getName());
			pw.println(dm.getPcrplateid());
			pw.println(dm.getMarker());
			pw.println(dm.getRegistrationnumber());
			pw.println(dm.getScientificName());
			pw.println(dm.getSamplePlateId());
			pw.println(dm.getPosition());
			pw.println(dm.getExtractID());
			pw.println(dm.getSeqStaff());
			pw.println(dm.getExtractPlateNumberIDSamples());
			pw.println(dm.getExtractMethod());
			pw.println(dm.getRegistrationScientificName());
		}
		pw.close();
	}

	private void readDummyRecords(String filename) throws IOException,
			ClassNotFoundException {
		FileInputStream fis = new FileInputStream(filename);
		ObjectInputStream ois = new ObjectInputStream(fis);
		readGeneiousFieldsValues.dummies = (ArrayList<Dummy>) ois.readObject();
		fis.close();
	}

	/*
	 * public String[] readLines(String filename) throws IOException {
	 * FileReader fileReader = new FileReader(filename); BufferedReader
	 * bufferedReader = new BufferedReader(fileReader); List<Dummy> lines = new
	 * ArrayList<Dummy>(); String line = null; while ((line =
	 * bufferedReader.readLine()) != null) { lines.add(line); }
	 * bufferedReader.close(); return lines.toArray(new String[lines.size()]); }
	 */

	private void setNotes_To_AB1_Fasta(
			AnnotatedPluginDocument documentAnnotated, String fileName)
			throws IOException {
		logger.info("----------------------------S T A R T ---------------------------------");
		logger.info("Start extracting value from file: " + fileName);

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

}
