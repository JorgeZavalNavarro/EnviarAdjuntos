package com.totalplay.syntech.integracion.sdm.soainfra.wssdm.adjuntos;

import com.totalplay.syntech.integracion.sdm.soainfra.wssdm.connbdd.ConnectorBDDConsultasBean;
import com.totalplay.syntech.integracion.sdm.soainfra.wssdm.props.AppPropsBean;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Category;

/**
 * Para el ambiente de desarrollo se estanm guardando las imagenes es
 * 192.168.112.90 (servicedesk) Path:
 *
 * @author Jorge Zavala Navarro
 */
public class ConsultaAdjuntosBean {

    // Constantes de la clase
    private static final Category log = Category.getInstance(ConsultaAdjuntosBean.class);
    private static final String uNoLock = "  WITH (NOLOCK) ";

    public List<AdjuntoInfoVO> listAdjuntoVO(String persidTicket) throws ConsultaAdjuntosException {
        log.info("Consulta de los elementos adjuntos para el ticket persid: " + persidTicket);
        List<AdjuntoInfoVO> retorno = null;
        if (persidTicket != null && !persidTicket.isEmpty()) {

            // Inicializamos nuestro valor de retorno 
            retorno = new ArrayList<>();

            // Declaramos nuestro conector a la base de datos
            Connection conn = null;

            try {
                log.info("Conectando a la base de datos...");
                // Inicializamos la conexión a la base de datos
                conn = ConnectorBDDConsultasBean.getConectionServiceDesk();
                log.info("Conexión a la BDD correcta !!");

                String sqlValidarTicket
                        = "select count(*) from call_req " + uNoLock + " where persid = ?";
                PreparedStatement psValidarTicket = conn.prepareCall(sqlValidarTicket);
                psValidarTicket.setString(1, persidTicket);
                ResultSet rsValidarTicket = psValidarTicket.executeQuery();
                if (rsValidarTicket.next()) {
                    if (rsValidarTicket.getInt(1) == 0) {
                        throw new ConsultaAdjuntosException("No se encontró el ticket con ID Persistente: " + persidTicket);
                    }
                }

                // Creamos el query a ejecutar
                String sqlBuscarAjuntosPorTicket
                        = " select ticket.id as id_ticket, \n"
                        + "	   ticket.persid as persid_ticket, \n"
                        + "	   ticket.ref_num as folio_ticket,\n"
                        // + "        ticket.external_system_ticket as folio_salesforce,"
                        // Se cambia el nombre del campo del tickeyt externo 22/06/2021
                        // por el campo de zfolio_dbw_sf el cual se va a registrar el numero
                        // de ticket correspondiente a salesforce
                        + "        ticket.zfolio_dbw_sf as folio_salesforce, \n"
                        + "        adjunto.id as id_adjunto, \n"
                        + "	   adjunto.persid as persid_adjunto, \n"
                        + "	   adjunto.del as activo_adjunto,\n"
                        + "	   adjunto.orig_file_name as nombre_archivo_original,\n"
                        + "	   adjunto.attmnt_name as nombre_adjunto,\n"
                        + "	   adjunto.file_name as nombre_archivo_adjunto,\n"
                        + "	   adjunto.file_date as fecha_archivo_adjunto,\n"
                        + "	   adjunto.file_size as peso_archivo_adjunto,\n"
                        + "	   adjunto.file_type as tipo_archivo_adjunto,\n"
                        + "	   adjunto.zip_flag as archivo_comprimido,\n"
                        + "	   adjunto.rel_file_path as carpeta_adjunto,\n"
                        + "	   adjunto.status as estatus_adjunto,\n"
                        + "	   adjunto.description as descripcion_adjunto\n"
                        + "   from call_req as ticket " + uNoLock + ", \n"
                        + "        attmnt as adjunto " + uNoLock + ",\n"
                        + "        usp_lrel_attachments_requests as rel " + uNoLock + "\n"
                        + "  where rel.cr = ticket.persid  \n"
                        + "    and rel.attmnt = adjunto.id   \n"
                        + "    and ticket.persid = ?";
                log.info("Query a ejecutar...");
                log.info(sqlBuscarAjuntosPorTicket);

                // Ejecutamos el query
                PreparedStatement psBuscarAdjuntosPorTicket = conn.prepareCall(sqlBuscarAjuntosPorTicket);
                psBuscarAdjuntosPorTicket.setString(1, persidTicket);
                log.info("Ejecutando el query...");

                ResultSet rsBuscarAdjuntosPorTicket = psBuscarAdjuntosPorTicket.executeQuery();

                // Llear listado con los registros encontrados
                AdjuntoInfoVO adjuntoVO = null;
                while (rsBuscarAdjuntosPorTicket.next()) {

                    // Validamos la información del adjunto, si NO se creo al momento de crear
                    // el ticket para descartarlos
                    if (validarAdjuntoPorLogs(rsBuscarAdjuntosPorTicket.getInt("id_adjunto"),
                            rsBuscarAdjuntosPorTicket.getString("persid_ticket"),
                            rsBuscarAdjuntosPorTicket.getString("nombre_archivo_original"))) {
                        adjuntoVO = new AdjuntoInfoVO();
                        adjuntoVO.setActivoAdjunto(rsBuscarAdjuntosPorTicket.getInt("activo_adjunto"));
                        adjuntoVO.setArchivoComprimido(rsBuscarAdjuntosPorTicket.getInt("archivo_comprimido"));
                        adjuntoVO.setDescripcionAdjunto(rsBuscarAdjuntosPorTicket.getString("descripcion_adjunto"));
                        adjuntoVO.setEstatusAdjunto(rsBuscarAdjuntosPorTicket.getString("estatus_adjunto"));
                        adjuntoVO.setFechaArchivoAdjunto(rsBuscarAdjuntosPorTicket.getInt("fecha_archivo_adjunto"));
                        adjuntoVO.setFolioTicket(rsBuscarAdjuntosPorTicket.getString("folio_ticket"));
                        adjuntoVO.setIdAdjunto(rsBuscarAdjuntosPorTicket.getInt("id_adjunto"));
                        adjuntoVO.setIdTicket(rsBuscarAdjuntosPorTicket.getInt("id_ticket"));
                        adjuntoVO.setNombreAdjunto(rsBuscarAdjuntosPorTicket.getString("nombre_adjunto"));
                        adjuntoVO.setNombreArchivoAdjunto(rsBuscarAdjuntosPorTicket.getString("nombre_archivo_adjunto"));
                        adjuntoVO.setNombreArchivoOriginal(rsBuscarAdjuntosPorTicket.getString("nombre_archivo_original"));
                        adjuntoVO.setPersidAdjunto(rsBuscarAdjuntosPorTicket.getString("persid_adjunto"));
                        adjuntoVO.setPersidTicket(rsBuscarAdjuntosPorTicket.getString("persid_ticket"));
                        adjuntoVO.setPesoArchivoAdjunto(rsBuscarAdjuntosPorTicket.getInt("peso_archivo_adjunto"));
                        adjuntoVO.setTipoArchivoAdjunto(rsBuscarAdjuntosPorTicket.getString("tipo_archivo_adjunto"));
                        adjuntoVO.setCarpetaAdjunto(rsBuscarAdjuntosPorTicket.getString("carpeta_adjunto"));
                        adjuntoVO.setFolioSalesforce(rsBuscarAdjuntosPorTicket.getString("folio_salesforce"));
                        retorno.add(adjuntoVO);
                    }
                }

                if (retorno == null || retorno.size() == 0) {
                    log.warn("No se encontraron adjuntos disposnibles para enviar a SF.");
                    // throw new ConsultaAdjuntosException("No se encontraron adjuntos para el ticket que se proporcionó");
                }

            } catch (Exception ex) {
                log.error(ex.getMessage());
                throw new ConsultaAdjuntosException(ex);

            } catch (Throwable th) {
                log.error(th.getMessage());
                throw new ConsultaAdjuntosException(th);

            } finally {
                try {
                    conn.close();
                } catch (Exception ex) {
                    log.error(ex.getMessage(), ex);
                    throw new ConsultaAdjuntosException(ex);

                } catch (Throwable th) {
                    log.error(th.getMessage(), th);
                    throw new ConsultaAdjuntosException(th);

                }
            }
        } else {
            log.info("No se está recibiendo la información del ticket");
            throw new ConsultaAdjuntosException("No se está recibiendo la información del ticket (persid)");
        }
        return retorno;

    }

