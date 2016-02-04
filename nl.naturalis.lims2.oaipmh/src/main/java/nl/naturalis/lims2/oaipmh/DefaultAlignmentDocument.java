package nl.naturalis.lims2.oaipmh;

import static nl.naturalis.lims2.oaipmh.PluginDocumentData.RootElement.DEFAULT_ALIGNMENT_DOCUMENT;
import nl.naturalis.lims2.oaipmh.DefaultAlignmentDocument.Field;

/**
 * Models the contents of the plugin_document_xml column in case the root
 * element is &lt;DefaultAlignmentDocument&gt;.
 * 
 * @author Ayco Holleman
 *
 */
public class DefaultAlignmentDocument extends PluginDocumentData<Field> {

	public static final String OPERATION_DE_NOVO_ASSEMBLY = "com.biomatters.plugins.alignment.AssemblyOperation_Denovo";

	public static enum Field {
		is_contig(Boolean.class);

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

	public DefaultAlignmentDocument()
	{
		super(DEFAULT_ALIGNMENT_DOCUMENT, Field.class);
	}

}
