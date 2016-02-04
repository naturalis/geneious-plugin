package nl.naturalis.lims2.oaipmh;

import java.util.EnumMap;

public abstract class PluginDocumentData<T extends Enum<T>> {

	public static enum RootElement {
		XML_SERIALISABLE_ROOT_ELEMENT, DEFAULT_ALIGNMENT_DOCUMENT, ABI_DOCUMENT
	}

	private final RootElement rootElement;
	private final EnumMap<T, Object> data;

	protected PluginDocumentData(RootElement rootElement, Class<T> fieldEnumClass)
	{
		this.rootElement = rootElement;
		this.data = new EnumMap<>(fieldEnumClass);
	}

	public RootElement getRootElement()
	{
		return rootElement;
	}

	public boolean isSet(T field)
	{
		return data.containsKey(field);
	}

	public Object get(T field)
	{
		return data.get(field);
	}

	public void set(T field, Object value)
	{
		data.put(field, value);
	}

}
