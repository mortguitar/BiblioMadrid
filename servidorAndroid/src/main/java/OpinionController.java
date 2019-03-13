import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OpinionController {

    @Autowired
    private OpinionRepository repository;

    /**
     * Obtiene todas las opiniones de los usuarios
     * @return
     */
    @RequestMapping("/opiniones")
    public List<Opinion> getOpiniones() {

        List<Opinion> listaOpiniones = repository.findAll();
        return listaOpiniones;
    }

    /**
     * Obtiene todas las opiniones con una puntuacion determinada
     * @param puntuacion
     * @return
     */
    @RequestMapping("/opiniones_puntuacion")
    public List<Opinion> getOpiniones(int puntuacion) {

        List<Opinion> listaOpiniones = repository.findByPuntuacion(puntuacion);
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
                           @RequestParam(value = "texto" , defaultValue = "nulo") String texto,
                           @RequestParam(value = "puntuacion", defaultValue = "-1") int puntuacion) {

        Opinion opinion = new Opinion();
        opinion.setTitulo(titulo);
        opinion.setTexto(texto);
        opinion.setFecha(new Date(System.currentTimeMillis()));
        opinion.setPuntuacion(puntuacion);

        repository.save(opinion);
    }
}
