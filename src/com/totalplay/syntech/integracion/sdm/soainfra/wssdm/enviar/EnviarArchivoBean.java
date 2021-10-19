package com.totalplay.syntech.integracion.sdm.soainfra.wssdm.enviar;

import com.totalplay.syntech.integracion.sdm.soainfra.wssdm.adjuntos.AdjuntoInfoVO;
import com.totalplay.syntech.integracion.sdm.soainfra.wssdm.adjuntos.ConsultaAdjuntosBean;
import com.totalplay.syntech.integracion.sdm.soainfra.wssdm.base64.ConversorBase64Bean;
import com.totalplay.syntech.integracion.sdm.soainfra.wssdm.connbdd.ConnectorBDDConsultasBean;
import com.totalplay.syntech.integracion.sdm.soainfra.wssdm.descomprimir.gz.DescomprimireGZBean;
import com.totalplay.syntech.integracion.sdm.soainfra.wssdm.keys.ApplicationKeys;
import com.totalplay.syntech.integracion.sdm.soainfra.wssdm.marcar.MarcarReplicaSalesforceBean;
import com.totalplay.syntech.integracion.sdm.soainfra.wssdm.props.AppPropsBean;
import com.totalplay.syntech.integracion.sdm.soainfra.wssdm.soainfra.client.ConsumoSoaInfraAPIRestClient;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.List;
import org.apache.log4j.Category;

/**
 *
 * @author Jorge Zavala Navarro
 */
public class EnviarArchivoBean {

    private static Category log = Category.getInstance(EnviarArchivoBean.class);

    // Propiedades de la clase
    // private static Hashtable<String, String> ht = null;
    public EnviarArchivoBean() {
        log.info("Base: " + AppPropsBean.getPropsVO().getBddClassDriver());
        /**
         * if (ht == null) { log.info("Inicializando normalización de los tipos
         * de archivo..."); ht = new Hashtable<>(); normalizarTipoArchivo(); }*
         */
    }

