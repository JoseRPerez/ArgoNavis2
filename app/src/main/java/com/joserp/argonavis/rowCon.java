package com.joserp.argonavis;

public class rowCon {
    final String Nombre;
    final int  Score;
    Boolean visible;
    final String ID;

    public rowCon(String ID, String Nombre, int Score, Boolean visible){
        //Definicion para userlist, solo contiene nombre, id, dsoIN y listaSEQ
        this.ID = ID;
        this.Nombre = Nombre;
        this.Score = Score;
        this.visible = visible;
    }

    public String getNombre() {
        return Nombre;
    }

    public String getScore() {
        return String.valueOf(Score) + "%" ;
    }
    public Boolean getVisible() {
        return visible;
    }


    public String getID() {
        return ID;
    }

    public void setVisible(Boolean state){
        visible = state;
    }

}
