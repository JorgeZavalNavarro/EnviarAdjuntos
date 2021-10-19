package com.totalplay.syntech.integracion.sdm.soainfra.wssdm.adjuntos;

import com.totalplay.syntech.integracion.sdm.soainfra.wssdm.core.CoreResultadoVO;
import java.util.List;

/**
 *
 * @author Jorge Zavala Navarro
 */
public class ConsultaAdjuntosResultadoVO extends CoreResultadoVO {

    // Propiedades de la clase
    private Integer id = null;
    private String persid = null;
    private String del = null;
    private String orig_file_name = null;
    private String attmnt_name = null;
    private String file_name = null;
    private Integer file_date = null;
    private Integer file_size = null;
    private String file_type = null;

    // Métodfos getters y setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPersid() {
        return persid;
    }

    public void setPersid(String persid) {
        this.persid = persid;
    }

    public String getDel() {
        return del;
    }

    public void setDel(String del) {
        this.del = del;
    }

    public String getOrig_file_name() {
        return orig_file_name;
    }

    public void setOrig_file_name(String orig_file_name) {
        this.orig_file_name = orig_file_name;
    }

    public String getAttmnt_name() {
        return attmnt_name;
    }

    public void setAttmnt_name(String attmnt_name) {
        this.attmnt_name = attmnt_name;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public Integer getFile_date() {
        return file_date;
    }

    public void setFile_date(Integer file_date) {
        this.file_date = file_date;
    }

    public Integer getFile_size() {
        return file_size;
    }

    public void setFile_size(Integer file_size) {
        this.file_size = file_size;
    }

    public String getFile_type() {
        return file_type;
    }

    public void setFile_type(String file_type) {
        this.file_type = file_type;
    }

    public String json() {
        String retorno = null;
        StringBuilder sbuilder = new StringBuilder();
        sbuilder.append("{");
        sbuilder.append("\"id\"").append(" : \"").append(id).append("\",");
        sbuilder.append("\"persid\"").append(" : \"").append(persid).append("\",");
        sbuilder.append("\"del\"").append(" : \"").append(del).append("\",");
        sbuilder.append("\"orig_file_name\"").append(" : \"").append(orig_file_name).append("\",");
        sbuilder.append("\"attmnt_name\"").append(" : \"").append(attmnt_name).append("\",");
        sbuilder.append("\"file_name\"").append(" : \"").append(file_name).append("\",");
        sbuilder.append("\"file_date\"").append(" : \"").append(file_date).append("\",");
        sbuilder.append("\"file_size\"").append(" : \"").append(file_size).append("\",");
        sbuilder.append("\"file_type\"").append(" : \"").append(file_type).append("\"");
        sbuilder.append("}");
        retorno = sbuilder.toString();
        return retorno;
    }

    public static String json(List<ConsultaAdjuntosResultadoVO> list) {
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