    public String enviarAdjunto(String persidTicket) throws EnviarArchivoException {
        String retorno = null;
        Boolean descomprimir = Boolean.FALSE;
        Boolean saltarLogException = Boolean.FALSE;

        log.info("Inicio del proceso - Enviar Adjunto a SF. Ultima Mof(18/Octubre/2021)...");

        // Validamos nuestros parámetros de entrada
        if (persidTicket != null && !persidTicket.isEmpty()) {
            log.info("ID Persistent del ticket: " + persidTicket);
            log.info("Inicio del proceso...");
            try {
                // Primero obtenemos la información del archivo adjunto
                log.info("PRIMERO ::: Obtener la información de los archivos adjuntos del ticket con persid: " + persidTicket);
                ConsultaAdjuntosBean consultaAdjuntoBean = new ConsultaAdjuntosBean();
                List<AdjuntoInfoVO> listInfoVO = consultaAdjuntoBean.listAdjuntoVO(persidTicket);

                // Validamos si no se encontraron registros
                if (listInfoVO == null || listInfoVO.size() == 0) {
                    // No se procesa nada
                    String warning = "No se encontrarón ajuntos a enviar para el ticket:" + persidTicket + ". No procede el envio de adjuntos.";
                    log.warn(warning);
                    saltarLogException = Boolean.TRUE;
                    throw new EnviarArchivoException(warning);

                }
                log.info("        ::: Se encontraron: " + listInfoVO.size() + " adjuntos para este ticket para enviar a Salesforce");

                // Segundo paso extraemos el inputStream ya descomprimido
                log.info("SEGUNDO ::: " + "Validar registro por registro");

                // Variable para el bucle
                String nombreArchivoRepositorio = null;
                InputStream is = null;
                DescomprimireGZBean descomprimirGzBean = new DescomprimireGZBean();
                ConversorBase64Bean base64Bean = new ConversorBase64Bean();
                MarcarReplicaSalesforceBean marcarBean = null;
                String cadBase64 = null;
                String ContentType = null;
                String numeroTicket = null;
                String numeroTicketSalesforce = null;
                ConsumoSoaInfraAPIRestClient client = null;
                String retCallWS = null;
                // Iniciar escaneo
                for (int i = 0; i < listInfoVO.size(); i++) {

                    // Resetear algunas variables
                    descomprimir = Boolean.FALSE;

                    log.info("        ::: Analizando registro: " + i);
                    log.info("        ::: ID desde el repositorio: " + listInfoVO.get(i).getIdAdjunto());
                    log.info("        ::: Validamos las propiedades de este adjunto ");
                    if (listInfoVO.get(i).getActivoAdjunto().intValue() == 0) {
                        log.info("        ::: Adjunto disponible, bandera delete: " + listInfoVO.get(i).getActivoAdjunto().intValue());

                        // Validamos si el ARCHIVO se encuentra comprimido
                        if (listInfoVO.get(i).getArchivoComprimido().intValue() == 1) {
                            descomprimir = Boolean.TRUE;
                            log.info("        ::: Archivo comprimido, es necesario descomprimir.");
                        }

                        // Validamos el estatus del adjunto
                        if (listInfoVO.get(i).getEstatusAdjunto().equals("INSTALLED")) {
                            // El contenido del archivo se encuentra en el repositorio
                            log.info("        ::: Archivo adjunto existente en el repopsitorio.");

                            log.info("        ::: Validar si la imagen ya se ha replicado en service desk");
                            log.info("        ::: " + listInfoVO.get(i).getDescripcionAdjunto());
                            if (listInfoVO.get(i).getDescripcionAdjunto() == null
                                    || !listInfoVO.get(i).getDescripcionAdjunto().contains(AppPropsBean.getPropsVO().getAdjuntoMarcaReplicadoSalesforce())) {

                                // El archivo no se encuentra en salesforce, por lo tanto lo marcamos para apartar el archivo y commiteamos el movimiento
                                log.info("        ::: " + "Marcamos la imagen como replicada en service desk, para evitar duplicidad.");
                                log.info("        ::: " + "En caso de alduna falla se reflejara en el Log de actividades.");
                                log.info("        ::: " + "Marcando adjunto: " + listInfoVO.get(i).getIdAdjunto());
                                marcarBean = new MarcarReplicaSalesforceBean();
                                marcarBean.marcarReplica(listInfoVO.get(i).getIdAdjunto());
                                log.info("        ::: " + "Imagen marcada correctamente !!");

                                // Determinamos el nombre del archivo a leer desde el repopsitorio
                                nombreArchivoRepositorio = AppPropsBean.getPropsVO().getRutaCarpetaAdjuntos() + "\\" + listInfoVO.get(i).getCarpetaAdjunto() + "\\" + listInfoVO.get(i).getNombreArchivoAdjunto();
                                log.info("        ::: Trabajamos con el archivo:" + nombreArchivoRepositorio);

                                // Cargamos el contenido a base64 y se desmprime en caso de requerirlo
                                if (descomprimir) {
                                    log.info("        ::: Descomprimir el archivo");
                                    is = descomprimirGzBean.descomprimirTOInputStream(nombreArchivoRepositorio, listInfoVO.get(i).getTipoArchivoAdjunto());
                                } else {
                                    is = new FileInputStream(nombreArchivoRepositorio);
                                }

                                // Convertimos el contenido a base 64
                                log.info("        ::: Obtener el contenido del archivo en base 64");
                                cadBase64 = base64Bean.convertirInputStreamTOBase64(is);
                                log.info("        ::: Longitud: " + cadBase64.length());

                                // Obtenemos el tipo de archivo
                                // ContentType = ht.getOrDefault(listInfoVO.get(i).getTipoArchivoAdjunto(), ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML);
                                
                                ContentType = listInfoVO.get(i).getTipoArchivoAdjunto();
                                log.info("        ::: " + "Obtenemos el tipo de archivo: " + ContentType);
                                numeroTicket = listInfoVO.get(i).getFolioTicket();
                                numeroTicketSalesforce = listInfoVO.get(i).getFolioSalesforce();

                                if (numeroTicketSalesforce == null || numeroTicketSalesforce.isEmpty()) {
                                    // El ticket no tiene asignado el número de ticket de salesforce. Es necesario validar la información
                                    String error = "El ticket " + listInfoVO.get(i).getFolioTicket() + " no cuenta con un numero de ticket de SalesForce.";
                                    log.error(error);
                                    throw new EnviarArchivoException(error);

                                }

                                log.info("        ::: " + "Obtenemos el numero de ticket (ref_num): " + numeroTicket);
                                log.info("        ::: " + "Obtenemos el numero de ticket de Salesforce: " + numeroTicketSalesforce);

                                // Mandamos llam,ar el ws de totalplay
                                log.info("        ::: " + "Invocamos el WS de TPE...");
                                client = new ConsumoSoaInfraAPIRestClient();
                                retCallWS = client.callWS(numeroTicketSalesforce, listInfoVO.get(i).getNombreArchivoOriginal(), cadBase64, ContentType);

                                log.info("   :: Resultado obtenido: " + retCallWS);
                                log.info("   :: Validar si contiene algun error el web service");
                                if (retCallWS != null && retCallWS.toLowerCase().contains("error")) {
                                    // Se produjo un error en el consumo del web service de tpe

                                    log.error("   :: Se encontró error al consumir el ws: " + AppPropsBean.getPropsVO().getUrlServicioSestino());

                                    log.error("   :: Marcar error en el adjunto, indicando que se tiene que revisar el log de actividades");
                                    MarcarReplicaSalesforceBean marcarBeanError = new MarcarReplicaSalesforceBean();
                                    marcarBeanError.marcarReplicaError(listInfoVO.get(i).getIdAdjunto());
                                    log.error("   :: El error se marco en el arjunto: " + listInfoVO.get(i).getIdAdjunto() + " Satisfactoriamente !!");

                                    log.error("   :: Agregar este error en el logActivitie de SDM...");

                                    log.error("   :: Obtener el id de sesión...");
                                    int sid = this.login(AppPropsBean.getPropsVO().getWssdCredencialUsuario(), AppPropsBean.getPropsVO().getWssdCredencialPassword());
                                    log.error("   :: ID Sesión: " + sid);
                                    String creator = this.getHandleForUserid(sid, AppPropsBean.getPropsVO().getWssdCredencialUsuario());
                                    String error = "Error en el consumo del servicio " + AppPropsBean.getPropsVO().getUrlServicioSestino() + " se ejecuto correctamente pero se recibió error del mismo servicio \n"
                                            + "Resultado: " + retCallWS + "\n"
                                            + "Recibió los parametros: " + "Numero de Ticket: " + numeroTicketSalesforce
                                            + ", Nombre del archivo original:" + listInfoVO.get(i).getNombreArchivoOriginal()
                                            + ", Longitud del contenido Base64: " + cadBase64.length()
                                            + ", Tipo de contenido: " + ContentType;
                                    log.error("   ::: Mensaje a enviar: " + error);
                                    String retCreateLog = createActivityLog(sid, creator, persidTicket, error, "LOG", 0, Boolean.FALSE);
                                    log.error("   ::: Resultado del logActivity: " + retCreateLog);
                                    log.error("   ::: Cerrar la sesión: " + sid);
                                    this.logout(sid);
                                    log.error("   ::: Fin de proceso");

                                } else {

                                    /**
                                     * Se cambiar el marcado de la replica en
                                     * sales force antes de enviar el archivo
                                     * para dejarlo apartado y evitar
                                     * duplicidades, por lo tanto el siguiente
                                     * código se ejecuta mas arriba
                                     */
//                                    log.info("        ::: " + "Marcamos la imagen como replicada en service desk");
//                                    marcarBean = new MarcarReplicaSalesforceBean();
//                                    marcarBean.marcarReplica(listInfoVO.get(i).getIdAdjunto());
//                                    log.info("        ::: " + "Imagen marcada correctamente !!");
                                    log.info("        ::: " + "Agregamos este resultado a log activity!!");
                                    String mensaje = "Exitoso en el consumo del servicio " + AppPropsBean.getPropsVO().getUrlServicioSestino() + " se ejecuto correctamente !! \n"
                                            + "Resultado: " + retCallWS + "\n"
                                            + "Adjunto Marcado: " + listInfoVO.get(i).getIdAdjunto();

                                    log.info("        ::: " + "Obtenemos el id de sesión");
                                    int sid = this.login(AppPropsBean.getPropsVO().getWssdCredencialUsuario(), AppPropsBean.getPropsVO().getWssdCredencialPassword());
                                    log.info("   :: ID Sesión: " + sid);
                                    String creator = this.getHandleForUserid(sid, AppPropsBean.getPropsVO().getWssdCredencialUsuario());

                                    String retCreateLog = createActivityLog(sid, creator, persidTicket, mensaje, "LOG", 0, Boolean.FALSE);
                                    log.info("   ::: Resultado del logActivity: " + retCreateLog);
                                    log.info("   ::: Cerrar la sesión: " + sid);
                                    this.logout(sid);
                                    log.info("   ::: Fin de proceso");

                                }
                            } else {
                                log.info("        ::: ADJUNTO ESTÁ MARCADO COMO YA REPLICADO EN SALESFORCE. NO SE ENVIA");
                            }

                        } else {
                            log.info("        ::: ADJUNTO NO DISPONIBLE, EL ESTATUS DEL ARCHIVO ES: " + listInfoVO.get(i).getEstatusAdjunto());
                        }

                    } else {
                        log.info("        ::: ADJUNTO NO DISPONIBLE, BANDERA DELETE: " + listInfoVO.get(i).getActivoAdjunto().intValue());
                    }
                }

                log.info("   ::: Fin de proceso");

                // Notificamos en el logActivity del ticket
            } catch (Exception ex) {
                if (!saltarLogException) {
                    try {
                        log.error("   ::: Se produjo un error en el proceso !!");
                        log.error("   ::: Error:" + ex.getMessage());
                        log.error("   ::: Notificamos en el log activity del ticket.-..");

                        // Notificamos en los log de activity del ticket
                        log.error("   ::: Obtener el ID de sesión...");
                        int sid = this.login(AppPropsBean.getPropsVO().getWssdCredencialUsuario(), AppPropsBean.getPropsVO().getWssdCredencialPassword());
                        log.error("SID: " + sid);
                        String error
                                = "Se produjo un error al intentar consumir el servicio web: " + AppPropsBean.getPropsVO().getUrlServicioSestino() + "\n"
                                + "Mensaje de error: " + ex.getMessage();
                        // 
                        String creator = this.getHandleForUserid(sid, AppPropsBean.getPropsVO().getWssdCredencialUsuario());
                        String retCreateLog = createActivityLog(sid, creator, persidTicket, error, "LOG", 0, Boolean.FALSE);
                        log.info("   ::: Resultado del logActivity: " + retCreateLog);

                        log.error("   ::: Cerrando la sesión: " + sid);
                        this.logout(sid);
                        log.error("   ::: Fin de proceso.");
                    } catch (Exception ex2) {
                        ex2.printStackTrace();
                        log.error(ex2.getMessage(), ex2);
                    } catch (Throwable ex2) {
                        ex2.printStackTrace();
                        log.error(ex2.getMessage(), ex2);
                    }
                }

                ex.printStackTrace();
                throw new EnviarArchivoException(ex);
            }
        } else {
            throw new EnviarArchivoException("No se están recibiendo los par{ametros");
        }
        return retorno;
    }

