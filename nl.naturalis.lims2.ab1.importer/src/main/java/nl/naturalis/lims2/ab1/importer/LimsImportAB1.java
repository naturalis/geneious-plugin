/**
 * <h1>Lims All Naturalis files Plugin</h1> 
 */
package nl.naturalis.lims2.ab1.importer;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jebl.util.ProgressListener;
import nl.naturalis.lims2.utils.LimsAB1Fields;
import nl.naturalis.lims2.utils.LimsImporterUtil;
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
 * @author Reinier.Kartowikromo category Lims Import All Naturalis files plugin
 * @version: 1.0 Date 08 august 2016 Company Naturalis Biodiversity Center City
 *           Leiden Country Netherlands
 */
public class LimsImportAB1 extends DocumentFileImporter {

	private LimsAB1Fields limsAB1Fields = new LimsAB1Fields();
	private LimsNotes limsNotes = new LimsNotes();
	private LimsImporterUtil limsImporterUtil = new LimsImporterUtil();
	private LimsReadGeneiousFieldsValues ReadGeneiousFieldsValues = new LimsReadGeneiousFieldsValues();
	private LimsCRSFields LimsCRSFields = new LimsCRSFields();
	private LimsFileSelector fileselector = new LimsFileSelector();
	private AnnotatedPluginDocument documentAnnotatedPlugin;
	private int count = 0;
	private static final Logger logger = LoggerFactory
			.getLogger(LimsImportAB1.class);

	public static List<DocumentField> displayFields;
	public static QueryField[] searchFields;
	private String logFileName = "";
	private boolean fastaFileExists = false;
	private int versienummer = 0;
	private boolean isDeleted = false;
	private String extractAb1FastaFileName = "";
	private List<String> list = new ArrayList<String>();
	private String[] ab1FileName = null;
	private String dummyFilename = "";
	private String annotatedDocumentID = "";
	private boolean ab1fileExists = false;
	private int cnt = 0;
	private int selectedTotal = 1;
	private List<AnnotatedPluginDocument> docs = null;

	public LimsImportAB1() {

	}

	@Override
	public String getFileTypeDescription() {
		return "All Naturalis Files";
	}

	@Override
	public String[] getPermissibleExtensions() {
		return new String[] { "" };
	}

