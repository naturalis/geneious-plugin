/**
 * 
 */
package nl.naturalis.lims2.downloader;

import java.util.ArrayList;
import java.util.List;

import com.biomatters.geneious.publicapi.databaseservice.QueryField;
import com.biomatters.geneious.publicapi.documents.Condition;
import com.biomatters.geneious.publicapi.documents.DocumentField;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class LimsAB1Fields {

	public List<DocumentField> displayFields;
	public QueryField[] searchFields;
	private List<Condition> conditionsList;

	private static final String KEY_EXTRACT_ID = "ExtractCode";
	private static final String KEY_PCR_PLAAT_ID = "PcrPlaatCode";
	private static final String KEY_MARKER = "MarkerCode";

	public void init() {
		displayFields = new ArrayList<DocumentField>();

		DocumentField extractID = DocumentField.createStringField("ExtractID",
				"Extract-id in the document", KEY_EXTRACT_ID);
		displayFields.add(extractID);

		DocumentField pcrPlaatField = DocumentField.createStringField(
				"PCRPlaatID", "PCR Plaat-id in the document", KEY_PCR_PLAAT_ID);
		displayFields.add(pcrPlaatField);

		DocumentField markerField = DocumentField.createStringField("Marker",
				"Marker in the document", KEY_MARKER);
		displayFields.add(markerField);

		/* Create the query fields */
		searchFields = new QueryField[3];

		conditionsList = new ArrayList<Condition>();
		conditionsList.add(Condition.APPROXIMATELY_EQUAL);
		conditionsList.add(Condition.BEGINS_WITH);
		conditionsList.add(Condition.CONTAINS);
		conditionsList.add(Condition.EQUAL);
		conditionsList.add(Condition.ENDS_WITH);
		conditionsList.add(Condition.GREATER_THAN);
		conditionsList.add(Condition.GREATER_THAN_OR_EQUAL_TO);
		conditionsList.add(Condition.IN_RANGE);
		conditionsList.add(Condition.LESS_THAN);
		conditionsList.add(Condition.LESS_THAN_OR_EQUAL_TO);
		conditionsList.add(Condition.NOT_CONTAINS);
		conditionsList.add(Condition.NOT_EQUAL);

		for (int index = 0; index < conditionsList.size(); index++) {
			Condition[] conditions = { conditionsList.get(index) };

			/* Extract-ID */
			QueryField qryExtractID = new QueryField(extractID, conditions);

			/* PCR Plaat-ID */
			QueryField qryPcrPlaatID = new QueryField(pcrPlaatField, conditions);

			/* Marker */
			QueryField qryMarker = new QueryField(markerField, conditions);

			/* Add all fields to the "search fields" */
			searchFields[0] = qryExtractID;
			searchFields[1] = qryPcrPlaatID;
			searchFields[2] = qryMarker;
		}
	}
}
