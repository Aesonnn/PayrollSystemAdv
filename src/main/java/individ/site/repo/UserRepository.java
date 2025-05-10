package individ.site.repo;

import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.repository.CrudRepository;
import individ.site.models.User;
// import java.util.List;

// public interface userRepository extends CrudRepository<User, Long>{
//     // List<User> findByUsername(String username);
//     Optional<User> findByUsername(String username);

// }


public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsernameAndPassword(String username, String password);
    User findByUsername(String username);
    User findByEmployeeId(Long employeeId);
}