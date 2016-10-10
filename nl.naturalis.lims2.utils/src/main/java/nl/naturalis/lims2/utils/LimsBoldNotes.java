package nl.naturalis.lims2.utils;

import java.util.ArrayList;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument.DocumentNotes;
import com.biomatters.geneious.publicapi.documents.Constraint;
import com.biomatters.geneious.publicapi.documents.DocumentNote;
import com.biomatters.geneious.publicapi.documents.DocumentNoteField;
import com.biomatters.geneious.publicapi.documents.DocumentNoteType;
import com.biomatters.geneious.publicapi.documents.DocumentNoteUtilities;

public class LimsBoldNotes {
	private static final Logger logger = LoggerFactory
			.getLogger(LimsBoldNotes.class);

	private ArrayList<DocumentNoteField> listNotes = new ArrayList<DocumentNoteField>();

	/* =================== RegistrationMarker ===================== */
	/* TraceFile Presence */
	private String fieldTraceFile;
	private String noteTypeCodeTraceFile;
	private String descriptionTraceFile;
	private String noteTextTraceFile = "N traces (Bold)";

	/* Nucleotide Length */
	private String fieldNucleotide;
	private String noteTypeCodeNucleotide;
	private String descriptionNucleotide;
	private String noteTextNucleotide = "Nucl-length (Bold)";

	/* GenBankID */
	private String fieldGenBankID;
	private String noteTypeCodeGenBankID;
	private String descriptionGenBankID;
	private String noteTextGenBankID = "GenBank ID (Bold)";

	/* GenBank URI */
	private String fieldGenBankURI;
	private String noteTypeCodeGenBankURI;
	private String descriptionGenBankURI;
	private String noteTextGenBankURI = "GenBank URI (Bold)";

	/* ============= Registration ===================== */

	/* BOLD-ID */
	private String fieldBoldID;
	private String noteTypeCodeBoldID;
	private String descriptionBoldID;
	private String noteTextBoldID = "BOLD ID (Bold)";

	/* Number of Images */
	private String fieldNumberOfImages;
	private String noteTypeCodeNumberOfImages;
	private String descriptionNumberOfImages;
	private String noteTextNumberOfImages = "N images (Bold)";

	/* BoldProjectID */
	private String fieldBoldProjectID;
	private String noteTypeCodeBoldProjectID;
	private String descriptionBoldProjectID;
	private String noteTextBoldProjectID = "BOLD proj-ID (Bold)";

	/* FieldID */
	private String fieldFieldID;
	private String noteTypeCodeFieldID;
	private String descriptionFieldID;
	private String noteTextFieldID = "Field ID (Bold)";

	/* BOLD BIN Code */
	private String fieldBoldBin;
	private String noteTypeCodeBoldBin;
	private String descriptionBoldBin;
	private String noteTextBoldBin = "BOLD BIN (Bold)";

	/* BOLD URI */
	private String fieldBoldURI;
	private String noteTypeCodeBoldURI;
	private String descriptionBoldURI;
	private String noteTextBoldURI = "BOLD URI (Bold)";