    /**
     * Valida que el adjunto
     *
     * @param idAdjunto
     * @param persIdTicket
     * @param nombreArchivo
     * @return
     * @throws ConsultaAdjuntosException
     */
    public Boolean validarAdjuntoPorLogs(Integer idAdjunto, String persIdTicket, String nombreArchivo) throws ConsultaAdjuntosException {
        Boolean retorno = null;

        log.info("   ::: Validar si este adjunto proviene de la creación de algun ticket en service desk");
        log.info("   ::: Validando para el adjunto............: " + idAdjunto);
        log.info("   ::: Validando pers Id Ticket.............: " + persIdTicket);
        log.info("   ::: Validando el nombre del archivo......: " + nombreArchivo);

        // Validamos la información
        if (idAdjunto != null && persIdTicket != null && !persIdTicket.isEmpty()
                && nombreArchivo != null && !nombreArchivo.isEmpty()) {

            // inicializamos nuestro elemento de retorno
            retorno = Boolean.FALSE;

            // Definir el conector a la base de datos
            Connection conn = null;

            try {

                log.info("   ::: Conectando a la base de datos...");
                conn = ConnectorBDDConsultasBean.getConectionServiceDesk();
                log.info("   ::: Conexión a la base de datos satisfactoria.");

                log.info("   ::: Recuparar Los logs correspondientes del ticket persId " + persIdTicket);
                String sqlLogsPorPersidTicket
                        = "select count(*) as cantidad\n"
                        + "  from act_log as logs \n"
                        + " where logs.analyst = " + AppPropsBean.getPropsVO().getAnalistaAdjuntoUuid() + "\n"
                        + "   and logs.description like '%Adjuntar documento%'\n"
                        + "   and logs.description like '%" + nombreArchivo + "%'\n"
                        + "   and logs.type = 'ATTACHTDOC'" + "\n"
                        + "   and logs.call_req_id = '" + persIdTicket + "'";
                log.info("SQL");
                log.info(sqlLogsPorPersidTicket);

                Statement stLogsPorPersidTicket = conn.createStatement();
                ResultSet rsLogsPorPersidTicket = stLogsPorPersidTicket.executeQuery(sqlLogsPorPersidTicket);
                if (rsLogsPorPersidTicket.next()) {
                    if (rsLogsPorPersidTicket.getInt("cantidad") == 0) {
                        // El adjunto no se creó al generar el ticket, por lo tanto regresamos True
                        // de que el adjunto puede proceder
                        log.info("   ::: El adjunto procede segun las politicas del logactivity");
                        retorno = Boolean.TRUE;
                    } else {
                        log.info("   ::: El adjunto se registró al crear el ticket. No aplica para este movimiento");
                        retorno = Boolean.FALSE;
                    }
                } else {
                    String error = "Error al intentar validar los adjuntos recibidos desde la creación del ticket desde SF";
                    log.error("   ::: " + error);
                    throw new ConsultaAdjuntosException(error);
                }
            } catch (Exception ex) {
                log.error(ex.getMessage());
                throw new ConsultaAdjuntosException(ex);

            } catch (Throwable th) {
                log.error(th.getMessage());
                throw new ConsultaAdjuntosException(th);

            } finally {
                try {
                    conn.close();
                } catch (Exception ex) {
                    log.error(ex.getMessage(), ex);
                    throw new ConsultaAdjuntosException(ex);

                } catch (Throwable th) {
                    log.error(th.getMessage(), th);
                    throw new ConsultaAdjuntosException(th);

                }
            }

        } else {
            String error = "No se esta recibiendo la información completa !!";
            log.error(error);
            throw new ConsultaAdjuntosException(error);
        }

        return retorno;

    }

