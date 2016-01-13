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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biomatters.geneious.publicapi.components.Dialogs;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.documents.PluginDocument;
import com.biomatters.geneious.publicapi.documents.sequence.SequenceDocument;
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

	private SequenceDocument seq;
	private List<String> msgList = new ArrayList<String>();
	private List<String> msgUitvalList = new ArrayList<String>();
	private List<String> msgMatchList = new ArrayList<String>();
	private List<String> verwerkingCnt = new ArrayList<String>();
	private List<String> verwerkList = new ArrayList<String>();
	private static final Logger logger = LoggerFactory
			.getLogger(LimsReadDataFromExcel.class);

	public int importCounter;
	private int importTotal;
	private String[] record = null;
	private final String noteCode = "DocumentNoteUtilities-Registrationnumber";
	private final String fieldName = "BasisOfRecordCode";
	private boolean match = false;
	private String registrationNumber;

	String logFileName = limsImporterUtil.getLogPath() + File.separator + "CRS"
			+ limsImporterUtil.getLogFilename();

	LimsLogger limsLogger = new LimsLogger(logFileName);

	@Override
	public void actionPerformed(
			AnnotatedPluginDocument[] annotatedPluginDocuments) {
		logger.info("Start updating selected document(s) with CRS data.");

		if (annotatedPluginDocuments[0] != null) {
			try {
				/** Add selected documents to a list. */
				docs = DocumentUtilities.getSelectedDocuments();
				String fileSelected = fcd.loadSelectedFile();
				if (fileSelected == null) {
					return;
				}

				msgUitvalList.add("Filename: " + fileSelected + "\n");

				logger.info("-------------------------- S T A R T --------------------------");
				logger.info("Start Reading data from a CRS file.");

				for (int cnt = 0; cnt < docs.size(); cnt++) {
					seq = (SequenceDocument) docs.get(cnt).getDocument();

					readDataFromCRSFile(annotatedPluginDocuments[cnt],
							fileSelected, cnt);

					if (matchRegistrationNumber(annotatedPluginDocuments[cnt],
							registrationNumber)) {
						System.out.println("Registration number matched: "
								+ registrationNumber);

						/* Add sequence name for the dialog screen */
						msgList.add(seq.getName() + "\n");

						/* set note for Phylum */
						limsNotes.setNoteToAB1FileName(
								annotatedPluginDocuments, "PhylumCode",
								"Phylum", "Phylum", LimsCRSFields.getPhylum(),
								cnt);

						/* Set note for Rank or Classification */
						limsNotes.setNoteToAB1FileName(
								annotatedPluginDocuments, "ClassificationCode",
								"Classification", "Classification",
								LimsCRSFields.getClassification(), cnt);

						/* set note for Orde */
						limsNotes.setNoteToAB1FileName(
								annotatedPluginDocuments, "OrdeCode", "Orde",
								"Orde", LimsCRSFields.getOrde(), cnt);

						/* set note for Family */
						limsNotes.setNoteToAB1FileName(
								annotatedPluginDocuments, "FamilyCode",
								"Family", "Family", LimsCRSFields.getFamily(),
								cnt);

						/* set note for SubFamily */
						limsNotes.setNoteToAB1FileName(
								annotatedPluginDocuments, "SubFamilyCode",
								"SubFamily", "SubFamily",
								LimsCRSFields.getSubFamily(), cnt);

						/* set note for Genus */
						limsNotes.setNoteToAB1FileName(
								annotatedPluginDocuments, "GenusCode", "Genus",
								"Genus", LimsCRSFields.getGenus(), cnt);

						/* set note for TaxonName */
						limsNotes.setNoteToAB1FileName(
								annotatedPluginDocuments, "TaxonNaamCode",
								"Taxon naam", "Taxon naam",
								LimsCRSFields.getTaxon(), cnt);

						/* set note for Determination */
						limsNotes.setNoteToAB1FileName(
								annotatedPluginDocuments, "DeterminatorCode",
								"Determinator", "Determinator",
								LimsCRSFields.getDeterminator(), cnt);

						/* set note for Sex */
						limsNotes.setNoteToAB1FileName(
								annotatedPluginDocuments, "SexCode", "Sex",
								"Sex", LimsCRSFields.getSex(), cnt);

						/* set note for Stadium */
						limsNotes.setNoteToAB1FileName(
								annotatedPluginDocuments, "StadiumCode",
								"Stadium", "Stadium",
								LimsCRSFields.getStadium(), cnt);

						/* set note for Legavit */
						limsNotes.setNoteToAB1FileName(
								annotatedPluginDocuments, "LegavitCode",
								"Legavit", "Legavit",
								LimsCRSFields.getLegavit(), cnt);

						/* set note for CollectingDate */
						limsNotes.setNoteToAB1FileName(
								annotatedPluginDocuments, "CollectingDateCode",
								"CollectingDate", "CollectingDate",
								LimsCRSFields.getCollectingDate(), cnt);

						/* set note for Country */
						limsNotes.setNoteToAB1FileName(
								annotatedPluginDocuments, "CountryCode",
								"Country", "Country",
								LimsCRSFields.getCountry(), cnt);

						/* set note for StateOrProvince */
						limsNotes.setNoteToAB1FileName(
								annotatedPluginDocuments,
								"StateOrProvinceCode", "StateOrProvince",
								"StateOrProvince",
								LimsCRSFields.getStateOrProvince(), cnt);

						/* set note for Location */
						limsNotes.setNoteToAB1FileName(
								annotatedPluginDocuments, "LocationCode",
								"Location", "Location",
								LimsCRSFields.getLocation(), cnt);

						/* set note for LatitudeDecimal */
						limsNotes.setNoteToAB1FileName(
								annotatedPluginDocuments,
								"LatitudeDecimalCode", "LatitudeDecimal",
								"LatitudeDecimal",
								LimsCRSFields.getLatitudeDecimal(), cnt);

						/* set note for LongitudeDecimal */
						limsNotes.setNoteToAB1FileName(
								annotatedPluginDocuments,
								"LongitudeDecimalCode", "LongitudeDecimal",
								"LongitudeDecimal",
								LimsCRSFields.getLongitudeDecimal(), cnt);

						/* set note for Height */
						limsNotes.setNoteToAB1FileName(
								annotatedPluginDocuments, "HeightCode",
								"Height", "Height", LimsCRSFields.getHeight(),
								cnt);

						logger.info("Done with adding notes to the document");
						importCounter = msgList.size();

						logger.info("--------------------------------------------------------");
						logger.info("Total of document(s) updated: "
								+ importCounter);
						logger.info("-------------------------- E N D --------------------------");
						logger.info("Done with updating the selected document(s). ");

						EventQueue.invokeLater(new Runnable() {

							@Override
							public void run() {
								Dialogs.showMessageDialog("CRS: "
										+ Integer.toString(msgList.size())
										+ " out of "
										+ Integer.toString(importTotal)
										+ " documents are imported." + "\n"
										+ msgList.toString());
								logger.info("CRS: Total imported document(s): "
										+ msgList.toString());

								limsLogger.logToFile(logFileName,
										msgUitvalList.toString());

								msgList.clear();
								msgUitvalList.clear();
								verwerkingCnt.clear();
								verwerkList.clear();
								match = false;
							}
						});
					} else {
						msgUitvalList.add("No document(s) match found for : "
								+ seq.getName() + "\n");
						msgMatchList.add("No document(s) match found for : "
								+ seq.getName());
					}
				}
				int rest = importTotal - verwerkList.size();
				msgUitvalList.add("Total records not matched: "
						+ Integer.toString(rest) + "\n");
			} catch (DocumentOperationException e) {
				e.printStackTrace();
			}
			/*
			 * logger.info("No document(s) match found for : " +
			 * msgMatchList.toString());
			 */
			/*
			 * if (match) { logger.info(
			 * "--------------------------------------------------------");
			 * logger.info("Total of document(s) updated: " + docs.size()); }
			 */
		}

		/*
		 * if (match) {
		 * logger.info("-------------------------- E N D --------------------------"
		 * ); logger.info("Done with updating the selected document(s). ");
		 * 
		 * EventQueue.invokeLater(new Runnable() {
		 * 
		 * @Override public void run() { Dialogs.showMessageDialog("CRS: " +
		 * Integer.toString(docs.size()) + " out of " +
		 * Integer.toString(importTotal) + " documents are imported." + "\n" +
		 * msgList.toString()); logger.info("CRS: Total imported document(s): "
		 * + msgList.toString());
		 * 
		 * limsLogger.logToFile(logFileName, msgUitvalList.toString());
		 * 
		 * msgList.clear(); msgUitvalList.clear(); verwerkingCnt.clear();
		 * verwerkList.clear(); } }); }
		 */

	}

	@Override
	public GeneiousActionOptions getActionOptions() {
		return new GeneiousActionOptions("CRS").setInMainToolbar(true);
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
				msgUitvalList
						.add("-----------------------------------------------"
								+ "\n");

				while ((record = csvReader.readNext()) != null) {
					if (record.length == 0) {
						continue;
					}

					registrationNumber = record[0];

					if (matchRegistrationNumber(annotatedPluginDocument,
							record[0])) {

						match = true;

						LimsCRSFields.setRegistratienummer(record[0]);
						LimsCRSFields.setPhylum(record[1]);
						LimsCRSFields.setClassification(record[2]);
						LimsCRSFields.setOrde(record[3]);
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
						LimsCRSFields.setStateOrProvince(record[14]);
						LimsCRSFields.setLocation(record[15]);
						LimsCRSFields.setLatitudeDecimal(record[16]);
						LimsCRSFields.setLongitudeDecimal(record[17]);
						LimsCRSFields.setHeight(record[18]);

						logger.info("Start with adding notes to the document");
						logger.info("CollectionRegistrationNumber: "
								+ LimsCRSFields.getRegistratienummer());
						logger.info("Phylum: " + LimsCRSFields.getPhylum());
						logger.info("Classification: "
								+ LimsCRSFields.getClassification());
						logger.info("Orde: " + LimsCRSFields.getOrde());
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
						logger.info("State or Province: "
								+ LimsCRSFields.getStateOrProvince());
						logger.info("Location: " + LimsCRSFields.getLocation());
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
}
