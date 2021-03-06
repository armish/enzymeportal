/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.ep.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.ac.ebi.biobabel.blast.NcbiBlastClient;
import uk.ac.ebi.biobabel.blast.NcbiBlastClientException;
import uk.ac.ebi.ep.adapter.literature.CitexploreWSClientPool;
import uk.ac.ebi.ep.adapter.literature.LiteratureConfig;
import uk.ac.ebi.ep.base.search.EnzymeFinder;
import uk.ac.ebi.ep.base.search.EnzymeRetriever;
import uk.ac.ebi.ep.data.domain.UniprotEntry;
import uk.ac.ebi.ep.data.entry.Field;
import uk.ac.ebi.ep.data.enzyme.model.ChemicalEntity;
import uk.ac.ebi.ep.data.enzyme.model.CountableMolecules;
import uk.ac.ebi.ep.data.enzyme.model.Enzyme;
import uk.ac.ebi.ep.data.enzyme.model.EnzymeModel;
import uk.ac.ebi.ep.data.enzyme.model.EnzymeReaction;
import uk.ac.ebi.ep.data.enzyme.model.Molecule;
import uk.ac.ebi.ep.data.enzyme.model.ProteinStructure;
import uk.ac.ebi.ep.data.enzyme.model.ReactionPathway;
import uk.ac.ebi.ep.data.exceptions.EnzymeFinderException;
import uk.ac.ebi.ep.data.exceptions.EnzymeRetrieverException;
import uk.ac.ebi.ep.data.exceptions.MultiThreadingException;
import uk.ac.ebi.ep.data.search.model.Disease;
import uk.ac.ebi.ep.data.search.model.SearchModel;
import uk.ac.ebi.ep.data.search.model.SearchParams;
import uk.ac.ebi.ep.data.search.model.SearchResults;
import uk.ac.ebi.ep.ebeye.autocomplete.Suggestion;
import uk.ac.ebi.ep.enzymeservices.chebi.ChebiConfig;
import uk.ac.ebi.ep.enzymeservices.intenz.IntenzConfig;
import uk.ac.ebi.ep.enzymeservices.reactome.ReactomeConfig;
import uk.ac.ebi.ep.functions.Functions;
import uk.ac.ebi.ep.functions.HtmlUtility;
import uk.ac.ebi.xchars.SpecialCharacters;
import uk.ac.ebi.xchars.domain.EncodingType;

/**
 *
 * @author joseph
 */
