package uk.ac.ebi.ep.pdbeadapter;

import java.util.ArrayList;
import java.util.List;
import uk.ac.ebi.ep.pdbeadapter.molecule.Molecule;

/**
 *
 * @author joseph
 */
public abstract  class PdbEntity {

    private String label;
    private List<Molecule> molecules;


    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<Molecule> getMolecules() {
        if (molecules == null) {
            molecules = new ArrayList<>();
        }

        return molecules;
    }

    public void setMolecules(List<Molecule> molecules) {
        this.molecules = molecules;
    }

    @Override
    public String toString() {
        return "PdbEntity{" + "label=" + label + ", molecules=" + molecules + '}';
    }


}
