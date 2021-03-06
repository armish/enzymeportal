package uk.ac.ebi.ep.adapter.ebeye;

import java.util.List;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import uk.ac.ebi.ebinocle.webservice.ArrayOfEntryReferences;
import uk.ac.ebi.ebinocle.webservice.EntryReferences;
import uk.ac.ebi.ebisearchservice.ArrayOfArrayOfString;
import uk.ac.ebi.ebisearchservice.ArrayOfString;
import uk.ac.ebi.ebisearchservice.EBISearchService;
import uk.ac.ebi.ebisearchservice.EBISearchService_Service;
import uk.ac.ebi.ep.adapter.ebeye.param.ParamGetNumberOfResults;
import uk.ac.ebi.ep.adapter.ebeye.param.ParamOfGetResults;
import uk.ac.ebi.ep.adapter.ebeye.util.Transformer;

/**
 *
 * @since   1.0
 * @version $LastChangedRevision$ <br/>
 *          $LastChangedDate$ <br/>
 *          $Author$
 * @author  $Author$
 */
public class EbeyeCallable {

    private static final ArrayOfString ACC_FIELD =
            Transformer.transformToArrayOfString(
                IEbeyeAdapter.FieldsOfGetResults.acc.name());

//********************************* VARIABLES ********************************//
    
	/** The EBEye search service doing all the work. */
    private static EBISearchService eBISearchService =
    		new EBISearchService_Service().getEBISearchServiceHttpPort();


//******************************** CONSTRUCTORS ******************************//


//****************************** GETTER & SETTER *****************************//


//********************************** METHODS *********************************//
//******************************** INNER CLASS *******************************//

    /**
     * Callable to retrieve the number of results for a given domain and query.
     */
    public static class NumberOfResultsCaller
	implements Callable<Integer> {
    	public static Logger LOGGER = Logger.getLogger(NumberOfResultsCaller.class);
        protected ParamGetNumberOfResults paramGetNumberOfResults;

        public NumberOfResultsCaller() {}

        public NumberOfResultsCaller(ParamGetNumberOfResults paramGetNumberOfResults) {
            this.paramGetNumberOfResults = paramGetNumberOfResults;
        }

        public ParamGetNumberOfResults getParamGetNumberOfResults() {
            return paramGetNumberOfResults;
        }

        public void setParamGetNumberOfResults(ParamGetNumberOfResults
        		paramGetNumberOfResults) {
            this.paramGetNumberOfResults = paramGetNumberOfResults;
        }

        public Integer getNumberOfResults() {
            int totalFound = getNumberOfResults(
                    paramGetNumberOfResults.getDomain(),
                    paramGetNumberOfResults.getQuery());
            return totalFound;
        }

        public Integer getNumberOfResults(String domain, String query) {
            return eBISearchService.getNumberOfResults(domain, query);
        }

        public Integer call() throws Exception {
           return getNumberOfResults();
        }

    }

//******************************** INNER CLASS *******************************//
    
    /**
     * Callable to get lists of results given a domain, a query and fields to
     * retrieve.
     */
    public static class GetResultsCallable
    implements Callable<ArrayOfArrayOfString> {
    	
        protected ParamOfGetResults param;
        protected int start;
        protected int size;

        public GetResultsCallable(){}
        
        public GetResultsCallable(ParamOfGetResults param, int start, int size) {
            this.param = param;
            this.start = start;
            this.size = size;
        }

        public ArrayOfArrayOfString call() throws Exception {
            return callGetResults();
        }

        public ArrayOfArrayOfString callGetResults() {
            ArrayOfArrayOfString EbeyeResult = callGetResults(
                    param.getDomain(),
                    param.getQuery(),
                    param.getFields(),
                    start,
                    size);
            return EbeyeResult;
        }

        public ArrayOfArrayOfString callGetResults(String domain, String query,
        		List<String> fields, int start, int size) {
            ArrayOfString ebeyeFields = Transformer
                    .transformToArrayOfString(fields);
            ArrayOfArrayOfString EbeyeResult = eBISearchService.getResults(
                    domain, query, ebeyeFields, start, size);
            return EbeyeResult;
        }

    }

//******************************** INNER CLASS *******************************//
    
    /**
     * Callable to retrieve entries (with field values) given their domain and IDs.
     */
    public static class GetEntriesCallable
    implements Callable<ArrayOfArrayOfString> {
    	
        protected String domain;
        protected ArrayOfString id;
        protected ArrayOfString fields;

        public GetEntriesCallable(String domain, ArrayOfString id, ArrayOfString fields) {
            this.domain = domain;
            this.id = id;
            this.fields = fields;
        }

        public ArrayOfArrayOfString call() throws Exception {
            return callGetEntries();
        }

        public ArrayOfArrayOfString callGetEntries() {
            ArrayOfArrayOfString EbeyeResult = eBISearchService.getEntries(
            		domain, id, fields);
            return EbeyeResult;
        }

    }

//******************************** INNER CLASS *******************************//
    
