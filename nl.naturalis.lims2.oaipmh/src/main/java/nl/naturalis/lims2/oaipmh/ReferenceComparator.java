package nl.naturalis.lims2.oaipmh;

import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Compares {@link AnnotatedDocument} instances based on whether one references
 * the other or vice versa. If one {@code AnnotatedDocument} references another
 * {@code AnnotatedDocument}, it means it has superseded the other
 * {@code AnnotatedDocument}. Thus when sorting {@link AnnotatedDocument}
 * instances, those that reference others should be pushed up, while those that
 * <i>are</i> referenced should be pushed down.
 * 
 * @author Ayco Holleman
 *
 */
public class ReferenceComparator implements Comparator<AnnotatedDocument> {

	private static final Logger logger = LogManager.getLogger(ReferenceComparator.class);

	private static final String MSG_DISCARD = "Record with id {} is referenced by "
			+ "record with id {}. It will be discarded.";

	public ReferenceComparator()
	{
	}

	private int numDispensable;

	/**
	 * Compares the specified {@link AnnotatedDocument} instances. A side-effect
	 * of the comparison is that the referenced document will be marked as
	 * dispensable. That is, it will be excluded from the OAI-PMH output.
	 */
	@Override
	public int compare(AnnotatedDocument ad0, AnnotatedDocument ad1)
	{
		// Does the 1st document reference the 2nd?
		List<String> urns = ad0.getDocument().getReferencedDocuments();
		if (urns != null && urns.contains(ad1.getUrn())) {
			if (!ad1.dispensable) {
				if (logger.isDebugEnabled()) {
					logger.debug(MSG_DISCARD, ad1.getId(), ad0.getId());
				}
				++numDispensable;
				ad1.dispensable = true;
			}
			return -1;
		}
		// Does the 2nd document reference the 2nd?
		urns = ad1.getDocument().getReferencedDocuments();
		if (urns != null && urns.contains(ad0.getUrn())) {
			if (!ad0.dispensable) {
				if (logger.isDebugEnabled()) {
					logger.debug(MSG_DISCARD, ad0.getId(), ad1.getId());
				}
				++numDispensable;
				ad0.dispensable = true;
			}
			return 1;
		}
		return 0;
	}

	public int countDispensableRecords()
	{
		return numDispensable;
	}

}
