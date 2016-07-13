/**
 * 
 */
package nl.naturalis.lims2.ab1.importer;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
public class LimsImportCRS extends DocumentAction {

	private LimsImporterUtil limsImporterUtil = new LimsImporterUtil();
	private LimsFileSelector fcd = new LimsFileSelector();
	private LimsFrameProgress limsFrameProgress = new LimsFrameProgress();
	private LimsCRSFields LimsCRSFields = new LimsCRSFields();
	private LimsReadGeneiousFieldsValues readGeneiousFieldsValues = new LimsReadGeneiousFieldsValues();
	private LimsNotes limsNotes = new LimsNotes();
	private static final Logger logger = LoggerFactory
			.getLogger(LimsImportCRS.class);
	private CSVReader csvReader = null;

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
	private List<String> UitvalList = new ArrayList<String>();
	private List<String> verwerkList = new ArrayList<String>();
	private List<String> MatchList = new ArrayList<String>();
	private List<String> msgList = new ArrayList<String>();
	private List<String> lackCRSList = new ArrayList<String>();
	private List<AnnotatedPluginDocument> listDocuments = new ArrayList<AnnotatedPluginDocument>();

	@Override
	public void actionPerformed(AnnotatedPluginDocument[] documentsSelected) {
		readDataFromCRSFrom_File(documentsSelected);

	}

