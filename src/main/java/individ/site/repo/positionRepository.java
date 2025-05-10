package individ.site.repo;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import individ.site.models.Position;

public interface positionRepository extends CrudRepository<Position, Long>{
    List<Position> findByTitle(String title); 
    List<Position> findByTitleContaining(String title);  
    List<Position> findAllByOrderBySalaryMaxDesc();
    List<Position> findAllByOrderBySalaryMinDesc();
    List<Position> findAllByOrderByTitle();
    List<Position> findAllByOrderById();
    List<Position> findAllByOrderByEmployeesDesc();
}
