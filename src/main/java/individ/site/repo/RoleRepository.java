package individ.site.repo;

import org.springframework.data.repository.CrudRepository;
import individ.site.models.Role;

public interface RoleRepository extends CrudRepository<Role, Long> {
    Role findByName(String name);
}
