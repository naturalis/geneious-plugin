/**
 * 
 */
package nl.naturalis.lims2.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsAB1Fields {

	private String extractID;
	private String pcrPlaatID;
	private String marker;
	private int versieNummer = 1;
	private String extractidSamplesFromDummy;
	private String samplePlateIdSamplesFromDummy;
	private String scientificNameSamplesFromDummy;
	private String registrnmbrSamplesFromDummy;
	private String positionSamplesFromDummy;

	public String getExtractidSamplesFromDummy() {
		return extractidSamplesFromDummy;
	}

	public void setExtractidSamplesFromDummy(String extractidSamplesFromDummy) {
		this.extractidSamplesFromDummy = extractidSamplesFromDummy;
	}

	public String getSamplePlateIdSamplesFromDummy() {
		return samplePlateIdSamplesFromDummy;
	}

	public void setSamplePlateIdSamplesFromDummy(
			String samplePlateIdSamplesFromDummy) {
		this.samplePlateIdSamplesFromDummy = samplePlateIdSamplesFromDummy;
	}

	public String getScientificNameSamplesFromDummy() {
		return scientificNameSamplesFromDummy;
	}

	public void setScientificNameSamplesFromDummy(
			String scientificNameSamplesFromDummy) {
		this.scientificNameSamplesFromDummy = scientificNameSamplesFromDummy;
	}

	public String getRegistrnmbrSamplesFromDummy() {
		return registrnmbrSamplesFromDummy;
	}

	public void setRegistrnmbrSamplesFromDummy(
			String registrnmbrSamplesFromDummy) {
		this.registrnmbrSamplesFromDummy = registrnmbrSamplesFromDummy;
	}

	public String getPositionSamplesFromDummy() {
		return positionSamplesFromDummy;
	}

	public void setPositionSamplesFromDummy(String positionSamplesFromDummy) {
		this.positionSamplesFromDummy = positionSamplesFromDummy;
	}

	public String getExtractID() {
		return extractID;
	}

	public void setExtractID(String extractID) {
		this.extractID = extractID;
	}

	public String getPcrPlaatID() {
		return pcrPlaatID;
	}

	public void setPcrPlaatID(String pcrPlaatID) {
		this.pcrPlaatID = pcrPlaatID;
	}

	public String getMarker() {
		return marker;
	}

	public void setMarker(String marker) {
		this.marker = marker;
	}

	public void setFieldValuesFromAB1FileName(String ab1FileName) {
		/*
		 * for example: e4010125015_Sil_tri_MJ243_COI-A01_M13F_A01_008.ab1
		 */
		if (ab1FileName.contains("_") && ab1FileName.contains("ab1")) {
			String[] underscore = StringUtils.split(ab1FileName, "_");
			setExtractID(underscore[0]);
			setPcrPlaatID(underscore[3]);
			setMarker(underscore[4].substring(0, underscore[4].indexOf("-")));
		} else {
			String[] underscore = StringUtils.split(ab1FileName, "_");
			setExtractID(underscore[0]);
			setPcrPlaatID(underscore[3]);
			setMarker(underscore[4]);
		}

	}

	public int getVersieNummer() {
		return versieNummer;
	}

	public void setVersieNummer(int versienummer) {
		this.versieNummer = versienummer;
	}

}
