package nl.naturalis.lims2.oaipmh.plates;

import java.io.OutputStream;

import nl.naturalis.lims2.oaipmh.Lims2OAIRepository;
import nl.naturalis.oaipmh.api.OAIPMHException;
import nl.naturalis.oaipmh.api.RepositoryException;

import org.openarchives.oai._2.OAIPMHtype;

public class DnaExtractPlateOAIRepository extends Lims2OAIRepository {

	public DnaExtractPlateOAIRepository()
	{
		super();
	}

	@Override
	public void listRecords(OutputStream out) throws OAIPMHException, RepositoryException
	{
		DnaExtractPlateListRecordsHandler handler = new DnaExtractPlateListRecordsHandler();
		OAIPMHtype oaipmh = handler.handleRequest(request);
		stream(oaipmh, out);
	}

}
