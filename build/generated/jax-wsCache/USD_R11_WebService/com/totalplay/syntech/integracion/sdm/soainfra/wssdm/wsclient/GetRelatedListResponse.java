
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
 *         &lt;element name="getRelatedListReturn" type="{http://www.ca.com/UnicenterServicePlus/ServiceDesk}ListResult"/>
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
    "getRelatedListReturn"
})
@XmlRootElement(name = "getRelatedListResponse")
public class GetRelatedListResponse {

    @XmlElement(required = true)
    protected ListResult getRelatedListReturn;

    /**
     * Obtiene el valor de la propiedad getRelatedListReturn.
     * 
     * @return
     *     possible object is
     *     {@link ListResult }
     *     
     */
    public ListResult getGetRelatedListReturn() {
        return getRelatedListReturn;
    }

    /**
     * Define el valor de la propiedad getRelatedListReturn.
     * 
     * @param value
     *     allowed object is
     *     {@link ListResult }
     *     
     */
    public void setGetRelatedListReturn(ListResult value) {
        this.getRelatedListReturn = value;
    }

}
