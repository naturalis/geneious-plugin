/**
 * <h1>Lims CRS Plugin</h1> 
 */
package nl.naturalis.lims2.ab1.importer;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import nl.naturalis.lims2.utils.LimsDatabaseChecker;
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
 * Select one or more AB1/Fasta document(s) in a Geneious folder. <br>
 * Press button "3 CRS".<br>
 * A dialog screen is displayed. <br>
 * Browse to the CSV files<br>
 * Select on of the AB1 or Fasta Csv file. The import process is started. <br>
 * If there is a match between a registration number from the csv file with the
 * Samples registration number in the one of the selected document(s),<br>
 * the notes will be added to the selected document(s) in Geneious.<br>
 * A processing log(matching a registration number) and failure log(not matching
 * a registration number) is created.</td>
 * </tr>
 * </table>
 * 
 * @author Reinier.Kartowikromo
 * @version: 1.0
 * 
 */
public class LimsImportCRS extends DocumentAction {

	private LimsImporterUtil limsImporterUtil = new LimsImporterUtil();
	private LimsFileSelector fcd = new LimsFileSelector();
	private LimsFrameProgress limsFrameProgress = new LimsFrameProgress();
	private LimsCRSFields LimsCRSFields = new LimsCRSFields();
	private LimsReadGeneiousFieldsValues readGeneiousFieldsValues = new LimsReadGeneiousFieldsValues();
	private LimsNotes limsNotes = new LimsNotes();
	private static final Logger logger = LoggerFactory
			.getLogger(LimsImportCRS.class);

	private boolean isRMNHNumber = false;
	private String fileSelected = null;
	private Object documentFileName = "";
	private String registrationNumber = "";
	private int crsRecordVerwerkt = 0;
	private int crsRecordUitval = 0;
	private int crsTotaalRecords = 0;
	private Object resultRegNum = null;
	private long startTime;
	private long lEndTime = 0;
	private long difference = 0;
	public int importCounter;
	private String logCrsFileName = "";
	private Object fasDocument = "";
	private LimsLogger limsLogger = null;
	private List<String> failureList = new ArrayList<String>();
	private List<String> processedList = new ArrayList<String>();
	private List<String> MatchList = new ArrayList<String>();
	private List<String> lackList = new ArrayList<String>();
	private List<AnnotatedPluginDocument> listDocuments = new ArrayList<AnnotatedPluginDocument>();

	/**
	 * Read the CRS CSV data from the file to start adding notes to the selected
	 * document(s)
	 * 
	 * @param documentsSelected
	 *            Set param documentsSelected
	 * */
	@Override
	public void actionPerformed(AnnotatedPluginDocument[] documentsSelected) {
		readCRSDataFromCSVFile(documentsSelected);

	}

	/**
	 * Add the button for CRS to the menu
	 * 
	 * @return Add the button to the menubar
	 * @see LimsImportCRS
	 * */
	@Override
	public GeneiousActionOptions getActionOptions() {
		return new GeneiousActionOptions("3 CRS", "CRS Import")
				.setToolbarName("3 CRS").setInPopupMenu(true)
				.setMainMenuLocation(GeneiousActionOptions.MainMenu.Tools, 2.0)
				.setInMainToolbar(true).setInPopupMenu(true)
				.setAvailableToWorkflows(true);

	}

	/**
	 * Help file will be implemented later on
	 * 
	 * @return No Help files
	 * @see LimsImportCRS
	 * */
	@Override
	public String getHelp() {
		return null;
	}

	/**
	 * Add the max value of selected document(s)<br>
	 * public static final int MAX_VALUE = 2147483647;
	 * 
	 * @return Return the count value of imported documents
	 * @see LimsImportCRS
	 * */
	@Override
	public DocumentSelectionSignature[] getSelectionSignatures() {
		return new DocumentSelectionSignature[] { new DocumentSelectionSignature(
				PluginDocument.class, 0, Integer.MAX_VALUE) };
	}

