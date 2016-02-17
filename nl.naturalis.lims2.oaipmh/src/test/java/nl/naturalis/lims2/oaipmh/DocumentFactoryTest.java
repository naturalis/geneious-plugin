package nl.naturalis.lims2.oaipmh;

import static org.junit.Assert.assertEquals;

import org.domainobject.util.FileUtil;
import org.domainobject.util.debug.BeanPrinter;
import org.junit.Test;

public class DocumentFactoryTest {

	@Test
	public void testCreateDocument()
	{
		String xml = FileUtil.getContents(getClass().getResourceAsStream("/document_xml_01.xml"));
		Document doc = DocumentFactory.createDocument(xml);
		assertEquals("01", DocumentClass.DefaultNucleotideSequence, doc.getDocumentClass());
		BeanPrinter.out(doc);
	}

}
