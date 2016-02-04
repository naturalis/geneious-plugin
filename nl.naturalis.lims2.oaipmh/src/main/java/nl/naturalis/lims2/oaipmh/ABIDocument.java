package nl.naturalis.lims2.oaipmh;

import static nl.naturalis.lims2.oaipmh.PluginDocumentData.RootElement.*;
import nl.naturalis.lims2.oaipmh.ABIDocument.Field;

/**
 * Models the contents of the plugin_document_xml column in case the root
 * element is &lt;ABIDocument&gt;.
 * 
 * @author Ayco Holleman
 *
 */
public class ABIDocument extends PluginDocumentData<Field> {

	public static enum Field {

		name;

		private final Class<?> type;

		private Field()
		{
			this.type = String.class;
		}

		private Field(Class<?> type)
		{
			this.type = type;
		}

		public Class<?> getType()
		{
			return type;
		}
	}

	public ABIDocument()
	{
		super(ABI_DOCUMENT, Field.class);
	}

}
