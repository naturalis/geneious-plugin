package nl.naturalis.lims2.oaipmh.extracts;

import static nl.naturalis.oaipmh.api.util.OAIPMHUtil.createResponseSkeleton;
import static nl.naturalis.oaipmh.api.util.OAIPMHUtil.dateTimeFormatter;
import static nl.naturalis.oaipmh.api.util.ObjectFactories.oaiFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nl.naturalis.lims2.oaipmh.AnnotatedDocument;
import nl.naturalis.lims2.oaipmh.DocumentNotes;
import nl.naturalis.lims2.oaipmh.DocumentNotes.Field;
import nl.naturalis.lims2.oaipmh.PluginDocumentData.RootElement;
import nl.naturalis.lims2.oaipmh.IAnnotatedDocumentPostFilter;
import nl.naturalis.lims2.oaipmh.IAnnotatedDocumentPreFilter;
import nl.naturalis.lims2.oaipmh.Lims2OAIUtil;
import nl.naturalis.lims2.oaipmh.ListRecordsHandler;
import nl.naturalis.lims2.oaipmh.PluginDocumentData;
import nl.naturalis.lims2.oaipmh.XMLSerialisableRootElement;
import nl.naturalis.lims2.oaipmh.jaxb.Amplification;
import nl.naturalis.lims2.oaipmh.jaxb.DNAExtract;
import nl.naturalis.lims2.oaipmh.jaxb.DNALabProject;
import nl.naturalis.lims2.oaipmh.jaxb.ExtractUnit;
import nl.naturalis.lims2.oaipmh.jaxb.Geneious;
import nl.naturalis.lims2.oaipmh.jaxb.GeneticAccession;
import nl.naturalis.lims2.oaipmh.jaxb.Sequencing;
import nl.naturalis.oaipmh.api.OAIPMHException;
import nl.naturalis.oaipmh.api.OAIPMHRequest;

import org.domainobject.util.ConfigObject;
import org.openarchives.oai._2.HeaderType;
import org.openarchives.oai._2.ListRecordsType;
import org.openarchives.oai._2.MetadataType;
import org.openarchives.oai._2.OAIPMHtype;
import org.openarchives.oai._2.RecordType;

import static nl.naturalis.lims2.oaipmh.PluginDocumentData.RootElement.XML_SERIALISABLE_ROOT_ELEMENT;

class DNAExtractListRecordsHandler extends ListRecordsHandler {

	@SuppressWarnings("static-method")
	OAIPMHtype handleRequest_old(OAIPMHRequest request) throws OAIPMHException
	{
		Lims2OAIUtil.checkMetadataPrefix(request);
		OAIPMHtype root = createResponseSkeleton(request);
		ListRecordsType listRecords = oaiFactory.createListRecordsType();
		root.setListRecords(listRecords);
		RecordType record = oaiFactory.createRecordType();
		listRecords.getRecord().add(record);
		HeaderType header = oaiFactory.createHeaderType();
		record.setHeader(header);
		header.setIdentifier("123423046");
		header.setDatestamp(dateTimeFormatter.format(new Date()));
		MetadataType metadata = oaiFactory.createMetadataType();
		record.setMetadata(metadata);
		Geneious geneious = new Geneious();
		metadata.setAny(geneious);

		DNAExtract extract = new DNAExtract();
		geneious.setDnaExtract(extract);
		ExtractUnit unit = new ExtractUnit();
		extract.setUnit(unit);
		unit.setUnitID("e123214324");
		unit.setAssociatedUnitID("RMNH.INS.23917");
		unit.setInstitutePlateID("NBCN123456");
		unit.setPlatePosition("A10");
		DNALabProject project = new DNALabProject();
		extract.setDnaLabProject(project);
		project.setProjectID("BCP1234");
		Sequencing seq = new Sequencing();
		project.setSequencing(seq);
		seq.setCloningStaff("");
		seq.setAmplificationStaff("");
		seq.setConsensusSequenceID("e4010125106_Rhy_ger_MJ243_COI-H08_M13R_P15_025");
		seq.setConsensusSequenceLength(650);
		seq.setConsensusSequenceQuality("fault");
		GeneticAccession ga = new GeneticAccession();
		seq.setGeneticAccession(ga);
		ga.setBOLDProcessID("ANTVI001-11");
		ga.setGeneticAccessionNumber("JQ412562");
		ga.setGeneticAccessionNumberURI("http://www.ncbi.nlm.nih.gov/nuccore/JQ412562");
		Amplification amp = new Amplification();
		project.setAmplification(amp);
		amp.setAmplificationStaff("");
		amp.setMarker("CO1");

		return root;
	}

