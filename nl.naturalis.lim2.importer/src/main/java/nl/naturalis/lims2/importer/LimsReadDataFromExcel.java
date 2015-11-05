/**
 * 
 */
package nl.naturalis.lims2.importer;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.naturalis.lims2.utils.LimsImporterUtil;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.PluginDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentAction;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;
import com.opencsv.CSVReader;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsReadDataFromExcel extends DocumentAction {

	static final Logger logger = LoggerFactory.getLogger(LimsReadDataFromExcel.class);
	PluginDocument annotatedPluginDocument;
	LimsImporterUtil limsImporterUtil = new LimsImporterUtil();
	LimsExcelFields limsExcelFields = new LimsExcelFields();
	LimsNotes limsNotes = new LimsNotes();
	//private FileSelectionOption fileSelectionOption;
	String extractIDfileName = null;
	
	List<String> listExtractID = new ArrayList<String>();
	
	
	@Override
	public void actionPerformed(AnnotatedPluginDocument[] annotatedPluginDocuments) {
		
		//annotatedPluginDocuments = fileSelectionOption.setAllowMultipleSelection(true);
 
		logger.info("-----------------------------------------------------------------");
		logger.info("Start");
		
		if (annotatedPluginDocuments[0] != null)
		{
			readDataFromExcel(annotatedPluginDocuments);
		}
		logger.info("-----------------------------------------------------------------");
		logger.info("Done with reading excel file. ");

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
		//return new DocumentSelectionSignature.forNucleotideSequences(2,Integer.MAX_VALUE);
		return new DocumentSelectionSignature[] {new DocumentSelectionSignature(PluginDocument.class,0,Integer.MAX_VALUE)};
	}
	
	
	
	/*public LimsReadDataFromExcel() {
		
		//.addFileSelectionOption("Test", "Test1", "Test2"); //)addBooleanOption("sampleOption", "text displayed the user", true);
    }
	
	public LimsReadDataFromExcel getOptions(final AnnotatedPluginDocument[] documents, final SelectionRange selectionRange) throws DocumentOperationException {
	     return new LimsReadDataFromExcel();
	 }
	*/
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
		logger.info("CSV file: " + csvPath);
		//System.out.println("CSV file: " + csvPath);
		
		try
		{
		CSVReader csvReader = new CSVReader(new FileReader(csvPath), '\t', '\'', 0);
		
		extractIDfileName = getExtractIDFromAB1FileName(annotatedPluginDocuments);
		
		String[] record = null;
		csvReader.readNext();
		

		try {
			while ((record = csvReader.readNext()) != null) 
			{
				if (record.length == 0) {
					continue;
				}
				
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
				
				    logger.info("Extract-ID: " + limsExcelFields.getExtractID());
				    logger.info("Project plaatnummer: " + limsExcelFields.getProjectPlaatNummer());
				    logger.info("Extract plaatnummer: " + limsExcelFields.getExtractPlaatNummer());
				    logger.info("Taxon naam: " + limsExcelFields.getTaxonNaam());
				    logger.info("Registrationnumber: " + limsExcelFields.getRegistrationNumber());
				    logger.info("Plaat positie: " + limsExcelFields.getPlaatPositie());
				    
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
		logger.info("Document Filename: " + fileName);
		String[] underscore = StringUtils.split(fileName, "_");
		return underscore[0];
	}

}
