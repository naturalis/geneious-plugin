/**
 * 
 */
package nl.naturalis.lims2.ab1.importer;

/**
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

	public String getBoldURI() {
		return boldURI;
	}

	public void setBoldURI(String boldURI) {
		this.boldURI = boldURI;
	}

	public String getBoldProjectID() {
		return boldProjectID;
	}

	public void setBoldProjectID(String boldProjectID) {
		this.boldProjectID = boldProjectID;
	}

	public String getFieldID() {
		return fieldID;
	}

	public void setFieldID(String fieldID) {
		this.fieldID = fieldID;
	}

	public String getBoldBIN() {
		return boldBIN;
	}

	public void setBoldBIN(String boldBIN) {
		boldBIN = boldBIN;
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
