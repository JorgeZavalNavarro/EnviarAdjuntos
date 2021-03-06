
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
 *         &lt;element name="rateDocumentReturn" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "rateDocumentReturn"
})
@XmlRootElement(name = "rateDocumentResponse")
public class RateDocumentResponse {

    @XmlElement(required = true)
    protected String rateDocumentReturn;

    /**
     * Obtiene el valor de la propiedad rateDocumentReturn.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRateDocumentReturn() {
        return rateDocumentReturn;
    }

    /**
     * Define el valor de la propiedad rateDocumentReturn.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRateDocumentReturn(String value) {
        this.rateDocumentReturn = value;
    }

}
