package com.joserp.argonavis;

public class Memory {
    String date, text, id, dsoID;
    String place, telescope, eyepiece, dsoTag;

    public Memory(String id, String dsoID, String date, String text,
                  String place, String telescope, String eyepiece, String dsoTag) {
        this.id = id;
        this.dsoID = dsoID;
        this.date = date;
        this.text = text;
        this.place = place;
        this.telescope = telescope;
        this.eyepiece = eyepiece;
        this.dsoTag = dsoTag;
    }

    public String getID() {return id;}
    public String getDSOID() {return dsoID;}
    public String getDate() {return date;}
    public String getMemory() {return text;}
    public String getPlace() {return place;}
    public String getTelescope() {return telescope;}
    public String getEyepiece() {return eyepiece;}
    public String getDSOTag() {return dsoTag;}

    public void setDate(String date) {this.date = date;}
    public void setMemory(String text) { this.text = text;}

    public void setEyepiece(String eyepiece) {this.eyepiece = eyepiece;}

    public void setTelescope(String telescope) {this.telescope = telescope;}

    public void setPlace(String place) {this.place = place;}
    public void setDSOTag(String dsoTag) {this.dsoTag = dsoTag;}
}
