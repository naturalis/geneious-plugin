package nl.naturalis.lims2.oaipmh.slides;

import java.io.OutputStream;

import nl.naturalis.lims2.oaipmh.Lims2OAIRepository;
import nl.naturalis.oaipmh.api.OAIPMHException;
import nl.naturalis.oaipmh.api.RepositoryException;

import org.openarchives.oai._2.OAIPMHtype;

public class DNASlideOAIRepository extends Lims2OAIRepository {

	public DNASlideOAIRepository()
	{
		super();
	}

	@Override
	public void listRecords(OutputStream out) throws OAIPMHException, RepositoryException
	{
		ListRecordsHandler handler = new ListRecordsHandler();
		OAIPMHtype oaipmh = handler.handleRequest(request);
		stream(oaipmh, out);
	}

}