@Controller
public class SearchController extends AbstractController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchController.class);
    
    private static final String ENZYME_MODEL = "enzymeModel";
    private static final String ERROR = "error";
    
    @Autowired
    private LiteratureConfig literatureConfig;
    @Autowired
    private ReactomeConfig reactomeConfig;
    @Autowired
    private ChebiConfig chebiConfig;
    
    @Autowired
    protected IntenzConfig intenzConfig;
    
    private enum ResponsePage {
        
        ENTRY, ERROR;
        
        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
    
    private boolean startsWithDigit(String data) {
        return Character.isDigit(data.charAt(0));
    }
    
    @PostConstruct
    public void init() {
        try {
            CitexploreWSClientPool.setSize(
                    literatureConfig.getCitexploreClientPoolSize());
            LOGGER.info("CiteXplore client pool size set successfuly");
        } catch (Exception e) {
            LOGGER.error("Unable to set CiteXplore client pool size", e);
        }
    }

    /**
     * Process the entry page,
     *
     * @param accession The UniProt accession of the enzyme.
     * @param field the requested tab.
     * @param model
     * @param session
     * @return
     */
    @RequestMapping(value = "/search/{accession}/{field}")
    protected String getEnzymeModel(Model model,
            @PathVariable String accession, @PathVariable String field,
            HttpSession session) {
        Field requestedField = Field.valueOf(field);
        EnzymeRetriever retriever = new EnzymeRetriever(enzymePortalService, ebeyeRestService);
        
        retriever.getIntenzAdapter().setConfig(intenzConfig);
        EnzymeModel enzymeModel = null;
        String responsePage = ResponsePage.ENTRY.toString();
        try {
            switch (requestedField) {
                case proteinStructure:
                    enzymeModel = retriever.getProteinStructure(accession);
                    break;
                case reactionsPathways:
                    retriever.getReactomeAdapter().setConfig(reactomeConfig);
                    enzymeModel = retriever.getReactionsPathways(accession);
                    break;
                case molecules:
                    retriever.getChebiAdapter().setConfig(chebiConfig);
                    enzymeModel = retriever.getMolecules(accession);
                    break;
                case diseaseDrugs:
                    
                    enzymeModel = retriever.getDiseases(accession);
                    break;
                case literature:
                    retriever.getLiteratureAdapter().setConfig(literatureConfig);
                    enzymeModel = retriever.getLiterature(accession);
                    break;
                default:
                    enzymeModel = retriever.getEnzyme(accession);
                    requestedField = Field.enzyme;
                    break;
            }
            if (enzymeModel != null) {
                enzymeModel.setRequestedfield(requestedField.name());
                model.addAttribute(ENZYME_MODEL, enzymeModel);
                addToHistory(session, accession);

                // If we got here from a bookmark, the summary might not be cached:
                String summId = Functions.getSummaryBasketId(enzymeModel);
                @SuppressWarnings("unchecked")
                final Map<String, UniprotEntry> sls = (Map<String, UniprotEntry>) session.getAttribute(Attribute.lastSummaries.name());
                if (sls == null) {
                    setLastSummaries(session, Collections.singletonList(
                            (UniprotEntry) enzymeModel));
                } else if (sls.get(summId) == null) {
                    sls.put(summId, enzymeModel);
                }
            }
        } catch (EnzymeRetrieverException ex) {
            // FIXME: this is an odd job to signal an error for the JSP!
            LOGGER.error("Unable to retrieve the entry!", ex);
            if (requestedField.getName().equalsIgnoreCase(Field.diseaseDrugs.getName())) {
                enzymeModel = new EnzymeModel();
                enzymeModel.setName("Diseases");
                enzymeModel.setRequestedfield(requestedField.name());
                Disease d = new Disease();
                d.setName(ERROR);
                enzymeModel.getDisease().add(0, d);
                model.addAttribute("enzymeModel", enzymeModel);
                LOGGER.error("Error in retrieving Disease Information");
            }
            if (requestedField.getName().equalsIgnoreCase(Field.molecules.getName())) {
                enzymeModel = new EnzymeModel();
                enzymeModel.setRequestedfield(requestedField.getName());
                enzymeModel.setName("Small Molecues");
                Molecule molecule = new Molecule();
                molecule.setName(ERROR);
                ChemicalEntity chemicalEntity = new ChemicalEntity();
                chemicalEntity.setDrugs(new CountableMolecules());
                chemicalEntity.getDrugs().setMolecule(new ArrayList<>());
                chemicalEntity.getDrugs().getMolecule().add(molecule);
                enzymeModel.setMolecule(chemicalEntity);
                model.addAttribute(ENZYME_MODEL, enzymeModel);
                LOGGER.error("Error in retrieving Molecules Information");
            }
            if (requestedField.getName().equalsIgnoreCase(Field.enzyme.getName())) {
                
                enzymeModel = new EnzymeModel();
                enzymeModel.setRequestedfield(requestedField.getName());
                enzymeModel.setName("Enzymes");
                Enzyme enzyme = new Enzyme();
                enzyme.getEnzymetype().add(0, ERROR);
                enzymeModel.setEnzyme(enzyme);
                
                model.addAttribute(ENZYME_MODEL, enzymeModel);
                LOGGER.error("Error in retrieving Enzymes");
            }
            if (requestedField.getName().equalsIgnoreCase(Field.proteinStructure.getName())) {
                enzymeModel = new EnzymeModel();
                enzymeModel.setRequestedfield(requestedField.getName());
                enzymeModel.setName("Protein Structures");
                ProteinStructure structure = new ProteinStructure();
                structure.setName(ERROR);
                enzymeModel.getProteinstructure().add(0, structure);
                
                model.addAttribute(ENZYME_MODEL, enzymeModel);
                LOGGER.error("Error in retrieving ProteinStructure");
            }
            if (requestedField.getName().equalsIgnoreCase(Field.reactionsPathways.getName())) {
                enzymeModel = new EnzymeModel();
                
                enzymeModel.setRequestedfield(requestedField.getName());
                enzymeModel.setName("Reactions and Pathways");
                ReactionPathway pathway = new ReactionPathway();
                EnzymeReaction reaction = new EnzymeReaction();
                
                reaction.setName(ERROR);
                pathway.setReaction(reaction);
                enzymeModel.getReactionpathway().add(0, pathway);
                
                model.addAttribute(ENZYME_MODEL, enzymeModel);
                LOGGER.error("Error in retrieving Reaction Pathways");
                
            }
            if (requestedField.getName().equalsIgnoreCase(Field.literature.getName())) {
                enzymeModel = new EnzymeModel();
                enzymeModel.setRequestedfield(requestedField.getName());
                enzymeModel.setName("Literatures");
                
                enzymeModel.getLiterature().add(0, ERROR);
                
                model.addAttribute(ENZYME_MODEL, enzymeModel);
                LOGGER.error("Error in retrieving Literature Information");
                
            }
            
        }
        
        return responsePage;
    }

    /**
     * This method is an entry point that accepts the request when the search
     * home page is loaded. It then forwards the request to the search page.
     *
     * @param model
     * @param session
     * @return
     */
    @RequestMapping(value = "/")
    public String viewSearchHome(Model model, HttpSession session) {
        SearchModel searchModelForm = new SearchModel();
        SearchParams searchParams = new SearchParams();
        searchParams.setStart(0);
        searchModelForm.setSearchparams(searchParams);
        model.addAttribute("searchModel", searchModelForm);
        clearHistory(session);
        return "index";
    }
    
    @ModelAttribute("/about")
    public SearchModel getabout(Model model) {
        SearchModel searchModelForm = searchform();
        model.addAttribute("searchModel", searchModelForm);
        return new SearchModel();
    }
    
    @RequestMapping(value = "/faq")
    public SearchModel getfaq(Model model) {
        
        SearchModel searchModelForm = searchform();
        model.addAttribute("searchModel", searchModelForm);
        return searchModelForm;
        
    }
    
    @RequestMapping(value = "/search")
    public SearchModel getSearch(Model model) {
        SearchModel searchModelForm = searchform();
        model.addAttribute("searchModel", searchModelForm);
        return searchModelForm;
    }

    /**
     * Processes the search request. When user enters a search text and presses
     * the submit button the request is processed here.
     *
     * @param searchModel
     * @param model
     * @param session
     * @param request
     * @return
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public String postSearchResult(SearchModel searchModel, Model model,
            HttpSession session, HttpServletRequest request) {
        String view = "error";
        
        String searchKey = null;
        SearchResults results = null;
        
        try {

            // See if it is already there, perhaps we are paginating:
            Map<String, SearchResults> prevSearches
                    = getPreviousSearches(session.getServletContext());
            searchKey = getSearchKey(searchModel.getSearchparams());
            results = prevSearches.get(searchKey);
            if (results == null) {
                // New search:
                clearHistory(session);
                
                switch (searchModel.getSearchparams().getType()) {
                    case KEYWORD:
                        results = searchKeyword(searchModel.getSearchparams());
                        break;
                    case SEQUENCE:
                        view = searchSequence(model, searchModel);
                        break;
                    case COMPOUND:
                        results = searchCompound(model, searchModel);
                        break;
                    default:
                }
            }
            
            if (results != null) { // something to show
                cacheSearch(session.getServletContext(), searchKey, results);
                setLastSummaries(session, results.getSummaryentries());
                searchModel.setSearchresults(results);
                applyFilters(searchModel, request);
                model.addAttribute("searchConfig", searchConfig);
                model.addAttribute("searchModel", searchModel);
                model.addAttribute("pagination", getPagination(searchModel));
                clearHistory(session);
                addToHistory(session, searchModel.getSearchparams().getType(),
                        searchKey);
                view = "search";
            }
        } catch (Exception e) {
            LOGGER.error("one of the search params (Text or Sequence is :" + searchKey, e);
        }
        
        return view;
    }
    
    @Override
    protected void clearHistory(HttpSession session) {
        @SuppressWarnings("unchecked")
        List<String> history = (LinkedList<String>) session.getAttribute(Attribute.history.getName());
        if (history == null) {
            history = new LinkedList<>();
            session.setAttribute(Attribute.history.getName(), history);
        } else {
            history.clear();
        }
    }
    
    @Override
    protected void addToHistory(HttpSession session, String s) {
        @SuppressWarnings("unchecked")
        List<String> history = (LinkedList<String>) session.getAttribute(Attribute.history.getName());
        if (history == null) {
            history = new LinkedList<>();
            session.setAttribute(Attribute.history.getName(), history);
        }
        if (history.isEmpty() || !history.get(history.size() - 1).equals(s)) {
            String cleanedText = HtmlUtility.cleanText(s);
            history.add(cleanedText);
        }
    }

    /**
     * Adds a search to the user history. The history item (String) actually
     * stored depends on the type of search, so that the links can be re-created
     * in the web page properly (see <code>breadcrumbs.jsp</code>).
     *
     * @param session the user session.
     * @param searchType the search type.
     * @param s the text to be added to history.
     */
    @Override
    protected void addToHistory(HttpSession session, SearchParams.SearchType searchType, String s) {
        switch (searchType) {
            case KEYWORD:
                addToHistory(session, "searchparams.text=" + s);
                break;
            case COMPOUND:
                addToHistory(session,
                        "searchparams.type=COMPOUND&searchparams.text=" + s);
                break;
            case SEQUENCE:
                addToHistory(session, "searchparams.sequence=" + s);
                break;
            default:
                 addToHistory(session, "searchparams.text=" + s);
            
        }
    }

    /**
     * Retrieves any previous searches stored in the application context.
     *
     * @param servletContext the application context.
     * @return a map of searches to results.
     */
    @SuppressWarnings("unchecked")
    protected Map<String, SearchResults> getPreviousSearches(
            ServletContext servletContext) {
        Map<String, SearchResults> prevSearches = (Map<String, SearchResults>) servletContext.getAttribute(Attribute.prevSearches.name());
        if (prevSearches == null) {
            // Map implementation which maintains the order of access:
            prevSearches = Collections.synchronizedMap(
                    new LinkedHashMap<String, SearchResults>(
                            searchConfig.getSearchCacheSize(), 1, true));
            
            servletContext.setAttribute(Attribute.prevSearches.getName(),
                    prevSearches);
            
        }
        return prevSearches;
    }

    /**
     * Processes a string to normalise it to use as a key in the application
     * cache.<br> Note that the key for a ChEBI ID depends on the type of
     * search: if a keyword search, the prefix will be lowercase (
     * <code>chebi:</code>); if a compound structure search, the prefix will be
     * uppercase ( <code>CHEBI:</code>).
     *
     * @param searchParams the search parameters, including the original search
     * text from the user.
     * @return A normalised string.
     */
    protected String getSearchKey(SearchParams searchParams) {
        
        String key = null;
        
        switch (searchParams.getType()) {
            case KEYWORD:
                key = searchParams.getText().trim().toLowerCase();
                break;
            case SEQUENCE:
                key = searchParams.getSequence().trim().toUpperCase()
                        .replaceAll("[\n\r]", "");
                break;
            case COMPOUND:
                key = searchParams.getText().trim().toUpperCase();
                break;
            default:
                key = searchParams.getText().trim().toLowerCase();
        }
        return key;
    }

    /**
     * Searches by keyword.
     *
     * @param searchParameters the search parameters.
     * @return the search results.
     */
    protected SearchResults searchKeyword(SearchParams searchParameters) {
        SearchResults results = null;
        EnzymeFinder finder = new EnzymeFinder(enzymePortalService, ebeyeRestService);
        results = finder.getEnzymes(searchParameters);
        
        return results;
    }

    /**
     * Uses a finder to search by compound ID.
     *
     * @param model the view model.
     * @param searchModel the search model, including a compound ID as the
     * <code>text</code> parameter.
     * @return the search results, or <code>null</code> if nothing found.
     * @since 1.0.27
     */
    private SearchResults searchCompound(Model model, SearchModel searchModel) {
        SearchResults results = null;
        EnzymeFinder finder = null;
        try {
            finder = new EnzymeFinder(enzymePortalService, ebeyeRestService);
            
            results = finder.getEnzymesByCompound(searchModel.getSearchparams());
            searchModel.setSearchresults(results);
            model.addAttribute("searchModel", searchModel);
            model.addAttribute("pagination", getPagination(searchModel));
        } catch (EnzymeFinderException e) {
            LOGGER.error("Unable to get enzymes by compound", e);
        }
        return results;
    }

    /**
     * A wrapper of {@code postSearchResult} method, created to accept the
     * search request using GET.
     *
     * @param searchModel
     * @param result
     * @param model
     * @param session
     * @param request
     * @return
     */
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String getSearchResults(SearchModel searchModel, BindingResult result,
            Model model, HttpSession session, HttpServletRequest request) {
        return postSearchResult(searchModel, model, session, request);
    }
    
    @RequestMapping(value = "/advanceSearch",
            method = RequestMethod.GET)
    public String getAdvanceSearch(Model model) {
        return "advanceSearch";
    }
    
    @ResponseBody
    @RequestMapping("/service/search")
    public List<Suggestion> enzymesAutocompleteSearch(@RequestParam(value = "name", required = true) String name) {
        if (name != null && name.length() >= 3) {
            String keyword = String.format("%%%s%%", name);
            
            List<Suggestion> suggestions = ebeyeService.ebeyeAutocompleteSearch(keyword.trim());
            
            if (suggestions != null && !suggestions.isEmpty()) {
                return suggestions.stream().distinct().collect(Collectors.toList());
            } else {
                return new ArrayList<>();
            }
            
        }
        
        return new ArrayList<>();
        
    }
    
    public String resolveSpecialCharacters(String data) {
        
        SpecialCharacters xchars = SpecialCharacters.getInstance(null);
        EncodingType[] encodings = {
            EncodingType.CHEBI_CODE,
            EncodingType.COMPOSED,
            EncodingType.EXTENDED_HTML,
            EncodingType.GIF,
            EncodingType.HTML,
            EncodingType.HTML_CODE,
            EncodingType.JPG,
            EncodingType.SWISSPROT_CODE,
            EncodingType.UNICODE
        };
        
        if (!xchars.validate(data)) {
            LOGGER.warn("SPECIAL CHARACTER PARSING ERROR : This is not a valid xchars string!" + data);
            
        }
        LOGGER.info("available encodings " + encodings);
        return xchars.xml2Display(data, EncodingType.CHEBI_CODE);
    }
    
    @RequestMapping(value = "/underconstruction", method = RequestMethod.GET)
    public String getSearchResult(Model model) {
        return "underconstruction";
    }
    
    @RequestMapping(value = "/about", method = RequestMethod.GET)
    public String getAbout(Model model) {
        return "about";
    }

    /**
     * Sends a search to a BLAST server. The job ID returned is stored in the
     * model as <code>jobId</code> attribute.
     *
     * @param model the Spring model.
     * @param searchModel the EP search model.
     * @return the view according to the search: <code>running</code> if
     * everything went ok and the search job is running, <code>error</code>
     * otherwise.
     */
    private String searchSequence(Model model, SearchModel searchModel) {
        String view = "error";
        EnzymeFinder finder = new EnzymeFinder(enzymePortalService, ebeyeRestService);
        try {
            String sequence = searchModel.getSearchparams().getSequence()
                    .trim().toUpperCase();
            String jobId = finder.blast(sequence);
            searchModel.getSearchparams().setPrevioustext(sequence);
            model.addAttribute("jobId", jobId);
            LOGGER.debug("BLAST job running: " + jobId);
            view = "running";
        } catch (NcbiBlastClientException e) {
            LOGGER.error("NcbiBlastClientException", e);
        }
        return view;
    }
    
    @RequestMapping(value = "/checkJob", method = RequestMethod.POST)
    public String checkJob(@RequestParam String jobId, Model model,
            SearchModel searchModel, HttpSession session) throws MultiThreadingException {
        String view = null;
        try {
            EnzymeFinder enzymeFinder = new EnzymeFinder(enzymePortalService, ebeyeRestService);
            
            NcbiBlastClient.Status status = enzymeFinder.getBlastStatus(jobId);
            switch (status) {
                case ERROR:
                case FAILURE:
                case NOT_FOUND:
                    LOGGER.error("Blast job returned status " + status);
                    view = "error";
                    break;
                case FINISHED:
                    LOGGER.debug("BLAST job finished");
                    SearchResults results = enzymeFinder.getBlastResult(jobId);
                    SearchParams searchParams = searchModel.getSearchparams();
                    String resultKey = getSearchKey(searchParams);
                    cacheSearch(session.getServletContext(), resultKey, results);
                    
                    searchParams.setSize(searchConfig.getResultsPerPage());
                    searchModel.setSearchresults(results);
                    model.addAttribute("searchModel", searchModel);
                    model.addAttribute("pagination", getPagination(searchModel));
                    view = "search";
                    break;
                case RUNNING:
                    model.addAttribute("jobId", jobId);
                    view = "running";
                    break;
                default:
            }
            
        } catch (NcbiBlastClientException t) {
            LOGGER.error("While checking BLAST job", t);
            view = "error";
        }
        return view;
    }
    
}
