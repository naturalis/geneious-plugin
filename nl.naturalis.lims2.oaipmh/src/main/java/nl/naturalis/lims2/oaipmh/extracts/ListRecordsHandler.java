package nl.naturalis.lims2.oaipmh.extracts;

import static nl.naturalis.oaipmh.api.util.OAIPMHUtil.dateTimeFormatter;
import static nl.naturalis.oaipmh.api.util.ObjectFactories.oaiFactory;

import java.util.Date;

import javax.xml.bind.JAXBException;

import nl.naturalis.lims2.oaipmh.jaxb.Amplification;
import nl.naturalis.lims2.oaipmh.jaxb.DNAExtract;
import nl.naturalis.lims2.oaipmh.jaxb.DNALabProject;
import nl.naturalis.lims2.oaipmh.jaxb.ExtractUnit;
import nl.naturalis.lims2.oaipmh.jaxb.Geneious;
import nl.naturalis.lims2.oaipmh.jaxb.GeneticAccession;
import nl.naturalis.lims2.oaipmh.jaxb.Sequencing;
import nl.naturalis.oaipmh.api.OAIPMHRequest;
import nl.naturalis.oaipmh.api.RepositoryException;
import nl.naturalis.oaipmh.api.util.OAIPMHMarshaller;
import nl.naturalis.oaipmh.api.util.OAIPMHUtil;

import org.openarchives.oai._2.HeaderType;
import org.openarchives.oai._2.ListRecordsType;
import org.openarchives.oai._2.MetadataType;
import org.openarchives.oai._2.OAIPMHtype;
import org.openarchives.oai._2.RecordType;

public class ListRecordsHandler {

	private final OAIPMHRequest request;

	public ListRecordsHandler(OAIPMHRequest request)
	{
		this.request = request;
	}

	public String handleRequest() throws RepositoryException
	{
		OAIPMHtype root = OAIPMHUtil.createResponseSkeleton(request);
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

		OAIPMHMarshaller marshaller = new OAIPMHMarshaller();
		marshaller.setRootElement(root);
		marshaller.addJaxbPackage("nl.naturalis.lims2.oaipmh.jaxb");
		marshaller.addSchemaLocation("http://data.naturalis.nl/lims2",
				"http://data.naturalis.nl/lims2/LIMS2.xsd");
		try {
			return marshaller.marshal();
		}
		catch (JAXBException e) {
			throw new RepositoryException(e);
		}

	}

}
