package nl.naturalis.lims2.oaipmh.extracts;

import java.io.OutputStream;

import nl.naturalis.lims2.oaipmh.Lims2OAIRepository;
import nl.naturalis.oaipmh.api.OAIPMHException;
import nl.naturalis.oaipmh.api.RepositoryException;

import org.openarchives.oai._2.OAIPMHtype;

/**
 * OAI repository for DNA extracts.
 * 
 * @author Ayco Holleman
 *
 */
public class DNAExtractOAIRepository extends Lims2OAIRepository {

	public DNAExtractOAIRepository()
	{
		super();
	}

	@Override
	public void listRecords(OutputStream out) throws OAIPMHException, RepositoryException
	{
		ListRecordsHandler handler = new ListRecordsHandler();
		OAIPMHtype oaipmh = handler.handleRequest(request);
		marshal(oaipmh, out);
	}

}
