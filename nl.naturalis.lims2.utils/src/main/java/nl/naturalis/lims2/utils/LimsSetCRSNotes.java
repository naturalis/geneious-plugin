/**
 * 
 */
package nl.naturalis.lims2.utils;

import java.util.ArrayList;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.Constraint;
import com.biomatters.geneious.publicapi.documents.DocumentNote;
import com.biomatters.geneious.publicapi.documents.DocumentNoteField;
import com.biomatters.geneious.publicapi.documents.DocumentNoteType;
import com.biomatters.geneious.publicapi.documents.DocumentNoteUtilities;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsSetCRSNotes {

	private static final Logger logger = LoggerFactory
			.getLogger(LimsSetCRSNotes.class);
	private LimsImporterUtil limsImporterUtil = new LimsImporterUtil();

	/* PHYLUM */
	private String fieldPhylum;
	private String noteTypeCodePhylum;
	private String descriptionPhylum;
	private String noteTextPhylum = "Phylum (CRS)";

	/* Class */
	private String fieldClass;
	private String noteTypeClass;
	private String descriptionClass;
	private String noteTextClass = "Class (CRS)";

	/* Order */
	private String fieldOrder;
	private String noteTypeOrder;
	private String descriptionOrder;
	private String noteTextOrder = "Order (CRS)";

	/* Family */
	private String fieldFamily;
	private String noteTypeFamily;
	private String descriptionFamily;
	private String noteTextFamily = "Family (CRS)";

	/* Subfamily */
	private String fieldSubfamily;
	private String noteTypeSubfamily;
	private String descriptionSubfamily;
	private String noteTextSubfamily = "Subfamily (CRS)";

	/* Genus */
	private String fieldGenus;
	private String noteTypeGenus;
	private String descriptionGenus;
	private String noteTextGenus = "Genus (CRS)";

	/* Scientific name (TaxonName) */
	private String fieldScientificname;
	private String noteTypeScientificname;
	private String descriptionScientificname;
	private String noteTextScientificname = "Scientific name (CRS)";

	/* Identifier */
	private String fieldIdentifier;
	private String noteTypeIdentifier;
	private String descriptionIdentifier;
	private String noteTextIdentifier = "Identifier (CRS)";

	/* Sex */
	private String fieldSex;
	private String noteTypeSex;
	private String descriptionSex;
	private String noteTextSex = "Sex (CRS)";

	/* Phase Or Stage */
	private String fieldStage;
	private String noteTypeStage;
	private String descriptionStage;
	private String noteTextStage = "Stage (CRS)";

	/* Collector */
	private String fieldLeg;
	private String noteTypeLeg;
	private String descriptionLeg;
	private String noteTextLeg = "Leg (CRS)";

	/* Collecting date */
	private String fieldDate;
	private String noteTypeDate;
	private String descriptionDate;
	private String noteTextDate = "Date (CRS)";

	/* Country */
	private String fieldCountry;
	private String noteTypeCountry;
	private String descriptionCountry;
	private String noteTextCountry = "Country (CRS)";

	/* BioRegion */
	private String fieldRegion;
	private String noteTypeRegion;
	private String descriptionRegion;
	private String noteTextRegion = "Region (CRS)";

	/* Locality */
	private String fieldLocality;
	private String noteTypeLocality;
	private String descriptionLocality;
	private String noteTextLocality = "Locality (CRS)";

	/* Latitude */
	private String fieldLat;
	private String noteTypeLat;
	private String descriptionLat;
	private String noteTextLat = "Lat (CRS)";

	/* Longtitude */
	private String fieldLong;
	private String noteTypeLong;
	private String descriptionLong;
	private String noteTextLong = "Long (CRS)";

	/* Altitude Height */
	private String fieldAltitude;
	private String noteTypeAltitude;
	private String descriptionAltitude;
	private String noteTextAltitude = "Altitude (CRS)";

	/* True False for CRS */
	private String fieldCRS;
	private String noteTypeCRS;
	private String descriptionCRS;
	private String noteTextCRS = "CRS (CRS)";

	ArrayList<DocumentNoteField> listNotes = new ArrayList<DocumentNoteField>();

	public void setImportNotes(AnnotatedPluginDocument document,
			String fileName, String phylum, String classes, String order,
			String family, String subFamily, String genus,
			String scientificName, String identifier, String sex, String stage,
			String collectorLeg, String collectingDate, String country,
			String bioRegion, String locality, String latitude,
			String longtitude, String heightAlitude) {
		logger.info("----------------------------S T A R T ---------------------------------");
		logger.info("Start extracting value from file: " + fileName);

		setFieldAndDescriptionValues();

		/* ============================================================ */

		addCRSNotesToList();

		/* ============================================================ */

		/* Phylum */
		this.noteTypeCodePhylum = "DocumentNoteUtilities-" + noteTextPhylum;
		DocumentNoteType documentNoteTypePhylum = DocumentNoteUtilities
				.getNoteType(this.noteTypeCodePhylum);

		/* Class */
		this.noteTypeClass = "DocumentNoteUtilities-" + noteTextClass;
		DocumentNoteType documentNoteTypeClass = DocumentNoteUtilities
				.getNoteType(this.noteTypeClass);

		/* Order */
		this.noteTypeOrder = "DocumentNoteUtilities-" + noteTextOrder;
		DocumentNoteType documentNoteTypeOrder = DocumentNoteUtilities
				.getNoteType(this.noteTypeOrder);

		/* Family */
		this.noteTypeFamily = "DocumentNoteUtilities-" + noteTextFamily;
		DocumentNoteType documentNoteTypeFamily = DocumentNoteUtilities
				.getNoteType(this.noteTypeFamily);

		/* Subfamily */
		this.noteTypeSubfamily = "DocumentNoteUtilities-" + noteTextSubfamily;
		DocumentNoteType documentNoteTypeSubfamily = DocumentNoteUtilities
				.getNoteType(this.noteTypeSubfamily);

		/* Genus */
		this.noteTypeGenus = "DocumentNoteUtilities-" + noteTextGenus;
		DocumentNoteType documentNoteTypeGenus = DocumentNoteUtilities
				.getNoteType(this.noteTypeGenus);

		/* TaxonName */
		this.noteTypeScientificname = "DocumentNoteUtilities-"
				+ noteTextScientificname;
		DocumentNoteType documentNoteTypeScientificName = DocumentNoteUtilities
				.getNoteType(this.noteTypeScientificname);

		/* Identifier */
		this.noteTypeIdentifier = "DocumentNoteUtilities-" + noteTextIdentifier;
		DocumentNoteType documentNoteTypeIdentifier = DocumentNoteUtilities
				.getNoteType(this.noteTypeIdentifier);

		/* Sex */
		this.noteTypeSex = "DocumentNoteUtilities-" + noteTextSex;
		DocumentNoteType documentNoteTypeSex = DocumentNoteUtilities
				.getNoteType(this.noteTypeSex);

		/* Phase Or Stage */
		this.noteTypeStage = "DocumentNoteUtilities-" + noteTextStage;
		DocumentNoteType documentNoteTypeStage = DocumentNoteUtilities
				.getNoteType(this.noteTypeStage);

		/* Collector */
		this.noteTypeLeg = "DocumentNoteUtilities-" + noteTextLeg;
		DocumentNoteType documentNoteTypeLeg = DocumentNoteUtilities
				.getNoteType(this.noteTypeLeg);

		/* Collecting date */
		this.noteTypeDate = "DocumentNoteUtilities-" + noteTextDate;
		DocumentNoteType documentNoteTypeDate = DocumentNoteUtilities
				.getNoteType(this.noteTypeDate);

		/* Country */
		this.noteTypeCountry = "DocumentNoteUtilities-" + noteTextCountry;
		DocumentNoteType documentNoteTypeCountry = DocumentNoteUtilities
				.getNoteType(this.noteTypeCountry);

		/* BioRegion */
		this.noteTypeRegion = "DocumentNoteUtilities-" + noteTextRegion;
		DocumentNoteType documentNoteTypeRegion = DocumentNoteUtilities
				.getNoteType(this.noteTypeRegion);

		/* Locality */
		this.noteTypeLocality = "DocumentNoteUtilities-" + noteTextLocality;
		DocumentNoteType documentNoteTypeLocality = DocumentNoteUtilities
				.getNoteType(this.noteTypeLocality);

		/* Latitude */
		this.noteTypeLat = "DocumentNoteUtilities-" + noteTextLat;
		DocumentNoteType documentNoteTypeLatitude = DocumentNoteUtilities
				.getNoteType(this.noteTypeLat);

		/* Longtitude */
		this.noteTypeLong = "DocumentNoteUtilities-" + noteTextLong;
		DocumentNoteType documentNoteTypeLongtitude = DocumentNoteUtilities
				.getNoteType(this.noteTypeLong);

		/* Height */
		this.noteTypeAltitude = "DocumentNoteUtilities-" + noteTextAltitude;
		DocumentNoteType documentNoteTypeAltitude = DocumentNoteUtilities
				.getNoteType(this.noteTypeAltitude);

		/* CRS True False */
		this.noteTypeCRS = "DocumentNoteUtilities-" + noteTypeCRS;
		DocumentNoteType documentNoteTypeCRS = DocumentNoteUtilities
				.getNoteType(this.noteTypeCRS);

		/* ================================================================= */

		/* Phylum */
		if (documentNoteTypePhylum == null) {
			documentNoteTypePhylum = DocumentNoteUtilities.createNewNoteType(
					noteTextPhylum, this.noteTypeCodePhylum,
					this.descriptionPhylum, listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypePhylum);
			logger.info("NoteType " + noteTextPhylum + " created succesful");
		}

		/* Class */
		if (documentNoteTypeClass == null) {
			documentNoteTypeClass = DocumentNoteUtilities.createNewNoteType(
					noteTextClass, this.noteTypeClass, this.descriptionClass,
					listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypeClass);
			logger.info("NoteType " + noteTextClass + " created succesful");
		}

		/* Order */
		if (documentNoteTypeOrder == null) {
			documentNoteTypeOrder = DocumentNoteUtilities.createNewNoteType(
					noteTextOrder, this.noteTypeOrder, this.descriptionOrder,
					listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypeOrder);
			logger.info("NoteType " + noteTextOrder + " created succesful");
		}

		/* Family */
		if (documentNoteTypeFamily == null) {
			documentNoteTypeFamily = DocumentNoteUtilities.createNewNoteType(
					noteTextFamily, this.noteTypeFamily,
					this.descriptionFamily, listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypeFamily);
			logger.info("NoteType " + noteTextFamily + " created succesful");
		}

		/* Subfamily */
		if (documentNoteTypeSubfamily == null) {
			documentNoteTypeSubfamily = DocumentNoteUtilities
					.createNewNoteType(noteTextSubfamily,
							this.noteTypeSubfamily, this.descriptionSubfamily,
							listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypeSubfamily);
			logger.info("NoteType " + noteTextSubfamily + " created succesful");
		}

		/* Genus */
		if (documentNoteTypeGenus == null) {
			documentNoteTypeGenus = DocumentNoteUtilities.createNewNoteType(
					noteTextGenus, this.noteTypeGenus, this.descriptionGenus,
					listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypeGenus);
			logger.info("NoteType " + noteTextGenus + " created succesful");
		}

		/* TaxonName */
		if (documentNoteTypeScientificName == null) {
			documentNoteTypeScientificName = DocumentNoteUtilities
					.createNewNoteType(noteTextScientificname,
							this.noteTypeScientificname,
							this.descriptionScientificname, listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypeScientificName);
			logger.info("NoteType " + noteTextScientificname
					+ " created succesful");
		}

		/* Identifier */
		if (documentNoteTypeIdentifier == null) {
			documentNoteTypeIdentifier = DocumentNoteUtilities
					.createNewNoteType(noteTextIdentifier,
							this.noteTypeIdentifier,
							this.descriptionIdentifier, listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypeIdentifier);
			logger.info("NoteType " + noteTextIdentifier + " created succesful");
		}

		/* Sex */
		if (documentNoteTypeSex == null) {
			documentNoteTypeSex = DocumentNoteUtilities.createNewNoteType(
					noteTextSex, this.noteTypeSex, this.descriptionSex,
					listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypeSex);
			logger.info("NoteType " + noteTextSex + " created succesful");
		}

		/* Phase Or Stage */
		if (documentNoteTypeStage == null) {
			documentNoteTypeStage = DocumentNoteUtilities.createNewNoteType(
					noteTextStage, this.noteTypeStage, this.descriptionStage,
					listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypeStage);
			logger.info("NoteType " + noteTextStage + " created succesful");
		}

		/* Collector */
		if (documentNoteTypeLeg == null) {
			documentNoteTypeLeg = DocumentNoteUtilities.createNewNoteType(
					noteTextLeg, this.noteTypeLeg, this.descriptionLeg,
					listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypeLeg);
			logger.info("NoteType " + noteTextLeg + " created succesful");
		}

		/* Collecting Date */
		if (documentNoteTypeDate == null) {
			documentNoteTypeDate = DocumentNoteUtilities.createNewNoteType(
					noteTextDate, this.noteTypeDate, this.descriptionDate,
					listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypeDate);
			logger.info("NoteType " + noteTextDate + " created succesful");
		}

		/* Country */
		if (documentNoteTypeCountry == null) {
			documentNoteTypeCountry = DocumentNoteUtilities.createNewNoteType(
					noteTextCountry, this.noteTypeCountry,
					this.descriptionCountry, listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypeCountry);
			logger.info("NoteType " + noteTextCountry + " created succesful");
		}

		/* BioRegion */
		if (documentNoteTypeRegion == null) {
			documentNoteTypeRegion = DocumentNoteUtilities.createNewNoteType(
					noteTextRegion, this.noteTypeRegion,
					this.descriptionRegion, listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypeRegion);
			logger.info("NoteType " + noteTextRegion + " created succesful");
		}

		/* Locality */
		if (documentNoteTypeLocality == null) {
			documentNoteTypeLocality = DocumentNoteUtilities.createNewNoteType(
					noteTextLocality, this.noteTypeLocality,
					this.descriptionLocality, listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypeLocality);
			logger.info("NoteType " + noteTextLocality + " created succesful");
		}

		/* Latitude */
		if (documentNoteTypeLatitude == null) {
			documentNoteTypeLatitude = DocumentNoteUtilities.createNewNoteType(
					noteTextLat, this.noteTypeLat, this.descriptionLat,
					listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypeLatitude);
			logger.info("NoteType " + noteTextLat + " created succesful");
		}

		/* Longtitude */
		if (documentNoteTypeLongtitude == null) {
			documentNoteTypeLongtitude = DocumentNoteUtilities
					.createNewNoteType(noteTextLong, this.noteTypeLong,
							this.descriptionLong, listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypeLongtitude);
			logger.info("NoteType " + noteTextLong + " created succesful");
		}

		/* Height */
		if (documentNoteTypeAltitude == null) {
			documentNoteTypeAltitude = DocumentNoteUtilities.createNewNoteType(
					noteTextAltitude, this.noteTypeAltitude,
					this.descriptionAltitude, listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypeAltitude);
			logger.info("NoteType " + noteTextAltitude + " created succesful");
		}

		/* CRS True False */
		if (documentNoteTypeCRS == null) {
			documentNoteTypeCRS = DocumentNoteUtilities.createNewNoteType(
					noteTypeCRS, this.noteTypeCRS, this.descriptionCRS,
					listNotes, false);
			DocumentNoteUtilities.setNoteType(documentNoteTypeCRS);
			logger.info("NoteType " + noteTypeCRS + " created succesful");
		}

		/* ============================================================== */

		/* Create note for Phylum */
		DocumentNote documentNotePhylum = documentNoteTypePhylum
				.createDocumentNote();
		documentNotePhylum.setFieldValue(this.fieldPhylum, phylum);
		logger.info("Note value " + this.fieldPhylum + ": " + phylum
				+ " added succesful");

		/* Create note for Class */
		DocumentNote documentNoteClass = documentNoteTypeClass
				.createDocumentNote();
		documentNoteClass.setFieldValue(this.fieldClass, classes);
		logger.info("Note value " + this.fieldPhylum + ": " + classes
				+ " added succesful");

		/* Create note for Order */
		DocumentNote documentNoteOrder = documentNoteTypeOrder
				.createDocumentNote();
		documentNoteOrder.setFieldValue(this.fieldOrder, order);
		logger.info("Note value " + this.fieldOrder + ": " + order
				+ " added succesful");

		/* Create note for Family */
		DocumentNote documentNotefamily = documentNoteTypeFamily
				.createDocumentNote();
		documentNotefamily.setFieldValue(this.fieldFamily, family);
		logger.info("Note value " + this.fieldFamily + ": " + family
				+ " added succesful");

		/* Create note for SubFamily */
		DocumentNote documentNoteSubfamily = documentNoteTypeSubfamily
				.createDocumentNote();
		documentNoteSubfamily.setFieldValue(this.fieldSubfamily, subFamily);
		logger.info("Note value " + this.fieldSubfamily + ": " + subFamily
				+ " added succesful");

		/* Create note for Genus */
		DocumentNote documentNoteGenus = documentNoteTypeGenus
				.createDocumentNote();
		documentNoteGenus.setFieldValue(this.fieldGenus, genus);
		logger.info("Note value " + this.fieldGenus + ": " + genus
				+ " added succesful");

		/* Create note for TaxonName */
		DocumentNote documentNoteTaxonName = documentNoteTypeScientificName
				.createDocumentNote();
		documentNoteTaxonName.setFieldValue(this.fieldScientificname,
				scientificName);
		logger.info("Note value " + this.fieldScientificname + ": "
				+ scientificName + " added succesful");

		/* Create note for Identifier */
		DocumentNote documentNoteIdentifier = documentNoteTypeIdentifier
				.createDocumentNote();
		documentNoteIdentifier.setFieldValue(this.fieldIdentifier, identifier);
		logger.info("Note value " + this.fieldIdentifier + ": " + identifier
				+ " added succesful");

		/* ============================================================ */

	}

	/**
	 * 
	 */
	private void setFieldAndDescriptionValues() {
		/* Phylum */
		this.fieldPhylum = "PhylumCode_CRS";
		this.descriptionPhylum = "Naturalis file " + noteTextPhylum + " note";

		/* Class */
		this.fieldClass = "ClassCode_CRS";
		this.descriptionClass = "Naturalis file " + noteTextClass + " note";

		/* Order */
		this.fieldOrder = "OrderCode_CRS";
		this.descriptionOrder = "Naturalis file " + noteTextOrder + " note";

		/* Family */
		this.fieldFamily = "FamilyCode_CRS";
		this.descriptionFamily = "Naturalis file " + noteTextFamily + " note";

		/* SubFamily */
		this.fieldSubfamily = "SubFamilyCode_CRS";
		this.descriptionSubfamily = "Naturalis file " + noteTextSubfamily
				+ " note";

		/* Genus */
		this.fieldGenus = "GenusCode_CRS";
		this.descriptionGenus = "Naturalis file " + noteTextGenus + " note";

		/* Taxon name */
		this.fieldScientificname = "TaxonName1Code_CRS";
		this.descriptionScientificname = "Naturalis file "
				+ noteTextScientificname + " note";

		/* Identifier */
		this.fieldIdentifier = "IdentifierCode_CRS";
		this.descriptionIdentifier = "Naturalis file " + noteTextIdentifier
				+ " note";

		/* Sex */
		this.fieldSex = "SexCode_CRS";
		this.descriptionSex = "Naturalis file " + noteTextSex + " note";

		/* Phase Or Stage */
		this.fieldStage = "PhaseOrStageCode_CRS";
		this.descriptionStage = "Naturalis file " + noteTextStage + " note";

		/* Collector */
		this.fieldLeg = "CollectorCode_CRS";
		this.descriptionLeg = "Naturalis file " + noteTextLeg + " note";

		/* Collecting date */
		this.fieldDate = "CollectingDateCode_CRS";
		this.descriptionDate = "Naturalis file " + noteTextDate + " note";

		/* Country */
		this.fieldCountry = "CountryCode_CRS";
		this.descriptionCountry = "Naturalis file " + noteTextCountry + " note";

		/* BioRegion */
		this.fieldRegion = "StateOrProvinceBioRegionCode_CRS";
		this.descriptionRegion = "Naturalis file " + noteTextRegion + " note";

		/* Locality */
		this.fieldLocality = "LocalityCode_CRS";
		this.descriptionLocality = "Naturalis file " + noteTextLocality
				+ " note";

		/* Latitude */
		this.fieldLat = "LatitudeDecimalCode_CRS";
		this.descriptionLat = "Naturalis file " + noteTextLat + " note";

		/* Longitude */
		this.fieldLong = "LongitudeDecimalCode_CRS";
		this.descriptionLong = "Naturalis file " + noteTextLong + " note";

		/* Height Alititude */
		this.fieldAltitude = "HeightCode_CRS";
		this.descriptionAltitude = "Naturalis file " + noteTextAltitude
				+ " note";

		/* True False CRS */
		this.fieldCRS = "CRSCode_CRS";
		this.descriptionCRS = "Naturalis file " + noteTextCRS + " note";
	}

	/**
	 * 
	 */
	private void addCRSNotesToList() {

		/* Phylum */
		listNotes.add(DocumentNoteField.createTextNoteField(noteTextPhylum,
				this.descriptionPhylum, this.fieldPhylum,
				Collections.<Constraint> emptyList(), false));

		/* Class */
		listNotes.add(DocumentNoteField.createTextNoteField(noteTextClass,
				this.descriptionClass, this.fieldClass,
				Collections.<Constraint> emptyList(), false));

		/* Order */
		listNotes.add(DocumentNoteField.createTextNoteField(noteTextOrder,
				this.descriptionOrder, this.fieldOrder,
				Collections.<Constraint> emptyList(), false));

		/* Family */
		listNotes.add(DocumentNoteField.createTextNoteField(noteTextFamily,
				this.descriptionFamily, this.fieldFamily,
				Collections.<Constraint> emptyList(), false));

		/* SubFamily */
		listNotes.add(DocumentNoteField.createTextNoteField(noteTextSubfamily,
				this.descriptionSubfamily, this.fieldSubfamily,
				Collections.<Constraint> emptyList(), false));

		/* Genus */
		listNotes.add(DocumentNoteField.createTextNoteField(noteTextGenus,
				this.descriptionGenus, this.fieldGenus,
				Collections.<Constraint> emptyList(), false));

		/* TaxonName */
		listNotes.add(DocumentNoteField.createTextNoteField(
				noteTextScientificname, this.descriptionScientificname,
				this.fieldScientificname, Collections.<Constraint> emptyList(),
				false));

		/* Identifier */
		listNotes.add(DocumentNoteField.createTextNoteField(noteTextIdentifier,
				this.descriptionIdentifier, this.fieldIdentifier,
				Collections.<Constraint> emptyList(), false));

		/* Sex */
		listNotes.add(DocumentNoteField.createTextNoteField(noteTextSex,
				this.descriptionSex, this.fieldSex,
				Collections.<Constraint> emptyList(), false));

		/* Phase Or Stage */
		listNotes.add(DocumentNoteField.createTextNoteField(noteTextStage,
				this.descriptionStage, this.fieldStage,
				Collections.<Constraint> emptyList(), false));

		/* Collector */
		listNotes.add(DocumentNoteField.createTextNoteField(noteTextLeg,
				this.descriptionLeg, this.fieldLeg,
				Collections.<Constraint> emptyList(), false));

		/* Collecting Date */
		listNotes.add(DocumentNoteField.createTextNoteField(noteTextDate,
				this.descriptionDate, this.fieldDate,
				Collections.<Constraint> emptyList(), false));

		/* Country */
		listNotes.add(DocumentNoteField.createTextNoteField(noteTextCountry,
				this.descriptionCountry, this.fieldCountry,
				Collections.<Constraint> emptyList(), false));

		/* BioRegion */
		listNotes.add(DocumentNoteField.createTextNoteField(noteTextRegion,
				this.descriptionRegion, this.fieldRegion,
				Collections.<Constraint> emptyList(), false));

		/* Locality */
		listNotes.add(DocumentNoteField.createTextNoteField(noteTextLocality,
				this.descriptionLocality, this.fieldLocality,
				Collections.<Constraint> emptyList(), false));

		/* Latitude */
		listNotes.add(DocumentNoteField.createTextNoteField(noteTextLat,
				this.descriptionLat, this.fieldLat,
				Collections.<Constraint> emptyList(), false));

		/* Longtitude */
		listNotes.add(DocumentNoteField.createTextNoteField(noteTextLong,
				this.descriptionLong, this.fieldLong,
				Collections.<Constraint> emptyList(), false));

		/* Height */
		listNotes.add(DocumentNoteField.createTextNoteField(noteTextAltitude,
				this.descriptionAltitude, this.fieldAltitude,
				Collections.<Constraint> emptyList(), false));

		/* CRS True False */
		listNotes.add(DocumentNoteField.createBooleanNoteField(noteTextCRS,
				this.descriptionCRS, this.fieldCRS, true));
	}
}
