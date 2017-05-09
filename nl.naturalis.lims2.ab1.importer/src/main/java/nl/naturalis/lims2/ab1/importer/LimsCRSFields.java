/**
 * 
 */
package nl.naturalis.lims2.ab1.importer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <table summary="CRS fields">
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
 * CRS Fields getter and setter<br>
 * Used in class LimsImportCRS<br>
 * Methods: extractRankOrClassification(),<br>
 * readCRSDataFromCSVFile()</td>
 * </tr>
 * </table>
 * 
 * @author Reinier.Kartowikromo
 *
 */
public class LimsCRSFields {

	private String registratienummer;
	private String phylum;
	private String subclass;
	private String klasse;
	private String suborder;
	private String order;
	private String family;
	private String subFamily;
	private String superFamily;
	private String genus;
	private String taxon;
	private String determinator;
	private String stadium;
	private String species;
	private String sex;
	private String legavit;
	private String collectingDate;
	private String country;
	private String bioRegion;
	private String city;
	private String Locality;
	private String latitudeDecimal;
	private String longitudeDecimal;
	private String Height;
	private String extractIDfileName;
	private String tribe;

	/**
	 * Get value subfamily
	 * 
	 * @return Return subFamily value
	 * @see LimsCRSFields
	 * */
	public String getSubFamily() {
		return subFamily;
	}

	/**
	 * Set value fro sub family
	 * 
	 * @param subFamily
	 *            Set param subFamily
	 * */
	public void setSubFamily(String subFamily) {
		this.subFamily = subFamily;
	}

	/**
	 * Get value of Tribe
	 * 
	 * @return Return value tribe
	 * @see LimsCRSFields
	 * */
	public String getTribe() {
		return tribe;
	}

	/**
	 * Set value for Tribe
	 * 
	 * @param tribe
	 *            Set param tribe value
	 * */
	public void setTribe(String tribe) {
		this.tribe = tribe;
	}

	/**
	 * Get value of Sub order
	 * 
	 * @return Return value sub Order
	 * @see LimsCRSFields
	 * */
	public String getSuborder() {
		return suborder;
	}

	/**
	 * Set value for Sub Order
	 * 
	 * @param suborder
	 *            Set param suborder value
	 * */
	public void setSuborder(String suborder) {
		this.suborder = suborder;
	}

	/**
	 * Get value of Subclass
	 * 
	 * @return Return subclass value
	 * @see LimsCRSFields
	 * */
	public String getSubclass() {
		return subclass;
	}

	/**
	 * Set value for Subclass
	 * 
	 * @param subclass
	 *            Set param subClass
	 * */
	public void setSubclass(String subclass) {
		this.subclass = subclass;
	}

	/**
	 * Get the value of ExtractID from the Filename
	 * 
	 * @return Return extractIDfileName value
	 * @see LimsCRSFields
	 * */
	public String getExtractIDfileName() {
		return extractIDfileName;
	}

	/**
	 * Set the value for ExtractID
	 * 
	 * @param extractIDfileName
	 *            Set param extractIDfileName
	 * */
	public void setExtractIDfileName(String extractIDfileName) {
		this.extractIDfileName = extractIDfileName;
	}

	/**
	 * Get the value of Registration number
	 * 
	 * @return Return registratienummer value
	 * @see LimsCRSFields
	 * */
	public String getRegistratienummer() {
		return registratienummer;
	}

	/**
	 * Set the value for Registratiion number
	 * 
	 * @param registratienummer
	 *            Set param registratienummer
	 * */
	public void setRegistratienummer(String registratienummer) {
		this.registratienummer = registratienummer;
	}

	/**
	 * Get the value of Phylum
	 * 
	 * @return Return Phylum Value
	 * @see LimsCRSFields
	 * */
	public String getPhylum() {
		return phylum;
	}

	/**
	 * Set the value for Phylum
	 * 
	 * @param phylum
	 *            Set param Phylum
	 * */
	public void setPhylum(String phylum) {
		this.phylum = phylum;
	}

	/**
	 * Get the value of Klasse(Class)
	 * 
	 * @return Return Klasse value
	 * @see LimsCRSFields
	 * */
	public String getKlasse() {
		return klasse;
	}

	/**
	 * Set the value for Klasse(class)
	 * 
	 * @param klasse
	 *            Set param Klasse
	 * */
	public void setKlasse(String klasse) {
		this.klasse = klasse;
	}

	/**
	 * Get value of Order
	 * 
	 * @return return order value
	 * @see LimsCRSFields
	 * */
	public String getOrder() {
		return order;
	}

	/**
	 * Set value for Order
	 * 
	 * @param order
	 *            Set param Order
	 * */
	public void setOrder(String order) {
		this.order = order;
	}

	/**
	 * Get the value of Family
	 * 
	 * @return Return Family value
	 * @see LimsCRSFields
	 * */
	public String getFamily() {
		return family;
	}

	/**
	 * Set the value for Family
	 * 
	 * @param family
	 *            Set param Family
	 * */
	public void setFamily(String family) {
		this.family = family;
	}

	/**
	 * Get the value of Super Family
	 * 
	 * @return Return Super Family value
	 * @see LimsCRSFields
	 * */
	public String getSuperFamily() {
		return superFamily;
	}

	/**
	 * Set the value for Super Family
	 * 
	 * @param superFamily
	 *            Set param superFamily
	 * */
	public void setSuperFamily(String superFamily) {
		this.superFamily = superFamily;
	}

