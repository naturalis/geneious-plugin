package nl.naturalis.lims2.utils;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jebl.util.ProgressListener;

import org.jdom.Element;

import com.biomatters.geneious.publicapi.databaseservice.DatabaseService;
import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentField;
import com.biomatters.geneious.publicapi.documents.DocumentHistory;
import com.biomatters.geneious.publicapi.documents.DocumentListener;
import com.biomatters.geneious.publicapi.documents.ElementProvider;
import com.biomatters.geneious.publicapi.documents.PluginDocument;
import com.biomatters.geneious.publicapi.documents.URN;
import com.biomatters.geneious.publicapi.documents.XMLSerializationException;
import com.biomatters.geneious.publicapi.plugin.DocumentOperationException;
import com.biomatters.geneious.publicapi.plugin.Geneious.MajorVersion;

public class TestAnnotatedPluginDocument extends AnnotatedPluginDocument {

	private DocumentNotes documentNotes;

	public void setDocumentNotes(DocumentNotes documentNotes)
	{
		this.documentNotes = documentNotes;
	}

	@Override
	public void fromXML(Element arg0) throws XMLSerializationException
	{
	}

	@Override
	public Element toXML()
	{
		return null;
	}

	@Override
	public void addDescendantOperationRecord(URN arg0)
	{
	}

	@Override
	public void addWeakReferenceDocumentListener(DocumentListener arg0)
	{
	}

	@Override
	public void additionalXmlChanged()
	{
	}

	@Override
	public void changeReferencedDocumentURNs(Map<URN, URN> arg0)
	{
	}

	@Override
	public String confirmDocumentValid()
	{
		return null;
	}

	@Override
	public void documentChanged(Element arg0)
	{
	}

	@Override
	public Element getAdditionalXml(String arg0, boolean arg1) throws IOException
	{
		return null;
	}

	@Override
	public Map<String, ElementProvider> getAdditionalXml(List<String> arg0, boolean arg1,
			ProgressListener arg2) throws IOException
	{
		return null;
	}

	@Override
	public Date getCreationDate()
	{
		return null;
	}

	@Override
	public DatabaseService getDatabase()
	{
		return null;
	}

	@Override
	public List<URN> getDescendantOperationRecords()
	{
		return null;
	}

	@Override
	public List<DocumentField> getDisplayableFields()
	{
		return null;
	}

	@Override
	public PluginDocument getDocument() throws DocumentOperationException
	{
		return null;
	}

	@Override
	public Class<? extends PluginDocument> getDocumentClass()
	{
		return null;
	}

	@Override
	public DocumentHistory getDocumentHistory() throws IOException
	{
		return null;
	}

	@Override
	public DocumentNotes getDocumentNotes(boolean arg0)
	{
		return documentNotes;
	}

	@Override
	public PluginDocument getDocumentOrCrash()
	{
		return null;
	}

	@Override
	public PluginDocument getDocumentOrNull()
	{
		return null;
	}

	@Override
	public <T extends Exception> PluginDocument getDocumentOrThrow(Class<T> arg0) throws T
	{
		return null;
	}

	@Override
	public <T extends Exception> PluginDocument getDocumentOrThrow(boolean arg0,
			ProgressListener arg1, Class<T> arg2) throws T
	{
		return null;
	}

	@Override
	public List<DocumentField> getExtendedDisplayableFields()
	{
		return null;
	}

	@Override
	public Object getFieldValue(String arg0)
	{
		return null;
	}

	@Override
	public Object getFieldValue(DocumentField arg0)
	{
		return null;
	}

	@Override
	public String getName()
	{
		return null;
	}

	@Override
	public URN getParentOperationRecord()
	{
		return null;
	}

	@Override
	public Element getPluginDocumentXml(ProgressListener arg0) throws IOException
	{
		return null;
	}

	@Override
	public List<String> getPotentialAdditionalXmlKeys(boolean arg0) throws IOException
	{
		return null;
	}

	@Override
	public Set<URN> getReferencedDocuments()
	{
		return null;
	}

	@Override
	public int getRevisionNumber()
	{
		return 0;
	}

	@Override
	public Map<DocumentField, List<Object>> getSearchableFieldsAndValues(ProgressListener arg0)
			throws IOException
	{
		return null;
	}

	@Override
	public Map<DocumentField, List<Object>> getSearchableFieldsAndValuesForAnnotatedDocumentOnly()
			throws IOException
	{
		return null;
	}

	@Override
	public long getSize()
	{
		return 0;
	}

	@Override
	public Set<URN> getStronglyReferencedDocuments()
	{
		return null;
	}

	@Override
	public String getSummary()
	{
		return null;
	}

	@Override
	public Object getTemporaryFieldValue(String arg0)
	{
		return null;
	}

	@Override
	public URN getURN()
	{
		return null;
	}

	@Override
	public Set<URN> getWeaklyReferencedDocuments()
	{
		return null;
	}

	@Override
	public boolean i()
	{
		return false;
	}

	@Override
	public boolean isDeletedFromWritableDatabaseService()
	{
		return false;
	}

	@Override
	public boolean isInLocalRepository()
	{
		return false;
	}

	@Override
	public boolean isUnread()
	{
		return false;
	}

	@Override
	public void removeWeakReferenceDocumentListener(DocumentListener arg0)
	{
	}

	@Override
	public void save()
	{
	}

	@Override
	public void save(boolean arg0)
	{
	}

	@Override
	public void saveDocument()
	{
	}

	@Override
	public boolean saveDocument(ActiveLinkSaveBehaviour arg0, boolean arg1, ProgressListener arg2)
	{
		return false;
	}

	@Override
	public void setAdditionalXml(String arg0, boolean arg1, Element arg2) throws IOException
	{
	}

	@Override
	public void setAdditionalXml(Map<String, ElementProvider> arg0, boolean arg1,
			ProgressListener arg2) throws IOException
	{
	}

	@Override
	public void setDatabase(DatabaseService arg0)
	{
	}

	@Override
	public void setFieldValue(DocumentField arg0, Object arg1)
	{
	}

	@Override
	public void setHiddenFieldValue(DocumentField arg0, Object arg1)
	{
	}

	@Override
	public void setName(String arg0)
	{
	}

	@Override
	public void setParentOperationRecord(URN arg0)
	{
	}

	@Override
	public void setSourceService(DatabaseService arg0)
	{
	}

	@Override
	public void setSummary(String arg0)
	{
	}

	@Override
	public void setTemporaryFieldValue(String arg0, Object arg1)
	{
	}

	@Override
	public boolean setUnread(boolean arg0)
	{
		return false;
	}

	@Override
	public AnnotatedPluginDocument toSummaryDocument()
	{
		return null;
	}

	@Override
	public Element toXMLExcludingInternalPluginDocument(MajorVersion arg0)
	{
		return null;
	}

}
