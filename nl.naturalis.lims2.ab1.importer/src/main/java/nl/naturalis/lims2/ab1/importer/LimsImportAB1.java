/**
 * 
 */
package nl.naturalis.lims2.ab1.importer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import jebl.util.ProgressListener;
import nl.naturalis.lims2.importer.LimsNotes;
import nl.naturalis.lims2.updater.LimsAB1Fields;

import org.apache.commons.lang3.StringUtils;
import org.biojava.bio.Annotation;
import org.biojava.bio.chromatogram.Chromatogram;
import org.biojava.bio.chromatogram.ChromatogramFactory;
import org.biojava.bio.chromatogram.ChromatogramTools;
import org.biojava.bio.chromatogram.UnsupportedChromatogramFormatException;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.impl.SimpleSequence;
import org.biojava.bio.symbol.SymbolList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument.DocumentNotes;
import com.biomatters.geneious.publicapi.documents.DocumentNote;
import com.biomatters.geneious.publicapi.documents.DocumentNoteField;
import com.biomatters.geneious.publicapi.documents.DocumentNoteType;
import com.biomatters.geneious.publicapi.documents.DocumentNoteUtilities;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.documents.sequence.NucleotideSequenceDocument;
import com.biomatters.geneious.publicapi.documents.sequence.SequenceDocument;
import com.biomatters.geneious.publicapi.implementations.sequence.DefaultNucleotideSequence;
import com.biomatters.geneious.publicapi.plugin.DocumentFileImporter;
import com.biomatters.geneious.publicapi.plugin.DocumentImportException;
import com.biomatters.geneious.publicapi.plugin.DocumentOperationException;
import com.biomatters.geneious.publicapi.plugin.PluginUtilities;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsImportAB1 extends DocumentFileImporter {

	static final Logger logger;
	private String fieldCode;
	private String description;
	private String noteTypeCode;
	SequenceDocument sequence;
	LimsAB1Fields limsAB1Flieds = new LimsAB1Fields();
	LimsNotes limsNotes = new LimsNotes();
	AnnotatedPluginDocument[] annotatedPluginDocuments;

	static {
		logger = LoggerFactory.getLogger(LimsImportAB1.class);
	}

	@Override
	public String getFileTypeDescription() {
		return "Naturalis Chromatogram AB1 Importer";
	}

	@Override
	public String[] getPermissibleExtensions() {
		return new String[] { ".ab1", ".ab", "geneious" };
	}

	@Override
	public void importDocuments(File file, ImportCallback importCallback,
			ProgressListener progressListener) throws IOException,
			DocumentImportException {

		String ab1File = file.getAbsolutePath();
		logger.info("ab1File: " + ab1File);
		String name = file.getName();
		String description = name;

		long fileSize = file.length();
		long count = 0;
		progressListener.setMessage("Importing sequence data");

		List<AnnotatedPluginDocument> docs = PluginUtilities.importDocuments(
				new File(ab1File), ProgressListener.EMPTY);

		docs = DocumentUtilities.getSelectedDocuments();
		for (int cnt = 0; cnt < docs.size(); cnt++) {
			try {
				sequence = (NucleotideSequenceDocument) docs.get(0)
						.getDocument();
			} catch (DocumentOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			logger.info("Selected document: " + sequence.getName());

			if (sequence.getName() != null) {
				setExtractIDFromAB1FileName(sequence.getName());
				logger.info("Extract-ID: " + limsAB1Flieds.getExtractID());
				logger.info("PCR plaat-ID: " + limsAB1Flieds.getPcrPlaatID());
				logger.info("Mark: " + limsAB1Flieds.getMarker());

				/** set note for Extract-ID */
				limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
						"ExtractIdCode", "Extract ID", "Extract-ID",
						limsAB1Flieds.getExtractID(), cnt);

				/** set note for PCR Plaat-ID */
				limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
						"PcrPlaatIdCode", "PCR plaat ID", "PCR plaat ID",
						limsAB1Flieds.getPcrPlaatID(), cnt);

				/** set note for Marker */
				limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
						"MarkerCode", "Marker", "Marker",
						limsAB1Flieds.getMarker(), cnt);

			}
			System.out.println("Sequence String  ="
					+ sequence.getSequenceString());
			System.out.println("Sequence String1 ="
					+ sequence.getCharSequence());
			System.out.println("Sequence String2 ="
					+ sequence.getSequenceAnnotations().toString());
			System.out.println("Sequence String3 ="
					+ sequence.getSequenceLength());
		}

		try {
			// progressListener.setProgress(((double) count) / 1);

			File traceFile = new File(ab1File);
			String fileName = traceFile.getName();

			Chromatogram trace = ChromatogramFactory.create(traceFile);
			SymbolList symbols = ChromatogramTools.getDNASequence(trace);
			Sequence seq = new SimpleSequence(symbols, fileName, fileName,
					Annotation.EMPTY_ANNOTATION);
			NucleotideSequenceDocument nucleotideSequenceDocument = new DefaultNucleotideSequence(
					fileName, "", seq.seqString().toString(), new Date());

			// SeqIOTools.writeFasta(System.out, seq);
			/*
			 * SequenceDocument nucleotideSequenceDocument = new
			 * DefaultNucleotideSequence( file.getName(), description,
			 * sequence.getSequenceString(), new Date(file.lastModified()));
			 */
			// count += fileSize;

			importCallback.addDocument(nucleotideSequenceDocument);

		} catch (UnsupportedChromatogramFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		 * List<AnnotatedPluginDocument> docs = PluginUtilities.importDocuments(
		 * new File(file.getAbsolutePath()), ProgressListener.EMPTY);
		 * 
		 * try { sequence = (SequenceDocument) docs.get(0).getDocument(); }
		 * catch (DocumentOperationException e) { e.printStackTrace(); }
		 * System.out.println("Sequence String=" +
		 * sequence.getSequenceString());
		 */
	}

	@Override
	public AutoDetectStatus tentativeAutoDetect(File file,
			String fileContentsStart) {
		return AutoDetectStatus.ACCEPT_FILE;
	}

	private void setExtractIDFromAB1FileName(String fileName) {
		/* for example: e4010125015_Sil_tri_MJ243_COI-A01_M13F_A01_008.ab1 */
		logger.info("Document Filename: " + fileName);
		String[] underscore = StringUtils.split(fileName, "_");
		limsAB1Flieds.setExtractID(underscore[0]);
		limsAB1Flieds.setPcrPlaatID(underscore[3]);
		limsAB1Flieds.setMarker(underscore[4]);
	}

	public void setNoteToAB1FileName(
			AnnotatedPluginDocument[] annotatedPluginDocuments,
			String fieldCode, String textNoteField, String noteTypeCode,
			String fieldValue, int count) {
		List<DocumentNoteField> listNotes = new ArrayList<DocumentNoteField>();

		/* "ExtractPlaatNummerCode" */
		this.fieldCode = fieldCode;
		/* Parameter example noteTypeCode = "Extract-Plaatnummer" */
		this.description = "Naturalis AB1 file " + noteTypeCode + " note";

		/*
		 * Parameter: textNoteField= ExtractPlaatNummer, this.fieldcode value
		 * fieldcode
		 */
		listNotes.add(DocumentNoteField.createTextNoteField(textNoteField,
				this.description, this.fieldCode, Collections.emptyList(),
				false));

		/* Check if note type exists */
		/* Parameter noteTypeCode get value "Extract Plaatnummer" */
		this.noteTypeCode = "DocumentNoteUtilities-" + noteTypeCode;
		DocumentNoteType documentNoteType = DocumentNoteUtilities
				.getNoteType(this.noteTypeCode);
		/* Extract-ID note */
		if (documentNoteType == null) {
			documentNoteType = DocumentNoteUtilities.createNewNoteType(
					noteTypeCode, this.noteTypeCode, this.description,
					listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteType);
			logger.info("NoteType " + noteTypeCode + " created succesful");
		}

		/* Create note for Extract-ID */
		DocumentNote documentNote = documentNoteType.createDocumentNote();
		documentNote.setFieldValue(this.fieldCode, fieldValue);

		AnnotatedPluginDocument.DocumentNotes documentNotes = (DocumentNotes) annotatedPluginDocuments[count]
				.getDocumentNotes(true);

		/* Set note */
		documentNotes.setNote(documentNote);
		/* Save the selected sequence document */
		documentNotes.saveNotes();
		logger.info("Note value " + noteTypeCode + " saved succesful");
	}

}
