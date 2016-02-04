package nl.naturalis.lims2.oaipmh;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Filters (eliminates) records from the annotated_document table after they
 * have been converted to {@link AnnotatedDocument} instances by an
 * {@link AnnotatedDocumentFactory}. This class contains course-grained logic
 * for filtering out irrelevant or corrupt records.
 * 
 * @author Ayco Holleman
 *
 */
public class AnnotatedDocumentPreFilter {

	private static final Logger logger = LogManager.getLogger(AnnotatedDocumentPreFilter.class);

	private static List<String> startStrings = Arrays.asList("<XMLSerialisableRootElement",
			"<ABIDocument", "<DefaultAlignmentDocument");

	public AnnotatedDocumentPreFilter()
	{
	}

	@SuppressWarnings("static-method")
	public boolean accept(ResultSet rs) throws SQLException
	{
		String xml = rs.getString("plugin_document_xml");
		boolean ok = false;
		for (String s : startStrings) {
			if (ok = ok || xml.startsWith(s)) {
				break;
			}
		}
		if (!ok && logger.isDebugEnabled()) {

		}
		return ok;
	}
}
