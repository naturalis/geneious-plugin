package nl.naturalis.lims2.oaipmh;

import static nl.naturalis.lims2.oaipmh.Lims2OAIUtil.LIMS2_XMLNS;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBException;

import nl.naturalis.oaipmh.api.IOAIRepository;
import nl.naturalis.oaipmh.api.IResumptionTokenParser;
import nl.naturalis.oaipmh.api.OAIPMHException;
import nl.naturalis.oaipmh.api.OAIPMHRequest;
import nl.naturalis.oaipmh.api.RepositoryException;
import nl.naturalis.oaipmh.api.XSDNotFoundException;
import nl.naturalis.oaipmh.api.util.OAIPMHMarshaller;

import org.domainobject.util.IOUtil;
import org.openarchives.oai._2.OAIPMHtype;

public abstract class Lims2OAIRepository implements IOAIRepository {

	protected OAIPMHRequest request;
	protected String repoBaseURL;

	public Lims2OAIRepository()
	{
	}

	@Override
	public void setRepositoryBaseUrl(String url)
	{
		this.repoBaseURL = url;
	}

	@Override
	public void getXSDForMetadataPrefix(OutputStream out, String prefix) throws RepositoryException
	{
		if (prefix.equals("lims2")) {
			InputStream in = getClass().getResourceAsStream("/geneious.xsd");
			if (in == null)
				throw new XSDNotFoundException(prefix);
			IOUtil.pipe(in, out, 2048);
			try {
				in.close();
			}
			catch (IOException e) {
				throw new RepositoryException(e);
			}
		}
		throw new XSDNotFoundException(prefix);
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
	public void getRecord(OutputStream out) throws OAIPMHException, RepositoryException
	{
		throw new RepositoryException("GetRecord request not implemented yet");
	}

	@Override
	public void listIdentifiers(OutputStream out) throws OAIPMHException, RepositoryException
	{
		throw new RepositoryException("ListIdentifiers request not implemented yet");
	}

	@Override
	public void listSets(OutputStream out) throws OAIPMHException, RepositoryException
	{
		throw new RepositoryException("ListSets request not implemented yet");
	}

	@Override
	public void identify(OutputStream out) throws OAIPMHException, RepositoryException
	{
		throw new RepositoryException("Identify request not implemented yet");
	}

	@Override
	public void listMetaDataFormats(OutputStream out) throws OAIPMHException, RepositoryException
	{
		throw new RepositoryException("ListMetaDataFormats request not implemented yet");
	}

	@Override
	public void done()
	{
	}

	protected void marshal(OAIPMHtype oaipmh, OutputStream out) throws RepositoryException
	{
		OAIPMHMarshaller marshaller = new OAIPMHMarshaller();
		marshaller.setRootElement(oaipmh);
		marshaller.addJaxbPackage("nl.naturalis.lims2.oaipmh.jaxb");
		String schemaLocation = repoBaseURL + "xsd/lims2.xsd";
		marshaller.addSchemaLocation(LIMS2_XMLNS, schemaLocation);
		try {
			marshaller.marshal(out);
		}
		catch (JAXBException e) {
			throw new RepositoryException(e);
		}
	}

}