    private void notificarLogActivity(String persidTicket, String mensaje, String usuario, String password) throws MalformedURLException {
        // Preparamos

        log.info("   ::: Generando log activity");
        log.info("   ::: PersID : " + persidTicket);
        log.info("   ::: Mensaje : " + mensaje);

        log.info("   ::: Obtenemos el sid del usuario: " + usuario);
        int sid = this.login(usuario, password);
        log.info("   ::: sid:" + sid);

        // Obtenemos el creator en base al usuario correspondiente
        log.info("   ::: Obtener el handleCreator del : " + usuario);
        String creator = this.getHandleForUserid(sid, usuario);
        log.info("   ::: HandleCreator : " + creator);

        log.info("   ::: Creando entrada al activitylog");
        String retActivityLog = this.createActivityLog(sid, creator, persidTicket, mensaje, "LOG", 0, Boolean.FALSE);
        log.info("   ::: ActivityLog respondió: " + retActivityLog);

    }

    /**
     * private void normalizarTipoArchivo() { ht.put("fileType",
     * ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML); ht.put("PNG",
     * ApplicationKeys.TIPO_ARCHIVO_IMAGE_JPEG); ht.put("png",
     * ApplicationKeys.TIPO_ARCHIVO_IMAGE_JPEG); ht.put("jpg",
     * ApplicationKeys.TIPO_ARCHIVO_IMAGE_JPEG); ht.put("jpeg",
     * ApplicationKeys.TIPO_ARCHIVO_IMAGE_JPEG); ht.put("xlsx",
     * ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML); ht.put("docx",
     * ApplicationKeys.TIPO_ARCHIVO_APPLICATION_PDF); ht.put("txt",
     * ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML); ht.put("msg",
     * ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML); ht.put("pdf",
     * ApplicationKeys.TIPO_ARCHIVO_APPLICATION_PDF); ht.put("xls",
     * ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML); ht.put("eml",
     * ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML); ht.put("zip",
     * ApplicationKeys.TIPO_ARCHIVO_APPLICATION_PDF); ht.put("pptx",
     * ApplicationKeys.TIPO_ARCHIVO_APPLICATION_PDF); ht.put("jfif",
     * ApplicationKeys.TIPO_ARCHIVO_IMAGE_JPEG); ht.put("DOC",
     * ApplicationKeys.TIPO_ARCHIVO_APPLICATION_PDF); ht.put("NULL",
     * ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML); ht.put("csv",
     * ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML); ht.put("mp4",
     * ApplicationKeys.TIPO_ARCHIVO_IMAGE_JPEG); ht.put("rar",
     * ApplicationKeys.TIPO_ARCHIVO_APPLICATION_PDF); ht.put("odt",
     * ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML); ht.put("GIF",
     * ApplicationKeys.TIPO_ARCHIVO_IMAGE_JPEG); ht.put("log",
     * ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML); ht.put("htm",
     * ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML); ht.put("xlsm",
     * ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML); ht.put("pcapng",
     * ApplicationKeys.TIPO_ARCHIVO_IMAGE_JPEG); ht.put("ods",
     * ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML); ht.put("bmp",
     * ApplicationKeys.TIPO_ARCHIVO_IMAGE_JPEG); ht.put("kmz",
     * ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML); ht.put("xps",
     * ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML); ht.put("ppt",
     * ApplicationKeys.TIPO_ARCHIVO_APPLICATION_PDF); ht.put("pcap",
     * ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML); ht.put("vsd",
     * ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML); ht.put("html",
     * ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML); ht.put("mp3",
     * ApplicationKeys.TIPO_ARCHIVO_IMAGE_JPEG); ht.put("har",
     * ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML); ht.put("local",
     * ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML); ht.put("rtf",
     * ApplicationKeys.TIPO_ARCHIVO_APPLICATION_PDF); ht.put("COM",
     * ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML); ht.put("K",
     * ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML); ht.put("ptmf",
     * ApplicationKeys.TIPO_ARCHIVO_APPLICATION_PDF); ht.put("xml",
     * ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML); ht.put("odp",
     * ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML); ht.put("wav",
     * ApplicationKeys.TIPO_ARCHIVO_IMAGE_JPEG); ht.put("dblg",
     * ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML); ht.put("gz",
     * ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML); ht.put("SOR",
     * ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML); ht.put("conf",
     * ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML); ht.put("dat",
     * ApplicationKeys.TIPO_ARCHIVO_APPLICATION_PDF); ht.put("ogg",
     * ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML); ht.put("dot",
     * ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML); ht.put("lic",
     * ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML); ht.put("odg",
     * ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML); ht.put("cfg",
     * ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML); ht.put("tgz",
     * ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML); ht.put("xlsb",
     * ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML); ht.put("oxps",
     * ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML); ht.put("MHT",
     * ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML); ht.put("sh",
     * ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML); ht.put("xlt",
     * ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML); ht.put("mpeg",
     * ApplicationKeys.TIPO_ARCHIVO_IMAGE_JPEG); ht.put("m4a",
     * ApplicationKeys.TIPO_ARCHIVO_IMAGE_JPEG); ht.put("ppsx",
     * ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML); ht.put("py",
     * ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML); ht.put("TMP",
     * ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML); ht.put("trc",
     * ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML); ht.put("m3u8",
     * ApplicationKeys.TIPO_ARCHIVO_IMAGE_JPEG); ht.put("dwg",
     * ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML); ht.put("deref",
     * ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML); ht.put("aac",
     * ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML); ht.put("cap",
     * ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML);
     *
     * // Video ht.put("cap", ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML);
     *
     * ht.put("pptm", ApplicationKeys.TIPO_ARCHIVO_TEXT_HTML); }
     *
     */
    private String createActivityLog(int sid, java.lang.String creator, java.lang.String objectHandle, java.lang.String description, java.lang.String logType, int timeSpent, boolean internal) throws MalformedURLException {
        URL url = new URL(AppPropsBean.getPropsVO().getUrlServicedeskWs());
        com.totalplay.syntech.integracion.sdm.soainfra.wssdm.wsclient.USDWebService service = new com.totalplay.syntech.integracion.sdm.soainfra.wssdm.wsclient.USDWebService(url);
        com.totalplay.syntech.integracion.sdm.soainfra.wssdm.wsclient.USDWebServiceSoap port = service.getUSDWebServiceSoap();
        return port.createActivityLog(sid, creator, objectHandle, description, logType, timeSpent, internal);
    }

