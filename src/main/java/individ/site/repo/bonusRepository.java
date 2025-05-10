package individ.site.repo;

import org.springframework.data.repository.CrudRepository;
import individ.site.models.Bonus;

public interface bonusRepository extends CrudRepository<Bonus, Long> {
    // Basic CRUD operations are provided by CrudRepository
}
