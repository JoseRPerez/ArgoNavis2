package com.joserp.argonavis;

import java.io.Serializable;

public class row extends BaseRow implements Serializable {
    final String Nombre, ID, Tipo, Clasif, Cat, Ref;

    public row(String ID, String Nombre, String Cat, String Ref, String Tipo, String Clasif){
        //Definicion para userlist, solo contiene nombre, id, dsoIN y listaSEQ
        this.ID = ID;
        this.Nombre = Nombre;
        this.Cat = Cat;
        this.Ref = Ref;
        this.Tipo = Tipo;
        this.Clasif = Clasif;
    }

    public String getNombre() {
        return Nombre;
    }

    public String getID() { return ID; }

    public String getTipo() { return Tipo; }

    public String getClasif() { return Clasif; }

    public String getCat() {return Cat;}

    public String getRef() {return Ref;}


}
