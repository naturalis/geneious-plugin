package nl.naturalis.lims2.oaipmh;

import java.util.EnumMap;

public class DefaultAlignmentDocument extends PluginDocumentData {

	public static final String OPERATION_DE_NOVO_ASSEMBLY = "com.biomatters.plugins.alignment.AssemblyOperation_Denovo";

	public static enum Field {
		is_contig;

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

	private EnumMap<Field, Object> data = new EnumMap<>(Field.class);

	/**
	 * Whether or not the XML in the plugin_document_xml column contained the
	 * specified element or attribute.
	 * 
	 * @param field
	 * @return
	 */
	public boolean isSet(Field field)
	{
		return data.containsKey(field);
	}

	public Object get(Field field)
	{
		return data.get(field);
	}

	public void set(Field field, Object value)
	{
		data.put(field, value);
	}

}
