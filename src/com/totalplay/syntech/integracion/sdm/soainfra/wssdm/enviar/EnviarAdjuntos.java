package com.totalplay.syntech.integracion.sdm.soainfra.wssdm.enviar;

import com.totalplay.syntech.integracion.sdm.soainfra.wssdm.props.AppPropsBean;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import org.apache.log4j.Category;

/**
 *
 * @author dell
 */
public class EnviarAdjuntos {

    // Constantes de la clase
    private static final Category log = Category.getInstance(EnviarAdjuntos.class);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        AppPropsBean.getPropsVO().getBddClassDriver();
        log.info("Inicianlzando");
        
        /** ASIGNANDO EL CHARSET **/
        log.info("Asignando Charset UTF-8");
        String charSet = "UTF-8";
        log.info("Configurando charset: " + charSet);

        try {
            asignarCharSet(charSet);
        } catch (Exception ex) {
            log.error("No se pudo asignar el charset " + charSet);
            log.error(ex);
            log.error("El programa puede presentar caracteres extraños en la información en lugar de letras con acentos, tildes, etc.");
        }
        

        // Tenemos que recibir tres parametrosa
        if (args != null && args.length == 1) {
            String persid = args[0].trim();
            try {
                EnviarArchivoBean bean = new EnviarArchivoBean();
                bean.enviarAdjunto(persid);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            System.out.println("Parametros insuficientes !!");
            System.out.println("<persidTicket>");
        }

        log.info("Finalizando programa...");
        System.exit(0);

    }

    public static void asignarCharSet(String charset) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        log.info("   ::: Charset actual: " + Charset.defaultCharset().name());
        log.info("   ::: File encoding actual: " + System.getProperty("file.encoding"));
        log.info("   ::: Asignando el charset: " + charset);
        System.setProperty("file.encoding", charset);

        log.info("   ::: File encoding actual: " + System.getProperty("file.encoding"));

        Field fieldCharset = Charset.class.getDeclaredField("defaultCharset");
        fieldCharset.setAccessible(true);
        fieldCharset.set(null, null);
        log.info("   ::: Charset actual: " + Charset.defaultCharset().name());

    }

}
