/*
 * Este es el APIRest que va a ejecutar servicedesk
 */
package com.totalplay.syntech.integracion.sdm.soainfra.wssdm.enviar;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author dell
 */
@Path("/enviarArchivo")
public class EnviarArchivoApiREST_IF {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of EnviarArchivoApiREST_IF
     */
    public EnviarArchivoApiREST_IF() {
    }

    /**
     * Retrieves representation of an instance of
     * com.totalplay.syntech.integracion.sdm.soainfra.wssdm.enviar.EnviarArchivoApiREST_IF
     * Método o funcionalidas para enviar el archivo correspondiente
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_XHTML_XML)
    public String getXml(
            @QueryParam("persid") String persid
    ) {
        //TODO return proper representation object
        String retorno = null;
        try {
            
            EnviarArchivoBean bean = new EnviarArchivoBean();
            retorno = bean.enviarAdjunto(persid);
        } catch (Exception ex) {
            retorno = ex.getMessage();
        }
        
        retorno = "<retorno>" + retorno + "</retorno>";

        return retorno;
    }

    /**
     * PUT method for updating or creating an instance of
     * EnviarArchivoApiREST_IF
     *
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }
}
