/**
 * 
 */
package nl.naturalis.lims2.ab1.importer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import jebl.util.ProgressListener;
import nl.naturalis.lims2.importer.LimsNotes;
import nl.naturalis.lims2.updater.LimsAB1Fields;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.PluginDocument;
import com.biomatters.geneious.publicapi.documents.sequence.SequenceDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentFileImporter;
import com.biomatters.geneious.publicapi.plugin.DocumentImportException;
import com.biomatters.geneious.publicapi.plugin.PluginUtilities;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsImportAB1 extends DocumentFileImporter {

	static final Logger logger;
	SequenceDocument sequence;
	LimsAB1Fields limsAB1Flieds = new LimsAB1Fields();
	LimsNotes limsNotes = new LimsNotes();
	PluginDocument annotatedPluginDocuments;
	AnnotatedPluginDocument[] annotatedPluginDocument;

	static {
		logger = LoggerFactory.getLogger(LimsImportAB1.class);
	}

	@Override
	public String getFileTypeDescription() {
		return "Naturalis Chromatogram AB1 Importer";
	}

	@Override
	public String[] getPermissibleExtensions() {
		return new String[] { "ab1", "abi" };
	}

	@Override
	public void importDocuments(File file, ImportCallback importCallback,
			ProgressListener progressListener) throws IOException,
			DocumentImportException {

		try {
			String ab1File = file.getCanonicalPath();
			logger.info("ab1File: " + ab1File);

			progressListener.setMessage("Importing sequence data");

			List<AnnotatedPluginDocument> docs = PluginUtilities
					.importDocuments(new File(ab1File), ProgressListener.EMPTY);

			/*
			 * try { sequence = (DefaultNucleotideSequence) docs.get(0)
			 * .getDocument(); } catch (DocumentOperationException e1) {
			 * e1.printStackTrace(); }
			 * 
			 * DefaultNucleotideSequence nucleotideSequenceDocument = new
			 * DefaultNucleotideSequence( file.getName(), "",
			 * sequence.getSequenceString(), new Date( file.lastModified()));
			 */

			// importCallback.addDocument(nucleotideSequenceDocument);

			// DocumentUtilities.addGeneratedDocuments(docs, true);
			importCallback.addDocument(docs.iterator().next());

			for (int cnt = 0; cnt < docs.size(); cnt++) {
				logger.info("Selected document: " + file.getName());

				if (file.getName() != null) {
					setExtractIDFromAB1FileName(file.getName());
					logger.info("Extract-ID: " + limsAB1Flieds.getExtractID());
					logger.info("PCR plaat-ID: "
							+ limsAB1Flieds.getPcrPlaatID());
					logger.info("Mark: " + limsAB1Flieds.getMarker());

					/** set note for Extract-ID */

					if (annotatedPluginDocuments != null) {
						/*
						 * limsNotes.setNoteToAB1FileName(annotatedPluginDocuments
						 * , "ExtractIdCode", "Extract ID", "Extract-ID",
						 * limsAB1Flieds.getExtractID(), cnt); -/ // ** set note
						 * for PCR Plaat-ID
						 */
						/*
						 * limsNotes.setNoteToAB1FileName(annotatedPluginDocuments
						 * , "PcrPlaatIdCode", "PCR plaat ID", "PCR plaat ID",
						 * limsAB1Flieds.getPcrPlaatID(), cnt);
						 *//** set note for Marker */
						/*
						 * limsNotes.setNoteToAB1FileName(annotatedPluginDocuments
						 * , "MarkerCode", "Marker", "Marker",
						 * limsAB1Flieds.getMarker(), cnt);
						 */
					}
				}
			}
		} catch (FileNotFoundException ex) {
			throw new DocumentImportException("File not found: "
					+ file.getName(), ex);
		}
	}

	@Override
	public AutoDetectStatus tentativeAutoDetect(File file,
			String fileContentsStart) {
		return AutoDetectStatus.ACCEPT_FILE;
	}

	private void setExtractIDFromAB1FileName(String fileName) {
		/* for example: e4010125015_Sil_tri_MJ243_COI-A01_M13F_A01_008.ab1 */
		String[] underscore = StringUtils.split(fileName, "_");
		limsAB1Flieds.setExtractID(underscore[0]);
		limsAB1Flieds.setPcrPlaatID(underscore[3]);
		limsAB1Flieds.setMarker(underscore[4]);
	}

}
