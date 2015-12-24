/**
 * 
 */
package nl.naturalis.lims2.ab1.importer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import nl.naturalis.lims2.utils.LimsImporterUtil;
import nl.naturalis.lims2.utils.LimsLogger;
import nl.naturalis.lims2.utils.LimsNotes;

import org.apache.commons.lang3.StringUtils;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.documents.PluginDocument;
import com.biomatters.geneious.publicapi.documents.sequence.SequenceDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentAction;
import com.biomatters.geneious.publicapi.plugin.DocumentOperationException;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsReadDataFromExcel extends DocumentAction {

	private List<AnnotatedPluginDocument> docs;
	LimsImporterUtil limsImporterUtil = new LimsImporterUtil();
	LimsExcelFields limsExcelFields = new LimsExcelFields();
	LimsNotes limsNotes = new LimsNotes();
	LimsFileSelector fcd = new LimsFileSelector();

	private String extractIDfileName = "";
	private SequenceDocument seq;
	// private Options options;

	String logFileName = limsImporterUtil.getLogPath() + File.separator
			+ limsImporterUtil.getLogFilename();

	LimsLogger limsLogger = new LimsLogger(logFileName);

	@Override
	public void actionPerformed(
			AnnotatedPluginDocument[] annotatedPluginDocuments) {

		limsLogger.logMessage("Start updating selected document(s).");

		if (annotatedPluginDocuments[0] != null) {
			try {
				/** Add selected documents to a list. */
				docs = DocumentUtilities.getSelectedDocuments();
				for (int cnt = 0; cnt < docs.size(); cnt++) {

					limsLogger
							.logMessage("-------------------------- S T A R T --------------------------");
					limsLogger
							.logMessage("Start Reading data from a excel file.");

					seq = (SequenceDocument) docs.get(cnt).getDocument();
					extractIDfileName = getExtractIDFromAB1FileName(seq
							.getName());

					readDataFromExcel(annotatedPluginDocuments,
							fcd.loadSelectedFile());

					/*
					 * EventQueue.invokeLater(new Runnable() {
					 * 
					 * @Override public void run() { options = new
					 * LimsOptions(); Dialogs.showOptionsDialog(options,
					 * "Select a file", true);
					 * 
					 * readDataFromExcel(annotatedPluginDocuments, fcd.loadFile(
					 * new Frame(), "Open...", ".\\", "*.txt"));
					 * 
					 * } });
					 */

					/*
					 * setNoteToAB1FileName(AnnotatedPluginDocument[]
					 * annotatedPluginDocuments, String fieldCode, String
					 * textNoteField, String noteTypeCode, String fieldValue)
					 */

					/* set note for Extract-ID */
					limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
							"ExtractIdCode", "Extract ID", "Extract-ID",
							limsExcelFields.getExtractID(), cnt);

					/* set note for Project Plaatnummer */
					limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
							"ProjectPlaatnummerCode", "Project Plaatnummer",
							"Project Plaatnummer",
							limsExcelFields.getProjectPlaatNummer(), cnt);

					/* Set note for Extract Plaatnummer */
					limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
							"ExtractPlaatNummerCode", "Extract Plaatnummer",
							"Extract Plaatnummer",
							limsExcelFields.getExtractPlaatNummer(), cnt);

					/* set note for Taxonnaam */
					limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
							"TaxonNaamCode", "Taxon naam", "Taxon naam",
							limsExcelFields.getTaxonNaam(), cnt);

					/* set note for Registrationnumber */
					limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
							"BasisOfRecordCode", "Registrationnumber",
							"Registrationnumber",
							limsExcelFields.getRegistrationNumber(), cnt);

					/* set note for Plaat positie */
					limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
							"PlaatpositieCode", "Plaat positie",
							"Plaat positie", limsExcelFields.getPlaatPositie(),
							cnt);
					limsLogger
							.logMessage("Done with adding notes to the document");
				}
			} catch (DocumentOperationException e) {
				e.printStackTrace();
			}
			limsLogger
					.logMessage("--------------------------------------------------------");
			limsLogger.logMessage("Total of document(s) updated: "
					+ docs.size());
		}

		limsLogger
				.logMessage("-------------------------- E N D --------------------------");
		limsLogger.logMessage("Done with updating the selected document(s). ");

	}

	@Override
	public GeneiousActionOptions getActionOptions() {
		return new GeneiousActionOptions("Read data from Excel")
				.setInMainToolbar(true);
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

	private void readDataFromExcel(
			AnnotatedPluginDocument[] annotatedPluginDocuments, String fileName) {
		String csvPath = "";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = "\t";
		String[] record = null;

		try {
			// csvFile = limsImporterUtil.getFileFromPropertieFile("excel");
			csvPath = limsImporterUtil.getPropValues() + fileName;
			System.out.println("Path is: " + csvPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		limsLogger.logMessage("CSV file: " + csvPath);

		limsLogger.logMessage("Start with adding notes to the document");
		try {
			String ID = "e";
			br = new BufferedReader(new FileReader(csvPath));
			while ((line = br.readLine()) != null) {
				/*
				 * if (line.length() == 0) { continue; }
				 */

				// use comma as separator
				record = line.split(cvsSplitBy);

				if (record[3] != null) {
					ID = record[3];
					System.out.println("ID: " + record[3]);
				}

				if (record[3].equals(extractIDfileName)) {
					limsExcelFields.setProjectPlaatNummer(record[0]);
					limsExcelFields.setPlaatPositie(record[1]);
					limsExcelFields.setExtractPlaatNummer(record[2]);
					if (record[3] != null) {
						limsExcelFields.setExtractID(ID);
					}
					limsExcelFields.setRegistrationNumber(record[4]);
					limsExcelFields.setTaxonNaam(record[5]);
					// limsExcelFields.setSubSample(record[0]);

					limsLogger.logMessage("Extract-ID: "
							+ limsExcelFields.getExtractID());
					limsLogger.logMessage("Project plaatnummer: "
							+ limsExcelFields.getProjectPlaatNummer());
					limsLogger.logMessage("Extract plaatnummer: "
							+ limsExcelFields.getExtractPlaatNummer());
					limsLogger.logMessage("Taxon naam: "
							+ limsExcelFields.getTaxonNaam());
					limsLogger.logMessage("Registrationnumber: "
							+ limsExcelFields.getRegistrationNumber());
					limsLogger.logMessage("Plaat positie: "
							+ limsExcelFields.getPlaatPositie());

					/*
					 * CSVReader csvReader = new CSVReader( new
					 * FileReader(csvPath), '\t', '\'', 0);
					 * 
					 * csvReader.readNext();
					 */

					// limsLogger.logMessage("Start with adding notes to the document");
					// try {
					// while ((record = csvReader.readNext()) != null) {
					// if (record.length == 0) {
					// continue;
					// }
					//
					// String ID = "e" + record[3];
					//
					// if (ID.equals(extractIDfileName)) {
					// limsExcelFields.setProjectPlaatNummer(record[0]);
					// limsExcelFields.setPlaatPositie(record[1]);
					// limsExcelFields.setExtractPlaatNummer(record[2]);
					// if (record[3] != null) {
					// limsExcelFields.setExtractID(ID);
					// }
					// limsExcelFields.setRegistrationNumber(record[4]);
					// limsExcelFields.setTaxonNaam(record[5]);
					// // limsExcelFields.setSubSample(record[0]);
					//
					// limsLogger.logMessage("Extract-ID: "
					// + limsExcelFields.getExtractID());
					// limsLogger.logMessage("Project plaatnummer: "
					// + limsExcelFields.getProjectPlaatNummer());
					// limsLogger.logMessage("Extract plaatnummer: "
					// + limsExcelFields.getExtractPlaatNummer());
					// limsLogger.logMessage("Taxon naam: "
					// + limsExcelFields.getTaxonNaam());
					// limsLogger.logMessage("Registrationnumber: "
					// + limsExcelFields.getRegistrationNumber());
					// limsLogger.logMessage("Plaat positie: "
					// + limsExcelFields.getPlaatPositie());

				} // end IF
			} // end While
		} catch (IOException e) {
			e.printStackTrace();
		}
		// try {
		// csvReader.close();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
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
		limsLogger.logMessage("Document Filename: " + fileName);
		String[] underscore = StringUtils.split(fileName, "_");
		return underscore[0];
	}
}
