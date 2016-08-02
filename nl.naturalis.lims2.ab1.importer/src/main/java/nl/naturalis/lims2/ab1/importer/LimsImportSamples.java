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

	private List<String> msgList = new ArrayList<String>();
	private List<String> UitvalList = new ArrayList<String>();
	private List<String> verwerkList = new ArrayList<String>();
	private List<String> verwerkingListCnt = new ArrayList<String>();
	private List<AnnotatedPluginDocument> listDocuments = new ArrayList<AnnotatedPluginDocument>();

	private CSVReader csvReader = null;

	private String fileSelected = "";

	private String ID = "";
	private String plateNumber = "";
	private String extractIDfileName = "";
	private String logSamplesFileName = "";
	private String documentFileName = "";
	private String readAssembyContigFileName = "";

	private boolean isExtractIDSeqExists = false;
	private boolean match = false;
	private boolean isSampleDoc = false;

	private int sampleRecordVerwerkt = 0;
	private Object version = 0;
	private int dummyRecordsVerwerkt = 0;
	private long startTime;
	private long lEndTime = 0;
	private long difference = 0;
	private int sampleTotaalRecords = 0;
	private int importCounter = 0;
	private int sampleRecordUitval = 0;
	private int sampleExactRecordsVerwerkt = 0;
	private int recordCount = 0;

	@Override
	public void actionPerformed(
			AnnotatedPluginDocument[] annotatedPluginDocument) {
		readDataFromExcel(annotatedPluginDocument);

	}

	@Override
	public GeneiousActionOptions getActionOptions() {
		return new GeneiousActionOptions("8 Samples new").setInPopupMenu(true)
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
		readGeneiousFieldsValues.activeDB = readGeneiousFieldsValues
				.getServerDatabaseServiceName();

		if (readGeneiousFieldsValues.activeDB != null) {

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

					logSamplesFileName = limsImporterUtil.getLogPath()
							+ "Sample-method-Uitvallijst-"
							+ limsImporterUtil.getLogFilename();

					limsLogger = new LimsLogger(logSamplesFileName);

					logger.info("Start updating selected document(s).");
					fileSelected = fcd.loadSelectedFile();

					/** Add selected documents to a list. */
					if (fileSelected == null) {
						return;
					}

					/* Opvragen aantal in te lezen records uit de Bold file. */
					if (sampleTotaalRecords == 0) {
						CSVReader reader;
						try {
							reader = new CSVReader(
									new FileReader(fileSelected), '\t', '\'', 0);
							sampleTotaalRecords = reader.readAll().size();
							reader.close();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

					/* Create the progressbar */
					limsFrameProgress.createProgressGUI();

					/** Start reading data from the file selected */
					logger.info("-------------------------- S T A R T --------------------------");
					logger.info("Start Reading data from a samples file.");
					logger.info("CSV file: " + fileSelected);

					UitvalList.clear();
					UitvalList.add("Filename: " + fileSelected + "\n");
					UitvalList.add("------------------------------------"
							+ "\n");

					try {
						csvReader = new CSVReader(new FileReader(fileSelected),
								'\t', '\'', 0);
						csvReader.readNext();

						importCounter = DocumentUtilities
								.getSelectedDocuments().size();

						listDocuments = DocumentUtilities
								.getSelectedDocuments();

						String[] record = null;
						while ((record = csvReader.readNext()) != null) {
							if (record.length == 1 && record[0].isEmpty()) {
								continue;
							}

							long startBeginTime = System.nanoTime();

							ID = "e" + record[3];

							Object resultExists = null;
							int cnt = 0;
							for (AnnotatedPluginDocument list : listDocuments) {

								resultExists = null;
								if (list.toString()
										.contains(
												"DocumentNoteUtilities-Extract ID (Seq)")) {
									resultExists = list
											.getDocumentNotes(true)
											.getNote(
													"DocumentNoteUtilities-Extract ID (Seq)")
											.getFieldValue("ExtractIDCode_Seq");
								}

								if (resultExists != null) {
									isExtractIDSeqExists = true;
								} else {
									isExtractIDSeqExists = false;
								}

								/* Read the cache_name from the document */
								if (list.toString().contains("cache_name")) {
									documentFileName = (String) list
											.getFieldValue("cache_name");
								} else {
									/*
									 * Read the override_cache_name from the
									 * document
									 */
									readAssembyContigFileName = (String) list
											.getFieldValue("override_cache_name");
								}

								/*
								 * Compare the cache_name with the name of the
								 * document
								 */
								if (documentFileName.equals(list.getName())) {

									if (list.toString().contains(
											"Reads Assembly Contig")) {
										continue;
									}
									if (isExtractIDSeqExists)
										version = Integer
												.parseInt((String) readGeneiousFieldsValues
														.getVersionValueFromAnnotatedPluginDocument(
																documents,
																"DocumentNoteUtilities-Document version",
																"DocumentVersionCode_Seq",
																cnt));
								}

								/* Check if name is from a Contig file */
								if ((readAssembyContigFileName != null)
										&& readAssembyContigFileName
												.toString()
												.contains(
														"Reads Assembly Contig")) {
									documentFileName = list.getName();
								} /*
								 * Check if name is from a Consensus document
								 */
								else if (list.getName().toString()
										.contains("consensus sequence")) {
									documentFileName = list.getName();
								} /*
								 * Check if name is from a dummy document
								 */
								else if (list.getName().toString()
										.contains("dum")) {
									documentFileName = list.getName();
								} /* from a imported file */
								else if (!list.toString().contains(
										"Reads Assembly Contig")) {
									/* Contig don't have imported filename. */
									documentFileName = (String) list
											.getDocumentNotes(true)
											.getNote("importedFrom")
											.getFieldValue("filename");
								}

								if ((documentFileName.toString()
										.contains("fas"))
										|| (documentFileName.toString()
												.contains("ab1"))
										|| (documentFileName.toString()
												.contains("dum"))) {
									extractIDfileName = getExtractIDFromAB1FileName(list
											.getName());
								} else if (list.getName().toString()
										.contains("consensus sequence")
										|| list.getName().toString()
												.contains("Contig")) {
									extractIDfileName = list.getName();
								}

								isSampleDoc = list.toString().contains(
										"ExtractIDCode_Seq");

								/*
								 * Check if record(PlateNumber) contain "-"
								 */
								if (record[2].length() > 0
										&& record[2].contains("-")) {
									plateNumber = record[2].substring(0,
											record[2].indexOf("-"));
								}

								/*
								 * ID (ID = record[0]) match extractid from the
								 * filename
								 */
								if (ID.equals(extractIDfileName)
										&& isExtractIDSeqExists) {

									startTime = new Date().getTime();

									match = true;
									sampleRecordVerwerkt++;

									msgList.add(extractIDfileName + "\n");

									recordCount++;
									/** Show the progress bar */
									limsFrameProgress
											.showProgress("Filename match : "
													+ extractIDfileName + "\n"
													+ "  Recordcount: "
													+ recordCount);

									setFieldsValues(record[0], record[1],
											plateNumber, ID, record[4],
											record[5], version, record[6]);

									logger.info("Document Filename: "
											+ documentFileName);

									logger.info("Start with adding notes to the document");
									setSamplesNotes(documents, cnt);
									logger.info("Done with adding notes to the document");

									if (!verwerkList.contains(ID)) {
										verwerkList.add(ID);
									}

									long endTime = System.nanoTime();
									long elapsedTime = endTime - startBeginTime;
									logger.info("Took: "
											+ (TimeUnit.SECONDS.convert(
													elapsedTime,
													TimeUnit.NANOSECONDS))
											+ " second(s)");
									elapsedTime = 0;
									match = false;

								} // end IF
								cnt++;
							} // end For

							if (!verwerkList.toString().contains(ID) && !match) {

								clearVariables();
								recordCount++;

								if (!UitvalList.toString().contains(ID)) {
									UitvalList
											.add("No document(s) match found for Registrationnumber: "
													+ ID + "\n");

									limsFrameProgress
											.showProgress("No match : " + ID
													+ "\n" + "  Recordcount: "
													+ recordCount);
								}
							}

						} // end While
						logger.info("--------------------------------------------------------");
						logger.info("Total of document(s) updated: "
								+ listDocuments.size());

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
										// + msgList.toString()
										// + "\n"
										+ "\n"
										+ "[3] "
										+ Integer.toString(sampleRecordUitval)
										+ " sample records are ignored."
										+ "\n"
										+ "\n"
										+ "[4] "
										+ dummyRecordsVerwerkt
										+ " (zero). dummy samples are ignored.");

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
				limsFrameProgress.createProgressGUI();
				fileSelected = fcd.loadSelectedFile();
				setExtractIDMatchSamplesSheetRecords(fileSelected, documents);
				limsFrameProgress.hideFrame();
			} else if (n == 2) {
				return;
			}
		}
	}

	private void clearVariables() {
		limsExcelFields.setProjectPlaatNummer("");
		limsExcelFields.setPlaatPositie("");
		limsExcelFields.setExtractPlaatNummer("");
		limsExcelFields.setExtractID("");
		limsExcelFields.setRegistrationNumber("");
		limsExcelFields.setTaxonNaam("");
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
									.showProgress("Creating dummy file: " + ID);

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
							limsFrameProgress
									.showProgress("Dummy file already exists in the DB: "
											+ ID);
						}
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

	private void setFieldsValues(String projectPlaatNr, String plaatPositie,
			String extractPlaatNr, String extractID, String registrationNumber,
			String taxonNaam, Object versieNummer, String sampleMethod) {

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
		limsExcelFields.setSubSample(sampleMethod); // record[6]

		String regScientificname = "";
		if (registrationNumber.length() > 0 && taxonNaam.length() > 0) {
			regScientificname = registrationNumber + " " + taxonNaam;
		} else if (registrationNumber.length() > 0) {
			regScientificname = registrationNumber;
		} else if (registrationNumber.length() == 0 && taxonNaam.length() > 0) {
			regScientificname = taxonNaam;
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

	private void setExtractIDMatchSamplesSheetRecords(String fileName,
			AnnotatedPluginDocument[] docsSamples) {

		List<String> UitvalList = new ArrayList<String>();
		List<String> exactVerwerkList = new ArrayList<String>();
		List<String> lackList = new ArrayList<String>();
		String[] record = null;
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
							listDocuments = DocumentUtilities
									.getSelectedDocuments();
						}

						int cnt = 0;
						for (AnnotatedPluginDocument list : listDocuments) {

							if (list.toString().contains(
									"Reads Assembly Contig")) {
								continue;
							}

							if ((list.toString().contains("fas"))
									|| (list.toString().contains("ab1"))
									|| (list.toString().contains("dum"))) {
								extractIDfileName = getExtractIDFromAB1FileName(list
										.getName());
							}

							isExtractIDSeqExists = list.toString().contains(
									"MarkerCode_Seq");

							if (!isExtractIDSeqExists) {
								if (!lackList.contains(DocumentUtilities
										.getSelectedDocuments().get(cnt)
										.getName())) {
									limsFrameProgress
											.showProgress("At least one selected document lacks Extract ID (Seq)."
													+ "\n" + list.getName());
									logger.info("At least one selected document lacks Extract ID (Seq)."
											+ list.getName());
									lackList.add(list.getName());
								}

							} else if (isExtractIDSeqExists) {
								version = Integer
										.parseInt((String) readGeneiousFieldsValues
												.getVersionValueFromAnnotatedPluginDocument(
														docsSamples,
														"DocumentNoteUtilities-Document version",
														"DocumentVersionCode_Seq",
														cnt));
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
										version, record[6]);
								logger.info("Start with adding notes to the document");
								setSamplesNotes(docsSamples, cnt);
								logger.info("Done with adding notes to the document");
							}
							cnt++;
						} // For
						if (!exactVerwerkList.contains(ID) && !isMatched) {
							if (!UitvalList.contains(ID)
									&& !limsImporterUtil.isAlpha(ID)) {
								UitvalList
										.add("No document(s) match found for Registrationnumber: "
												+ ID + "\n");
								limsFrameProgress
										.showProgress("No document match: "
												+ ID);
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
								+ Integer.toString(listDocuments.size())
								+ " existing documents (of "
								+ listDocuments.size()
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

}
