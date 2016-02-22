package nl.naturalis.lims2.oaipmh;

import java.util.EnumMap;

/**
 * {@code DocumentNotes} instances maintain the values of all notes present in
 * the document_xml column.
 * 
 * @author Ayco Holleman
 *
 */
public class DocumentNotes {

	/**
	 * Enumerates all XML elements that contain a note that might be used for
	 * OAI-PMH generation. These elements are nested within a &lt;note&gt;
	 * element.
	 * 
	 * @author Ayco Holleman
	 *
	 */
	public static enum Note {
		/**
		 * Flag set to "true" to indicate that CRS data has been imported.
		 */
		CRSCode_CRS,
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
		 * Maps to consensusSequenceQuality for DNA extracts.
		 */
		ConsensusSeqPass_Code_Seq,
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

	private EnumMap<Note, String> data = new EnumMap<>(Note.class);

	public int count()
	{
		return data.size();
	}

	/**
	 * Whether or not the document_xml column contains the specified
	 * &lt;note&gt; element.
	 * 
	 * @param note
	 * @return
	 */
	public boolean isSet(Note note)
	{
		return data.containsKey(note);
	}

	/**
	 * Set the value of the specified note.
	 * 
	 * @param note
	 * @param value
	 */
	public void set(Note note, String value)
	{
		data.put(note, value);
	}

	/**
	 * Get the value of the specified note.
	 * 
	 * @param note
	 * @return
	 */
	public String get(Note note)
	{
		return data.get(note);
	}

}
