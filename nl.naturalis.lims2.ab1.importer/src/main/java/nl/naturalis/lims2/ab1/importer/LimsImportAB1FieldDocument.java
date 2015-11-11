/**
 * 
 */
package nl.naturalis.lims2.ab1.importer;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import jebl.util.ProgressListener;
import nl.naturalis.lims2.updater.LimsAB1Fields;

import org.apache.commons.lang3.StringUtils;
import org.jdom.CDATA;
import org.jdom.Element;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentField;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.documents.PluginDocument;
import com.biomatters.geneious.publicapi.documents.URN;
import com.biomatters.geneious.publicapi.documents.XMLSerializationException;
import com.biomatters.geneious.publicapi.plugin.DocumentImportException;
import com.biomatters.geneious.publicapi.plugin.PluginUtilities;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsImportAB1FieldDocument implements PluginDocument {

	LimsAB1Fields limsAB1Flieds = new LimsAB1Fields();
	private static final String KEY_EXTRACT_ID = "ExtractCode";
	private static final String KEY_PCR_PLAAT_ID = "PcrPlaatCode";
	private static final String KEY_MARKER = "MarkerCode";

	final DocumentField extractIDField = DocumentField.createStringField(
			"ExtractID", "Extract-id in the document", KEY_EXTRACT_ID);
	final DocumentField pcrPlaatField = DocumentField.createStringField(
			"PCRPlaatID", "PCR Plaat-id in the document", KEY_PCR_PLAAT_ID);
	final DocumentField markerField = DocumentField.createStringField("Marker",
			"Marker in the document", KEY_MARKER);

	private String fileName;
	private AnnotatedPluginDocument documents;
	private String extractid;

	public LimsImportAB1FieldDocument(File file, String fileName)
			throws IOException, DocumentImportException {
		this.fileName = fileName;
		setExtractIDFromAB1FileName(fileName);

		List<AnnotatedPluginDocument> docs = PluginUtilities.importDocuments(
				new File(file.getAbsolutePath()), ProgressListener.EMPTY);

		/*
		 * * List<AnnotationGeneratorResult> resultsList = new
		 * ArrayList<SequenceAnnotationGenerator.AnnotationGeneratorResult>();
		 * 
		 * AnnotationGeneratorResult result = new AnnotationGeneratorResult();
		 * result.addDocumentFieldToSet(new
		 * DocumentFieldAndValue(extractIDField, limsAB1Flieds.getExtractID()));
		 * System.out.println(extractIDField);
		 * 
		 * resultsList.add(result); System.out.println(resultsList.toString());
		 */

		this.documents = docs.iterator().next();
		DocumentUtilities.addGeneratedDocuments(docs, true);
	}

	public LimsImportAB1FieldDocument() {
	}

	String getText() {
		System.out.println(documents);
		return documents.toString();
	}

	@Override
	public void fromXML(Element element) throws XMLSerializationException {
		fileName = element.getChildText("fileName");
		// documents = element.getChild("documents").getText();
	}

	@Override
	public Element toXML() {
		Element root = new Element("TextDocument");
		root.addContent(new Element("fileName").setText(fileName));
		root.addContent(new Element("documents").setContent(new CDATA(documents
				.toString())));
		// root.addContent(new Element("extractid").setText(extractid));
		return root;
	}

	@Override
	public Date getCreationDate() {
		return new Date();
	}

	@Override
	public String getDescription() {
		return "(AB1 file)";
	}

	@Override
	public List<DocumentField> getDisplayableFields() {
		return Arrays.asList(new DocumentField[] { extractIDField,
				pcrPlaatField, markerField });
	}

	@Override
	public Object getFieldValue(String value) {
		if (value.equals(KEY_EXTRACT_ID)) {
			System.out.println("Extract-ID: " + limsAB1Flieds.getExtractID());
			return limsAB1Flieds.getExtractID();
		}
		if (value.equals(KEY_PCR_PLAAT_ID)) {
			System.out
					.println("PCR Plaat-ID: " + limsAB1Flieds.getPcrPlaatID());
			return limsAB1Flieds.getPcrPlaatID();
		}
		if (value.equals(KEY_MARKER)) {
			System.out.println("Marker: " + limsAB1Flieds.getMarker());
			return limsAB1Flieds.getMarker();
		}

		return null;
	}

	@Override
	public String getName() {
		return fileName;
	}

	@Override
	public URN getURN() {
		return null;
	}

	@Override
	public String toHTML() {
		/*
		 * return "<pre>" + documents.replace("<", "&lt;").replace(">", "&gt;")
		 * .replace("&", "&amp;") + "</pre>";
		 */
		return null;
	}

	private void setExtractIDFromAB1FileName(String fileName) {
		/* for example: e4010125015_Sil_tri_MJ243_COI-A01_M13F_A01_008.ab1 */
		String[] underscore = StringUtils.split(fileName, "_");
		limsAB1Flieds.setExtractID(underscore[0]);
		limsAB1Flieds.setPcrPlaatID(underscore[3]);
		limsAB1Flieds.setMarker(underscore[4]);
	}

	/*
	 * @Override public Result[] getSearchContent() { return new Result[] { new
	 * Result(null, documents.toString()), new Result(extractIDField,
	 * limsAB1Flieds.getExtractID()) }; }
	 */

}
