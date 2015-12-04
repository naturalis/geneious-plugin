/**
 * 
 */
package nl.naturalis.lims2.ab1.importer;

import java.util.List;

import nl.naturalis.lims2.excel.importer.LimsNotes;
import nl.naturalis.lims2.updater.LimsAB1Fields;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.documents.sequence.NucleotideSequenceDocument;
import com.biomatters.geneious.publicapi.documents.sequence.SequenceDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentAction;
import com.biomatters.geneious.publicapi.plugin.DocumentFileImporter;
import com.biomatters.geneious.publicapi.plugin.DocumentOperationException;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;
import com.biomatters.geneious.publicapi.plugin.GeneiousPlugin;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsImportAB1Plugin extends GeneiousPlugin {

	static final String HELP = "Naturalis imported ab1 file with Chromatogram and DNA sequence(s)";

	@Override
	public String getAuthors() {
		return "Natauralis Reinier.Kartowikromo";
	}

	@Override
	public String getDescription() {
		return "Import AB1 files";
	}

	@Override
	public String getHelp() {
		return HELP;
	}

	@Override
	public int getMaximumApiVersion() {
		return 9;
	}

	@Override
	public String getMinimumApiVersion() {
		return "7.0";
	}

	@Override
	public String getName() {
		return "Naturalis AB1 file plugin";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	public DocumentFileImporter[] getDocumentFileImporters() {
		return new DocumentFileImporter[] { new LimsImportAB1() };
	}

	public DocumentAction[] getDocumentActions() {
		return new DocumentAction[] { new DocumentAction() {

			final Logger logger = LoggerFactory
					.getLogger(LimsImportAB1Plugin.class);

			SequenceDocument seq;
			LimsNotes limsNotes = new LimsNotes();
			LimsAB1Fields limsAB1Flieds = new LimsAB1Fields();
			private List<AnnotatedPluginDocument> docs;

			@Override
			public void actionPerformed(
					AnnotatedPluginDocument[] annotatedPluginDocuments) {
				logger.info("-----------------------------------------------------------------");
				try {
					docs = DocumentUtilities.getSelectedDocuments();
					for (int cnt = 0; cnt < docs.size(); cnt++) {
						seq = (SequenceDocument) docs.get(cnt).getDocument();
						logger.info("Start extracting value from file: "
								+ seq.getName());

						if (seq.getName() != null) {
							setFieldValuesFromAB1FileName(seq.getName());
							logger.info("Extract-ID: "
									+ limsAB1Flieds.getExtractID());
							logger.info("PCR plaat-ID: "
									+ limsAB1Flieds.getPcrPlaatID());
							logger.info("Mark: " + limsAB1Flieds.getMarker());

							/** set note for Extract-ID */
							limsNotes.setNoteToAB1FileName(
									annotatedPluginDocuments, "ExtractIdCode",
									"Extract ID", "Extract-ID",
									limsAB1Flieds.getExtractID(), cnt);

							/** set note for PCR Plaat-ID */
							limsNotes.setNoteToAB1FileName(
									annotatedPluginDocuments, "PcrPlaatIdCode",
									"PCR plaat ID", "PCR plaat ID",
									limsAB1Flieds.getPcrPlaatID(), cnt);

							/** set note for Marker */
							limsNotes.setNoteToAB1FileName(
									annotatedPluginDocuments, "MarkerCode",
									"Marker", "Marker",
									limsAB1Flieds.getMarker(), cnt);
						}
					}
				} catch (DocumentOperationException e) {
					try {
						throw new Exception();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				logger.info("Total of document(s) updated: " + docs.size());
				logger.info("-----------------------------------------------------------------");
				logger.info("Done with extracting Ab1 file name. ");

			}

			@Override
			public GeneiousActionOptions getActionOptions() {
				return new GeneiousActionOptions("Update AB1 document")
						.setInMainToolbar(true);
			}

			@Override
			public String getHelp() {
				return null;
			}

			@Override
			public DocumentSelectionSignature[] getSelectionSignatures() {
				return new DocumentSelectionSignature[] { new DocumentSelectionSignature(
						NucleotideSequenceDocument.class, 0, Integer.MAX_VALUE) };
			}

			private void setFieldValuesFromAB1FileName(String ab1FileName) {
				/*
				 * for example:
				 * e4010125015_Sil_tri_MJ243_COI-A01_M13F_A01_008.ab1
				 */
				String[] underscore = StringUtils.split(ab1FileName, "_");
				limsAB1Flieds.setExtractID(underscore[0]);
				limsAB1Flieds.setPcrPlaatID(underscore[3]);
				limsAB1Flieds.setMarker(underscore[4]);

			}

		}

		};
	}

}
