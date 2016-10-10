/**
 * <h1>Lims All Naturalis files Plugin</h1> 
 */
package nl.naturalis.lims2.ab1.importer;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import jebl.util.ProgressListener;
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
	private LimsNotes limsNotes = new LimsNotes();
	private LimsImporterUtil limsImporterUtil = new LimsImporterUtil();
	private LimsReadGeneiousFieldsValues ReadGeneiousFieldsValues = new LimsReadGeneiousFieldsValues();
	private LimsFileSelector fileselector = new LimsFileSelector();
	private LimsSQL limsSQL = new LimsSQL();
	private LimsImportNotes limsImportNotes = new LimsImportNotes();
	private LimsReplaceDummyNotes limsReplaceDummyNotes = new LimsReplaceDummyNotes();

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
	private ArrayList<String> list = new ArrayList<String>();
	private String[] ab1FileName = null;
	private String dummyFilename = "";
	private String annotatedDocumentID = "";
	private boolean ab1fileExists = false;
	private int selectedTotal = 1;
	private ArrayList<AnnotatedPluginDocument> docs = null;
	private long startBeginTime = 0;

	LimsDatabaseChecker dbchk = new LimsDatabaseChecker();

	public LimsImportAB1() {

		if (ReadGeneiousFieldsValues.activeDB != null) {
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

		/*
		 * LimsDatabaseChecker dbchk = new LimsDatabaseChecker();
		 */
		if (!dbchk.checkDBName()) {
			Dialogs.showMessageDialog(dbchk.msg);
			dbchk.restartGeneious();
		}

		/* Get Databasename */
		if (ReadGeneiousFieldsValues.activeDB.length() == 0) {
			ReadGeneiousFieldsValues.activeDB = ReadGeneiousFieldsValues
					.getServerDatabaseServiceName();
		}

		if (ReadGeneiousFieldsValues.activeDB != null) {

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
					int counter = limsSQL.importcounter + 1;
					limsSQL.updateImportCount(counter, file.getName());
				}

				// ab1fileExists = ab1DocExists;

				/* Check if file exists in the database */
				/*
				 * ab1fileExists = ReadGeneiousFieldsValues
				 * .fileNameExistsInGeneiousDatabase(file.getName());
				 */

				/*
				 * dummyFilename = ReadGeneiousFieldsValues
				 * .getCacheNameFromGeneiousDatabase(ab1FileName[0] + ".dum",
				 * "//document/hiddenFields/cache_name");
				 */

				// limsSQL.truefalse = false;

				/* if exists then get the ID from the dummy file */
				/*
				 * int dummyExists = ReadGeneiousFieldsValues
				 * .checkIfDocExistsInTableAnnotatedDocument( ab1FileName[0] +
				 * ".dum", "//document/hiddenFields/cache_name");
				 */
				int dummyExists = limsSQL
						.checkIfSampleDocExistsInTableAnnotatedDocument(ab1FileName[0]
								+ ".dum");
				// limsSQL.documentNameExist(ab1FileName[0]
				// + ".dum");
				if (dummyExists == 1) {
					annotatedDocumentID = ReadGeneiousFieldsValues
							.getIDFromTableAnnotatedDocument(ab1FileName[0]
									+ ".dum",
									"//document/hiddenFields/cache_name");

					dummyFilename = ReadGeneiousFieldsValues.dummyName;

					if (dummyFilename.length() > 0) {
						list.clear();
						list.addAll(ReadGeneiousFieldsValues
								.getDummySamplesValues(dummyFilename));
					}
				}

			}

			extractAb1FastaFileName = file.getName();
			/* FAS check */
			if (extractAb1FastaFileName.contains(".fas")) {
				// && !extractAb1FastaFileName.contains("ab1")) {

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

				// fastaFileExists = fastaExists;

				/* Check if file already exists in the database. */
				/*
				 * fastaFileExists = LimsReadGeneiousFieldsValues
				 * .checkOfFastaOrAB1Exists(extractAb1FastaFileName,
				 * "plugin_document_xml", "//XMLSerialisableRootElement/name");
				 */
				/*
				 * Get the version number from the last inserted document that
				 * match the criteria
				 */
				versienummer = ReadGeneiousFieldsValues
						.getLastVersionFromDocument(extractAb1FastaFileName);
				if (versienummer == 0) {
					fastaFileExists = false;
				} else {
					fastaFileExists = true;
				}
			} else {
				/* AB1 version check */
				versienummer = ReadGeneiousFieldsValues
						.getLastVersionFromDocument(extractAb1FastaFileName);
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

			if (file.getName() != null && list.size() == 0 && !isDeleted) {

				limsAB1Fields.extractAB1_FastaFileName(extractAb1FastaFileName);

				/* Set version number */
				setVersionNumber();

				/* Add Notes to AB1/Fasta document */
				if (documentAnnotatedPlugin != null) {
					// setNotes_To_AB1_Fasta(documentAnnotatedPlugin,
					// extractAb1FastaFileName);
					limsImportNotes.setImportNotes(documentAnnotatedPlugin,
							extractAb1FastaFileName,
							limsAB1Fields.getExtractID(),
							limsAB1Fields.getPcrPlaatID(),
							limsAB1Fields.getMarker(), versienummer,
							limsImporterUtil.getPropValues("seqsequencestaff"));
				}
			}

			else {
				/* AB1 Document */
				limsAB1Fields.extractAB1_FastaFileName(extractAb1FastaFileName);

				String extractID = limsAB1Fields.getExtractID();
				String pcrPlateId = limsAB1Fields.getPcrPlaatID();
				String marker = limsAB1Fields.getMarker();

				/* Set version number */
				setVersionNumber();

				/*
				 * When Dummy file exists and the AB1 imported document match
				 * the Dummy filename then the dummy notes will be replaced with
				 * the AB1 notes. After all the matching dummy document will be
				 * deleted from the Geneious Folder/Database. Most of the notes
				 * from the Dummy documents will be inherited and add to the AB1
				 * files
				 */

				limsReplaceDummyNotes
						.replaceDummyNotesWithAB1Notes(
								documentAnnotatedPlugin,
								pcrPlateId,
								marker,
								extractID,
								ReadGeneiousFieldsValues.extractidSamplesFromDummy,
								ReadGeneiousFieldsValues.samplePlateIdSamplesFromDummy,
								ReadGeneiousFieldsValues.scientificNameSamplesFromDummy,
								ReadGeneiousFieldsValues.registrnmbrSamplesFromDummy,
								ReadGeneiousFieldsValues.positionSamplesFromDummy,
								ReadGeneiousFieldsValues.extractPlateIDSamples,
								ReadGeneiousFieldsValues.extractionMethodSamples,
								ReadGeneiousFieldsValues.registrationScientificName,
								versienummer);
				if (count > 0) {
					selectedTotal = 0;
				}

				// replaceDummyNotesWithAB1Notes(documentAnnotatedPlugin,
				// ReadGeneiousFieldsValues.extractidSamplesFromDummy);

			}
			if (docs != null) {
				docs.clear();
			}

			logger.info("Total of document(s) filename extracted: " + count);
			logger.info("----------------------------E N D ---------------------------------");
			logger.info("Done with extracting/imported Ab1 files. ");
			isDeleted = false;

			calculateTimeForAddingNotes(startBeginTime);

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
								ReadGeneiousFieldsValues
										.DeleteDummyRecordFromTableAnnotatedtDocument(annotatedDocumentID);
								limsSQL.DeleteDummyRecordFromTableAnnotatedtDocument(ab1FileName[0]
										+ ".dum");
							} catch (IOException e) {
								e.printStackTrace();
							}
							isDeleted = true;
							logger.info("Filename: "
									+ ab1FileName[0]
									+ ".dum has been deleted from table annotated_document");
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

	/*
	 * Import Fasta with Naturalis Plugin Set notes for fasta file
	 */
	/*
	 * private void setNotes_To_AB1_Fasta( AnnotatedPluginDocument
	 * documentAnnotated, String fileName) throws IOException { logger.info(
	 * "----------------------------S T A R T ---------------------------------"
	 * ); logger.info("Start extracting value from file: " + fileName);
	 * 
	 * set note for Extract-ID limsNotes.setImportNotes(documentAnnotated,
	 * "ExtractIDCode_Seq", "Extract ID (Seq)", "Extract ID (Seq)",
	 * limsAB1Fields.getExtractID());
	 * 
	 * set note for PCR Plaat-ID limsNotes.setImportNotes(documentAnnotated,
	 * "PCRplateIDCode_Seq", "PCR plate ID (Seq)", "PCR plate ID (Seq)",
	 * limsAB1Fields.getPcrPlaatID());
	 * 
	 * set note for Marker limsNotes.setImportNotes(documentAnnotated,
	 * "MarkerCode_Seq", "Marker (Seq)", "Marker (Seq)",
	 * limsAB1Fields.getMarker());
	 * 
	 * set note for Marker limsNotes.setImportNotes(documentAnnotated,
	 * "DocumentVersionCode_Seq", "Document version", "Document version",
	 * Integer.toString(versienummer));
	 * 
	 * set note for SequencingStaffCode_FixedValue_Seq
	 * limsNotes.setImportNotes(documentAnnotated,
	 * "SequencingStaffCode_FixedValue_Seq", "Seq-staff (Seq)",
	 * "Seq-staff (Seq)", limsImporterUtil.getPropValues("seqsequencestaff"));
	 * 
	 * set note for ConsensusSeqPassCode_Seq
	 * limsNotes.setImportConsensusSeqPassNotes(documentAnnotated,
	 * limsNotes.ConsensusSeqPass, "ConsensusSeqPassCode_Seq", "Pass (Seq)",
	 * "Pass (Seq)", null); }
	 */

	private void setNotes_To_AB1_Fasta(
			AnnotatedPluginDocument documentAnnotated, String fileName)
			throws IOException {
		limsNotes.setImportConsensusSeqPassNotes(documentAnnotated,
				limsNotes.ConsensusSeqPass, "ConsensusSeqPassCode_Seq",
				"Pass (Seq)", "Pass (Seq)", null);
	}

	/*
	 * Import Replace dummy documents notes with AB1 notes
	 */
	private void replaceDummyNotesWithAB1Notes(
			AnnotatedPluginDocument documentAnnotated, String extractID)
			throws IOException {

		if (limsAB1Fields.getExtractID().equals(extractID)) {

			/* set note for PCR Plaat-ID */
			limsNotes.setImportNotes(documentAnnotated, "PCRplateIDCode_Seq",
					"PCR plate ID (Seq)", "PCR plate ID (Seq)",
					limsAB1Fields.getPcrPlaatID());

			/* set note for Marker */
			limsNotes.setImportNotes(documentAnnotated, "MarkerCode_Seq",
					"Marker (Seq)", "Marker (Seq)", limsAB1Fields.getMarker());

			/* set note for Extract-ID */
			limsNotes.setImportNotes(documentAnnotated, "ExtractIDCode_Seq",
					"Extract ID (Seq)", "Extract ID (Seq)",
					limsAB1Fields.getExtractID());

			/* set note for Extract-ID */
			limsNotes.setImportNotes(documentAnnotated,
					"ExtractIDCode_Samples", "Extract ID (Samples)",
					"Extract ID (Samples)",
					ReadGeneiousFieldsValues.extractidSamplesFromDummy);

			/* set note for Project Plate number */
			limsNotes.setImportNotes(documentAnnotated,
					"ProjectPlateNumberCode_Samples",
					"Sample plate ID (Samples)", "Sample plate ID (Samples)",
					ReadGeneiousFieldsValues.samplePlateIdSamplesFromDummy);

			/* set note for Taxon name */
			limsNotes.setImportNotes(documentAnnotated,
					"TaxonName2Code_Samples", "[Scientific name] (Samples)",
					"[Scientific name] (Samples)",
					ReadGeneiousFieldsValues.scientificNameSamplesFromDummy);

			/* set note for Registration number */
			limsNotes.setImportNotes(documentAnnotated,
					"RegistrationNumberCode_Samples", "Registr-nmbr (Samples)",
					"Registr-nmbr (Samples)",
					ReadGeneiousFieldsValues.registrnmbrSamplesFromDummy);

			/* set note for Plate position */
			limsNotes.setImportNotes(documentAnnotated,
					"PlatePositionCode_Samples", "Position (Samples)",
					"Position (Samples)",
					ReadGeneiousFieldsValues.positionSamplesFromDummy);

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
					"ExtractPlateNumberCode_Samples",
					"Extract plate ID (Samples)", "Extract plate ID (Samples)",
					ReadGeneiousFieldsValues.extractPlateIDSamples);

			/* set note for Extract Method */
			limsNotes.setImportNotes(documentAnnotated,
					"SampleMethodCode_Samples", "Extraction method (Samples)",
					"Extraction method (Samples)",
					ReadGeneiousFieldsValues.extractionMethodSamples);

			/*
			 * set note for RegistrationNumberCode_TaxonName2Code_Samples
			 */
			limsNotes.setImportNotes(documentAnnotated,
					"RegistrationNumberCode_TaxonName2Code_Samples",
					"Registr-nmbr_[Scientific_name] (Samples)",
					"Registr-nmbr_[Scientific_name] (Samples)",
					ReadGeneiousFieldsValues.registrationScientificName);

			/* set note for Version */
			limsNotes.setImportNotes(documentAnnotated,
					"DocumentVersionCode_Seq", "Document version",
					"Document version", Integer.toString(versienummer));

			if (count > 0) {
				selectedTotal = 0;
			}
		}
	}

	private void setVersionNumber() {
		if (ab1fileExists) {
			versienummer++;
		} else if (fastaFileExists) {
			versienummer++;
		} else {
			versienummer = 1;
		}
	}

	private void calculateTimeForAddingNotes(long startBeginTime) {
		long endTime = System.nanoTime();
		long elapsedTime = endTime - startBeginTime;
		logger.info("Took: "
				+ (TimeUnit.SECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS))
				+ " second(s)");
		elapsedTime = 0;
		endTime = 0;
	}

}
