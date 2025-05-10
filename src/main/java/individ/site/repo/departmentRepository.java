package individ.site.repo;

import org.springframework.data.repository.CrudRepository;
import individ.site.models.Department;

public interface departmentRepository extends CrudRepository<Department, Long>{
    
}