	@Override
	public GeneiousActionOptions getActionOptions() {
		return new GeneiousActionOptions("6 CRS New", "CRS Import")
				.setToolbarName("6 CRS New").setInPopupMenu(true)
				.setMainMenuLocation(GeneiousActionOptions.MainMenu.Tools, 2.0)
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

	private void readDataFromCRSFrom_File(
			AnnotatedPluginDocument[] annotatedPluginDocuments) {

		long startBeginTime = 0;

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

				UitvalList.clear();
				UitvalList.add("Filename: " + fileSelected + "\n");
				UitvalList.add("Username: " + System.getProperty("user.name")
						+ "\n");
				UitvalList.add("Type action: Import CRS data " + "\n");

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

					/* Opvragen aantal in te lezen records uit de Bold file. */
					if (crsTotaalRecords == 0) {
						try {
							csvReader = new CSVReader(new FileReader(
									fileSelected), '\t', '\'', 1);
							crsTotaalRecords = csvReader.readAll().size();
							csvReader.close();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

					logger.info("Aantal te lezen records: " + crsTotaalRecords);

					lackCRSList.clear();
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
							for (AnnotatedPluginDocument list : listDocuments) {

								isRMNHNumber = false;
								documentFileName = list.getName();

								if ((documentFileName.toString()
										.contains("ab1"))
										|| (list.toString().contains("fas"))
										&& (!list.toString().contains("dum"))) {
									fasDocument = list.getDocumentNotes(true)
											.getNote("importedFrom")
											.getFieldValue("filename");
								}

								/* Add sequence name for the dialog screen */
								if (DocumentUtilities.getSelectedDocuments()
										.listIterator().hasNext()) {
									msgList.add(documentFileName + "\n");
								}

								/* Check of the filename contain "FAS" extension */
								if (fasDocument.toString().contains("fas")
										&& fasDocument != null) {
									documentFileName = list.getName();
								} else {
									/* get AB1 filename */
									if (!list.toString().contains(
											"consensus sequence")
											|| !list.toString().contains(
													"Contig")) {
										documentFileName = list.getName();
									}
								}

								isRMNHNumber = list.toString().contains(
										"RegistrationNumberCode_Samples");

								if (!isRMNHNumber) {
									if (!lackCRSList.toString().contains(
											list.getName())) {
										lackCRSList.add(list.getName());
										logger.info("At least one selected document lacks Registr-nmbr (Sample)."
												+ list.getName());
									}
								} else {
									resultRegNum = (list
											.getDocumentNotes(true)
											.getNote(
													"DocumentNoteUtilities-Registr-nmbr (Samples)")
											.getFieldValue("RegistrationNumberCode_Samples"));
								}

								if (isRMNHNumber) {
									if (resultRegNum.equals(registrationNumber)) {

										startBeginTime = System.nanoTime();

										recordCount++;

										limsFrameProgress
												.showProgress("Match : "
														+ registrationNumber
														+ "\n"
														+ "  Recordcount: "
														+ recordCount);

										logger.info("Registration number matched: "
												+ registrationNumber);

										crsRecordVerwerkt++;

										clearFieldValues();

										for (int i = 0, n = row.length; i < n; i++) {
											LimsCRSFields
													.setRegistratienummer(row[0]);
											extractRankOrClassification(row[1],
													row[2]);
											LimsCRSFields.setGenus(row[3]);
											LimsCRSFields.setTaxon(row[4]);
											LimsCRSFields
													.setDeterminator(row[5]);
											LimsCRSFields.setSex(row[6]);
											LimsCRSFields.setStadium(row[7]);
											LimsCRSFields.setLegavit(row[8]);
											if (row[9].length() > 0) {
												LimsCRSFields
														.setCollectingDate(row[9]);
											} else {
												LimsCRSFields
														.setCollectingDate("10000101L");
											}
											LimsCRSFields.setCountry(row[10]);
											if (i == 11) {
												LimsCRSFields
														.setBioRegion(row[i]);
											}

											if (i == 12) {
												LimsCRSFields
														.setLocality(row[i]);
											}

											if (i == 13) {
												LimsCRSFields
														.setLatitudeDecimal(row[i]);
											}

											if (i == 14) {
												LimsCRSFields
														.setLongitudeDecimal(row[i]);
											}
											if (i == 15) {
												LimsCRSFields.setHeight(row[i]);
											}
										}

										/* Add notes */
										setCRSNotes(annotatedPluginDocuments,
												cnt);
										logger.info("Done with adding notes to the document: "
												+ documentFileName);

										if (!verwerkList.contains(resultRegNum)) {
											verwerkList.add(resultRegNum
													.toString());
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
										endTime = 0;
									} // end IF
								}
								cnt++;
							} // end For Selected
						} // end if registration contain only numbers

						if (!verwerkList.toString()
								.contains(registrationNumber)
								&& registrationNumber.matches(".*\\d+.*")) {
							recordCount++;
							UitvalList
									.add("No document(s) match found for Registrationnumber: "
											+ registrationNumber + "\n");

							limsFrameProgress.showProgress("No match : "
									+ registrationNumber + "\n"
									+ "  Recordcount: " + recordCount);
							continue;
						}
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

						UitvalList.add("Total records not matched: "
								+ Integer.toString(UitvalList.size() - 3)
								+ "\n");
					}
					/** Calculating the Duration of the import **/
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

					/**
					 * Show message with the info from the total of records
					 * which has been import to Geneious
					 **/
					EventQueue.invokeLater(new Runnable() {

						@Override
						public void run() {
							crsRecordUitval = UitvalList.size() - 4;

							Dialogs.showMessageDialog(Integer
									.toString(crsTotaalRecords)
									+ " records have been read of which: "
									+ "\n"
									+ "[1] "
									+ verwerkList.size()
									+ " records are imported and linked to "
									+ Integer.toString(crsRecordVerwerkt)
									+ " existing documents (of "
									+ importCounter
									+ " selected)"
									+ "\n"
									+ "\n"
									+ "List of "
									+ Integer.toString(importCounter)
									+ " selected documents: "
									+ "\n"
									/*
									 * + msgList.toString() + "\n" + "\n"
									 */
									+ "[2] "
									+ Integer.toString(crsRecordUitval)
									+ " records are ignored."
									+ "\n"
									+ "\n"
									+ "[3] "
									+ "At least one or "
									+ Integer.toString(lackCRSList.size())
									+ " selected document lacks Registr-nmbr (Sample).");

							logger.info(verwerkList.size()
									+ " records are imported and linked to "
									+ Integer.toString(crsRecordVerwerkt)
									+ " existing documents (of "
									+ importCounter + " selected)" + "\n"
									+ "\n" + "List of "
									+ Integer.toString(importCounter));
							logger.info(Integer.toString(crsRecordUitval)
									+ " records are ignored.");

							limsLogger.logToFile(logCrsFileName,
									UitvalList.toString());

							msgList.clear();
							UitvalList.clear();
							verwerkList.clear();
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

	private void setCRSNotes(AnnotatedPluginDocument[] documents, int cnt) {

		/** set note for Phylum: FieldValue, Label, NoteType, */
		limsNotes.setNoteToAB1FileName(documents, "PhylumCode_CRS",
				"Phylum (CRS)", "Phylum (CRS)", LimsCRSFields.getPhylum()
						.trim(), cnt);

		/** Set note for Class */
		limsNotes.setNoteToAB1FileName(documents, "ClassCode_CRS",
				"Class (CRS)", "Class (CRS)", LimsCRSFields.getKlasse().trim(),
				cnt);

		/** set note for Order */
		limsNotes.setNoteToAB1FileName(documents, "OrderCode_CRS",
				"Order (CRS)", "Order (CRS)", LimsCRSFields.getOrder().trim(),
				cnt);

		/* set note for Family */
		limsNotes.setNoteToAB1FileName(documents, "FamilyCode_CRS",
				"Family (CRS)", "Family (CRS)", LimsCRSFields.getFamily()
						.trim(), cnt);

		/** set note for SubFamily */
		limsNotes.setNoteToAB1FileName(documents, "SubFamilyCode_CRS",
				"Subfamily (CRS)", "Subfamily (CRS)", LimsCRSFields
						.getSubFamily().trim(), cnt);

		/** set note for Genus */
		limsNotes.setNoteToAB1FileName(documents, "GenusCode_CRS",
				"Genus (CRS)", "Genus (CRS)", LimsCRSFields.getGenus().trim(),
				cnt);

		/** set note for TaxonName */
		limsNotes.setNoteToAB1FileName(documents, "TaxonName1Code_CRS",
				"Scientific name (CRS)", "Scientific name (CRS)", LimsCRSFields
						.getTaxon().trim(), cnt);

		/** set note for Identifier */
		limsNotes.setNoteToAB1FileName(documents, "IdentifierCode_CRS",
				"Identifier (CRS)", "Identifier (CRS)", LimsCRSFields
						.getDeterminator().trim(), cnt);

		/** set note for Sex */
		limsNotes.setNoteToAB1FileName(documents, "SexCode_CRS", "Sex (CRS)",
				"Sex (CRS)", LimsCRSFields.getSex().trim(), cnt);

		/** set note for Phase Or Stage */
		limsNotes.setNoteToAB1FileName(documents, "PhaseOrStageCode_CRS",
				"Stage (CRS)", "Stage (CRS)",
				LimsCRSFields.getStadium().trim(), cnt);

		/** set note for Collector */
		limsNotes.setNoteToAB1FileName(documents, "CollectorCode_CRS",
				"Leg (CRS)", "Leg (CRS)", LimsCRSFields.getLegavit().trim(),
				cnt);

		/** set note for Collecting date */
		if (LimsCRSFields.getCollectingDate().length() > 0) {
			limsNotes.setNoteToAB1FileName(documents, "CollectingDateCode_CRS",
					"Date (CRS)", "Date (CRS)",
					LimsCRSFields.getCollectingDate(), cnt);
		}

		/** set note for Country */
		limsNotes.setNoteToAB1FileName(documents, "CountryCode_CRS",
				"Country (CRS)", "Country (CRS)", LimsCRSFields.getCountry()
						.trim(), cnt);

		/** set note for BioRegion */
		limsNotes.setNoteToAB1FileName(documents,
				"StateOrProvinceBioRegionCode_CRS", "Region (CRS)",
				"Region (CRS)", LimsCRSFields.getBioRegion().trim(), cnt);

		/** set note for Locality */
		limsNotes.setNoteToAB1FileName(documents, "LocalityCode_CRS",
				"Locality (CRS)", "Locality (CRS)", LimsCRSFields.getLocality()
						.trim(), cnt);

		/** set note for Latitude */
		limsNotes.setNoteToAB1FileName(documents, "LatitudeDecimalCode_CRS",
				"Lat (CRS)", "Lat (CRS)", LimsCRSFields.getLatitudeDecimal(),
				cnt);

		/** set note for Longitude */
		limsNotes.setNoteToAB1FileName(documents, "LongitudeDecimalCode_CRS",
				"Long (CRS)", "Long (CRS)",
				LimsCRSFields.getLongitudeDecimal(), cnt);

		/** set note for Height */
		limsNotes.setNoteToAB1FileName(documents, "HeightCode_CRS",
				"Altitude (CRS)", "Altitude (CRS)", LimsCRSFields.getHeight(),
				cnt);

		limsNotes.setImportTrueFalseNotes(documents, "CRSCode_CRS",
				"CRS (CRS)", "CRS (CRS)", true, cnt);
	}

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
