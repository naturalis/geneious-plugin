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
	private boolean fastaFileExists = false;
	private int versienummer = 0;
	private boolean isDeleted = false;
	private List<String> list = new ArrayList<String>();

	private int cnt = 0;

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
				.getIDFromTableAnnotatedDocument(ab1FileName[0] + ".dum",
						"//document/hiddenFields/cache_name");

		list.addAll(ReadGeneiousFieldsValues
				.getDummySamplesValues(dummyFilename));

		// System.out.println(list.toString());
		//
		// for (int i = 1; i < list.size(); i++) {
		//
		// ReadGeneiousFieldsValues.setDummyPcrPlateIdSeqValue(list.get(i));
		// System.out.println("PCR Plate: "
		// + ReadGeneiousFieldsValues.getDummyPcrPlateIdSeqValue());
		// ReadGeneiousFieldsValues.setDummyMarkerSeqValue(list.get(i));
		// ReadGeneiousFieldsValues.setDummyRegistrNmbrSamplesValue(list
		// .get(i));
		// ReadGeneiousFieldsValues.setDummyScientificNameSamplesValue(list
		// .get(i));
		// ReadGeneiousFieldsValues.setDummySamplePlateIdSamplesValue(list
		// .get(i));
		// ReadGeneiousFieldsValues.setDummyPositionSamplesValue(list.get(i));
		// ReadGeneiousFieldsValues.setDummyExtractIDSamplesValue(list.get(i));
		// ReadGeneiousFieldsValues.setDummySeqStaffSamplesValue(list.get(i));
		// }

		/*
		 * if filename is equal to the dummy file name then delete the dummy
		 * record.
		 */
		if (dummyFilename.equals(ab1FileName[0] + ".dum")) {
			ReadGeneiousFieldsValues
					.DeleteDummyRecordFromTableAnnotatedtDocument(annotatedDocumentID);
			isDeleted = true;
			logger.info("Filename: " + ab1FileName[0]
					+ ".dum has been deleted from table annotated_document");
		} else {
			isDeleted = false;
		}

		String extractAb1FastaFileName = "";
		ArrayList<Integer> listcnt = new ArrayList<Integer>();

		listcnt.add(cnt++);

		boolean ab1fileExists = ReadGeneiousFieldsValues
				.fileNameExistsInGeneiousDatabase(file.getName());

		extractAb1FastaFileName = file.getName();

		if (file.getName().contains("fas")) {
			extractAb1FastaFileName = fileselector.readFastaContent(file,
					extractAb1FastaFileName);
			fastaFileExists = ReadGeneiousFieldsValues.checkOfFastaOrAB1Exists(
					extractAb1FastaFileName, "plugin_document_xml",
					"//XMLSerialisableRootElement/name");
			versienummer = ReadGeneiousFieldsValues
					.getLastVersionFromDocument(extractAb1FastaFileName);
		} else {
			versienummer = ReadGeneiousFieldsValues
					.getLastVersionFromDocument(file.getName());
		}
		progressListener.setMessage("Importing sequence data");

		List<AnnotatedPluginDocument> docs = PluginUtilities.importDocuments(
				file, ProgressListener.EMPTY);

		progressListener.setProgress(0, 10);
		// System.out.println("Database Services: "
		// + PluginUtilities.getWritableDatabaseServiceRoots());

		count += docs.size();

		document = importCallback.addDocument(docs.listIterator().next());

		if (file.getName() != null) {

			limsAB1Fields
					.setFieldValuesFromAB1FileName(extractAb1FastaFileName);

			logger.info("----------------------------S T A R T ---------------------------------");
			logger.info("Start extracting value from file: " + file.getName());

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

			if (ab1fileExists) {
				versienummer++;
			} else if (fastaFileExists) {
				versienummer++;
			} else {
				versienummer = 1;
			}

			/* set note for Marker */
			try {
				limsNotes.setImportNotes(document, "DocumentVersionCode_Seq",
						"Document version", "Document version",
						Integer.toString(versienummer));
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			/* set note for AmplicificationStaffCode_FixedValue */
			try {
				limsNotes.setImportNotes(document,
						"AmplicificationStaffCode_FixedValue_Seq",
						"Ampl-staff (Seq)", "Ampl-staff (Seq)",
						limsImporterUtil.getPropValues("seqamplicification"));
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			limsNotes.setImportConsensusSeqPassNotes(document,
					limsNotes.ConsensusSeqPass, "ConsensusSeqPass_Code_Seq",
					"Pass (Seq)", "Pass (Seq)", null);

			if (isDeleted) {

				/* set note for PCR Plaat-ID */
				limsNotes.setImportNotes(document, "PCRplateIDCode_Seq",
						"PCR plate ID (Seq)", "PCR plate ID (Seq)", "AA000");

				/* set note for Marker */
				limsNotes.setImportNotes(document, "MarkerCode_Seq",
						"Marker (Seq)", "Marker (Seq)", "Dum");

				/** set note for Extract-ID */
				limsNotes.setImportNotes(document, "ExtractIDCode_Samples",
						"Extract ID (Samples)", "Extract ID (Samples)",
						list.get(7));

				/** set note for Project Plate number */
				limsNotes.setImportNotes(document,
						"ProjectPlateNumberCode_Samples",
						"Sample plate ID (Samples)",
						"Sample plate ID (Samples)", list.get(5));

				/** set note for Taxon name */
				limsNotes.setImportNotes(document, "TaxonName2Code_Samples",
						"[Scientific name] (Samples)",
						"[Scientific name] (Samples)", list.get(4));

				/** set note for Registration number */
				limsNotes.setImportNotes(document,
						"RegistrationNumberCode_Samples",
						"Registr-nmbr (Samples)", "Registr-nmbr (Samples)",
						list.get(3));

				/** set note for Plate position */
				limsNotes
						.setImportNotes(document, "PlatePositionCode_Samples",
								"Position (Samples)", "Position (Samples)",
								list.get(6));

				/** SequencingStaffCode_FixedValue */
				try {
					limsNotes.setImportNotes(document,
							"SequencingStaffCode_FixedValue_Samples",
							"Seq-staff (Samples)", "Seq-staff (Samples)",
							limsImporterUtil
									.getPropValues("samplessequencestaff"));
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
		logger.info("Total of document(s) filename extracted: " + count);
		logger.info("----------------------------E N D ---------------------------------");
		logger.info("Done with extracting/imported Ab1 files. ");
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

	@SuppressWarnings("unused")
	private void createLogFile(String fileName, List<String> list) {
		logFileName = limsImporterUtil.getLogPath() + File.separator + fileName
				+ limsImporterUtil.getLogFilename();
		LimsLogger limsLogger = new LimsLogger(logFileName);
		limsLogger.logToFile(logFileName, list.toString());
	}

}
