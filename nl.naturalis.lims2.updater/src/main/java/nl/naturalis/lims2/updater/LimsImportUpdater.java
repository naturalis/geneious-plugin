/**
 * 
 */
package nl.naturalis.lims2.updater;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.naturalis.lims2.utils.LimsImporterUtil;


import org.apache.commons.lang3.StringUtils;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument.DocumentNotes;
import com.biomatters.geneious.publicapi.documents.DocumentField;
import com.biomatters.geneious.publicapi.documents.DocumentNote;
import com.biomatters.geneious.publicapi.documents.DocumentNoteField;
import com.biomatters.geneious.publicapi.documents.DocumentNoteType;
import com.biomatters.geneious.publicapi.documents.DocumentNoteUtilities;
import com.biomatters.geneious.publicapi.documents.sequence.NucleotideSequenceDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentAction;
import com.biomatters.geneious.publicapi.plugin.DocumentOperationException;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;
import com.opencsv.CSVReader;



/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsImportUpdater extends DocumentAction{

	private static final String KEY_BOS = "BOS";
	final DocumentField documentField = DocumentField.createStringField("Registrationnumber", "Basis of record in the document", KEY_BOS, true, true); 
    LimsImporterUtil limsImporterUtil = new LimsImporterUtil();
    LimsAB1Fields limsAB1Flieds = new LimsAB1Fields();
	
	AnnotatedPluginDocument annotatedPluginDocument;
	private String Name;
	private String Registrationnumber;
	private String CreatedDate;
	private String Description;
	private String ImportedFromFilename;
	private String ImportedFromPath;
	private String Modified;
	private String Size;
	private String extractIdFileName;

	
	@Override
	public void actionPerformed(AnnotatedPluginDocument[] annotatedPluginDocuments) {
		System.out.println("Registrationnumber: " + getMetaDataRegistrationnumber());
		
		/*SequenceAnnotation annotationToAdd = new SequenceAnnotation(Name, SequenceAnnotation.TYPE_CDS, new SequenceAnnotationInterval(3,10));
		EditableSequenceDocument sequence;
		try {
			sequence = (EditableSequenceDocument) annotatedPluginDocuments[0].getDocument();
		
		List<SequenceAnnotation> annotations= new ArrayList<SequenceAnnotation>(sequence.getSequenceAnnotations());
		annotations.add(annotationToAdd);
		sequence.setAnnotations(annotations);
		annotatedPluginDocuments[0].saveDocument();
		} catch (DocumentOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		
		try {
			extractIdFileName = annotatedPluginDocuments[0].getDocument().getName();
			System.out.println("Filename: " + extractIdFileName);
			if (extractIdFileName != null)
			{	
				setFieldValuesFromAB1FileName(extractIdFileName);
				System.out.println("Extract-ID: " + limsAB1Flieds.getExtractID());
				System.out.println("PCR plaat-ID: " + limsAB1Flieds.getPcrPlaatID());
				System.out.println("Mark: " + limsAB1Flieds.getMarker());
				setNoteForExtractIDFromAB1FileName(annotatedPluginDocuments);
				setNoteForPcrPlaatIDFromAB1FileName(annotatedPluginDocuments);
				setNoteForMarkerFromAB1FileName(annotatedPluginDocuments);
			}
		}
		catch (DocumentOperationException e) 
		{
			try 
			{
			throw new Exception();
			} 
			catch (Exception e1) 
			{
				e1.printStackTrace();
			}
		}
	}
	
	/**
	 * Create note for Extract-ID
	 * @param annotatedPluginDocuments Add annotatedPluginDocuments
	 */
	private void setNoteForExtractIDFromAB1FileName(AnnotatedPluginDocument[] annotatedPluginDocuments)
	{
		List<DocumentNoteField> listExtractID =  new ArrayList<DocumentNoteField>();
		String fieldExtractId  = "ExtractIdCode";
		
		listExtractID.add(DocumentNoteField.createTextNoteField("ExtractID",  "Naturalis AB1 file Extract-ID note", fieldExtractId,  (List) Collections.emptyList(), false));
		/* Check if note type exists */
		String noteTypeCodeExtractId  = "DocumentNoteUtilities-Extract ID";
		DocumentNoteType noteTypeExtract = DocumentNoteUtilities.getNoteType(noteTypeCodeExtractId);
		/* Extract-ID note */
		if (noteTypeExtract == null)
		{
			noteTypeExtract = DocumentNoteUtilities.createNewNoteType("Extract ID", noteTypeCodeExtractId, "Naturalis AB1 file Extract-ID note",  listExtractID, false);
			DocumentNoteUtilities.setNoteType(noteTypeExtract);
		}
		
		/* Create note for Extract-ID */
		DocumentNote documentNoteExtractID = noteTypeExtract.createDocumentNote();
		documentNoteExtractID.setFieldValue(fieldExtractId, limsAB1Flieds.getExtractID());
		
		AnnotatedPluginDocument.DocumentNotes documentNotes = (DocumentNotes) annotatedPluginDocuments[0].getDocumentNotes(true);
		
		/* Set note for Extract-ID, PCR plaat-ID and Marker */
		documentNotes.setNote(documentNoteExtractID);
		/* Save the selected sequence document */
		documentNotes.saveNotes();
		System.out.println("Note Extract-ID saved succesful");
	}
	
	/**
	 * Create a note PCR plaat-ID
	 * @param annotatedPluginDocuments Add annotatedPluginDocuments
	 */
	private void setNoteForPcrPlaatIDFromAB1FileName(AnnotatedPluginDocument[] annotatedPluginDocuments)
	{
		List<DocumentNoteField> listPcrPlaatID =  new ArrayList<DocumentNoteField>();
		String fieldPcrPlaatId = "PcrPlaatIdCode";

		listPcrPlaatID.add(DocumentNoteField.createTextNoteField("PcrPlaatID", "Naturalis AB1 file PCR Plaat-ID note", fieldPcrPlaatId,  (List) Collections.emptyList(), false));
		/* Check if note type exists */
		String noteTypeCodePcrPlaatId = "DocumentNoteUtilities-PCR plaat ID";
		DocumentNoteType noteTypePCR = DocumentNoteUtilities.getNoteType(noteTypeCodePcrPlaatId);
		/* PCR plaat-ID note */
		if (noteTypePCR == null)
		{
			noteTypePCR = DocumentNoteUtilities.createNewNoteType("PCR plaat ID", noteTypeCodePcrPlaatId, "Naturalis AB1 file PCR Plaat-ID note",  listPcrPlaatID, false);
			DocumentNoteUtilities.setNoteType(noteTypePCR);
		}
		
		/* Create note for PCR plaat-ID */
		DocumentNote documentNotePcrPlaatID = noteTypePCR.createDocumentNote();
		documentNotePcrPlaatID.setFieldValue(fieldPcrPlaatId, limsAB1Flieds.getPcrPlaatID());
	  	
		AnnotatedPluginDocument.DocumentNotes documentNotes = (DocumentNotes) annotatedPluginDocuments[0].getDocumentNotes(true);
		/* Set note for Extract-ID, PCR plaat-ID and Marker */
		documentNotes.setNote(documentNotePcrPlaatID);
		/* Save the selected sequence document */
		documentNotes.saveNotes();
		System.out.println("Note PCR plaat-ID saved succesful");	
	}
	
	/**
	 * Create note for Marker.
	 * @param annotatedPluginDocuments Add annotatedPluginDocuments
	 */
	private void setNoteForMarkerFromAB1FileName(AnnotatedPluginDocument[] annotatedPluginDocuments)
	{
		List<DocumentNoteField> listMarker =  new ArrayList<DocumentNoteField>();
		String fieldMarker     = "MarkerCode";

		listMarker.add(DocumentNoteField.createTextNoteField("Marker", "Naturalis AB1 file Marker note", fieldMarker,  (List) Collections.emptyList(), false));
		/* Check if note type exists */
		String noteTypeCodeMarker     = "DocumentNoteUtilities-Marker";
		DocumentNoteType noteTypeMarker = DocumentNoteUtilities.getNoteType(noteTypeCodeMarker);
		
		/* Marker note */
		if (noteTypeMarker == null)
		{
			noteTypeMarker = DocumentNoteUtilities.createNewNoteType("Marker", noteTypeCodeMarker, "Naturalis AB1 file Marker note",  listMarker, false);
			DocumentNoteUtilities.setNoteType(noteTypeMarker);
		}
		
		/* Create note for Marker */
		DocumentNote documentNoteMarker = noteTypeMarker.createDocumentNote();
		documentNoteMarker.setFieldValue(fieldMarker, limsAB1Flieds.getMarker());
	  	
		AnnotatedPluginDocument.DocumentNotes documentNotes = (DocumentNotes) annotatedPluginDocuments[0].getDocumentNotes(true);
		/* Set note for Extract-ID, PCR plaat-ID and Marker */
		documentNotes.setNote(documentNoteMarker);
		/* Save the selected sequence document */
		documentNotes.saveNotes();
		System.out.println("Note Marker saved succesful");
	}
	
	
	
	
	@Override
	public GeneiousActionOptions getActionOptions() {
		return new GeneiousActionOptions("Update Selected document").setInMainToolbar(true);
	}

	@Override
	public String getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DocumentSelectionSignature[] getSelectionSignatures() 
	{
		return new DocumentSelectionSignature[] {new DocumentSelectionSignature(NucleotideSequenceDocument.class,0,Integer.MAX_VALUE)};
	}
	

	/*public DocumentType[] getDocumentTypes() {
        return new DocumentType[]{new DocumentType<LimsImporterUpdaterDocument>("Naturalis update-File)", LimsImporterUpdaterDocument.class, null)};
    }*/

	private void setFieldValuesFromAB1FileName(String ab1FileName)
	{
		/* for example: e4010125015_Sil_tri_MJ243_COI-A01_M13F_A01_008.ab1 */
		
		String fileName = ab1FileName;
		String[] underscore = StringUtils.split(fileName, "_");
		limsAB1Flieds.setExtractID(underscore[0]);
		limsAB1Flieds.setPcrPlaatID(underscore[3]);
		limsAB1Flieds.setMarker(underscore[4]);
		
	}
	
	private String getMetaDataRegistrationnumber() 
	{
		String csvFileName = null;
		try {
			csvFileName = limsImporterUtil.getPropValues() + "BOS.csv";
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		System.out.println("CSV file: " + csvFileName);
		CSVReader csvReader = null;
		try {
			csvReader = new CSVReader(new FileReader(csvFileName), ',');
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		String[] record = null;
		try {
			csvReader.readNext();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			while ((record = csvReader.readNext()) != null) 
			{
				if (record.length == 0) {
					continue;
				}
											
				Name = record[0];
				Registrationnumber = record[1];
				CreatedDate = record[2];
				Description = record[3];
				ImportedFromFilename = record[4];
				ImportedFromPath = record[5];
				Modified = record[6];
				Size = record[7];
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			csvReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return Registrationnumber;
	}

}
