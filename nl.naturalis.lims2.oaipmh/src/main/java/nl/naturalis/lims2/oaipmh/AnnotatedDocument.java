package nl.naturalis.lims2.oaipmh;

/**
 * Models a record in the annotated_document table.
 * 
 * @author Ayco Holleman
 *
 */
public class AnnotatedDocument {

	private int id;
	private int folderId;
	private long modified;
	private String urn;
	private int referenceCount;
	private Document document;
	private PluginDocumentData<?> pluginDocumentData;

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public int getFolderId()
	{
		return folderId;
	}

	public void setFolderId(int folderId)
	{
		this.folderId = folderId;
	}

	public long getModified()
	{
		return modified;
	}

	public void setModified(long modified)
	{
		this.modified = modified;
	}

	public String getUrn()
	{
		return urn;
	}

	public void setUrn(String urn)
	{
		this.urn = urn;
	}

	public int getReferenceCount()
	{
		return referenceCount;
	}

	public void setReferenceCount(int referenceCount)
	{
		this.referenceCount = referenceCount;
	}

	public Document getDocument()
	{
		return document;
	}

	public void setDocument(Document document)
	{
		this.document = document;
	}

	public PluginDocumentData<?> getPluginDocumentData()
	{
		return pluginDocumentData;
	}

	public void setPluginDocumentData(PluginDocumentData<?> pluginDocumentData)
	{
		this.pluginDocumentData = pluginDocumentData;
	}

}
