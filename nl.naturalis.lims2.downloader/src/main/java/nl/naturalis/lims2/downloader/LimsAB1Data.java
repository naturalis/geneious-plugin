/**
 * 
 */
package nl.naturalis.lims2.downloader;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsAB1Data {
	public LimsAB1Data(String extractID, String pcrPlaatID, String marker) {
		this.extractID = extractID;
		this.pcrPlaatID = pcrPlaatID;
		this.marker = marker;
	}

	public LimsAB1Data() {
	};

	public String extractID;
	public String pcrPlaatID;
	public String marker;

}
