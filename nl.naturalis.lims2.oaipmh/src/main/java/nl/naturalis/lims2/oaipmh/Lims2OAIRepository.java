package nl.naturalis.lims2.oaipmh;

import java.io.InputStream;

import nl.naturalis.oaipmh.api.IOAIRepository;
import nl.naturalis.oaipmh.api.IResumptionTokenParser;
import nl.naturalis.oaipmh.api.OAIPMHRequest;
import nl.naturalis.oaipmh.api.RepositoryException;

import org.domainobject.util.FileUtil;

public abstract class Lims2OAIRepository implements IOAIRepository {

	protected OAIPMHRequest request;

	public Lims2OAIRepository()
	{
	}

	@Override
	public String getXSDForNamespacePrefix(String namespacePrefix)
	{
		if (namespacePrefix.equals("lims2")) {
			InputStream is = getClass().getResourceAsStream("/geneious.xsd");
			return FileUtil.getContents(is);
		}
		return null;
	}

	@Override
	public void init(OAIPMHRequest request)
	{
		this.request = request;
	}

	@Override
	public IResumptionTokenParser getResumptionTokenParser()
	{
		return null;
	}

	@Override
	public String listMetaDataFormats() throws RepositoryException
	{
		return null;
	}

	@Override
	public void done()
	{
	}

}
