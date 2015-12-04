/**
 * 
 */
package nl.naturalis.lims2.excel.importer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import jebl.util.ProgressListener;
import nl.naturalis.lims2.utils.LimsImporterUtil;
import nl.naturalis.lims2.utils.LimsNotes;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentAction;
import com.biomatters.geneious.publicapi.plugin.DocumentFileImporter;
import com.biomatters.geneious.publicapi.plugin.DocumentImportException;
import com.biomatters.geneious.publicapi.plugin.GeneiousPlugin;
import com.biomatters.geneious.publicapi.plugin.PluginUtilities;
import com.opencsv.CSVReader;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsReadDataFromExcelPlugin extends GeneiousPlugin {

	@Override
	public String getAuthors() {
		return "Natauralis Reinier.Kartowikromo";
	}

	@Override
	public String getDescription() {
		return "Read data from excel file";
	}

	@Override
	public String getHelp() {
		return null;
	}

	@Override
	public int getMaximumApiVersion() {
		return 7;
	}

	@Override
	public String getMinimumApiVersion() {
		return "4.0";
	}

	@Override
	public String getName() {
		return "Naturalis import from Excel document";
	}

	@Override
	public String getVersion() {
		return "0.1";
	}

	public DocumentAction[] getDocumentActions() {
		return new DocumentAction[] { new LimsReadDataFromExcel() };
	}

	public DocumentFileImporter[] getDocumentFileImporters() {
		return new DocumentFileImporter[] { new DocumentFileImporter() {

			LimsImporterUtil limsImporterUtil = new LimsImporterUtil();
			LimsExcelFields limsExcelFields = new LimsExcelFields();
			LimsNotes limsNotes = new LimsNotes();

			private AnnotatedPluginDocument document;
			private int count = 0;
			final Logger logger = LoggerFactory
					.getLogger(LimsReadDataFromExcelPlugin.class);

			private String extractIDfileName = "";

			@Override
			public String getFileTypeDescription() {
				return "Naturalis add metadata from Excel files.";
			}

			@Override
			public String[] getPermissibleExtensions() {
				return new String[] { ".ab1" };
			}

			@Override
			public void importDocuments(File file,
					ImportCallback importCallback,
					ProgressListener progressListener) throws IOException,
					DocumentImportException {

				progressListener.setMessage("Importing excel data");
				List<AnnotatedPluginDocument> doclist = PluginUtilities
						.importDocuments(file, ProgressListener.EMPTY);

				count += doclist.size();

				document = importCallback
						.addDocument(doclist.iterator().next());

				if (file.getName() != null) {

					String[] underscore = StringUtils
							.split(file.getName(), "_");
					extractIDfileName = underscore[0];

					logger.info("");
					logger.info("Start adding metadata to ab1 file: "
							+ file.getName());
					logger.info("------------------------------");
					// logger.info("Import file: " + file.getName());

					readDataFromExcel();

					/* set note for Extract-ID */
					limsNotes.setImportNotes(document, "ExtractIdCode",
							"Extract ID", "Extract-ID",
							limsExcelFields.getExtractID());

					/* set note for Project Plaatnummer */
					limsNotes.setImportNotes(document,
							"ProjectPlaatnummerCode", "Project Plaatnummer",
							"Project Plaatnummer",
							limsExcelFields.getProjectPlaatNummer());

					/* Set note for Extract Plaatnummer */
					limsNotes.setImportNotes(document,
							"ExtractPlaatNummerCode", "Extract Plaatnummer",
							"Extract Plaatnummer",
							limsExcelFields.getExtractPlaatNummer());

					/* set note for Taxonnaam */
					limsNotes.setImportNotes(document, "TaxonNaamCode",
							"Taxon naam", "Taxon naam",
							limsExcelFields.getTaxonNaam());

					/* set note for Registrationnumber */
					limsNotes.setImportNotes(document, "BasisOfRecordCode",
							"Registrationnumber", "Registrationnumber",
							limsExcelFields.getRegistrationNumber());

					/* set note for Plaat positie */
					limsNotes.setImportNotes(document, "PlaatpositieCode",
							"Plaat positie", "Plaat positie",
							limsExcelFields.getPlaatPositie());
					logger.info("Done with adding notes to the document");
				}
				logger.info("Total of document(s) imported: " + count);
				logger.info("-----------------------------------------------------------------");
				logger.info("Done with importing the selected document(s). ");

			}

			@Override
			public AutoDetectStatus tentativeAutoDetect(File arg0, String arg1) {
				return AutoDetectStatus.ACCEPT_FILE;
			}

			private void readDataFromExcel() {
				String csvPath = null;
				String csvFile = null;
				try {
					csvFile = limsImporterUtil
							.getFileFromPropertieFile("excel");
					csvPath = limsImporterUtil.getPropValues() + csvFile;
				} catch (IOException e) {
					e.printStackTrace();
				}
				logger.info("Excel file path: " + csvPath);

				try {
					CSVReader csvReader = new CSVReader(
							new FileReader(csvPath), '\t', '\'', 0);

					String[] record = null;
					csvReader.readNext();

					logger.info("Start with adding notes to the document");
					try {
						while ((record = csvReader.readNext()) != null) {
							if (record.length == 0) {
								continue;
							}

							String ID = "e" + record[3];

							if (ID.equals(extractIDfileName)) {
								limsExcelFields
										.setProjectPlaatNummer(record[0]);
								limsExcelFields.setPlaatPositie(record[1]);
								limsExcelFields
										.setExtractPlaatNummer(record[2]);
								if (record[3] != null) {
									limsExcelFields.setExtractID(ID);
								}
								limsExcelFields
										.setRegistrationNumber(record[4]);
								limsExcelFields.setTaxonNaam(record[5]);
								// limsExcelFields.setSubSample(record[0]);

								logger.info("Extract-ID: "
										+ limsExcelFields.getExtractID());
								logger.info("Project plaatnummer: "
										+ limsExcelFields
												.getProjectPlaatNummer());
								logger.info("Extract plaatnummer: "
										+ limsExcelFields
												.getExtractPlaatNummer());
								logger.info("Taxon naam: "
										+ limsExcelFields.getTaxonNaam());
								logger.info("Registrationnumber: "
										+ limsExcelFields
												.getRegistrationNumber());
								logger.info("Plaat positie: "
										+ limsExcelFields.getPlaatPositie());

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
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

		};

	}
}
