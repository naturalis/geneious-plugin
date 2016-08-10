package nl.naturalis.lims2.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.biomatters.geneious.publicapi.documents.DocumentNote;
import com.biomatters.geneious.publicapi.documents.DocumentNoteField;
import com.biomatters.geneious.publicapi.documents.DocumentNoteType;

public class TestDocumentNoteType01 implements DocumentNoteType {

	private String name;
	private String code;
	private String description;
	private HashMap<String, DocumentNoteField> fields = new HashMap<>();

	@Override
	public DocumentNote createDocumentNote()
	{
		return new TestDocumentNote01();
	}

	@Override
	public String getCode()
	{
		return code;
	}

	@Override
	public String getDescription()
	{
		return description;
	}

	@Override
	public DocumentNoteField getField(String arg0)
	{
		return fields.get(arg0);
	}

	@Override
	public List<DocumentNoteField> getFields()
	{
		return new ArrayList<>(fields.values());
	}

	@Override
	public long getModifiedDate()
	{
		return 0;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public boolean isDefaultVisibleInTable()
	{
		return false;
	}

	@Override
	public boolean isStoredInConnectedNonLocalDatbase()
	{
		return false;
	}

	@Override
	public boolean isVisible()
	{
		return false;
	}

	@Override
	public void removeField(String arg0)
	{
		fields.remove(arg0);
	}

	@Override
	public void setDefaultVisibleInTable(boolean arg0)
	{
	}

	@Override
	public void setDescription(String arg0)
	{
		this.description = arg0;
	}

	@Override
	public void setField(DocumentNoteField arg0)
	{
		fields.put(arg0.getCode(), arg0);
	}

	@Override
	public void setName(String arg0)
	{
		this.name = arg0;
	}

	@Override
	public void setVisible(boolean arg0)
	{
	}

}
