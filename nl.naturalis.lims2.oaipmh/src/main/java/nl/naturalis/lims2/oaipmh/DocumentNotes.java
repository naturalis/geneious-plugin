package nl.naturalis.lims2.oaipmh;

import java.util.EnumMap;

public class DocumentNotes {

	public static enum Field {
		/**
		 * Maps to unitID for specimens; maps to associatedUnitID for DNA
		 * extracts.
		 */
		RegistrationNumberCode_Samples,
		/**
		 * Maps to associatedUnitID for specimens.
		 */
		BOLDIDCode_BOLD,
		/**
		 * Maps to uri for specimens.
		 */
		BOLDURICode_FixedValue,
		/**
		 * Maps to institutePlateID
		 */
		ExtractPlateNumberCode_Samples,
		/**
		 * Maps to unitID for DNA extracts.
		 */
		ExtractIDCode_Samples,
		/**
		 * Maps to platePosition for DNA extracts.
		 */
		PlatePositionCode_Samples,
		/**
		 * Maps to batchID for DNA extracts.
		 */
		ProjectPlateNumberCode_Samples,
		/**
		 * Maps to versionNumber for DNA extracts.
		 */
		DocumentVersionCode,
		/**
		 * Maps to marker for DNA extracts.
		 */
		MarkerCode_Seq,
		/**
		 * Maps to pcrPlateID for DNA extracts.
		 */
		PCRplateIDCode_Seq,
		/**
		 * Maps to consensusSequenceLength for DNA extracts.
		 */
		NucleotideLengthCode_Bold,
		/**
		 * Maps to consensusSequenceLength for DNA extracts.
		 */
		GenBankIDCode_Bold,
		/**
		 * Maps to geneticAccessionNumberURI for DNA extracts.
		 */
		GenBankURICode_FixedValue,
		/**
		 * Maps to multiMediaObjectComment for specimens.
		 */
		NumberOfImagesCode_BOLD,
		/**
		 * Maps to sequencingStaff for DNA extracts.
		 */
		SequencingStaffCode_FixedValue,
		/**
		 * Maps to amplificationStaff for DNA extracts.
		 */
		AmplicificationStaffCode_FixedValue
	}

	private EnumMap<Field, String> data = new EnumMap<>(Field.class);

	/**
	 * Whether or not the XML in the document_xml column or plugin_document_xml
	 * column contained the specified element or attribute.
	 * 
	 * @param field
	 * @return
	 */
	public boolean isSet(Field field)
	{
		return data.containsKey(field);
	}

	public void set(Field field, String value)
	{
		data.put(field, value);
	}

	public String get(Field field)
	{
		return data.get(field);
	}

}
