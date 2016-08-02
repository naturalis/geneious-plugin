/**
 * 
 */
package nl.naturalis.lims2.ab1.importer;

import java.awt.EventQueue;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

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
	// private Lims2Connectie lims2Connectie = new Lims2Connectie();

	private String extractIDfileName = "";
	private List<String> msgList = new ArrayList<String>();
	private List<String> msgUitvalList = new ArrayList<String>();
	private List<String> verwerkingListCnt = new ArrayList<String>();
	private List<String> verwerkList = new ArrayList<String>();
	private List<String> lackYesList = new ArrayList<String>();
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
	private Object version = 0;
	private String recordDocumentName = "";
	private String readAssembyContigFileName = "";
	private int sampleRecordCntVerwerkt = 0;
	private int sampleRecordUitval = 0;
	private int sampleTotaalRecords = 0;
	private CSVReader csvReader = null;
	private int dummyRecordsVerwerkt = 0;
	private boolean match = false;
	private String logSamplesFileName = "";
	private LimsLogger limsLogger = null;
	private long startTime;
	long lEndTime = 0;
	long difference = 0;
	private int totalLack = 0;

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
		geneiousFieldsValues.activeDB = geneiousFieldsValues
				.getServerDatabaseServiceName();

		/*
		 * try { logger.info("Actieve Connectie: " +
		 * lims2Connectie.getSimpleConnectionServer(
		 * geneiousFieldsValues.resultDB).getCatalog()); } catch (SQLException
		 * e1) { e1.printStackTrace(); }
		 */

		if (geneiousFieldsValues.activeDB != null) {
			Object[] options = { "Ok", "No", "Cancel" };
			int n = JOptionPane.showOptionDialog(frame,
					"Create dummy sequences for unknown extract ID's?",
					"Samples", JOptionPane.YES_NO_CANCEL_OPTION,
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

					logSamplesFileName = limsImporterUtil.getLogPath()
							+ "Sample-method-Uitvallijst-"
							+ limsImporterUtil.getLogFilename();

					limsLogger = new LimsLogger(logSamplesFileName);

					/*
					 * for (int cnt = 0; cnt < DocumentUtilities
					 * .getSelectedDocuments().size(); cnt++) {
					 * isExtractIDSeqExists = documents[cnt].toString()
					 * .contains("MarkerCode_Seq"); }
					 * 
					 * if (!isExtractIDSeqExists) { Dialogs.showMessageDialog(
					 * "At least one selected document lacks Extract ID (Seq)."
					 * ); return; }
					 */

					logger.info("Start updating selected document(s).");
					fileSelected = fcd.loadSelectedFile();
					/** Add selected documents to a list. */

					if (fileSelected == null) {
						return;
					}
					logger.info("CSV file: " + fileSelected);
					/** Start reading data from the file selected */
					logger.info("-------------------------- S T A R T --------------------------");
					logger.info("Start Reading data from a samples file.");

					msgUitvalList.add("Filename: " + fileSelected + "\n");

					startTime = new Date().getTime();
					for (int cnt = 0; cnt < DocumentUtilities
							.getSelectedDocuments().size(); cnt++) {

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
								version = geneiousFieldsValues
										.getVersionValueFromAnnotatedPluginDocument(
												documents,
												"DocumentNoteUtilities-Document version",
												"DocumentVersionCode_Seq", cnt);
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

						isSampleDoc = DocumentUtilities.getSelectedDocuments()
								.iterator().next().toString()
								.contains("ExtractIDCode_Seq");

						if (sampleTotaalRecords == 0) {
							try {
								csvReader = new CSVReader(new FileReader(
										fileSelected), '\t', '\'', 1);
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
						limsFrameProgress.createProgressGUI();

						if (isExtractIDSeqExists) {
							try {
								readDataFromExcel(fileSelected,
										extractIDfileName, documents, cnt);
							} catch (IOException e) {

								e.printStackTrace();
							}
						} else {
							limsFrameProgress
									.showProgress("At least one selected document lacks Extract ID (Seq)."
											+ DocumentUtilities
													.getSelectedDocuments()
													.get(cnt).getName());
							logger.info("At least one selected document lacks Extract ID (Seq)."
									+ DocumentUtilities.getSelectedDocuments()
											.get(cnt).getName());
							if (!lackYesList.contains(DocumentUtilities
									.getSelectedDocuments().get(cnt).getName())) {
								logger.info("At least one selected document lacks Extract ID (Seq)."
										+ DocumentUtilities
												.getSelectedDocuments()
												.get(cnt).getName());
								lackYesList.add(DocumentUtilities
										.getSelectedDocuments().get(cnt)
										.getName());
							}
						}

						importCounter = DocumentUtilities
								.getSelectedDocuments().size();

						result = "";
					}

					logger.info("--------------------------------------------------------");
					logger.info("Total of document(s) updated: "
							+ verwerkList.size());
					limsFrameProgress.hideFrame();

					/* Set for creating dummy files */
					if (isSampleDoc) {
						limsFrameProgress.createProgressGUI();
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

					lEndTime = new Date().getTime();
					difference = lEndTime - startTime;
					String hms = String.format("%02d:%02d:%02d",
							TimeUnit.MILLISECONDS.toHours(difference),
							TimeUnit.MILLISECONDS.toMinutes(difference)
									% TimeUnit.HOURS.toMinutes(1),
							TimeUnit.MILLISECONDS.toSeconds(difference)
									% TimeUnit.MINUTES.toSeconds(1));
					logger.info("Import records in : '" + hms
							+ " hour(s)/minute(s)/second(s).'");
					logger.info("Import records in : '"
							+ TimeUnit.MILLISECONDS.toMinutes(difference)
							+ " minutes.'");

					EventQueue.invokeLater(new Runnable() {

						@Override
						public void run() {

							if (isExtractIDSeqExists) {
								sampleRecordUitval = msgUitvalList.size() - 1;
							} else {
								sampleRecordUitval = msgUitvalList.size() - 2;
							}

							Dialogs.showMessageDialog(Integer
									.toString(sampleTotaalRecords)
									+ " sample records have been read of which: "
									+ "\n"
									+ "[1] "
									+ Integer.toString(verwerkList.size())
									+ " samples are imported and linked to "
									+ Integer.toString(sampleRecordCntVerwerkt)
									+ " existing documents (of "
									+ importCounter
									+ " selected)"
									+ "\n"
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
									+ " sample records are ignored."
									+ "\n"
									+ "\n"
									+ "[4] "
									+ "At least one or "
									+ Integer.toString(lackYesList.size())
									+ " selected document lacks Extract ID (Seq).");

							logger.info("Sample-method: Total imported document(s): "
									+ msgList.toString());

							limsLogger.logToFile(logSamplesFileName,
									msgUitvalList.toString());

							msgList.clear();
							msgUitvalList.clear();
							verwerkingListCnt.clear();
							verwerkList.clear();
							sampleRecordUitval = 0;
							sampleRecordCntVerwerkt = 0;
							dummyRecordsVerwerkt = 0;
							lackYesList.clear();
						}
					});
				}
			} else if (n == 1) {

				fileSelected = fcd.loadSelectedFile();
				limsFrameProgress.createProgressGUI();
				setExtractIDMatchSamplesSheetRecords(fileSelected, documents);
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

		/** set note for Taxonomy Name */
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

		/*
		 * Lims-190:Sample import maak of update extra veld veldnaam -
		 * Registr-nmbr_[Scientific name] (Samples) en veldcode =
		 * RegistrationNumberCode_TaxonName2Code_Samples
		 */
		limsNotes.setNoteToAB1FileName(documents,
				"RegistrationNumberCode_TaxonName2Code_Samples",
				"Registr-nmbr_[Scientific name] (Samples)",
				"Registr-nmbr_[Scientific name] (Samples)",
				limsExcelFields.getRegNumberScientificName(), cnt);
	}

	@Override
	public GeneiousActionOptions getActionOptions() {
		return new GeneiousActionOptions("1 of 2 Samples").setInPopupMenu(true)
				.setMainMenuLocation(GeneiousActionOptions.MainMenu.Tools, 1.0)
				.setInMainToolbar(true).setInPopupMenu(true)
				.setAvailableToWorkflows(true);

		// GeneiousActionOptions parent = new GeneiousActionOptions("Naturalis",
		// "Samples import")
		// .setMainMenuLocation(GeneiousActionOptions.MainMenu.Tools, 2.0)
		// .setInMainToolbar(true).setInPopupMenu(true);
		// GeneiousActionOptions submenuItem1 = new GeneiousActionOptions(
		// "1 of 2 Samples", "Samples Import");
		// GeneiousActionOptions sub2 =
		// parent.createSubmenuActionOptions(parent,
		// submenuItem1);
		//
		// return sub2;
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
						if (record.length == 1 && record[0].isEmpty()) {
							continue;
						}

						long startBeginTime = System.nanoTime();

						if (record[3].trim() != null) {
							ID = "e" + record[3];
						}

						if (record[2].length() > 0 && record[2].contains("-")) {
							plateNumber = record[2].substring(0,
									record[2].indexOf("-"));
						}

						String dummyFile = geneiousFieldsValues
								.getFastaIDForSamples_GeneiousDB(ID);

						if (dummyFile.length() > 0) {
							dummyFile = getExtractIDFromAB1FileName(dummyFile);
						}

						if (!dummyFile.equals(ID)) {
							limsFrameProgress
									.showProgress("Creating dummy document: "
											+ ID);

							if (ID.equals("e")
									&& LimsImporterUtil.extractNumber(ID)
											.isEmpty()) {
								logger.info("Record is empty: " + ID);
							} else {
								limsDummySeq.createDummySampleSequence(ID, ID,
										record[0], plateNumber, record[5],
										record[4], record[1], record[6]);
								dummyRecordsVerwerkt++;
							}
						} else {
							limsFrameProgress.showProgress("Dummy document: "
									+ ID + " already exists.");
						}
						long endTime = System.nanoTime();
						long elapsedTime = endTime - startBeginTime;
						logger.info("Took: "
								+ (TimeUnit.SECONDS.convert(elapsedTime,
										TimeUnit.NANOSECONDS)) + " second(s)");
						elapsedTime = 0;
						logger.info("-----------------");

					} // end While

					/*
					 * if (dummyRecordsVerwerkt == 0) {
					 * Dialogs.showMessageDialog("[3] " + dummyRecordsVerwerkt +
					 * "(zero). dummy samples are ignored."); }
					 */
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

	/* Uitvoeren "NO" keuze */
	private void setExtractIDMatchSamplesSheetRecords(String fileName,
			AnnotatedPluginDocument[] docsSamples) {

		List<String> UitvalList = new ArrayList<String>();
		List<String> exactVerwerkList = new ArrayList<String>();
		List<String> lackList = new ArrayList<String>();

		try {
			if (fileName != null) {
				logger.info("Read samples file: " + fileName);
				CSVReader csvReader = new CSVReader(new FileReader(fileName),
						'\t', '\'', 0);
				csvReader.readNext();

				logSamplesFileName = limsImporterUtil.getLogPath()
						+ "Sample-method-Uitvallijst-"
						+ limsImporterUtil.getLogFilename();

				limsLogger = new LimsLogger(logSamplesFileName);

				try {
					while ((record = csvReader.readNext()) != null) {
						if (record.length == 1 && record[0].isEmpty()) {
							continue;
						}

						if (record[3].trim() != null) {
							ID = "e" + record[3];
						}

						if (record[2].length() > 0 && record[2].contains("-")) {
							plateNumber = record[2].substring(0,
									record[2].indexOf("-"));
						}

						/*
						 * String dummyFile = geneiousFieldsValues
						 * .getFastaIDForSamples_GeneiousDB(ID);
						 * 
						 * if (dummyFile.length() > 0) { dummyFile =
						 * getExtractIDFromAB1FileName(dummyFile); }
						 */

						Boolean isMatched = false;
						if (!DocumentUtilities.getSelectedDocuments().isEmpty()) {
							docs = DocumentUtilities.getSelectedDocuments();
						}
						for (int cnt = 0; cnt < docs.size(); cnt++) {

							if ((docs.get(cnt).toString().contains("fas"))
									|| (docs.get(cnt).toString()
											.contains("ab1"))
									|| (docs.get(cnt).toString()
											.contains("dum"))) {
								extractIDfileName = getExtractIDFromAB1FileName(docs
										.get(cnt).getName());
							}

							isExtractIDSeqExists = docs.get(cnt).toString()
									.contains("MarkerCode_Seq");

							if (!isExtractIDSeqExists) {
								if (!lackList.contains(DocumentUtilities
										.getSelectedDocuments().get(cnt)
										.getName())) {
									limsFrameProgress
											.showProgress("At least one selected document lacks Extract ID (Seq)."
													+ "\n"
													+ docs.get(cnt).getName());
									logger.info("At least one selected document lacks Extract ID (Seq)."
											+ docs.get(cnt).getName());
									lackList.add(docs.get(cnt).getName());
								}

							}

							if (extractIDfileName.equals(ID)
									&& isExtractIDSeqExists) {
								isMatched = true;
								if (!exactVerwerkList.contains(ID)) {
									exactVerwerkList.add(ID);
								}
								limsFrameProgress
										.showProgress("Document match: " + ID);
								setFieldsValues(record[0], record[1],
										plateNumber, ID, record[4], record[5],
										version);
								logger.info("Start with adding notes to the document");
								setSamplesNotes(docsSamples, cnt);
								logger.info("Done with adding notes to the document");
							}
						} // For
						if (!exactVerwerkList.contains(ID) && !isMatched) {
							if (!UitvalList.contains(ID)
									&& !limsImporterUtil.isAlpha(ID)) {
								UitvalList
										.add("No document(s) match found for Registrationnumber: "
												+ ID + "\n");
							}
						}
						isMatched = false;
					} // end While

					if (exactVerwerkList.size() > 0 || !isExtractIDSeqExists) {
						/*
						 * int exactLack = 0; if (exactVerwerkList.size() > 0) {
						 * exactLack = docs.size() - exactVerwerkList.size(); }
						 * else { exactLack = lackList.size(); }
						 */

						Dialogs.showMessageDialog(readTotalRecordsOfFileSelected(fileName)
								+ " sample records have been read of which: "
								+ "\n"
								+ "\n"
								+ "[1] "
								+ Integer.toString(exactVerwerkList.size())
								+ " samples are imported and linked to "
								+ Integer.toString(docs.size())
								+ " existing documents (of "
								+ docs.size()
								+ " selected)"
								+ "\n"
								+ "\n"
								+ "[2] "
								+ Integer.toString(UitvalList.size())
								+ " samples records are ignored."
								+ "\n"
								+ "\n"
								+ "[3] "
								+ "At least one or "
								+ Integer.toString(lackList.size())
								+ " selected document lacks Extract ID (Seq).");

						UitvalList.add("Total records not matched: "
								+ Integer.toString(UitvalList.size()) + "\n");

						limsLogger.logToFile(logSamplesFileName,
								UitvalList.toString());
						UitvalList.clear();
						exactVerwerkList.clear();
						lackList.clear();
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

	private int readTotalRecordsOfFileSelected(String fileName) {
		int result = 0;
		if (result == 0) {
			try {
				csvReader = new CSVReader(new FileReader(fileName), '\t', '\'',
						1);
				result = csvReader.readAll().size();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			csvReader = null;
		}
		return result;
	}

	private void readDataFromExcel(String fileName, String extractID,
			AnnotatedPluginDocument[] documents, int cnt) throws IOException {

		msgUitvalList.clear();
		try {
			csvReader = new CSVReader(new FileReader(fileName), '\t', '\'', 0);
			csvReader.readNext();

			/** Show the progress bar */

			limsFrameProgress.showProgress(documents[cnt].getName());

			long startBeginTime = System.nanoTime();
			try {
				while ((record = csvReader.readNext()) != null) {
					if (record.length == 1 && record[0].isEmpty()) {
						continue;
					}

					ID = "e" + record[3];

					if (record[2].length() > 0 && record[2].contains("-")) {
						plateNumber = record[2].substring(0,
								record[2].indexOf("-"));
					}

					isExtractIDSeqExists = documents[cnt].toString().contains(
							"MarkerCode_Seq");

					if (ID.equals(extractID) && isExtractIDSeqExists) {

						match = true;
						sampleRecordCntVerwerkt++;

						msgList.add(extractID);

						setFieldsValues(record[0], record[1], plateNumber, ID,
								record[4], record[5], (String) version);

						logger.info("Start with adding notes to the document");
						setSamplesNotes(documents, cnt);
						logger.info("Done with adding notes to the document");

						if (!verwerkList.contains(ID)) {
							verwerkList.add(ID);
						}
						long endTime = System.nanoTime();
						long elapsedTime = endTime - startBeginTime;
						logger.info("Took: "
								+ (TimeUnit.SECONDS.convert(elapsedTime,
										TimeUnit.NANOSECONDS)) + " second(s)");
						elapsedTime = 0;
						logger.info("-----------------");

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

						if (!msgUitvalList.contains(record[3])) {
							if (record[3].length() == 1 && record[3].isEmpty()) {
								msgUitvalList
										.add("No document(s) match found for Registrationnumber: "
												+ record[3] + "\n");
							}
						}
					}
					match = false;
				} // While
				totalLack = lackYesList.size();
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
		// logger.info("Document Filename: " + fileName);
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

	private void setFieldsValues(String projectPlaatNr, String plaatPositie,
			String extractPlaatNr, String extractID, String registrationNumber,
			String taxonNaam, Object versieNummer) {

		limsExcelFields.setProjectPlaatNummer(projectPlaatNr); // record[0]
		limsExcelFields.setPlaatPositie(plaatPositie); // record[1]
		limsExcelFields.setExtractPlaatNummer(extractPlaatNr);
		if (extractID != null) {
			limsExcelFields.setExtractID(extractID);
		} else {
			limsExcelFields.setExtractID("");
		}
		limsExcelFields.setRegistrationNumber(registrationNumber); // record[4]
		limsExcelFields.setTaxonNaam(taxonNaam); // record[5]

		String regScientificname = "";
		if (registrationNumber.length() > 0 && taxonNaam.length() > 0) {
			regScientificname = registrationNumber + " " + taxonNaam;
		} else {
			regScientificname = registrationNumber;
		}
		limsExcelFields.setRegNumberScientificName(regScientificname);

		logger.info("Extract-ID: " + limsExcelFields.getExtractID());
		logger.info("Project plaatnummer: "
				+ limsExcelFields.getProjectPlaatNummer());
		logger.info("Extract plaatnummer: "
				+ limsExcelFields.getExtractPlaatNummer());
		logger.info("Taxon naam: " + limsExcelFields.getTaxonNaam());
		logger.info("Registrationnumber: "
				+ limsExcelFields.getRegistrationNumber());
		logger.info("Plaat positie: " + limsExcelFields.getPlaatPositie());
		logger.info("Sample method: " + limsExcelFields.getSubSample());
		logger.info("Registr-nmbr_[Scientific name] (Samples): "
				+ limsExcelFields.getRegNumberScientificName());

		limsExcelFields.setVersieNummer(versieNummer);

	}
}
