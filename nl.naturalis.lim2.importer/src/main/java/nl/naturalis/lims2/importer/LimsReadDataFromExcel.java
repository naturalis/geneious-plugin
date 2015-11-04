/**
 * 
 */
package nl.naturalis.lims2.importer;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import nl.naturalis.lims2.utils.LimsImporterUtil;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.PluginDocument;
import com.biomatters.geneious.publicapi.documents.sequence.NucleotideSequenceDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentAction;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;
import com.opencsv.CSVReader;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsReadDataFromExcel extends DocumentAction {

	PluginDocument annotatedPluginDocument;
	LimsImporterUtil limsImporterUtil = new LimsImporterUtil();
	LimsExcelFields limsExcelFields = new LimsExcelFields();
	LimsNotes limsNotes = new LimsNotes();
	
	String extractIDfileName = null;
	
	List<String> listExtractID = new ArrayList<String>();
	
	
	@Override
	public void actionPerformed(AnnotatedPluginDocument[] annotatedPluginDocuments) {

		readDataFromExcel(annotatedPluginDocuments);
/*		extractIDfileName = getExtractIDFromAB1FileName(annotatedPluginDocuments);
		System.out.println("File extractID: " + extractIDfileName);	
		if (extractIDfileName != null)
		{		
			readDataFromExcel(annotatedPluginDocuments);
			System.out.println("List Result: " + listExtractID.toString());	
			
			
			System.out.println("#1 iterator");
			Iterator<String> iterator = listExtractID.iterator();
			while (iterator.hasNext()) {
				System.out.println("# FileName: " + iterator.next());
				if (iterator.next().equals(extractIDfileName))
				{
				   System.out.println("Iterator: " + iterator.next());
				}
			}
			
			// for loop
			System.out.println("#2 for");
			for (int i = 0; i < listExtractID.size(); i++) {
				if (listExtractID.get(i).equals(extractIDfileName))
				{
					System.out.println(listExtractID.get(i));
				}
			}*/
		
/*			for (String record : listExtractID) {
				System.out.println("Record Reinier List: " + record);
				System.out.println("ExtractID: " + record);	
			
				if (record.equals(extractIDfileName))
				{
					extractPlaatNummer = listExtractID.get(0);
					extractID = listExtractID.get(1);
					System.out.println("FileName: " + extractIDfileName);	
					setNoteForExtractPlaatNummerFromAB1FileName(annotatedPluginDocuments);
				}
			}*/
		//}
	}

	@Override
	public GeneiousActionOptions getActionOptions() {
		return new GeneiousActionOptions("Read data from Excel").setInMainToolbar(true);
	}

	@Override
	public String getHelp() {
		return null;
	}

	@Override
	public DocumentSelectionSignature[] getSelectionSignatures() {
		return new DocumentSelectionSignature[] {new DocumentSelectionSignature(NucleotideSequenceDocument.class,0,Integer.MAX_VALUE)};
	}
	
	
	private void readDataFromExcel(AnnotatedPluginDocument[] annotatedPluginDocuments) 
	{
		String csvPath = null;
		String csvFile = null;
		try
		{
		csvFile = limsImporterUtil.getFileFromPropertieFile(); 
		csvPath = limsImporterUtil.getPropValues() + csvFile;
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		System.out.println("CSV file: " + csvPath);
		
		try
		{
		CSVReader csvReader = new CSVReader(new FileReader(csvPath), '\t', '\'', 0);
		
		String[] record = null;
		csvReader.readNext();

		try {
			while ((record = csvReader.readNext()) != null) 
			{
				if (record.length == 0) {
					continue;
				}
				
				extractIDfileName = getExtractIDFromAB1FileName(annotatedPluginDocuments);
				//System.out.println("File extractID: " + extractIDfileName);	
				
				/*try {
					annotatedPluginDocument = annotatedPluginDocuments[5].getDocument();
				} catch (DocumentOperationException e) {
					e.printStackTrace();
				}*/
				
				String ID =  "e" + record[3];
				
				if (ID.equals(extractIDfileName))
				{
					limsExcelFields.setProjectPlaatNummer(record[0]);
					limsExcelFields.setPlaatPositie(record[1]);
					limsExcelFields.setExtractPlaatNummer(record[2]);
					if(record[3] != null)
					{
						limsExcelFields.setExtractID(ID);
					}
					limsExcelFields.setRegistrationNumber(record[4]);
					limsExcelFields.setTaxonNaam(record[5]);
					//limsExcelFields.setSubSample(record[0]);
				
				
					System.out.println("Extract-ID: " + limsExcelFields.getExtractID());
					System.out.println("Extract plaatnummer: " + limsExcelFields.getExtractPlaatNummer());
					System.out.println("Project plaatnummer: " + limsExcelFields.getProjectPlaatNummer());
				
					
					/* setNoteToAB1FileName(AnnotatedPluginDocument[] annotatedPluginDocuments, String fieldCode, 
										    String textNoteField, String noteTypeCode, String fieldValue)*/
					
					/* set note for Extract-ID*/
					limsNotes.setNoteToAB1FileName(annotatedPluginDocuments, "ExtractIdCode", "Extract ID", "Extract-ID", limsExcelFields.getExtractID());
					
					/* set note for Project Plaatnummer */
					limsNotes.setNoteToAB1FileName(annotatedPluginDocuments, "ProjectPlaatnummerCode", "Project Plaatnummer", "Project Plaatnummer" , limsExcelFields.getProjectPlaatNummer());
					
					/* Set note for Extract Plaatnummer*/
					limsNotes.setNoteToAB1FileName(annotatedPluginDocuments, "ExtractPlaatNummerCode", "Extract Plaatnummer", "Extract Plaatnummer", limsExcelFields.getExtractPlaatNummer());
					
					/* set note for Taxonnaam */
					limsNotes.setNoteToAB1FileName(annotatedPluginDocuments, "TaxonNaamCode", "Taxon naam", "Taxon naam", limsExcelFields.getTaxonNaam());
					
					/* set note for Registrationnumber */
					limsNotes.setNoteToAB1FileName(annotatedPluginDocuments, "BasisOfRecordCode", "Registrationnumber", "Registrationnumber", limsExcelFields.getRegistrationNumber());
					
					/* set note for Plaat positie */
					limsNotes.setNoteToAB1FileName(annotatedPluginDocuments, "PlaatpositieCode", "Plaat positie", "Plaat positie", limsExcelFields.getPlaatPositie());
					
					
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
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Extract the ID from the filename
	 * @param annotatedPluginDocuments set the param 
	 * @return
	 */
	private String getExtractIDFromAB1FileName(AnnotatedPluginDocument[] annotatedPluginDocuments)
	{
		/* for example: e4010125015_Sil_tri_MJ243_COI-A01_M13F_A01_008.ab1 */
		String fileName = annotatedPluginDocuments[0].getName().toString();
		String[] underscore = StringUtils.split(fileName, "_");
		return underscore[0];
	}
	
	
	/*private void setNoteForExtractPlaatNummerFromAB1FileName(AnnotatedPluginDocument[] annotatedPluginDocuments)
	{
		List<DocumentNoteField> listExtractPlaatnummer =  new ArrayList<DocumentNoteField>();
		String fieldExtractPlaatnr  = "ExtractPlaatNummerCode";
		
		listExtractPlaatnummer.add(DocumentNoteField.createTextNoteField("ExtractPlaatNummer",  "Naturalis AB1 file Extract-Plaatnummer note", fieldExtractPlaatnr,  (List) Collections.emptyList(), false));
		 Check if note type exists 
		String noteTypeCodeExtractPlaatnr  = "DocumentNoteUtilities-Extract Plaatnummer";
		DocumentNoteType noteTypeExtractPlaatnr = DocumentNoteUtilities.getNoteType(noteTypeCodeExtractPlaatnr);
		 Extract-ID note 
		if (noteTypeExtractPlaatnr == null)
		{
			noteTypeExtractPlaatnr = DocumentNoteUtilities.createNewNoteType("Extract Plaatnummer", noteTypeCodeExtractPlaatnr, "Naturalis AB1 file Extract-Plaatnummer note",  listExtractPlaatnummer, false);
			DocumentNoteUtilities.setNoteType(noteTypeExtractPlaatnr);
		}
		
		 Create note for Extract-ID 
		DocumentNote documentNoteExtractID = noteTypeExtractPlaatnr.createDocumentNote();
		documentNoteExtractID.setFieldValue(fieldExtractPlaatnr, getExtractPlaatNummerValue());
		
		AnnotatedPluginDocument.DocumentNotes documentNotes = (DocumentNotes) annotatedPluginDocuments[0].getDocumentNotes(true);
		
		 Set note for Project plaatnummer
		documentNotes.setNote(documentNoteExtractID);
		 Save the selected sequence document 
		documentNotes.saveNotes();
		System.out.println("Note Extract- Plaatnummer saved succesful");
	}
*/

}
