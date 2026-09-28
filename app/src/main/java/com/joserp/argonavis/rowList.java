package com.joserp.argonavis;

public class rowList {
    String Nombre;
    final String ID;
    final String dsoIn;
    Boolean IsSeq;

    public rowList(String ID, String Nombre, String dsoIn, Boolean IsSeq){
        //Definicion para userlist, solo contiene nombre, id, dsoIN y listaSEQ
        this.ID = ID;
        this.Nombre = Nombre;
        this.IsSeq = IsSeq;
        this.dsoIn = dsoIn;
    }

    public String getNombre() {

        return Nombre;
    }

    public String getID() {
        return ID; }

    public String getdsoIn() {
        return dsoIn; }

    public Boolean getSEQ() {
        return IsSeq; }


    public void setNombre (String NewName) {
        Nombre = NewName; }

    public void setSeq (boolean NewisSeq) {
        IsSeq = NewisSeq; }

}
