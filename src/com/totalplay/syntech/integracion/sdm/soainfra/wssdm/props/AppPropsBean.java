package com.totalplay.syntech.integracion.sdm.soainfra.wssdm.props;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import com.totalplay.syntech.integracion.sdm.soainfra.wssdm.keys.ApplicationKeys;
import org.apache.log4j.Category;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author Jorge Zavala Navarro
 */
public class AppPropsBean {

    // Propiedades de la clase
    private static AppPropsVO propsVO = null;
    private static Properties props = new Properties();
    
    static final Category log = Category.getInstance(AppPropsBean.class);

    static {
        

        log.info("Cargando propiedades de la clase...");

        // Cargar propiedades desde el archivo de configuración
        try {
            
            // Inicializamos las propiedades correspondientes
            cargarProps();
            
            // Inicializamos los logs
            iniciarLogs();
        
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private static void iniciarLogs() throws FileNotFoundException, IOException {
        log.info("Iniciar los archivos de los logs...");
        Properties logProperties = new Properties();
        logProperties.load(new FileInputStream(AppPropsBean.getPropsVO().getPathConfigLogs()));
        PropertyConfigurator.configure(logProperties);
        log.info("Logs inicializados satisfactoriamente !!");
    }
    
    private static void cargarProps() throws FileNotFoundException, IOException{
            InputStream is = AppPropsBean.class.getResourceAsStream(ApplicationKeys.ARCHIVO_PROPIEDADES_WEB);
            if (is == null) {
                is = new FileInputStream(ApplicationKeys.ARCHIVO_PROPIEDADES_WEB);
            }
            props.load(is);

            // Inicializar la clase con las propiedades
            propsVO = new AppPropsVO();

            // Propiedades principales de la aplicación
            propsVO.setAmbienteEjecucion(props.getProperty(AppPropsKeys.AMBIENTE_EJECUCION));
                    
            propsVO.setUrlServicedeskWs(props.getProperty(propsVO.getAmbienteEjecucion() + "." + AppPropsKeys.URL_SERVICEDESK_WS));
            propsVO.setRutaCarpetaAdjuntos(props.getProperty(propsVO.getAmbienteEjecucion() + "." + AppPropsKeys.RUTA_CARPETA_ADJUNTOS));
            propsVO.setServidorStoreAdjuntos(props.getProperty(propsVO.getAmbienteEjecucion() + "." + AppPropsKeys.RUTA_CARPETA_ADJUNTOS));
            propsVO.setUrlServicioSestino(props.getProperty(propsVO.getAmbienteEjecucion() + "." + AppPropsKeys.URL_SERVICIO_DESTINO));

            propsVO.setBddClassDriver(props.getProperty(propsVO.getAmbienteEjecucion() + "." + AppPropsKeys.BDD_CLASS_DRIVER));
            propsVO.setBddUrlFabricante(props.getProperty(propsVO.getAmbienteEjecucion() + "." + AppPropsKeys.BDD_URL_FABRICANTE));
            propsVO.setBddConexionServidor(props.getProperty(propsVO.getAmbienteEjecucion() + "." + AppPropsKeys.BDD_CONEXION_SERVIDOR));
            propsVO.setBddConexionPuerto(props.getProperty(propsVO.getAmbienteEjecucion() + "." + AppPropsKeys.BDD_CONEXION_PUERTO));
            propsVO.setBddConexionBasedatos(props.getProperty(propsVO.getAmbienteEjecucion() + "." + AppPropsKeys.BDD_CONEXION_BASEDATOS));
            propsVO.setBddConexionUsuario(props.getProperty(propsVO.getAmbienteEjecucion() + "." + AppPropsKeys.BDD_CONEXION_USUARIO));
            propsVO.setBddConexionPassword(props.getProperty(propsVO.getAmbienteEjecucion() + "." + AppPropsKeys.BDD_CONEXION_PASSWORD));
            propsVO.setQueryTimeoutSecs(props.getProperty(propsVO.getAmbienteEjecucion() + "." + AppPropsKeys.QUERY_TIMEOUT_SECS));
            propsVO.setWssdTimeoutConect(props.getProperty(propsVO.getAmbienteEjecucion() + "." + AppPropsKeys.WSSD_TIMEOUT_CONECT));
            propsVO.setWssdTimeoutRead(props.getProperty(propsVO.getAmbienteEjecucion() + "." + AppPropsKeys.WSSD_TIMEOUT_READ));
            propsVO.setRutaArchivosSeguimiento(props.getProperty(propsVO.getAmbienteEjecucion() + "." + AppPropsKeys.RUTA_ARCHIVOS_SEGUIMIENTO));
            
            propsVO.setWssdCredencialUsuario(props.getProperty(propsVO.getAmbienteEjecucion() + "." + AppPropsKeys.WSSD_CREDENCIAL_USUARIO));
            propsVO.setWssdCredencialPassword(props.getProperty(propsVO.getAmbienteEjecucion() + "." + AppPropsKeys.WSSD_CREDENCIAL_PASSWORD));
            
            propsVO.setComandoPdmCacheRefreshAttachment(props.getProperty(propsVO.getAmbienteEjecucion() + "." + AppPropsKeys.COMANDO_PDM_CACHE_REFRESH_ATTACHMENT));
            propsVO.setAdjuntoMarcaReplicadoSalesforce(props.getProperty(propsVO.getAmbienteEjecucion() + "." + AppPropsKeys.ADJUNTO_MARCA_REPLICADO_SALESFORCE));
            propsVO.setPathConfigLogs(props.getProperty(propsVO.getAmbienteEjecucion() + "." + AppPropsKeys.PATH_CONFIG_LOGS));
            propsVO.setAnalistaAdjuntoUuid(props.getProperty(propsVO.getAmbienteEjecucion() + "." + AppPropsKeys.ANALISTA_ADJUNTO_UUID));
    
    }

    public static AppPropsVO getPropsVO() {
        return propsVO;
    }

    public static void setPropsVO(AppPropsVO propsVO) {
        AppPropsBean.propsVO = propsVO;
    }

}
