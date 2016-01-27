package nl.naturalis.lims2.oaipmh;

public class Document {

	private DocumentClass documentClass;
	private DocumentNotes notes;

	public DocumentClass getDocumentClass()
	{
		return documentClass;
	}

	public void setDocumentClass(DocumentClass type)
	{
		this.documentClass = type;
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
