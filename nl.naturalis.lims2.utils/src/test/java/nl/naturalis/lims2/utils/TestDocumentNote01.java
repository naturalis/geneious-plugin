package nl.naturalis.lims2.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.biomatters.geneious.publicapi.documents.DocumentNote;
import com.biomatters.geneious.publicapi.documents.DocumentNoteField;

public class TestDocumentNote01 implements DocumentNote {

	private String description;
	private String name;
	private String noteTypeCode;

	private HashMap<String, Object> fieldValues = new HashMap<>();
	private List<DocumentNoteField> fields = new ArrayList<>();

	@Override
	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	@Override
	public Object getFieldValue(String arg0)
	{
		return fieldValues.get(arg0);
	}

	@Override
	public boolean setFieldValue(String arg0, Object arg1)
	{
		fieldValues.put(arg0, arg1);
		return true;
	}

	@Override
	public List<DocumentNoteField> getFields()
	{
		return fields;
	}

	public void addField(DocumentNoteField field)
	{
		fields.add(field);
	}

	@Override
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public String getNoteTypeCode()
	{
		return noteTypeCode;
	}

	public void setNoteTypeCode(String noteTypeCode)
	{
		this.noteTypeCode = noteTypeCode;
	}

}
