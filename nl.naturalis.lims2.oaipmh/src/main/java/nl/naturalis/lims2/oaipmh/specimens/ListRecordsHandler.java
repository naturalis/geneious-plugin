package nl.naturalis.lims2.oaipmh.specimens;

import static nl.naturalis.lims2.oaipmh.DocumentNotes.Field.BOLDIDCode_BOLD;
import static nl.naturalis.lims2.oaipmh.DocumentNotes.Field.BOLDURICode_FixedValue;
import static nl.naturalis.lims2.oaipmh.DocumentNotes.Field.NumberOfImagesCode_BOLD;
import static nl.naturalis.lims2.oaipmh.DocumentNotes.Field.RegistrationNumberCode_Samples;
import static nl.naturalis.oaipmh.api.util.OAIPMHUtil.createResponseSkeleton;
import static nl.naturalis.oaipmh.api.util.OAIPMHUtil.dateTimeFormatter;
import static nl.naturalis.oaipmh.api.util.ObjectFactories.oaiFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nl.naturalis.lims2.oaipmh.AbstractListRecordsHandler;
import nl.naturalis.lims2.oaipmh.AnnotatedDocument;
import nl.naturalis.lims2.oaipmh.DocumentNotes;
import nl.naturalis.lims2.oaipmh.IAnnotatedDocumentPostFilter;
import nl.naturalis.lims2.oaipmh.IAnnotatedDocumentPreFilter;
import nl.naturalis.lims2.oaipmh.Lims2OAIUtil;
import nl.naturalis.lims2.oaipmh.jaxb.Geneious;
import nl.naturalis.lims2.oaipmh.jaxb.Specimen;
import nl.naturalis.lims2.oaipmh.jaxb.SpecimenUnit;
import nl.naturalis.oaipmh.api.OAIPMHException;
import nl.naturalis.oaipmh.api.OAIPMHRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.domainobject.util.ConfigObject;
import org.openarchives.oai._2.HeaderType;
import org.openarchives.oai._2.ListRecordsType;
import org.openarchives.oai._2.MetadataType;
import org.openarchives.oai._2.OAIPMHtype;
import org.openarchives.oai._2.RecordType;

class ListRecordsHandler extends AbstractListRecordsHandler {

	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger(ListRecordsHandler.class);

	public ListRecordsHandler(ConfigObject config, OAIPMHRequest request)
	{
		super(config, request);
	}

	// OAIPMHtype handleRequest_old() throws OAIPMHException
	// {
	// Lims2OAIUtil.checkMetadataPrefix(request);
	// OAIPMHtype root = createResponseSkeleton(request);
	// ListRecordsType listRecords = oaiFactory.createListRecordsType();
	// root.setListRecords(listRecords);
	// RecordType record = oaiFactory.createRecordType();
	// listRecords.getRecord().add(record);
	//
	// HeaderType header = oaiFactory.createHeaderType();
	// record.setHeader(header);
	// header.setIdentifier("154383046");
	// header.setDatestamp(dateTimeFormatter.format(new Date()));
	//
	// MetadataType metadata = oaiFactory.createMetadataType();
	// record.setMetadata(metadata);
	// Geneious geneious = new Geneious();
	// metadata.setAny(geneious);
	// Specimen specimen = new Specimen();
	// geneious.setSpecimen(specimen);
	// SpecimenUnit unit = new SpecimenUnit();
	// specimen.setUnit(unit);
	// unit.setUnitID("RMNH.INS.23917");
	// unit.setAssociatedUnitID("ANTVI001-11");
	// unit.setUri("http://www.boldsystems.org/index.php/Public_RecordView?processid=ANTVI001-11");
	// unit.setMultiMediaObjectComment(1);
	//
	// return root;
	// }

