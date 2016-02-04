package nl.naturalis.lims2.oaipmh;

import java.util.EnumMap;

public class DocumentNotes {

	public static enum Field {
		/**
		 * Maps to &lt;unitID&gt; for specimens. Maps to
		 * &lt;associatedUnitID&gt; for DNA extracts.
		 */
		RegistrationNumberCode_Samples,
		/**
		 * Maps to &lt;associatedUnitID&gt; for specimens.
		 */
		BOLDIDCode_BOLD,
		/**
		 * Maps to &lt;institutePlateID&gt;
		 */
		ExtractPlateNumberCode_Samples,
		/**
		 * Maps to &lt;unitID&gt; for DNA extracts.
		 */
		ExtractIDCode_Samples,
		/**
		 * Maps to &lt;platePosition&gt; for DNA extracts.
		 */
		PlatePositionCode_Samples,
		/**
		 * Maps to &lt;batchID&gt; for DNA extracts.
		 */
		ProjectPlateNumberCode_Samples,
		/**
		 * Maps to &lt;versionNumber&gt; for DNA extracts.
		 */
		DocumentVersionCode,
		/**
		 * Maps to &lt;marker&gt; for DNA extracts.
		 */
		MarkerCode_Seq,
		/**
		 * Maps to &lt;pcrPlateID&gt; for DNA extracts.
		 */
		PCRplateIDCode_Seq,
		/**
		 * Maps to &lt;consensusSequenceLength&gt; for DNA extracts.
		 */
		NucleotideLengthCode_Bold,
		/**
		 * Maps to &lt;consensusSequenceLength&gt; for DNA extracts.
		 */
		GenBankIDCode_Bold,
		/**
		 * Maps to &lt;multiMediaObjectComment&gt; for specimens.
		 */
		NumberOfImagesCode_BOLD
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