    /**
     * Callable to get entries referenced from one domain to another.
     */
    public static class GetReferencedEntriesSet
    implements Callable<ArrayOfEntryReferences> {
    	
        protected String domain;
        protected ArrayOfString entries;
        protected String referencedDomain;
        protected ArrayOfString fields;

        public GetReferencedEntriesSet(String domain, ArrayOfString entries,
                String referencedDomain, ArrayOfString fields) {
            this.domain = domain;
            this.entries = entries;
            this.referencedDomain = referencedDomain;
            this.fields = fields;
        }

        public ArrayOfEntryReferences call() throws Exception {
            return callGetReferencedEntriesSet();
        }

        public ArrayOfEntryReferences callGetReferencedEntriesSet() {
            ArrayOfEntryReferences EbeyeResult =
            		eBISearchService.getReferencedEntriesSet(
            				domain, entries, referencedDomain, fields);
            return EbeyeResult;
        }

        public ArrayOfArrayOfString callGetReferencedEntriesFlatSet() {
            ArrayOfArrayOfString EbeyeResult =
            		eBISearchService.getReferencedEntriesFlatSet(
            				domain, entries, referencedDomain, fields);
            return EbeyeResult;
        }

    }

    /**
     * Callable to retrieve UniProt accessions referenced by the results of a
     * search.
     */
    public static class GetUniprotReferencesCallable
    implements Callable<ArrayOfArrayOfString>{

        protected GetResultsCallable getResultsCallable;

        /**
         * Constructor.
         * @param param parameters for the search, including the search terms
         *      and the searched domain.
         * @param start first record to be fetched from the results list.
         * @param size how many records to fetch.
         */
        public GetUniprotReferencesCallable(ParamOfGetResults param, int start,
                                            int size) {
            getResultsCallable = new GetResultsCallable(param, start, size);
        }

        public ArrayOfArrayOfString call() throws Exception {
            return getReferences();
        }

        /**
         * Method to get UniProt references from EB-Eye. It first calls
         * EB-Eyes's <code>getResults</code> to get the ids of any entries
         * matching the search terms in the searched domain, then uses those ids
         * to call <code>getReferencedEntriesSet</code> to retrieve related
         * UniProt accessions.
         * @return a String[][] in which each String[] is a primary UniProt
         *      accession followed by its secondary ones.
         */
        private ArrayOfArrayOfString getReferences(){
            ArrayOfArrayOfString refs = null;
            ArrayOfArrayOfString ids = getResultsCallable.callGetResults();
            ArrayOfString arrIds = Transformer.transformToArrayOfString(ids);
            if (arrIds != null){
                ArrayOfEntryReferences upRefs =
                        eBISearchService.getReferencedEntriesSet(
                                getResultsCallable.param.getDomain(), arrIds,
                                Domains.uniprot.name(), ACC_FIELD);
                for (EntryReferences entryRefs : upRefs.getEntryReferences()){
                    ArrayOfArrayOfString values =
                            entryRefs.getReferences().getValue();
                    if (values == null || values.getArrayOfString() == null){
                        continue;
                    }
                    if (refs == null){
                        refs = values;
                    } else {
                        refs.getArrayOfString().addAll(values.getArrayOfString());
                    }
                }
            }
            return refs;
        }

    }

//******************************** INNER CLASS *******************************//

/*
    public static class GetAllResultsCallable
            implements Callable<List<ArrayOfArrayOfString>> {
        ParamOfGetAllResults paramOfGetAllResults;

        public GetAllResultsCallable(ParamOfGetAllResults paramOfGetAllResults) {
            this.paramOfGetAllResults = paramOfGetAllResults;
        }


        public List<ArrayOfArrayOfString> call() throws Exception {
            return getAllResults();
        }

    public List<ArrayOfArrayOfString> getAllResults()
                                        throws InterruptedException, ExecutionException {
        NumberOfResultsCaller caller =
                new EbeyeCallable.NumberOfResultsCaller(paramOfGetAllResults);

        int totalFound = caller.getNumberOfResults();
        List<ArrayOfArrayOfString> rawResults =
                this.getEbeyeResults(totalFound);
        return rawResults;
    }

    public List<ArrayOfArrayOfString> getEbeyeResults (int totalFound)
            throws InterruptedException, ExecutionException {
        String domain = paramOfGetAllResults.getDomain();
        String query = paramOfGetAllResults.getQuery();
        List<String> fields = paramOfGetAllResults.getFields();

        List<ArrayOfArrayOfString> resultList = new ArrayList<ArrayOfArrayOfString>();
        if (totalFound > 0) {
            ExecutorService pool = Executors.newCachedThreadPool();
            try {
                ArrayOfString ebeyeFields = Transformer.transformToArrayOfString(fields);
                int numberOfLoops = Calculator.calTotalPages(totalFound,
                        IEbeyeAdapter.EBEYE_RESULT_LIMIT);
                int lastLoopSize = Calculator.getLastPageResults(totalFound,
                        IEbeyeAdapter.EBEYE_RESULT_LIMIT);
                int currentLoop = 0;
                int start = 0;
                while (currentLoop < numberOfLoops) {
                    int size = IEbeyeAdapter.EBEYE_RESULT_LIMIT;
                    if (currentLoop == numberOfLoops-1) {
                        size = lastLoopSize;
                    }
                    Callable<ArrayOfArrayOfString> callable =
                            new GetResultsCallable(
                            domain, query, ebeyeFields, start,size);
                    Future<ArrayOfArrayOfString> future = pool.submit(callable);
                    ArrayOfArrayOfString rawResults = (ArrayOfArrayOfString)future.get();
                    resultList.add(rawResults);
                    start = start+size;
                    currentLoop++;
                }
            }
            finally {
                pool.shutdown();
            }

        }
        return resultList;
    }

    }
*/


}
