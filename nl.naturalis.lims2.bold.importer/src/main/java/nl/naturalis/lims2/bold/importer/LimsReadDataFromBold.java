/**
 * 
 */
package nl.naturalis.lims2.bold.importer;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import nl.naturalis.lims2.excel.importer.LimsNotes;
import nl.naturalis.lims2.utils.LimsImporterUtil;
import nl.naturalis.lims2.utils.LimsReadGeneiousFieldsValues;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class LimsReadDataFromBold extends DocumentAction {

	static final Logger logger = LoggerFactory
			.getLogger(LimsReadDataFromBold.class);
	LimsNotes limsNotes = new LimsNotes();
	LimsImporterUtil limsImporterUtil = new LimsImporterUtil();
	LimsBoldFields limsBoldFields = new LimsBoldFields();
	LimsReadGeneiousFieldsValues readGeneiousFieldsValues = new LimsReadGeneiousFieldsValues();
	SequenceDocument seq;

	private String boldFilePath;
	private String boldFile;
	private String extractIDfileName;
	private final String noteCode = "DocumentNoteUtilities-Registrationnumber";
	private final String fieldName = "BasisOfRecordCode";
	private List<AnnotatedPluginDocument> docs;

	@Override
	public void actionPerformed(
			AnnotatedPluginDocument[] annotatedPluginDocuments) {
		logger.info("-----------------------------------------------------------------");
		logger.info("Start adding Bold metadata to AB1 File(s)");

		if (annotatedPluginDocuments[0] != null) {

			try {
				docs = DocumentUtilities.getSelectedDocuments();
				for (int cnt = 0; cnt < docs.size(); cnt++) {

					seq = (SequenceDocument) docs.get(cnt).getDocument();
					logger.info("Selected document: " + seq.getName());
					setExtractIDfileName(seq.getName());
					extractIDfileName = getExtractIDFromAB1FileName(seq
							.getName());
					readDataFromBold(annotatedPluginDocuments[cnt]);

					/*
					 * setNoteToAB1FileName(AnnotatedPluginDocument[]
					 * annotatedPluginDocuments, String fieldCode, String
					 * textNoteField, String noteTypeCode, String fieldValue)
					 */

					/* set note for Col.Registratie code */
					limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
							"ColRegistratieCode", "Col Registratie code",
							"Col Registratie code",
							limsBoldFields.getColRegistratiecode(), cnt);

					/* set note for BOLD-ID */
					limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
							"BOLDIDCode", "BOLD-ID", "BOLD-ID",
							limsBoldFields.getBoldID(), cnt);

					/* Set note for Marker */
					limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
							"MarkerCode", "Marker", "Marker",
							limsBoldFields.getMarker(), cnt);

					/* set note for TraceFile Presence */
					limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
							"TraceFilePresenceCode", "TraceFile Presence",
							"TraceFile Presence",
							limsBoldFields.getTraceFilePresence(), cnt);

					/* set note for Nucleotide Length */
					limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
							"NucleotideLengthCode", "Nucleotide Length",
							"Nucleotide Length",
							limsBoldFields.getNucleotideLength(), cnt);

					/* set note for GenBankID */
					limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
							"GenBankIDCode", "GenBank-ID", "GenBank-ID",
							limsBoldFields.getGenBankID(), cnt);

				}
			} catch (DocumentOperationException e) {
				e.printStackTrace();
			}
			logger.info("Total of document(s) updated: " + docs.size());
		}
		logger.info("-----------------------------------------------------------------");
		logger.info("Done with reading bold file. ");

	}

	@Override
	public GeneiousActionOptions getActionOptions() {
		return new GeneiousActionOptions("Update file with data from Bold")
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

	private void readDataFromBold(
			AnnotatedPluginDocument annotatedPluginDocument) {
		try {
			setBoldFile(limsImporterUtil.getFileFromPropertieFile("bold"));
			setBoldFilePath(limsImporterUtil.getPropValues() + getBoldFile());
			logger.info("CSV file: " + getBoldFilePath());
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			CSVReader csvReader = new CSVReader(new FileReader(
					getBoldFilePath()), '\t', '\'', 0);

			String[] record = null;
			csvReader.readNext();

			try {
				while ((record = csvReader.readNext()) != null) {
					if (record.length == 0) {
						continue;
					}

					String ID = "e" + record[3];

					/** DocumentNoteUtilities-Registrationnumber */
					/** Get value from "BasisOfRecordCode" */
					Object fieldValue = readGeneiousFieldsValues
							.readValueFromAnnotatedPluginDocument(
									annotatedPluginDocument, noteCode,
									fieldName);

					// if (ID.equals(getExtractIDfileName()))
					if (record[5].equals(fieldValue)) {

						logger.info("Registrationnumber "
								+ record[5]
								+ " from the Bold file is equal to the fieldvalue: "
								+ fieldValue + " from the AB1 file.");

						limsBoldFields.setMarker(record[1]);
						limsBoldFields.setBoldID(record[4]);
						limsBoldFields.setColRegistratiecode(record[5]);
						limsBoldFields.setNucleotideLength(record[6]);
						limsBoldFields.setTraceFilePresence(record[7]);
						limsBoldFields.setGenBankID(record[15]);

						logger.info("Bold-ID: " + limsBoldFields.getBoldID());
						logger.info("Col.Registratiecode: "
								+ limsBoldFields.getColRegistratiecode());
						logger.info("GenBankID: "
								+ limsBoldFields.getGenBankID());
						logger.info("Marker: " + limsBoldFields.getMarker());
						logger.info("Nucleotide: "
								+ limsBoldFields.getNucleotideLength());
						logger.info("TraceFile Presence: "
								+ limsBoldFields.getTraceFilePresence());

						logger.info("Done with adding notes to the document");

					} // end IF
				} // end While
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

	public String getBoldFilePath() {
		return boldFilePath;
	}

	public void setBoldFilePath(String boldFilePath) {
		this.boldFilePath = boldFilePath;
	}

	public String getBoldFile() {
		return boldFile;
	}

	public void setBoldFile(String boldFile) {
		this.boldFile = boldFile;
	}

	public String getExtractIDfileName() {
		return extractIDfileName;
	}

	public void setExtractIDfileName(String extractIDfileName) {
		this.extractIDfileName = extractIDfileName;
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
		String[] underscore = StringUtils.split(fileName, "_");
		return underscore[0];
	}

}
