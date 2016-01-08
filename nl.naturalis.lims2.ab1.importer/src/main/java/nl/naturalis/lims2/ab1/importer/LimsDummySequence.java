/**
 * 
 */
package nl.naturalis.lims2.ab1.importer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jebl.util.ProgressListener;
import nl.naturalis.lims2.utils.LimsAB1Fields;
import nl.naturalis.lims2.utils.LimsNotes;

import com.biomatters.geneious.publicapi.documents.AnnotatedPluginDocument;
import com.biomatters.geneious.publicapi.documents.DocumentUtilities;
import com.biomatters.geneious.publicapi.documents.URN;
import com.biomatters.geneious.publicapi.documents.sequence.NucleotideSequenceDocument;
import com.biomatters.geneious.publicapi.implementations.sequence.DefaultNucleotideSequence;
import com.biomatters.geneious.publicapi.plugin.DocumentOperation;
import com.biomatters.geneious.publicapi.plugin.DocumentSelectionSignature;
import com.biomatters.geneious.publicapi.plugin.GeneiousActionOptions;
import com.biomatters.geneious.publicapi.plugin.Options;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsDummySequence extends DocumentOperation {

	private LimsAB1Fields limsAB1Fields = new LimsAB1Fields();
	private LimsNotes limsNotes = new LimsNotes();

	@Override
	public GeneiousActionOptions getActionOptions() {
		return new GeneiousActionOptions("New Dummy Sequence")
				.setInMainToolbar(true);
	}

	@Override
	public String getHelp() {
		return "This operation creates a <b>New Dummy Sequence</b>";
	}

	@Override
	public DocumentSelectionSignature[] getSelectionSignatures() {
		return new DocumentSelectionSignature[0];
	}

	public List<AnnotatedPluginDocument> performOperation(
			AnnotatedPluginDocument[] docs, ProgressListener progress,
			Options options) {
		// lets create the list that we're going to return...
		ArrayList<AnnotatedPluginDocument> sequenceList = new ArrayList<AnnotatedPluginDocument>();

		// The options that we created in the getOptions() method above
		// has been
		// passed to us, hopefully the user has filled in their sequence.
		// We get the option we added by using its name.
		// MultiLineStringOption
		// has a String ValueType, so we can safely cast to a String
		// object.
		String residues = "NNNNNNNNNN";// (String)//
										// options.getValue("residues");

		// lets construct a new sequence document from the residues that
		// the
		// user entered
		NucleotideSequenceDocument sequence = new DefaultNucleotideSequence(
				"New Sequence", "A new dummy Sequence", residues, new Date(),
				URN.generateUniqueLocalURN("Dummy"));

		// and add it to the list
		sequenceList.add(DocumentUtilities
				.createAnnotatedPluginDocument(sequence));

		// AnnotatedPluginDocument annotatedPluginDocument =
		// (AnnotatedPluginDocument) sequence;

		try {
			limsNotes.setImportNotes(sequenceList.iterator().next(),
					"VersieCode", "Version number", "Version number", "0");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		// normally we would set the progress incrementally as we went,
		// but this
		// operation is quick so we just set it to finished when we're
		// done.
		(progress).setProgress(1.0);

		// return the list containing the sequence we just created, and
		// we're
		// done!
		return sequenceList;
	}

}
