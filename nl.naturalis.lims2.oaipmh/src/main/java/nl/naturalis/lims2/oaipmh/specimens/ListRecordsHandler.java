package nl.naturalis.lims2.oaipmh.specimens;

import static nl.naturalis.lims2.oaipmh.DocumentNotes.Field.BOLDIDCode_BOLD;
import static nl.naturalis.lims2.oaipmh.DocumentNotes.Field.NumberOfImagesCode_BOLD;
import static nl.naturalis.lims2.oaipmh.DocumentNotes.Field.RegistrationNumberCode_Samples;
import static nl.naturalis.lims2.oaipmh.Lims2OAIUtil.checkMetadataPrefix;
import static nl.naturalis.lims2.oaipmh.Lims2OAIUtil.connect;
import static nl.naturalis.lims2.oaipmh.Lims2OAIUtil.disconnect;
import static nl.naturalis.oaipmh.api.util.OAIPMHUtil.createResponseSkeleton;
import static nl.naturalis.oaipmh.api.util.OAIPMHUtil.dateTimeFormatter;
import static nl.naturalis.oaipmh.api.util.ObjectFactories.oaiFactory;

import java.io.InputStream;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

import nl.naturalis.lims2.oaipmh.AbstractListRecordsHandler;
import nl.naturalis.lims2.oaipmh.AnnotatedDocument;
import nl.naturalis.lims2.oaipmh.DocumentNotes;
import nl.naturalis.lims2.oaipmh.Lims2OAIUtil;
import nl.naturalis.lims2.oaipmh.jaxb.Geneious;
import nl.naturalis.lims2.oaipmh.jaxb.Specimen;
import nl.naturalis.lims2.oaipmh.jaxb.SpecimenUnit;
import nl.naturalis.oaipmh.api.NoRecordsMatchError;
import nl.naturalis.oaipmh.api.OAIPMHException;
import nl.naturalis.oaipmh.api.OAIPMHRequest;
import nl.naturalis.oaipmh.api.RepositoryException;
import nl.naturalis.oaipmh.api.util.ResumptionToken;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.domainobject.util.ConfigObject;
import org.domainobject.util.FileUtil;
import org.openarchives.oai._2.HeaderType;
import org.openarchives.oai._2.ListRecordsType;
import org.openarchives.oai._2.MetadataType;
import org.openarchives.oai._2.OAIPMHtype;
import org.openarchives.oai._2.RecordType;
import org.openarchives.oai._2.ResumptionTokenType;

class ListRecordsHandler extends AbstractListRecordsHandler {

	private static final Logger logger = LogManager.getLogger(ListRecordsHandler.class);

	public ListRecordsHandler(ConfigObject config, OAIPMHRequest request)
	{
		super(config, request);
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
		List<AnnotatedDocument> records = loadRecords();
		if (records.size() == 0) {
			throw new OAIPMHException(new NoRecordsMatchError());
		}
		OAIPMHtype root = createResponseSkeleton(request);
		ListRecordsType listRecords = oaiFactory.createListRecordsType();
		root.setListRecords(listRecords);
		int pageSize = config.getInt("specimens.repo.pagesize");
		int offset = request.getPage() * pageSize;
		for (int i = offset; i < records.size() && i < pageSize; ++i) {
			addRecord(records.get(i), listRecords);
		}
		if (offset + pageSize < records.size()) {
			addResumptionToken(listRecords, records.size(), offset);
		}
		return root;
	}

