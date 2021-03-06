
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
 *         &lt;element name="sid" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="assetHandle" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="contactHandle" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="logText" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "sid",
    "assetHandle",
    "contactHandle",
    "logText"
})
@XmlRootElement(name = "addAssetLog")
public class AddAssetLog {

    protected int sid;
    @XmlElement(required = true)
    protected String assetHandle;
    @XmlElement(required = true)
    protected String contactHandle;
    @XmlElement(required = true)
    protected String logText;

    /**
     * Obtiene el valor de la propiedad sid.
     * 
     */
    public int getSid() {
        return sid;
    }

    /**
     * Define el valor de la propiedad sid.
     * 
     */
    public void setSid(int value) {
        this.sid = value;
    }

    /**
     * Obtiene el valor de la propiedad assetHandle.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAssetHandle() {
        return assetHandle;
    }

    /**
     * Define el valor de la propiedad assetHandle.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAssetHandle(String value) {
        this.assetHandle = value;
    }

    /**
     * Obtiene el valor de la propiedad contactHandle.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContactHandle() {
        return contactHandle;
    }

    /**
     * Define el valor de la propiedad contactHandle.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContactHandle(String value) {
        this.contactHandle = value;
    }

    /**
     * Obtiene el valor de la propiedad logText.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLogText() {
        return logText;
    }

    /**
     * Define el valor de la propiedad logText.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLogText(String value) {
        this.logText = value;
    }

}
