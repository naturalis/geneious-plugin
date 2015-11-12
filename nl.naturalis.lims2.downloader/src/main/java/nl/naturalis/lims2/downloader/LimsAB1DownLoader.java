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

	// LimsAB1Fields limsAB1Fields = new LimsAB1Fields();
	private Date creationDate;
	private String name;
	private String residues;
	private ArrayList<String> fields;

	public LimsAB1DownLoader() {
	}

	public LimsAB1DownLoader(String name, String residues, Date creationDate,
			ArrayList<String> fields) {

		this.name = name;
		this.residues = residues;
		this.creationDate = creationDate;
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
		return new Date();
	}

	@Override
	public String getDescription() {
		return "(AB1 files)";
	}

	@Override
	public List<DocumentField> getDisplayableFields() {
		return LimsAB1Fields.displayFields;
	}

	@Override
	public Object getFieldValue(String fieldCodeName) {
		for (int i = 0; i < LimsAB1Fields.displayFields.size(); i++) {
			DocumentField field = LimsAB1Fields.displayFields.get(i);
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
		return null;
	}

	@Override
	public String toHTML() {
		return "<html></html>";
	}

	public String name() {
		return name;
	}

	public ArrayList<String> getFields() {
		return fields;
	}

	public void setFields(ArrayList<String> fields) {
		this.fields = fields;
	}

	public String getResidues() {
		return residues;
	}

	public void setResidues(String residues) {
		this.residues = residues;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date creationDate() {
		return creationDate;
	}

}
