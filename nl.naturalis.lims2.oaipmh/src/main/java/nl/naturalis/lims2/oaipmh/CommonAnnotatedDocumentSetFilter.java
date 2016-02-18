package nl.naturalis.lims2.oaipmh;

import java.util.Collections;
import java.util.List;

public class CommonAnnotatedDocumentSetFilter implements IAnnotatedDocumentSetFilter {

	@Override
	public List<AnnotatedDocument> filter(List<AnnotatedDocument> input)
	{
		ReferenceComparator comparator = new ReferenceComparator();
		Collections.sort(input, comparator);
		return input.subList(0, input.size() - comparator.countDispensableRecords());
	}

}
