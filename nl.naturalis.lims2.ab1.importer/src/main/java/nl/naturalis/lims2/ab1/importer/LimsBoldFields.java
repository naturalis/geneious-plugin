/**
 * 
 */
package nl.naturalis.lims2.ab1.importer;

/**
 * <table>
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
 * Fields for Bold notes</td>
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
	 * @return
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
	 * @return
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
	 * @return
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
	 * @return
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
	 * */
	public void setBoldBIN(String boldBIN) {
		this.boldBIN = boldBIN;
	}

	public String getColRegistratiecode() {
		return colRegistratiecode;
	}

	public void setColRegistratiecode(String colRegistratiecode) {
		this.colRegistratiecode = colRegistratiecode;
	}

	public String getBoldID() {
		return boldID;
	}

	public void setBoldID(String boldID) {
		this.boldID = boldID;
	}

	public String getMarker() {
		return marker;
	}

	public void setMarker(String marker) {
		this.marker = marker;
	}

	public String getTraceFilePresence() {
		return traceFilePresence;
	}

	public void setTraceFilePresence(String traceFilePresence) {
		this.traceFilePresence = traceFilePresence;
	}

	public String getNucleotideLength() {
		return nucleotideLength;
	}

	public void setNucleotideLength(String nucleotideLength) {
		this.nucleotideLength = nucleotideLength;
	}

	public String getGenBankID() {
		return genBankID;
	}

	public void setGenBankID(String genBankID) {
		this.genBankID = genBankID;
	}

	public String getNumberOfImagesBold() {
		return numberOfImagesBold;
	}

	public void setNumberOfImagesBold(String numberOfImagesBold) {
		this.numberOfImagesBold = numberOfImagesBold;
	}

	public String getCoi5PAccession() {
		return coi5PAccession;
	}

	public void setCoi5PAccession(String coi5pAccession) {
		coi5PAccession = coi5pAccession;
	}

}
