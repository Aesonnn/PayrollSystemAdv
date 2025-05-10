package individ.site.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import individ.site.models.Role;
import individ.site.repo.RoleRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        // Initialize roles if they don't exist
        createRoleIfNotFound("ADMIN");
        createRoleIfNotFound("USER");

        
    }
    
    private void createRoleIfNotFound(String name) {
        if (roleRepository.findByName(name) == null) {
            Role role = new Role(name);
            roleRepository.save(role);
        }
    }
}
