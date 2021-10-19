package com.totalplay.syntech.integracion.sdm.soainfra.wssdm.utils;

/**
 *
 * @author jzavala
 */
public class SerialClaveException extends Exception{

    public SerialClaveException(Exception ex){
        super(ex);
    }

    public SerialClaveException(String mensaje){
        super(mensaje);
    }
}
