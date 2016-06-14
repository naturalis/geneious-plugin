/**
 * 
 */
package nl.naturalis.lims2.ab1.importer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
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

	public String getSubFamily() {
		return subFamily;
	}

	public void setSubFamily(String subFamily) {
		this.subFamily = subFamily;
	}

	public String getTribe() {
		return tribe;
	}

	public void setTribe(String tribe) {
		this.tribe = tribe;
	}

	public String getSuborder() {
		return suborder;
	}

	public void setSuborder(String suborder) {
		this.suborder = suborder;
	}

	public String getSubclass() {
		return subclass;
	}

	public void setSubclass(String subclass) {
		this.subclass = subclass;
	}

	public String getExtractIDfileName() {
		return extractIDfileName;
	}

	public void setExtractIDfileName(String extractIDfileName) {
		this.extractIDfileName = extractIDfileName;
	}

	public String getRegistratienummer() {
		return registratienummer;
	}

	public void setRegistratienummer(String registratienummer) {
		this.registratienummer = registratienummer;
	}

	public String getPhylum() {
		return phylum;
	}

	public void setPhylum(String phylum) {
		this.phylum = phylum;
	}

	public String getKlasse() {
		return klasse;
	}

	public void setKlasse(String klasse) {
		this.klasse = klasse;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getFamily() {
		return family;
	}

	public void setFamily(String family) {
		this.family = family;
	}

	public String getSuperFamily() {
		return superFamily;
	}

	public void setSuperFamily(String superFamily) {
		this.superFamily = superFamily;
	}

	public String getGenus() {
		return genus;
	}

	public void setGenus(String genus) {
		this.genus = genus;
	}

	public String getTaxon() {
		return taxon;
	}

	public void setTaxon(String taxon) {
		this.taxon = taxon;
	}

	public String getDeterminator() {
		return determinator;
	}

	public void setDeterminator(String determinator) {
		this.determinator = determinator;
	}

	public String getStadium() {
		return stadium;
	}

	public void setStadium(String stadium) {
		this.stadium = stadium;
	}

	public String getSpecies() {
		return species;
	}

	public void setSpecies(String species) {
		this.species = species;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getLegavit() {
		return legavit;
	}

	public void setLegavit(String legavit) {
		this.legavit = legavit;
	}

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

	public void setCollectingDate(String collectingDate) {
		this.collectingDate = collectingDate;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getBioRegion() {
		return bioRegion;
	}

	public void setBioRegion(String bioRegion) {
		this.bioRegion = bioRegion;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getLocality() {
		return Locality;
	}

	public void setLocality(String Locality) {
		this.Locality = Locality;
	}

	public String getLatitudeDecimal() {
		return latitudeDecimal;
	}

	public void setLatitudeDecimal(String latitudeDecimal) {
		this.latitudeDecimal = latitudeDecimal;
	}

	public String getLongitudeDecimal() {
		return longitudeDecimal;
	}

	public void setLongitudeDecimal(String longitudeDecimal) {
		this.longitudeDecimal = longitudeDecimal;
	}

	public String getHeight() {
		return Height;
	}

	public void setHeight(String height) {
		Height = height;
	}

}
