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
public class LimsNotesAB1FastaSamples {

	private static final Logger logger = LoggerFactory
			.getLogger(LimsNotesAB1FastaSamples.class);
	private LimsSamplesFields limsSamplesFields = new LimsSamplesFields();
	private LimsNotes limsNotes = new LimsNotes();
	private LimsImporterUtil limsImporterUtil = new LimsImporterUtil();

	private String regScientificname;

	/* Adding notes to the documents */

	public void enrich_AB1_Fasta_Documents_With_SamplesNotes(
			AnnotatedPluginDocument[] documents, int cnt) {

		/* set note for Registration number */
		limsNotes.setNoteToAB1FileName(documents,
				"RegistrationNumberCode_Samples", "Registr-nmbr (Samples)",
				"Registr-nmbr (Samples)",
				limsSamplesFields.getRegistrationNumber(), cnt);

		/* set note for Taxonname */
		limsNotes.setNoteToAB1FileName(documents, "TaxonName2Code_Samples",
				"[Scientific name] (Samples)", "[Scientific name] (Samples)",
				limsSamplesFields.getTaxonNaam(), cnt);

		/* set note for Project Plate number */
		limsNotes.setNoteToAB1FileName(documents,
				"ProjectPlateNumberCode_Samples", "Sample plate ID (Samples)",
				"Sample plate ID (Samples)",
				limsSamplesFields.getProjectPlaatNummer(), cnt);

		/* Set note for Extract plate number */
		limsNotes.setNoteToAB1FileName(documents,
				"ExtractPlateNumberCode_Samples", "Extract plate ID (Samples)",
				"Extract plate ID (Samples)",
				limsSamplesFields.getExtractPlaatNummer(), cnt);

		/* set note for Plate position */
		limsNotes.setNoteToAB1FileName(documents, "PlatePositionCode_Samples",
				"Position (Samples)", "Position (Samples)",
				limsSamplesFields.getPlaatPositie(), cnt);

		/* set note for Extract-ID */
		limsNotes.setNoteToAB1FileName(documents, "ExtractIDCode_Samples",
				"Extract ID (Samples)", "Extract ID (Samples)",
				limsSamplesFields.getExtractID(), cnt);

		/* set note for Sample method */
		limsNotes.setNoteToAB1FileName(documents, "SampleMethodCode_Samples",
				"Extraction method (Samples)", "Extraction method (Samples)",
				limsSamplesFields.getSubSample(), cnt);

		/* Set note for the document version */
		limsNotes.setNoteToAB1FileName(documents, "DocumentVersionCode_Seq",
				"Document version", "Document version",
				String.valueOf(limsSamplesFields.getVersieNummer()), cnt);

		/* AmplicificationStaffCode_FixedValue_Samples */

		try {
			limsNotes.setNoteToAB1FileName(documents,
					"AmplicificationStaffCode_FixedValue_Samples",
					"Ampl-staff (Samples)", "Ampl-staff (Samples)",
					limsImporterUtil.getPropValues("samplesamplicification"),
					cnt);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		/*
		 * Lims-190:Sample import maak of update extra veld veldnaam -
		 * Registr-nmbr_[Scientific name] (Samples) en veldcode =
		 * RegistrationNumberCode_TaxonName2Code_Samples
		 */
		limsNotes.setNoteToAB1FileName(documents,
				"RegistrationNumberCode_TaxonName2Code_Samples",
				"Registr-nmbr_[Scientific_name] (Samples)",
				"Registr-nmbr_[Scientific_name] (Samples)",
				limsSamplesFields.getRegNumberScientificName(), cnt);
	}

	/* Add the values to the fields variables */
	public void setSamplesNotes_FieldsValues(String projectPlaatNr,
			String plaatPositie, String extractPlaatNr, String extractID,
			String registrationNumber, String taxonNaam, Object versieNummer,
			String sampleMethod) {

		// record[0]
		limsSamplesFields.setProjectPlaatNummer(projectPlaatNr);

		// record[1]
		limsSamplesFields.setPlaatPositie(plaatPositie);

		// record[2]
		limsSamplesFields.setExtractPlaatNummer(extractPlaatNr);

		// record[3]
		if (extractID != null) {
			limsSamplesFields.setExtractID(extractID);
		} else {
			limsSamplesFields.setExtractID("");
		}

		// record[4]
		limsSamplesFields.setRegistrationNumber(registrationNumber);

		// record[5]
		limsSamplesFields.setTaxonNaam(taxonNaam);

		// record[6]
		limsSamplesFields.setSubSample(sampleMethod);

		if (registrationNumber.length() > 0 && taxonNaam.length() > 0) {
			regScientificname = registrationNumber + "_"
					+ taxonNaam.replaceAll(" ", "_");
		} else if (registrationNumber.length() > 0) {
			regScientificname = registrationNumber;
		} else if (registrationNumber.length() == 0 && taxonNaam.length() > 0) {
			regScientificname = taxonNaam;
		}

		/*
		 * Set a combination of registrationnumber with scientificname or only
		 * scientificname
		 */
		limsSamplesFields.setRegNumberScientificName(regScientificname);

		/*
		 * Set the version number
		 */
		limsSamplesFields.setVersieNummer(versieNummer);

		logger.info("Extract-ID: " + limsSamplesFields.getExtractID());
		logger.info("Project plaatnummer: "
				+ limsSamplesFields.getProjectPlaatNummer());
		logger.info("Extract plaatnummer: "
				+ limsSamplesFields.getExtractPlaatNummer());
		logger.info("Taxon naam: " + limsSamplesFields.getTaxonNaam());
		logger.info("Registrationnumber: "
				+ limsSamplesFields.getRegistrationNumber());
		logger.info("Plaat positie: " + limsSamplesFields.getPlaatPositie());
		logger.info("Sample method: " + limsSamplesFields.getSubSample());
		logger.info("Registr-nmbr_[Scientific_name] (Samples): "
				+ limsSamplesFields.getRegNumberScientificName());
	}

}
