/**
 * 
 */
package nl.naturalis.lims2.ab1.importer;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jebl.util.ProgressListener;
import nl.naturalis.lims2.utils.LimsAB1Fields;
import nl.naturalis.lims2.utils.LimsImporterUtil;
import nl.naturalis.lims2.utils.LimsLogger;
import nl.naturalis.lims2.utils.LimsNotes;
import nl.naturalis.lims2.utils.LimsReadGeneiousFieldsValues;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biomatters.geneious.publicapi.databaseservice.QueryField;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentField;
import com.biomatters.geneious.publicapi.plugin.DocumentFileImporter;
import com.biomatters.geneious.publicapi.plugin.DocumentImportException;
import com.biomatters.geneious.publicapi.plugin.PluginUtilities;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsImportAB1 extends DocumentFileImporter {

	private LimsAB1Fields limsAB1Fields = new LimsAB1Fields();
	private LimsNotes limsNotes = new LimsNotes();
	private LimsImporterUtil limsImporterUtil = new LimsImporterUtil();
	private LimsReadGeneiousFieldsValues ReadGeneiousFieldsValues = new LimsReadGeneiousFieldsValues();
	private LimsCRSFields LimsCRSFields = new LimsCRSFields();
	private LimsFileSelector fileselector = new LimsFileSelector();
	private AnnotatedPluginDocument document;
	private int count = 0;
	private static final Logger logger = LoggerFactory
			.getLogger(LimsImportAB1.class);

	public static List<DocumentField> displayFields;
	public static QueryField[] searchFields;
	private String logFileName = "";
	private boolean fastaFileExists = false;
	private int versienummer = 0;
	private boolean isDeleted = false;
	private String extractAb1FastaFileName = "";
	private List<String> list = new ArrayList<String>();
	private String[] ab1FileName = null;
	private String dummyFilename = "";
	private String annotatedDocumentID = "";

	private int cnt = 0;
	private int selectedTotal = 1;

	public LimsImportAB1() {

	}

	@Override
	public String getFileTypeDescription() {
		return "All Naturalis Files";
	}

	@Override
	public String[] getPermissibleExtensions() {
		return new String[] { "" };
	}

	@Override
	public void importDocuments(File file, ImportCallback importCallback,
			ProgressListener progressListener) throws IOException,
			DocumentImportException {

		/* Get Databasename */
		ReadGeneiousFieldsValues.activeDB = ReadGeneiousFieldsValues
				.getServerDatabaseServiceName();

		if (ReadGeneiousFieldsValues.activeDB != null) {
			/* Get the filename and extract the ID */
			ab1FileName = StringUtils.split(file.getName(), "_");

			/* Check if Dummy file exists in the database */
			dummyFilename = ReadGeneiousFieldsValues
					.getCacheNameFromGeneiousDatabase(ab1FileName[0] + ".dum",
							"//document/hiddenFields/cache_name");

			/* if exists then get the ID from the dummy file */
			annotatedDocumentID = ReadGeneiousFieldsValues
					.getIDFromTableAnnotatedDocument(ab1FileName[0] + ".dum",
							"//document/hiddenFields/cache_name");

			list.clear();
			list.addAll(ReadGeneiousFieldsValues
					.getDummySamplesValues(dummyFilename));

			ArrayList<Integer> listcnt = new ArrayList<Integer>();

			listcnt.add(cnt++);

			boolean ab1fileExists = ReadGeneiousFieldsValues
					.fileNameExistsInGeneiousDatabase(file.getName());

			extractAb1FastaFileName = file.getName();

			if (file.getName().contains("fas")) {
				extractAb1FastaFileName = fileselector.readFastaContent(file,
						extractAb1FastaFileName);
				fastaFileExists = ReadGeneiousFieldsValues
						.checkOfFastaOrAB1Exists(extractAb1FastaFileName,
								"plugin_document_xml",
								"//XMLSerialisableRootElement/name");
				versienummer = ReadGeneiousFieldsValues
						.getLastVersionFromDocument(extractAb1FastaFileName);
			} else {
				versienummer = ReadGeneiousFieldsValues
						.getLastVersionFromDocument(file.getName());
			}
			progressListener.setMessage("Importing sequence data");

			List<AnnotatedPluginDocument> docs = PluginUtilities
					.importDocuments(file, ProgressListener.EMPTY);

			progressListener.setProgress(0, 10);

			count += docs.size();

			document = importCallback.addDocument(docs.listIterator().next());

			if (file.getName() != null && list.size() == 0 && !isDeleted) {

				limsAB1Fields
						.setFieldValuesFromAB1FileName(extractAb1FastaFileName);

				logger.info("----------------------------S T A R T ---------------------------------");
				logger.info("Start extracting value from file: "
						+ file.getName());

				/* set note for Extract-ID */
				try {
					limsNotes.setImportNotes(document, "ExtractIDCode_Seq",
							"Extract ID (Seq)", "Extract ID (Seq)",
							limsAB1Fields.getExtractID());
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				/* set note for PCR Plaat-ID */
				try {
					limsNotes.setImportNotes(document, "PCRplateIDCode_Seq",
							"PCR plate ID (Seq)", "PCR plate ID (Seq)",
							limsAB1Fields.getPcrPlaatID());
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				/* set note for Marker */
				try {
					limsNotes.setImportNotes(document, "MarkerCode_Seq",
							"Marker (Seq)", "Marker (Seq)",
							limsAB1Fields.getMarker());
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				if (ab1fileExists) {
					versienummer++;
				} else if (fastaFileExists) {
					versienummer++;
				} else {
					versienummer = 1;
				}

				/* set note for Marker */
				try {
					limsNotes.setImportNotes(document,
							"DocumentVersionCode_Seq", "Document version",
							"Document version", Integer.toString(versienummer));
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				/* set note for SequencingStaffCode_FixedValue_Seq */
				try {
					limsNotes.setImportNotes(document,
							"SequencingStaffCode_FixedValue_Seq",
							"Seq-staff (Seq)", "Seq-staff (Seq)",
							limsImporterUtil.getPropValues("seqsequencestaff"));
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				limsNotes.setImportConsensusSeqPassNotes(document,
						limsNotes.ConsensusSeqPass, "ConsensusSeqPassCode_Seq",
						"Pass (Seq)", "Pass (Seq)", null);
			}

			else {

				limsAB1Fields
						.setFieldValuesFromAB1FileName(extractAb1FastaFileName);

				String extractid = ReadGeneiousFieldsValues.extractidSamplesFromDummy;

				if (limsAB1Fields.getExtractID().equals(extractid)) {

					/* set note for PCR Plaat-ID */
					try {
						limsNotes.setImportNotes(document,
								"PCRplateIDCode_Seq", "PCR plate ID (Seq)",
								"PCR plate ID (Seq)",
								limsAB1Fields.getPcrPlaatID());
					} catch (Exception ex) {
						ex.printStackTrace();
					}

					/* set note for Marker */
					try {
						limsNotes.setImportNotes(document, "MarkerCode_Seq",
								"Marker (Seq)", "Marker (Seq)",
								limsAB1Fields.getMarker());
					} catch (Exception ex) {
						ex.printStackTrace();
					}

					/* set note for Extract-ID */
					try {
						limsNotes.setImportNotes(document, "ExtractIDCode_Seq",
								"Extract ID (Seq)", "Extract ID (Seq)",
								limsAB1Fields.getExtractID());
					} catch (Exception ex) {
						ex.printStackTrace();
					}

					/** set note for Extract-ID */
					limsNotes.setImportNotes(document, "ExtractIDCode_Samples",
							"Extract ID (Samples)", "Extract ID (Samples)",
							ReadGeneiousFieldsValues.extractidSamplesFromDummy);

					/** set note for Project Plate number */
					limsNotes
							.setImportNotes(
									document,
									"ProjectPlateNumberCode_Samples",
									"Sample plate ID (Samples)",
									"Sample plate ID (Samples)",
									ReadGeneiousFieldsValues.samplePlateIdSamplesFromDummy);

					/** set note for Taxon name */
					limsNotes
							.setImportNotes(
									document,
									"TaxonName2Code_Samples",
									"[Scientific name] (Samples)",
									"[Scientific name] (Samples)",
									ReadGeneiousFieldsValues.scientificNameSamplesFromDummy);

					/** set note for Registration number */
					limsNotes
							.setImportNotes(
									document,
									"RegistrationNumberCode_Samples",
									"Registr-nmbr (Samples)",
									"Registr-nmbr (Samples)",
									ReadGeneiousFieldsValues.registrnmbrSamplesFromDummy);

					/** set note for Plate position */
					limsNotes.setImportNotes(document,
							"PlatePositionCode_Samples", "Position (Samples)",
							"Position (Samples)",
							ReadGeneiousFieldsValues.positionSamplesFromDummy);

					/** SequencingStaffCode_FixedValue_Seq */
					try {
						limsNotes.setImportNotes(document,
								"SequencingStaffCode_FixedValue_Seq",
								"Seq-staff (Seq)", "Seq-staff (Seq)",
								limsImporterUtil
										.getPropValues("seqsequencestaff"));
					} catch (IOException e) {
						e.printStackTrace();
					}

					/** AmplicificationStaffCode_FixedValue_Samples */
					try {
						limsNotes
								.setImportNotes(
										document,
										"AmplicificationStaffCode_FixedValue_Samples",
										"Ampl-staff (Samples)",
										"Ampl-staff (Samples)",
										limsImporterUtil
												.getPropValues("samplesamplicification"));
					} catch (IOException e) {
						e.printStackTrace();
					}

					/** set note for Extract Plate ID Samples */
					limsNotes.setImportNotes(document,
							"ExtractPlateNumberCode_Samples",
							"Extract plate ID (Samples)",
							"Extract plate ID (Samples)",
							ReadGeneiousFieldsValues.extractPlateIDSamples);

					/** set note for Extract Method */
					limsNotes.setImportNotes(document,
							"SampleMethodCode_Samples",
							"Extraction method (Samples)",
							"Extraction method (Samples)",
							ReadGeneiousFieldsValues.extractionMethodSamples);

					/**
					 * set note for
					 * RegistrationNumberCode_TaxonName2Code_Samples
					 */
					limsNotes
							.setImportNotes(
									document,
									"RegistrationNumberCode_TaxonName2Code_Samples",
									"Registr-nmbr_[Scientific name] (Samples)",
									"Registr-nmbr_[Scientific name] (Samples)",
									ReadGeneiousFieldsValues.registrationScientificName);

					if (ab1fileExists) {
						versienummer++;
					} else if (fastaFileExists) {
						versienummer++;
					} else {
						versienummer = 1;
					}
					/* set note for Version */
					try {
						limsNotes.setImportNotes(document,
								"DocumentVersionCode_Seq", "Document version",
								"Document version",
								Integer.toString(versienummer));
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					if (count > 0) {
						selectedTotal = 0;
					}

					// setCRSNotes(document);
				}
			}
			logger.info("Total of document(s) filename extracted: " + count);
			logger.info("----------------------------E N D ---------------------------------");
			logger.info("Done with extracting/imported Ab1 files. ");
			isDeleted = false;

			if (selectedTotal == 0) {
				EventQueue.invokeLater(new Runnable() {

					@Override
					public void run() {
						if (dummyFilename.equals(ab1FileName[0] + ".dum")) {
							try {
								/* Get Databasename */
								/*
								 * ReadGeneiousFieldsValues.activeDB =
								 * ReadGeneiousFieldsValues
								 * .getServerDatabaseServiceName();
								 */
								ReadGeneiousFieldsValues
										.DeleteDummyRecordFromTableAnnotatedtDocument(annotatedDocumentID);
							} catch (IOException e) {
								e.printStackTrace();
							}
							isDeleted = true;
							logger.info("Filename: "
									+ ab1FileName[0]
									+ ".dum has been deleted from table annotated_document");
						}
					}
				});
			}
		}
	}

	@Override
	public AutoDetectStatus tentativeAutoDetect(File file,
			String fileContentsStart) {
		if (fileContentsStart.startsWith(">")) {
			return AutoDetectStatus.MAYBE;
		} else {
			return AutoDetectStatus.ACCEPT_FILE;
		}

	}

	@SuppressWarnings("unused")
	private void createLogFile(String fileName, List<String> list) {
		logFileName = limsImporterUtil.getLogPath() + File.separator + fileName
				+ limsImporterUtil.getLogFilename();
		LimsLogger limsLogger = new LimsLogger(logFileName);
		limsLogger.logToFile(logFileName, list.toString());
	}

	/*
	 * private void setCRSNotes(AnnotatedPluginDocument documents) {
	 *//** set note for Phylum: FieldValue, Label, NoteType, */
	/*
	 * limsNotes.setImportNotes(documents, "PhylumCode_CRS", "Phylum (CRS)",
	 * "Phylum (CRS)", LimsCRSFields.getPhylum().trim());
	 *//** Set note for Class */
	/*
	 * limsNotes.setImportNotes(documents, "ClassCode_CRS", "Class (CRS)",
	 * "Class (CRS)", LimsCRSFields.getKlasse().trim());
	 *//** set note for Order */
	/*
	 * limsNotes.setImportNotes(documents, "OrderCode_CRS", "Order (CRS)",
	 * "Order (CRS)", LimsCRSFields.getOrder().trim());
	 * 
	 * set note for Family limsNotes.setImportNotes(documents, "FamilyCode_CRS",
	 * "Family (CRS)", "Family (CRS)", LimsCRSFields.getFamily().trim());
	 *//** set note for SubFamily */
	/*
	 * limsNotes.setImportNotes(documents, "SubFamilyCode_CRS",
	 * "Subfamily (CRS)", "Subfamily (CRS)", LimsCRSFields
	 * .getSubFamily().trim());
	 *//** set note for Genus */
	/*
	 * limsNotes.setImportNotes(documents, "GenusCode_CRS", "Genus (CRS)",
	 * "Genus (CRS)", LimsCRSFields.getGenus().trim());
	 *//** set note for TaxonName */
	/*
	 * limsNotes.setImportNotes(documents, "TaxonName1Code_CRS",
	 * "Scientific name (CRS)", "Scientific name (CRS)", LimsCRSFields
	 * .getTaxon().trim());
	 *//** set note for Identifier */
	/*
	 * limsNotes.setImportNotes(documents, "IdentifierCode_CRS",
	 * "Identifier (CRS)", "Identifier (CRS)", LimsCRSFields
	 * .getDeterminator().trim());
	 *//** set note for Sex */
	/*
	 * limsNotes.setImportNotes(documents, "SexCode_CRS", "Sex (CRS)",
	 * "Sex (CRS)", LimsCRSFields.getSex().trim());
	 *//** set note for Phase Or Stage */
	/*
	 * limsNotes .setImportNotes(documents, "PhaseOrStageCode_CRS",
	 * "Stage (CRS)", "Stage (CRS)", LimsCRSFields .getStadium().trim());
	 *//** set note for Collector */
	/*
	 * limsNotes.setImportNotes(documents, "CollectorCode_CRS", "Leg (CRS)",
	 * "Leg (CRS)", LimsCRSFields.getLegavit().trim());
	 *//** set note for Collecting date */
	/*
	 * if (LimsCRSFields.getCollectingDate().length() > 0) {
	 * limsNotes.setImportNotes(documents, "CollectingDateCode_CRS",
	 * "Date (CRS)", "Date (CRS)", LimsCRSFields.getCollectingDate()); }
	 *//** set note for Country */
	/*
	 * limsNotes.setImportNotes(documents, "CountryCode_CRS", "Country (CRS)",
	 * "Country (CRS)", LimsCRSFields.getCountry().trim());
	 *//** set note for BioRegion */
	/*
	 * limsNotes.setImportNotes(documents, "StateOrProvinceBioRegionCode_CRS",
	 * "Region (CRS)", "Region (CRS)", LimsCRSFields.getBioRegion() .trim());
	 *//** set note for Locality */
	/*
	 * limsNotes.setImportNotes(documents, "LocalityCode_CRS", "Locality (CRS)",
	 * "Locality (CRS)", LimsCRSFields.getLocality() .trim());
	 *//** set note for Latitude */
	/*
	 * limsNotes.setImportNotes(documents, "LatitudeDecimalCode_CRS",
	 * "Lat (CRS)", "Lat (CRS)", LimsCRSFields.getLatitudeDecimal());
	 *//** set note for Longitude */
	/*
	 * limsNotes .setImportNotes(documents, "LongitudeDecimalCode_CRS",
	 * "Long (CRS)", "Long (CRS)", LimsCRSFields.getLongitudeDecimal());
	 *//** set note for Height */
	/*
	 * limsNotes.setImportNotes(documents, "HeightCode_CRS", "Altitude (CRS)",
	 * "Altitude (CRS)", LimsCRSFields.getHeight());
	 * 
	 * // limsNotes.setImportTrueFalseNotes(documents, "CRSCode_CRS", //
	 * "CRS (CRS)", "CRS (CRS)", true); }
	 */

}
