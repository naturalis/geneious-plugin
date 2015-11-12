/**
 * 
 */
package nl.naturalis.lims2.downloader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jdom.Element;

import com.biomatters.geneious.publicapi.documents.DocumentField;
import com.biomatters.geneious.publicapi.documents.PluginDocument;
import com.biomatters.geneious.publicapi.documents.URN;
import com.biomatters.geneious.publicapi.documents.XMLSerializationException;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsAB1DownLoader implements PluginDocument {

	LimsAB1Fields limsAB1Fields;
	private Date creationDate;
	private String name;
	private String summary;
	private URN urn;
	private ArrayList<String> fields;

	public LimsAB1DownLoader() {
	}

	public LimsAB1DownLoader(Date creationDate, String name, String summary,
			URN urn, ArrayList<String> fields) {
		this.setCreationDate(creationDate);
		this.name = name;
		this.summary = summary;
		this.urn = urn;
		this.fields = fields;
	}

	@Override
	public void fromXML(Element element) throws XMLSerializationException {

	}

	@Override
	public Element toXML() {
		return new Element("AB1Element");
	}

	@Override
	public Date getCreationDate() {
		return creationDate;
	}

	@Override
	public String getDescription() {
		return "(AB1 files)";
	}

	@Override
	public List<DocumentField> getDisplayableFields() {
		return limsAB1Fields.displayFields;
	}

	@Override
	public Object getFieldValue(String fieldCodeName) {
		for (int i = 0; i < limsAB1Fields.displayFields.size(); i++) {
			DocumentField field = limsAB1Fields.displayFields.get(i);
			if (field.getCode().equals(fieldCodeName)) {
				return (Object) fields.get(i);
			}
		}
		return new Object();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public URN getURN() {
		return urn;
	}

	@Override
	public String toHTML() {
		return "<html></html>";
	}

	public Date creationDate() {
		return creationDate;
	}

	public String name() {
		return name;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public URN getUrn() {
		return urn;
	}

	public void setUrn(URN urn) {
		this.urn = urn;
	}

	public ArrayList<String> getFields() {
		return fields;
	}

	public void setFields(ArrayList<String> fields) {
		this.fields = fields;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

}