    // 
    public ConsultaAdjuntosResultadoVO buscarInfoImagen(String persid, Integer id) throws ConsultaAdjuntosException {
        ConsultaAdjuntosResultadoVO retorno = null;
        if (id != null && persid != null && !persid.isEmpty()) {

            // Inicializamos el elemento de retorno
            retorno = new ConsultaAdjuntosResultadoVO();

            // Declaramos el coinector a la base de datos
            Connection conn = null;
            try {

                // Conectar a la base de datos
                conn = ConnectorBDDConsultasBean.getConectionServiceDesk();

                // Ejecutamos el query para buscar el adjunto
                String sqlBuscarAdjunto
                        = "  select top 1 id, persid, del, orig_file_name,\n"
                        + "         attmnt_name, file_name, file_date,    \n"
                        + "         file_size, file_size, file_type       \n"
                        + "    from attmnt  " + uNoLock + "               \n"
                        + "   where id = ?                                \n"
                        + "	and persid = ?                            \n"
                        + "   order by file_date desc                       ";
                PreparedStatement psBuscarAdjunto = conn.prepareCall(sqlBuscarAdjunto);
                psBuscarAdjunto.setInt(1, id);
                psBuscarAdjunto.setString(2, persid);
                ResultSet rsBuscarAdjunto = psBuscarAdjunto.executeQuery();
                if (rsBuscarAdjunto.next()) {

                    // Cargamos la información del documento
                    retorno.setAttmnt_name(rsBuscarAdjunto.getString("attmnt_name"));
                    retorno.setDel(rsBuscarAdjunto.getString("del"));
                    retorno.setFile_date(rsBuscarAdjunto.getInt("file_date"));
                    retorno.setFile_name(rsBuscarAdjunto.getString("file_name"));
                    retorno.setFile_size(rsBuscarAdjunto.getInt("file_size"));
                    retorno.setFile_type(rsBuscarAdjunto.getString("file_type"));
                    retorno.setId(rsBuscarAdjunto.getInt("id"));
                    retorno.setOrig_file_name(rsBuscarAdjunto.getString("orig_file_name"));
                    retorno.setPersid(rsBuscarAdjunto.getString("persid"));

                } else {
                    throw new ConsultaAdjuntosException("No se encontrol información del adjunto persid:"
                            + persid + ", id:" + id);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                throw new ConsultaAdjuntosException(ex);

            } catch (Throwable th) {
                th.printStackTrace();
                throw new ConsultaAdjuntosException(th);

            } finally {
                try {
                    // Liberar la conexión a la base de datos
                    conn.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    throw new ConsultaAdjuntosException(ex);
                } catch (Throwable th) {
                    th.printStackTrace();
                    throw new ConsultaAdjuntosException(th);
                }
            }

        } else {
            throw new ConsultaAdjuntosException("No se está recibiendo el id del adjunto a buscar !!");
        }
        return retorno;
    }

}
