package nl.naturalis.lims2.oaipmh;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.domainobject.util.CollectionUtil;
import org.domainobject.util.DOMUtil;
import org.domainobject.util.convert.Stringifier;
import org.w3c.dom.Element;

public class AnnotatedDocumentFactory {

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
		return doc;
	}

	private Document parseDocumentXML(String xml)
	{
		Element root = DOMUtil.getDocumentElement(xml);
		assert (root.hasAttribute("class"));
		String s = root.getAttribute("class");
		DocumentClass documentClass = DocumentClass.parse(s);
		assert (documentClass != null);
		Document doc = new Document();
		doc.setDocumentClass(documentClass);
		Element e = DOMUtil.getChild(root, "notes");
		if (e != null) {
			DocumentNotes notes = getDocumentNotes(e);
			doc.setNotes(notes);
		}
		return doc;
	}

	private PluginDocumentData parsePluginDocumentXML(String xml)
	{
		Element root = DOMUtil.getDocumentElement(xml);
		if (root.getTagName().equals("XMLSerialisableRootElement"))
			return handleXMLSerialisableRootElement(root);
		if (root.getTagName().equals("DefaultAlignmentDocument"))
			return handleDefaultAlignmentDocument(root);
		return null;
	}

	private PluginDocumentData handleXMLSerialisableRootElement(Element root)
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

	private PluginDocumentData handleDefaultAlignmentDocument(Element root)
	{
		DefaultAlignmentDocument result = new DefaultAlignmentDocument();
		for (DefaultAlignmentDocument.Field field : DefaultAlignmentDocument.Field.values()) {
			if (field == DefaultAlignmentDocument.Field.is_contig) {
				getIsContig(result, root);
			}
		}
		return result;
	}

	@SuppressWarnings("static-method")
	private DocumentNotes getDocumentNotes(Element notesElement)
	{
		DocumentNotes dn = new DocumentNotes();
		for (DocumentNotes.Field field : DocumentNotes.Field.values()) {
			Element e = DOMUtil.getDescendant(notesElement, field.name());
			if (e != null)
				dn.set(field, e.getTextContent());
		}
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
