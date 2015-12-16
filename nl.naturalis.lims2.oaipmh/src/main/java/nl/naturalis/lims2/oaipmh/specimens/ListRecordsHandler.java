package nl.naturalis.lims2.oaipmh.specimens;

import static nl.naturalis.oaipmh.api.util.OAIPMHUtil.dateTimeFormatter;
import static nl.naturalis.oaipmh.api.util.ObjectFactories.oaiDcFactory;
import static nl.naturalis.oaipmh.api.util.ObjectFactories.oaiFactory;

import java.util.Date;

import nl.naturalis.oaipmh.api.OAIPMHRequest;

import org.openarchives.oai._2.HeaderType;
import org.openarchives.oai._2.ListRecordsType;
import org.openarchives.oai._2.MetadataType;
import org.openarchives.oai._2.RecordType;
import org.openarchives.oai._2_0.oai_dc.OaiDcType;

public class ListRecordsHandler {

	private final OAIPMHRequest request;

	public ListRecordsHandler(OAIPMHRequest request)
	{
		this.request = request;
	}

	public ListRecordsType handleRequest()
	{
		ListRecordsType listRecords = oaiFactory.createListRecordsType();
		RecordType record = oaiFactory.createRecordType();
		listRecords.getRecord().add(record);
		HeaderType header = oaiFactory.createHeaderType();
		record.setHeader(header);
		header.setIdentifier("154383046");
		header.setDatestamp(dateTimeFormatter.format(new Date()));
		MetadataType metadata = oaiFactory.createMetadataType();
		record.setMetadata(metadata);
		OaiDcType oaiDc = oaiDcFactory.createOaiDcType();
		metadata.setAny(oaiDc);
		return listRecords;

	}

}
