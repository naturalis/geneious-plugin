package nl.naturalis.lims2.oaipmh.specimens;

import static nl.naturalis.lims2.oaipmh.Lims2OAIUtil.checkMetadataPrefix;
import static nl.naturalis.lims2.oaipmh.Lims2OAIUtil.connect;
import static nl.naturalis.lims2.oaipmh.Lims2OAIUtil.disconnect;
import static nl.naturalis.oaipmh.api.util.OAIPMHUtil.createResponseSkeleton;
import static nl.naturalis.oaipmh.api.util.OAIPMHUtil.dateTimeFormatter;
import static nl.naturalis.oaipmh.api.util.ObjectFactories.oaiFactory;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import nl.naturalis.lims2.oaipmh.Lims2OAIUtil;
import nl.naturalis.lims2.oaipmh.jaxb.Geneious;
import nl.naturalis.lims2.oaipmh.jaxb.Specimen;
import nl.naturalis.lims2.oaipmh.jaxb.SpecimenUnit;
import nl.naturalis.oaipmh.api.NoRecordsMatchError;
import nl.naturalis.oaipmh.api.OAIPMHException;
import nl.naturalis.oaipmh.api.OAIPMHRequest;
import nl.naturalis.oaipmh.api.RepositoryException;

import org.domainobject.util.ConfigObject;
import org.domainobject.util.FileUtil;
import org.openarchives.oai._2.HeaderType;
import org.openarchives.oai._2.ListRecordsType;
import org.openarchives.oai._2.MetadataType;
import org.openarchives.oai._2.OAIPMHtype;
import org.openarchives.oai._2.RecordType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ListRecordsHandler {

	private static final Logger logger = LoggerFactory.getLogger(ListRecordsHandler.class);

	private final ConfigObject cfg;
	private final OAIPMHRequest request;

	public ListRecordsHandler(OAIPMHRequest request)
	{
		this.request = request;
		this.cfg = Lims2OAIUtil.getConfig();
	}

	OAIPMHtype handleRequest_old() throws OAIPMHException
	{
		Lims2OAIUtil.checkMetadataPrefix(request);
		OAIPMHtype root = createResponseSkeleton(request);
		ListRecordsType listRecords = oaiFactory.createListRecordsType();
		root.setListRecords(listRecords);
		RecordType record = oaiFactory.createRecordType();
		listRecords.getRecord().add(record);

		HeaderType header = oaiFactory.createHeaderType();
		record.setHeader(header);
		header.setIdentifier("154383046");
		header.setDatestamp(dateTimeFormatter.format(new Date()));

		MetadataType metadata = oaiFactory.createMetadataType();
		record.setMetadata(metadata);
		Geneious geneious = new Geneious();
		metadata.setAny(geneious);
		Specimen specimen = new Specimen();
		geneious.setSpecimen(specimen);
		SpecimenUnit unit = new SpecimenUnit();
		specimen.setUnit(unit);
		unit.setUnitID("RMNH.INS.23917");
		unit.setAssociatedUnitID("ANTVI001-11");
		unit.setUri("http://www.boldsystems.org/index.php/Public_RecordView?processid=ANTVI001-11");
		unit.setMultiMediaObjectComment(1);

		return root;
	}

	OAIPMHtype handleRequest() throws RepositoryException, OAIPMHException
	{
		checkMetadataPrefix(request);
		Connection conn = null;
		try {
			conn = connect(cfg);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(getSQL());
			if (!rs.next()) {
				throw new OAIPMHException(new NoRecordsMatchError());
			}
			OAIPMHtype root = createResponseSkeleton(request);
			ListRecordsType listRecords = oaiFactory.createListRecordsType();
			root.setListRecords(listRecords);
			do {
				RecordType record = oaiFactory.createRecordType();
				listRecords.getRecord().add(record);
				record.setHeader(createHeader(rs));
				record.setMetadata(createMetadata(rs));
			}
			while (rs.next());
			return root;
		}
		catch (RepositoryException | OAIPMHException e) {
			throw e;
		}
		catch (Throwable t) {
			throw new RepositoryException("Unexpected error while processing request", t);
		}
		finally {
			disconnect(conn);
		}
	}

	private static HeaderType createHeader(ResultSet rs) throws SQLException
	{
		HeaderType header = oaiFactory.createHeaderType();
		header.setIdentifier(rs.getString("id"));
		long modified = 1000L * rs.getLong("modified");
		header.setDatestamp(dateTimeFormatter.format(new Date(modified)));
		return header;
	}

	private static MetadataType createMetadata(ResultSet rs) throws SQLException
	{
		MetadataType metadata = oaiFactory.createMetadataType();
		Geneious geneious = new Geneious();
		metadata.setAny(geneious);
		Specimen specimen = new Specimen();
		geneious.setSpecimen(specimen);
		specimen.setUnit(createSpecimenUnit(rs));
		return metadata;
	}

	private static SpecimenUnit createSpecimenUnit(ResultSet rs) throws SQLException
	{
		SpecimenUnit unit = new SpecimenUnit();
		unit.setUnitID(rs.getString("unit_id"));
		unit.setAssociatedUnitID(rs.getString("assoc_unit_id"));
		unit.setUri(rs.getString("uri"));
		unit.setMultiMediaObjectComment(1);
		return unit;
	}

	private String getSQL()
	{
		InputStream is = getClass().getResourceAsStream("specimens.sql");
		String basic = FileUtil.getContents(is);
		if (request.getFrom() == null && request.getUntil() == null)
			return basic;
		StringBuilder sb = new StringBuilder(basic.length() + 100);
		sb.append(basic);
		if (request.getFrom() != null) {
			/*
			 * The modified column contains the number of seconds since
			 * 01-01-1970 while Date.getTime() returns the number of
			 * milliseconds since 01-01-1970.
			 */
			sb.append(" AND (1000 * modified) >= ").append(request.getFrom().getTime());
		}
		if (request.getUntil() != null) {
			sb.append(" AND (1000 * modified) <= ").append(request.getUntil().getTime());
		}
		return sb.toString();
	}
}
