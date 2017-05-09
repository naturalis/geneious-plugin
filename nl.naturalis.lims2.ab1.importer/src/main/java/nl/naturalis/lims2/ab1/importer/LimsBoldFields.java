/**
 * 
 */
package nl.naturalis.lims2.ab1.importer;

/**
 * <table summary="Bolds csv fields">
 * <tr>
 * <td>
 * Date: 24 august 2016</td>
 * </tr>
 * <tr>
 * <td>
 * Company: Naturalis Biodiversity Center</td>
 * </tr>
 * <tr>
 * <td>
 * City: Leiden</td>
 * </tr>
 * <tr>
 * <td>
 * Country: Netherlands</td>
 * </tr>
 * <tr>
 * <td>
 * Description:<br>
 * BOLD Fields getter and setter<br>
 * Used in class LimsImportBold</td>
 * </tr>
 * </table>
 * 
 * @author Reinier.Kartowikromo
 *
 */
public class LimsBoldFields {

	private String colRegistratiecode;
	private String boldID;
	private String marker;
	private String traceFilePresence;
	private String nucleotideLength;
	private String genBankID;
	private String numberOfImagesBold;
	private String boldProjectID;
	private String fieldID;
	private String boldBIN;
	private String boldURI;
	private String coi5PAccession;

	/**
	 * get the url value to add to the bold note.<br>
	 * Used in LimsImportBold<br>
	 * limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
	 * "BOLDURICode_FixedValue_Bold", "BOLD URI (Bold)", "BOLD URI (Bold)",
	 * limsBoldFields.getBoldURI(), cnt);
	 * 
	 * @return Return boldURI value
	 * @see String
	 * */
	public String getBoldURI() {
		return boldURI;
	}

	/**
	 * Set the value from the Bold Csv record.<br>
	 * Used in LimsImportBold<br>
	 * limsBoldFields.setBoldURI(boldURI);
	 * 
	 * @param boldURI
	 *            Set param boldURI
	 * */
	public void setBoldURI(String boldURI) {
		this.boldURI = boldURI;
	}

	/**
	 * Get value boldProjectID.<br>
	 * Used in LimsImportBold<br>
	 * limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
	 * "BOLDprojIDCode_Bold", "BOLD proj-ID (Bold)", "BOLD proj-ID (Bold)",
	 * limsBoldFields.getBoldProjectID(), cnt);
	 * 
	 * @return Return value boldProjectID
	 * */
	public String getBoldProjectID() {
		return boldProjectID;
	}

	/**
	 * Set the boldProjectID value.<br>
	 * Used in LimsImportBold.<br>
	 * limsBoldFields.setBoldProjectID(boldProjectID);
	 * 
	 * @param boldProjectID
	 *            Set param boldProjectID value
	 * */
	public void setBoldProjectID(String boldProjectID) {
		this.boldProjectID = boldProjectID;
	}

	/**
	 * get the fieldID value.<br>
	 * Used in LimsImportBold<br>
	 * limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
	 * "FieldIDCode_Bold", "Field ID (Bold)", "Field ID (Bold)",
	 * limsBoldFields.getFieldID(), cnt);
	 * 
	 * @return Return fieldID value
	 * @see String
	 * */
	public String getFieldID() {
		return fieldID;
	}

	/**
	 * Set fieldID value.<br>
	 * Used in LimsImportBold<br>
	 * limsBoldFields.setFieldID(fieldID);
	 * 
	 * @param fieldID
	 *            return value FieldID
	 * */
	public void setFieldID(String fieldID) {
		this.fieldID = fieldID;
	}

	/**
	 * Get the boldBIN value.<br>
	 * Used in LimsImportBold<br>
	 * limsNotes.setNoteToAB1FileName(annotatedPluginDocuments,
	 * "BOLDBINCode_Bold", "BOLD BIN (Bold)", "BOLD BIN (Bold)",
	 * limsBoldFields.getBoldBIN(), cnt);
	 * 
	 * @return Return boldBin value
	 * @see String
	 * */
	public String getBoldBIN() {
		return boldBIN;
	}

	/**
	 * Set the boldBIN value.<br>
	 * Used in LimsImportBold<br>
	 * limsBoldFields.setBoldBIN(boldBIN);
	 * 
	 * @param boldBIN
	 *            Return value Bold Bin
	 * */
	public void setBoldBIN(String boldBIN) {
		this.boldBIN = boldBIN;
	}

