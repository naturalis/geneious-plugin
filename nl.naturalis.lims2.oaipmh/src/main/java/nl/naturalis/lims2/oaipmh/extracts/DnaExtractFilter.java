package nl.naturalis.lims2.oaipmh.extracts;

import java.sql.ResultSet;
import java.sql.SQLException;

import nl.naturalis.lims2.oaipmh.AnnotatedDocument;
import nl.naturalis.lims2.oaipmh.IAnnotatedDocumentPostFilter;
import nl.naturalis.lims2.oaipmh.IAnnotatedDocumentPreFilter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Implements both pre and post filtering for DNA extracts. Only the method
 * specified by {@link IAnnotatedDocumentPreFilter} actually contains filter
 * logic. The method specified by {@link IAnnotatedDocumentPostFilter} currently
 * just returns {@code true}.
 * 
 * @author Ayco Holleman
 *
 */
public class DnaExtractFilter implements IAnnotatedDocumentPostFilter, IAnnotatedDocumentPreFilter {

	private static final Logger logger = LogManager.getLogger(DnaExtractFilter.class);

	public DnaExtractFilter()
	{
	}

	@Override
	public boolean accept(ResultSet rs) throws SQLException
	{
		// Some bare-knuckle XML parsing here for fail-fast processing
		String xml = rs.getString("document_xml");

		if (xml.indexOf("<ExtractIDCode_Samples>") == -1) {
			if (logger.isDebugEnabled()) {
				logger.debug("Record discarded: document_xml column does not contain string \"<ExtractIDCode_Samples>\"");
			}
			return false;
		}
		return true;
	}

	@Override
	public boolean accept(AnnotatedDocument ad)
	{
		return true;
	}

}