    private int login(java.lang.String username, java.lang.String password) throws MalformedURLException {
        URL url = new URL(AppPropsBean.getPropsVO().getUrlServicedeskWs());
        com.totalplay.syntech.integracion.sdm.soainfra.wssdm.wsclient.USDWebService service = new com.totalplay.syntech.integracion.sdm.soainfra.wssdm.wsclient.USDWebService(url);
        com.totalplay.syntech.integracion.sdm.soainfra.wssdm.wsclient.USDWebServiceSoap port = service.getUSDWebServiceSoap();
        return port.login(username, password);
    }

    private void logout(int sid) throws MalformedURLException {
        URL url = new URL(AppPropsBean.getPropsVO().getUrlServicedeskWs());
        com.totalplay.syntech.integracion.sdm.soainfra.wssdm.wsclient.USDWebService service = new com.totalplay.syntech.integracion.sdm.soainfra.wssdm.wsclient.USDWebService(url);
        com.totalplay.syntech.integracion.sdm.soainfra.wssdm.wsclient.USDWebServiceSoap port = service.getUSDWebServiceSoap();
        port.logout(sid);
    }

    private String getHandleForUserid(int sid, java.lang.String userID) throws MalformedURLException {
        URL url = new URL(AppPropsBean.getPropsVO().getUrlServicedeskWs());
        com.totalplay.syntech.integracion.sdm.soainfra.wssdm.wsclient.USDWebService service = new com.totalplay.syntech.integracion.sdm.soainfra.wssdm.wsclient.USDWebService(url);
        com.totalplay.syntech.integracion.sdm.soainfra.wssdm.wsclient.USDWebServiceSoap port = service.getUSDWebServiceSoap();
        return port.getHandleForUserid(sid, userID);
    }

