package nl.naturalis.lims2.oaipmh;

import java.util.Collection;
import java.util.List;

public class AnnotatedDocumentSetFilter {

	private List<AnnotatedDocument> input;

	public AnnotatedDocumentSetFilter(List<AnnotatedDocument> set)
	{
		this.input = set;
	}

	public Collection<AnnotatedDocument> filter()
	{
		return input;
	}

	private boolean isReferencedInSet(AnnotatedDocument referenced)
	{
		for (AnnotatedDocument referencer : input) {
			// referencer.getDocument().
		}
		return false;
	}

}
