package com.example.megascan.model;

public class Produs {
    String cod;
    double pret;
    String denumire;
    String firma;

    public Produs(String cod, double pret, String denumire, String firma) {
        this.cod = cod;
        this.pret = pret;
        this.denumire = denumire;
        this.firma = firma;
    }

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public double getPret() {
        return pret;
    }

    public void setPret(double pret) {
        this.pret = pret;
    }

    public String getDenumire() {
        return denumire;
    }

    public void setDenumire(String denumire) {
        this.denumire = denumire;
    }

    public String getFirma() {
        return firma;
    }

    public void setFirma(String firma) {
        this.firma = firma;
    }
}
