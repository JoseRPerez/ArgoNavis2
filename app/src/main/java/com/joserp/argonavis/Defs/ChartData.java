package com.joserp.argonavis.Defs;

// Clase auxiliar para almacenar los datos de cada carta

public class ChartData {
    private String arriba, Namme;
    private String izquierda;
    private String derecha;
    private String abajo;
    private double arMin;
    private double arMax;
    private double decMin;
    private double decMax;

    public ChartData(String Namme, String izquierda, String derecha, String arriba, String abajo,
                     double arMin, double arMax, double decMin, double decMax) {
        this.Namme = Namme;
        this.arriba = arriba;
        this.izquierda = izquierda;
        this.derecha = derecha;
        this.abajo = abajo;
        this.arMin = arMin;
        this.arMax = arMax;
        this.decMin = decMin;
        this.decMax = decMax;
    }

    // Getters
    public String getArriba() { return arriba; }
    public String getIzquierda() { return izquierda; }
    public String getDerecha() { return derecha; }
    public String getNamme() {return Namme;}
    public String getAbajo() { return abajo; }
    public double getArMin() { return arMin; }
    public double getArMax() { return arMax; }
    public double getDecMin() { return decMin; }
    public double getDecMax() { return decMax; }
}

