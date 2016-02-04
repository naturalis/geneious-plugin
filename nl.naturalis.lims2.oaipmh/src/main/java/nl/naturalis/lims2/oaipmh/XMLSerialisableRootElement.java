package nl.naturalis.lims2.oaipmh;

import static nl.naturalis.lims2.oaipmh.PluginDocumentData.RootElement.XML_SERIALISABLE_ROOT_ELEMENT;
import nl.naturalis.lims2.oaipmh.XMLSerialisableRootElement.Field;

/**
 * Models the contents of the plugin_document_xml column in case the root
 * element is &lt;XMLSerialisableRootElement&gt;.
 * 
 * @author Ayco Holleman
 *
 */
public class XMLSerialisableRootElement extends PluginDocumentData<Field> {

	public static enum Field {
		description,
		charSequence,
		finishedAddingOutputDocuments(Boolean.class),
		inputDocument(String[].class),
		outputDocument,
		operationId;

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

	public XMLSerialisableRootElement()
	{
		super(XML_SERIALISABLE_ROOT_ELEMENT, Field.class);
	}

}
