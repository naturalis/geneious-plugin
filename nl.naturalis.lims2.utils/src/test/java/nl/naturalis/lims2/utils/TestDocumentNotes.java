package nl.naturalis.lims2.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument.DocumentNotes;
import com.biomatters.geneious.publicapi.documents.DocumentNote;

public class TestDocumentNotes implements DocumentNotes {

	private HashMap<String, DocumentNote> notes = new HashMap<>();

	@Override
	public List<DocumentNote> getAllNotes()
	{
		return new ArrayList<>(notes.values());
	}

	@Override
	public DocumentNote getNote(String arg0)
	{
		return notes.get(arg0);
	}

	@Override
	public void removeNote(String arg0)
	{
		notes.remove(arg0);
	}

	@Override
	public void saveNotes()
	{
	}

	@Override
	public void setNote(DocumentNote arg0)
	{
		notes.put(arg0.getNoteTypeCode(), arg0);
	}

}
