package nl.naturalis.lims2.oaipmh;

import nl.naturalis.oaipmh.api.CannotDisseminateFormatError;
import nl.naturalis.oaipmh.api.OAIPMHException;
import nl.naturalis.oaipmh.api.OAIPMHRequest;

public class Lims2OAIUtil {

	public static final String LIMS2_XMLNS = "http://data.naturalis.nl/lims2";
	public static final String LIMS2_XMLNS_PREFIX = "lims2";

	private Lims2OAIUtil()
	{
	}

	public static void checkMetadataPrefix(OAIPMHRequest request) throws OAIPMHException
	{
		if (!request.getMetadataPrefix().equals("lims2"))
			throw new OAIPMHException(new CannotDisseminateFormatError("lims2"));
	}

}
