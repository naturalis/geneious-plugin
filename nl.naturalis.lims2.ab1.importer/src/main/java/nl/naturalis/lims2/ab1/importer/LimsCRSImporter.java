/**
 * 
 */
package nl.naturalis.lims2.ab1.importer;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

	private List<AnnotatedPluginDocument> docs;
	private LimsImporterUtil limsImporterUtil = new LimsImporterUtil();
	private LimsCRSFields LimsCRSFields = new LimsCRSFields();
	private LimsNotes limsNotes = new LimsNotes();
	private LimsFileSelector fcd = new LimsFileSelector();
	private LimsReadGeneiousFieldsValues readGeneiousFieldsValues = new LimsReadGeneiousFieldsValues();

	private List<String> msgList = new ArrayList<String>();
	private List<String> msgUitvalList = new ArrayList<String>();
	private List<String> msgMatchList = new ArrayList<String>();
	private List<String> verwerkingCnt = new ArrayList<String>();
	private List<String> verwerkList = new ArrayList<String>();
	private static final Logger logger = LoggerFactory
			.getLogger(LimsReadDataFromSamples.class);

	public int importCounter;
	private int importTotal;
	private String[] record = null;
	private final String noteCode = "DocumentNoteUtilities-Registr-nmbr (Samples)";
	private final String fieldName = "RegistrationNumberCode_Samples";
	private boolean match = false;
	private String registrationNumber;
	private Object documentFileName = "";
	private String fileSelected = "";
	private Object fasDocument = "";
	private AnnotatedPluginDocument[] documents = null;
	private boolean isRMNHNumber = false;

	String logFileName = limsImporterUtil.getLogPath() + File.separator
			+ "CRS-Uitvallijst-" + limsImporterUtil.getLogFilename();

	LimsLogger limsLogger = new LimsLogger(logFileName);

	LimsFrameProgress limsFrameProgress = new LimsFrameProgress();

	@Override
	public void actionPerformed(
			AnnotatedPluginDocument[] annotatedPluginDocuments) {

		/* Get database name */
		readGeneiousFieldsValues.resultDB = readGeneiousFieldsValues
				.getServerDatabaseServiceName();

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

			isRMNHNumber = DocumentUtilities.getSelectedDocuments().iterator()
					.next().toString()
					.contains("RegistrationNumberCode_Samples");

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
			docs = DocumentUtilities.getSelectedDocuments();

			msgUitvalList.add("Filename: " + fileSelected + "\n");
			msgUitvalList.add("Username: " + System.getProperty("user.name")
					+ "\n");
			msgUitvalList.add("Type action: Import CRS data " + "\n");

			for (int cnt = 0; cnt < docs.size(); cnt++) {
				documentFileName = annotatedPluginDocuments[cnt]
						.getFieldValue("cache_name");

				if (documentFileName.toString().contains("ab1")
						|| docs.toString().contains("fas")
						&& !docs.toString().contains("dum")) {
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
					documentFileName = (String) readGeneiousFieldsValues
							.readValueFromAnnotatedPluginDocument(
									annotatedPluginDocuments[cnt],
									"DocumentNoteUtilities-Extract ID (Seq)",
									"ExtractIDCode_Seq");

				} else {
					/* get AB1 filename */
					if (!docs.toString().contains("consensus sequence")
							|| !docs.toString().contains("Contig")) {
						documentFileName = docs.get(cnt).getName();
					}
				}

				documents = annotatedPluginDocuments;
				/* Add notes */
				readDataFromCRSFile(documents[cnt], fileSelected, cnt,
						(String) documentFileName);
				importCounter = DocumentUtilities.getSelectedDocuments().size();

				limsFrameProgress.showProgress(docs.get(cnt).getName());
			}
			logger.info("--------------------------------------------------------");
			logger.info("Total of document(s) updated: " + importCounter);
			logger.info("-------------------------- E N D --------------------------");
			logger.info("Done with updating the selected document(s). ");

			if (documentFileName != null) {
				msgMatchList.add("No document(s) match found for : "
						+ documentFileName);
				int rest = importTotal - verwerkList.size();
				msgUitvalList.add("Total records not matched: "
						+ Integer.toString(rest) + "\n");
			}
			EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					Dialogs.showMessageDialog("CRS: "
							+ Integer.toString(importTotal)
							+ " out of "
							+ Integer.toString(DocumentUtilities
									.getSelectedDocuments().size())
							+ " documents are imported." + "\n"
							+ msgList.toString());
					logger.info("CRS: Total imported document(s): "
							+ msgList.toString());

					limsLogger.logToFile(logFileName, msgUitvalList.toString());
					msgList.clear();
					msgUitvalList.clear();
					verwerkingCnt.clear();
					verwerkList.clear();
					match = false;
					limsFrameProgress.hideFrame();
				}
			});

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
		limsNotes.setNoteToAB1FileName(documents, "CollectingDateCode_CRS",
				"Date (CRS)", "Date (CRS)", LimsCRSFields.getCollectingDate(),
				cnt);

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
		return new GeneiousActionOptions("3 CRS").setInPopupMenu(true)
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

	private void readDataFromCRSFile(
			AnnotatedPluginDocument annotatedPluginDocuments, String fileName,
			int i, String documentName) {

		int counter = 0;
		int cntVerwerkt = 0;

		try {
			CSVReader csvReader = new CSVReader(new FileReader(fileName), '\t',
					'\'', 0);
			csvReader.readNext();

			try {
				while ((record = csvReader.readNext()) != null) {
					if (record.length == 0) {
						continue;
					}

					registrationNumber = record[0];

					String regnumber = readGeneiousFieldsValues
							.getRegistrationNumberFromTableAnnotatedDocument(
									documentName,
									"//document/notes/note/RegistrationNumberCode_Samples",
									"//document/hiddenFields/cache_name");

					if (regnumber.equals(registrationNumber)) {

						logger.info("Registration number matched: "
								+ registrationNumber);

						match = true;

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

						logger.info("Start with adding notes to the document");
						logger.info("CollectionRegistrationNumber: "
								+ LimsCRSFields.getRegistratienummer());
						logger.info("Phylum: " + LimsCRSFields.getPhylum());
						logger.info("Classification: "
								+ LimsCRSFields.getKlasse());
						logger.info("Orde: " + LimsCRSFields.getOrder());
						logger.info("Family: " + LimsCRSFields.getFamily());
						logger.info("SubFamily: "
								+ LimsCRSFields.getSubFamily());
						logger.info("Genus: " + LimsCRSFields.getGenus());
						logger.info("Taxon: " + LimsCRSFields.getTaxon());
						logger.info("Determinator: "
								+ LimsCRSFields.getDeterminator());
						logger.info("Sex: " + LimsCRSFields.getSex());
						logger.info("Stadium: " + LimsCRSFields.getStadium());
						logger.info("Legavit: " + LimsCRSFields.getLegavit());
						logger.info("CollectionDate: "
								+ LimsCRSFields.getCollectingDate());
						logger.info("Country: " + LimsCRSFields.getCountry());
						logger.info("Region: " + LimsCRSFields.getBioRegion());
						logger.info("Location: " + LimsCRSFields.getLocality());
						logger.info("LatitudeDecimal: "
								+ LimsCRSFields.getLatitudeDecimal());
						logger.info("LongitudeDecimal: "
								+ LimsCRSFields.getLongitudeDecimal());
						logger.info("Height: " + LimsCRSFields.getHeight());

						// counter--;
						cntVerwerkt++;
						verwerkingCnt.add(Integer.toString(cntVerwerkt));
						verwerkList.add(record[0]);

					} // end IF
					else if (!verwerkList.contains(record[0]) && !match) {
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

						msgUitvalList
								.add("No document(s) match found for Registrationnumber: "
										+ record[0] + "\n");
						match = false;

					}
					counter++;
				} // end While
				importTotal = counter;
				counter = counter - verwerkingCnt.size();

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

			// switch (forwardSlash[i].trim()) {
			// case "phylum":
			// LimsCRSFields.setPhylum(name[i]);
			// break;
			// case "subclass":
			// LimsCRSFields.setSubclass(name[i]);
			// break;
			// case "class":
			// LimsCRSFields.setKlasse(name[i]);
			// break;
			// case "suborder":
			// LimsCRSFields.setSuborder(name[i]);
			// break;
			// case "order":
			// LimsCRSFields.setOrder(name[i]);
			// break;
			// case "family":
			// LimsCRSFields.setFamily(name[i]);
			// break;
			// case "superfamily":
			// LimsCRSFields.setSuperFamily(name[i]);
			// break;
			// case "subfamily":
			// LimsCRSFields.setSubFamily(name[i]);
			// break;
			// case "tribe":
			// LimsCRSFields.setTribe(name[i]);
			// break;
			// }
		}
	}

}
