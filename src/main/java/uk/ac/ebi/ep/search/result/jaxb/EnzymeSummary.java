//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.06.28 at 04:36:10 PM CEST 
//


package uk.ac.ebi.ep.search.result.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EnzymeSummary complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EnzymeSummary">
 *   &lt;complexContent>
 *     &lt;extension base="{http://ebi.ac.uk/enzymeportal/enzymes}EnzymeAccession">
 *       &lt;sequence>
 *         &lt;element name="ec" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="function" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="synonym" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="pdbeaccession" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="uniprotid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="relatedspecies" type="{http://ebi.ac.uk/enzymeportal/enzymes}EnzymeAccession" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EnzymeSummary", propOrder = {
    "ec",
    "name",
    "function",
    "synonym",
    "pdbeaccession",
    "uniprotid",
    "relatedspecies"
})
public class EnzymeSummary
    extends EnzymeAccession
{

    @XmlElement(required = true)
    protected List<String> ec;
    @XmlElement(required = true)
    protected String name;
    protected String function;
    protected List<String> synonym;
    protected List<String> pdbeaccession;
    @XmlElement(required = true)
    protected String uniprotid;
    protected List<EnzymeAccession> relatedspecies;

    /**
     * Gets the value of the ec property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ec property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEc().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getEc() {
        if (ec == null) {
            ec = new ArrayList<String>();
        }
        return this.ec;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the function property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFunction() {
        return function;
    }

    /**
     * Sets the value of the function property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFunction(String value) {
        this.function = value;
    }

    /**
     * Gets the value of the synonym property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the synonym property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSynonym().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getSynonym() {
        if (synonym == null) {
            synonym = new ArrayList<String>();
        }
        return this.synonym;
    }

    /**
     * Gets the value of the pdbeaccession property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the pdbeaccession property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPdbeaccession().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getPdbeaccession() {
        if (pdbeaccession == null) {
            pdbeaccession = new ArrayList<String>();
        }
        return this.pdbeaccession;
    }

    /**
     * Gets the value of the uniprotid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUniprotid() {
        return uniprotid;
    }

    /**
     * Sets the value of the uniprotid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUniprotid(String value) {
        this.uniprotid = value;
    }

    /**
     * Gets the value of the relatedspecies property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the relatedspecies property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRelatedspecies().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EnzymeAccession }
     * 
     * 
     */
    public List<EnzymeAccession> getRelatedspecies() {
        if (relatedspecies == null) {
            relatedspecies = new ArrayList<EnzymeAccession>();
        }
        return this.relatedspecies;
    }

}