	// OAIPMHtype handleRequest() throws RepositoryException, OAIPMHException
	// {
	// checkMetadataPrefix(request);
	// addAnnotatedDocumentPreFilter(new SpecimenFilter());
	// List<AnnotatedDocument> records = getAnnotatedDocuments();
	// if (records.size() == 0) {
	// throw new OAIPMHException(new NoRecordsMatchError());
	// }
	// OAIPMHtype root = createResponseSkeleton(request);
	// ListRecordsType listRecords = oaiFactory.createListRecordsType();
	// root.setListRecords(listRecords);
	// int pageSize = config.getInt("specimens.repo.pagesize");
	// int offset = request.getPage() * pageSize;
	// int last = Math.min(records.size(), offset + pageSize);
	// logResultSetInfo(records.size(), pageSize);
	// for (int i = offset; i < last; ++i) {
	// addRecord(records.get(i), listRecords);
	// }
	// if (last < records.size()) {
	// addResumptionToken(listRecords, records.size(), offset);
	// }
	// return root;
	// }

	// private static void addRecord(AnnotatedDocument ad, ListRecordsType
	// listRecords)
	// {
	// RecordType record = oaiFactory.createRecordType();
	// listRecords.getRecord().add(record);
	// record.setHeader(createHeader(ad));
	// record.setMetadata(createMetadata(ad));
	// }
	//
	// private static HeaderType createHeader(AnnotatedDocument ad)
	// {
	// HeaderType header = oaiFactory.createHeaderType();
	// header.setIdentifier(String.valueOf(ad.getId()));
	// long modified = 1000L * ad.getModified();
	// header.setDatestamp(dateTimeFormatter.format(new Date(modified)));
	// return header;
	// }
	//
	// private static MetadataType createMetadata(AnnotatedDocument ad)
	// {
	// MetadataType metadata = oaiFactory.createMetadataType();
	// Geneious geneious = new Geneious();
	// metadata.setAny(geneious);
	// Specimen specimen = new Specimen();
	// geneious.setSpecimen(specimen);
	// specimen.setUnit(createSpecimenUnit(ad));
	// return metadata;
	// }
	//
	// private static SpecimenUnit createSpecimenUnit(AnnotatedDocument ad)
	// {
	// SpecimenUnit unit = new SpecimenUnit();
	// DocumentNotes notes = ad.getDocument().getNotes();
	// unit.setUnitID(notes.get(RegistrationNumberCode_Samples));
	// unit.setAssociatedUnitID(notes.get(BOLDIDCode_BOLD));
	// unit.setUri(notes.get(BOLDURICode_FixedValue));
	// String s = notes.get(NumberOfImagesCode_BOLD);
	// if (s != null) {
	// Integer i = Integer.valueOf(s);
	// unit.setMultiMediaObjectComment(i);
	// }
	// return unit;
	// }

	@Override
	protected List<IAnnotatedDocumentPreFilter> getAnnotatedDocumentPreFilters()
	{
		List<IAnnotatedDocumentPreFilter> filters = new ArrayList<>(1);
		filters.add(new SpecimenFilter());
		return filters;
	}

	@Override
	protected List<IAnnotatedDocumentPostFilter> getAnnotatedDocumentPostFilters()
	{
		return new ArrayList<>(0);
	}

	@Override
	protected void setMetadata(Geneious geneious, AnnotatedDocument ad)
	{
		Specimen specimen = new Specimen();
		geneious.setSpecimen(specimen);
		SpecimenUnit unit = new SpecimenUnit();
		specimen.setUnit(unit);
		DocumentNotes notes = ad.getDocument().getNotes();
		unit.setUnitID(notes.get(RegistrationNumberCode_Samples));
		unit.setAssociatedUnitID(notes.get(BOLDIDCode_BOLD));
		unit.setUri(notes.get(BOLDURICode_FixedValue));
		String s = notes.get(NumberOfImagesCode_BOLD);
		if (s != null) {
			Integer i = Integer.valueOf(s);
			unit.setMultiMediaObjectComment(i);
		}
	}

	@Override
	protected int getPageSize()
	{
		return config.getInt("specimens.repo.pagesize");
	}

}
