/**
 * 
 */
package nl.naturalis.lims2.ab1.importer;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import nl.naturalis.lims2.utils.LimsFrameProgress;
import nl.naturalis.lims2.utils.LimsImporterUtil;
import nl.naturalis.lims2.utils.LimsLogger;
import nl.naturalis.lims2.utils.LimsNotes;
import nl.naturalis.lims2.utils.LimsReadGeneiousFieldsValues;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biomatters.geneious.publicapi.components.Dialogs;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.documents.PluginDocument;
import com.biomatters.geneious.publicapi.documents.sequence.SequenceDocument;
import com.biomatters.geneious.publicapi.plugin.DocumentAction;
import com.biomatters.geneious.publicapi.plugin.DocumentOperationException;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;
import com.opencsv.CSVReader;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsReadDataFromSamples extends DocumentAction {

	private List<AnnotatedPluginDocument> docs;
	private LimsImporterUtil limsImporterUtil = new LimsImporterUtil();
	private LimsSamplesFields limsExcelFields = new LimsSamplesFields();
	private LimsNotes limsNotes = new LimsNotes();
	private LimsFileSelector fcd = new LimsFileSelector();
	private LimsReadGeneiousFieldsValues ReadGeneiousFieldsValues = new LimsReadGeneiousFieldsValues();
	private LimsDummySeq limsDummySeq = new LimsDummySeq();

	private String extractIDfileName = "";
	private SequenceDocument seq;
	private List<String> msgList = new ArrayList<String>();
	private List<String> msgUitvalList = new ArrayList<String>();
	private List<String> verwerkingListCnt = new ArrayList<String>();
	private List<String> verwerkList = new ArrayList<String>();
	private static final Logger logger = LoggerFactory
			.getLogger(LimsReadDataFromSamples.class);
	private Object documentFileName = "";
	private Object result = "";

	public int importCounter;
	private int importTotal;
	private String[] record = null;
	private String ID = "";
	private String fileSelected = "";
	private final String noteCode = "DocumentNoteUtilities-Extract ID (Seq)";
	private final String fieldName = "ExtractIDCode_Seq";
	private JFrame frame = new JFrame();

	String logFileName = limsImporterUtil.getLogPath() + File.separator
			+ "Sample-method-Uitvallijst-" + limsImporterUtil.getLogFilename();

	LimsLogger limsLogger = new LimsLogger(logFileName);
	LimsFrameProgress limsFrameProgress = new LimsFrameProgress();

	// JProgressBar progressBar = new JProgressBar();
	//
	// static final int MY_MINIMUM = 0;
	// static final int MY_MAXIMUM = 100;
	// final LimsProgressBar it = new LimsProgressBar();
	// JLabel jl = new JLabel();

	public LimsReadDataFromSamples() {

	}

	@Override
	public void actionPerformed(
			AnnotatedPluginDocument[] annotatedPluginDocuments) {

		performOperation(annotatedPluginDocuments);
	}

	// public void createProgressBar() {
	// JFrame frame = new JFrame("Reading records from files");
	// frame.setSize(280, 100);
	// frame.isAlwaysOnTop();
	// frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	// frame.setContentPane(it);
	// jl.setText("0%");
	// frame.add(BorderLayout.CENTER, jl);
	// // frame.pack();
	//
	// frame.setVisible(true);
	// }

	public void performOperation(AnnotatedPluginDocument[] documents) {

		Object[] options = { "Create Dummy", "Read Samples", "Cancel" };
		int n = JOptionPane.showOptionDialog(frame,
				"Choose one option to start Samples import", "Samples",
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
				null, options, options[2]);
		if (n == 0) {
			limsFrameProgress.createProgressBar();
			// createProgressBar();
			fileSelected = fcd.loadSelectedFile();
			setExtractIDFromSamplesSheet(fileSelected);
			limsFrameProgress.hideFrame();
			// frame.setVisible(false);

		} else if (n == 1) {
			if (DocumentUtilities.getSelectedDocuments().isEmpty()) {
				EventQueue.invokeLater(new Runnable() {

					@Override
					public void run() {

						Dialogs.showMessageDialog("Select all documents");
						return;
					}
				});
			}
			if (!DocumentUtilities.getSelectedDocuments().isEmpty()) {
				logger.info("Start updating selected document(s).");
				fileSelected = fcd.loadSelectedFile();
				try {
					/** Add selected documents to a list. */
					docs = DocumentUtilities.getSelectedDocuments();

					if (fileSelected == null) {
						return;
					}

					msgUitvalList.add("Filename: " + fileSelected + "\n");

					for (int cnt = 0; cnt < docs.size(); cnt++) {

						logger.info("-------------------------- S T A R T --------------------------");
						logger.info("Start Reading data from a samples file.");

						seq = (SequenceDocument) docs.get(cnt).getDocument();

						documentFileName = documents[cnt]
								.getFieldValue("cache_name");

						/* Get file name from the document(s) */
						if ((documentFileName.toString().contains("dum"))
								|| (documentFileName.toString().contains("ab1"))) {

						} else {
							result = ReadGeneiousFieldsValues
									.readValueFromAnnotatedPluginDocument(docs
											.iterator().next(), "importedFrom",
											"filename");
						}

						/* Check of the filename contain "FAS" extension */
						if (result.toString().contains("fas")) {
							extractIDfileName = getExtractIDFromAB1FileName((String) documentFileName);
						} else {
							/* get AB1 filename */
							extractIDfileName = getExtractIDFromAB1FileName(seq
									.getName());
						}
						msgList.add(extractIDfileName);

						limsFrameProgress.createProgressBar();
						setSamplesNotes(documents, cnt);
						limsFrameProgress.hideFrame();

						logger.info("Done with adding notes to the document");
						importCounter = msgList.size();
					}
				} catch (DocumentOperationException e) {
					e.printStackTrace();
				}
				logger.info("--------------------------------------------------------");
				logger.info("Total of document(s) updated: " + docs.size());

				/* Set for creating dummy files */
				limsFrameProgress.createProgressBar();
				setExtractIDFromSamplesSheet(fileSelected);
				limsFrameProgress.hideFrame();

				// }

				logger.info("-------------------------- E N D --------------------------");
				logger.info("Done with updating the selected document(s). ");

				EventQueue.invokeLater(new Runnable() {

					@Override
					public void run() {
						Dialogs.showMessageDialog("Sample-method: "
								+ Integer.toString(docs.size()) + " out of "
								+ Integer.toString(importTotal)
								+ " documents are imported." + "\n"
								+ msgList.toString());
						logger.info("Sample-method: Total imported document(s): "
								+ msgList.toString());

						limsLogger.logToFile(logFileName,
								msgUitvalList.toString());

						msgList.clear();
						msgUitvalList.clear();
						verwerkingListCnt.clear();
						verwerkList.clear();
						frame.setVisible(false);
					}
				});

			}
		} else if (n == 2) {
			System.out.println("Optie 2");
			return;
		}
	}

	private void setSamplesNotes(AnnotatedPluginDocument[] documents, int cnt) {

		// for (int i = MY_MINIMUM; i <= MY_MAXIMUM; i++) {
		// final int percent = i;
		// try {
		// SwingUtilities.invokeLater(new Runnable() {
		// public void run() {
		// it.updateBar(percent);
		// jl.setText(percent + "%");
		// }
		// });
		// java.lang.Thread.sleep(3);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// }
		limsFrameProgress.showProgress();
		readDataFromExcel(fileSelected);
		/** set note for Registration number */
		limsNotes.setNoteToAB1FileName(documents,
				"RegistrationNumberCode_Samples", "Registr-nmbr (Samples)",
				"Registr-nmbr (Samples)",
				limsExcelFields.getRegistrationNumber(), cnt);

		/** set note for Taxonname */
		limsNotes.setNoteToAB1FileName(documents, "TaxonName2Code_Samples",
				"[Scientific name] (Samples)", "[Scientific name] (Samples)",
				limsExcelFields.getTaxonNaam(), cnt);

		/** set note for Project Plate number */
		limsNotes.setNoteToAB1FileName(documents,
				"ProjectPlateNumberCode_Samples", "Sample plate ID (Samples)",
				"Sample plate ID (Samples)",
				limsExcelFields.getProjectPlaatNummer(), cnt);

		/** Set note for Extract plate number */
		limsNotes.setNoteToAB1FileName(documents,
				"ExtractPlateNumberCode_Samples", "Extract plate ID (Samples)",
				"Extract plate ID (Samples)",
				limsExcelFields.getExtractPlaatNummer(), cnt);

		/** set note for Plate position */
		limsNotes.setNoteToAB1FileName(documents, "PlatePositionCode_Samples",
				"Position (Samples)", "Position (Samples)",
				limsExcelFields.getPlaatPositie(), cnt);

		/** set note for Extract-ID */
		limsNotes.setNoteToAB1FileName(documents, "ExtractIDCode_Samples",
				"Extract ID (Samples)", "Extract ID (Samples)",
				limsExcelFields.getExtractID(), cnt);

		/** set note for Sample method */
		limsNotes.setNoteToAB1FileName(documents, "SampleMethodCode_Samples",
				"Extraction method (Samples)", "Extraction method (Samples)",
				limsExcelFields.getSubSample(), cnt);

		limsNotes.setNoteToAB1FileName(documents, "DocumentVersionCode_Seq",
				"Document version", "Document version",
				limsExcelFields.getVersieNummer(), cnt);

		/** SequencingStaffCode_FixedValue */
		try {
			limsNotes
					.setNoteToAB1FileName(documents,
							"SequencingStaffCode_FixedValue_Samples",
							"Seq-staff (Samples)", "Seq-staff (Samples)",
							limsImporterUtil
									.getPropValues("samplessequencestaff"), cnt);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public GeneiousActionOptions getActionOptions() {
		return new GeneiousActionOptions("1 of 2 Samples").setInPopupMenu(true)
				.setMainMenuLocation(GeneiousActionOptions.MainMenu.Tools, 1.0)
				.setInMainToolbar(true).setInPopupMenu(true)
				.setAvailableToWorkflows(true);
	}

	@Override
	public String getHelp() {
		return null;
	}

	@Override
	public DocumentSelectionSignature[] getSelectionSignatures() {
		return new DocumentSelectionSignature[] { new DocumentSelectionSignature(
				PluginDocument.class, 0, Integer.MAX_VALUE) };
	}

	/*
	 * Create dummy files for samples when there is no match with records in the
	 * database
	 */
	private void setExtractIDFromSamplesSheet(String fileName) {
		try {
			if (fileName != null) {
				logger.info("Read samples file: " + fileName);
				CSVReader csvReader = new CSVReader(new FileReader(fileName),
						'\t', '\'', 0);
				csvReader.readNext();

				int cnt = 0;
				try {
					while ((record = csvReader.readNext()) != null) {
						if (record.length == 0) {
							continue;
						}
						limsFrameProgress.showProgress();

						ID = "e" + record[3];
						String plateNumber = record[2].substring(0,
								record[2].indexOf("-"));

						String dummyFile = ReadGeneiousFieldsValues
								.getFastaIDForSamples_GeneiousDB(ID);
						if (dummyFile.trim() != "") {
							dummyFile = getExtractIDFromAB1FileName(dummyFile);
						}
						if (!dummyFile.equals(ID)) {
							limsDummySeq.createDummySampleSequence(ID, ID,
									record[0], plateNumber, record[5],
									record[4], record[1]);
						}

						// for (int i = MY_MINIMUM; i <= MY_MAXIMUM; i++) {
						// final int percent = i;
						// try {
						// SwingUtilities.invokeLater(new Runnable() {
						// public void run() {
						// it.updateBar(percent);
						// jl.setText(percent + "%");
						// }
						// });
						// java.lang.Thread.sleep(3);
						// } catch (InterruptedException e) {
						// e.printStackTrace();
						// }
						// }
						cnt++;
					} // end While
					Dialogs.showMessageDialog("Done creating:" + cnt
							+ " Dummy Samples");
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					csvReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void readDataFromExcel(String fileName) {

		int counter = 0;
		int cntVerwerkt = 0;

		logger.info("CSV file: " + fileName);

		logger.info("Start with adding notes to the document");
		try {
			CSVReader csvReader = new CSVReader(new FileReader(fileName), '\t',
					'\'', 0);
			csvReader.readNext();

			try {
				msgUitvalList
						.add("-----------------------------------------------"
								+ "\n");
				msgUitvalList.add("Ab1 filename: " + seq.getName() + "\n");

				while ((record = csvReader.readNext()) != null) {
					if (record.length == 0) {
						continue;
					}

					ID = "e" + record[3];
					String plateNumber = record[2].substring(0,
							record[2].indexOf("-"));

					if (ID.equals(extractIDfileName)) {
						limsExcelFields.setProjectPlaatNummer(record[0]);
						limsExcelFields.setPlaatPositie(record[1]);
						limsExcelFields.setExtractPlaatNummer(plateNumber);
						if (record[3] != null) {
							limsExcelFields.setExtractID(ID);
						}
						limsExcelFields.setRegistrationNumber(record[4]);
						limsExcelFields.setTaxonNaam(record[5]);

						logger.info("Extract-ID: "
								+ limsExcelFields.getExtractID());
						logger.info("Project plaatnummer: "
								+ limsExcelFields.getProjectPlaatNummer());
						logger.info("Extract plaatnummer: "
								+ limsExcelFields.getExtractPlaatNummer());
						logger.info("Taxon naam: "
								+ limsExcelFields.getTaxonNaam());
						logger.info("Registrationnumber: "
								+ limsExcelFields.getRegistrationNumber());
						logger.info("Plaat positie: "
								+ limsExcelFields.getPlaatPositie());
						logger.info("Sample method: "
								+ limsExcelFields.getSubSample());
						counter--;
						cntVerwerkt++;
						verwerkingListCnt.add(Integer.toString(cntVerwerkt));
						verwerkList.add(ID);

					} // end IF

					if (!verwerkList.contains(ID)) {
						msgUitvalList.add("Record ExtractID: " + record[3]
								+ "\n");
					}
					counter++;
				} // end While
				importTotal = counter;
				counter = counter - verwerkingListCnt.size();
				msgUitvalList.add("Total records: " + Integer.toString(counter)
						+ "\n");

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

	/**
	 * Extract the ID from the filename
	 * 
	 * @param annotatedPluginDocuments
	 *            set the param
	 * @return
	 */
	private String getExtractIDFromAB1FileName(String fileName) {
		/* for example: e4010125015_Sil_tri_MJ243_COI-A01_M13F_A01_008.ab1 */
		String[] underscore = null;
		logger.info("Document Filename: " + fileName);
		if (fileName.contains("_") && fileName.contains("ab1")) {
			underscore = StringUtils.split(fileName, "_");
		} else if (fileName.contains("_") && !fileName.contains(".")) {
			underscore = StringUtils.split(fileName, "_");
		} else if (fileName.contains(".") && fileName.contains("dum")) {
			underscore = StringUtils.split(fileName, ".");
		}
		return underscore[0];
	}

	private boolean matchExtractId(
			AnnotatedPluginDocument annotatedPluginDocument, String extractID) {

		Object fieldValue = ReadGeneiousFieldsValues
				.readValueFromAnnotatedPluginDocument(annotatedPluginDocument,
						noteCode, fieldName);
		if (extractID.equals(fieldValue)) {
			return true;
		}
		return false;
	}
}
