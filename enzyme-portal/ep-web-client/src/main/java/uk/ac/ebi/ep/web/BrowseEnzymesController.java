/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.ep.web;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import uk.ac.ebi.ep.core.search.EnzymeFinder;
import uk.ac.ebi.ep.enzymes.EnzymeEntry;
import uk.ac.ebi.ep.enzymes.EnzymeSubSubclass;
import uk.ac.ebi.ep.enzymes.EnzymeSubclass;
import uk.ac.ebi.ep.enzymes.IntenzEnzyme;
import uk.ac.ebi.ep.mm.CustomXRef;
import uk.ac.ebi.ep.mm.Entry;
import uk.ac.ebi.ep.search.exception.EnzymeFinderException;
import uk.ac.ebi.ep.search.model.SearchModel;
import uk.ac.ebi.ep.search.model.SearchParams;
import uk.ac.ebi.ep.search.model.SearchResults;

/**
 *
 * @author joseph
 */
@Controller
public class BrowseEnzymesController extends AbstractController {

    private static final Logger LOGGER = Logger.getLogger(BrowseEnzymesController.class);
    //concrete jsp's
    private static final String BROWSE_ENZYMES = "/browse_enzymes";
    private static final String EC = "/ec";
    private static final String RESULT = "/search_result_ec";
    //abtract url
    private static final String BROWSE_ENZYME_CLASSIFICATION = "/browse/enzymes";
    private static final String BROWSE_EC = "/browse/enzyme";
    private static final String SEARCH_ENZYMES = "/search/enzymes";
    private static final String EC_NUMBER = "ec";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String SUBCLASSES = "subclasses";
    private static final String SUBSUBCLASSES = "subsubclasses";
    private static final String ENTRIES = "entries";
    private static final String INTENZ_URL = "http://www.ebi.ac.uk/intenz/ws/EC";
    private static final String ROOT = "ROOT";
    private static final String SUBCLASS = "SUBCLASS";
    private static final String SUBSUBCLASS = "SUBSUBCLASS";
    private static final String selectedEc = "selectedEc";
    private transient int total;

    private SearchResults findEnzymesByEntry(String entry_id) {

        SearchResults results = null;
        EnzymeFinder finder = new EnzymeFinder(searchConfig);
        finder.getUniprotAdapter().setConfig(uniprotConfig);
        finder.getIntenzAdapter().setConfig(intenzConfig);
        finder.getEbeyeAdapter().setConfig(ebeyeConfig);


        Entry entry = finder.findEntryById(entry_id);//get the entry id ( ec)


        List<String> ids = new ArrayList<>();
        Collection<CustomXRef> xrefResult = null;
        //int total = 0;
        if (entry != null) {

    // long startTime = System.currentTimeMillis();
      xrefResult = finder.getXrefs(entry);
        //long endTime = System.currentTimeMillis();

        //System.out.println("CustomXRef processing took " + (endTime - startTime) + 
               // " milliseconds.");
           
            for (CustomXRef ref : xrefResult) {
                ids = ref.getIdList();
                total = ref.getResult_count();



            }

            SearchParams searchParams = new SearchParams();
            searchParams.setText(entry.getEntryName());
            searchParams.setType(SearchParams.SearchType.KEYWORD);
            searchParams.setStart(0);
            searchParams.setPrevioustext(entry.getEntryName());
            
            finder.setSearchParams(searchParams);

            if (ids.size() > 0) {

                results = finder.computeEnzymeSummary(ids, new SearchResults());
                
            } else if (ids.isEmpty()) {
                //if not found at mm, search via ebeye using the enzyme name as keyword
                return getEnzymes(finder, searchParams);
            }
        }
        return results;
    }

    private SearchResults getEnzymes(EnzymeFinder finder, SearchParams searchParams) {
        SearchResults results = null;

        try {
            results = finder.getEnzymes(searchParams);
        } catch (EnzymeFinderException ex) {

            LOGGER.fatal("ERROR while searching for enzymes", ex);
        }
        return results;
    }

    private String computeResult(@ModelAttribute("searchModel") SearchModel searchModel,
            @RequestParam(value = "entryid", required = false) String entryID,
            Model model, HttpSession session, HttpServletRequest request) {

        String view = "error";

        Map<String, SearchResults> prevSearches =
                getPreviousSearches(session.getServletContext());
        String searchKey = getSearchKey(searchModel.getSearchparams());

        SearchResults results = prevSearches.get(searchKey);

        if (results == null) {
            // New search:
            clearHistory(session);
            results = findEnzymesByEntry(entryID);

        }

        if (results != null) {
            cacheSearch(session.getServletContext(), searchKey, results);
            setLastSummaries(session, results.getSummaryentries());
            searchModel.setSearchresults(results);
            applyFilters(searchModel, request);
            model.addAttribute("searchModel", searchModel);
            model.addAttribute("pagination", getPagination(searchModel));
            clearHistory(session);
            addToHistory(session, searchModel.getSearchparams().getType(),
                    searchKey);
            view = RESULT;
        }


        return view;
    }
    
//        protected Pagination getPagination1(SearchModel searchModel) {
//        Pagination pagination = new Pagination(
//                total,
//                searchConfig.getResultsPerPage());
//        pagination.setFirstResult(searchModel.getSearchparams().getStart());
//        return pagination;
//    }
    

