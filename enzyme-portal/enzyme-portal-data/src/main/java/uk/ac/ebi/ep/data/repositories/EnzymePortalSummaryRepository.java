/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.ep.data.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.ep.data.domain.EnzymePortalSummary;

/**
 *
 * @author joseph
 */
public interface EnzymePortalSummaryRepository extends JpaRepository<EnzymePortalSummary, Long>, QueryDslPredicateExecutor<EnzymePortalSummary>, EnzymePortalSummaryRepositoryCustom {

    @Query(value = "SELECT * FROM ENZYME_PORTAL_SUMMARY WHERE COMMENT_TYPE='REGULATION' ", nativeQuery = true)
    List<EnzymePortalSummary> findEnzymePortalSummaries();
    
     @Transactional(readOnly = true)
    @Query(value = "SELECT * FROM ENZYME_PORTAL_SUMMARY WHERE COMMENT_TEXT IS NOT NULL AND COMMENT_TYPE = :COMMENT_TYPE ", nativeQuery = true)
     List<EnzymePortalSummary> findSummariesByCommentType(@Param("COMMENT_TYPE") String commentType);

}