    public static void main(String... params) {
        try {
            String ticket = "cr:1470773";
            EnviarArchivoBean bean = new EnviarArchivoBean();
            bean.enviarAdjunto(ticket);
        } catch (Exception ex) {
            log.error("No se pudo procesar la petición.");
            log.error(ex.getLocalizedMessage());

            // 
        }
    }

    public static void main_(String... param) {

        AppPropsBean.getPropsVO().getBddClassDriver();
        log.info("procesando...");
        
        EnviarArchivoBean.ActualizarTablaThread actualizarTabla1 = new EnviarArchivoBean(). new ActualizarTablaThread("001");
        Thread threadActualizarTabla1 = new Thread(actualizarTabla1);

        EnviarArchivoBean.ActualizarTablaThread actualizarTabla2 = new EnviarArchivoBean(). new ActualizarTablaThread("002");
        Thread threadActualizarTabla2 = new Thread(actualizarTabla2);

        EnviarArchivoBean.ActualizarTablaThread actualizarTabla3 = new EnviarArchivoBean(). new ActualizarTablaThread("003");
        Thread threadActualizarTabla3 = new Thread(actualizarTabla3);
        


        threadActualizarTabla1.start();
        threadActualizarTabla2.start();
        threadActualizarTabla3.start();

    }

