/**
 * 
 */
package nl.naturalis.lims2.ab1.importer;

import java.awt.EventQueue;
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
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;
import com.opencsv.CSVReader;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsImportSamples extends DocumentAction {

	private LimsImporterUtil limsImporterUtil = new LimsImporterUtil();
	private LimsSamplesFields limsExcelFields = new LimsSamplesFields();
	private LimsNotes limsNotes = new LimsNotes();
	private LimsFileSelector fcd = new LimsFileSelector();
	private LimsReadGeneiousFieldsValues readGeneiousFieldsValues = new LimsReadGeneiousFieldsValues();
	private LimsDummySeq limsDummySeq = new LimsDummySeq();
	private static final Logger logger = LoggerFactory
			.getLogger(LimsImportSamples.class);
	private LimsFrameProgress limsFrameProgress = new LimsFrameProgress();
	private JFrame frame = new JFrame();
	private LimsLogger limsLogger = null;

	private List<AnnotatedPluginDocument> docs;
	private List<String> msgList = new ArrayList<String>();
	private List<String> UitvalList = new ArrayList<String>();
	private List<String> verwerkList = new ArrayList<String>();
	private List<String> verwerkingListCnt = new ArrayList<String>();

	private CSVReader csvReader = null;

	private String fileSelected = "";

	private String ID = "";
	private String plateNumber = "";
	private String extractIDfileName = "";
	private String logSamplesFileName = "";
	private String documentFileName = "";
	private String readAssembyContigFileName = "";
	// private String recordDocumentName = "";
	private String result = "";

	private boolean isExtractIDSeqExists = false;
	private boolean match = false;
	private boolean isSampleDoc = false;

	private int sampleRecordVerwerkt = 0;
	private int version = 0;
	private int dummyRecordsVerwerkt = 0;
	private long startTime;
	private long lEndTime = 0;
	private long difference = 0;
	private int sampleTotaalRecords = 0;
	private int importCounter = 0;
	private int sampleRecordUitval = 0;
	private int sampleExactRecordsVerwerkt = 0;
	private int recordCount = 0;
	private int exactVerwerkt = 0;

	@Override
	public void actionPerformed(
			AnnotatedPluginDocument[] annotatedPluginDocument) {
		readDataFromExcel(annotatedPluginDocument);

	}

	@Override
	public GeneiousActionOptions getActionOptions() {
		return new GeneiousActionOptions("7 of 8 Samples new")
				.setInPopupMenu(true)
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

	private void readDataFromExcel(AnnotatedPluginDocument[] documents) {

		/* Get Database name */
		readGeneiousFieldsValues.resultDB = readGeneiousFieldsValues
				.getServerDatabaseServiceName();

		if (readGeneiousFieldsValues.resultDB != null) {

			Object[] options = { "Ok", "No", "Cancel" };
			int n = JOptionPane.showOptionDialog(frame,
					"Create dummy sequences for unknown extract ID's?",
					"Samples", JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
			if (n == 0) {

				/** Check if document(s) has been selected **/
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

					// docs = DocumentUtilities.getSelectedDocuments();

					logSamplesFileName = limsImporterUtil.getLogPath()
							+ "Sample-method-Uitvallijst-"
							+ limsImporterUtil.getLogFilename();

					limsLogger = new LimsLogger(logSamplesFileName);

					for (int cnt = 0; cnt < DocumentUtilities
							.getSelectedDocuments().size(); cnt++) {
						isExtractIDSeqExists = documents[cnt].toString()
								.contains("MarkerCode_Seq");
					}

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

					/* Create the progressbar */
					limsFrameProgress.createProgressBar();

					/** Start reading data from the file selected */
					logger.info("-------------------------- S T A R T --------------------------");
					logger.info("Start Reading data from a samples file.");
					logger.info("CSV file: " + fileSelected);

					UitvalList.clear();
					UitvalList.add("Filename: " + fileSelected + "\n");
					UitvalList
							.add("-----------------------------------------------"
									+ "\n");
					startTime = new Date().getTime();

					try {
						csvReader = new CSVReader(new FileReader(fileSelected),
								'\t', '\'', 0);
						csvReader.readNext();

						importCounter = DocumentUtilities
								.getSelectedDocuments().size();

						String[] record = null;
						while ((record = csvReader.readNext()) != null) {
							if (record.length == 0) {
								continue;
							}

							long startBeginTime = System.nanoTime();

							ID = "e" + record[3];

							for (int cnt = 0; cnt < DocumentUtilities
									.getSelectedDocuments().size(); cnt++) {

								isExtractIDSeqExists = readGeneiousFieldsValues
										.getValueFromAnnotatedPluginDocument(
												documents[cnt],
												"DocumentNoteUtilities-Extract ID (Seq)",
												"ExtractIDCode_Seq");

								/* Read the cache_name from the document */
								documentFileName = (String) DocumentUtilities
										.getSelectedDocuments().get(cnt)
										.getFieldValue("cache_name");

								/*
								 * Read the override_cache_name from the
								 * document
								 */
								readAssembyContigFileName = (String) DocumentUtilities
										.getSelectedDocuments().get(cnt)
										.getFieldValue("override_cache_name");

								/*
								 * Compare the cacahe_name with the name of the
								 * document
								 */
								if (documentFileName.equals(DocumentUtilities
										.getSelectedDocuments().get(cnt)
										.getName())) {

									if (!DocumentUtilities
											.getSelectedDocuments().toString()
											.contains("consensus sequence")
											|| !DocumentUtilities
													.getSelectedDocuments()
													.toString()
													.contains("Contig")) {
										version = Integer
												.parseInt((String) readGeneiousFieldsValues
														.getVersionValueFromAnnotatedPluginDocument(
																documents,
																"DocumentNoteUtilities-Document version",
																"DocumentVersionCode_Seq",
																cnt));
									}
								}

								/* Check if name is from a Contig file */
								if ((readAssembyContigFileName != null)
										&& readAssembyContigFileName
												.toString()
												.contains(
														"Reads Assembly Contig")) {
									documentFileName = DocumentUtilities
											.getSelectedDocuments().get(cnt)
											.getName();
								} /*
								 * Check if name is from a Consensus document
								 */
								else if (DocumentUtilities
										.getSelectedDocuments().get(cnt)
										.getName().toString()
										.contains("consensus sequence")) {
									documentFileName = DocumentUtilities
											.getSelectedDocuments().get(cnt)
											.getName();
								} /*
								 * Check if name is from a dummy document
								 */
								else if (DocumentUtilities
										.getSelectedDocuments().get(cnt)
										.getName().toString().contains("dum")) {
									documentFileName = DocumentUtilities
											.getSelectedDocuments().get(cnt)
											.getName();
								} /* from a imported file */
								else {
									documentFileName = (String) readGeneiousFieldsValues
											.readValueFromAnnotatedPluginDocument(
													documents[cnt],
													"importedFrom", "filename");
								}

								/* Get file name from the document(s) */
								if (documentFileName.toString().contains("ab1")
										|| documentFileName.toString()
												.contains("fas")
										|| documentFileName.toString()
												.contains("dum")) {
									/*
									 * Value from the Database table field
									 */
									result = readGeneiousFieldsValues
											.getFileNameFromGeneiousDatabase(
													DocumentUtilities
															.getSelectedDocuments()
															.get(cnt).getName(),
													"//XMLSerialisableRootElement/name");
								}

								if ((result != null && documentFileName
										.toString().contains("fas"))
										|| (result.toString().contains("ab1"))
										|| (result.toString().contains("dum"))) {
									extractIDfileName = getExtractIDFromAB1FileName(DocumentUtilities
											.getSelectedDocuments().get(cnt)
											.getName());
								} else if (DocumentUtilities
										.getSelectedDocuments().get(cnt)
										.getName().toString()
										.contains("consensus sequence")
										|| DocumentUtilities
												.getSelectedDocuments()
												.get(cnt).getName().toString()
												.contains("Contig")) {
									extractIDfileName = DocumentUtilities
											.getSelectedDocuments().get(cnt)
											.getName();
								}

								isSampleDoc = DocumentUtilities
										.getSelectedDocuments().iterator()
										.next().toString()
										.contains("ExtractIDCode_Seq");

								if (sampleTotaalRecords == 0) {
									CSVReader reader = new CSVReader(
											new FileReader(fileSelected), '\t',
											'\'', 0);
									sampleTotaalRecords = reader.readAll()
											.size();
									reader.close();
								}

								/** Create progress bar */
								limsFrameProgress.createProgressBar();

								if (isExtractIDSeqExists) {

									/*
									 * Check if record(PlateNumber) contain "-"
									 */
									if (record[2].length() > 0
											&& record[2].contains("-")) {
										plateNumber = record[2].substring(0,
												record[2].indexOf("-"));
									}

									/*
									 * ID (ID = record[0]) match extractid from
									 * the filename
									 */
									if (ID.equals(extractIDfileName)) {

										match = true;
										sampleRecordVerwerkt++;

										msgList.add(extractIDfileName + "\n");

										recordCount++;
										/** Show the progress bar */
										limsFrameProgress
												.showProgress("Match : "
														+ extractIDfileName
														+ "\n"
														+ "  Recordcount: "
														+ recordCount);

										limsExcelFields
												.setProjectPlaatNummer(record[0]);
										limsExcelFields
												.setPlaatPositie(record[1]);
										limsExcelFields
												.setExtractPlaatNummer(plateNumber);
										if (ID != null) {
											limsExcelFields.setExtractID(ID);
										} else {
											limsExcelFields.setExtractID("");
										}
										limsExcelFields
												.setRegistrationNumber(record[4]);
										limsExcelFields.setTaxonNaam(record[5]);
										limsExcelFields
												.setVersieNummer(version);

										logger.info("Document Filename: "
												+ documentFileName);

										logger.info("Start with adding notes to the document");
										setSamplesNotes(documents, cnt);
										logger.info("Done with adding notes to the document");

										if (!verwerkList.contains(ID)) {
											verwerkList.add(ID);
										}

										long endTime = System.nanoTime();
										long elapsedTime = endTime
												- startBeginTime;
										logger.info("Took: "
												+ (TimeUnit.SECONDS.convert(
														elapsedTime,
														TimeUnit.NANOSECONDS))
												+ " second(s)");
										elapsedTime = 0;
										match = false;

									} // end IF
									else if (!verwerkList.contains(ID)
											&& !match) {
										recordCount++;

										limsExcelFields
												.setProjectPlaatNummer("");
										limsExcelFields.setPlaatPositie("");
										limsExcelFields
												.setExtractPlaatNummer("");
										if (record[3] != null) {
											limsExcelFields.setExtractID("");
										}
										limsExcelFields
												.setRegistrationNumber("");
										limsExcelFields.setTaxonNaam("");

										if (!UitvalList.contains(ID)) {
											UitvalList
													.add("No document(s) match found for Registrationnumber: "
															+ ID + "\n");

											limsFrameProgress
													.showProgress("No match : "
															+ ID + "\n"
															+ "  Recordcount: "
															+ recordCount);
										}
									}

								}

								result = "";
							} // end For

						} // end While
						logger.info("--------------------------------------------------------");
						logger.info("Total of document(s) updated: "
								+ docs.size());

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
							UitvalList.add("Total records not matched: "
									+ Integer.toString(UitvalList.size())
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
						logger.info("Totaal records verwerkt: "
								+ verwerkList.size());

						EventQueue.invokeLater(new Runnable() {

							@Override
							public void run() {
								sampleRecordUitval = UitvalList.size() - 1;
								sampleExactRecordsVerwerkt = verwerkList.size();

								Dialogs.showMessageDialog(Integer
										.toString(sampleTotaalRecords)
										+ " sample records have been read of which: "
										+ "\n"
										+ "[1] "
										+ Integer
												.toString(sampleExactRecordsVerwerkt)
										+ " samples are imported and linked to "
										+ Integer
												.toString(sampleRecordVerwerkt)
										+ " existing documents (of "
										+ importCounter
										+ " selected) \n"
										+ "[2] "
										+ Integer
												.toString(dummyRecordsVerwerkt)
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

								limsLogger.logToFile(logSamplesFileName,
										UitvalList.toString());

								msgList.clear();
								UitvalList.clear();
								verwerkingListCnt.clear();
								verwerkList.clear();
								sampleExactRecordsVerwerkt = 0;
								sampleRecordUitval = 0;
								sampleRecordVerwerkt = 0;
								dummyRecordsVerwerkt = 0;
								recordCount = 0;
								limsFrameProgress.hideFrame();
							}
						});
					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						csvReader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
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

	/*
	 * private void readDataFromExcel(AnnotatedPluginDocument[] documents) {
	 * 
	 * Get Database name readGeneiousFieldsValues.resultDB =
	 * readGeneiousFieldsValues .getServerDatabaseServiceName();
	 * 
	 * if (readGeneiousFieldsValues.resultDB != null) {
	 * 
	 * Object[] options = { "Ok", "No", "Cancel" }; int n =
	 * JOptionPane.showOptionDialog(frame,
	 * "Create dummy sequences for unknown extract ID's?", "Samples",
	 * JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
	 * options, options[2]); if (n == 0) {
	 *//** Check if document(s) has been selected **/
	/*
	 * if (DocumentUtilities.getSelectedDocuments().isEmpty()) {
	 * EventQueue.invokeLater(new Runnable() {
	 * 
	 * @Override public void run() {
	 * 
	 * Dialogs.showMessageDialog("Select at least one document"); return; } });
	 * }
	 * 
	 * if (!DocumentUtilities.getSelectedDocuments().isEmpty()) {
	 * 
	 * // docs = DocumentUtilities.getSelectedDocuments();
	 * 
	 * logSamplesFileName = limsImporterUtil.getLogPath() +
	 * "Sample-method-Uitvallijst-" + limsImporterUtil.getLogFilename();
	 * 
	 * limsLogger = new LimsLogger(logSamplesFileName);
	 * 
	 * for (int cnt = 0; cnt < DocumentUtilities .getSelectedDocuments().size();
	 * cnt++) { isExtractIDSeqExists = documents[cnt].toString()
	 * .contains("MarkerCode_Seq"); }
	 * 
	 * if (!isExtractIDSeqExists) { Dialogs.showMessageDialog(
	 * "At least one selected document lacks Extract ID (Seq)."); return; }
	 * 
	 * logger.info("Start updating selected document(s)."); fileSelected =
	 * fcd.loadSelectedFile();
	 *//** Add selected documents to a list. */
	/*
	 * if (fileSelected == null) { return; }
	 * 
	 * Create the progressbar limsFrameProgress.createProgressBar();
	 *//** Start reading data from the file selected */
	/*
	 * logger.info("-------------------------- S T A R T --------------------------"
	 * ); logger.info("Start Reading data from a samples file.");
	 * logger.info("CSV file: " + fileSelected);
	 * 
	 * UitvalList.clear(); UitvalList.add("Filename: " + fileSelected + "\n");
	 * UitvalList .add("-----------------------------------------------" +
	 * "\n"); startTime = new Date().getTime();
	 * 
	 * try { csvReader = new CSVReader(new FileReader(fileSelected), '\t', '\'',
	 * 0); csvReader.readNext();
	 * 
	 * importCounter = DocumentUtilities .getSelectedDocuments().size();
	 * 
	 * String[] record = null; while ((record = csvReader.readNext()) != null) {
	 * if (record.length == 0) { continue; }
	 * 
	 * long startBeginTime = System.nanoTime();
	 * 
	 * ID = "e" + record[3];
	 * 
	 * for (int cnt = 0; cnt < DocumentUtilities .getSelectedDocuments().size();
	 * cnt++) {
	 * 
	 * isExtractIDSeqExists = readGeneiousFieldsValues
	 * .getValueFromAnnotatedPluginDocument( documents[cnt],
	 * "DocumentNoteUtilities-Extract ID (Seq)", "ExtractIDCode_Seq");
	 * 
	 * Read the cache_name from the document
	 * 
	 * documentFileName = (String) docs.get(cnt) .getFieldValue("cache_name");
	 * 
	 * 
	 * documentFileName = (String) DocumentUtilities
	 * .getSelectedDocuments().get(cnt) .getFieldValue("cache_name");
	 * 
	 * 
	 * Read the override_cache_name from the document
	 * 
	 * readAssembyContigFileName = (String) DocumentUtilities
	 * .getSelectedDocuments().get(cnt) .getFieldValue("override_cache_name");
	 * 
	 * 
	 * Compare the cacahe_name with the name of the document
	 * 
	 * if (documentFileName.equals(DocumentUtilities
	 * .getSelectedDocuments().get(cnt) .getName())) {
	 * 
	 * if (!documentFileName.toString().contains( "consensus sequence") ||
	 * !documentFileName.toString() .contains("Contig")) { if
	 * (readGeneiousFieldsValues .getVersionValueFromAnnotatedPluginDocument(
	 * documents, "DocumentNoteUtilities-Document version",
	 * "DocumentVersionCode_Seq", cnt) != null) { version = Integer
	 * .parseInt((String) readGeneiousFieldsValues
	 * .getVersionValueFromAnnotatedPluginDocument( documents,
	 * "DocumentNoteUtilities-Document version", "DocumentVersionCode_Seq",
	 * cnt)); } else version = 1; } }
	 * 
	 * Check if name is from a Contig file if ((readAssembyContigFileName !=
	 * null) && readAssembyContigFileName .toString() .contains(
	 * "Reads Assembly Contig")) { documentFileName = DocumentUtilities
	 * .getSelectedDocuments().get(cnt) .getName(); } Check if name is from a
	 * Consensus document
	 * 
	 * else if (DocumentUtilities .getSelectedDocuments().get(cnt)
	 * .getName().toString() .contains("consensus sequence")) { documentFileName
	 * = DocumentUtilities .getSelectedDocuments().get(cnt) .getName(); } Check
	 * if name is from a dummy document
	 * 
	 * else if (DocumentUtilities .getSelectedDocuments().get(cnt)
	 * .getName().toString().contains("dum")) { documentFileName =
	 * DocumentUtilities .getSelectedDocuments().get(cnt) .getName(); } from a
	 * imported file else { documentFileName = (String) readGeneiousFieldsValues
	 * .readValueFromAnnotatedPluginDocument( documents[cnt], "importedFrom",
	 * "filename"); }
	 * 
	 * Get file name from the document(s) if
	 * (documentFileName.toString().contains("ab1") ||
	 * documentFileName.toString() .contains("fas") ||
	 * documentFileName.toString() .contains("dum")) {
	 * 
	 * Value from the Database table field
	 * 
	 * result = readGeneiousFieldsValues .getFileNameFromGeneiousDatabase(
	 * DocumentUtilities .getSelectedDocuments() .get(cnt).getName(),
	 * "//XMLSerialisableRootElement/name"); }
	 * 
	 * if ((result != null && documentFileName .toString().contains("fas")) ||
	 * (result.toString().contains("ab1")) ||
	 * (result.toString().contains("dum"))) { extractIDfileName =
	 * getExtractIDFromAB1FileName(DocumentUtilities
	 * .getSelectedDocuments().get(cnt) .getName()); } else if
	 * (DocumentUtilities .getSelectedDocuments().get(cnt) .getName().toString()
	 * .contains("consensus sequence") || DocumentUtilities
	 * .getSelectedDocuments() .get(cnt).getName().toString()
	 * .contains("Contig")) { extractIDfileName = DocumentUtilities
	 * .getSelectedDocuments().get(cnt).getName(); }
	 * 
	 * isSampleDoc = DocumentUtilities .getSelectedDocuments().iterator()
	 * .next().toString() .contains("ExtractIDCode_Seq");
	 * 
	 * if (sampleTotaalRecords == 0) { CSVReader reader = new CSVReader( new
	 * FileReader(fileSelected), '\t', '\'', 0); sampleTotaalRecords =
	 * reader.readAll() .size(); reader.close(); }
	 *//** Create progress bar */
	/*
	 * limsFrameProgress.createProgressBar();
	 * 
	 * if (isExtractIDSeqExists) {
	 * 
	 * 
	 * Check if record(PlateNumber) contain "-"
	 * 
	 * if (record[2].length() > 0 && record[2].contains("-")) { plateNumber =
	 * record[2].substring(0, record[2].indexOf("-")); }
	 * 
	 * 
	 * ID (ID = record[0]) match extractid from the filename
	 * 
	 * if (ID.equals(extractIDfileName)) {
	 * 
	 * match = true; sampleRecordVerwerkt++;
	 * 
	 * msgList.add(extractIDfileName + "\n");
	 * 
	 * recordCount++;
	 *//** Show the progress bar */
	/*
	 * limsFrameProgress .showProgress("Match : " + extractIDfileName + "\n" +
	 * "  Recordcount: " + recordCount);
	 * 
	 * limsExcelFields .setProjectPlaatNummer(record[0]); limsExcelFields
	 * .setPlaatPositie(record[1]); limsExcelFields
	 * .setExtractPlaatNummer(plateNumber); if (ID != null) {
	 * limsExcelFields.setExtractID(ID); } else {
	 * limsExcelFields.setExtractID(""); } limsExcelFields
	 * .setRegistrationNumber(record[4]);
	 * limsExcelFields.setTaxonNaam(record[5]); limsExcelFields
	 * .setVersieNummer(version);
	 * 
	 * logger.info("Document Filename: " + documentFileName);
	 * 
	 * logger.info("Start with adding notes to the document");
	 * setSamplesNotes(documents, cnt);
	 * logger.info("Done with adding notes to the document");
	 * 
	 * if (!verwerkList.contains(ID)) { verwerkList.add(ID); }
	 * 
	 * long endTime = System.nanoTime(); long elapsedTime = endTime -
	 * startBeginTime; logger.info("Took: " + (TimeUnit.SECONDS.convert(
	 * elapsedTime, TimeUnit.NANOSECONDS)) + " second(s)"); elapsedTime = 0;
	 * 
	 * } // end IF }
	 * 
	 * match = false;
	 * 
	 * result = ""; } // end For
	 * 
	 * if (!verwerkList.contains(ID) && !match) { recordCount++;
	 * 
	 * limsExcelFields.setProjectPlaatNummer("");
	 * limsExcelFields.setPlaatPositie("");
	 * limsExcelFields.setExtractPlaatNummer(""); if (record[3] != null) {
	 * limsExcelFields.setExtractID(""); }
	 * limsExcelFields.setRegistrationNumber("");
	 * limsExcelFields.setTaxonNaam("");
	 * 
	 * if (!UitvalList.contains(ID)) { UitvalList
	 * .add("No document(s) match found for Registrationnumber: " + ID + "\n");
	 * 
	 * 
	 * limsFrameProgress .showProgress("No match : " + ID + "\n" +
	 * "  Recordcount: " + recordCount);
	 * 
	 * }
	 * 
	 * } } // end While
	 * logger.info("--------------------------------------------------------");
	 * logger.info("Total of document(s) updated: " + DocumentUtilities
	 * .getSelectedDocuments().size());
	 * 
	 * Set for creating dummy files if (isSampleDoc) {
	 * limsFrameProgress.createProgressBar();
	 * setExtractIDFromSamplesSheet(fileSelected, extractIDfileName);
	 * limsFrameProgress.hideFrame(); }
	 * 
	 * logger.info("-------------------------- E N D --------------------------")
	 * ; logger.info("Done with updating the selected document(s). ");
	 * 
	 * if (extractIDfileName != null) {
	 * UitvalList.add("Total records not matched: " +
	 * Integer.toString(UitvalList.size()) + "\n"); }
	 * 
	 * lEndTime = new Date().getTime(); difference = lEndTime - startTime;
	 * String hms = String.format("%02d:%02d:%02d",
	 * TimeUnit.MILLISECONDS.toHours(difference),
	 * TimeUnit.MILLISECONDS.toMinutes(difference) %
	 * TimeUnit.HOURS.toMinutes(1), TimeUnit.MILLISECONDS.toSeconds(difference)
	 * % TimeUnit.MINUTES.toSeconds(1)); logger.info("Import records in : '" +
	 * hms + " hour(s)/minute(s)/second(s).'");
	 * logger.info("Import records in : '" +
	 * TimeUnit.MILLISECONDS.toMinutes(difference) + " minutes.'");
	 * exactVerwerkt = verwerkList.size();
	 * logger.info("Totaal records verwerkt: " + exactVerwerkt);
	 * 
	 * EventQueue.invokeLater(new Runnable() {
	 * 
	 * @Override public void run() { sampleRecordUitval = UitvalList.size() - 1;
	 * sampleExactRecordsVerwerkt = verwerkList.size();
	 * 
	 * Dialogs.showMessageDialog(Integer .toString(sampleTotaalRecords) +
	 * " sample records have been read of which: " + "\n" + "[1] " + Integer
	 * .toString(sampleExactRecordsVerwerkt) +
	 * " samples are imported and linked to " + Integer
	 * .toString(sampleRecordVerwerkt) + " existing documents (of " +
	 * importCounter + " selected) \n" + "[2] " + Integer
	 * .toString(dummyRecordsVerwerkt) + " sample are imported as dummy" + "\n"
	 * + "\n" + "List of " + Integer.toString(importCounter) +
	 * " selected documents: " + "\n" + msgList.toString() + "\n" + "\n" +
	 * "[3] " + Integer.toString(sampleRecordUitval) +
	 * " sample records are ignored.");
	 * 
	 * logger.info("Sample-method: Total imported document(s): " +
	 * msgList.toString());
	 * 
	 * limsLogger.logToFile(logSamplesFileName, UitvalList.toString());
	 * 
	 * msgList.clear(); UitvalList.clear(); verwerkingListCnt.clear();
	 * verwerkList.clear(); sampleExactRecordsVerwerkt = 0; sampleRecordUitval =
	 * 0; sampleRecordVerwerkt = 0; dummyRecordsVerwerkt = 0; recordCount = 0;
	 * exactVerwerkt = 0; limsFrameProgress.hideFrame(); } }); } catch
	 * (IOException e) { e.printStackTrace(); } try { csvReader.close(); } catch
	 * (IOException e) { e.printStackTrace(); } } } else if (n == 1) {
	 * limsFrameProgress.createProgressBar(); fileSelected =
	 * fcd.loadSelectedFile(); setExtractIDFromSamplesSheet(fileSelected,
	 * extractIDfileName); limsFrameProgress.hideFrame(); } else if (n == 2) {
	 * return; } } }
	 */
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
				String[] record = null;
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

						String dummyFile = readGeneiousFieldsValues
								.getFastaIDForSamples_GeneiousDB(ID);

						if (dummyFile.trim() != "") {
							dummyFile = getExtractIDFromAB1FileName(dummyFile);
						}

						if (!dummyFile.equals(ID)) {
							limsFrameProgress
									.showProgress("Creating dummy id: " + ID);

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

}
