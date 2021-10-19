
package com.totalplay.syntech.integracion.sdm.soainfra.wssdm.wsclient;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="getRelatedListValuesResult" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="numRowsFound" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "getRelatedListValuesResult",
    "numRowsFound"
})
@XmlRootElement(name = "getRelatedListValuesResponse")
public class GetRelatedListValuesResponse {

    @XmlElement(required = true)
    protected String getRelatedListValuesResult;
    protected int numRowsFound;

    /**
     * Obtiene el valor de la propiedad getRelatedListValuesResult.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGetRelatedListValuesResult() {
        return getRelatedListValuesResult;
    }

    /**
     * Define el valor de la propiedad getRelatedListValuesResult.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGetRelatedListValuesResult(String value) {
        this.getRelatedListValuesResult = value;
    }

    /**
     * Obtiene el valor de la propiedad numRowsFound.
     * 
     */
    public int getNumRowsFound() {
        return numRowsFound;
    }

    /**
     * Define el valor de la propiedad numRowsFound.
     * 
     */
    public void setNumRowsFound(int value) {
        this.numRowsFound = value;
    }

}
