package com.totalplay.syntech.integracion.sdm.soainfra.wssdm.adjuntos;

import java.util.List;

/**
 * 
 * @author Jorge Zavala Navarro
 */
public class AdjuntoInfoVO{
    
    // Propiedades de la clase
    private Integer idTicket = null;
    private String persidTicket = null;
    private String folioTicket = null;
    private String folioSalesforce = null;
    private Integer idAdjunto = null;
    private String persidAdjunto = null;
    private Integer activoAdjunto = null;
    private String nombreArchivoOriginal = null;
    private String nombreAdjunto = null;
    private String nombreArchivoAdjunto = null;
    private Integer fechaArchivoAdjunto = null;
    private Integer pesoArchivoAdjunto = null;
    private String tipoArchivoAdjunto = null;
    private Integer archivoComprimido = null;
    private String estatusAdjunto = null;
    private String descripcionAdjunto = null;
    private String carpetaAdjunto = null;

    // Métodos getters y setters

    public Integer getIdTicket() {
        return idTicket;
    }

    public void setIdTicket(Integer idTicket) {
        this.idTicket = idTicket;
    }

    public String getPersidTicket() {
        return persidTicket;
    }

    public void setPersidTicket(String persidTicket) {
        this.persidTicket = persidTicket;
    }

    public String getFolioTicket() {
        return folioTicket;
    }

    public void setFolioTicket(String folioTicket) {
        this.folioTicket = folioTicket;
    }

    public Integer getIdAdjunto() {
        return idAdjunto;
    }

    public void setIdAdjunto(Integer idAdjunto) {
        this.idAdjunto = idAdjunto;
    }

    public String getPersidAdjunto() {
        return persidAdjunto;
    }

    public void setPersidAdjunto(String persidAdjunto) {
        this.persidAdjunto = persidAdjunto;
    }

    public Integer getActivoAdjunto() {
        return activoAdjunto;
    }

    public void setActivoAdjunto(Integer activoAdjunto) {
        this.activoAdjunto = activoAdjunto;
    }

    public String getNombreArchivoOriginal() {
        return nombreArchivoOriginal;
    }

    public void setNombreArchivoOriginal(String nombreArchivoOriginal) {
        this.nombreArchivoOriginal = nombreArchivoOriginal;
    }

    public String getNombreAdjunto() {
        return nombreAdjunto;
    }

    public void setNombreAdjunto(String nombreAdjunto) {
        this.nombreAdjunto = nombreAdjunto;
    }

    public String getNombreArchivoAdjunto() {
        return nombreArchivoAdjunto;
    }

    public void setNombreArchivoAdjunto(String nombreArchivoAdjunto) {
        this.nombreArchivoAdjunto = nombreArchivoAdjunto;
    }

    public Integer getFechaArchivoAdjunto() {
        return fechaArchivoAdjunto;
    }

    public void setFechaArchivoAdjunto(Integer fechaArchivoAdjunto) {
        this.fechaArchivoAdjunto = fechaArchivoAdjunto;
    }

    public Integer getPesoArchivoAdjunto() {
        return pesoArchivoAdjunto;
    }

    public void setPesoArchivoAdjunto(Integer pesoArchivoAdjunto) {
        this.pesoArchivoAdjunto = pesoArchivoAdjunto;
    }

    public String getTipoArchivoAdjunto() {
        return tipoArchivoAdjunto;
    }

    public void setTipoArchivoAdjunto(String tipoArchivoAdjunto) {
        this.tipoArchivoAdjunto = tipoArchivoAdjunto;
    }

    public Integer getArchivoComprimido() {
        return archivoComprimido;
    }

    public void setArchivoComprimido(Integer archivoComprimido) {
        this.archivoComprimido = archivoComprimido;
    }

    public String getEstatusAdjunto() {
        return estatusAdjunto;
    }

    public void setEstatusAdjunto(String estatusAdjunto) {
        this.estatusAdjunto = estatusAdjunto;
    }

    public String getDescripcionAdjunto() {
        return descripcionAdjunto;
    }

    public void setDescripcionAdjunto(String descripcionAdjunto) {
        this.descripcionAdjunto = descripcionAdjunto;
    }

    public String getCarpetaAdjunto() {
        return carpetaAdjunto;
    }

    public void setCarpetaAdjunto(String carpetaAdjunto) {
        this.carpetaAdjunto = carpetaAdjunto;
    }

    public String getFolioSalesforce() {
        return folioSalesforce;
    }

    public void setFolioSalesforce(String folioSalesforce) {
        this.folioSalesforce = folioSalesforce;
    }
    
    
    public String json() {
        String retorno = null;
        StringBuilder sbuilder = new StringBuilder();
        sbuilder.append("{");
        sbuilder.append("\"idTicket\"").append(" : \"").append(idTicket).append("\",");
        sbuilder.append("\"persidTicket\"").append(" : \"").append(persidTicket).append("\",");
        sbuilder.append("\"folioTicket\"").append(" : \"").append(folioTicket).append("\",");
        sbuilder.append("\"folioSalesforce\"").append(" : \"").append(folioSalesforce).append("\",");
        sbuilder.append("\"idAdjunto\"").append(" : \"").append(idAdjunto).append("\",");
        sbuilder.append("\"persidAdjunto\"").append(" : \"").append(persidAdjunto).append("\",");
        sbuilder.append("\"activoAdjunto\"").append(" : \"").append(activoAdjunto).append("\",");
        sbuilder.append("\"nombreArchivoOriginal\"").append(" : \"").append(nombreArchivoOriginal).append("\",");
        sbuilder.append("\"nombreAdjunto\"").append(" : \"").append(nombreAdjunto).append("\",");
        sbuilder.append("\"nombreArchivoAdjunto\"").append(" : \"").append(nombreArchivoAdjunto).append("\",");
        sbuilder.append("\"fechaArchivoAdjunto\"").append(" : \"").append(fechaArchivoAdjunto).append("\",");
        sbuilder.append("\"pesoArchivoAdjunto\"").append(" : \"").append(pesoArchivoAdjunto).append("\",");
        sbuilder.append("\"tipoArchivoAdjunto\"").append(" : \"").append(tipoArchivoAdjunto).append("\",");
        sbuilder.append("\"archivoComprimido\"").append(" : \"").append(archivoComprimido).append("\",");
        sbuilder.append("\"estatusAdjunto\"").append(" : \"").append(estatusAdjunto).append("\",");
        sbuilder.append("\"carpetaAdjunto\"").append(" : \"").append(carpetaAdjunto).append("\",");
        sbuilder.append("\"descripcionAdjunto\"").append(" : \"").append(descripcionAdjunto).append("\"");
        sbuilder.append("}");
        retorno = sbuilder.toString();
        return retorno;
    }

    public static String json(List<AdjuntoInfoVO> list) {
        String retorno = null;
        StringBuilder sbuilder = new StringBuilder();
        if (list != null && list.size() > 0) {

            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    sbuilder.append("[").append(list.get(i).json());
                } else {
                    sbuilder.append(",").append(list.get(i).json());
                }
            }

            sbuilder.append("]");
        } else {
            sbuilder.append("sin informacion");
        }

        retorno = sbuilder.toString();
        return retorno;
    }
    

}