	public void setNotesToBoldDocumentsRegistration(
			AnnotatedPluginDocument[] annotatedPluginDocuments, int cnt,
			String boldID, String numberOfImages, String boldProjectID,
			String fieldID, String boldBin, String boldURI) {

		setFieldAndDescriptionValuesForRegistration();

		/* ======================================================== */
		addNotesToListNotesForRegistration();

		/* ======================================================== */
		/* BOLD-ID */
		this.noteTypeCodeBoldID = "DocumentNoteUtilities-" + noteTextBoldID;
		DocumentNoteType documentNoteTypeBoldID = DocumentNoteUtilities
				.getNoteType(this.noteTypeCodeBoldID);

		/* Number of Images */
		this.noteTypeCodeNumberOfImages = "DocumentNoteUtilities-"
				+ noteTextNumberOfImages;
		DocumentNoteType documentNoteTypeNumberOfImages = DocumentNoteUtilities
				.getNoteType(this.noteTypeCodeNumberOfImages);

		/* BoldProjectID */
		this.noteTypeCodeBoldProjectID = "DocumentNoteUtilities-"
				+ noteTextBoldProjectID;
		DocumentNoteType documentNoteTypeBoldProjectID = DocumentNoteUtilities
				.getNoteType(this.noteTypeCodeBoldProjectID);

		/* FieldID */
		this.noteTypeCodeFieldID = "DocumentNoteUtilities-" + noteTextFieldID;
		DocumentNoteType documentNoteTypeFieldID = DocumentNoteUtilities
				.getNoteType(this.noteTypeCodeFieldID);

		/* BOLD BIN Code */
		this.noteTypeCodeBoldBin = "DocumentNoteUtilities-" + noteTextBoldBin;
		DocumentNoteType documentNoteTypeBoldBin = DocumentNoteUtilities
				.getNoteType(this.noteTypeCodeBoldBin);

		/* BOLD URI */
		this.noteTypeCodeBoldURI = "DocumentNoteUtilities-" + noteTextBoldURI;
		DocumentNoteType documentNoteTypeBoldURI = DocumentNoteUtilities
				.getNoteType(this.noteTypeCodeBoldURI);

		/* ======================================================== */
		/* BOLD-ID */
		if (documentNoteTypeBoldID == null) {
			documentNoteTypeBoldID = DocumentNoteUtilities.createNewNoteType(
					noteTextBoldID, this.noteTypeCodeBoldID,
					this.descriptionBoldID, listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypeBoldID);
			logger.info("NoteType " + noteTextBoldID + " created succesful");
		}

		/* Number of Images */
		if (documentNoteTypeNumberOfImages == null) {
			documentNoteTypeNumberOfImages = DocumentNoteUtilities
					.createNewNoteType(noteTextNumberOfImages,
							this.noteTypeCodeNumberOfImages,
							this.descriptionNumberOfImages, listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypeNumberOfImages);
			logger.info("NoteType " + noteTextNumberOfImages
					+ " created succesful");
		}

		/* BoldProjectID */
		if (documentNoteTypeBoldProjectID == null) {
			documentNoteTypeBoldProjectID = DocumentNoteUtilities
					.createNewNoteType(noteTextBoldProjectID,
							this.noteTypeCodeBoldProjectID,
							this.descriptionBoldProjectID, listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypeBoldProjectID);
			logger.info("NoteType " + noteTextBoldProjectID
					+ " created succesful");
		}

		/* FieldID */
		if (documentNoteTypeFieldID == null) {
			documentNoteTypeFieldID = DocumentNoteUtilities.createNewNoteType(
					noteTextFieldID, this.noteTypeCodeFieldID,
					this.descriptionFieldID, listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypeFieldID);
			logger.info("NoteType " + noteTextFieldID + " created succesful");
		}

		/* BOLD BIN Code */
		if (documentNoteTypeBoldBin == null) {
			documentNoteTypeBoldBin = DocumentNoteUtilities.createNewNoteType(
					noteTextBoldBin, this.noteTypeCodeBoldBin,
					this.descriptionBoldBin, listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypeBoldBin);
			logger.info("NoteType " + noteTextBoldBin + " created succesful");
		}

		/* BOLD URI */
		if (documentNoteTypeBoldURI == null) {
			documentNoteTypeBoldURI = DocumentNoteUtilities.createNewNoteType(
					noteTextBoldURI, this.noteTypeCodeBoldURI,
					this.descriptionBoldURI, listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypeBoldURI);
			logger.info("NoteType " + noteTextBoldURI + " created succesful");
		}

		/* ========================================================= */

		/* BOLD-ID */
		DocumentNote documentNoteBoldID = documentNoteTypeBoldID
				.createDocumentNote();
		documentNoteBoldID.setFieldValue(this.fieldBoldID, boldID);
		logger.info("Note value " + this.fieldBoldID + ": " + boldID
				+ " added succesful");

		/* Number of Images */
		DocumentNote documentNoteNumberOfImages = documentNoteTypeNumberOfImages
				.createDocumentNote();
		documentNoteNumberOfImages.setFieldValue(this.fieldNumberOfImages,
				numberOfImages);
		logger.info("Note value " + this.fieldNumberOfImages + ": "
				+ numberOfImages + " added succesful");

		/* BoldProjectID */
		DocumentNote documentNoteBoldProjectID = documentNoteTypeBoldProjectID
				.createDocumentNote();
		documentNoteBoldProjectID.setFieldValue(this.fieldBoldProjectID,
				boldProjectID);
		logger.info("Note value " + this.fieldBoldProjectID + ": "
				+ boldProjectID + " added succesful");

		/* FieldID */
		DocumentNote documentNoteFieldID = documentNoteTypeFieldID
				.createDocumentNote();
		documentNoteFieldID.setFieldValue(this.fieldFieldID, fieldID);
		logger.info("Note value " + this.fieldFieldID + ": " + fieldID
				+ " added succesful");

		/* BOLD BIN Code */
		DocumentNote documentNoteBoldBin = documentNoteTypeBoldBin
				.createDocumentNote();
		documentNoteBoldBin.setFieldValue(this.fieldBoldBin, boldBin);
		logger.info("Note value " + this.fieldBoldBin + ": " + boldBin
				+ " added succesful");

		/* BOLD URI */
		DocumentNote documentNoteBoldURI = documentNoteTypeBoldURI
				.createDocumentNote();
		documentNoteBoldURI.setFieldValue(this.fieldBoldURI, boldURI);
		logger.info("Note value " + this.fieldBoldURI + ": " + boldURI
				+ " added succesful");

		/* ======================================================== */

		if (documentNoteTypeBoldProjectID.getName().equals(
				"BOLD proj-ID (Bold)")
				|| documentNoteTypeFieldID.getName().equals("Field ID (Bold)")
				|| documentNoteTypeBoldBin.getName().equals("BOLD BIN (Bold)")) {
			documentNoteTypeBoldProjectID.setDefaultVisibleInTable(false);
			documentNoteTypeFieldID.setDefaultVisibleInTable(false);
			documentNoteTypeBoldBin.setDefaultVisibleInTable(false);
		}

		/* ======================================================== */
		AnnotatedPluginDocument.DocumentNotes documentNotes = (DocumentNotes) annotatedPluginDocuments[cnt]
				.getDocumentNotes(true);

		/* Set note */
		documentNotes.setNote(documentNoteBoldID);
		documentNotes.setNote(documentNoteNumberOfImages);
		documentNotes.setNote(documentNoteBoldProjectID);
		documentNotes.setNote(documentNoteFieldID);
		documentNotes.setNote(documentNoteBoldBin);
		documentNotes.setNote(documentNoteBoldURI);

		/* Save the selected sequence document */
		documentNotes.saveNotes();

		logger.info("Notes added succesful");

		if (listNotes != null) {
			listNotes.clear();
		}
	}