    public class ActualizarTablaThread implements Runnable {

        // Ejecutamos los movimientos correspondientes
        String attmntPersId = "attmnt:400587";
        String marcaComentario = "MARCA ABCDE";
        String sqlLimpiarComentario
                = "update attmnt "
                + "   set "
                + "       description = null"
                + " where persid = ?";
        String sqlAgregarComentario
                = "update attmnt "
                + "   set "
                + "       description = concat(description, ?) "
                + "where persid = ?"
                + "  and ( description is null "
                + "        or description not like '%" + marcaComentario + "%' )";
        String sqlLeerComentario
                = "select description "
                + "  from attmnt "
                + " where persid = ?";
        String idHilo = null;
        
        public ActualizarTablaThread(String idHilo){
            this.idHilo = idHilo;
        }

        @Override
        public void run() {
            
            limpiarComentario();

            log.info("   ::: " + this.idHilo + " --> Obtenemos el comentario actual...");
            String comentario = leerComentarioActual();
            log.info("   ::: " + this.idHilo + " --> Comentario actual: " + comentario);
            
            // Validar si esta marcado
            if(comentario !=null && comentario.contains(marcaComentario)){
                log.info("   ::: " + this.idHilo + " --> Registro marcado, por lo tanto terminamos");
            }else{
                log.info("   ::: " + this.idHilo + " --> No se detecta comentario, por lo tanto lo marcamos...");
                actualizarComentarioActual();
                log.info("   ::: " + this.idHilo + " --> Comentario actualizado");
            }
            
            // leer comentario final
            log.info("   ::: " + this.idHilo + " --> Obtenemos el comentario final...");
            comentario = leerComentarioActual();
            log.info("   ::: " + this.idHilo + " --> Comentario final actualizado: " + comentario);
            
            actualizarComentarioActual();

        }
        
