import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface OpinionRepository extends CrudRepository<Opinion, Integer> {

	List<Opinion> findAll();
	List<Opinion> findByPuntuacion(int puntuacion);
}
