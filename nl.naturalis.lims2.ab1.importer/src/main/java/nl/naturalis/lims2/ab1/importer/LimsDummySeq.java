/**
 * <h1> Dummy Sequence</h>
 */
package nl.naturalis.lims2.ab1.importer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import nl.naturalis.lims2.utils.LimsImporterUtil;
import nl.naturalis.lims2.utils.LimsNotes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.documents.URN;
import com.biomatters.geneious.publicapi.documents.sequence.NucleotideSequenceDocument;
import com.biomatters.geneious.publicapi.implementations.sequence.DefaultNucleotideSequence;

/**
 * <table summary="Create dummies sequence">
 * <tr>
 * <td>
 * Date: 24 august 2016</td>
 * </tr>
 * <tr>
 * <td>
 * Company: Naturalis Biodiversity Center</td>
 * </tr>
 * <tr>
 * <td>
 * City: Leiden</td>
 * </tr>
 * <tr>
 * <td>
 * Country: Netherlands</td>
 * </tr>
 * <tr>
 * <td>
 * Description:<br>
 * Class to create a dummy DefaultNucleotideSequence document with some values.<br>
 * Parameters are:<br>
 * filename, extractID, projectPlaatnummer, extractPlaatnummer, taxonName,
 * registrationNumber, plaatPositie, extractMethod
 * </tr>
 * </table>
 * 
 * @author Reinier.Kartowikromo
 *
 */
public class LimsDummySeq {

	private LimsNotes limsNotes = new LimsNotes();
	private LimsImporterUtil limsImporterUtil = new LimsImporterUtil();
	private static final Logger logger = LoggerFactory
			.getLogger(LimsDummySeq.class);

	/**
	 * Method to create a dummy sequence document
	 * 
	 * @param filename
	 *            Set param filename
	 * @param extractID
	 *            set param extractid
	 * @param projectPlaatnummer
	 *            set param Project plaatnummer
	 * @param extractPlaatnummer
	 *            set param extractplaatnummer
	 * @param taxonName
	 *            set param taxonname
	 * @param registrationNumber
	 *            set param Registration number
	 * @param plaatPositie
	 *            set param PlaatPositie
	 * @param extractMethod
	 *            set param ExtractMethod
	 * */
	public void createDummySampleSequence(String filename, String extractID,
			String projectPlaatnummer, String extractPlaatnummer,
			String taxonName, String registrationNumber, String plaatPositie,
			String extractMethod) {

		ArrayList<AnnotatedPluginDocument> sequenceList = new ArrayList<AnnotatedPluginDocument>();

		/* Define the values for the dummy document */
		NucleotideSequenceDocument sequence = new DefaultNucleotideSequence(
				filename + ".dum", "A new dummy Sequence Samples",
				"NNNNNNNNNN", new Date(), URN.generateUniqueLocalURN("Dummy"));

		/* Add seqeunce document */
		sequenceList.add(DocumentUtilities
				.createAnnotatedPluginDocument(sequence));

		/* set note for PCR Plaat-ID */
		limsNotes.setImportNotes(sequenceList.iterator().next(),
				"PCRplateIDCode_Seq", "PCR plate ID (Seq)",
				"PCR plate ID (Seq)", "AA000");

		/* set note for Marker */
		limsNotes.setImportNotes(sequenceList.iterator().next(),
				"MarkerCode_Seq", "Marker (Seq)", "Marker (Seq)", "Dum");

		/* set note for Extract-ID */
		limsNotes.setImportNotes(sequenceList.iterator().next(),
				"ExtractIDCode_Samples", "Extract ID (Samples)",
				"Extract ID (Samples)", extractID);

		/* set note for Project Plate number */
		limsNotes.setImportNotes(sequenceList.iterator().next(),
				"ProjectPlateNumberCode_Samples", "Sample plate ID (Samples)",
				"Sample plate ID (Samples)", projectPlaatnummer);

		/* set note for Plate position */
		limsNotes.setImportNotes(sequenceList.iterator().next(),
				"PlatePositionCode_Samples", "Position (Samples)",
				"Position (Samples)", plaatPositie);

		/* Set note for Institute Extract Plate number */
		limsNotes.setImportNotes(sequenceList.iterator().next(),
				"ExtractPlateNumberCode_Samples", "Extract plate ID (Samples)",
				"Extract plate ID (Samples)", extractPlaatnummer);

		/* set note for Taxon name */
		limsNotes.setImportNotes(sequenceList.iterator().next(),
				"TaxonName2Code_Samples", "[Scientific name] (Samples)",
				"[Scientific name] (Samples)", taxonName);

		/* set note for Registration number */
		limsNotes.setImportNotes(sequenceList.iterator().next(),
				"RegistrationNumberCode_Samples", "Registr-nmbr (Samples)",
				"Registr-nmbr (Samples)", registrationNumber);

		/* set note for Sample method */
		limsNotes.setImportNotes(sequenceList.iterator().next(),
				"SampleMethodCode_Samples", "Extraction method (Samples)",
				"Extraction method (Samples)", extractMethod);

		/* Set note Version */
		limsNotes.setImportNotes(sequenceList.iterator().next(),
				"DocumentVersionCode_Seq", "Document version",
				"Document version", "0");

		/* SequencingStaffCode_FixedValue */
		try {
			limsNotes.setImportNotes(sequenceList.iterator().next(),
					"SequencingStaffCode_FixedValue_Samples",
					"Seq-staff (Samples)", "Seq-staff (Samples)",
					limsImporterUtil.getPropValues("samplessequencestaff"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		/* AmplicificationStaffCode_FixedValue_Samples */
		try {
			limsNotes.setImportNotes(sequenceList.iterator().next(),
					"AmplicificationStaffCode_FixedValue_Samples",
					"Ampl-staff (Samples)", "Ampl-staff (Samples)",
					limsImporterUtil.getPropValues("samplesamplicification"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		/*
		 * Lims-190:Sample import maak of update extra veld veldnaam -
		 * Registr-nmbr_[Scientific name] (Samples) en veldcode =
		 * RegistrationNumberCode_TaxonName2Code_Samples
		 * 
		 * combine Registration number with scientificname
		 */

		String regScientificname = "";
		if (registrationNumber.length() > 0 && taxonName.length() > 0) {
			regScientificname = registrationNumber + " " + taxonName;
		} else if (registrationNumber.length() > 0) {
			regScientificname = registrationNumber;
		} else if (registrationNumber.length() == 0 && taxonName.length() > 0) {
			regScientificname = taxonName;
		}

		/* Set note Registration with scientificname */
		limsNotes.setImportNotes(sequenceList.iterator().next(),
				"RegistrationNumberCode_TaxonName2Code_Samples",
				"Registr-nmbr_[Scientific_name] (Samples)",
				"Registr-nmbr_[Scientific_name] (Samples)",
				regScientificname.trim());

		logger.info("New Dummy: " + filename + " file added.");
		DocumentUtilities.addGeneratedDocuments(sequenceList, false);
	}
}