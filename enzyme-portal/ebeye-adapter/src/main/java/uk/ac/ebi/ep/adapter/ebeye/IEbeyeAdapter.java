package uk.ac.ebi.ep.adapter.ebeye;

import java.util.*;

import uk.ac.ebi.ep.adapter.ebeye.param.ParamOfGetResults;
import uk.ac.ebi.ep.search.exception.MultiThreadingException;

/**
 *
 * @since   1.0
 * @version $LastChangedRevision$ <br/>
 *          $LastChangedDate$ <br/>
 *          $Author$
 * @author  $Author$
 */
public interface IEbeyeAdapter {

    public static final String EBEYE_SPECIES_FIELD ="organism_scientific_name";

	/**
	 * Field retrievable from EBEye web services for some domains,
	 * containing cross references to UniProt accessions.
	 */
	public static final String UNIPROT_REF_FIELD = "UNIPROT";

	// Supported fields

	/**
	 * Fields of the {@link Domains#uniprot uniprot} domain.
	 */
	public static enum FieldsOfGetResults {
		id, acc;
		public static List<String> getFields() {
			List<String> fields = new ArrayList<String>();
			fields.add(id.name());
			fields.add(acc.name());
			return fields;
		}
	};

	/**
	 * Fields of the {@link Domains#uniprot uniprot} domain.
	 */
	public static enum FieldsOfUniprotNameMap {
		id, acc, descRecName;
		public static List<String> getFields() {
			List<String> fields = new ArrayList<String>();
			fields.add(id.name());
			fields.add(acc.name());
			fields.add(descRecName.name());
			return fields;
		}
	};
	
	public static enum FieldsOfPdbe {
		id, name;
		public static List<String> asStrings(){
			List<String> strings = new ArrayList<String>();
			for (FieldsOfPdbe field : values()) {
				strings.add(field.name());
			}
			return strings;
		}
	}

	/**
	 * Fields of the {@link Domains#chebi chebi} domain.
	 */
	public static enum FieldsOfChebiNameMap {
		id, name;
		public static List<String> getFields() {
			List<String> fields = new ArrayList<String>();
			fields.add(id.name());
			fields.add(name.name());
			return fields;
		}
    };

//********************************** METHODS *********************************//

    /**
     * Gets number of results for more than 1 query. The number of results per
     * query is set in the ParamOfGetResults object.
     * @param paramOfGetResults the object containing domain and query info.
     * @return the same object with the number of results updated.
     * @throws MultiThreadingException
     */
    public List<ParamOfGetResults> getNumberOfResults(
            List<ParamOfGetResults> paramOfGetResults) throws MultiThreadingException;
    
    /**
     * Gets the number of results for a given query in a given domain.
     * @param paramOfGetResults the object containing domain and query info.
     * @return the same object with the number of results updated.
     */
    public ParamOfGetResults getNumberOfResults(ParamOfGetResults paramOfGetResults);

    /**
     * Gets all of the UniProt accessions related to the queries.
     * @param paramOfGetResults the objects containing domain and query info.
     * @return a List of UniProt accessions.
     * @throws MultiThreadingException
     */
    public List<String> getRelatedUniprotAccessionSet(
    		List<ParamOfGetResults> paramOfGetResults)
	throws MultiThreadingException;

    /**
     * Gets the value of fields in an index for a list of queries.
     * @param paramOfGetResults the objects wrapping queries.
     * @return a set of field values.
     * @throws MultiThreadingException
     */
    public Set<String> getValueOfFields(List<ParamOfGetResults> paramOfGetResults)
	throws MultiThreadingException;

    /**
     * Gets the value of fields in an index for a given query.
     * @param paramOfGetResults the object wrapping a query.
     * @return a set of field values.
     * @throws MultiThreadingException
     */
    public Set<String> getValueOfFields(ParamOfGetResults paramOfGetResults)
	throws MultiThreadingException;

    public Map<String, String> getMapOfFieldAndValue(List<ParamOfGetResults> params)
	throws MultiThreadingException;

    /**
     * Gets the UniProt references corresponding to the given IDs.
     * @param paramOfGetResults the object wrapping a query (with IDs).
     * @return a map of entry IDs to UniProt accessions.
     * @throws MultiThreadingException
     */
    public Map<String,List<String>> getUniprotRefAccesionsMap(
    		ParamOfGetResults paramOfGetResults)
	throws MultiThreadingException;

    /**
     * Retrieves IDs and names for the given accessions.
     * @param domain the domain where the accessions belong.
     * @param accessions the queried accessions.
     * @return a map of ID to name.
     */
    public Map<String, String> getNameMapByAccessions(String domain,
    		Collection<String> accessions);

    /**
     * Sets the configuration for EB-Eye searches.
     * @param config
     */
	public abstract void setConfig(EbeyeConfig config);

	/**
	 * Gets the current configuration, which can then be modified.
	 * @return the current configuration for EB-Eye searches.
	 */
	public abstract EbeyeConfig getConfig();

	/**
	 * Queries EB-Eye for some fields from a query.
	 * @param params parameters (domain, query, fields) to send to EB-Eye.
	 * @return a List whose items are Lists of strings with the values for the
	 * 		requested fields, or <code>null</code> if nothing found.
	 */
	public abstract List<List<String>> getFields(ParamOfGetResults params);
 
}
