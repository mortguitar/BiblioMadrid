package com.mortega.servidor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * Controlador para las opiniones
 * Contendrá todos los métodos que realicen operaciones sobre opiniones de los usuarios
 *
 * @author Santiago Faci
 * @version curso 2015-2016
 */
@RestController
public class Controller {

    @Autowired
    private OpinionRepository repository;

    /**
     * Obtiene todas las opiniones de los usuarios
     * @return
     */
    @RequestMapping("/opiniones")
    public List<Opiniones> getOpiniones() {

        List<Opiniones> listaOpiniones = repository.findAll();
        return listaOpiniones;
    }

    /**
     * Obtiene todas las opiniones con una puntuacion determinada
     * @param puntuacion
     * @return
     */
    @RequestMapping("/opiniones_puntuacion")
    public List<Opiniones> getOpiniones(int puntuacion) {

        List<Opiniones> listaOpiniones = repository.findByPuntuacion(puntuacion);
        return listaOpiniones;
    }

    /**
     * Registra una nueva opinión en la Base de Datos
     * @param titulo
     * @param texto
     * @param puntuacion
     */
    @RequestMapping("/add_opinion")
    public void addOpinion(@RequestParam(value = "titulo", defaultValue = "nada") String titulo,
                           @RequestParam(value = "texto" , defaultValue = "nada mas") String texto,
                           @RequestParam(value = "puntuacion", defaultValue = "-1") int puntuacion) {

        Opiniones opinion = new Opiniones();
        opinion.setTitulo(titulo);
        opinion.setTexto(texto);
        opinion.setFecha(new Date(System.currentTimeMillis()));
        opinion.setPuntuacion(puntuacion);

        repository.save(opinion);
    }
}