	/**
	 * Get the value of Genus
	 * 
	 * @return return value Genus
	 * @see LimsCRSFields
	 * */
	public String getGenus() {
		return genus;
	}

	/**
	 * Set the value for Genus
	 * 
	 * @param genus
	 *            Set param Genus
	 * */
	public void setGenus(String genus) {
		this.genus = genus;
	}

	/**
	 * Get the value of Taxon
	 * 
	 * @return Return Taxon value
	 * @see LimsCRSFields
	 * */
	public String getTaxon() {
		return taxon;
	}

	/**
	 * Set the value for Taxon
	 * 
	 * @param taxon
	 *            Set param String taxon
	 * */
	public void setTaxon(String taxon) {
		this.taxon = taxon;
	}

	/**
	 * Get the value of Determinator
	 * 
	 * @return Return Determinator value
	 * @see LimsCRSFields
	 * */
	public String getDeterminator() {
		return determinator;
	}

	/**
	 * Set the value for Determinator
	 * 
	 * @param determinator
	 *            Set the value for Determinator
	 * */
	public void setDeterminator(String determinator) {
		this.determinator = determinator;
	}

	/**
	 * Get the value of Stadium
	 * 
	 * @return Get the value of Stadium
	 * */
	public String getStadium() {
		return stadium;
	}

	/**
	 * Set the value for Stadium
	 * 
	 * @param stadium
	 *            Set the value for Stadium
	 * */
	public void setStadium(String stadium) {
		this.stadium = stadium;
	}

	/**
	 * Get the value of Species
	 * 
	 * @return Get the value of Species
	 * */
	public String getSpecies() {
		return species;
	}

	/**
	 * Set the value for Species
	 * 
	 * @param species
	 *            Set species value
	 * */
	public void setSpecies(String species) {
		this.species = species;
	}

	/**
	 * Get the value of Sex
	 * 
	 * @return Get the value of Sex
	 * */
	public String getSex() {
		return sex;
	}

	/**
	 * Set the value for Sex
	 * 
	 * @param sex
	 *            Set sex value
	 * */
	public void setSex(String sex) {
		this.sex = sex;
	}

	/**
	 * Get the value of Legavit
	 * 
	 * @return legavit value
	 * */
	public String getLegavit() {
		return legavit;
	}

	/**
	 * Set value for Legavit
	 * 
	 * @param legavit
	 *            set legavit value
	 * */
	public void setLegavit(String legavit) {
		this.legavit = legavit;
	}

	/**
	 * Get the value of Collecting Date
	 * 
	 * @return Get the value of Collecting Date
	 * */
	public String getCollectingDate() {
		String strDateFormat = "yyyyMMdd";
		Date collDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
		try {
			collDate = sdf.parse(collectingDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		return formatter.format(collDate);

	}

	/**
	 * Set the value for Collecting Date
	 * 
	 * @param collectingDate
	 *            Set collectingDate value
	 * */
	public void setCollectingDate(String collectingDate) {
		this.collectingDate = collectingDate;
	}

	/**
	 * Get the value of Country
	 * 
	 * @return country value
	 * */
	public String getCountry() {
		return country;
	}

	/**
	 * Set value for Country
	 * 
	 * @param country
	 *            Set country value
	 * */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * Get the value of BioRegion
	 * 
	 * @return bioRegion value
	 * */
	public String getBioRegion() {
		return bioRegion;
	}

	/**
	 * Set the value for BioRegion
	 * 
	 * @param bioRegion
	 *            Set String param bioRegion
	 * */
	public void setBioRegion(String bioRegion) {
		this.bioRegion = bioRegion;
	}

	/**
	 * Get the value of City
	 * 
	 * @return Return City value
	 * @see LimsCRSFields
	 * */
	public String getCity() {
		return city;
	}

	/**
	 * Set the value for City
	 * 
	 * @param city
	 *            Set City value
	 * */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * Get the value of Locality
	 * 
	 * @return Locality value
	 * */
	public String getLocality() {
		return Locality;
	}

	/**
	 * Set the value for Locality
	 * 
	 * @param Locality
	 *            Set Locality
	 * */
	public void setLocality(String Locality) {
		this.Locality = Locality;
	}

	/**
	 * Get the value of LatitudeDecimal
	 * 
	 * @return latitudeDecimal latitudeDecimal value
	 * */
	public String getLatitudeDecimal() {
		return latitudeDecimal;
	}

	/**
	 * Set the value for LatitudeDecimal
	 * 
	 * @param latitudeDecimal
	 *            set latitudeDecimal value
	 * */
	public void setLatitudeDecimal(String latitudeDecimal) {
		this.latitudeDecimal = latitudeDecimal;
	}

	/**
	 * Get the value of LongitudeDecimal
	 * 
	 * @return Return the value of LongitudeDecimal
	 * @see String
	 * */
	public String getLongitudeDecimal() {
		return longitudeDecimal;
	}

	/**
	 * Set the value for LongitudeDecimal
	 * 
	 * @param longitudeDecimal
	 *            Set param longtitudeDecimal
	 * 
	 * */
	public void setLongitudeDecimal(String longitudeDecimal) {
		this.longitudeDecimal = longitudeDecimal;
	}

	/**
	 * Get the value of Height
	 * 
	 * @return Return the height value
	 * @see String
	 * */
	public String getHeight() {
		return Height;
	}

	/**
	 * Set the value for Height
	 * 
	 * @param height
	 *            Set param height
	 * */
	public void setHeight(String height) {
		Height = height;
	}
}