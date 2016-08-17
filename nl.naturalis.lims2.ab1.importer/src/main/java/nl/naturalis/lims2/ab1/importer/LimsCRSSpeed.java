package nl.naturalis.lims2.ab1.importer;

import java.awt.EventQueue;
import java.io.FileReader;
import java.io.IOException;
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

public class LimsCRSSpeed extends DocumentAction {
	private LimsImporterUtil limsImporterUtil = new LimsImporterUtil();
	private LimsFileSelector fcd = new LimsFileSelector();
	private LimsFrameProgress limsFrameProgress = new LimsFrameProgress();
	private LimsCRSFields limsCRSFields = new LimsCRSFields();
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
	private long startBeginTime = 0;
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
	private List<AnnotatedPluginDocument> listDocuments = new ArrayList<AnnotatedPluginDocument>();

	@Override
	public void actionPerformed(AnnotatedPluginDocument[] documentsSelected) {
		readDataFromCRSFrom_File(documentsSelected);

	}

	@Override
	public GeneiousActionOptions getActionOptions() {
		return new GeneiousActionOptions("6 CRS Speed", "CRS Import")
				.setToolbarName("6 CRS Speed").setInPopupMenu(true)
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

	/** Select AB1 or Fasta documents to add notes to the documents */
	private void readDataFromCRSFrom_File(
			AnnotatedPluginDocument[] annotatedPluginDocuments) {

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

				int recordCount = 0;
				importCounter = DocumentUtilities.getSelectedDocuments().size();
				/* add the selected document into the list. */
				listDocuments = DocumentUtilities.getSelectedDocuments();

				/* Opvragen aantal in te lezen records uit de CRS file. */
				crsTotaalRecords = limsImporterUtil
						.countRecordsCSV(fileSelected);

				logger.info("Aantal te lezen records: " + crsTotaalRecords);

				try {

					csvReader = new CSVReader(new FileReader(fileSelected),
							'\t', '\'', 1);
					String[] rowLine;
					while ((rowLine = csvReader.readNext()) != null) {

						registrationNumber = rowLine[0];

						/* if registration string contains number */
						if (registrationNumber.matches(".*\\d+.*")
								&& registrationNumber.length() > 0) {
							recordCount = readCsvFile(annotatedPluginDocuments,
									recordCount, rowLine);

							/* Add data(total records) to the failure list */
							recordCount = addDocumentsToFailureList(recordCount);
						} // end if
					} // end while
				} catch (IOException e) {
					e.printStackTrace();
				}
				logger.info("--------------------------------------------------------");
				logger.info("Total of document(s) updated: " + importCounter);
				logger.info("-------------------------- E N D --------------------------");
				logger.info("Done with updating the selected document(s). ");

				if (documentFileName != null) {
					MatchList.add("No document(s) match found for : "
							+ documentFileName);

					failureList.add("Total records not matched: "
							+ Integer.toString(failureList.size() - 3) + "\n");
				}
				/** Calculating the Duration of the import **/
				setDurationEndTimeProcessing();

				/**
				 * Show message with the info from the total of records which
				 * has been import to Geneious
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
								+ " existing documents (of " + importCounter
								+ " selected)" + "\n" + "\n" + "List of "
								+ Integer.toString(importCounter));
						logger.info(Integer.toString(crsRecordUitval)
								+ " records are ignored.");

						limsLogger.logToFile(logCrsFileName,
								failureList.toString());

						failureList.clear();
						processedList.clear();
						limsFrameProgress.hideFrame();
						crsRecordUitval = 0;
						crsRecordVerwerkt = 0;
					}

					/**
					 * Show dialog message at the end of processing the
					 * document(s)
					 */
					private void showMessageDialogEndOfProcessing() {
						Dialogs.showMessageDialog(Integer
								.toString(crsTotaalRecords)
								+ " records have been read of which: "
								+ "\n"
								+ "[1] "
								+ processedList.size()
								+ " records are imported and linked to "
								+ Integer.toString(crsRecordVerwerkt)
								+ " existing documents (of "
								+ importCounter
								+ " selected)"
								+ "\n"
								+ "\n"
								+ "[2] "
								+ Integer.toString(crsRecordUitval)
								+ " records are ignored."
								+ "\n"
								+ "\n"
								+ getLackMessage(isRMNHNumber));
					}
				});
			}
		}
	}

	/**
	 * @param annotatedPluginDocuments
	 * @param recordCount
	 * @param rowLine
	 * @param cnt
	 * @return
	 */
	private int readCsvFile(AnnotatedPluginDocument[] annotatedPluginDocuments,
			int recordCount, String[] rowLine) {
		int cnt = 0;
		for (AnnotatedPluginDocument list : listDocuments) {

			isRMNHNumber = false;
			/* Get the filename */
			documentFileName = list.getName();

			/*
			 * Check if file has the notes "ImportedFrom", dummies file do not
			 * have that
			 */
			getFastDocumentName(list);

			/*
			 * Check of the filename contain "FAS" extension
			 */
			setDocumentFileName(list);

			/*
			 * check if document contain a registration number
			 */
			isRMNHNumber = list.toString().contains(
					"RegistrationNumberCode_Samples");

			if (isRMNHNumber) {
				/*
				 * Get registration number from the document
				 */
				resultRegNum = (list.getDocumentNotes(true).getNote(
						"DocumentNoteUtilities-Registr-nmbr (Samples)")
						.getFieldValue("RegistrationNumberCode_Samples"));
			}

			if (!resultRegNum.equals(registrationNumber)) {
				cnt++;
				continue;
			}

			if (isRMNHNumber) {
				/*
				 * If the Registration number from the CSV record match with the
				 * registration number from the selected document then start
				 * processing
				 */
				if (resultRegNum.equals(registrationNumber)) {

					/* Start time of the process */
					startBeginTime = System.nanoTime();

					recordCount++;
					/* Show progressbar GUI */
					limsFrameProgress.showProgress("Match : "
							+ registrationNumber + "\n" + "  Recordcount: "
							+ recordCount);

					logger.info("Registration number matched: "
							+ registrationNumber);

					crsRecordVerwerkt++;

					/* Clear fields variables */
					// clearFieldValues();

					/* Set Registration number */
					limsCRSFields.setRegistratienummer(rowLine[0]);
					/*
					 * Set Rank or classification and Name
					 */
					extractRankOrClassification(rowLine[1], rowLine[2]);
					/* Set Genus or monomial */
					limsCRSFields.setGenus(rowLine[3]);
					/* Full scientific name */
					limsCRSFields.setTaxon(rowLine[4]);
					/* Identifier */
					limsCRSFields.setDeterminator(rowLine[5]);
					/* Sex */
					limsCRSFields.setSex(rowLine[6]);
					/* Phase or stage */
					limsCRSFields.setStadium(rowLine[7]);
					/* Agent */
					limsCRSFields.setLegavit(rowLine[8]);
					/* Collecting start date */
					if (rowLine[9].length() > 0) {
						limsCRSFields.setCollectingDate(rowLine[9]);
					} else {
						limsCRSFields.setCollectingDate("10000101L");
					}
					/* Country */
					limsCRSFields.setCountry(rowLine[10]);
					/* State/province */
					limsCRSFields.setBioRegion(rowLine[11]);
					/* Locality */
					limsCRSFields.setLocality(rowLine[12]);
					/* Latitude */
					limsCRSFields.setLatitudeDecimal(rowLine[13]);
					/* Longitude */
					limsCRSFields.setLongitudeDecimal(rowLine[14]);
					/* Altitude */
					limsCRSFields.setHeight(rowLine[15]);

					/* Add notes to the document(s) */
					setCRSNotes(annotatedPluginDocuments, cnt);

					logger.info("Done with adding notes to the document: "
							+ documentFileName);

					/*
					 * Add registrationnumber to a processlist
					 */
					if (!processedList.contains(resultRegNum)) {
						processedList.add(resultRegNum.toString());
					}
					/*
					 * End duration of the processing of notes
					 */
					calculateTimeForAddingNotes(startBeginTime);
				} // end IF
			}
			cnt++;
		} // end for
		return recordCount;
	}

	/**
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

	/**
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

	/**
	 * @param startBeginTime
	 */
	private void calculateTimeForAddingNotes(long startBeginTime) {
		long endTime = System.nanoTime();
		long elapsedTime = endTime - startBeginTime;
		logger.info("Took: "
				+ (TimeUnit.SECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS))
				+ " second(s)");
		elapsedTime = 0;
		endTime = 0;
	}

	/**
	 * @param recordCount
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

	/**
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

	/**
	 * Set notes to the documents
	 * */
	private void setCRSNotes(AnnotatedPluginDocument[] documents, int cnt) {

		/** set note for Phylum: FieldValue, Label, NoteType, */
		limsNotes.setNoteToAB1FileName(documents, "PhylumCode_CRS",
				"Phylum (CRS)", "Phylum (CRS)", limsCRSFields.getPhylum()
						.trim(), cnt);

		/** Set note for Class */
		limsNotes.setNoteToAB1FileName(documents, "ClassCode_CRS",
				"Class (CRS)", "Class (CRS)", limsCRSFields.getKlasse().trim(),
				cnt);

		/** set note for Order */
		limsNotes.setNoteToAB1FileName(documents, "OrderCode_CRS",
				"Order (CRS)", "Order (CRS)", limsCRSFields.getOrder().trim(),
				cnt);

		/* set note for Family */
		limsNotes.setNoteToAB1FileName(documents, "FamilyCode_CRS",
				"Family (CRS)", "Family (CRS)", limsCRSFields.getFamily()
						.trim(), cnt);

		/** set note for SubFamily */
		if (limsCRSFields.getSubFamily() != null) {
			System.out.println(limsCRSFields.getSubFamily());
			limsNotes.setNoteToAB1FileName(documents, "SubFamilyCode_CRS",
					"Subfamily (CRS)", "Subfamily (CRS)", limsCRSFields
							.getSubFamily().trim(), cnt);
		}

		/** set note for Genus */
		limsNotes.setNoteToAB1FileName(documents, "GenusCode_CRS",
				"Genus (CRS)", "Genus (CRS)", limsCRSFields.getGenus().trim(),
				cnt);

		/** set note for TaxonName */
		limsNotes.setNoteToAB1FileName(documents, "TaxonName1Code_CRS",
				"Scientific name (CRS)", "Scientific name (CRS)", limsCRSFields
						.getTaxon().trim(), cnt);

		/** set note for Identifier */
		limsNotes.setNoteToAB1FileName(documents, "IdentifierCode_CRS",
				"Identifier (CRS)", "Identifier (CRS)", limsCRSFields
						.getDeterminator().trim(), cnt);

		/** set note for Sex */
		limsNotes.setNoteToAB1FileName(documents, "SexCode_CRS", "Sex (CRS)",
				"Sex (CRS)", limsCRSFields.getSex().trim(), cnt);

		/** set note for Phase Or Stage */
		limsNotes.setNoteToAB1FileName(documents, "PhaseOrStageCode_CRS",
				"Stage (CRS)", "Stage (CRS)",
				limsCRSFields.getStadium().trim(), cnt);

		/** set note for Collector */
		limsNotes.setNoteToAB1FileName(documents, "CollectorCode_CRS",
				"Leg (CRS)", "Leg (CRS)", limsCRSFields.getLegavit().trim(),
				cnt);

		/** set note for Collecting date */
		if (limsCRSFields.getCollectingDate().length() > 0) {
			limsNotes.setNoteToAB1FileName(documents, "CollectingDateCode_CRS",
					"Date (CRS)", "Date (CRS)",
					limsCRSFields.getCollectingDate(), cnt);
		}

		/** set note for Country */
		limsNotes.setNoteToAB1FileName(documents, "CountryCode_CRS",
				"Country (CRS)", "Country (CRS)", limsCRSFields.getCountry()
						.trim(), cnt);

		/** set note for BioRegion */
		limsNotes.setNoteToAB1FileName(documents,
				"StateOrProvinceBioRegionCode_CRS", "Region (CRS)",
				"Region (CRS)", limsCRSFields.getBioRegion().trim(), cnt);

		/** set note for Locality */
		limsNotes.setNoteToAB1FileName(documents, "LocalityCode_CRS",
				"Locality (CRS)", "Locality (CRS)", limsCRSFields.getLocality()
						.trim(), cnt);

		/** set note for Latitude */
		limsNotes.setNoteToAB1FileName(documents, "LatitudeDecimalCode_CRS",
				"Lat (CRS)", "Lat (CRS)", limsCRSFields.getLatitudeDecimal(),
				cnt);

		/** set note for Longitude */
		limsNotes.setNoteToAB1FileName(documents, "LongitudeDecimalCode_CRS",
				"Long (CRS)", "Long (CRS)",
				limsCRSFields.getLongitudeDecimal(), cnt);

		/** set note for Height */
		limsNotes.setNoteToAB1FileName(documents, "HeightCode_CRS",
				"Altitude (CRS)", "Altitude (CRS)", limsCRSFields.getHeight(),
				cnt);

		limsNotes.setImportTrueFalseNotes(documents, "CRSCode_CRS",
				"CRS (CRS)", "CRS (CRS)", true, cnt);
	}

	/**
	 * Split the Rank or classification values. Example: order / family /
	 * subfamily / tribe Coleoptera / Leiodidae / Cholevinae / Cholevini Catops
	 * */
	private void extractRankOrClassification(String rankOrClassificationValue,
			String nameValue) {

		String[] forwardSlash = StringUtils.split(rankOrClassificationValue,
				"/");

		for (int i = 0; i < forwardSlash.length; i++) {

			String[] name = StringUtils.split(nameValue, "/");

			if (forwardSlash[i].trim().equals("phylum")) {
				limsCRSFields.setPhylum(name[i]);
			} else if (forwardSlash[i].trim().equals("subclass")) {
				limsCRSFields.setSubclass(name[i]);
			} else if (forwardSlash[i].trim().equals("class")) {
				limsCRSFields.setKlasse(name[i]);
			} else if (forwardSlash[i].trim().equals("suborder")) {
				limsCRSFields.setSuborder(name[i]);
			} else if (forwardSlash[i].trim().equals("order")) {
				limsCRSFields.setOrder(name[i]);
			} else if (forwardSlash[i].trim().equals("family")) {
				limsCRSFields.setFamily(name[i]);
			} else if (forwardSlash[i].trim().equals("superfamily")) {
				limsCRSFields.setSuperFamily(name[i]);
			} else if (forwardSlash[i].trim().equals("subfamily")) {
				limsCRSFields.setSubFamily(name[i]);
			} else if (forwardSlash[i].trim().equals("tribe")) {
				limsCRSFields.setTribe(name[i]);
			}
		}
	}

	/**
	 * Show lacks message of documents that not has been processed
	 * 
	 * @param missing
	 * @return
	 * */
	private String getLackMessage(Boolean missing) {
		if (missing)
			return "[3] At least one selected document lacks ExtractID(Seq)";
		return "";
	}

	/** Clear fields variables */
	private void clearFieldValues() {
		limsCRSFields.setRegistratienummer("");
		limsCRSFields.setPhylum("");
		limsCRSFields.setKlasse("");
		limsCRSFields.setOrder("");
		limsCRSFields.setFamily("");
		limsCRSFields.setSubFamily("");
		limsCRSFields.setGenus("");
		limsCRSFields.setTaxon("");
		limsCRSFields.setDeterminator("");
		limsCRSFields.setSex("");
		limsCRSFields.setStadium("");
		limsCRSFields.setLegavit("");
		limsCRSFields.setCollectingDate("");
		limsCRSFields.setCountry("");
		limsCRSFields.setBioRegion("");
		limsCRSFields.setLocality("");
		limsCRSFields.setLatitudeDecimal("");
		limsCRSFields.setLongitudeDecimal("");
		limsCRSFields.setHeight("");
	}

}
