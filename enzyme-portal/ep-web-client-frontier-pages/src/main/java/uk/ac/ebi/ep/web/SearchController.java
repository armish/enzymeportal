package uk.ac.ebi.ep.web;


import java.util.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import uk.ac.ebi.ep.adapter.chebi.ChebiConfig;
import uk.ac.ebi.ep.adapter.ebeye.EbeyeConfig;
import uk.ac.ebi.ep.adapter.intenz.IntenzConfig;
import uk.ac.ebi.ep.adapter.literature.CitexploreWSClientPool;
import uk.ac.ebi.ep.adapter.literature.LiteratureConfig;
import uk.ac.ebi.ep.adapter.reactome.ReactomeConfig;
import uk.ac.ebi.ep.adapter.uniprot.UniprotConfig;
import uk.ac.ebi.ep.core.filter.CompoundsPredicate;
import uk.ac.ebi.ep.core.filter.DefaultPredicate;
import uk.ac.ebi.ep.core.filter.DiseasesPredicate;
import uk.ac.ebi.ep.core.filter.SpeciesPredicate;
import uk.ac.ebi.ep.core.search.Config;
import uk.ac.ebi.ep.core.search.EnzymeFinder;
import uk.ac.ebi.ep.core.search.EnzymeRetriever;
import uk.ac.ebi.ep.entry.Field;
import uk.ac.ebi.ep.enzyme.model.ChemicalEntity;
import uk.ac.ebi.ep.enzyme.model.Disease;
import uk.ac.ebi.ep.enzyme.model.Enzyme;
import uk.ac.ebi.ep.enzyme.model.EnzymeModel;
import uk.ac.ebi.ep.enzyme.model.EnzymeReaction;
import uk.ac.ebi.ep.enzyme.model.Molecule;
import uk.ac.ebi.ep.enzyme.model.ProteinStructure;
import uk.ac.ebi.ep.enzyme.model.ReactionPathway;
import uk.ac.ebi.ep.search.exception.EnzymeFinderException;
import uk.ac.ebi.ep.search.model.Compound;
import uk.ac.ebi.ep.search.model.EnzymeAccession;
import uk.ac.ebi.ep.search.model.EnzymeSummary;
import uk.ac.ebi.ep.search.model.SearchModel;
import uk.ac.ebi.ep.search.model.SearchParams;
import uk.ac.ebi.ep.search.model.SearchResults;
import uk.ac.ebi.ep.search.model.Species;
import uk.ac.ebi.ep.search.result.Pagination;

/**
 *
 * @since 1.0
 * @version $LastChangedRevision$ <br/> $LastChangedDate: 2012-03-26
 * 11:53:57 +0100 (Mon, 26 Mar 2012) $ <br/> $Author$
 * @author $Author$
 */
@Controller
public class SearchController {

    private static final Logger LOGGER = Logger.getLogger(SearchController.class);

    private enum ResponsePage {

        ENTRY, ERROR;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
    @Autowired
    private EbeyeConfig ebeyeConfig;
    @Autowired
    private UniprotConfig uniprotConfig;
    @Autowired
    private Config searchConfig;
    @Autowired
    private IntenzConfig intenzConfig;
    @Autowired
    private ReactomeConfig reactomeConfig;
    @Autowired
    private ChebiConfig chebiConfig;
    @Autowired
    private LiteratureConfig literatureConfig;