	/**
	 * Import AB1 and fasta files
	 * */
	@Override
	public void importDocuments(File file, ImportCallback importCallback,
			ProgressListener progressListener) throws IOException,
			DocumentImportException {

		/* Get Databasename */
		ReadGeneiousFieldsValues.activeDB = ReadGeneiousFieldsValues
				.getServerDatabaseServiceName();

		if (ReadGeneiousFieldsValues.activeDB != null) {
			/* Get the filename and extract the ID */
			ab1FileName = StringUtils.split(file.getName(), "_");

			/* Check if Dummy file exists in the database */
			dummyFilename = ReadGeneiousFieldsValues
					.getCacheNameFromGeneiousDatabase(ab1FileName[0] + ".dum",
							"//document/hiddenFields/cache_name");

			/* if exists then get the ID from the dummy file */
			annotatedDocumentID = ReadGeneiousFieldsValues
					.getIDFromTableAnnotatedDocument(ab1FileName[0] + ".dum",
							"//document/hiddenFields/cache_name");

			list.clear();
			list.addAll(ReadGeneiousFieldsValues
					.getDummySamplesValues(dummyFilename));

			/* Check if file exists in the database */
			ab1fileExists = ReadGeneiousFieldsValues
					.fileNameExistsInGeneiousDatabase(file.getName());

			extractAb1FastaFileName = file.getName();

			/* FAS check */
			if (extractAb1FastaFileName.contains("fas")) {
				/* Get the file name from the Fasta file content */
				extractAb1FastaFileName = fileselector.readFastaContent(file);

				/* Check if file already exists in the database. */
				fastaFileExists = ReadGeneiousFieldsValues
						.checkOfFastaOrAB1Exists(extractAb1FastaFileName,
								"plugin_document_xml",
								"//XMLSerialisableRootElement/name");
				/*
				 * Get the version number from the last inserted document that
				 * match the criteria
				 */
				versienummer = ReadGeneiousFieldsValues
						.getLastVersionFromDocument(extractAb1FastaFileName);
			} else {
				/* AB1 version check */
				versienummer = ReadGeneiousFieldsValues
						.getLastVersionFromDocument(extractAb1FastaFileName);
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

			docs = PluginUtilities
					.importDocuments(file, ProgressListener.EMPTY);

			progressListener.setProgress(0, 10);

			count += docs.size();

			documentAnnotatedPlugin = importCallback.addDocument(docs
					.listIterator().next());

			if (file.getName() != null && list.size() == 0 && !isDeleted) {

				limsAB1Fields
						.setFieldValuesFromAB1FileName(extractAb1FastaFileName);

				/* Set version number */
				setVersionNumber();

				/* Add Notes to Fasta document */
				setFastaFilesNotes(documentAnnotatedPlugin, file.getName());
			}

			else {
				/* AB1 Document */
				limsAB1Fields
						.setFieldValuesFromAB1FileName(extractAb1FastaFileName);

				String extractid = ReadGeneiousFieldsValues.extractidSamplesFromDummy;

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
				replaceDummyNotesWithAB1Notes(documentAnnotatedPlugin,
						extractid);

			}
			logger.info("Total of document(s) filename extracted: " + count);
			logger.info("----------------------------E N D ---------------------------------");
			logger.info("Done with extracting/imported Ab1 files. ");
			isDeleted = false;

			if (selectedTotal == 0) {
				EventQueue.invokeLater(new Runnable() {

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

	@Override
	public AutoDetectStatus tentativeAutoDetect(File file,
			String fileContentsStart) {
		if (fileContentsStart.startsWith(">")) {
			return AutoDetectStatus.MAYBE;
		} else {
			return AutoDetectStatus.ACCEPT_FILE;
		}

	}

	/**
	 * Import Fasta with Naturalis Plugin Set notes for fasta file
	 * */
	private void setFastaFilesNotes(AnnotatedPluginDocument documentAnnotated,
			String fileName) {
		logger.info("----------------------------S T A R T ---------------------------------");
		logger.info("Start extracting value from file: " + fileName);

		/* set note for Extract-ID */
		try {
			limsNotes.setImportNotes(documentAnnotated, "ExtractIDCode_Seq",
					"Extract ID (Seq)", "Extract ID (Seq)",
					limsAB1Fields.getExtractID());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		/* set note for PCR Plaat-ID */
		try {
			limsNotes.setImportNotes(documentAnnotated, "PCRplateIDCode_Seq",
					"PCR plate ID (Seq)", "PCR plate ID (Seq)",
					limsAB1Fields.getPcrPlaatID());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		/* set note for Marker */
		try {
			limsNotes.setImportNotes(documentAnnotated, "MarkerCode_Seq",
					"Marker (Seq)", "Marker (Seq)", limsAB1Fields.getMarker());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		/* set note for Marker */
		try {
			limsNotes.setImportNotes(documentAnnotated,
					"DocumentVersionCode_Seq", "Document version",
					"Document version", Integer.toString(versienummer));
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		/* set note for SequencingStaffCode_FixedValue_Seq */
		try {
			limsNotes.setImportNotes(documentAnnotated,
					"SequencingStaffCode_FixedValue_Seq", "Seq-staff (Seq)",
					"Seq-staff (Seq)",
					limsImporterUtil.getPropValues("seqsequencestaff"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		limsNotes.setImportConsensusSeqPassNotes(documentAnnotated,
				limsNotes.ConsensusSeqPass, "ConsensusSeqPassCode_Seq",
				"Pass (Seq)", "Pass (Seq)", null);
	}

	/**
	 * Import Replace dummy documents notes with AB1 notes
	 * */
	private void replaceDummyNotesWithAB1Notes(
			AnnotatedPluginDocument documentAnnotated, String extractID) {

		if (limsAB1Fields.getExtractID().equals(extractID)) {

			/* set note for PCR Plaat-ID */
			try {
				limsNotes.setImportNotes(documentAnnotated,
						"PCRplateIDCode_Seq", "PCR plate ID (Seq)",
						"PCR plate ID (Seq)", limsAB1Fields.getPcrPlaatID());
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			/* set note for Marker */
			try {
				limsNotes.setImportNotes(documentAnnotated, "MarkerCode_Seq",
						"Marker (Seq)", "Marker (Seq)",
						limsAB1Fields.getMarker());
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			/* set note for Extract-ID */
			try {
				limsNotes.setImportNotes(documentAnnotated,
						"ExtractIDCode_Seq", "Extract ID (Seq)",
						"Extract ID (Seq)", limsAB1Fields.getExtractID());
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			/** set note for Extract-ID */
			limsNotes.setImportNotes(documentAnnotated,
					"ExtractIDCode_Samples", "Extract ID (Samples)",
					"Extract ID (Samples)",
					ReadGeneiousFieldsValues.extractidSamplesFromDummy);

			/** set note for Project Plate number */
			limsNotes.setImportNotes(documentAnnotated,
					"ProjectPlateNumberCode_Samples",
					"Sample plate ID (Samples)", "Sample plate ID (Samples)",
					ReadGeneiousFieldsValues.samplePlateIdSamplesFromDummy);

			/** set note for Taxon name */
			limsNotes.setImportNotes(documentAnnotated,
					"TaxonName2Code_Samples", "[Scientific name] (Samples)",
					"[Scientific name] (Samples)",
					ReadGeneiousFieldsValues.scientificNameSamplesFromDummy);

			/** set note for Registration number */
			limsNotes.setImportNotes(documentAnnotated,
					"RegistrationNumberCode_Samples", "Registr-nmbr (Samples)",
					"Registr-nmbr (Samples)",
					ReadGeneiousFieldsValues.registrnmbrSamplesFromDummy);

			/** set note for Plate position */
			limsNotes.setImportNotes(documentAnnotated,
					"PlatePositionCode_Samples", "Position (Samples)",
					"Position (Samples)",
					ReadGeneiousFieldsValues.positionSamplesFromDummy);

			/** SequencingStaffCode_FixedValue_Seq */
			try {
				limsNotes.setImportNotes(documentAnnotated,
						"SequencingStaffCode_FixedValue_Seq",
						"Seq-staff (Seq)", "Seq-staff (Seq)",
						limsImporterUtil.getPropValues("seqsequencestaff"));
			} catch (IOException e) {
				e.printStackTrace();
			}

			/** AmplicificationStaffCode_FixedValue_Samples */
			try {
				limsNotes.setImportNotes(documentAnnotated,
						"AmplicificationStaffCode_FixedValue_Samples",
						"Ampl-staff (Samples)", "Ampl-staff (Samples)",
						limsImporterUtil
								.getPropValues("samplesamplicification"));
			} catch (IOException e) {
				e.printStackTrace();
			}

			/** set note for Extract Plate ID Samples */
			limsNotes.setImportNotes(documentAnnotated,
					"ExtractPlateNumberCode_Samples",
					"Extract plate ID (Samples)", "Extract plate ID (Samples)",
					ReadGeneiousFieldsValues.extractPlateIDSamples);

			/** set note for Extract Method */
			limsNotes.setImportNotes(documentAnnotated,
					"SampleMethodCode_Samples", "Extraction method (Samples)",
					"Extraction method (Samples)",
					ReadGeneiousFieldsValues.extractionMethodSamples);

			/**
			 * set note for RegistrationNumberCode_TaxonName2Code_Samples
			 */
			limsNotes.setImportNotes(documentAnnotated,
					"RegistrationNumberCode_TaxonName2Code_Samples",
					"Registr-nmbr_[Scientific name] (Samples)",
					"Registr-nmbr_[Scientific name] (Samples)",
					ReadGeneiousFieldsValues.registrationScientificName);

			/* set note for Version */
			try {
				limsNotes.setImportNotes(documentAnnotated,
						"DocumentVersionCode_Seq", "Document version",
						"Document version", Integer.toString(versienummer));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
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

}
