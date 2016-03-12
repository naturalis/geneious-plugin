/**
 * 
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
 * @author Reinier.Kartowikromo
 *
 */
public class LimsDummySeq {

	private LimsNotes limsNotes = new LimsNotes();
	private LimsImporterUtil limsImporterUtil = new LimsImporterUtil();
	private static final Logger logger = LoggerFactory
			.getLogger(LimsDummySeq.class);

	public void createDummySampleSequence(String filename, String extractID,
			String projectPlaatnummer, String extractPlaatnummer,
			String taxonName, String registrationNumber, String plaatPositie) {

		ArrayList<AnnotatedPluginDocument> sequenceList = new ArrayList<AnnotatedPluginDocument>();

		NucleotideSequenceDocument sequence = new DefaultNucleotideSequence(
				filename + ".dum", "A new dummy Sequence Samples",
				"NNNNNNNNNN", new Date(), URN.generateUniqueLocalURN("Dummy"));

		sequenceList.add(DocumentUtilities
				.createAnnotatedPluginDocument(sequence));

		/** set note for Extract-ID */
		limsNotes.setImportNotes(sequenceList.iterator().next(),
				"ExtractIDCode_Samples", "Extract ID (Samples)",
				"Extract ID (Samples)", extractID);

		/** set note for Project Plate number */
		limsNotes.setImportNotes(sequenceList.iterator().next(),
				"ProjectPlateNumberCode_Samples", "Sample plate ID (Samples)",
				"Sample plate ID (Samples)", projectPlaatnummer);

		/** Set note for Extract Plate number */
		limsNotes.setImportNotes(sequenceList.iterator().next(),
				"ExtractPlateNumberCode_Samples", "Extract plate ID (Samples)",
				"Extract plate ID (Samples)", extractPlaatnummer);

		/** set note for Taxon name */
		limsNotes.setImportNotes(sequenceList.iterator().next(),
				"TaxonName2Code_Samples", "[Scientific name] (Samples)",
				"[Scientific name] (Samples)", taxonName);

		/** set note for Registration number */
		limsNotes.setImportNotes(sequenceList.iterator().next(),
				"RegistrationNumberCode_Samples", "Registr-nmbr (Samples)",
				"Registr-nmbr (Samples)", registrationNumber);

		/** set note for Plate position */
		limsNotes.setImportNotes(sequenceList.iterator().next(),
				"PlatePositionCode_Samples", "Position (Samples)",
				"Position (Samples)", plaatPositie);

		/** set note for Sample method */
		limsNotes.setImportNotes(sequenceList.iterator().next(),
				"SampleMethodCode_Samples", "Extraction method (Samples)",
				"Extraction method (Samples)", "Sample method");

		limsNotes.setImportNotes(sequenceList.iterator().next(),
				"DocumentVersionCode_Seq", "Document version",
				"Document version", "0");

		/* set note for PCR Plaat-ID */
		limsNotes.setImportNotes(sequenceList.iterator().next(),
				"PCRplateIDCode_Seq", "PCR plate ID (Seq)",
				"PCR plate ID (Seq)", "AA000");

		/* set note for Marker */
		limsNotes.setImportNotes(sequenceList.iterator().next(),
				"MarkerCode_Seq", "Marker (Seq)", "Marker (Seq)", "Dum");

		/** SequencingStaffCode_FixedValue */
		try {
			limsNotes.setImportNotes(sequenceList.iterator().next(),
					"SequencingStaffCode_FixedValue_Samples",
					"Seq-staff (Samples)", "Seq-staff (Samples)",
					limsImporterUtil.getPropValues("samplessequencestaff"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		logger.info("New Dummy: " + filename + " file added.");
		DocumentUtilities.addGeneratedDocuments(sequenceList, false);
	}

	/*
	 * public List<AnnotatedPluginDocument> performOperation(
	 * AnnotatedPluginDocument[] docs, ProgressListener progress, Options
	 * options) {
	 * 
	 * ArrayList<AnnotatedPluginDocument> sequenceList = new
	 * ArrayList<AnnotatedPluginDocument>();
	 * 
	 * String residues = "NNNNNNNNNN";
	 * 
	 * NucleotideSequenceDocument sequence = new DefaultNucleotideSequence(
	 * "New Sequence", "A new dummy Sequence", residues, new Date(),
	 * URN.generateUniqueLocalURN("Dummy"));
	 * 
	 * sequenceList.add(DocumentUtilities
	 * .createAnnotatedPluginDocument(sequence));
	 * 
	 * set note for Extract-ID try {
	 * limsNotes.setImportNotes(sequenceList.iterator().next(), "ExtractIdCode",
	 * "Extract ID", "Extract-ID", limsExcelFields.getExtractID()); } catch
	 * (Exception ex) { ex.printStackTrace(); }
	 * 
	 * set note for Project Plaatnummer try {
	 * limsNotes.setImportNotes(sequenceList.iterator().next(),
	 * "ProjectPlaatnummerCode", "Project Plaatnummer", "Project Plaatnummer",
	 * limsExcelFields.getProjectPlaatNummer()); } catch (Exception ex) {
	 * ex.printStackTrace(); }
	 * 
	 * set note for Extract Plaatnummer try {
	 * limsNotes.setImportNotes(sequenceList.iterator().next(),
	 * "ExtractPlaatNummerCode", "Extract Plaatnummer", "Extract Plaatnummer",
	 * limsExcelFields.getExtractPlaatNummer()); } catch (Exception ex) {
	 * ex.printStackTrace(); }
	 * 
	 * set note for Taxonnaam try {
	 * limsNotes.setImportNotes(sequenceList.iterator().next(), "TaxonNaamCode",
	 * "Taxon naam", "Taxon naam", limsExcelFields.getTaxonNaam()); } catch
	 * (Exception ex) { ex.printStackTrace(); }
	 * 
	 * set note for Registrationnumber try {
	 * limsNotes.setImportNotes(sequenceList.iterator().next(),
	 * "BasisOfRecordCode", "Registrationnumber", "Registrationnumber",
	 * limsExcelFields.getRegistrationNumber()); } catch (Exception ex) {
	 * ex.printStackTrace(); }
	 * 
	 * set note for Plaat positie try {
	 * limsNotes.setImportNotes(sequenceList.iterator().next(),
	 * "PlaatpositieCode", "Plaat positie", "Plaat positie",
	 * limsExcelFields.getPlaatPositie()); } catch (Exception ex) {
	 * ex.printStackTrace(); }
	 * 
	 * set note for Sample method try {
	 * limsNotes.setImportNotes(sequenceList.iterator().next(),
	 * "SampleMethodCode", "Sample method", "Sample method",
	 * limsExcelFields.getSubSample()); } catch (Exception ex) {
	 * ex.printStackTrace(); }
	 * 
	 * try { limsNotes.setImportNotes(sequenceList.iterator().next(),
	 * "VersieCode", "Version number", "Version number", "0"); } catch
	 * (Exception ex) { ex.printStackTrace(); }
	 * 
	 * (progress).setProgress(1.0);
	 * 
	 * return sequenceList; }
	 */

}
