package com.joserp.argonavis;

public abstract class BaseRow {
    //Creamos este clase para poder compartir metodos entre distintos tipos de clases Row
    public abstract String getNombre();

    public abstract String getID();

    public abstract String getTipo();

    public abstract String getClasif();

    public abstract String getCat();

    public abstract String getRef();
    
}
