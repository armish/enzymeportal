package uk.ac.ebi.ep.uniprot.adapter;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import uk.ac.ebi.ep.ebeye.result.jaxb.Result;
import uk.ac.ebi.ep.search.result.jaxb.EnzymeSummary;

/**
 *
 * @since   1.0
 * @version $LastChangedRevision$ <br/>
 *          $LastChangedDate$ <br/>
 *          $Author$
 * @author  $Author$
 */
public interface IUniprotAdapter {
    //second
    public static final int ENTRY_TIMEOUT = 30;

//********************************* VARIABLES ********************************//


//******************************** CONSTRUCTORS ******************************//


//****************************** GETTER & SETTER *****************************//


//********************************** METHODS *********************************//
    public EnzymeSummary getEnzymeEntry(String accession);

    public List<EnzymeSummary> getEnzymeEntries(Set<String> accessionList)
            throws InterruptedException, ExecutionException, TimeoutException ;

    public List<EnzymeSummary> getEnzymeEntries(List<Result> briefResultList)
            throws InterruptedException, ExecutionException, TimeoutException ;

}


