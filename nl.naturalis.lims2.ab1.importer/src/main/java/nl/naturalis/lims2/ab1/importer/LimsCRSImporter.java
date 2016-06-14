/**
 * 
 */
package nl.naturalis.lims2.ab1.importer;

import java.awt.EventQueue;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
public class LimsCRSImporter extends DocumentAction {

	// private List<AnnotatedPluginDocument> docs;
	private LimsImporterUtil limsImporterUtil = new LimsImporterUtil();
	private LimsCRSFields LimsCRSFields = new LimsCRSFields();
	private LimsNotes limsNotes = new LimsNotes();
	private LimsFileSelector fcd = new LimsFileSelector();
	private LimsReadGeneiousFieldsValues readGeneiousFieldsValues = new LimsReadGeneiousFieldsValues();

	private List<String> msgList = new ArrayList<String>();
	private List<String> msgUitvalList = new ArrayList<String>();
	private List<String> msgMatchList = new ArrayList<String>();
	private List<String> verwerkList = new ArrayList<String>();
	private static final Logger logger = LoggerFactory
			.getLogger(LimsCRSImporter.class);

	public int importCounter;
	private int importTotal;
	private String[] record = null;
	private final String noteCode = "DocumentNoteUtilities-Registr-nmbr (Samples)";
	private final String fieldName = "RegistrationNumberCode_Samples";
	private boolean match = false;
	private String registrationNumber;
	private Object documentFileName = "";
	private String fileSelected = null;
	private Object fasDocument = "";
	private AnnotatedPluginDocument[] documents = null;
	private boolean isRMNHNumber = false;
	private int crsRecordCntVerwerkt = 0;
	private int crsRecordUitval = 0;
	private int crsTotaalRecords = 0;
	private CSVReader csvReader = null;
	private int crsExactRecordsVerwerkt = 0;
	private String regnumber = "";
	private String logCrsFileName = "";
	private LimsLogger limsLogger = null;
	private long startTime;
	long lEndTime = 0;
	long difference = 0;
	// private ArrayList<Object> listContainSamples = new ArrayList<>();

	LimsFrameProgress limsFrameProgress = new LimsFrameProgress();

