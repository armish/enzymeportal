package uk.ac.ebi.ep.core;

import uk.ac.ebi.ep.core.search.CommonSpecies;
import uk.ac.ebi.ep.search.model.Species;

/**
 * Wrapper around a Species object which provides the missing
 * <code>equals</code> and <code>hashCode</code> methods (the class
 * <code>Species</code> is auto-generated from a XML schema and does not
 * provide them).
 * <br>
 * It also implements <code>Comparable</code> to provide a 'natural' order for
 * Species objects.
 * <br>
 * All the implemented methods are based exclusively on the species scientific
 * name.
 * @author rafa
 *
 */
public final class SpeciesDefaultWrapper implements Comparable<SpeciesDefaultWrapper> {

 
    private final Species species;

    public final Species getSpecies() {
        return species;
    }

    public SpeciesDefaultWrapper(Species species) {
        if (species == null) {
            throw new NullPointerException();
        }
        this.species = species;
    }


    public int compareTo(SpeciesDefaultWrapper other) {

        if (species.getCommonname() == null & other.species.getCommonname() == null) {
            return species.getScientificname().compareTo(other.species.getScientificname());
        }
        if (species.getCommonname() != null & other.species.getCommonname() == null) {
            return species.getCommonname().compareTo(other.species.getScientificname());
        }
        if (species.getCommonname() == null & other.species.getCommonname() != null) {
            return species.getScientificname().compareTo(other.species.getCommonname());
        }
        //to avoid empty string in the list ( for auto complete filtering)
//        if(species.getCommonname().equals("") & other.species.getCommonname().equals("")){
//            return species.getScientificname().compareTo(other.species.getScientificname());
//        }
        if(species.getCommonname() != null & species.getScientificname().split("\\(")[0].trim().equalsIgnoreCase(CommonSpecies.Baker_Yeast.getScientificName()) && other.species.getCommonname() != null & other.species.getScientificname().split("\\(")[0].trim().equalsIgnoreCase(CommonSpecies.Baker_Yeast.getScientificName())){
           return species.getScientificname().compareTo(other.species.getScientificname());
        }
        return species.getCommonname().compareTo(other.species.getCommonname());
        //return species.getScientificname().compareTo(other.species.getScientificname());
        

    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SpeciesDefaultWrapper other = (SpeciesDefaultWrapper) obj;
        if (this.species != other.species && (this.species == null || !this.species.equals(other.species))) {
            return false;
        }
        if (this.species.getCommonname() != other.species.getCommonname() && (this.species.getCommonname() == null || !this.species.getCommonname().equals(other.species.getCommonname()))) {
            return false;
        }
        if (this.species.getScientificname() != other.species.getScientificname() && (this.species.getScientificname() == null || !this.species.getScientificname().equals(other.species.getScientificname()))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + (this.species.getScientificname() != null ? this.species.getScientificname().hashCode() : 0);
        return hash;
    }
}
