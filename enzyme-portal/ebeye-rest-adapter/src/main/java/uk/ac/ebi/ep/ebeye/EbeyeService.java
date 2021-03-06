/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.ep.ebeye;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.ep.ebeye.autocomplete.EbeyeAutocomplete;
import uk.ac.ebi.ep.ebeye.autocomplete.Suggestion;

/**
 *
 * @author joseph
 */
@Service
public class EbeyeService {

    private final RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());
    // String url = "http://www.ebi.ac.uk/ebisearch/ws/rest/uniprot?query=" + query + "&format=json&size=100";

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        //factory.setReadTimeout(2000);
        //factory.setConnectTimeout(200);
        return factory;
    }

    private EbeyeSearchResult getEbeyeSearchResult(String url) {
        EbeyeSearchResult results = restTemplate.getForObject(url.trim(), EbeyeSearchResult.class);
        return results;
    }

    @Async
    private Future<EbeyeSearchResult> searchEbeyeDomain(String url) throws InterruptedException {
        EbeyeSearchResult results = getEbeyeSearchResult(url);
        return new AsyncResult<>(results);
    }

    public EbeyeSearchResult query(String query) {
        List<String> ebeyeDomains = new ArrayList<String>() {
            {
                //add("http://www.ebi.ac.uk/ebisearch/ws/rest/uniprot?format=json&size=100&query=");
                //add("http://wwwdev.ebi.ac.uk/ebisearch/ws/rest/enzymeportal?facetcount=100&format=json&query=");
                add("http://wwwdev.ebi.ac.uk/ebisearch/ws/rest/enzymeportal?format=json&size=100&query=");
                //add("http://www.ebi.ac.uk/ebisearch/ws/rest/intenz?format=json&size=100&fields=UNIPROT&query=");
                //add("http://www.ebi.ac.uk/ebisearch/ws/rest/uniprot?format=json&size=100&query=");
                //add("http://wwwdev.ebi.ac.uk/ebisearch/ws/rest/enzymeportal?format=json&size=100&fields=name&query=");
            }
        };

        // get element as soon as it is available
        Optional<EbeyeSearchResult> result = ebeyeDomains.stream().map((base) -> {
            String url = base + query;
            // open connection and fetch the result

            return getEbeyeSearchResult(url.trim());
//            EbeyeSearchResult r = null;
//            try {
//                r = searchEbeyeDomain(url).get();
//                
//            } catch (InterruptedException | ExecutionException ex) {
//                //Logger.getLogger(EbeyeService.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            return r;

        }).findAny();
        return result.get();
    }

    public List<Suggestion> ebeyeAutocompleteSearch(String searchTerm) {
        String url = "http://wwwdev.ebi.ac.uk/ebisearch/ws/rest/enzymeportal/autocomplete?term=" + searchTerm + "&format=json";

        EbeyeAutocomplete autocomplete = restTemplate.getForObject(url.trim(), EbeyeAutocomplete.class);

        return autocomplete.getSuggestions();

    }

    public List<EbeyeSearchResult> query(String query, int iteration) {
        List<String> ebeyeDomains = new LinkedList<>();

        for (int index = 0; index <= iteration; index++) {

            String link = "http://wwwdev.ebi.ac.uk/ebisearch/ws/rest/enzymeportal?format=json&size=100&start=" + index * 100 + "&fields=name&query=";
            ebeyeDomains.add(link);
        }

        EbeyeSearchResult result = null;
        List<EbeyeSearchResult> results = new ArrayList<>();
        for (String base : ebeyeDomains) {
            String url = base + query;
            result = getEbeyeSearchResult(url.trim());

            results.add(result);
        }

        return results;
    }

}