    @RequestMapping(value = BROWSE_ENZYME_CLASSIFICATION, method = RequestMethod.GET)
    public String browseEc(Model model, HttpSession session) {
        clearSelectedEc(session);

        SearchModel searchModelForm = searchform();
        model.addAttribute("searchModel", searchModelForm);
        return BROWSE_ENZYMES;
}

    @RequestMapping(value = BROWSE_EC + "/{ec}/{ecname}", method = RequestMethod.GET)
    public String showStaticEc(@ModelAttribute("searchModel") SearchModel searchModel,
            @PathVariable("ec") String ec, @PathVariable("ecname") String ecname,
            Model model, HttpSession session, HttpServletRequest request) throws MalformedURLException, IOException {
        clearSelectedEc(session);
        browseEc(model, session, ecname, null, null, null, ec);
        return EC;


    }

    @RequestMapping(value = BROWSE_EC, method = RequestMethod.GET)
    public String browseEcTree(@ModelAttribute("searchModel") SearchModel searchModel,
            @RequestParam(value = "ec", required = false) String ec, @RequestParam(value = "ecname", required = false) String ecname,
            @RequestParam(value = "subecname", required = false) String subecname,
            @RequestParam(value = "subsubecname", required = false) String subsubecname,
            @RequestParam(value = "entryecname", required = false) String entryecname, Model model, HttpSession session, HttpServletRequest request) throws MalformedURLException, IOException {

        if (ec != null && ec.length() >= 7) {
            model.addAttribute("entryid", ec);
            return computeResult(searchModel, ec, model, session, request);

        } else {
            browseEc(model, session, ecname, subecname, subsubecname, entryecname, ec);
        }

        return EC;
    }

    @RequestMapping(value = SEARCH_ENZYMES, method = RequestMethod.GET)
    public String showEnzymeByEC(@ModelAttribute("searchModel") SearchModel searchModel,
            @RequestParam(value = "ec", required = false) String ec, @RequestParam(value = "ecname", required = false) String ecname,
            @RequestParam(value = "subecname", required = false) String subecname,
            @RequestParam(value = "subsubecname", required = false) String subsubecname,
            @RequestParam(value = "entryecname", required = false) String entryecname, Model model, HttpSession session, HttpServletRequest request) throws MalformedURLException, IOException {


        model.addAttribute("entryid", ec);
        model.addAttribute("entryname", entryecname);
        return computeResult(searchModel, ec, model, session, request);
    }

    @RequestMapping(value = SEARCH_ENZYMES, method = RequestMethod.POST)
    public String searchEnzymesByEcPost(@ModelAttribute("searchModel") SearchModel searchModel,
            @RequestParam(value = "ec", required = false) String ec, @RequestParam(value = "ecname", required = false) String ecname,
            @RequestParam(value = "subecname", required = false) String subecname,
            @RequestParam(value = "subsubecname", required = false) String subsubecname,
            @RequestParam(value = "entryecname", required = false) String entryecname, Model model, HttpSession session, HttpServletRequest request) throws MalformedURLException, IOException {

        model.addAttribute("entryid", ec);
        model.addAttribute("entryname", entryecname);

        return computeResult(searchModel, ec, model, session, request);

    }

    private void browseEc(Model model, HttpSession session, String ecname, String sub_ecname, String subsub_ecname, String entry_ecname, String ec) throws MalformedURLException, IOException {

        String intenz_url = String.format("%s/%s.json", INTENZ_URL, ec);
        URL url = new URL(intenz_url);
        try (InputStream is = url.openStream();
                JsonReader rdr = Json.createReader(is)) {

            computeJsonData(rdr, model, session, ecname, sub_ecname, subsub_ecname, entry_ecname, ec);
        }
    }

