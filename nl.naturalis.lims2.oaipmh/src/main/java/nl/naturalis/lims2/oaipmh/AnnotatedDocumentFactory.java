package nl.naturalis.lims2.oaipmh;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.domainobject.util.CollectionUtil;
import org.domainobject.util.DOMUtil;
import org.domainobject.util.convert.Stringifier;
import org.domainobject.util.debug.BeanPrinter;
import org.w3c.dom.Element;
import org.xml.sax.SAXParseException;

public class AnnotatedDocumentFactory {

	private static final Logger logger = LogManager.getLogger(AnnotatedDocumentFactory.class);

	public AnnotatedDocumentFactory()
	{
	}

	public AnnotatedDocument create(ResultSet rs) throws SQLException
	{
		AnnotatedDocument doc = new AnnotatedDocument();
		doc.setId(rs.getInt("id"));
		doc.setFolderId(rs.getInt("folder_id"));
		doc.setModified(rs.getLong("modified"));
		doc.setReferenceCount(rs.getInt("reference_count"));
		String xml = rs.getString("document_xml");
		doc.setDocument(parseDocumentXML(xml));
		xml = rs.getString("plugin_document_xml");
		doc.setPluginDocumentData(parsePluginDocumentXML(xml));
		if (logger.isDebugEnabled()) {
			StringWriter sw = new StringWriter(2048);
			BeanPrinter bp = new BeanPrinter(new PrintWriter(sw));
			bp.setShowClassNames(false);
			bp.dump(doc);
			logger.debug("\n" + sw.toString());
		}
		return doc;
	}

	Document parseDocumentXML(String xml)
	{
		logger.debug("Parsing contents of column \"document_xml\"");
		Element root;
		try {
			root = DOMUtil.getDocumentElement(xml);
		}
		catch (SAXParseException e) {
			logger.error("Error while parsing plugin_document_xml; {}\n\n{}", e.getMessage(), xml);
			return null;
		}
		String s = root.getAttribute("class");
		DocumentClass documentClass = DocumentClass.parse(s);
		Document doc = new Document();
		doc.setDocumentClass(documentClass);
		Element e = DOMUtil.getChild(root, "notes");
		if (e != null) {
			logger.debug("Found <notes> element. Searching for usable <note> elements.");
			DocumentNotes notes = getDocumentNotes(e);
			doc.setNotes(notes);
		}
		else if (logger.isDebugEnabled()) {
			logger.debug("No <notes> element in column \"document_xml\"");
		}
		return doc;
	}

	PluginDocumentData<?> parsePluginDocumentXML(String xml)
	{
		logger.debug("Parsing contents of column \"plugin_document_xml\"");
		Element root;
		try {
			root = DOMUtil.getDocumentElement(xml);
		}
		catch (SAXParseException e) {
			logger.error("Error while parsing plugin_document_xml; {}\n\n{}", e.getMessage(), xml);
			return null;
		}
		if (root.getTagName().equals("XMLSerialisableRootElement"))
			return handleXMLSerialisableRootElement(root);
		if (root.getTagName().equals("DefaultAlignmentDocument"))
			return handleDefaultAlignmentDocument(root);
		if (root.getTagName().equals("ABIDocument"))
			return handleABIDocument(root);
		return null;
	}

	private PluginDocumentData<XMLSerialisableRootElement.Field> handleXMLSerialisableRootElement(
			Element root)
	{
		XMLSerialisableRootElement result = new XMLSerialisableRootElement();
		for (XMLSerialisableRootElement.Field field : XMLSerialisableRootElement.Field.values()) {
			if (field == XMLSerialisableRootElement.Field.inputDocument) {
				getInputDocuments(result, root);
			}
			else if (field == XMLSerialisableRootElement.Field.finishedAddingOutputDocuments) {
				getFinishedAddingOutputDocuments(result, root);
			}
			else {
				Element e = DOMUtil.getChild(root, field.name());
				if (e != null) {
					result.set(field, e.getTextContent());
				}
			}
		}
		return result;
	}

	private PluginDocumentData<DefaultAlignmentDocument.Field> handleDefaultAlignmentDocument(
			Element root)
	{
		DefaultAlignmentDocument result = new DefaultAlignmentDocument();
		for (DefaultAlignmentDocument.Field field : DefaultAlignmentDocument.Field.values()) {
			if (field == DefaultAlignmentDocument.Field.is_contig) {
				getIsContig(result, root);
			}
			else {
				Element e = DOMUtil.getChild(root, field.name());
				if (e != null) {
					result.set(field, e.getTextContent());
				}
			}
		}
		return result;
	}

	@SuppressWarnings("static-method")
	private PluginDocumentData<ABIDocument.Field> handleABIDocument(Element root)
	{
		return new ABIDocument();
	}

	@SuppressWarnings("static-method")
	private DocumentNotes getDocumentNotes(Element notesElement)
	{
		DocumentNotes dn = new DocumentNotes();
		int i = 0;
		for (DocumentNotes.Field field : DocumentNotes.Field.values()) {
			Element e = DOMUtil.getDescendant(notesElement, field.name());
			if (e != null) {
				i++;
				dn.set(field, e.getTextContent());
				logger.debug("Found document note for {}", field.name());
			}
		}
		logger.debug("Number of usable <note> elements: {}", i);
		return dn;
	}

	@SuppressWarnings("static-method")
	private void getInputDocuments(XMLSerialisableRootElement result, Element root)
	{
		List<Element> elems = DOMUtil.getChildren(root, "inputDocument");
		if (elems != null) {
			List<String> strings = CollectionUtil.stringify(elems, new Stringifier<Element>() {
				@Override
				public String execute(Element obj, Object... conversionArguments)
				{
					return obj.getAttribute("weakReference");
				}
			});
			result.set(XMLSerialisableRootElement.Field.inputDocument, strings);
		}
	}

	@SuppressWarnings("static-method")
	private void getFinishedAddingOutputDocuments(XMLSerialisableRootElement result, Element root)
	{
		XMLSerialisableRootElement.Field f = XMLSerialisableRootElement.Field.finishedAddingOutputDocuments;
		if (root.hasAttribute(f.toString())) {
			String s = root.getAttribute(f.toString());
			Boolean value = Boolean.valueOf(s);
			result.set(f, value);
		}
	}

	@SuppressWarnings("static-method")
	private void getIsContig(DefaultAlignmentDocument result, Element root)
	{
		DefaultAlignmentDocument.Field f = DefaultAlignmentDocument.Field.is_contig;
		if (root.hasAttribute(f.toString())) {
			String s = root.getAttribute(f.toString());
			Boolean value = Boolean.valueOf(s);
			result.set(f, value);
		}
	}

}
