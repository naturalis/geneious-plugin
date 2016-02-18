package nl.naturalis.lims2.oaipmh;

import java.util.List;

public interface IAnnotatedDocumentSetFilter {

	List<AnnotatedDocument> filter(List<AnnotatedDocument> input);

}