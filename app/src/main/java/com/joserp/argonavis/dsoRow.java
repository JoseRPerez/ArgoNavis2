package com.joserp.argonavis;

import java.io.Serializable;

public class dsoRow extends BaseRow implements Serializable  {
    //serizalizable permite pasar un objeto de un intent a otro

    String Catalogo, Nombre, ID, Tipo, Clasif;
    int rate;
    boolean ToView, Visto;



    public dsoRow(String Catalogo, String Nombre, String Tipo, String Clasif, boolean ToView, boolean Visto, int rate, String ID){
        this.Catalogo = Catalogo;
        this.Nombre = Nombre;
        this.Tipo = Tipo;
        this.Clasif = Clasif;
        this.ToView = ToView;
        this.Visto = Visto;
        this.rate = rate;
        this.ID = ID;
    }

    public String getCatalogo() {
        return Catalogo;
    }

    public int getrate() {
        return rate;
    }

    public String getClasif() { return Clasif; }

    public String getCat() {
        return "";
    }

    @Override
    public String getRef() {
        return "";
    }


    public String getNombre() {
        return Nombre;
    }

    public String getTipo() {
        return Tipo;
    }

    public boolean getToView() {
        return ToView;
    }

    public boolean getVisto() {
        return Visto;
    }

    public String getID() {
        return ID;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public void setVisto( boolean Visto) {
        this.Visto = Visto;
    }

    public void setToView( boolean toView) {
        this.ToView = toView;
    }

    public void setNombre( String nombre) {
        this.Nombre = nombre;
    }
}