	/**
	 * 
	 */
	private void addNotesToListNotesForRegistration() {
		/* BOLD-ID */
		listNotes.add(DocumentNoteField.createTextNoteField(noteTextBoldID,
				this.descriptionBoldID, this.fieldBoldID,
				Collections.<Constraint> emptyList(), false));

		/* Number of Images */
		listNotes.add(DocumentNoteField.createTextNoteField(
				noteTextNumberOfImages, this.descriptionNumberOfImages,
				this.fieldNumberOfImages, Collections.<Constraint> emptyList(),
				false));

		/* BoldProjectID */
		listNotes.add(DocumentNoteField.createTextNoteField(
				noteTextBoldProjectID, this.descriptionBoldProjectID,
				this.fieldBoldProjectID, Collections.<Constraint> emptyList(),
				false));

		/* FieldID */
		listNotes.add(DocumentNoteField.createTextNoteField(noteTextFieldID,
				this.descriptionFieldID, this.fieldFieldID,
				Collections.<Constraint> emptyList(), false));

		/* BOLD BIN Code */
		listNotes.add(DocumentNoteField.createTextNoteField(noteTextBoldBin,
				this.descriptionBoldBin, this.fieldBoldBin,
				Collections.<Constraint> emptyList(), false));

		/* BOLD URI */
		listNotes.add(DocumentNoteField.createTextNoteField(noteTextBoldURI,
				this.descriptionBoldURI, this.fieldBoldURI,
				Collections.<Constraint> emptyList(), false));
	}

