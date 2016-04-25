/**
 * 
 */
package nl.naturalis.lims2.ab1.importer;

import java.awt.EventQueue;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import nl.naturalis.lims2.utils.Lims2Connectie;
import nl.naturalis.lims2.utils.LimsFrameProgress;
import nl.naturalis.lims2.utils.LimsImporterUtil;
import nl.naturalis.lims2.utils.LimsLogger;
import nl.naturalis.lims2.utils.LimsNotes;
import nl.naturalis.lims2.utils.LimsReadGeneiousFieldsValues;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biomatters.geneious.publicapi.components.Dialogs;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.documents.PluginDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentAction;
import com.biomatters.geneious.publicapi.plugin.DocumentOperationException;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;
import com.opencsv.CSVReader;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsReadDataFromSamples extends DocumentAction {

	private List<AnnotatedPluginDocument> docs;
	private LimsImporterUtil limsImporterUtil = new LimsImporterUtil();
	private LimsSamplesFields limsExcelFields = new LimsSamplesFields();
	private LimsNotes limsNotes = new LimsNotes();
	private LimsFileSelector fcd = new LimsFileSelector();
	private LimsReadGeneiousFieldsValues geneiousFieldsValues = new LimsReadGeneiousFieldsValues();
	private LimsDummySeq limsDummySeq = new LimsDummySeq();

	private String extractIDfileName = "";
	private List<String> msgList = new ArrayList<String>();
	private List<String> msgUitvalList = new ArrayList<String>();
	private List<String> verwerkingListCnt = new ArrayList<String>();
	private List<String> verwerkList = new ArrayList<String>();
	private static final Logger logger = LoggerFactory
			.getLogger(LimsReadDataFromSamples.class);
	private String documentFileName = "";
	private String result = "";

	public int importCounter;
	private String[] record = null;
	private String ID = "";
	private String fileSelected = "";
	private final String noteCode = "DocumentNoteUtilities-Extract ID (Seq)";
	private final String fieldName = "ExtractIDCode_Seq";
	private JFrame frame = new JFrame();

	LimsFrameProgress limsFrameProgress = new LimsFrameProgress();
	private String plateNumber = "";
	private boolean isSampleDoc = false;
	private boolean isExtractIDSeqExists = false;
	private int version = 0;
	private String recordDocumentName = "";
	private String readAssembyContigFileName = "";
	private int sampleRecordCntVerwerkt = 0;
	private int sampleRecordUitval = 0;
	private int sampleTotaalRecords = 0;
	private CSVReader csvReader = null;
	private int sampleExactRecordsVerwerkt = 0;
	private int dummyRecordsVerwerkt = 0;
	private boolean match = false;
	private Lims2Connectie lims2Connectie = new Lims2Connectie();
	private String mapName = "";

	public LimsReadDataFromSamples() {

	}

	@Override
	public void actionPerformed(
			AnnotatedPluginDocument[] annotatedPluginDocuments) {

		try {
			performOperation(annotatedPluginDocuments);
		} catch (DocumentOperationException e) {
			e.printStackTrace();
		}
	}

	public void performOperation(AnnotatedPluginDocument[] documents)
			throws DocumentOperationException {

		/* Get Database name */
		geneiousFieldsValues.resultDB = geneiousFieldsValues
				.getServerDatabaseServiceName();

		try {
			System.out.println("Actieve Connectie: "
					+ lims2Connectie.getSimpleConnectionServer(
							geneiousFieldsValues.resultDB).getCatalog());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		if (geneiousFieldsValues.resultDB != null) {
			Object[] options = { "Ok", "No", "Cancel" };
			int n = JOptionPane.showOptionDialog(frame,
					"Choose one option to start Samples import", "Samples",
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
			if (n == 0) {
				if (DocumentUtilities.getSelectedDocuments().isEmpty()) {
					EventQueue.invokeLater(new Runnable() {

						@Override
						public void run() {

							Dialogs.showMessageDialog("Select at least one document");
							return;
						}
					});
				}
				if (!DocumentUtilities.getSelectedDocuments().isEmpty()) {
					docs = DocumentUtilities.getSelectedDocuments();

					String logFileName = limsImporterUtil.getLogPath()
							+ "Sample-method-Uitvallijst-"
							+ limsImporterUtil.getLogFilename();

					LimsLogger limsLogger = new LimsLogger(logFileName);

					isExtractIDSeqExists = DocumentUtilities
							.getSelectedDocuments().iterator().next()
							.toString().contains("MarkerCode_Seq");

					if (!isExtractIDSeqExists) {
						Dialogs.showMessageDialog("At least one selected document lacks Extract ID (Seq).");
						return;
					}

					logger.info("Start updating selected document(s).");
					fileSelected = fcd.loadSelectedFile();
					/** Add selected documents to a list. */

					if (fileSelected == null) {
						return;
					}

					msgUitvalList.add("Filename: " + fileSelected + "\n");

					for (int cnt = 0; cnt < docs.size(); cnt++) {

						// System.out.println("Database:"
						// + documents[cnt].isInLocalRepository());
						// System.out.println("Database:"
						// + documents[cnt].getDatabase().getFullPath());
						// System.out.println("Database:"
						// + documents[cnt].getDatabase());
						// int beginIndex = documents[cnt].getDatabase()
						// .toString().indexOf("=");
						// int endIndex =
						// documents[cnt].getDatabase().toString()
						// .indexOf(",");
						// mapName = documents[cnt].getDatabase().toString()
						// .substring(beginIndex + 1, endIndex);
						// System.out.println("Database map:" + mapName);

						isExtractIDSeqExists = geneiousFieldsValues
								.getValueFromAnnotatedPluginDocument(
										documents[cnt],
										"DocumentNoteUtilities-Extract ID (Seq)",
										"ExtractIDCode_Seq");

						documentFileName = (String) docs.get(cnt)
								.getFieldValue("cache_name");

						readAssembyContigFileName = (String) docs.get(cnt)
								.getFieldValue("override_cache_name");

						recordDocumentName = docs.get(cnt).getName();

						if (documentFileName.equals(recordDocumentName)) {

							if (!docs.toString().contains("consensus sequence")
									|| !docs.toString().contains("Contig")) {
								version = Integer
										.parseInt((String) geneiousFieldsValues
												.getVersionValueFromAnnotatedPluginDocument(
														documents,
														"DocumentNoteUtilities-Document version",
														"DocumentVersionCode_Seq",
														cnt));
							}
						}

						if ((readAssembyContigFileName != null)
								&& readAssembyContigFileName.toString()
										.contains("Reads Assembly Contig")) {
							documentFileName = docs.get(cnt).getName();
						} else if (docs.get(cnt).getName().toString()
								.contains("consensus sequence")) {
							documentFileName = docs.get(cnt).getName();

						} else if (docs.get(cnt).getName().toString()
								.contains("dum")) {
							documentFileName = docs.get(cnt).getName();
						} else {
							documentFileName = (String) geneiousFieldsValues
									.readValueFromAnnotatedPluginDocument(
											documents[cnt], "importedFrom",
											"filename");
						}

						/* Get file name from the document(s) */
						if (documentFileName.toString().contains("ab1")
								|| documentFileName.toString().contains("fas")
								|| documentFileName.toString().contains("dum")) {
							result = geneiousFieldsValues
									.getFileNameFromGeneiousDatabase(
											docs.get(cnt).getName(),
											"//XMLSerialisableRootElement/name");
						}

						if ((result != null && documentFileName.toString()
								.contains("fas"))
								|| (result.toString().contains("ab1"))
								|| (result.toString().contains("dum"))) {
							extractIDfileName = getExtractIDFromAB1FileName(docs
									.get(cnt).getName());
						} else if (docs.get(cnt).getName().toString()
								.contains("consensus sequence")
								|| docs.get(cnt).getName().toString()
										.contains("Contig")) {
							extractIDfileName = docs.get(cnt).getName();
						}

						msgList.add(extractIDfileName);

						isSampleDoc = DocumentUtilities.getSelectedDocuments()
								.iterator().next().toString()
								.contains("ExtractIDCode_Seq");

						if (sampleTotaalRecords == 0) {
							try {
								csvReader = new CSVReader(new FileReader(
										fileSelected), '\t', '\'', 0);
								sampleTotaalRecords = csvReader.readAll()
										.size();
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
							csvReader = null;
						}

						/** Create progress bar */
						limsFrameProgress.createProgressBar();

						if (isExtractIDSeqExists) {
							try {
								readDataFromExcel(fileSelected,
										extractIDfileName, documents, cnt);
							} catch (IOException e) {

								e.printStackTrace();
							}
						}

						importCounter = msgList.size();

						result = "";

					}

					logger.info("--------------------------------------------------------");
					logger.info("Total of document(s) updated: " + docs.size());
					limsFrameProgress.hideFrame();

					/* Set for creating dummy files */
					if (isSampleDoc) {
						limsFrameProgress.createProgressBar();
						setExtractIDFromSamplesSheet(fileSelected,
								extractIDfileName);
						limsFrameProgress.hideFrame();
					}

					logger.info("-------------------------- E N D --------------------------");
					logger.info("Done with updating the selected document(s). ");

					if (extractIDfileName != null) {
						msgUitvalList
								.add("Total records not matched: "
										+ Integer.toString(msgUitvalList.size())
										+ "\n");
					}
					EventQueue.invokeLater(new Runnable() {

						@Override
						public void run() {
							int totalResult = 0;
							sampleRecordUitval = msgUitvalList.size() - 1;
							totalResult = (sampleTotaalRecords - (msgUitvalList
									.size() - 1));
							if (totalResult > 0 && totalResult != 1) {
								sampleExactRecordsVerwerkt = totalResult;
							} else {
								sampleExactRecordsVerwerkt = 0;
							}

							Dialogs.showMessageDialog(Integer
									.toString(sampleTotaalRecords)
									+ " sample records have been read of which: "
									+ "\n"
									+ "[1] "
									+ Integer
											.toString(sampleExactRecordsVerwerkt)
									+ " samples are imported and linked to "
									+ Integer.toString(sampleRecordCntVerwerkt)
									+ " existing documents (of "
									+ importCounter
									+ " selected) \n"
									+ "[2] "
									+ Integer.toString(dummyRecordsVerwerkt)
									+ " sample are imported as dummy"
									+ "\n"
									+ "\n"
									+ "List of "
									+ Integer.toString(importCounter)
									+ " selected documents: "
									+ "\n"
									+ msgList.toString()
									+ "\n"
									+ "\n"
									+ "[3] "
									+ Integer.toString(sampleRecordUitval)
									+ " sample records are ignored.");

							logger.info("Sample-method: Total imported document(s): "
									+ msgList.toString());

							limsLogger.logToFile(logFileName,
									msgUitvalList.toString());

							msgList.clear();
							msgUitvalList.clear();
							verwerkingListCnt.clear();
							verwerkList.clear();
							sampleExactRecordsVerwerkt = 0;
							sampleRecordUitval = 0;
							sampleRecordCntVerwerkt = 0;
							dummyRecordsVerwerkt = 0;
						}
					});
				}
			} else if (n == 1) {
				limsFrameProgress.createProgressBar();
				fileSelected = fcd.loadSelectedFile();
				setExtractIDFromSamplesSheet(fileSelected, extractIDfileName);
				limsFrameProgress.hideFrame();
			} else if (n == 2) {
				return;
			}

		}
	}

	private void setSamplesNotes(AnnotatedPluginDocument[] documents, int cnt) {

		/** set note for Registration number */
		limsNotes.setNoteToAB1FileName(documents,
				"RegistrationNumberCode_Samples", "Registr-nmbr (Samples)",
				"Registr-nmbr (Samples)",
				limsExcelFields.getRegistrationNumber(), cnt);

		/** set note for Taxonname */
		limsNotes.setNoteToAB1FileName(documents, "TaxonName2Code_Samples",
				"[Scientific name] (Samples)", "[Scientific name] (Samples)",
				limsExcelFields.getTaxonNaam(), cnt);

		/** set note for Project Plate number */
		limsNotes.setNoteToAB1FileName(documents,
				"ProjectPlateNumberCode_Samples", "Sample plate ID (Samples)",
				"Sample plate ID (Samples)",
				limsExcelFields.getProjectPlaatNummer(), cnt);

		/** Set note for Extract plate number */
		limsNotes.setNoteToAB1FileName(documents,
				"ExtractPlateNumberCode_Samples", "Extract plate ID (Samples)",
				"Extract plate ID (Samples)",
				limsExcelFields.getExtractPlaatNummer(), cnt);

		/** set note for Plate position */
		limsNotes.setNoteToAB1FileName(documents, "PlatePositionCode_Samples",
				"Position (Samples)", "Position (Samples)",
				limsExcelFields.getPlaatPositie(), cnt);

		/** set note for Extract-ID */
		limsNotes.setNoteToAB1FileName(documents, "ExtractIDCode_Samples",
				"Extract ID (Samples)", "Extract ID (Samples)",
				limsExcelFields.getExtractID(), cnt);

		/** set note for Sample method */
		limsNotes.setNoteToAB1FileName(documents, "SampleMethodCode_Samples",
				"Extraction method (Samples)", "Extraction method (Samples)",
				limsExcelFields.getSubSample(), cnt);

		limsNotes.setNoteToAB1FileName(documents, "DocumentVersionCode_Seq",
				"Document version", "Document version",
				String.valueOf(limsExcelFields.getVersieNummer()), cnt);

		/** AmplicificationStaffCode_FixedValue_Samples */
		try {
			limsNotes.setNoteToAB1FileName(documents,
					"AmplicificationStaffCode_FixedValue_Samples",
					"Ampl-staff (Samples)", "Ampl-staff (Samples)",
					limsImporterUtil.getPropValues("samplesamplicification"),
					cnt);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public GeneiousActionOptions getActionOptions() {
		return new GeneiousActionOptions("1 of 2 Samples").setInPopupMenu(true)
				.setMainMenuLocation(GeneiousActionOptions.MainMenu.Tools, 1.0)
				.setInMainToolbar(true).setInPopupMenu(true)
				.setAvailableToWorkflows(true);
	}

	@Override
	public String getHelp() {
		return null;
	}

	@Override
	public DocumentSelectionSignature[] getSelectionSignatures() {
		return new DocumentSelectionSignature[] { new DocumentSelectionSignature(
				PluginDocument.class, 0, Integer.MAX_VALUE) };
	}

	/*
	 * Create dummy files for samples when there is no match with records in the
	 * database
	 */
	private void setExtractIDFromSamplesSheet(String fileName,
			String extractFileID) {
		try {
			if (fileName != null) {
				logger.info("Read samples file: " + fileName);
				CSVReader csvReader = new CSVReader(new FileReader(fileName),
						'\t', '\'', 0);
				csvReader.readNext();

				try {
					while ((record = csvReader.readNext()) != null) {
						if (record.length == 0) {
							continue;
						}

						if (record[3].trim() != null) {
							ID = "e" + record[3];
						}

						if (record[2].length() > 0 && record[2].contains("-")) {
							plateNumber = record[2].substring(0,
									record[2].indexOf("-"));
						}

						String dummyFile = geneiousFieldsValues
								.getFastaIDForSamples_GeneiousDB(ID);

						if (dummyFile.trim() != "") {
							dummyFile = getExtractIDFromAB1FileName(dummyFile);
						}

						if (!dummyFile.equals(ID)) {
							limsFrameProgress.showProgress(ID);

							if (ID.equals("e")
									&& LimsImporterUtil.extractNumber(ID)
											.isEmpty()) {
								logger.info("Record is empty: " + ID);
							} else {
								limsDummySeq.createDummySampleSequence(ID, ID,
										record[0], plateNumber, record[5],
										record[4], record[1]);
								dummyRecordsVerwerkt++;
							}
						}
					} // end While

					if (dummyRecordsVerwerkt == 0) {
						Dialogs.showMessageDialog("[3] " + dummyRecordsVerwerkt
								+ "(zero). dummy samples are ignored.");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					csvReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void readDataFromExcel(String fileName, String extractID,
			AnnotatedPluginDocument[] documents, int cnt) throws IOException {

		msgUitvalList.clear();

		logger.info("CSV file: " + fileName);
		/** Start reading data from the file selected */
		logger.info("-------------------------- S T A R T --------------------------");
		logger.info("Start Reading data from a samples file.");

		try {
			csvReader = new CSVReader(new FileReader(fileName), '\t', '\'', 0);
			csvReader.readNext();

			msgUitvalList.add("-----------------------------------------------"
					+ "\n");
			// msgUitvalList
			// .add("Ab1 filename: " + docs.get(cnt).getName() + "\n");

			/** Show the progress bar */
			limsFrameProgress.showProgress(docs.get(cnt).getName());

			try {
				while ((record = csvReader.readNext()) != null) {
					if (record.length == 0) {
						continue;
					}

					ID = "e" + record[3];

					if (record[2].length() > 0 && record[2].contains("-")) {
						plateNumber = record[2].substring(0,
								record[2].indexOf("-"));
					}

					if (ID.equals(extractID)) {

						match = true;
						sampleRecordCntVerwerkt++;

						limsExcelFields.setProjectPlaatNummer(record[0]);
						limsExcelFields.setPlaatPositie(record[1]);
						limsExcelFields.setExtractPlaatNummer(plateNumber);
						if (ID != null) {
							limsExcelFields.setExtractID(ID);
						} else {
							limsExcelFields.setExtractID("");
						}
						limsExcelFields.setRegistrationNumber(record[4]);
						limsExcelFields.setTaxonNaam(record[5]);

						logger.info("Extract-ID: "
								+ limsExcelFields.getExtractID());
						logger.info("Project plaatnummer: "
								+ limsExcelFields.getProjectPlaatNummer());
						logger.info("Extract plaatnummer: "
								+ limsExcelFields.getExtractPlaatNummer());
						logger.info("Taxon naam: "
								+ limsExcelFields.getTaxonNaam());
						logger.info("Registrationnumber: "
								+ limsExcelFields.getRegistrationNumber());
						logger.info("Plaat positie: "
								+ limsExcelFields.getPlaatPositie());
						logger.info("Sample method: "
								+ limsExcelFields.getSubSample());

						limsExcelFields.setVersieNummer(version);

						logger.info("Start with adding notes to the document");
						setSamplesNotes(documents, cnt);
						logger.info("Done with adding notes to the document");

						verwerkList.add(ID);

					} // end IF

					if (!verwerkList.contains(ID) && !match) {
						limsExcelFields.setProjectPlaatNummer("");
						limsExcelFields.setPlaatPositie("");
						limsExcelFields.setExtractPlaatNummer("");
						if (record[3] != null) {
							limsExcelFields.setExtractID("");
						}
						limsExcelFields.setRegistrationNumber("");
						limsExcelFields.setTaxonNaam("");

						if (!msgUitvalList
								.contains("No document(s) match found for Registrationnumber: "
										+ record[3])) {
							msgUitvalList
									.add("No document(s) match found for Registrationnumber: "
											+ record[3] + "\n");
						}
					}
					match = false;
				}
			} catch (IOException e) {
				e.printStackTrace();
			} // end While
			try {
				csvReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Extract the ID from the filename
	 * 
	 * @param annotatedPluginDocuments
	 *            set the param
	 * @return
	 */
	private String getExtractIDFromAB1FileName(String fileName) {
		/* for example: e4010125015_Sil_tri_MJ243_COI-A01_M13F_A01_008.ab1 */
		String[] underscore = null;
		logger.info("Document Filename: " + fileName);
		if (fileName.contains("_") && fileName.contains("ab1")) {
			underscore = StringUtils.split(fileName, "_");
		} else if (fileName.contains("_") && !fileName.contains(".")) {
			underscore = StringUtils.split(fileName, "_");
		} else if (fileName.contains(".") && fileName.contains("dum")) {
			underscore = StringUtils.split(fileName, ".");
		} else if (fileName.contains("_")) {
			underscore = StringUtils.split(fileName, "_");
		} else {
			throw new IllegalArgumentException("String " + fileName
					+ " cannot be split. ");
		}
		return underscore[0];
	}

	@SuppressWarnings("unused")
	private boolean matchExtractId(
			AnnotatedPluginDocument annotatedPluginDocument, String extractID) {

		Object fieldValue = geneiousFieldsValues
				.readValueFromAnnotatedPluginDocument(annotatedPluginDocument,
						noteCode, fieldName);
		if (extractID.equals(fieldValue)) {
			return true;
		}
		return false;
	}
}
