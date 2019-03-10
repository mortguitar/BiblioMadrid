package com.mortega.servidor;

import org.springframework.data.annotation.Id;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import java.util.Date;

/**
 * Opinion que los usuarios tienen sobre un monumento
 * Se deben definir las anotaciones que indican la tabla y columnas a las que
 * representa esta clase y sus atributos
 *
 * @author Santiago Faci
 * @version curso 2015-2016
 */
@Entity
@Table(name = "opiniones")
public class Opiniones {

    @Id
    @GeneratedValue
    private int id;
    @Column
    private String titulo;
    @Column
    private String texto;
    @Column
    private Date fecha;
    @Column
    private int puntuacion;

    // Constructor
    public Opiniones() {

    }

    // Getters y Setters


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }
}
