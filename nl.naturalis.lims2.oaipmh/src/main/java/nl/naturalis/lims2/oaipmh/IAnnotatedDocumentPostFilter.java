package nl.naturalis.lims2.oaipmh;

public interface IAnnotatedDocumentPostFilter {

	boolean accept(AnnotatedDocument ad);

}