	/**
	 * Get the registration number<br>
	 * Not in used in LimsImportBold<br>
	 * 
	 * @return Return value Registratiecode
	 * @see String
	 * */
	public String getColRegistratiecode() {
		return colRegistratiecode;
	}

	/**
	 * Set registration number or code Not in used in LimsImportBold<br>
	 * 
	 * @param colRegistratiecode
	 *            Set param colRegistratiecode
	 * */
	public void setColRegistratiecode(String colRegistratiecode) {
		this.colRegistratiecode = colRegistratiecode;
	}

	/**
	 * Get the BoldID<br>
	 * Used in LimsImportBold<br>
	 * setNotesThatMatchRegistrationNumber();
	 * setNotesToBoldDocumentsRegistration();
	 * 
	 * @return Return boldID value
	 * @see String
	 * */
	public String getBoldID() {
		return boldID;
	}

	/**
	 * Set the BoldID<br>
	 * Used in setNotesThatMatchRegistrationNumber();
	 *
	 * @param boldID
	 *            Set param boldID
	 * */
	public void setBoldID(String boldID) {
		this.boldID = boldID;
	}

	/**
	 * Get the Marker (example: COI)
	 * 
	 * @return Return Marker value
	 * @see String
	 * */
	public String getMarker() {
		return marker;
	}

	/**
	 * Set the marker
	 * 
	 * @param marker
	 *            Set param Marker
	 * */
	public void setMarker(String marker) {
		this.marker = marker;
	}

	/**
	 * Get the value of TraceFilePresence<br>
	 * Used in method setNotesToBoldDocumentsRegistrationMarker(
	 * 
	 * @return Return value TraceFile Presence
	 * @see String
	 * */
	public String getTraceFilePresence() {
		return traceFilePresence;
	}

	/**
	 * Set the value of TraceFilePresence<br>
	 * Used in method setNotesThatMatchRegistrationNumberAndMarker
	 * 
	 * @param traceFilePresence
	 *            Set param traceFilePresence value
	 * */
	public void setTraceFilePresence(String traceFilePresence) {
		this.traceFilePresence = traceFilePresence;
	}

	/**
	 * Get the value for Nucleotide length<br>
	 * Used in method setNotesToBoldDocumentsRegistrationMarker();
	 * 
	 * @return Return value nucleotide length
	 * @see String
	 * */
	public String getNucleotideLength() {
		return nucleotideLength;
	}

	/**
	 * Set the value for Nucleotide length<br>
	 * Used in method setNotesThatMatchRegistrationNumberAndMarker()
	 * 
	 * @param nucleotideLength
	 *            Set param nucleotideLength value
	 * */
	public void setNucleotideLength(String nucleotideLength) {
		this.nucleotideLength = nucleotideLength;
	}

	/**
	 * Get the value of GenBankID<br>
	 * Used in method setNotesToBoldDocumentsRegistrationMarker()
	 * 
	 * @return Return value genBankID
	 * @see String
	 * */
	public String getGenBankID() {
		return genBankID;
	}

	/**
	 * Set the value for GenBankID<br>
	 * Used in method setNotesThatMatchRegistrationNumberAndMarker()
	 * 
	 * @param genBankID
	 *            Set param genBankID value
	 * */
	public void setGenBankID(String genBankID) {
		this.genBankID = genBankID;
	}

	/**
	 * Get the value of Number Of Images Bold<br>
	 * Used in method setNotesToBoldDocumentsRegistration()
	 * 
	 * @return Return value number of images bold value
	 * @see String
	 * */
	public String getNumberOfImagesBold() {
		return numberOfImagesBold;
	}

	/**
	 * Set the value for Number Of Images Bold<br>
	 * Used in method setNotesThatMatchRegistrationNumber();
	 * 
	 * @param numberOfImagesBold
	 *            Set param number of images bold value
	 * */
	public void setNumberOfImagesBold(String numberOfImagesBold) {
		this.numberOfImagesBold = numberOfImagesBold;
	}

	/**
	 * Get the value of the Marker: COI 5 Accession<br>
	 * Used in method setNotesToBoldDocumentsRegistrationMarker()
	 * 
	 * @return Return Coi5P Accession marker value
	 * @see String
	 * */
	public String getCoi5PAccession() {
		return coi5PAccession;
	}

	/**
	 * Set the value for the Marker: COI 5 Accession<br>
	 * Used in method setNotesThatMatchRegistrationNumberAndMarker
	 * 
	 * @param coi5pAccession
	 *            Set param coi5pAccession
	 * */
	public void setCoi5PAccession(String coi5pAccession) {
		coi5PAccession = coi5pAccession;
	}
}