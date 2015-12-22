package nl.naturalis.lims2.oaipmh.extracts;

import nl.naturalis.oaipmh.api.IOAIRepository;
import nl.naturalis.oaipmh.api.IResumptionTokenParser;
import nl.naturalis.oaipmh.api.OAIPMHRequest;
import nl.naturalis.oaipmh.api.RepositoryException;

public class DNAExtractOAIRepository implements IOAIRepository {

	private OAIPMHRequest request;

	public DNAExtractOAIRepository()
	{
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
	public String getRecord() throws RepositoryException
	{
		return null;
	}

	@Override
	public String listRecords() throws RepositoryException
	{
		ListRecordsHandler handler = new ListRecordsHandler(request);
		return handler.handleRequest();
	}

	@Override
	public String listIdentifiers() throws RepositoryException
	{
		return null;
	}

	@Override
	public String listMetaDataFormats() throws RepositoryException
	{
		return null;
	}

	@Override
	public String listSets() throws RepositoryException
	{
		return null;
	}

	@Override
	public String identify() throws RepositoryException
	{
		return null;
	}

	@Override
	public void done()
	{
	}

}
