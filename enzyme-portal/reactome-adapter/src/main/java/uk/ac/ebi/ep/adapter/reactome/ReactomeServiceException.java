package uk.ac.ebi.ep.adapter.reactome;

/**
 *
 * @since   1.0
 * @version $LastChangedRevision$ <br/>
 *          $LastChangedDate$ <br/>
 *          $Author$
 * @author  $Author$
 */
public class ReactomeServiceException extends Exception{

    public ReactomeServiceException(Throwable cause) {
        super(cause);
    }

    public ReactomeServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReactomeServiceException(String message) {
        super(message);
    }

    public ReactomeServiceException() {
    }

}
