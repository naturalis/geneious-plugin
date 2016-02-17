package nl.naturalis.lims2.oaipmh;

import org.w3c.dom.Element;

public class ABIDocumentFactory {

	public ABIDocumentFactory()
	{
	}

	@SuppressWarnings("static-method")
	public ABIDocument build(Element root)
	{
		return new ABIDocument();
	}

}
