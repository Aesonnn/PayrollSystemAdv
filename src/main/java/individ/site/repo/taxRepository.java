package individ.site.repo;

import org.springframework.data.repository.CrudRepository;
import individ.site.models.Tax;


public interface taxRepository extends CrudRepository<Tax, Long>{
    
}
