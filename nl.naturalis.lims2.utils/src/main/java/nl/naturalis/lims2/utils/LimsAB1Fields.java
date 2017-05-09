/**
 * 
 */
package nl.naturalis.lims2.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <table summary="Extract filename">
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
 * Getters and Setters fields for the class LimsImportAB1, LimsImportAB1Update<br>
 * Used in method importDocuments(), setSplitDocumentsNotes()<br>
 * setFieldValuesFromAB1FileName()
 * 
 * </tr>
 * </table>
 * 
 * @author Reinier.Kartowikromo
 *
 */
public class LimsAB1Fields {

	private static final Logger logger = LoggerFactory
			.getLogger(LimsAB1Fields.class);
	private String extractID;
	private String pcrPlaatID;
	private String marker;
	private int versieNummer = 1;

	public void setLimsAB1Fields(String pExtractID, String pPcrPlaatID,
			String pMarker) {
		extractID = pExtractID;
		pcrPlaatID = pPcrPlaatID;
		marker = pMarker;

	}

	/**
	 * Get the value of ExtractID
	 * 
	 * @return the value of ExtractID
	 * */
	public String getExtractID() {
		return extractID;
	}

	/**
	 * Set the value for ExtractID
	 * 
	 * @param extractID
	 *            set the param
	 * */
	public void setExtractID(String extractID) {
		this.extractID = extractID;
	}

	/**
	 * Get the value of PCRPlaatdID
	 * 
	 * @return the value of PcrPlaatID
	 * 
	 * */
	public String getPcrPlaatID() {
		return pcrPlaatID;
	}

	/**
	 * Set the value for PCRPlaatdID
	 * 
	 * @param pcrPlaatID
	 *            the value for PCRPlaatdID
	 * */
	public void setPcrPlaatID(String pcrPlaatID) {
		this.pcrPlaatID = pcrPlaatID;
	}

	/**
	 * Get the value of Marker
	 * 
	 * @return value of Marker
	 * */
	public String getMarker() {
		return marker;
	}

	/**
	 * Set the value for Marker
	 * 
	 * @param marker
	 *            value for Marker
	 * */
	public void setMarker(String marker) {
		this.marker = marker;
	}

	/**
	 * Extract the value from the AB1 filename document(s)
	 * 
	 * @param ab1FileName
	 *            Set filename as parameter
	 * */
	public void extractAB1_FastaFileName(String ab1FileName) {
		/*
		 * for example: e4010125015_Sil_tri_MJ243_COI-A01_M13F_A01_008.ab1
		 */
		if (ab1FileName != "") {

			String regex = "\\s*\\bReads Assembly\\b\\s*";
			ab1FileName = ab1FileName.replaceAll(regex, "");

			String regexConsesus = "\\s*\\bconsensus sequence\\b\\s*";
			ab1FileName = ab1FileName.replaceAll(regexConsesus, "");

			if (ab1FileName.contains("_") && ab1FileName.contains(".ab1")) {
				String[] underscore = StringUtils.split(ab1FileName, "_");
				setExtractID(underscore[0]);
				setPcrPlaatID(underscore[3]);
				setMarker(underscore[4]
						.substring(0, underscore[4].indexOf("-")));
			} else if (!ab1FileName.contains(".ab1")) {

				String[] underscore = StringUtils.split(ab1FileName, "_");
				if (underscore[0] != "") {
					setExtractID(underscore[0]);
				} else {
					logger.info("Geen Fasta ExtractID aanwezig? ");
				}
				if (underscore[3] != "") {
					setPcrPlaatID(underscore[3]);
				} else {
					logger.info("Geen Fasta PcrPlaatID aanwezig? ");
				}
				if (underscore[4] != "" && underscore[4].contains("-")) {
					setMarker(underscore[4].substring(0,
							underscore[4].indexOf("-")));
				} else {
					if (underscore[4].contains(".fas")) {
						setMarker(underscore[4].substring(0,
								underscore[4].indexOf(".")));
					} else {
						int emptySpace = underscore[4].indexOf(" ");
						if (underscore[4].indexOf(" ") > 0) {
							setMarker(underscore[4].substring(0,
									underscore[4].indexOf(" ")));
						} else {
							setMarker(underscore[4]);
						}
						// setMarker(underscore[(underscore.length - 1)]);
					}
					// logger.info("Geen Fasta Marker aanwezig? ");
				}
			}
		}
	}

	/**
	 * Get the value of Versionn number
	 * 
	 * @return versienummer
	 * */
	public int getVersieNummer() {
		return versieNummer;
	}

	/**
	 * Set the value for Version number
	 * 
	 * @param versienummer
	 *            Set param versienummer
	 * */
	public void setVersieNummer(int versienummer) {
		this.versieNummer = versienummer;
	}
}