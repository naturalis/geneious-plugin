package nl.naturalis.lims2.oaipmh;

import static nl.naturalis.lims2.oaipmh.DefaultAlignmentDocument.Field.is_contig;
import static nl.naturalis.lims2.oaipmh.PluginDocumentData.RootElement.DEFAULT_ALIGNMENT_DOCUMENT;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Filters (eliminates) records from the annotated_document table after they
 * have been converted to {@link AnnotatedDocument} instances by an
 * {@link AnnotatedDocumentFactory}. This class contains fine-grained logic for
 * determining if a record should be part of the OAI-PMH output.
 * 
 * @author Ayco Holleman
 *
 */
public class AnnotatedDocumentPostFilter {

	private static final Logger logger = LogManager.getLogger(AnnotatedDocumentPostFilter.class);

	public AnnotatedDocumentPostFilter()
	{
	}

	@SuppressWarnings("static-method")
	public boolean accept(AnnotatedDocument ad)
	{
		if (ad.getDocument() == null || ad.getPluginDocumentData() == null) {
			/*
			 * Caused by invalid XML, but this is already logged the
			 * AnnotatedDocumentFactory.
			 */
			return false;
		}
		if (ad.getDocument().getNotes() == null) {
			if (logger.isDebugEnabled())
				logger.debug("Record discarded: no usable <note> elements");
			return false;
		}
		if (ad.getPluginDocumentData().getRootElement() == DEFAULT_ALIGNMENT_DOCUMENT) {
			DefaultAlignmentDocument dad = (DefaultAlignmentDocument) ad.getPluginDocumentData();
			if (dad.get(is_contig) == Boolean.FALSE) {
				if (logger.isDebugEnabled())
					logger.debug("Record discarded: <DefaultAlignmentDocument> only considered when is_contig=\"true\"");
				return false;
			}
		}
		return true;
	}

}
