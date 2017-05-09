/**
 * 
 */
package nl.naturalis.lims2.utils;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsNotesAB1FastaImport {

	private static final Logger logger = LoggerFactory
			.getLogger(LimsNotesAB1FastaImport.class);
	private LimsAB1Fields limsAB1Fields = new LimsAB1Fields();
	private LimsNotes limsNotes = new LimsNotes();
	private LimsImporterUtil limsImporterUtil = new LimsImporterUtil();
	public String extractID;
	public String pcrPlateID;
	public String marker;
	static final String nd = "not determined";

	public void enrich_AB1_And_Fasta_DocumentsWithNotes(
			AnnotatedPluginDocument documentAnnotated, String pFileName,
			int pVersienummer) throws IOException {
		logger.info("----------------------------S T A R T ---------------------------------");
		logger.info("Start extracting value from file: " + pFileName);

		/* set note for Extract-ID */
		limsNotes.setImportNotes(documentAnnotated, "ExtractIDCode_Seq",
				"Extract ID (Seq)", "Extract ID (Seq)",
				limsAB1Fields.getExtractID());

		/* set note for PCR Plaat-ID */
		limsNotes.setImportNotes(documentAnnotated, "PCRplateIDCode_Seq",
				"PCR plate ID (Seq)", "PCR plate ID (Seq)",
				limsAB1Fields.getPcrPlaatID());

		/* set note for Marker */
		limsNotes.setImportNotes(documentAnnotated, "MarkerCode_Seq",
				"Marker (Seq)", "Marker (Seq)", limsAB1Fields.getMarker());

		/* set note for Document version */
		limsNotes.setImportNotes(documentAnnotated, "DocumentVersionCode_Seq",
				"Document version", "Document version",
				Integer.toString(pVersienummer));

		/* set note for SequencingStaffCode_FixedValue_Seq */
		limsNotes.setImportNotes(documentAnnotated,
				"SequencingStaffCode_FixedValue_Seq", "Seq-staff (Seq)",
				"Seq-staff (Seq)",
				limsImporterUtil.getPropValues("seqsequencestaff"));

		/* set note for ConsensusSeqPassCode_Seq */
		try {
			limsNotes.setImportConsensusSeqPassNotes(documentAnnotated,
					limsNotes.ConsensusSeqPass, "ConsensusSeqPassCode_Seq",
					"Pass (Seq)", "Pass (Seq)",
					limsNotes.ConsensusSeqPass[6] = "not determined");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void set_AB1_Fasta_DocumentFileName(String extractAb1FastaFileName) {
		limsAB1Fields.extractAB1_FastaFileName(extractAb1FastaFileName);
		extractID = limsAB1Fields.getExtractID();
		pcrPlateID = limsAB1Fields.getPcrPlaatID();
		marker = limsAB1Fields.getMarker();
	}

	public void setFastaDocumentFileName(String pExtractFastaFileName) {
		limsAB1Fields.extractAB1_FastaFileName(pExtractFastaFileName);
		extractID = limsAB1Fields.getExtractID();
		pcrPlateID = limsAB1Fields.getPcrPlaatID();
		marker = limsAB1Fields.getMarker();
	}
}