	/**
	 * 
	 */
	private void setFieldAndDescriptionValuesForRegistration() {
		/* BOLD-ID */
		this.fieldBoldID = "BOLDIDCode_Bold";
		this.descriptionBoldID = "Naturalis file " + noteTextBoldID + " note";

		/* Number of Images */
		this.fieldNumberOfImages = "NumberOfImagesCode_Bold";
		this.descriptionNumberOfImages = "Naturalis file "
				+ noteTextNumberOfImages + " note";

		/* BoldProjectID */
		this.fieldBoldProjectID = "BOLDprojIDCode_Bold";
		this.descriptionBoldProjectID = "Naturalis file "
				+ noteTextBoldProjectID + " note";

		/* FieldID */
		this.fieldFieldID = "FieldIDCode_Bold";
		this.descriptionFieldID = "Naturalis file " + noteTextFieldID + " note";

		/* BOLD BIN Code */
		this.fieldBoldBin = "BOLDBINCode_Bold";
		this.descriptionBoldBin = "Naturalis file " + noteTextBoldBin + " note";

		/* BOLD URI */
		this.fieldBoldURI = "BOLDURICode_FixedValue_Bold";
		this.descriptionBoldURI = "Naturalis file " + noteTextBoldURI + " note";
	}

	/* =================== Registration Marker ============== */

	public void setNotesToBoldDocumentsRegistrationMarker(
			AnnotatedPluginDocument[] annotatedPluginDocuments, int cnt,
			String traceFilePresence, String nucleotideLength,
			String genBankID, String genBankURI) {

		setFieldAndDescriptionValuesForRegistrationMarker();

		/* ========================================================= */

		addNotesToListNotesRegistrationMarker();

		/* ========================================================= */
		/* TraceFile Presence */
		this.noteTypeCodeTraceFile = "DocumentNoteUtilities-"
				+ noteTextTraceFile;
		DocumentNoteType documentNoteTypeTraceFile = DocumentNoteUtilities
				.getNoteType(this.noteTypeCodeTraceFile);

		/* Nucleotide Length */
		this.noteTypeCodeNucleotide = "DocumentNoteUtilities-"
				+ noteTextNucleotide;
		DocumentNoteType documentNoteTypeNucleotide = DocumentNoteUtilities
				.getNoteType(this.noteTypeCodeNucleotide);

		/* GenBankID */
		this.noteTypeCodeGenBankID = "DocumentNoteUtilities-"
				+ noteTextGenBankID;
		DocumentNoteType documentNoteTypeGenBankID = DocumentNoteUtilities
				.getNoteType(this.noteTypeCodeGenBankID);

		/* GenBank URI */
		this.noteTypeCodeGenBankURI = "DocumentNoteUtilities-"
				+ noteTextGenBankURI;
		DocumentNoteType documentNoteTypeGenBankURI = DocumentNoteUtilities
				.getNoteType(this.noteTypeCodeGenBankURI);

		/* ========================================================= */
		/* TraceFile Presence */
		if (documentNoteTypeTraceFile == null) {
			documentNoteTypeTraceFile = DocumentNoteUtilities
					.createNewNoteType(noteTextTraceFile,
							this.noteTypeCodeTraceFile,
							this.descriptionTraceFile, listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypeTraceFile);
			logger.info("NoteType " + noteTextTraceFile + " created succesful");
		}

		/* Nucleotide Length */
		if (documentNoteTypeNucleotide == null) {
			documentNoteTypeNucleotide = DocumentNoteUtilities
					.createNewNoteType(noteTextNucleotide,
							this.noteTypeCodeNucleotide,
							this.descriptionNucleotide, listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypeNucleotide);
			logger.info("NoteType " + noteTextNucleotide + " created succesful");
		}

		/* GenBankID */
		if (documentNoteTypeGenBankID == null) {
			documentNoteTypeGenBankID = DocumentNoteUtilities
					.createNewNoteType(noteTextGenBankID,
							this.noteTypeCodeGenBankID,
							this.descriptionGenBankID, listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypeGenBankID);
			logger.info("NoteType " + noteTextGenBankID + " created succesful");
		}

		/* GenBank URI */
		if (documentNoteTypeGenBankURI == null) {
			documentNoteTypeGenBankURI = DocumentNoteUtilities
					.createNewNoteType(noteTextGenBankURI,
							this.noteTypeCodeGenBankURI,
							this.descriptionGenBankURI, listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypeGenBankURI);
			logger.info("NoteType " + noteTextGenBankURI + " created succesful");
		}

		/* ========================================================= */

		/* Create note for TraceFile Presence */
		DocumentNote documentNoteTraceFile = documentNoteTypeTraceFile
				.createDocumentNote();
		documentNoteTraceFile.setFieldValue(this.fieldTraceFile,
				traceFilePresence);
		logger.info("Note value " + this.fieldTraceFile + ": "
				+ traceFilePresence + " added succesful");

		/* Nucleotide Length */
		DocumentNote documentNoteNucleotide = documentNoteTypeNucleotide
				.createDocumentNote();
		documentNoteNucleotide.setFieldValue(this.fieldNucleotide,
				nucleotideLength);
		logger.info("Note value " + this.fieldNucleotide + ": "
				+ nucleotideLength + " added succesful");

		/* GenBankID */
		DocumentNote documentNoteGenBankID = documentNoteTypeGenBankID
				.createDocumentNote();
		documentNoteGenBankID.setFieldValue(this.fieldGenBankID, genBankID);
		logger.info("Note value " + this.fieldGenBankID + ": " + genBankID
				+ " added succesful");

		/* GenBank URI */
		DocumentNote documentNoteGenBankURI = documentNoteTypeGenBankURI
				.createDocumentNote();
		documentNoteGenBankURI.setFieldValue(this.fieldGenBankURI, genBankURI);
		logger.info("Note value " + this.fieldGenBankURI + ": " + genBankURI
				+ " added succesful");

		/* ========================================================== */
		if (documentNoteTypeNucleotide.getName().equals(
				"Nucleotide length (Bold)")
				|| documentNoteTypeGenBankID.getName().equals(
						"GenBank ID (Bold)")) {
			documentNoteTypeNucleotide.setDefaultVisibleInTable(false);
			documentNoteTypeGenBankID.setDefaultVisibleInTable(false);
		}

		/* ========================================================== */

		AnnotatedPluginDocument.DocumentNotes documentNotes = (DocumentNotes) annotatedPluginDocuments[cnt]
				.getDocumentNotes(true);

		/* Set note */
		documentNotes.setNote(documentNoteTraceFile);
		documentNotes.setNote(documentNoteNucleotide);
		documentNotes.setNote(documentNoteGenBankID);
		documentNotes.setNote(documentNoteGenBankURI);

		/* Save the selected sequence document */
		documentNotes.saveNotes();

		logger.info("Notes added succesful");

		if (listNotes != null) {
			listNotes.clear();
		}

	}