    @PostConstruct
    public void init(){
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
     * @return
     */
    @RequestMapping(value = "/search/{accession}/{field}")
    protected String getEnzymeModel(Model model,
            @PathVariable String accession, @PathVariable String field,
            HttpSession session) {
        Field requestedField = Field.valueOf(field);
        EnzymeRetriever retriever = new EnzymeRetriever(searchConfig);
        retriever.getEbeyeAdapter().setConfig(ebeyeConfig);
        retriever.getUniprotAdapter().setConfig(uniprotConfig);
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
            enzymeModel.setRequestedfield(requestedField.name());
            model.addAttribute("enzymeModel", enzymeModel);
            addToHistory(session, accession);
        } catch (Exception ex) {
            LOGGER.error("Unable to retrieve the entry!", ex);
            if (requestedField.getName().equalsIgnoreCase(Field.diseaseDrugs.getName())) {
                enzymeModel = new EnzymeModel();
                enzymeModel.setRequestedfield(requestedField.name());
                Disease d = new Disease();
                d.setName("error");
                enzymeModel.getDisease().add(0, d);
                model.addAttribute("enzymeModel", enzymeModel);
                LOGGER.fatal("Error in retrieving Disease Information");
            }
            if (requestedField.getName().equalsIgnoreCase(Field.molecules.getName())) {
                enzymeModel = new EnzymeModel();
                enzymeModel.setRequestedfield(requestedField.getName());
                Molecule molecule = new Molecule();
                molecule.setName("error");
                ChemicalEntity chemicalEntity = new ChemicalEntity();
                chemicalEntity.getDrugs().add(0, molecule);
                enzymeModel.setMolecule(chemicalEntity);
                model.addAttribute("enzymeModel", enzymeModel);
                LOGGER.fatal("Error in retrieving Molecules Information");
            }
            if (requestedField.getName().equalsIgnoreCase(Field.enzyme.getName())) {

                enzymeModel = new EnzymeModel();
                enzymeModel.setRequestedfield(requestedField.getName());
                Enzyme enzyme = new Enzyme();
                enzyme.getEnzymetype().add(0, "error");
                enzymeModel.setEnzyme(enzyme);

                model.addAttribute("enzymeModel", enzymeModel);
                LOGGER.fatal("Error in retrieving Enzymes");
            }
            if (requestedField.getName().equalsIgnoreCase(Field.proteinStructure.getName())) {
                enzymeModel = new EnzymeModel();
                enzymeModel.setRequestedfield(requestedField.getName());
                ProteinStructure structure = new ProteinStructure();
                structure.setName("error");
                enzymeModel.getProteinstructure().add(0, structure);

                model.addAttribute("enzymeModel", enzymeModel);
                LOGGER.fatal("Error in retrieving ProteinStructure");
            }
            if (requestedField.getName().equalsIgnoreCase(Field.reactionsPathways.getName())) {
                enzymeModel = new EnzymeModel();

                enzymeModel.setRequestedfield(requestedField.getName());
                ReactionPathway pathway = new ReactionPathway();
                EnzymeReaction reaction = new EnzymeReaction();
                reaction.setName("error");
                pathway.setReaction(reaction);
                enzymeModel.getReactionpathway().add(0, pathway);


                model.addAttribute("enzymeModel", enzymeModel);
                LOGGER.fatal("Error in retrieving Reaction Pathways");

            }
            if (requestedField.getName().equalsIgnoreCase(Field.literature.getName())) {
                enzymeModel = new EnzymeModel();
                enzymeModel.setRequestedfield(requestedField.getName());

                enzymeModel.getLiterature().add(0, "error");

                model.addAttribute("enzymeModel", enzymeModel);
                LOGGER.fatal("Error in retrieving Literature Information");

            }

        } finally {
            retriever.closeResources();
        }
        return responsePage;
    }

    /**
     * This method is an entry point that accepts the request when the search
     * home page is loaded. It then forwards the request to the search page.
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/")
    public String viewSearchHome(Model model, HttpSession session) {
        SearchModel searchModelForm = new SearchModel();
        SearchParams searchParams = new SearchParams();
        searchParams.setText("Enter a name to search");
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

    @ModelAttribute("searchModelForm")
    public SearchModel searchform() {
        SearchModel searchModelForm = new SearchModel();
        SearchParams searchParams = new SearchParams();
        searchParams.setText("Enter a name to search");
        searchParams.setStart(0);
        searchModelForm.setSearchparams(searchParams);
        return searchModelForm;
    }

    /**
     * A wrapper of {@code postSearchResult} method, created to accept the
     * search request using GET.
     *
     * @param searchModel
     * @param result
     * @param model
     * @return
     */
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String getSearchResult(SearchModel searchModel, BindingResult result,
            Model model, HttpSession session) {
        return postSearchResult(searchModel, result, model, session);
    }

