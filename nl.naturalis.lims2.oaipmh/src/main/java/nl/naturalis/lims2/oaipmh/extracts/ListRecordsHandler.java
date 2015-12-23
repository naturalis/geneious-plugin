package nl.naturalis.lims2.oaipmh.extracts;

import static nl.naturalis.oaipmh.api.util.OAIPMHUtil.createResponseSkeleton;
import static nl.naturalis.oaipmh.api.util.OAIPMHUtil.dateTimeFormatter;
import static nl.naturalis.oaipmh.api.util.ObjectFactories.oaiFactory;

import java.util.Date;

import nl.naturalis.lims2.oaipmh.Lims2OAIUtil;
import nl.naturalis.lims2.oaipmh.jaxb.Amplification;
import nl.naturalis.lims2.oaipmh.jaxb.DNAExtract;
import nl.naturalis.lims2.oaipmh.jaxb.DNALabProject;
import nl.naturalis.lims2.oaipmh.jaxb.ExtractUnit;
import nl.naturalis.lims2.oaipmh.jaxb.Geneious;
import nl.naturalis.lims2.oaipmh.jaxb.GeneticAccession;
import nl.naturalis.lims2.oaipmh.jaxb.Sequencing;
import nl.naturalis.oaipmh.api.OAIPMHException;
import nl.naturalis.oaipmh.api.OAIPMHRequest;

import org.openarchives.oai._2.HeaderType;
import org.openarchives.oai._2.ListRecordsType;
import org.openarchives.oai._2.MetadataType;
import org.openarchives.oai._2.OAIPMHtype;
import org.openarchives.oai._2.RecordType;

class ListRecordsHandler {

	@SuppressWarnings("static-method")
	OAIPMHtype handleRequest(OAIPMHRequest request) throws OAIPMHException
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

}
