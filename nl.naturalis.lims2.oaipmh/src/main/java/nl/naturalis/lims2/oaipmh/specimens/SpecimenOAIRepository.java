package nl.naturalis.lims2.oaipmh.specimens;

import nl.naturalis.lims2.oaipmh.Lims2OAIRepository;
import nl.naturalis.oaipmh.api.RepositoryException;

public class SpecimenOAIRepository extends Lims2OAIRepository {

	public SpecimenOAIRepository()
	{
		super();
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
	public String listSets() throws RepositoryException
	{
		return null;
	}

	@Override
	public String identify() throws RepositoryException
	{
		return null;
	}

}