    /**
     * Processes the search request. When user enters a search text and presses
     * the submit button the request is processed here.
     *
     * @param searchModelForm
     * @param result
     * @param model
     * @return
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public String postSearchResult(SearchModel searchModelForm, BindingResult result,
            Model model, HttpSession session) {
        String view = "search";
        clearHistory(session);

        if (searchModelForm != null) {
            try {
                SearchParams searchParameters = searchModelForm.getSearchparams();
                searchParameters.setSize(searchConfig.getResultsPerPage());
                SearchResults resultSet = null;
                // See if it is already there, perhaps we are paginating:
                @SuppressWarnings("unchecked")
                Map<String, SearchResults> prevSearches = (Map<String, SearchResults>) session.getServletContext().getAttribute("searches");
                if (prevSearches == null) {
                    // Map implementation which maintains the order of access:
                    prevSearches = Collections.synchronizedMap(
                            new LinkedHashMap<String, SearchResults>(
                            searchConfig.getSearchCacheSize(), 1, true));
                    session.getServletContext().setAttribute("searches", prevSearches);
                }
                resultSet = prevSearches.get(searchParameters.getText().toLowerCase());
                if (resultSet == null) {
                    // Make a new search:
                    EnzymeFinder finder = new EnzymeFinder(searchConfig);
                    finder.getEbeyeAdapter().setConfig(ebeyeConfig);
                    finder.getUniprotAdapter().setConfig(uniprotConfig);
                    finder.getIntenzAdapter().setConfig(intenzConfig);
                    try {
                        resultSet = finder.getEnzymes(searchParameters);
                        // cache it in the session, making room if necessary:
                        synchronized (prevSearches) {
                            while (prevSearches.size() >= searchConfig.getSearchCacheSize()) {
                                // remove the eldest:
                                prevSearches.remove(prevSearches.keySet().iterator().next());
                            }
                            prevSearches.put(searchParameters.getText().toLowerCase(), resultSet);
                        }
                    } catch (EnzymeFinderException ex) {
                        LOGGER.error("Unable to create the result list because an error "
                                + "has occurred in the find method! \n", ex);
                    } finally {
                        finder.closeResources();
                    }
                }

                final int numOfResults = resultSet.getSummaryentries().size();
                Pagination pagination = new Pagination(
                        numOfResults, searchParameters.getSize());
                pagination.setFirstResult(searchParameters.getStart());

                // Filter:
                List<String> speciesFilter = searchParameters.getSpecies();
                List<String> compoundsFilter = searchParameters.getCompounds();
                List<String> diseasesFilter = searchParameters.getDiseases();

//both from auto complete and normal selection. selected items are displayed on top the list and returns back to the orignial list when not selected.
                SearchResults searchResults = resultSet;
                List<Species> defaultSpeciesList = searchResults.getSearchfilters().getSpecies();
                resetSelectedSpecies(defaultSpeciesList);

                for (String selectedItems : searchParameters.getSpecies()) {

                    for (Species theSpecies : defaultSpeciesList) {
                        if (selectedItems.equals(theSpecies.getScientificname())) {
                            theSpecies.setSelected(true);
                        }

                    }
                }

                List<Compound> defaultCompoundList = searchResults.getSearchfilters().getCompounds();
                resetSelectedCompounds(defaultCompoundList);

                for (String SelectedCompounds : searchParameters.getCompounds()) {
                    for (Compound theCompound : defaultCompoundList) {
                        if (SelectedCompounds.equals(theCompound.getName())) {
                            theCompound.setSelected(true);
                        }
                    }
                }

                List<uk.ac.ebi.ep.search.model.Disease> defaultDiseaseList = searchResults.getSearchfilters().getDiseases();
                resetSelectedDisease(defaultDiseaseList);

                for (String selectedDisease : searchParameters.getDiseases()) {
                    for (uk.ac.ebi.ep.search.model.Disease disease : defaultDiseaseList) {
                        if (selectedDisease.equals(disease.getName())) {
                            disease.setSelected(true);
                        }
                    }
                }



                // list to hold all selected species both from the specie list and auto-complete
                Set<String> allSelectedItems = new TreeSet<String>();

                //if an item is seleted, then filter the list
                if (!speciesFilter.isEmpty() || !compoundsFilter.isEmpty() || !diseasesFilter.isEmpty()) {
                    List<EnzymeSummary> filteredResults =
                            new LinkedList<EnzymeSummary>(resultSet.getSummaryentries());


                    CollectionUtils.filter(filteredResults,
                            new SpeciesPredicate(speciesFilter));
                    CollectionUtils.filter(filteredResults,
                            new CompoundsPredicate(compoundsFilter));
                    CollectionUtils.filter(filteredResults,
                            new DiseasesPredicate(diseasesFilter));





                    allSelectedItems.addAll(compoundsFilter);


                    allSelectedItems.addAll(diseasesFilter);


                    allSelectedItems.addAll(speciesFilter);


                    CollectionUtils.filter(filteredResults, new DefaultPredicate(allSelectedItems));

//filtering ends here

                    // Create a new SearchResults, don't modify the one in session
                    SearchResults sr = new SearchResults();

                    // Update the number of results to paginate:
                    pagination.setNumberOfResults(filteredResults.size());

                    model.addAttribute("pagination", pagination);
                    sr.setSearchfilters(resultSet.getSearchfilters());
                    sr.setSummaryentries(filteredResults);
                    // show the total number of hits (w/o filtering):
                    sr.setTotalfound(resultSet.getTotalfound());
                    searchModelForm.setSearchresults(sr);
                } else {
                    // Show all of them:
                    searchModelForm.setSearchresults(resultSet);
                }

                model.addAttribute("searchModel", searchModelForm);
                model.addAttribute("pagination", pagination);

                addToHistory(session,
                        "searchparams.text=" + searchParameters.getText());
            } catch (Throwable e) {
                LOGGER.error("Failed search", e);
                view = "error";
            }
        }
        return view;
    }
    static final Comparator<Species> SORT_SPECIES = new Comparator<Species>() {

        public int compare(Species sp1, Species sp2) {
            if (sp1.getCommonname() == null && sp2.getCommonname() == null) {

                return sp1.getScientificname().compareTo(sp2.getScientificname());
            }
            int compare = sp1.getScientificname().compareTo(sp2.getScientificname());

            return ((compare == 0) ? sp1.getScientificname().compareTo(sp2.getScientificname()) : compare);

        }
    };

    private void resetSelectedSpecies(List<Species> speciesList) {
        for (Species sp : speciesList) {

            sp.setSelected(false);

        }
    }

    private void resetSelectedCompounds(List<Compound> compounds) {
        for (Compound compound : compounds) {
            compound.setSelected(false);
        }
    }

    private void resetSelectedDisease(List<uk.ac.ebi.ep.search.model.Disease> diseases) {
        for (uk.ac.ebi.ep.search.model.Disease disease : diseases) {
            disease.setSelected(false);
        }
    }

    @RequestMapping(value = "/underconstruction", method = RequestMethod.GET)
    public String getSearchResult(Model model) {
        return "underconstruction";
    }

    @RequestMapping(value = "/about", method = RequestMethod.GET)
    public String getAbout(Model model) {
        return "about";
    }

    private void addToHistory(HttpSession session, String s) {
        @SuppressWarnings("unchecked")
        List<String> history = (List<String>) session.getAttribute("history");
        if (history == null) {
            history = new ArrayList<String>();
            session.setAttribute("history", history);
        }
        if (history.isEmpty() || !history.get(history.size() - 1).equals(s)) {
            history.add(s);
        }
    }

    private void clearHistory(HttpSession session) {
        @SuppressWarnings("unchecked")
        List<String> history = (List<String>) session.getAttribute("history");
        if (history == null) {
            history = new ArrayList<String>();
            session.setAttribute("history", history);
        } else {
            history.clear();
        }
    }
}
