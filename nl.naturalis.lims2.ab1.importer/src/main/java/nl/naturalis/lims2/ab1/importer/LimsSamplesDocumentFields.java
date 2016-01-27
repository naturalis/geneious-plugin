package nl.naturalis.lims2.ab1.importer;

import java.util.List;

import com.biomatters.geneious.publicapi.documents.DocumentField;
import com.biomatters.geneious.publicapi.documents.XMLSerializable;

public interface LimsSamplesDocumentFields extends XMLSerializable {

	public String getNoteExtractID();

	public String getNoteProjectPlateNumber();

	public String getNoteExtractPlateNumber();

	public String getNoteTaxonName();

	public String getNoteRegistrationNumber();

	public String getNotePlatePosition();

	public String getNoteSampleMethod();

	public String getNoteDocumentVersion();

	public List<DocumentField> getSamplesAttributes();

	public Object getSamplesAttributeValue(String attributeName);

}
