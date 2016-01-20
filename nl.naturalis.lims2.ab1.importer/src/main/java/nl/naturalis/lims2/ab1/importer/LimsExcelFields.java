/**
 * 
 */
package nl.naturalis.lims2.ab1.importer;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsExcelFields {

	private String extractID;
	private String projectPlaatNummer;
	private String extractPlaatNummer;
	private String plaatPositie;
	private String taxonNaam;
	private String registrationNumber;
	private String subSample = "Sample method";
	private String versieNummer = "1";

	public String getExtractID() {
		return extractID;
	}

	public void setExtractID(String extractID) {
		this.extractID = extractID;
	}

	public String getProjectPlaatNummer() {
		return projectPlaatNummer;
	}

	public void setProjectPlaatNummer(String projectPlaatNummer) {
		this.projectPlaatNummer = projectPlaatNummer;
	}

	public String getExtractPlaatNummer() {
		return extractPlaatNummer;
	}

	public void setExtractPlaatNummer(String extractPlaatNummer) {
		this.extractPlaatNummer = extractPlaatNummer;
	}

	public String getPlaatPositie() {
		return plaatPositie;
	}

	public void setPlaatPositie(String plaatPositie) {
		this.plaatPositie = plaatPositie;
	}

	public String getTaxonNaam() {
		return taxonNaam;
	}

	public void setTaxonNaam(String taxonNaam) {
		this.taxonNaam = taxonNaam;
	}

	public String getRegistrationNumber() {
		return registrationNumber;
	}

	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}

	public String getSubSample() {
		return subSample;
	}

	public void setSubSample(String subSample) {
		this.subSample = subSample;
	}

	public String getVersieNummer() {
		return versieNummer;
	}

	public void setVersieNummer(String versieNummer) {
		this.versieNummer = versieNummer;
	}

}
