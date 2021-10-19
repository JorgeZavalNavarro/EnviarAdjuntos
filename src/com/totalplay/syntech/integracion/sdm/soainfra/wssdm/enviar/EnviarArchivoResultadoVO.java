package com.totalplay.syntech.integracion.sdm.soainfra.wssdm.enviar;

import com.totalplay.syntech.integracion.sdm.soainfra.wssdm.core.CoreResultadoVO;

/**
 * 
 * @author Jorge Zavala Navarro
 */
public class EnviarArchivoResultadoVO extends CoreResultadoVO{
    
    // Propiedades de la clase
    private String resultado = null;

    // Métodos getters y setters
    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }
    
    

}
