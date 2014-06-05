/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.ep.data.domain;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author joseph
 */
@Entity
@Table(name = "UNIPROT_ENTRY")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "UniprotEntry.findAll", query = "SELECT u FROM UniprotEntry u"),
    @NamedQuery(name = "UniprotEntry.findByDbentryId", query = "SELECT u FROM UniprotEntry u WHERE u.dbentryId = :dbentryId"),
    //@NamedQuery(name = "UniprotEntry.findByAccession", query = "SELECT u FROM UniprotEntry u WHERE u.accession = :accession"),
    @NamedQuery(name = "UniprotEntry.findByName", query = "SELECT u FROM UniprotEntry u WHERE u.name = :name"),
    @NamedQuery(name = "UniprotEntry.findByTaxId", query = "SELECT u FROM UniprotEntry u WHERE u.taxId = :taxId"),
    @NamedQuery(name = "UniprotEntry.findByProteinName", query = "SELECT u FROM UniprotEntry u WHERE u.proteinName = :proteinName"),
    @NamedQuery(name = "UniprotEntry.findByScientificName", query = "SELECT u FROM UniprotEntry u WHERE u.scientificName = :scientificName"),
    @NamedQuery(name = "UniprotEntry.findByCommonName", query = "SELECT u FROM UniprotEntry u WHERE u.commonName = :commonName"),
    @NamedQuery(name = "UniprotEntry.findBySynonymName", query = "SELECT u FROM UniprotEntry u WHERE u.synonymName = :synonymName")})
public class UniprotEntry implements Serializable {
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "accession", fetch = FetchType.EAGER)
    private Set<EnzymeSummary> enzymeSummarySet;
    @OneToMany(mappedBy = "uniprotAccession", fetch = FetchType.EAGER)
    private Set<Reaction> reactionSet;
    @OneToMany(mappedBy = "uniprotAccession", fetch = FetchType.EAGER)
    private Set<EnzymePortalCompound> enzymePortalCompoundSet;
    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @Column(name = "DBENTRY_ID")
    private long dbentryId;
    @Id
    @Basic(optional = false)
    @Column(name = "ACCESSION")
    private String accession;
    @Column(name = "NAME")
    private String name;
    @Column(name = "TAX_ID")
    private Long taxId;
    @Column(name = "PROTEIN_NAME")
    private String proteinName;
    @Column(name = "SCIENTIFIC_NAME")
    private String scientificName;
    @Column(name = "COMMON_NAME")
    private String commonName;
    @Column(name = "SYNONYM_NAME")
    private String synonymName;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "accession", fetch = FetchType.EAGER)
    private Set<Disease> diseaseSet;

    public UniprotEntry() {
    }

    public UniprotEntry(String accession) {
        this.accession = accession;
    }

    public UniprotEntry(String accession, long dbentryId) {
        this.accession = accession;
        this.dbentryId = dbentryId;
    }

    public long getDbentryId() {
        return dbentryId;
    }

    public void setDbentryId(long dbentryId) {
        this.dbentryId = dbentryId;
    }

    public String getAccession() {
        return accession;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTaxId() {
        return taxId;
    }

    public void setTaxId(Long taxId) {
        this.taxId = taxId;
    }

    public String getProteinName() {
        return proteinName;
    }

    public void setProteinName(String proteinName) {
        this.proteinName = proteinName;
    }

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getSynonymName() {
        return synonymName;
    }

    public void setSynonymName(String synonymName) {
        this.synonymName = synonymName;
    }

    @XmlTransient
    public Set<Disease> getDiseaseSet() {
        return diseaseSet;
    }

    public void setDiseaseSet(Set<Disease> diseaseSet) {
        this.diseaseSet = diseaseSet;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (accession != null ? accession.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UniprotEntry)) {
            return false;
        }
        UniprotEntry other = (UniprotEntry) object;
        if ((this.accession == null && other.accession != null) || (this.accession != null && !this.accession.equals(other.accession))) {
            return false;
        }
        return true;
    }

//    @Override
//    public String toString() {
//        return "uk.ac.ebi.ep.data.domain.UniprotEntry[ accession=" + accession + " ]";
//    }
    @Override
    public String toString() {
        return "UniprotEntry{" + "accession=" + accession + ", name=" + name + ", proteinName=" + proteinName + ", scientificName=" + scientificName + ", commonName=" + commonName + ", synonymName=" + synonymName + '}';
    }
 
    

    @XmlTransient
    public Set<EnzymePortalCompound> getEnzymePortalCompoundSet() {
        return enzymePortalCompoundSet;
    }

    public void setEnzymePortalCompoundSet(Set<EnzymePortalCompound> enzymePortalCompoundSet) {
        this.enzymePortalCompoundSet = enzymePortalCompoundSet;
    }

    @XmlTransient
    public Set<EnzymeSummary> getEnzymeSummarySet() {
        return enzymeSummarySet;
    }

    public void setEnzymeSummarySet(Set<EnzymeSummary> enzymeSummarySet) {
        this.enzymeSummarySet = enzymeSummarySet;
    }

    @XmlTransient
    public Set<Reaction> getReactionSet() {
        return reactionSet;
    }

    public void setReactionSet(Set<Reaction> reactionSet) {
        this.reactionSet = reactionSet;
    }
    
}