        private String leerComentarioActual(){
            
            String retorno = null;
            
            Connection conn = null;
            try {

                log.info("   ::: " + this.idHilo + " --> Intentamos conectar a la base de datos...");
                conn = ConnectorBDDConsultasBean.getConectionServiceDesk();
                log.info("   ::: " + this.idHilo + " --> La conexión a la base de datos es correcta");
                
                // Consultamos la descripción 
                PreparedStatement psLeerComentario = conn.prepareCall(sqlLeerComentario);
                psLeerComentario.setString(1, this.attmntPersId);
                ResultSet rsLeerComentario = psLeerComentario.executeQuery();
                if(rsLeerComentario.next()){
                    log.info("   ::: " + this.idHilo + " --> Comentario actual: " + rsLeerComentario.getString("description"));
                    retorno = rsLeerComentario.getString("description");
                }

            } catch (Exception ex) {
                log.error(ex);
            } catch (Throwable ex) {
                log.error(ex);
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (Throwable ex) {
                        log.error(ex);
                    }
                }
            }  
            
            return retorno;
        }
        
        private String actualizarComentarioActual(){
            
            String retorno = null;
            
            Connection conn = null;
            try {

                log.info("   ::: " + this.idHilo + " --> Intentamos conectar a la base de datos...");
                conn = ConnectorBDDConsultasBean.getConectionServiceDesk();
                log.info("   ::: " + this.idHilo + " --> La conexión a la base de datos es correcta");
                
                // Consultamos la descripción 
                log.info("   ::: " + this.idHilo + " --> Actualizando el comentario del adjunto...");
                PreparedStatement psLeerComentario = conn.prepareCall(sqlAgregarComentario);
                psLeerComentario.setString(1, marcaComentario + "::" + idHilo);
                psLeerComentario.setString(2, attmntPersId);
                log.info("   ::: " + this.idHilo + " --> Query para actualizar....");
                log.info("   ::: " + this.idHilo + " --> " + sqlAgregarComentario);
                int regsActs = psLeerComentario.executeUpdate();
                log.info("   ::: " + this.idHilo + " --> Comentario actualizado: " + regsActs);
                conn.commit();
                log.info("   ::: " + this.idHilo + " --> Cambios aplicados correctamente ");
                

            } catch (Exception ex) {
                log.error(ex);
            } catch (Throwable ex) {
                log.error(ex);
            } finally {
                if (conn != null) {
                    try {
                        // Esperamos 5 segundos
                        // log.info("   ::: " + this.idHilo + " --> Esperamos 5 segundos antes de desconectar... ");
                        //  Thread.sleep(5000);
                        log.info("   ::: " + this.idHilo + " --> Cerrando conección a la base de datos");
                        conn.close();
                        log.info("   ::: " + this.idHilo + " --> Conexión cerrada.");
                    } catch (Throwable ex) {
                        log.error(ex);
                    }
                }
            }  
            
            return retorno;
        }
        
        
        private String limpiarComentario(){
            
            String retorno = null;
            
            Connection conn = null;
            try {

                log.info("   ::: " + this.idHilo + " --> Intentamos conectar a la base de datos...");
                conn = ConnectorBDDConsultasBean.getConectionServiceDesk();
                log.info("   ::: " + this.idHilo + " --> La conexión a la base de datos es correcta");
                
                // Consultamos la descripción 
                log.info("   ::: " + this.idHilo + " --> Limpiando el comentario adjunto.......");
                PreparedStatement psLeerComentario = conn.prepareCall(sqlLimpiarComentario);
                psLeerComentario.setString(1, attmntPersId);
                psLeerComentario.executeUpdate();
                log.info("   ::: " + this.idHilo + " --> Comentario actualizado ");
                conn.commit();
                log.info("   ::: " + this.idHilo + " --> Cambios aplicados correctamente ");
                

            } catch (Exception ex) {
                log.error(ex);
            } catch (Throwable ex) {
                log.error(ex);
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (Throwable ex) {
                        log.error(ex);
                    }
                }
            }  
            
            return retorno;
        }


    }

}
