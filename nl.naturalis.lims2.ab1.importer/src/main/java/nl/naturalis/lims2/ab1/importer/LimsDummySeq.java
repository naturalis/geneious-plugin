/**
 * 
 */
package nl.naturalis.lims2.ab1.importer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jebl.util.ProgressListener;
import nl.naturalis.lims2.utils.LimsNotes;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.documents.URN;
import com.biomatters.geneious.publicapi.documents.sequence.NucleotideSequenceDocument;
import com.biomatters.geneious.publicapi.implementations.sequence.DefaultNucleotideSequence;
import com.biomatters.geneious.publicapi.plugin.Options;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsDummySeq {

	private LimsNotes limsNotes = new LimsNotes();
	private LimsExcelFields limsExcelFields = new LimsExcelFields();

	public List<AnnotatedPluginDocument> performOperation(
			AnnotatedPluginDocument[] docs, ProgressListener progress,
			Options options) {

		ArrayList<AnnotatedPluginDocument> sequenceList = new ArrayList<AnnotatedPluginDocument>();

		String residues = "NNNNNNNNNN";

		NucleotideSequenceDocument sequence = new DefaultNucleotideSequence(
				"New Sequence", "A new dummy Sequence", residues, new Date(),
				URN.generateUniqueLocalURN("Dummy"));

		sequenceList.add(DocumentUtilities
				.createAnnotatedPluginDocument(sequence));

		/* set note for Extract-ID */
		try {
			limsNotes.setImportNotes(sequenceList.iterator().next(),
					"ExtractIdCode", "Extract ID", "Extract-ID",
					limsExcelFields.getExtractID());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		/* set note for Project Plaatnummer */
		try {
			limsNotes.setImportNotes(sequenceList.iterator().next(),
					"ProjectPlaatnummerCode", "Project Plaatnummer",
					"Project Plaatnummer",
					limsExcelFields.getProjectPlaatNummer());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		/* set note for Extract Plaatnummer */
		try {
			limsNotes.setImportNotes(sequenceList.iterator().next(),
					"ExtractPlaatNummerCode", "Extract Plaatnummer",
					"Extract Plaatnummer",
					limsExcelFields.getExtractPlaatNummer());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		/* set note for Taxonnaam */
		try {
			limsNotes.setImportNotes(sequenceList.iterator().next(),
					"TaxonNaamCode", "Taxon naam", "Taxon naam",
					limsExcelFields.getTaxonNaam());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		/* set note for Registrationnumber */
		try {
			limsNotes.setImportNotes(sequenceList.iterator().next(),
					"BasisOfRecordCode", "Registrationnumber",
					"Registrationnumber",
					limsExcelFields.getRegistrationNumber());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		/* set note for Plaat positie */
		try {
			limsNotes.setImportNotes(sequenceList.iterator().next(),
					"PlaatpositieCode", "Plaat positie", "Plaat positie",
					limsExcelFields.getPlaatPositie());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		/* set note for Sample method */
		try {
			limsNotes.setImportNotes(sequenceList.iterator().next(),
					"SampleMethodCode", "Sample method", "Sample method",
					limsExcelFields.getSubSample());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		try {
			limsNotes.setImportNotes(sequenceList.iterator().next(),
					"VersieCode", "Version number", "Version number", "0");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		try {
			limsNotes.setImportNotes(sequenceList.iterator().next(),
					"VersieCode", "Version number", "Version number", "0");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		(progress).setProgress(1.0);

		return sequenceList;
	}

}
