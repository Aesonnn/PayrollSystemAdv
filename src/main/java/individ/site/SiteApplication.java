package individ.site;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import individ.site.models.Role;
import individ.site.models.User;
import individ.site.repo.RoleRepository;
import individ.site.service.UserService;


// TODO: 
//      1. It is time for Integrations
//      N. If integrations are not the way to go or if plenty of time, add contract for employee and the salary in it
//         The salary should be taken from a contract and prepopulate the respective field in the payroll. 
//         Or create some other kind of deduction or benefit that would be applied to the payroll.


@SpringBootApplication
@EnableJpaRepositories(basePackageClasses = individ.site.repo.UserRepository.class)
public class SiteApplication {

	public static void main(String[] args) {
		SpringApplication.run(SiteApplication.class, args);
	}

    @Bean
    public CommandLineRunner initializeSystem(RoleRepository roleRepository, UserService userService) {
        return args -> {
            // Create default roles if they don't exist
            if (roleRepository.findByName("ADMIN") == null) {
                roleRepository.save(new Role("ADMIN"));
                System.out.println("Created ADMIN role");
            }
            
            if (roleRepository.findByName("USER") == null) {
                roleRepository.save(new Role("USER"));
                System.out.println("Created USER role");
            }
            
            System.out.println("Role initialization completed");
            
            // First try to create a default admin user
            userService.createAdminUser();
            
            // Get the first user from the repository and make them an admin
            User firstUser = userService.getFirstUser();
            if (firstUser != null) {
                userService.addRoleToUser(firstUser.getId(), "ADMIN");
                System.out.println("Added ADMIN role to user: " + firstUser.getUsername());
            }
            
            System.out.println("Admin user initialization completed");
        };
    }

}
