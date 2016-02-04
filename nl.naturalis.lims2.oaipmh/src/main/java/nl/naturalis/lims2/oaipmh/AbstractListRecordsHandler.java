package nl.naturalis.lims2.oaipmh;

import static nl.naturalis.lims2.oaipmh.Lims2OAIUtil.connect;
import static nl.naturalis.lims2.oaipmh.Lims2OAIUtil.disconnect;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nl.naturalis.oaipmh.api.OAIPMHRequest;
import nl.naturalis.oaipmh.api.RepositoryException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.domainobject.util.ConfigObject;

public abstract class AbstractListRecordsHandler {

	private static final Logger logger = LogManager.getLogger(AbstractListRecordsHandler.class);

	protected final ConfigObject config;
	protected final OAIPMHRequest request;

	public AbstractListRecordsHandler(ConfigObject config, OAIPMHRequest request)
	{
		this.request = request;
		this.config = config;
	}

	protected List<AnnotatedDocument> loadRecords() throws RepositoryException
	{
		AnnotatedDocumentFactory factory = new AnnotatedDocumentFactory();
		AnnotatedDocumentPreFilter preFilter = new AnnotatedDocumentPreFilter();
		AnnotatedDocumentPostFilter postFilter = new AnnotatedDocumentPostFilter();
		List<AnnotatedDocument> records = new ArrayList<>();
		String sql = getSQL();
		Connection conn = null;
		try {
			conn = connect(config);
			Statement stmt = conn.createStatement();
			logger.debug("Executing query:\n" + sql);
			ResultSet rs = stmt.executeQuery(sql.toString());
			while (rs.next()) {
				if (logger.isDebugEnabled())
					logger.debug("Processing annotated_document record (id={})", rs.getInt("id"));
				if (preFilter.accept(rs)) {
					AnnotatedDocument record = factory.create(rs);
					if (postFilter.accept(record)) {
						records.add(record);
					}
				}
			}
		}
		catch (SQLException e) {
			throw new RepositoryException("Error while executing query", e);
		}
		finally {
			disconnect(conn);
		}
		return records;
	}

	private String getSQL()
	{
		StringBuilder sb = new StringBuilder(1000);
		sb.append("SELECT id,folder_id,UNIX_TIMESTAMP(modified) AS modified, \n");
		sb.append("       urn,document_xml,plugin_document_xml,reference_count \n");
		sb.append("  FROM annotated_document \n");
		sb.append(" WHERE reference_count=0 \n");
		if (request.getFrom() != null) {
			/*
			 * Column "modified" contains the number of seconds since 01-01-1970
			 * while Date.getTime() returns the number of milliseconds since
			 * 01-01-1970.
			 */
			sb.append(" AND modified >= ").append(getSeconds(request.getFrom()));
		}
		if (request.getUntil() != null) {
			sb.append(" AND modified <= ").append(getSeconds(request.getUntil()));
		}
		int pageSize = config.getInt("specimens.repo.pagesize");
		int offset = request.getPage() * pageSize;
		sb.append(" LIMIT ").append(offset).append(",").append(pageSize);
		return sb.toString();
	}

	private static long getSeconds(Date date)
	{
		return (long) Math.floor(date.getTime() / 1000);
	}

}