	OAIPMHtype handleRequest2() throws RepositoryException, OAIPMHException
	{
		checkMetadataPrefix(request);
		Connection conn = null;
		try {
			conn = connect(config);
			Statement stmt = conn.createStatement();
			String sql = getSQL();
			if (logger.isDebugEnabled())
				logger.debug("Executing query:\n" + sql);
			ResultSet rs = stmt.executeQuery(sql);

			Statement stmt2 = conn.createStatement();
			ResultSet rs2 = stmt2.executeQuery("SELECT FOUND_ROWS()");
			rs2.next();
			int resultSetSize = rs2.getInt(1);
			logResultSetInfo(resultSetSize);

			OAIPMHtype root = createResponseSkeleton(request);
			ListRecordsType listRecords = oaiFactory.createListRecordsType();
			root.setListRecords(listRecords);

			int pageSize = config.getInt("specimens.repo.pagesize");
			int i = 0;
			while (rs.next()) {
				if (i++ == pageSize)
					break;
				// addRecord(rs, listRecords);
			}

			if (i == 0) {
				throw new OAIPMHException(new NoRecordsMatchError());
			}

			int offset = request.getPage() * pageSize;
			if (offset + i < resultSetSize) {
				addResumptionToken(listRecords, resultSetSize, offset);
			}
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

	private void logResultSetInfo(int resultSetSize)
	{
		int pageSize = config.getInt("specimens.repo.pagesize");
		int offset = request.getPage() * pageSize;
		int recordsToGo = resultSetSize - offset - pageSize;
		int requestsToGo = (int) Math.ceil(recordsToGo / pageSize);
		logger.info("Records satisfying request: " + resultSetSize);
		logger.info("Records served per request: " + pageSize);
		logger.info("Remaining records: " + recordsToGo);
		String fmt = "%s more request%s needed to complete harvest";
		String plural = requestsToGo == 1 ? "" : "s";
		logger.info(String.format(fmt, requestsToGo, plural));
	}

	private void addResumptionToken(ListRecordsType listRecords, int numRecords, int offset)
	{
		ResumptionTokenType resumptionToken = oaiFactory.createResumptionTokenType();
		listRecords.setResumptionToken(resumptionToken);
		resumptionToken.setCompleteListSize(BigInteger.valueOf(numRecords));
		resumptionToken.setCursor(BigInteger.valueOf(offset));
		ResumptionToken tokenGenerator = new ResumptionToken();
		String token = tokenGenerator.compose(request);
		resumptionToken.setValue(token);
	}

	private static void addRecord(AnnotatedDocument ad, ListRecordsType listRecords)
	{
		RecordType record = oaiFactory.createRecordType();
		listRecords.getRecord().add(record);
		record.setHeader(createHeader(ad));
		record.setMetadata(createMetadata(ad));
	}

	private static HeaderType createHeader(AnnotatedDocument ad)
	{
		HeaderType header = oaiFactory.createHeaderType();
		header.setIdentifier(String.valueOf(ad.getId()));
		long modified = 1000L * ad.getModified();
		header.setDatestamp(dateTimeFormatter.format(new Date(modified)));
		return header;
	}

	private static MetadataType createMetadata(AnnotatedDocument ad)
	{
		MetadataType metadata = oaiFactory.createMetadataType();
		Geneious geneious = new Geneious();
		metadata.setAny(geneious);
		Specimen specimen = new Specimen();
		geneious.setSpecimen(specimen);
		specimen.setUnit(createSpecimenUnit(ad));
		return metadata;
	}

	private static SpecimenUnit createSpecimenUnit(AnnotatedDocument ad)
	{
		SpecimenUnit unit = new SpecimenUnit();
		DocumentNotes notes = ad.getDocument().getNotes();
		unit.setUnitID(notes.get(RegistrationNumberCode_Samples));
		unit.setAssociatedUnitID(notes.get(RegistrationNumberCode_Samples));
		unit.setUri(notes.get(BOLDIDCode_BOLD));
		String s = notes.get(NumberOfImagesCode_BOLD);
		if (s != null) {
			Integer i = Integer.valueOf(s);
			unit.setMultiMediaObjectComment(i);
		}
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
		int pageSize = config.getInt("specimens.repo.pagesize");
		int offset = request.getPage() * pageSize;
		sb.append(" LIMIT ").append(offset).append(",").append(pageSize);
		String sql = sb.toString();
		return sql;
	}
}