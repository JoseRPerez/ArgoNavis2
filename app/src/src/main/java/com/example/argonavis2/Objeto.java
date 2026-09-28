package com.joserp.argonavis;

public class Objeto {

    String Catalogo, Referencia, Nombre, Tipo, Clasificacion, ToView, Visto, ID;

    public Objeto(String Catalogo, String Nombre, String Tipo, String ToView, String Visto, String ID){
        this.Catalogo = Catalogo;
        //this.Referencia = Referencia;
        this.Nombre = Nombre;
        this.Tipo = Tipo;
        //this.Clasificacion = Clasificacion;
        this.ToView = ToView;
        this.Visto = Visto;
        this.ID = ID;
    }

    public String getCatalogo() {
        return Catalogo;
    }

    /*public String getReferencia() {
        return Referencia;
    }*/

    public String getNombre() {
        return Nombre;
    }

    public String getTipo() {
        return Tipo;
    }

    /*public String getClasificacion() {
        return Clasificacion;
    }*/

    public String getToView() {
        return ToView;
    }

    public String getVisto() {
        return Visto;
    }

    public String getID() {
        return ID;
    }

}
