package nl.naturalis.lims2.oaipmh;

public abstract class PluginDocumentData {

	public static enum RootElement {
		XML_SERIALISABLE_ROOT_ELEMENT, DEFAULT_ALIGNMENT_DOCUMENT
	}

	private RootElement rootElement;

	public RootElement getRootElement()
	{
		return rootElement;
	}

	public void setRootElement(RootElement rootElement)
	{
		this.rootElement = rootElement;
	}

}