    /**
     * This method keeps track of the selected enzymes in their hierarchy for
     * the browse enzyme
     *
     * @param session
     * @param s the selected enzyme
     * @param type the position in the hierarchy
     */
    private void addToSelectedEc(HttpSession session, IntenzEnzyme s, String type) {
        @SuppressWarnings("unchecked")
        LinkedList<IntenzEnzyme> history = (LinkedList<IntenzEnzyme>) session.getAttribute(selectedEc);


        if (history == null) {

            history = new LinkedList<>();
            session.setAttribute(selectedEc, history);
        }

        if (!history.isEmpty() && history.contains(s)) {

            if (type.equalsIgnoreCase(ROOT) && history.size() == 2) {
                history.removeLast();

            }
            if (type.equalsIgnoreCase(ROOT) && history.size() == 3) {
                history.removeLast();
                history.removeLast();
                //history.remove(history.size()-1);//same as above


            }
            if (type.equalsIgnoreCase(SUBCLASS) && history.size() == 2) {
                history.removeLast();
                history.add(s);

            }
            if (type.equalsIgnoreCase(SUBCLASS) && history.size() == 3) {
                history.removeLast();

            }

        } else if ((history.isEmpty() || !history.contains(s)) && (history.size() < 3)) {
            history.add(s);

        }
    }

    private void clearSelectedEc(HttpSession session) {
        @SuppressWarnings("unchecked")
        LinkedList<IntenzEnzyme> history = (LinkedList<IntenzEnzyme>) session.getAttribute(selectedEc);
        if (history == null) {
            //history = new ArrayList<String>();
            history = new LinkedList<>();
            session.setAttribute(selectedEc, history);
        } else {
            history.clear();
        }
    }

    private void computeJsonData(JsonReader jsonReader, Model model, HttpSession session, String... ecname) {
        JsonObject jsonObject = jsonReader.readObject();

        IntenzEnzyme root = new IntenzEnzyme();

        String ec = jsonObject.getString(EC_NUMBER);
        //String name = jsonObject.getString(NAME);
        String description = null;



        if (jsonObject.containsKey(DESCRIPTION)) {
            description = jsonObject.getString(DESCRIPTION);

            root.setDescription(description);
        }
        root.setEc(ec);
        root.setName(ecname[0]);
        root.setSubclassName(ecname[1]);
        root.setSubsubclassName(ecname[2]);
        root.setEntryName(ecname[3]);

        //compute the childObject
        if (jsonObject.containsKey(SUBCLASSES)) {

            JsonArray jsonArray = jsonObject.getJsonArray(SUBCLASSES);

            for (JsonObject childObject : jsonArray.getValuesAs(JsonObject.class)) {
                String _ec = null;
                String _name = null;
                String _desc = null;
                _ec = childObject.getString(EC_NUMBER);
                _name = childObject.getString(NAME);

                EnzymeSubclass subclass = new EnzymeSubclass();

                if (childObject.containsKey(DESCRIPTION)) {
                    _desc = childObject.getString(DESCRIPTION);
                    subclass.setDescription(_desc);
                }

                subclass.setEc(_ec);
                subclass.setName(_name);
                root.getChildren().add(subclass);


            }
            addToSelectedEc(session, root, ROOT);
            model.addAttribute("json", root);
        }
        if (jsonObject.containsKey(SUBSUBCLASSES)) {

            JsonArray jsonArray = jsonObject.getJsonArray(SUBSUBCLASSES);

            for (JsonObject childObject : jsonArray.getValuesAs(JsonObject.class)) {
                String _ec = null;
                String _name = null;
                String _desc = null;
                _ec = childObject.getString(EC_NUMBER);
                _name = childObject.getString(NAME);

                EnzymeSubSubclass subsubclass = new EnzymeSubSubclass();


                if (childObject.containsKey(DESCRIPTION)) {
                    _desc = childObject.getString(DESCRIPTION);

                    subsubclass.setDescription(_desc);
                }


                subsubclass.setEc(_ec);
                subsubclass.setName(_name);

                root.getSubSubclasses().add(subsubclass);

            }

            model.addAttribute("json", root);
            addToSelectedEc(session, root, SUBCLASS);
        }
        if (jsonObject.containsKey(ENTRIES)) {

            JsonArray jsonArray = jsonObject.getJsonArray(ENTRIES);


            for (JsonObject childObject : jsonArray.getValuesAs(JsonObject.class)) {
                String _ec = null;
                String _name = null;
                String _desc = null;
                _ec = childObject.getString(EC_NUMBER);
                _name = childObject.getString(NAME);


                EnzymeEntry entries = new EnzymeEntry();
                if (childObject.containsKey(DESCRIPTION)) {
                    _desc = childObject.getString(DESCRIPTION);

                    entries.setDescription(_desc);
                }

                entries.setEc(_ec);
                entries.setName(_name);
                root.setEc(ecname[4]);
                root.getEntries().add(entries);

            }

            model.addAttribute("json", root);
            addToSelectedEc(session, root, SUBSUBCLASS);
        }

    }
}
