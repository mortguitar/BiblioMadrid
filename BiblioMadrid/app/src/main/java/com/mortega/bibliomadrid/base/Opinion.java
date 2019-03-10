package com.mortega.bibliomadrid.base;

import java.io.Serializable;

public class Opinion implements Serializable {

    private int puntuacion;
    private String opinionText;

    public Opinion() { super(); }

    public int getPuntuacion() { return puntuacion; }
    public void setPuntuacion(int puntuacion) { this.puntuacion = puntuacion; }

    public String getOpinionText() { return opinionText; }
    public void setOpinionText(String opinionText) { this.opinionText = opinionText; }
}
