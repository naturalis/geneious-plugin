package nl.naturalis.lims2.oaipmh;

import nl.naturalis.oaipmh.api.CannotDisseminateFormatError;
import nl.naturalis.oaipmh.api.OAIPMHException;
import nl.naturalis.oaipmh.api.OAIPMHRequest;

/**
 * Provides common functionality for LIMS2/Geneious repositories.
 * 
 * @author Ayco Holleman
 *
 */
public class Lims2OAIUtil {

	/**
	 * XML namespace for lims2 elements (http://data.naturalis.nl/lims2).
	 */
	public static final String LIMS2_XMLNS = "http://data.naturalis.nl/lims2";
	/**
	 * XML namespace prefix for lims2 elements (lims2).
	 */
	public static final String LIMS2_XMLNS_PREFIX = "lims2";

	private Lims2OAIUtil()
	{
	}

	/**
	 * Make sure metadataPrefix argument is "lims2".
	 * 
	 * @param request
	 * @throws OAIPMHException
	 */
	public static void checkMetadataPrefix(OAIPMHRequest request) throws OAIPMHException
	{
		if (!request.getMetadataPrefix().equals("lims2"))
			throw new OAIPMHException(new CannotDisseminateFormatError(request));
	}

}
