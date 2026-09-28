package com.joserp.argonavis.Defs;

import com.joserp.argonavis.BaseRow;

import java.util.Objects;
import java.io.Serializable;
public class DSO extends BaseRow implements Serializable {
    //Al ser Serializable nos permite pasarlo de una actividad a otra
    private String catalogo, referencia, desig, cons, nombre, id, memo, info, size, mag;
    private String carta, tipo, clasif;
    private int rate;
    private double ar, dec;
    private boolean toView, visto;

    public DSO() {
        //esta clase no toma ningun parmetro pero si tiene metodos
        double ar = -9999;
        double dec = -9999;
    }


    public void setCat(String catalogo) {
        this.catalogo = catalogo;
    }

    public void setRef(String referencia) {
        this.referencia = referencia;
    }

    public void setDesig(String desig) {
        this.desig = desig;
    }

    public void setCons(String cons) {
        this.cons = cons;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setId(String id) {this.id = id;}

    public void setMemo(String memo) {this.memo = memo;}

    public void setInfo(String info) {this.info = info;}

    public void setSize(String size) {this.size = size;}

    public void setMag(String mag) {this.mag = mag;}

    public void setCarta(String carta) {this.carta = carta;}

    public void setTipo(String tipo) {this.tipo = tipo;}

    public void setClasif(String clasif) {this.clasif = clasif;}

    public void setRate(int rate) {this.rate = rate;}

    public void setToView(boolean toView) {this.toView = toView;}

    public void setVisto(boolean visto) {this.visto = visto;}

    public void setAR(double ar) {this.ar = ar;}

    public void setDEC(double dec) {this.dec = dec;}

    public double getAR() {return ar;}

    public double getDEC() {return dec;}

    // Metodo auxiliar para verificar si las coordenadas están establecidas
    public boolean hasCoordinates() {
        return ar != -9999.0 && dec != -9999.0;
    }


    public String getCat() {return catalogo;}

    public String getRef() {return referencia;}

    public String getDesig() {return desig;}

    public String getCons() {return cons;}

    public String getNombre() {return nombre;}

    public String getID() {return id;}

    public String getId() {return id;}

    public String getMemo() {return memo;}

    public String getInfo() {return info;}

    public String getSize() {return size;}

    public String getMag() {return mag;}

    public String getCarta() { return carta; }

    public String getTipo() {return tipo;}

    public String getClasif() {return clasif;}

    public int getRate() {return rate;}

    public String getCode() {return catalogo + referencia;}

    public String getAllCodes() {
        if (this.getDesig().isEmpty()) {
            return getCode();
        } else {
            return getCode() + " (" + getDesig() + ")";
        }
    }

    public boolean isToView() {return toView;}

    public boolean isVisto() {return visto;}

    public void setField(String campo, String newString) {
        switch (campo) {
            case "CARTA":
                setCarta(newString);
                break;
            case "MEMORY":
                setMemo(newString);
                break;
            case "NOTES":
                setInfo(newString);
                break;
        }
    }

    public String getClassification() {
        return Objects.toString(tipo, "") + " " + Objects.toString(clasif, "");
    }
}
