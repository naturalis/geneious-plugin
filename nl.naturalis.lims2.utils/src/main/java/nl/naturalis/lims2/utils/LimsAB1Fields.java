/**
 * 
 */
package nl.naturalis.lims2.utils;

import org.apache.commons.lang3.StringUtils;

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

	/*
	 * private static final Logger logger = LoggerFactory
	 * .getLogger(LimsAB1Fields.class);
	 */
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

				String[] fileName = StringUtils.split(ab1FileName, "_");
				for (int i = 0; i < fileName.length; i++) {
					if (i == 0) {
						setExtractID(fileName[i]);
					} else if (i == 3) {
						setPcrPlaatID(fileName[i]);
					} else if (i == 4) {
						if (!fileName[4].contains("-")) {
							return;
						} else {
							setMarker(fileName[i].substring(0,
									fileName[i].indexOf("-")));
						}
					}
				}
			} else if (!ab1FileName.contains(".ab1")) {

				String[] filename = StringUtils.split(ab1FileName, "_");
				for (int i = 0; i < filename.length; i++) {
					if (i == 0) {
						setExtractID(filename[0]);
					} else if (i == 3) {
						setPcrPlaatID(filename[3]);
					} else if (i == 4 && filename[4].contains("-")) {
						setMarker(filename[4].substring(0,
								filename[4].indexOf("-")));
					} else {
						if (filename[4].contains(".fas")) {
							setMarker(filename[4].substring(0,
									filename[4].indexOf(".")));
						} else {
							if (filename[4].indexOf(" ") > 0) {
								setMarker(filename[4].substring(0,
										filename[4].indexOf(" ")));
							} else {
								setMarker(filename[4]);
							}
						}
					}
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