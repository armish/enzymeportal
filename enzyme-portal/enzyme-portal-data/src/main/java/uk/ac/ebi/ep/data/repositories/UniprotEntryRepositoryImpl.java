/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.ep.data.repositories;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.Projections;
import com.mysema.query.types.expr.StringExpression;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.ep.data.domain.QUniprotEntry;
import uk.ac.ebi.ep.data.domain.UniprotEntry;
import uk.ac.ebi.ep.data.search.model.EnzymeSummary;
import uk.ac.ebi.ep.data.search.model.Species;
import uk.ac.ebi.ep.data.search.model.Taxonomy;

/**
 *
 * @author joseph
 */
public class UniprotEntryRepositoryImpl implements UniprotEntryRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;
    private static final QUniprotEntry $ = QUniprotEntry.uniprotEntry;

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional(readOnly = true)
    @Override
    public List<UniprotEntry> findEnzymesByNamePrefixes(List<String> name_prefixes) {
        EntityGraph eGraph = entityManager.getEntityGraph("UniprotEntryEntityGraph");
        //EntityGraph eGraph =entityManager.createEntityGraph("UniprotEntryEntityGraph");

        eGraph.addAttributeNodes("enzymePortalPathwaysSet", "enzymePortalReactionSet",
                "enzymePortalSummarySet", "enzymePortalDiseaseSet", "enzymePortalCompoundSet",
                "uniprotXrefSet", "enzymePortalEcNumbersSet");

        JPAQuery query = new JPAQuery(entityManager);

        query.setHint("javax.persistence.fetchgraph", eGraph);
        query.setHint("javax.persistence.query.timeout", 450);

        //StringExpression idPrefix = $.name.substring(0, $.name.indexOf("_"));
        StringExpression idPrefix = $.relatedProteinsId.namePrefix;
        BooleanBuilder builder = new BooleanBuilder();
        name_prefixes.stream().forEach((prefix) -> {

            builder.or(idPrefix.equalsIgnoreCase(prefix));

        });
        query.from($).where(builder);

        return query.list($);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UniprotEntry> findEnzymesByAccessions(List<String> accessions) {

        EntityGraph eGraph = entityManager.getEntityGraph("UniprotEntryEntityGraph");

        eGraph.addAttributeNodes("enzymePortalPathwaysSet", "enzymePortalReactionSet",
                "enzymePortalSummarySet", "enzymePortalDiseaseSet", "enzymePortalCompoundSet",
                "uniprotXrefSet", "enzymePortalEcNumbersSet");
        JPAQuery query = new JPAQuery(entityManager);

        query.setHint("javax.persistence.fetchgraph", eGraph);

        BooleanBuilder builder = new BooleanBuilder();
        accessions.stream().forEach((accession) -> {

            builder.or($.accession.equalsIgnoreCase(accession));

        });
        query.from($).where(builder);
        return query.distinct().list($).stream().distinct().collect(Collectors.toList());
    }



    @Transactional(readOnly = true)
    @Override
    public List<UniprotEntry> findEnzymeByNamePrefix(String namePrefix) {

        EntityGraph eGraph = entityManager.getEntityGraph("UniprotEntryEntityGraph");

        eGraph.addAttributeNodes("enzymePortalPathwaysSet", "enzymePortalReactionSet",
                "enzymePortalSummarySet", "enzymePortalDiseaseSet", "enzymePortalCompoundSet",
                "uniprotXrefSet", "enzymePortalEcNumbersSet");
        JPAQuery query = new JPAQuery(entityManager);
        query.setHint("javax.persistence.fetchgraph", eGraph);

        //StringExpression idPrefix = $.name.substring(0, $.name.indexOf("_"));
         StringExpression idPrefix = $.relatedProteinsId.namePrefix;
        BooleanBuilder builder = new BooleanBuilder();

        builder.or(idPrefix.equalsIgnoreCase(namePrefix));

        query.from($).where(builder);
        return query.list($).stream().distinct().collect(Collectors.toList());

    }

    @Transactional(readOnly = true)
    @Override
    public List<String> filterEnzymesInAccessions(List<String> accessions) {

        JPAQuery query = new JPAQuery(entityManager);

        BooleanBuilder builder = new BooleanBuilder();
        accessions.parallelStream().forEach((accession) -> {

            builder.or($.accession.equalsIgnoreCase(accession));

        });
        List<String> enzymes = query.from($).where(builder).list($.accession).stream().distinct().collect(Collectors.toList());

        return enzymes;

    }


    @Override
    public List<UniprotEntry> findEnzymesByAccession(String accession) {
        EntityGraph eGraph = entityManager.getEntityGraph("UniprotEntryEntityGraph");

        eGraph.addAttributeNodes("enzymePortalPathwaysSet", "enzymePortalReactionSet",
                "enzymePortalSummarySet", "enzymePortalDiseaseSet", "enzymePortalCompoundSet",
                "uniprotXrefSet", "enzymePortalEcNumbersSet");
        JPAQuery query = new JPAQuery(entityManager);

        query.setHint("javax.persistence.fetchgraph", eGraph);
        List<UniprotEntry> enzymes = query.from($).where($.accession.equalsIgnoreCase(accession)).list($);
        return enzymes;

    }

    @Transactional(readOnly = true)
    @Override
    public List<Taxonomy> getCountForOrganisms(List<Long> taxids) {

        JPAQuery query = new JPAQuery(entityManager);
        // List<Tuple> tuple =    query.from($).groupBy($.taxId,$.scientificName,$.commonName).orderBy($.taxId.count().desc()).limit(11).list($.taxId,$.scientificName,$.commonName,$.taxId.count());

        List<Taxonomy> result = query.from($).where($.taxId.in(taxids)).groupBy($.taxId, $.scientificName, $.commonName).
                list(Projections.constructor(Taxonomy.class, $.taxId, $.scientificName, $.commonName, $.taxId.count()));

        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public List<String> findAccessionsByTaxId(Long taxId) {
        JPAQuery query = new JPAQuery(entityManager);
        List<String> result = query.from($).where($.taxId.eq(taxId)).distinct().list($.accession);
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public List<UniprotEntry> findEnzymesByTaxId(Long taxId) {
        JPAQuery query = new JPAQuery(entityManager);
        List<UniprotEntry> result = query.from($).where($.taxId.eq(taxId)).distinct().limit(10).list($);
        return result;

    }

    @Transactional(readOnly = true)
    @Override
    public Page<EnzymeSummary> findEnzymesByAccessions(List<String> accessions, Pageable pageable) {
        JPAQuery query = new JPAQuery(entityManager);
         //CollQueryFactory.
        List<EnzymeSummary> result = query.from($).where($.accession.in(accessions))
                .list(Projections.constructor(EnzymeSummary.class, $));

        return new PageImpl<>(result, pageable, result.size());

    }

    @Override
    @Transactional(readOnly = true)
    public List<Species> findSpeciesByTaxId(Long taxId) {
        JPAQuery query = new JPAQuery(entityManager);
        List<Species> result = query.from($).where($.taxId.eq(taxId)).distinct()
                .list(Projections.constructor(Species.class, $.scientificName, $.commonName, $.taxId));

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Species> findSpeciesByScientificName(String sName) {
        JPAQuery query = new JPAQuery(entityManager);
        List<Species> result = query.from($).where($.scientificName.equalsIgnoreCase(sName)).distinct()
                .list(Projections.constructor(Species.class, $.scientificName, $.commonName, $.taxId));

        return result;
    }

    @Override
    public List<UniprotEntry> findEnzymesByMeshId(String meshId) {
        JPAQuery query = new JPAQuery(entityManager);
        List<UniprotEntry> result = query.from($).where($.enzymePortalDiseaseSet.any().meshId.trim().equalsIgnoreCase(meshId)).list($);
        return result;
    }

    @Override
    public List<UniprotEntry> findEnzymesByPathwayId(String pathwayId) {
        JPAQuery query = new JPAQuery(entityManager);
         List<UniprotEntry> result = query.from($).where($.enzymePortalPathwaysSet.any().pathwayId.trim().equalsIgnoreCase(pathwayId)).list($);
        return result;
    }

    private <T >Set<T> toSet(T... s) {
        return new HashSet<>(Arrays.asList(s));
    }

}