	/**
	 * 
	 */
	private void addNotesToListNotesRegistrationMarker() {
		/* TraceFile Presence */
		listNotes.add(DocumentNoteField.createTextNoteField(noteTextTraceFile,
				this.descriptionTraceFile, this.fieldTraceFile,
				Collections.<Constraint> emptyList(), false));

		/* Nucleotide Length */
		listNotes.add(DocumentNoteField.createTextNoteField(noteTextNucleotide,
				this.descriptionNucleotide, this.fieldNucleotide,
				Collections.<Constraint> emptyList(), false));

		/* GenBankID */
		listNotes.add(DocumentNoteField.createTextNoteField(noteTextGenBankID,
				this.descriptionGenBankID, this.fieldGenBankID,
				Collections.<Constraint> emptyList(), false));

		/* GenBank URI */
		listNotes.add(DocumentNoteField.createTextNoteField(noteTextGenBankURI,
				this.descriptionGenBankURI, this.fieldGenBankURI,
				Collections.<Constraint> emptyList(), false));
	}

	/**
	 * 
	 */
	private void setFieldAndDescriptionValuesForRegistrationMarker() {
		/* TraceFile Presence */
		this.fieldTraceFile = "TraceFilePresenceCode_Bold";
		this.descriptionTraceFile = "Naturalis file " + noteTextTraceFile
				+ " note";

		/* Nucleotide Length */
		this.fieldNucleotide = "NucleotideLengthCode_Bold";
		this.descriptionNucleotide = "Naturalis file " + noteTextNucleotide
				+ " note";

		/* GenBankID */
		this.fieldGenBankID = "GenBankIDCode_Bold";
		this.descriptionGenBankID = "Naturalis file " + noteTextGenBankID
				+ " note";

		/* GenBank URI */
		this.fieldGenBankURI = "GenBankURICode_FixedValue_Bold";
		this.descriptionGenBankURI = "Naturalis file " + noteTextGenBankURI
				+ " note";
	}

}
