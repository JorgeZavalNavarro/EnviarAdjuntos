package com.totalplay.syntech.integracion.sdm.soainfra.wssdm.soainfra.client;

import com.totalplay.syntech.integracion.sdm.soainfra.wssdm.props.AppPropsBean;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import org.apache.log4j.Category;

/**
 *
 * @author Jorge Zavala Navarro
 */
public class ConsumoSoaInfraAPIRestClient {
    
    Category log = Category.getInstance(ConsumoSoaInfraAPIRestClient.class);

    public String callWS(String NoTicket, String NombreArchivo, String Archivo, String ContentType) throws ConsumoSoaInfraAPIRestException {
        String retorno = null;
        try {

            String urlWS = AppPropsBean.getPropsVO().getUrlServicioSestino();
            
            String params
                    = "{\"NoTicket\": \"" + NoTicket + "\","
                    + "\"NombreArchivo\": \"" + NombreArchivo + "\","
                    + "\"Archivo\": \"" + Archivo + "\","
                    + "\"ContentType\": \"" + ContentType + "\"}";
            String[] details = {};
            log.info("Solicitando envio a soa de....");
            log.info("NoTicket: " + NoTicket);
            log.info("NombreArchivo: " + NombreArchivo);
            log.info("Archivo (long): " + Archivo.length());
            log.info("ContentType: " + ContentType);
            
            log.info("Invocando el servicio: " + urlWS);
            log.info("Información a Postear: " + params);
            

            log.info(Arrays.toString(details));

            URL line_api_url = new URL(urlWS);
            String payload = params;
            // log.info("JSON a enviar....");
            // log.info(payload);

            HttpURLConnection linec = (HttpURLConnection) line_api_url.openConnection();
            // linec.setDoInput(true);
            linec.setDoOutput(true);
            linec.setRequestMethod("POST");
            linec.setRequestProperty("Content-Type", "application/json");
            // linec.setRequestProperty("Authorization", "Bearer 1djCb/mXV+KtryMxr6i1bXw");
            // log.info(payload);
            // OutputStreamWriter writer = new OutputStreamWriter(linec.getOutputStream(), "UTF-8");
            OutputStream os = linec.getOutputStream();
            os.write(payload.getBytes());
            os.flush();
            BufferedReader in = new BufferedReader(new InputStreamReader(linec.getInputStream()));
            String inputLine;
            
            retorno = "";

            while ((inputLine = in.readLine()) != null) {
                log.info(inputLine);
                retorno = retorno + inputLine;
            }
            in.close();
            linec.disconnect();
            
            // retorno = "Se ejecutó el servicio web !!";

        } catch (Exception e) {
            log.info("Exception in NetClientGet:- " + e);
            throw new ConsumoSoaInfraAPIRestException(e);
            
        }
        return retorno;
    }

    public static void main(String... params) {

        // Probamos el web serevice
        try{
        ConsumoSoaInfraAPIRestClient client = new ConsumoSoaInfraAPIRestClient();
        client.callWS("100", "archivo 100", "985420T9VB58TV2B9485TB", "PNG");
        }catch(Exception ex){
            ex.printStackTrace();
        }

    }

}
