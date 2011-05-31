//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.05.30 at 05:46:20 PM BST 
//


package uk.ac.ebi.ep.search.result.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SearchFilter complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SearchFilter">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="diseases" type="{http://ebi.ac.uk/enzymeportal/enzymes}Disease" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="compounds" type="{http://ebi.ac.uk/enzymeportal/enzymes}Compound" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="species" type="{http://ebi.ac.uk/enzymeportal/enzymes}Species" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SearchFilter", propOrder = {
    "diseases",
    "compounds",
    "species"
})
public class SearchFilter {

    protected List<Disease> diseases;
    protected List<Compound> compounds;
    protected List<Species> species;

    /**
     * Gets the value of the diseases property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the diseases property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDiseases().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Disease }
     * 
     * 
     */
    public List<Disease> getDiseases() {
        if (diseases == null) {
            diseases = new ArrayList<Disease>();
        }
        return this.diseases;
    }

    /**
     * Gets the value of the compounds property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the compounds property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCompounds().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Compound }
     * 
     * 
     */
    public List<Compound> getCompounds() {
        if (compounds == null) {
            compounds = new ArrayList<Compound>();
        }
        return this.compounds;
    }

    /**
     * Gets the value of the species property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the species property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSpecies().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Species }
     * 
     * 
     */
    public List<Species> getSpecies() {
        if (species == null) {
            species = new ArrayList<Species>();
        }
        return this.species;
    }

}
