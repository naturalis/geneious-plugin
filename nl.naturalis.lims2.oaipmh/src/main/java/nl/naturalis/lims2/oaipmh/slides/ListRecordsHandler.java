package nl.naturalis.lims2.oaipmh.slides;

import static nl.naturalis.oaipmh.api.util.OAIPMHUtil.dateTimeFormatter;
import static nl.naturalis.oaipmh.api.util.ObjectFactories.oaiFactory;

import java.util.Date;

import javax.xml.bind.JAXBException;

import nl.naturalis.lims2.oaipmh.jaxb.DNASlide;
import nl.naturalis.lims2.oaipmh.jaxb.Geneious;
import nl.naturalis.lims2.oaipmh.jaxb.SlideUnit;
import nl.naturalis.oaipmh.api.OAIPMHRequest;
import nl.naturalis.oaipmh.api.RepositoryException;
import nl.naturalis.oaipmh.api.util.OAIPMHMarshaller;
import nl.naturalis.oaipmh.api.util.OAIPMHUtil;

import org.openarchives.oai._2.HeaderType;
import org.openarchives.oai._2.ListRecordsType;
import org.openarchives.oai._2.MetadataType;
import org.openarchives.oai._2.OAIPMHtype;
import org.openarchives.oai._2.RecordType;

class ListRecordsHandler {

	private final OAIPMHRequest request;

	public ListRecordsHandler(OAIPMHRequest request)
	{
		this.request = request;
	}

	public String handleRequest() throws RepositoryException
	{
		OAIPMHtype root = OAIPMHUtil.createResponseSkeleton(request);
		ListRecordsType listRecords = oaiFactory.createListRecordsType();
		root.setListRecords(listRecords);
		RecordType record = oaiFactory.createRecordType();
		listRecords.getRecord().add(record);
		HeaderType header = oaiFactory.createHeaderType();
		record.setHeader(header);
		header.setIdentifier("123423046");
		header.setDatestamp(dateTimeFormatter.format(new Date()));
		MetadataType metadata = oaiFactory.createMetadataType();
		record.setMetadata(metadata);
		Geneious geneious = new Geneious();
		metadata.setAny(geneious);
		DNASlide slide = new DNASlide();
		geneious.setDnaSlide(slide);
		SlideUnit unit = new SlideUnit();
		slide.setUnit(unit);
		unit.setBatchID("BCP1234-99");
		unit.setInstituePlateID("NBCN123456");
		OAIPMHMarshaller marshaller = new OAIPMHMarshaller();
		marshaller.setRootElement(root);
		marshaller.addJaxbPackage("nl.naturalis.lims2.oaipmh.jaxb");
		marshaller.addSchemaLocation("http://data.naturalis.nl/lims2",
				"http://data.naturalis.nl/lims2/LIMS2.xsd");
		try {
			return marshaller.marshal();
		}
		catch (JAXBException e) {
			throw new RepositoryException(e);
		}

	}

}
