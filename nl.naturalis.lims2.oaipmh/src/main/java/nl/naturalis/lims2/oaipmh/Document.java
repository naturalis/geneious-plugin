package nl.naturalis.lims2.oaipmh;

import java.util.List;

/**
 * Class reflecting the contents of the document_xml column within the
 * annotated_document table.
 * 
 * @author Ayco Holleman
 *
 */
public class Document {

	private DocumentClass documentClass;
	private List<String> referencedDocuments;
	private DocumentNotes notes;

	public DocumentClass getDocumentClass()
	{
		return documentClass;
	}

	public void setDocumentClass(DocumentClass documentClass)
	{
		this.documentClass = documentClass;
	}

	public List<String> getReferencedDocuments()
	{
		return referencedDocuments;
	}

	public void setReferencedDocuments(List<String> referencedDocuments)
	{
		this.referencedDocuments = referencedDocuments;
	}

	public DocumentNotes getNotes()
	{
		return notes;
	}

	public void setNotes(DocumentNotes notes)
	{
		this.notes = notes;
	}

}
