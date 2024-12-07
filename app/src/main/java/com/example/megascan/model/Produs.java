package com.example.megascan.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Produs {
    @JsonProperty("COD")
    String COD;
    @JsonProperty("PRET")
    double PRET;
    @JsonProperty("DENUMIRE")
    String DENUMIRE;
    @JsonProperty("FIRMA")
    String FIRMA;

    public Produs(){}

    public Produs(String cod, double pret, String denumire, String firma) {
        this.COD = cod;
        this.PRET = pret;
        this.DENUMIRE = denumire;
        this.FIRMA = firma;
    }

    public String getCod() {
        return COD;
    }

    public void setCod(String cod) {
        this.COD = cod;
    }

    public double getPret() {
        return PRET;
    }

    public void setPret(double PRET) {
        this.PRET = PRET;
    }

    public String getDenumire() {
        return DENUMIRE;
    }

    public void setDenumire(String DENUMIRE) {
        this.DENUMIRE = DENUMIRE;
    }

    public String getFirma() {
        return FIRMA;
    }

    public void setFirma(String FIRMA) {
        this.FIRMA = FIRMA;
    }
}
