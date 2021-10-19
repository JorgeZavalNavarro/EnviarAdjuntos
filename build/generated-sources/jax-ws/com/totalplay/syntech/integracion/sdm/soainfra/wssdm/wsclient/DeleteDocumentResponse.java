
package com.totalplay.syntech.integracion.sdm.soainfra.wssdm.wsclient;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="deleteDocumentReturn" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "deleteDocumentReturn"
})
@XmlRootElement(name = "deleteDocumentResponse")
public class DeleteDocumentResponse {

    protected int deleteDocumentReturn;

    /**
     * Obtiene el valor de la propiedad deleteDocumentReturn.
     * 
     */
    public int getDeleteDocumentReturn() {
        return deleteDocumentReturn;
    }

    /**
     * Define el valor de la propiedad deleteDocumentReturn.
     * 
     */
    public void setDeleteDocumentReturn(int value) {
        this.deleteDocumentReturn = value;
    }

}