package uk.ac.ebi.util.result;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uk.ac.ebi.ebisearchservice.ArrayOfArrayOfString;
import uk.ac.ebi.ebisearchservice.ArrayOfString;
import uk.ac.ebi.ep.config.Domain;
import uk.ac.ebi.ep.config.ResultField;
import uk.ac.ebi.ep.config.ResultFieldList;
import uk.ac.ebi.ep.search.model.EnzymeSummary;
import uk.ac.ebi.ep.search.model.Species;

/**
 *
 * @since   1.0
 * @version $LastChangedRevision$ <br/>
 *          $LastChangedDate$ <br/>
 *          $Author$
 * @author  $Author$
 */
public class EBeyeDataTypeConverter extends DataTypeConverter {

    public static ArrayOfString createEbeyeFieldArray(Domain domain) {
        List<ResultField> fieldList = domain.getResultFieldList().getResultField();
        Iterator<ResultField> it = fieldList.iterator();
        ArrayOfString resultRefFields = new ArrayOfString();
        while (it.hasNext()) {
            ResultField field = (ResultField) it.next();
            resultRefFields.getString().add(field.getId());
        }
        return resultRefFields;
    }
    
    public static String convertArrayOfStringToString(ArrayOfString arrayOfString) {
        if (arrayOfString == null) {
            return null;
        }
        List<String> list = arrayOfString.getString();
        Iterator<String> it = list.iterator();
        StringBuffer sb = new StringBuffer();
        int counter = 0;
        while (it.hasNext()) {
            sb.append((String) it.next());
            counter++;
            if (counter < list.size()) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    /**
     * @deprecated see transform
     * @param list
     * @return
     */
    public static ArrayOfString listToArrayOfString(List<String> list) {
        ArrayOfString arrayOfString = new ArrayOfString();
        arrayOfString.getString().addAll(list);
        return arrayOfString;
    }


    public static EnzymeSummary getEntryResultToEnzymeSummary(
                                                        List<String> resultFields) {
        EnzymeSummary enzymeSummary= new EnzymeSummary();
        int counter = 0;
        for (String field:resultFields) {
            String resultFieldValue = new String(field);
            switch (counter) {
                case 0: {
                    enzymeSummary.setUniprotid(resultFieldValue);
                    break;
                }
                case 1: {
                    /*
                    String accessionXLinks = DataTypeConverter
                                        .uniprotAccessionsToXLinks(resultFieldValue);
                     *
                     */
                    List<String> accessionList =DataTypeConverter
                                            .accessionsToList(resultFieldValue.split("\\s"));
                    enzymeSummary.getUniprotaccessions().addAll(accessionList);
                    break;
                }
                case 2: {
                    enzymeSummary.setName(resultFieldValue);
                    break;
                }
                case 3: {
                    String name = enzymeSummary.getName();
                    if (name.isEmpty() || name==null) {
                        //descSubName
                        enzymeSummary.setName(resultFieldValue);
                    }
                    break;
                }
                case 4: {
                    Species species = new Species();
                    species.setScientificname(resultFieldValue);
                    enzymeSummary.setSpecies(species);
                    break;
                }
            }
        counter++;
        }

       return enzymeSummary;

    }
/*
    public static EnzymeSummaryCollection 
            getEntriesResultsToEnzymeSummaryCollection (
                                            ArrayOfArrayOfString results) {
        List<ArrayOfString> resultList = results.getArrayOfString();
        Iterator it = resultList.iterator();
        EnzymeSummaryCollection enzymes = new EnzymeSummaryCollection();
        while (it.hasNext()) {
            ArrayOfString result = (ArrayOfString)it.next();
             EnzymeSummary enzymeSummary =
                     getEntryResultToEnzymeSummary(result.getString());
                enzymes.getEnzymesummary().add(enzymeSummary);
        }
         return enzymes;
    }
*/
    public static List<ResultFieldList> setResultsValues( 
            ResultFieldList resultFieldList
            , ArrayOfArrayOfString results) {
        //ResultFieldList resultFieldList = domain.getResultFieldList();
        List<ResultFieldList> resultsValues = new ArrayList<ResultFieldList>();
        List<ArrayOfString> resultList = results.getArrayOfString();
        Iterator<ArrayOfString> it = resultList.iterator();
        while (it.hasNext()) {
            ArrayOfString result = (ArrayOfString)it.next();
            ResultFieldList clonedObj =
                    DataTypeConverter.cloneResultFieldList(resultFieldList);
            setResultValues(clonedObj, result);
            resultsValues.add(clonedObj);
        }
        return resultsValues;
    }

    public static void setResultValues(ResultFieldList resultFieldList
            , ArrayOfString result) {
        List<String> values = result.getString();
        Iterator<?> itField = resultFieldList.getResultField().iterator();
        Iterator<String> itResult = values.iterator();
        while (itField.hasNext() && itResult.hasNext()) {
            ResultField resultField = (ResultField)itField.next();
            String value = (String)itResult.next();
            resultField.setResultvalue(value);
        }
    }

}
