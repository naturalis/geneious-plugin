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
import com.biomatters.geneious.publicapi.documents.sequence.SequenceDocument;
import com.biomatters.geneious.publicapi.implementations.DefaultAlignmentDocument;
import com.biomatters.geneious.publicapi.implementations.sequence.DefaultNucleotideSequence;
import com.biomatters.geneious.publicapi.plugin.DocumentAction;
import com.biomatters.geneious.publicapi.plugin.DocumentOperationException;
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

	private SequenceDocument seq = null;
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
	private DefaultNucleotideSequence defaultNucleotideSequence = null;
	private DefaultAlignmentDocument alignmentDocument = null;
	private Object documentFileName = "";
	private String fileSelected = "";
	private Object regnr = "";
	private boolean result = false;
	AnnotatedPluginDocument[] documents = null;

	String logFileName = limsImporterUtil.getLogPath() + File.separator
			+ "CRS-Uitvallijst-" + limsImporterUtil.getLogFilename();

	LimsLogger limsLogger = new LimsLogger(logFileName);

	@Override
	public void actionPerformed(
			AnnotatedPluginDocument[] annotatedPluginDocuments) {

		if (DocumentUtilities.getSelectedDocuments().isEmpty()) {
			EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					Dialogs.showMessageDialog("Select all documents");
					return;
				}
			});
		}

		if (!DocumentUtilities.getSelectedDocuments().isEmpty()) {

			fileSelected = fcd.loadSelectedFile();
			if (fileSelected == null) {
				return;
			}

			logger.info("Start updating selected document(s) with CRS data.");
			logger.info("-------------------------- S T A R T --------------------------");
			logger.info("Start Reading data from a CRS file.");
			try {
				/** Add selected documents to a list. */
				docs = DocumentUtilities.getSelectedDocuments();

				msgUitvalList.add("Filename: " + fileSelected + "\n");
				msgUitvalList.add("Username: "
						+ System.getProperty("user.name") + "\n");
				msgUitvalList.add("Type action: Import CRS data " + "\n");

				for (int cnt = 0; cnt < docs.size(); cnt++) {
					documentFileName = annotatedPluginDocuments[cnt]
							.getFieldValue("cache_name");

					result = false;

					/* Add sequence name for the dialog screen */
					if (DocumentUtilities.getSelectedDocuments().listIterator()
							.hasNext()) {
						msgList.add(documentFileName + "\n");
					}

					/* Reads Assembly Contig 1 consensus sequence */
					try {
						if (readGeneiousFieldsValues
								.getCacheNameFromGeneiousDatabase(
										documentFileName,
										"//document/hiddenFields/cache_name")
								.equals(documentFileName)
								&& !documentFileName.toString().contains("ab1")) {

							defaultNucleotideSequence = (DefaultNucleotideSequence) docs
									.get(cnt).getDocument();

							logger.info("Selected Contig consensus sequence document: "
									+ defaultNucleotideSequence.getName());

							result = true;
							logger.debug("Result CRS :" + result);
						}
					} catch (IOException e2) {
						e2.printStackTrace();
					}

					/* Reads Assembly Contig 1 file */
					try {
						if (readGeneiousFieldsValues
								.getCacheNameFromGeneiousDatabase(
										documentFileName,
										"//document/hiddenFields/override_cache_name")
								.equals(documentFileName)) {
							alignmentDocument = (DefaultAlignmentDocument) docs
									.get(cnt).getDocument();

							logger.info("Selected Contig document: "
									+ alignmentDocument.getName());

							result = true;
							logger.debug("Result CRS :" + result);
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					/* AB1 file */
					try {
						if (readGeneiousFieldsValues
								.getFileNameFromGeneiousDatabase(
										(String) documentFileName).equals(
										documentFileName)) {
							seq = (SequenceDocument) docs.get(cnt)
									.getDocument();

							logger.info("Selected AB1 document: "
									+ seq.getName());

							result = true;
							logger.debug("Result CRS :" + result);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}

					if (result) {
						documents = annotatedPluginDocuments;
						readDataFromCRSFile(documents[cnt], fileSelected, cnt);
						/* Add notes */
						// setCRSNotes(annotatedPluginDocuments, cnt);
						// logger.info("Done with adding notes to the document: "
						// + documentFileName);
						importCounter = DocumentUtilities
								.getSelectedDocuments().size();
					}
				}
			} catch (DocumentOperationException e) {
				e.printStackTrace();
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
							+ Integer.toString(DocumentUtilities
									.getSelectedDocuments().size())
							+ " out of " + Integer.toString(importTotal)
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

				}
			});

		}
	}

	private void setCRSNotes(AnnotatedPluginDocument[] documents, int cnt) {

		// if (regnr.equals(registrationNumber)) {
		/** set note for Phylum: FieldValue, Label, NoteType, */
		limsNotes.setNoteToAB1FileName(documents, "PhylumCode_CRS",
				"Phylum (CRS)", "Phylum (CRS)", LimsCRSFields.getPhylum(), cnt);

		/** Set note for Class */
		limsNotes.setNoteToAB1FileName(documents, "ClassCode_CRS",
				"Class (CRS)", "Class (CRS)",
				LimsCRSFields.getClassification(), cnt);

		/** set note for Order */
		limsNotes.setNoteToAB1FileName(documents, "OrderCode_CRS",
				"Order (CRS)", "Order (CRS)", LimsCRSFields.getOrder(), cnt);

		/* set note for Family */
		limsNotes.setNoteToAB1FileName(documents, "FamilyCode_CRS",
				"Family (CRS)", "Family (CRS)", LimsCRSFields.getFamily(), cnt);

		/** set note for SubFamily */
		limsNotes.setNoteToAB1FileName(documents, "SubFamilyCode_CRS",
				"Subfamily (CRS)", "Subfamily (CRS)",
				LimsCRSFields.getSubFamily(), cnt);

		/** set note for Genus */
		limsNotes.setNoteToAB1FileName(documents, "GenusCode_CRS",
				"Genus (CRS)", "Genus (CRS)", LimsCRSFields.getGenus(), cnt);

		/** set note for TaxonName */
		limsNotes.setNoteToAB1FileName(documents, "TaxonName1Code_CRS",
				"Scientific name (CRS)", "Scientific name (CRS)",
				LimsCRSFields.getTaxon(), cnt);

		/** set note for Identifier */
		limsNotes.setNoteToAB1FileName(documents, "IdentifierCode_CRS",
				"Identifier (CRS)", "Identifier (CRS)",
				LimsCRSFields.getDeterminator(), cnt);

		/** set note for Sex */
		limsNotes.setNoteToAB1FileName(documents, "SexCode_CRS", "Sex (CRS)",
				"Sex (CRS)", LimsCRSFields.getSex(), cnt);

		/** set note for Phase Or Stage */
		limsNotes.setNoteToAB1FileName(documents, "PhaseOrStageCode_CRS",
				"Stage (CRS)", "Stage (CRS)", LimsCRSFields.getStadium(), cnt);

		/** set note for Collector */
		limsNotes.setNoteToAB1FileName(documents, "CollectorCode_CRS",
				"Leg (CRS)", "Leg (CRS)", LimsCRSFields.getLegavit(), cnt);

		/** set note for Collecting date */
		limsNotes.setNoteToAB1FileName(documents, "CollectingDateCode_CRS",
				"Date (CRS)", "Date (CRS)", LimsCRSFields.getCollectingDate(),
				cnt);

		/** set note for Country */
		limsNotes.setNoteToAB1FileName(documents, "CountryCode_CRS",
				"Country (CRS)", "Country (CRS)", LimsCRSFields.getCountry(),
				cnt);

		/** set note for BioRegion */
		limsNotes.setNoteToAB1FileName(documents,
				"StateOrProvinceBioRegionCode_CRS", "Region (CRS)",
				"Region (CRS)", LimsCRSFields.getBioRegion(), cnt);

		/** set note for Locality */
		limsNotes.setNoteToAB1FileName(documents, "LocalityCode_CRS",
				"Locality (CRS)", "Locality (CRS)",
				LimsCRSFields.getLocality(), cnt);

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
		// }
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
			AnnotatedPluginDocument annotatedPluginDocument, String fileName,
			int i) {

		int counter = 0;
		int cntVerwerkt = 0;

		try {
			CSVReader csvReader = new CSVReader(new FileReader(fileName), '\t',
					'\'', 0);
			csvReader.readNext();

			try {
				/*
				 * msgUitvalList
				 * .add("-----------------------------------------------" +
				 * "\n");
				 */

				while ((record = csvReader.readNext()) != null) {
					if (record.length == 0) {
						continue;
					}

					registrationNumber = record[0];

					// if (regnr.equals(record[0]))
					if (matchRegistrationNumber(annotatedPluginDocument,
							record[0])) {

						System.out.println("Registration number matched: "
								+ registrationNumber);

						match = true;

						LimsCRSFields.setRegistratienummer(record[0]);
						LimsCRSFields.setPhylum(record[1]);
						LimsCRSFields.setClassification(record[2]);
						LimsCRSFields.setOrder(record[3]);
						LimsCRSFields.setFamily(record[4]);
						LimsCRSFields.setSubFamily(record[5]);
						LimsCRSFields.setGenus(record[6]);
						LimsCRSFields.setTaxon(record[7]);
						LimsCRSFields.setDeterminator(record[8]);
						LimsCRSFields.setSex(record[9]);
						LimsCRSFields.setStadium(record[10]);
						LimsCRSFields.setLegavit(record[11]);
						LimsCRSFields.setCollectingDate(record[12]);
						LimsCRSFields.setCountry(record[13]);
						LimsCRSFields.setBioRegion(record[14]);
						LimsCRSFields.setLocality(record[15]);
						LimsCRSFields.setLatitudeDecimal(record[16]);
						LimsCRSFields.setLongitudeDecimal(record[17]);
						LimsCRSFields.setHeight(record[18]);

						/* Add notes */
						setCRSNotes(documents, i);
						logger.info("Done with adding notes to the document: "
								+ documentFileName);

						logger.info("Start with adding notes to the document");
						logger.info("CollectionRegistrationNumber: "
								+ LimsCRSFields.getRegistratienummer());
						logger.info("Phylum: " + LimsCRSFields.getPhylum());
						logger.info("Classification: "
								+ LimsCRSFields.getClassification());
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
						LimsCRSFields.setClassification("");
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
	private boolean matchRegistrationNumber(
			AnnotatedPluginDocument annotatedPluginDocument,
			String registrationNumber) {

		Object fieldValue = readGeneiousFieldsValues
				.readValueFromAnnotatedPluginDocument(annotatedPluginDocument,
						noteCode, fieldName);
		if (registrationNumber.equals(fieldValue)) {
			return true;
		}
		return false;
	}

	private String getExtractIDFromAB1FileName(String fileName) {
		/* for example: e4010125015_Sil_tri_MJ243_COI-A01_M13F_A01_008.ab1 */
		String[] underscore = StringUtils.split(fileName, "_");
		return underscore[0];
	}
}