	@Override
	public void actionPerformed(
			AnnotatedPluginDocument[] annotatedPluginDocuments) {

		/* Get database name */
		readGeneiousFieldsValues.resultDB = readGeneiousFieldsValues
				.getServerDatabaseServiceName();

		if (readGeneiousFieldsValues.resultDB != null) {
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

				logCrsFileName = limsImporterUtil.getLogPath()
						+ "CRS-Uitvallijst-"
						+ limsImporterUtil.getLogFilename();

				limsLogger = new LimsLogger(logCrsFileName);

				for (int cnt = 0; cnt < DocumentUtilities
						.getSelectedDocuments().size(); cnt++) {
					isRMNHNumber = annotatedPluginDocuments[cnt].toString()
							.contains("RegistrationNumberCode_Samples");
				}

				if (!isRMNHNumber) {
					Dialogs.showMessageDialog("At least one selected document lacks Registr-nmbr (Sample).");
					return;
				}

				fileSelected = fcd.loadSelectedFile();
				if (fileSelected == null) {
					return;
				}

				limsFrameProgress.createProgressBar();

				logger.info("Start updating selected document(s) with CRS data.");
				logger.info("-------------------------- S T A R T --------------------------");
				logger.info("Start Reading data from a CRS file.");
				/** Add selected documents to a list. */
				// docs = DocumentUtilities.getSelectedDocuments();

				msgUitvalList.add("Filename: " + fileSelected + "\n");
				msgUitvalList.add("Username: "
						+ System.getProperty("user.name") + "\n");
				msgUitvalList.add("Type action: Import CRS data " + "\n");

				startTime = new Date().getTime();
				for (int cnt = 0; cnt < DocumentUtilities
						.getSelectedDocuments().size(); cnt++) {
					documentFileName = annotatedPluginDocuments[cnt]
							.getFieldValue("cache_name");

					if ((documentFileName.toString().contains("ab1"))
							|| (annotatedPluginDocuments[cnt].toString()
									.contains("fas"))
							&& (!annotatedPluginDocuments[cnt].toString()
									.contains("dum"))) {
						fasDocument = readGeneiousFieldsValues
								.readValueFromAnnotatedPluginDocument(
										annotatedPluginDocuments[cnt],
										"importedFrom", "filename");
					}

					/* Add sequence name for the dialog screen */
					if (DocumentUtilities.getSelectedDocuments().listIterator()
							.hasNext()) {
						msgList.add(documentFileName + "\n");
					}

					/* Check of the filename contain "FAS" extension */
					if (fasDocument.toString().contains("fas")
							&& fasDocument != null) {
						documentFileName = annotatedPluginDocuments[cnt]
								.getFieldValue("cache_name");
					} else {
						/* get AB1 filename */
						if (!annotatedPluginDocuments[cnt].toString().contains(
								"consensus sequence")
								|| !annotatedPluginDocuments[cnt].toString()
										.contains("Contig")) {
							documentFileName = annotatedPluginDocuments[cnt]
									.getName();
						}
					}

					documents = annotatedPluginDocuments;

					if (crsTotaalRecords == 0) {
						try {
							csvReader = new CSVReader(new FileReader(
									fileSelected), '\t', '\'', 0);
							crsTotaalRecords = csvReader.readAll().size();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						csvReader = null;
					}
					/* Add notes */
					readDataFromCRS_Into_Memory(documents[cnt], fileSelected,
							cnt, (String) documentFileName);
					// readDataFromCRSFile(documents[cnt], fileSelected, cnt,
					// (String) documentFileName);
					importCounter = DocumentUtilities.getSelectedDocuments()
							.size();

					limsFrameProgress
							.showProgress(annotatedPluginDocuments[cnt]
									.getName());
				}

				logger.info("--------------------------------------------------------");
				logger.info("Total of document(s) updated: " + importCounter);
				logger.info("-------------------------- E N D --------------------------");
				logger.info("Done with updating the selected document(s). ");

				if (documentFileName != null) {
					msgMatchList.add("No document(s) match found for : "
							+ documentFileName);

					msgUitvalList.add("Total records not matched: "
							+ Integer.toString(msgUitvalList.size()) + "\n");
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
						crsRecordUitval = msgUitvalList.size() - 1;
						crsExactRecordsVerwerkt = (crsTotaalRecords - (msgUitvalList
								.size() - 1));

						Dialogs.showMessageDialog(Integer
								.toString(crsTotaalRecords)
								+ " records have been read of which: "
								+ "\n"
								+ "[1] "
								+ crsExactRecordsVerwerkt
								+ " records are imported and linked to "
								+ Integer.toString(crsRecordCntVerwerkt)
								+ " existing documents (of "
								+ importCounter
								+ " selected)"
								+ "\n"
								+ "\n"
								+ "List of "
								+ Integer.toString(importCounter)
								+ " selected documents: "
								+ "\n"
								+ msgList.toString()
								+ "\n"
								+ "\n"
								+ "[2] "
								+ Integer.toString(crsRecordUitval)
								+ " records are ignored.");

						logger.info(crsExactRecordsVerwerkt
								+ " records are imported and linked to "
								+ Integer.toString(crsRecordCntVerwerkt)
								+ " existing documents (of " + importCounter
								+ " selected)" + "\n" + "\n" + "List of "
								+ Integer.toString(importCounter));
						logger.info(Integer.toString(crsRecordUitval)
								+ " records are ignored.");

						/*
						 * limsLogger.logToFile(logCrsFileName,
						 * msgUitvalList.toString());
						 */

						limsLogger.logToFile(logCrsFileName,
								msgUitvalList.toString());

						msgList.clear();
						msgUitvalList.clear();
						verwerkList.clear();
						match = false;
						limsFrameProgress.hideFrame();
						crsExactRecordsVerwerkt = 0;
						crsRecordUitval = 0;
						crsRecordCntVerwerkt = 0;

					}
				});
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

	@Override
	public GeneiousActionOptions getActionOptions() {
		return new GeneiousActionOptions("3 CRS", "CRS Import")
				.setToolbarName("3 CRS").setInPopupMenu(true)
				.setMainMenuLocation(GeneiousActionOptions.MainMenu.Tools, 2.0)
				.setInMainToolbar(true).setInPopupMenu(true)
				.setAvailableToWorkflows(true);

		// the parent options need to be shared between the two document
		// operations so you should create a method to get the same options from
		// both places.
		/*
		 * GeneiousActionOptions parent = new GeneiousActionOptions("Naturalis",
		 * "CRS import")
		 * .setMainMenuLocation(GeneiousActionOptions.MainMenu.Tools, 2.0)
		 * .setInMainToolbar(true).setInPopupMenu(true); GeneiousActionOptions
		 * submenuItem1 = new GeneiousActionOptions("3 CRS", "CRS Import");
		 * GeneiousActionOptions sub1 =
		 * parent.createSubmenuActionOptions(parent, submenuItem1);
		 * 
		 * return sub1;
		 */
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

	private void readDataFromCRSFile(
			AnnotatedPluginDocument annotatedPluginDocuments, String fileName,
			int i, String documentName) {

		msgUitvalList.clear();
		try {
			csvReader = new CSVReader(new FileReader(fileName), '\t', '\'', 0);
			csvReader.readNext();

			try {
				while ((record = csvReader.readNext()) != null) {
					if (record.length == 0) {
						continue;
					}

					registrationNumber = record[0];

					String cacheNameCopy = "";
					if (documentName.contains("Copy")
							|| documentName.contains("kopie")) {
						cacheNameCopy = "//document/hiddenFields/override_cache_name";
					} else {
						cacheNameCopy = "//document/hiddenFields/cache_name";
					}

					regnumber = readGeneiousFieldsValues
							.getRegistrationNumberFromTableAnnotatedDocument(
									documentName,
									"//document/notes/note/RegistrationNumberCode_Samples",
									cacheNameCopy);

					if (regnumber.equals(registrationNumber)) {

						logger.info("Registration number matched: "
								+ registrationNumber);

						match = true;
						crsRecordCntVerwerkt++;

						LimsCRSFields.setRegistratienummer(record[0]);
						extractRankOrClassification(record[1], record[2]);
						LimsCRSFields.setGenus(record[3]);
						LimsCRSFields.setTaxon(record[4]);
						LimsCRSFields.setDeterminator(record[5]);
						LimsCRSFields.setSex(record[6]);
						LimsCRSFields.setStadium(record[7]);
						LimsCRSFields.setLegavit(record[8]);
						LimsCRSFields.setCollectingDate(record[9]);
						LimsCRSFields.setCountry(record[10]);
						LimsCRSFields.setBioRegion(record[11]);
						LimsCRSFields.setLocality(record[12]);
						LimsCRSFields.setLatitudeDecimal(record[13]);
						LimsCRSFields.setLongitudeDecimal(record[14]);
						LimsCRSFields.setHeight(record[15]);

						/* Add notes */
						setCRSNotes(documents, i);
						logger.info("Done with adding notes to the document: "
								+ documentFileName);

						setCRSNotesLog();

						verwerkList.add(record[0]);

					} // end IF
					else if (!verwerkList.contains(registrationNumber)
							&& !match) {

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

						if (!msgUitvalList
								.contains("No document(s) match found for Registrationnumber: "
										+ registrationNumber)) {
							msgUitvalList
									.add("No document(s) match found for Registrationnumber: "
											+ registrationNumber + "\n");
						}
					}
					match = false;
				} // end While
				importTotal = crsRecordCntVerwerkt;
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				csvReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setCRSNotesLog() {
		logger.info("Start with adding notes to the document");
		logger.info("CollectionRegistrationNumber: "
				+ LimsCRSFields.getRegistratienummer());
		logger.info("Phylum: " + LimsCRSFields.getPhylum());
		logger.info("Classification: " + LimsCRSFields.getKlasse());
		logger.info("Orde: " + LimsCRSFields.getOrder());
		logger.info("Family: " + LimsCRSFields.getFamily());
		logger.info("SubFamily: " + LimsCRSFields.getSubFamily());
		logger.info("Genus: " + LimsCRSFields.getGenus());
		logger.info("Taxon: " + LimsCRSFields.getTaxon());
		logger.info("Determinator: " + LimsCRSFields.getDeterminator());
		logger.info("Sex: " + LimsCRSFields.getSex());
		logger.info("Stadium: " + LimsCRSFields.getStadium());
		logger.info("Legavit: " + LimsCRSFields.getLegavit());
		logger.info("CollectionDate: " + LimsCRSFields.getCollectingDate());
		logger.info("Country: " + LimsCRSFields.getCountry());
		logger.info("Region: " + LimsCRSFields.getBioRegion());
		logger.info("Location: " + LimsCRSFields.getLocality());
		logger.info("LatitudeDecimal: " + LimsCRSFields.getLatitudeDecimal());
		logger.info("LongitudeDecimal: " + LimsCRSFields.getLongitudeDecimal());
		logger.info("Height: " + LimsCRSFields.getHeight());

	}

	/** DocumentNoteUtilities-Registration number */
	/** Get value from "BasisOfRecordCode" */
	@SuppressWarnings("unused")
	private boolean matchRegistrationNumber(
			AnnotatedPluginDocument[] annotatedPluginDocuments,
			Object registrationNumber, int i) {

		Object fieldValue = readGeneiousFieldsValues
				.readValueFromAnnotatedPluginDocument(
						annotatedPluginDocuments[i], noteCode, fieldName);
		if (fieldValue == null) {
			EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					Dialogs.showMessageDialog("Geen value gevonden. Eerst sample uitvoeren");
					logger.info("Sample-method has not been imported. Run Sample import first ");

				}
			});
		}

		if (registrationNumber.equals(fieldValue)) {
			return true;
		}
		return false;
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

	private int getFileCount(String fileName) throws IOException {
		InputStream is = new BufferedInputStream(new FileInputStream(fileName));
		byte[] chars = new byte[1024];
		int numberOfChars = 0;
		int count = 0;
		while ((numberOfChars = is.read(chars)) != -1) {
			for (int i = 0; i < numberOfChars; ++i) {
				if (chars[i] == '\n' && numberOfChars - i != 1) {
					++count;
				}
			}
		}
		count++;
		return count; // number of lines

	}

	public Reader getReader(String relativePath) {
		try {
			return new InputStreamReader(this.getClass().getResourceAsStream(
					relativePath), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("Unable to read input", e);
		}
	}

	public void printAndValidate(Object[] headers, Collection<?> rows) {

		if (headers != null) {
			System.out.println(Arrays.toString(headers));
			System.out.println("=======================");
		}

		int rowCount = 1;
		for (Object row : rows) {
			System.out.println((rowCount++) + " "
					+ Arrays.toString((Object[]) row));
			System.out.println("-----------------------");
		}

		// printAndValidate();
	}

	public void printAndValidate(Collection<?> rows) {
		printAndValidate(null, rows);
	}

	private void readDataFromCRS_Into_Memory(
			AnnotatedPluginDocument annotatedPluginDocuments, String fileName,
			int cnt, String documentName) {

		msgUitvalList.clear();
		String line = "";
		String cvsSplitBy = "\t";
		int recordCount = 0;
		try {

			long startTime = System.nanoTime();
			InputStream in = new FileInputStream(new File(fileName));
			BufferedReader bufReader = new BufferedReader(
					new InputStreamReader(in));
			while ((line = bufReader.readLine()) != null) {
				if (line.length() == 0) {
					continue;
				}

				String[] row = line.split(cvsSplitBy);

				registrationNumber = row[0];

				String cacheNameCopy = "";
				if (documentName.contains("Copy")
						|| documentName.contains("kopie")) {
					cacheNameCopy = "//document/hiddenFields/override_cache_name";
				} else {
					cacheNameCopy = "//document/hiddenFields/cache_name";
				}

				regnumber = readGeneiousFieldsValues
						.getRegistrationNumberFromTableAnnotatedDocument(
								documentName,
								"//document/notes/note/RegistrationNumberCode_Samples",
								cacheNameCopy);
				recordCount++;
				if (regnumber.equals(registrationNumber)) {

					limsFrameProgress.showProgress("Match : "
							+ registrationNumber + "\n" + "  Recordcount: "
							+ recordCount);

					logger.info("Registration number matched: "
							+ registrationNumber);
					logger.info("Start with adding notes to the document");

					match = true;
					crsRecordCntVerwerkt++;

					clearVariabelen();

					for (int i = 0; i < row.length; i++) {
						LimsCRSFields.setRegistratienummer(row[0]);
						extractRankOrClassification(row[1], row[2]);
						LimsCRSFields.setGenus(row[3]);
						LimsCRSFields.setTaxon(row[4]);
						LimsCRSFields.setDeterminator(row[5]);
						LimsCRSFields.setSex(row[6]);
						LimsCRSFields.setStadium(row[7]);
						LimsCRSFields.setLegavit(row[8]);
						if (row[9].length() > 0) {
							LimsCRSFields.setCollectingDate(row[9]);
						} else {
							LimsCRSFields.setCollectingDate("10000101L");
						}
						LimsCRSFields.setCountry(row[10]);
						if (i == 11) {
							LimsCRSFields.setBioRegion(row[i]);
						}

						if (i == 12) {
							LimsCRSFields.setLocality(row[i]);
						}

						if (i == 13) {
							LimsCRSFields.setLatitudeDecimal(row[i]);
						}

						if (i == 14) {
							LimsCRSFields.setLongitudeDecimal(row[i]);
						}
						if (i == 15) {
							LimsCRSFields.setHeight(row[i]);
						}
					}

					/* Add notes */
					setCRSNotes(documents, cnt);
					logger.info("Done with adding notes to the document: "
							+ documentFileName);

					// setCRSNotesLog();
					if (row[0] != null) {
						verwerkList.add(row[0]);
					}
					long endTime = System.nanoTime();
					long elapsedTime = endTime - startTime;
					logger.info("Took: "
							+ (TimeUnit.SECONDS.convert(elapsedTime,
									TimeUnit.NANOSECONDS)) + " second(s)");
					;
					break;
				} // end IF
				else if (!verwerkList.contains(registrationNumber) && !match) {
					// else if (!match) {

					if (!msgUitvalList
							.contains("No document(s) match found for Registrationnumber: "
									+ registrationNumber)) {
						msgUitvalList
								.add("No document(s) match found for Registrationnumber: "
										+ registrationNumber + "\n");
						/*
						 * limsFrameProgress.showProgress("No Match : " +
						 * registrationNumber + "\n" + "  Recordcount: " +
						 * recordCount);
						 */
					}

				}
				match = false;

			}
			bufReader.close();
			in.close();

			importTotal = crsRecordCntVerwerkt;
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	private void clearVariabelen() {
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

	private void readDataFromCRSFrom_File(
			AnnotatedPluginDocument[] annotatedPluginDocuments) {

		fileSelected = fcd.loadSelectedFile();
		if (fileSelected == null) {
			return;
		}

		msgUitvalList.clear();
		String line = "";
		String cvsSplitBy = "\t";
		int recordCount = 0;
		try {

			long startTime = System.nanoTime();
			InputStream in = new FileInputStream(new File(fileSelected));
			BufferedReader bufReader = new BufferedReader(
					new InputStreamReader(in));

			documentFileName = annotatedPluginDocuments[0]
					.getFieldValue("cache_name");

			while ((line = bufReader.readLine()) != null) {
				if (line.length() == 0) {
					continue;
				}

				String[] row = line.split(cvsSplitBy);

				registrationNumber = row[0];

				recordCount++;

				String cacheNameCopy = "";
				if (documentFileName.toString().contains("Copy")
						|| documentFileName.toString().contains("kopie")) {
					cacheNameCopy = "//document/hiddenFields/override_cache_name";
				} else {
					cacheNameCopy = "//document/hiddenFields/cache_name";
				}

				regnumber = readGeneiousFieldsValues
						.getRegistrationNumberFromTableAnnotatedDocument(
								documentFileName,
								"//document/notes/note/RegistrationNumberCode_Samples",
								cacheNameCopy);

				if (regnumber.equals(registrationNumber)) {

					limsFrameProgress.showProgress("Match : "
							+ registrationNumber + "\n" + "  Recordcount: "
							+ recordCount);

					logger.info("Registration number matched: "
							+ registrationNumber);

					match = true;
					crsRecordCntVerwerkt++;

					for (int i = 0; i < row.length; i++) {
						LimsCRSFields.setRegistratienummer(row[0]);
						extractRankOrClassification(row[1], row[2]);
						LimsCRSFields.setGenus(row[3]);
						LimsCRSFields.setTaxon(row[4]);
						LimsCRSFields.setDeterminator(row[5]);
						LimsCRSFields.setSex(row[6]);
						LimsCRSFields.setStadium(row[7]);
						LimsCRSFields.setLegavit(row[8]);
						if (row[9].length() > 0) {
							LimsCRSFields.setCollectingDate(row[9]);
						} else {
							LimsCRSFields.setCollectingDate("10000101L");
						}
						LimsCRSFields.setCountry(row[10]);
						if (i == 11) {
							LimsCRSFields.setBioRegion(row[i]);
						}

						if (i == 12) {
							LimsCRSFields.setLocality(row[i]);
						}

						if (i == 13) {
							LimsCRSFields.setLatitudeDecimal(row[i]);
						}

						if (i == 14) {
							LimsCRSFields.setLongitudeDecimal(row[i]);
						}
						if (i == 15) {
							LimsCRSFields.setHeight(row[i]);
						}
					}

					/* Add notes */
					setCRSNotes(documents, 0);
					logger.info("Done with adding notes to the document: "
							+ documentFileName);

					setCRSNotesLog();
					if (row[0] != null) {
						verwerkList.add(row[0]);
					}
					long endTime = System.nanoTime();
					System.out.println("Took: " + (endTime - startTime));

				} // end IF
				else if (!verwerkList.contains(registrationNumber) && !match) {

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

					if (!msgUitvalList
							.contains("No document(s) match found for Registrationnumber: "
									+ registrationNumber)) {
						msgUitvalList
								.add("No document(s) match found for Registrationnumber: "
										+ registrationNumber + "\n");
					}

					limsFrameProgress.showProgress("No match : "
							+ registrationNumber + "\n" + "  Recordcount: "
							+ recordCount);
				}
				match = false;
			}
			in.close();
			bufReader.close();
			importTotal = crsRecordCntVerwerkt;
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

}
