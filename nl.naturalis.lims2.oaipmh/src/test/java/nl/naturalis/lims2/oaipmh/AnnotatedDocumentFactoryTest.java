package nl.naturalis.lims2.oaipmh;

import static org.junit.Assert.assertEquals;

import org.domainobject.util.FileUtil;
import org.domainobject.util.debug.BeanPrinter;
import org.junit.Test;

public class AnnotatedDocumentFactoryTest {

	@Test
	public void testParseDocumentXML()
	{
		String xml = FileUtil.getContents(getClass().getResourceAsStream("/document_xml_01.xml"));
		AnnotatedDocumentFactory adf = new AnnotatedDocumentFactory();
		Document doc = adf.parseDocumentXML(xml);
		assertEquals("01", DocumentClass.DefaultNucleotideSequence, doc.getDocumentClass());
		BeanPrinter.out(doc);
	}

	@Test
	public void testParsePluginDocumentXML()
	{
		// fail("Not yet implemented");
	}

}
