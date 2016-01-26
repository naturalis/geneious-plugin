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
	private String versieNummer = "1";

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
		if (ab1FileName.contains("_")) {
			String[] underscore = StringUtils.split(ab1FileName, "_");
			setExtractID(underscore[0]);
			setPcrPlaatID(underscore[3]);
			setMarker(underscore[4].substring(0, underscore[4].indexOf("-")));
		}

	}

	public String getVersieNummer() {
		return versieNummer;
	}

	public void setVersieNummer(String versieNummer) {
		this.versieNummer = versieNummer;
	}

}
