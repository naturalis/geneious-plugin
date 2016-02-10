package nl.naturalis.lims2.oaipmh.specimens;

import java.sql.ResultSet;
import java.sql.SQLException;

import nl.naturalis.lims2.oaipmh.AnnotatedDocument;
import nl.naturalis.lims2.oaipmh.IAnnotatedDocumentPostFilter;
import nl.naturalis.lims2.oaipmh.IAnnotatedDocumentPreFilter;

public class SpecimenFilter implements IAnnotatedDocumentPostFilter, IAnnotatedDocumentPreFilter {

	public SpecimenFilter()
	{
	}

	@Override
	public boolean accept(ResultSet rs) throws SQLException
	{
		// Some bare-knuckle XML parsing here for fail-fast processing
		String xml = rs.getString("document_xml");
		if (xml.indexOf("<RegistrationNumberCode_Samples>") == -1) {
			return false;
		}
		return true;
	}

	@Override
	public boolean accept(AnnotatedDocument ad)
	{
		return true;
	}

}
