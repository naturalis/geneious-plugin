/**
 * 
 */
package nl.naturalis.lims2.ab1.importer;

import java.util.Date;
import java.util.List;

import org.jdom.Element;

import com.biomatters.geneious.publicapi.documents.DocumentField;
import com.biomatters.geneious.publicapi.documents.PluginDocument;
import com.biomatters.geneious.publicapi.documents.URN;
import com.biomatters.geneious.publicapi.documents.XMLSerializationException;
import com.biomatters.geneious.publicapi.documents.XMLSerializer;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsSamplesFields implements PluginDocument,
		LimsSamplesDocumentFields {

	private String extractID;
	private String projectPlaatNummer;
	private String extractPlaatNummer;
	private String plaatPositie;
	private String taxonNaam;
	private String registrationNumber;
	private String subSample = "Sample method";
	private Object versieNummer = "1";
	private LimsSamplesDocumentFields samplesResults;
	private String regNumberScientificName;

	public LimsSamplesFields(LimsSamplesDocumentFields typeSamples) {
		samplesResults = typeSamples;
	}

	public LimsSamplesFields() {

	}

	public LimsSamplesFields(Element e) throws XMLSerializationException {
		fromXML(e);
	}

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

	public Object getVersieNummer() {
		return versieNummer;
	}

	public void setVersieNummer(Object versieNummer) {
		this.versieNummer = versieNummer;
	}

	@Override
	public void fromXML(Element element) throws XMLSerializationException {
		samplesResults = (LimsSamplesDocumentFields) XMLSerializer
				.classFromXML(element);

	}

	@Override
	public Element toXML() {
		return XMLSerializer.classToXML("Samples", samplesResults);
	}

	@Override
	public Date getCreationDate() {
		return new Date();
	}

	@Override
	public String getDescription() {
		Object description = getFieldValue("document.notes");
		return description != null ? description.toString() : "";
	}

	@Override
	public List<DocumentField> getDisplayableFields() {
		return samplesResults.getSamplesAttributes();
	}

	@Override
	public Object getFieldValue(String fieldCodeName) {
		return samplesResults.getSamplesAttributeValue(fieldCodeName);
	}

	@Override
	public String getName() {
		return "" + getExtractID();
	}

	@Override
	public URN getURN() {
		return null;
	}

	@Override
	public String toHTML() {
		return null;
	}

	@Override
	public List<DocumentField> getSamplesAttributes() {
		return samplesResults.getSamplesAttributes();
	}

	@Override
	public Object getSamplesAttributeValue(String attributeName) {
		return samplesResults.getSamplesAttributeValue(attributeName);
	}

	@Override
	public String getNoteExtractID() {
		return samplesResults.getNoteExtractID();
	}

	@Override
	public String getNoteProjectPlateNumber() {
		return samplesResults.getNoteProjectPlateNumber();
	}

	@Override
	public String getNoteExtractPlateNumber() {
		return samplesResults.getNoteExtractPlateNumber();
	}

	@Override
	public String getNoteTaxonName() {
		return samplesResults.getNoteTaxonName();
	}

	@Override
	public String getNoteRegistrationNumber() {
		return samplesResults.getNoteRegistrationNumber();
	}

	@Override
	public String getNotePlatePosition() {
		return samplesResults.getNotePlatePosition();
	}

	@Override
	public String getNoteSampleMethod() {
		return samplesResults.getNoteSampleMethod();
	}

	@Override
	public String getNoteDocumentVersion() {
		return samplesResults.getNoteDocumentVersion();
	}

	public String getSamplesHTML() {
		StringBuilder htmlBuilder = new StringBuilder();
		// no inspection StringConcatenationInsideStringBufferAppend
		htmlBuilder.append("<h1>" + getName() + "</h1>\n");
		// no inspection StringConcatenationInsideStringBufferAppend
		htmlBuilder.append("<table border=\"0\">\n");
		List<DocumentField> samplesAttributes = samplesResults
				.getSamplesAttributes();
		if (samplesAttributes == null || samplesAttributes.size() == 0) {
			return null;
		}
		for (DocumentField field : samplesAttributes) {
			String name = field.getName();
			Object value = samplesResults.getSamplesAttributeValue(field
					.getCode());
			if (value == null) {
				value = "";
			}
			// no inspection StringConcatenationInsideStringBufferAppend
			htmlBuilder.append("<tr><td align=\"right\"><b>" + name
					+ ":</b></td><td>" + value + "</td></tr>\n");
		}
		htmlBuilder.append("</table>\n");
		return htmlBuilder.toString();
	}

	public String getRegNumberScientificName() {
		return regNumberScientificName;
	}

	public void setRegNumberScientificName(String regNumberScientificName) {
		this.regNumberScientificName = regNumberScientificName;
	}

}
