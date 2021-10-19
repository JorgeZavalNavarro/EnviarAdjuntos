package com.totalplay.syntech.integracion.sdm.soainfra.wssdm.marcar;

import com.totalplay.syntech.integracion.sdm.soainfra.wssdm.connbdd.ConnectorBDDConsultasBean;
import com.totalplay.syntech.integracion.sdm.soainfra.wssdm.keys.ApplicationKeys;
import com.totalplay.syntech.integracion.sdm.soainfra.wssdm.props.AppPropsBean;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import org.apache.log4j.Category;

/**
 *
 * @author Jorge Zavala Navarro
 */
public class MarcarReplicaSalesforceBean {

    // Propiedades de la clase
    private static final Category log = Category.getInstance(MarcarReplicaSalesforceBean.class);

    public void marcarReplica(Integer idAdjunto) throws MarcarReplicaSalesforceException {
        log.info("Marcando la replica en salesforce para la imagen: " + idAdjunto);
        if (idAdjunto != null) {
            Connection conn = null;

            try {
                log.info("Conectando a la base de datos");
                conn = ConnectorBDDConsultasBean.getConectionServiceDesk();
                log.info("Conexión a la base de datos satisfactoria");

                log.info("Marcar el adjunto....");
                String sqlMarcarImagen
                        = "update attmnt "
                        + "set description = concat(description, ?) "
                        + "where id = ?   "
                        + "  and ( description is null\n"
                        + "        or description not like '%" + AppPropsBean.getPropsVO().getAdjuntoMarcaReplicadoSalesforce() + "%')";
                PreparedStatement psMarcarImagen = conn.prepareCall(sqlMarcarImagen);
                psMarcarImagen.setString(1, " " + AppPropsBean.getPropsVO().getAdjuntoMarcaReplicadoSalesforce() + " ");
                psMarcarImagen.setInt(2, idAdjunto);
                int retUpdate = psMarcarImagen.executeUpdate();
                
                // Si retorna 0 es que ya esta apartado el registro y tenemos que sacar esxception
                if(retUpdate == 0){
                    String error = "Ya se está procesando para el adjunto id: " + idAdjunto + " ya que su registro se encuentra marcado con: " + AppPropsBean.getPropsVO().getAdjuntoMarcaReplicadoSalesforce();
                    log.error(error);
                    throw new MarcarReplicaSalesforceException(error);
                }

                // 
                log.info("Aplicando cambios");
                conn.commit();
                log.info("El adjunto se marcó satisfactoriamente !!");

                log.info("Ejecutar el comando para actualizar los comentarios...");
                this.ejecutarPDM();

            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
                throw new MarcarReplicaSalesforceException(ex);

            } catch (Throwable th) {
                log.error(th.getMessage(), th);
                throw new MarcarReplicaSalesforceException(th);

            } finally {
                try {
                    conn.close();
                } catch (Exception ex) {
                    log.error(ex.getMessage(), ex);
                    throw new MarcarReplicaSalesforceException(ex);

                } catch (Throwable th) {
                    log.error(th.getMessage(), th);
                    throw new MarcarReplicaSalesforceException(th);

                }
            }
        } else {
            throw new MarcarReplicaSalesforceException("No se está recibiendo el id de la imagen a marcar !!");
        }
    }

    /**
     * Método que va a marcare la replica con algun error
     *
     * @param idAdjunto
     * @throws MarcarReplicaSalesforceException
     */
    public void marcarReplicaError(Integer idAdjunto) throws MarcarReplicaSalesforceException {
        log.info("Marcando la replica en salesforce para la imagen: " + idAdjunto);
        if (idAdjunto != null) {
            Connection conn = null;

            try {
                log.info("Conectando a la base de datos");
                conn = ConnectorBDDConsultasBean.getConectionServiceDesk();
                log.info("Conexión a la base de datos satisfactoria");

                log.info("Marcar el adjunto con error de replica....");
                String sqlMarcarImagen
                        = "update attmnt "
                        + "set description = concat(description, ?) "
                        + "where id = ?";
                PreparedStatement psMarcarImagen = conn.prepareCall(sqlMarcarImagen);
                psMarcarImagen.setString(1, " ERROR DE REPLICADO (ver log de actividades del ticket) ");
                psMarcarImagen.setInt(2, idAdjunto);
                psMarcarImagen.executeUpdate();

                // 
                log.info("Aplicando cambios");
                conn.commit();
                log.info("El adjunto se marcó satisfactoriamente con el error de replicado!!");

                log.info("Ejecutar el comando para actualizar los comentarios...");
                this.ejecutarPDM();

            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
                throw new MarcarReplicaSalesforceException(ex);

            } catch (Throwable th) {
                log.error(th.getMessage(), th);
                throw new MarcarReplicaSalesforceException(th);

            } finally {
                try {
                    conn.close();
                } catch (Exception ex) {
                    log.error(ex.getMessage(), ex);
                    throw new MarcarReplicaSalesforceException(ex);

                } catch (Throwable th) {
                    log.error(th.getMessage(), th);
                    throw new MarcarReplicaSalesforceException(th);

                }
            }
        } else {
            throw new MarcarReplicaSalesforceException("No se está recibiendo el id de la imagen a marcar !!");
        }
    }

    /**
     * Ejecutar el programa para actualizar los registros de los adjuntos de SDM
     */
    public void ejecutarPDM() {

        // Obtener el nombre del comando a ejecutar
        String comandoEjecutar = null;
        log.info("   ::: Ejecución del comando para actualizar la información de los adjuntos...");

        try {

            comandoEjecutar = AppPropsBean.getPropsVO().getComandoPdmCacheRefreshAttachment();
            // comandoEjecutar = "cmd /c dir";
            log.info("   ::: Comando a ejecutar: " + comandoEjecutar);

            // Lanzar programa (se usa "cmd /c dir" para lanzar un comando del sistema operativo)
            log.info("   ::: Ejecutando...");
            Process p = Runtime.getRuntime().exec(comandoEjecutar);

            /**
             * Obtenemos la salida del comando *
             */
            log.info("   ::: Recibieno respuesta...");
            InputStream is = p.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            // Se lee la primera linea
            String aux = br.readLine();
            // log.info("     -->" + aux);

            // Mientras se haya leido alguna linea
            while (aux != null) {
                // Se escribe la linea en pantalla
                System.out.println(aux);
                log.info("     -->" + aux);

                // y se lee la siguiente.
                aux = br.readLine();
            }
            log.info("   ::: Ejecución terminada");

        } catch (Exception ex) {
            // Excepciones si hay algún problema al arrancar el ejecutable o al leer su salida.*/
            log.error("No se pudo ejecutar el programa: " + comandoEjecutar);
            log.error(ex.getLocalizedMessage());
            log.error(ex.getMessage());
            log.error("Trasa del error....", ex);
        }
    }

}