	public DNAExtractListRecordsHandler(ConfigObject config, OAIPMHRequest request)
	{
		super(config, request);
	}

	@Override
	protected List<IAnnotatedDocumentPreFilter> getAnnotatedDocumentPreFilters()
	{
		List<IAnnotatedDocumentPreFilter> filters = new ArrayList<>(1);
		filters.add(new DNAExtractFilter());
		return filters;
	}

	@Override
	protected List<IAnnotatedDocumentPostFilter> getAnnotatedDocumentPostFilters()
	{
		return new ArrayList<>(0);
	}

	@Override
	protected void setMetadata(Geneious geneious, AnnotatedDocument ad)
	{
		geneious.setDnaExtract(createDnaExtract(ad));
	}

	@Override
	protected int getPageSize()
	{
		return config.getInt("dna-extracts.repo.pagesize");
	}

	private static DNAExtract createDnaExtract(AnnotatedDocument ad)
	{
		DNAExtract extract = new DNAExtract();
		extract.setUnit(createExtractUnit(ad));
		extract.setDnaLabProject(createDnaLabProject(ad));
		return extract;
	}

	private static DNALabProject createDnaLabProject(AnnotatedDocument ad)
	{
		DocumentNotes notes = ad.getDocument().getNotes();
		DNALabProject project = new DNALabProject();
		project.setProjectID("???? (mapping not in spec)");
		project.setVersionNumber(notes.get(Field.DocumentVersionCode));
		project.setSequencing(createSequencing(ad));
		project.setAmplification(createAmplification(ad));
		return project;
	}

	private static ExtractUnit createExtractUnit(AnnotatedDocument ad)
	{
		DocumentNotes notes = ad.getDocument().getNotes();
		ExtractUnit unit = new ExtractUnit();
		unit.setUnitID(notes.get(Field.ExtractIDCode_Samples));
		unit.setAssociatedUnitID(notes.get(Field.BOLDIDCode_BOLD));
		unit.setInstitutePlateID(notes.get(Field.ExtractPlateNumberCode_Samples));
		unit.setPlatePosition(notes.get(Field.PlatePositionCode_Samples));
		return unit;
	}

	private static Sequencing createSequencing(AnnotatedDocument ad)
	{
		DocumentNotes notes = ad.getDocument().getNotes();
		Sequencing seq = new Sequencing();
		seq.setCloningStaff(notes.get(Field.SequencingStaffCode_FixedValue));
		seq.setAmplificationStaff(notes.get(Field.AmplicificationStaffCode_FixedValue));
		if (ad.getPluginDocumentData().getRootElement() == XML_SERIALISABLE_ROOT_ELEMENT) {
			XMLSerialisableRootElement e = (XMLSerialisableRootElement) ad.getPluginDocumentData();
			String name = (String) e.get(XMLSerialisableRootElement.Field.name);
			seq.setConsensusSequenceID(name);
		}
		int i = Integer.parseInt(notes.get(Field.NucleotideLengthCode_Bold));
		seq.setConsensusSequenceLength(i);
		seq.setConsensusSequenceQuality("???? (mapping not in spec)");
		seq.setGeneticAccession(createGeneticAccession(ad));
		return seq;
	}

	private static GeneticAccession createGeneticAccession(AnnotatedDocument ad)
	{
		DocumentNotes notes = ad.getDocument().getNotes();
		GeneticAccession ga = new GeneticAccession();
		ga.setBOLDProcessID(notes.get(Field.BOLDIDCode_BOLD));
		ga.setGeneticAccessionNumber(notes.get(Field.GenBankIDCode_Bold));
		ga.setGeneticAccessionNumberURI(notes.get(Field.GenBankURICode_FixedValue));
		return ga;
	}

	private static Amplification createAmplification(AnnotatedDocument ad)
	{
		DocumentNotes notes = ad.getDocument().getNotes();
		Amplification amp = new Amplification();
		amp.setAmplificationStaff(notes.get(Field.AmplicificationStaffCode_FixedValue));
		amp.setMarker(notes.get(Field.MarkerCode_Seq));
		return amp;
	}
}
