package nl.naturalis.lims2.oaipmh.specimens;

import java.util.List;

import nl.naturalis.oaipmh.api.IOAIRepository;
import nl.naturalis.oaipmh.api.IResumptionTokenParser;
import nl.naturalis.oaipmh.api.OAIPMHRequest;
import nl.naturalis.oaipmh.api.RepositoryException;

import org.openarchives.oai._2.GetRecordType;
import org.openarchives.oai._2.IdentifyType;
import org.openarchives.oai._2.ListIdentifiersType;
import org.openarchives.oai._2.ListMetadataFormatsType;
import org.openarchives.oai._2.ListRecordsType;
import org.openarchives.oai._2.ListSetsType;
import org.openarchives.oai._2.OAIPMHerrorType;

public class SpecimenOAIRepository implements IOAIRepository {

	private OAIPMHRequest request;

	public SpecimenOAIRepository()
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
	public GetRecordType getRecord() throws RepositoryException
	{
		return null;
	}

	@Override
	public ListRecordsType listRecords() throws RepositoryException
	{
		ListRecordsHandler handler = new ListRecordsHandler(request);
		return handler.handleRequest();
	}

	@Override
	public ListIdentifiersType listIdentifiers() throws RepositoryException
	{
		return null;
	}

	@Override
	public ListMetadataFormatsType listMetaDataFormats() throws RepositoryException
	{
		return null;
	}

	@Override
	public ListSetsType listSets() throws RepositoryException
	{
		return null;
	}

	@Override
	public IdentifyType identify() throws RepositoryException
	{
		return null;
	}

	@Override
	public List<OAIPMHerrorType> getErrors()
	{
		return null;
	}

	@Override
	public void done()
	{
	}

}