	/*
	 * Select AB1 or Fasta documents to add notes to the documents
	 * 
	 * @param annotatedPluginDocuments
	 */
	private void readCRSDataFromCSVFile(
			AnnotatedPluginDocument[] annotatedPluginDocuments) {

		long startBeginTime = 0;

		LimsDatabaseChecker dbchk = new LimsDatabaseChecker();
		if (!dbchk.checkDBName()) {
			return;
		}
		/* Get the active database name */
		readGeneiousFieldsValues.activeDB = readGeneiousFieldsValues
				.getServerDatabaseServiceName();

		/* if database exists then continue the process else abort. */
		if (readGeneiousFieldsValues.activeDB != null) {

			/* if no documents in Geneious has been selected show a message. */
			if (DocumentUtilities.getSelectedDocuments().isEmpty()) {
				EventQueue.invokeLater(new Runnable() {

					@Override
					public void run() {
						Dialogs.showMessageDialog("Select at least one document");
						return;
					}
				});
			} else /*
					 * if documents has been selected continue the process to
					 * import data
					 */
			if (!DocumentUtilities.getSelectedDocuments().isEmpty()) {

				/* get the path from the propertie file: lims-import.properties */
				logCrsFileName = limsImporterUtil.getLogPath()
						+ "CRS-Uitvallijst-"
						+ limsImporterUtil.getLogFilename();

				/* Create logfile */
				limsLogger = new LimsLogger(logCrsFileName);

				/*
				 * Dialoogscherm voor het selecteren van een Bold file om in
				 * kunnen te lezen.
				 */
				fileSelected = fcd.loadSelectedFile();
				if (fileSelected == null) {
					return;
				}

				/* Create Dialog windows for processing the file */
				limsFrameProgress.createProgressGUI();
				logger.info("Start updating selected document(s) with CRS data.");
				logger.info("-------------------------- S T A R T --------------------------");
				logger.info("Start Reading data from a CRS file.");

				failureList.clear();
				failureList.add("Filename: " + fileSelected + "\n");
				failureList.add("Username: " + System.getProperty("user.name")
						+ "\n");
				failureList.add("Type action: Import CRS data " + "\n");

				/*
				 * Begintijd opstarten tijdens het proces van verwerken van de
				 * import.
				 */
				startTime = new Date().getTime();

				String line = "";
				String cvsSplitBy = "\t";
				int recordCount = 0;
				try {
					/* read the file in memory */
					InputStream in = new FileInputStream(new File(fileSelected));
					BufferedReader bufReader = new BufferedReader(
							new InputStreamReader(in));

					importCounter = DocumentUtilities.getSelectedDocuments()
							.size();

					/* add the selected document into the list. */
					listDocuments = DocumentUtilities.getSelectedDocuments();

					/* Opvragen aantal in te lezen records uit de CRS file. */
					crsTotaalRecords = limsImporterUtil
							.countCsvRecords(fileSelected);

					logger.info("Aantal te lezen records: " + crsTotaalRecords);

					/* Start processing the CRS CVS records */
					while ((line = bufReader.readLine()) != null) {
						if (line.length() == 1 && line.isEmpty()) {
							continue;
						}

						/* Splitting the data */
						String[] row = line.split(cvsSplitBy);

						/* get the registration number. */
						registrationNumber = row[0];

						/* if registration string contains number */
						if (registrationNumber.matches(".*\\d+.*")
								&& registrationNumber.length() > 0) {

							int cnt = 0;
							/* Looping thru the selected documents to add Notes */
							for (AnnotatedPluginDocument list : listDocuments) {

								/* Get the filename */
								documentFileName = list.getName();

								/*
								 * Check if file has the notes "ImportedFrom",
								 * dummies file do not have that
								 */
								getFastDocumentName(list);

								/* Check of the filename contain "FAS" extension */
								setDocumentFileName(list);

								/*
								 * check if document contain a registration
								 * number
								 */
								isRMNHNumber = list.toString().contains(
										"RegistrationNumberCode_Samples");

								if (isRMNHNumber) {
									/*
									 * Get registration number from the document
									 */
									resultRegNum = (list
											.getDocumentNotes(true)
											.getNote(
													"DocumentNoteUtilities-Registr-nmbr (Samples)")
											.getFieldValue("RegistrationNumberCode_Samples"));
								} else {
									if (!lackList.toString().contains(
											list.getName())) {
										lackList.add(list.getName());
										logger.info("At least one selected document lacks Registr-nmbr (Sample)."
												+ list.getName());
									}
								}

								if ((resultRegNum == null)
										|| (!resultRegNum
												.equals(registrationNumber))) {
									cnt++;
									continue;
								}

								/*
								 * If the Registration number from the CSV
								 * record match with the registration number
								 * from the selected document then start
								 * processing
								 */
								if (resultRegNum.equals(registrationNumber)
										&& isRMNHNumber
										&& !documentFileName.toString()
												.contains(".dum")) {

									/* Start time of the process */
									startBeginTime = System.nanoTime();

									recordCount++;
									/* Show progressbar GUI */
									limsFrameProgress.showProgress("Match : "
											+ registrationNumber + "\n"
											+ "  Recordcount: " + recordCount);

									logger.info("Registration number matched: "
											+ registrationNumber);

									crsRecordVerwerkt++;

									/* Clear fields variables */
									clearFieldValues();

									/*
									 * TODO: Anders aanpakken middels CSVReader
									 * functionaliteit. Misschien wordt de
									 * preformance wat beter.
									 */
									/*
									 * Looping thru a row set from the CSV file
									 */
									for (int i = 0, n = row.length; i < n; i++) {
										/* Set Registration number */
										LimsCRSFields
												.setRegistratienummer(row[0]);
										/*
										 * Set Rank or classification and Name
										 */
										extractRankOrClassification(row[1],
												row[2]);
										/* Set Genus or monomial */
										LimsCRSFields.setGenus(row[3]);
										/* Full scientific name */
										LimsCRSFields.setTaxon(row[4]);
										/* Identifier */
										LimsCRSFields.setDeterminator(row[5]);
										/* Sex */
										LimsCRSFields.setSex(row[6]);
										/* Phase or stage */
										LimsCRSFields.setStadium(row[7]);
										/* Agent */
										LimsCRSFields.setLegavit(row[8]);
										/* Collecting start date */
										if (row[9].length() > 0) {
											LimsCRSFields
													.setCollectingDate(row[9]);
										} else {
											LimsCRSFields
													.setCollectingDate("10000101L");
										}
										/* Country */
										LimsCRSFields.setCountry(row[10]);
										/* State/province */
										if (i == 11) {
											LimsCRSFields.setBioRegion(row[i]);
										}
										/* Locality */
										if (i == 12) {
											LimsCRSFields.setLocality(row[i]);
										}
										/* Latitude */
										if (i == 13) {
											LimsCRSFields
													.setLatitudeDecimal(row[i]);
										}
										/* Longitude */
										if (i == 14) {
											LimsCRSFields
													.setLongitudeDecimal(row[i]);
										}
										/* Altitude */
										if (i == 15) {
											LimsCRSFields.setHeight(row[i]);
										}
									}

									/* Add notes to the selected document */
									enrichNotesToCRS(annotatedPluginDocuments,
											cnt);

									logger.info("Done with adding notes to the document: "
											+ documentFileName);

									/*
									 * Add registrationnumber to a processlist
									 */
									if (!processedList.contains(resultRegNum)) {
										processedList.add(resultRegNum
												.toString());
									}
									/*
									 * End duration of the processing of notes
									 */
									limsImporterUtil
											.calculateTimeForAddingNotes(startBeginTime);
								} // end IF

								cnt++;
							} // end For Selected
						} // end if registration contain only numbers

						/* Add data(total records) to the failure list */
						recordCount = addDocumentsToFailureList(recordCount);
					} // end While
					bufReader.close();
					in.close();

					logger.info("--------------------------------------------------------");
					logger.info("Total of document(s) updated: "
							+ importCounter);
					logger.info("-------------------------- E N D --------------------------");
					logger.info("Done with updating the selected document(s). ");

					if (documentFileName != null) {
						MatchList.add("No document(s) match found for : "
								+ documentFileName);

						failureList.add("Total records not matched: "
								+ Integer.toString(failureList.size() - 3)
								+ "\n");
					}
					/** Calculating the Duration of the import **/
					setDurationEndTimeProcessing();

					/**
					 * Show message with the info from the total of records
					 * which has been import to Geneious
					 **/
					EventQueue.invokeLater(new Runnable() {

						@Override
						public void run() {
							crsRecordUitval = failureList.size() - 4;

							/*
							 * Show a message at the end of processing the
							 * document(s)
							 */
							showMessageDialogEndOfProcessing();

							logger.info(processedList.size()
									+ " records are imported and linked to "
									+ Integer.toString(crsRecordVerwerkt)
									+ " existing documents (of "
									+ importCounter + " selected)" + "\n"
									+ "\n" + "List of "
									+ Integer.toString(importCounter));
							logger.info(Integer.toString(crsRecordUitval)
									+ " records are ignored.");

							limsLogger.logToFile(logCrsFileName,
									failureList.toString());

							failureList.clear();
							processedList.clear();
							lackList.clear();
							limsFrameProgress.hideFrame();
							crsRecordUitval = 0;
							crsRecordVerwerkt = 0;
						}

					});
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
		}
	}

	/*
	 * Show dialog message at the end of processing the document(s)
	 */
	private void showMessageDialogEndOfProcessing() {
		crsTotaalRecords = processedList.size() + crsRecordUitval;
		Dialogs.showMessageDialog(Integer.toString(crsTotaalRecords)
				+ " records have been read of which: " + "\n" + "[1] "
				+ processedList.size() + " records are imported and linked to "
				+ Integer.toString(crsRecordVerwerkt)
				+ " existing documents (of " + importCounter + " selected)"
				+ "\n" + "\n" + "[2] " + Integer.toString(crsRecordUitval)
				+ " records are ignored." + "\n" + "\n"
				+ getLackMessage(isLackListNotEmpty()));
	}

	/*
	 * Check of there are document(s) without registration number (Samples)
	 * 
	 * @return
	 */
	private boolean isLackListNotEmpty() {
		if (lackList.size() > 0)
			return true;
		return false;
	}

	/*
	 * Get document filename
	 * 
	 * @param list
	 */
	private void setDocumentFileName(AnnotatedPluginDocument list) {
		if (fasDocument.toString().contains("fas") && fasDocument != null) {
			documentFileName = list.getName();
		} else {
			/* get AB1 filename */
			if (!list.toString().contains("consensus sequence")
					|| !list.toString().contains("Contig")) {
				documentFileName = list.getName();
			}
		}
	}

	/*
	 * Check if document contain notes "importedFrom"
	 * 
	 * @param list
	 */
	private void getFastDocumentName(AnnotatedPluginDocument list) {
		if ((documentFileName.toString().contains("ab1"))
				|| (list.toString().contains("fas"))
				&& (!list.toString().contains("dum"))) {
			fasDocument = list.getDocumentNotes(true).getNote("importedFrom")
					.getFieldValue("filename");
		}
	}

	/*
	 * Calculate the end time of the process adding notes.
	 * 
	 * @param startBeginTime
	 */
	/*
	 * private void calculateTimeForAddingNotes(long startBeginTime) { long
	 * endTime = System.nanoTime(); long elapsedTime = endTime - startBeginTime;
	 * logger.info("Took: " + (TimeUnit.SECONDS.convert(elapsedTime,
	 * TimeUnit.NANOSECONDS)) + " second(s)"); elapsedTime = 0; endTime = 0; }
	 */

	/*
	 * Add document(s) that not matched to the failure list.
	 * 
	 * @param recordCount
	 * 
	 * @return
	 */
	private int addDocumentsToFailureList(int recordCount) {
		if (!processedList.toString().contains(registrationNumber)
				&& registrationNumber.matches(".*\\d+.*")) {
			recordCount++;
			failureList
					.add("No document(s) match found for Registrationnumber: "
							+ registrationNumber + "\n");
			/* Show failure records on the dialog screen */
			limsFrameProgress.showProgress("No match : " + registrationNumber
					+ "\n" + "  Recordcount: " + recordCount);
			return recordCount;
		}
		return recordCount;
	}

	/*
	 * Calculating the Duration of the import (end time)
	 */
	private void setDurationEndTimeProcessing() {
		lEndTime = new Date().getTime();
		difference = lEndTime - startTime;
		String hms = String.format(
				"%02d:%02d:%02d",
				TimeUnit.MILLISECONDS.toHours(difference),
				TimeUnit.MILLISECONDS.toMinutes(difference)
						% TimeUnit.HOURS.toMinutes(1),
				TimeUnit.MILLISECONDS.toSeconds(difference)
						% TimeUnit.MINUTES.toSeconds(1));
		logger.info("Import records in : '" + hms
				+ " hour(s)/minute(s)/second(s).'");
		logger.info("Import records in : '"
				+ TimeUnit.MILLISECONDS.toMinutes(difference) + " minutes.'");
	}

	/*
	 * Set notes to the documents
	 * 
	 * @param documents , cnt
	 */

	private void enrichNotesToCRS(AnnotatedPluginDocument[] documents, int cnt) {

		/* set note for Phylum: FieldValue, Label, NoteType, */
		limsNotes.setNoteToAB1FileName(documents, "PhylumCode_CRS",
				"Phylum (CRS)", "Phylum (CRS)", LimsCRSFields.getPhylum()
						.trim(), cnt);

		/* Set note for Class */
		limsNotes.setNoteToAB1FileName(documents, "ClassCode_CRS",
				"Class (CRS)", "Class (CRS)", LimsCRSFields.getKlasse().trim(),
				cnt);

		/* set note for Order */
		limsNotes.setNoteToAB1FileName(documents, "OrderCode_CRS",
				"Order (CRS)", "Order (CRS)", LimsCRSFields.getOrder().trim(),
				cnt);

		/* set note for Family */
		limsNotes.setNoteToAB1FileName(documents, "FamilyCode_CRS",
				"Family (CRS)", "Family (CRS)", LimsCRSFields.getFamily()
						.trim(), cnt);

		/* set note for SubFamily */
		limsNotes.setNoteToAB1FileName(documents, "SubFamilyCode_CRS",
				"Subfamily (CRS)", "Subfamily (CRS)", LimsCRSFields
						.getSubFamily().trim(), cnt);

		/* set note for Genus */
		limsNotes.setNoteToAB1FileName(documents, "GenusCode_CRS",
				"Genus (CRS)", "Genus (CRS)", LimsCRSFields.getGenus().trim(),
				cnt);

		/* set note for TaxonName */
		limsNotes.setNoteToAB1FileName(documents, "TaxonName1Code_CRS",
				"Scientific name (CRS)", "Scientific name (CRS)", LimsCRSFields
						.getTaxon().trim(), cnt);

		/* set note for Identifier */
		limsNotes.setNoteToAB1FileName(documents, "IdentifierCode_CRS",
				"Identifier (CRS)", "Identifier (CRS)", LimsCRSFields
						.getDeterminator().trim(), cnt);

		/* set note for Sex */
		limsNotes.setNoteToAB1FileName(documents, "SexCode_CRS", "Sex (CRS)",
				"Sex (CRS)", LimsCRSFields.getSex().trim(), cnt);

		/* set note for Phase Or Stage */
		limsNotes.setNoteToAB1FileName(documents, "PhaseOrStageCode_CRS",
				"Stage (CRS)", "Stage (CRS)",
				LimsCRSFields.getStadium().trim(), cnt);

		/* set note for Collector */
		limsNotes.setNoteToAB1FileName(documents, "CollectorCode_CRS",
				"Leg (CRS)", "Leg (CRS)", LimsCRSFields.getLegavit().trim(),
				cnt);

		/* set note for Collecting date */
		if (LimsCRSFields.getCollectingDate().length() > 0) {
			limsNotes.setNoteToAB1FileName(documents, "CollectingDateCode_CRS",
					"Date (CRS)", "Date (CRS)",
					LimsCRSFields.getCollectingDate(), cnt);
		}

		/* set note for Country */
		limsNotes.setNoteToAB1FileName(documents, "CountryCode_CRS",
				"Country (CRS)", "Country (CRS)", LimsCRSFields.getCountry()
						.trim(), cnt);

		/* set note for BioRegion */
		limsNotes.setNoteToAB1FileName(documents,
				"StateOrProvinceBioRegionCode_CRS", "Region (CRS)",
				"Region (CRS)", LimsCRSFields.getBioRegion().trim(), cnt);

		/* set note for Locality */
		limsNotes.setNoteToAB1FileName(documents, "LocalityCode_CRS",
				"Locality (CRS)", "Locality (CRS)", LimsCRSFields.getLocality()
						.trim(), cnt);

		/* set note for Latitude */
		limsNotes.setNoteToAB1FileName(documents, "LatitudeDecimalCode_CRS",
				"Lat (CRS)", "Lat (CRS)", LimsCRSFields.getLatitudeDecimal(),
				cnt);

		/* set note for Longitude */
		limsNotes.setNoteToAB1FileName(documents, "LongitudeDecimalCode_CRS",
				"Long (CRS)", "Long (CRS)",
				LimsCRSFields.getLongitudeDecimal(), cnt);

		/* set note for Height */
		limsNotes.setNoteToAB1FileName(documents, "HeightCode_CRS",
				"Altitude (CRS)", "Altitude (CRS)", LimsCRSFields.getHeight(),
				cnt);

		/* Set true/false note value to the document(s) */
		limsNotes.setImportTrueFalseNotes(documents, "CRSCode_CRS",
				"CRS (CRS)", "CRS (CRS)", true, cnt);
	}

	/*
	 * Split the Rank or classification values. Example: order / family /
	 * subfamily / tribe Coleoptera / Leiodidae / Cholevinae / Cholevini Catops
	 * 
	 * @param rankOrClassificationValue , nameValue
	 */
	private void extractRankOrClassification(String rankOrClassificationValue,
			String nameValue) {

		String[] forwardSlash = StringUtils.split(rankOrClassificationValue,
				"/");

		for (int i = 0; i < forwardSlash.length; i++) {

			String[] name = StringUtils.split(nameValue, "/");

			if (forwardSlash[i].trim().equals("phylum")) {
				LimsCRSFields.setPhylum(name[i]);
			} else if (forwardSlash[i].trim().equals("subclass")) {
				LimsCRSFields.setSubclass(name[i]);
			} else if (forwardSlash[i].trim().equals("class")) {
				LimsCRSFields.setKlasse(name[i]);
			} else if (forwardSlash[i].trim().equals("suborder")) {
				LimsCRSFields.setSuborder(name[i]);
			} else if (forwardSlash[i].trim().equals("order")) {
				LimsCRSFields.setOrder(name[i]);
			} else if (forwardSlash[i].trim().equals("family")) {
				LimsCRSFields.setFamily(name[i]);
			} else if (forwardSlash[i].trim().equals("superfamily")) {
				LimsCRSFields.setSuperFamily(name[i]);
			} else if (forwardSlash[i].trim().equals("subfamily")) {
				LimsCRSFields.setSubFamily(name[i]);
			} else if (forwardSlash[i].trim().equals("tribe")) {
				LimsCRSFields.setTribe(name[i]);
			}
		}
	}

	/*
	 * Show lacks message of documents that not has been processed
	 * 
	 * @param missing
	 * 
	 * @return
	 */
	private String getLackMessage(Boolean missing) {
		if (missing)
			return "[3] At least one selected document lacks registr-nmbr (Samples)";
		return "";
	}

	/* Clear fields variables */
	private void clearFieldValues() {
		LimsCRSFields.setRegistratienummer("");
		LimsCRSFields.setPhylum("");
		LimsCRSFields.setKlasse("");
		LimsCRSFields.setOrder("");
		LimsCRSFields.setFamily("");
		LimsCRSFields.setSubFamily("");
		LimsCRSFields.setGenus("");
		LimsCRSFields.setTaxon("");
		LimsCRSFields.setDeterminator("");
		LimsCRSFields.setSex("");
		LimsCRSFields.setStadium("");
		LimsCRSFields.setLegavit("");
		LimsCRSFields.setCollectingDate("");
		LimsCRSFields.setCountry("");
		LimsCRSFields.setBioRegion("");
		LimsCRSFields.setLocality("");
		LimsCRSFields.setLatitudeDecimal("");
		LimsCRSFields.setLongitudeDecimal("");
		LimsCRSFields.setHeight("");
	}

}
