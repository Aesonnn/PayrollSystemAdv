package individ.site.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;

import individ.site.models.Role;
import individ.site.models.User;
import individ.site.repo.RoleRepository;
import individ.site.repo.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository repo;
    
    @Autowired
    private RoleRepository roleRepository;

    public User login(String username, String password) {
        User user = repo.findByUsernameAndPassword(username, password);
        return user;
    }

    public boolean hasAccess(User user, String requiredRole) {
        if (user == null) return false;
        return user.hasRole(requiredRole) || user.hasRole("ADMIN");
    }

    public void register(User user) {
        // Data validation
        if (user == null || user.getUsername() == null || user.getPassword() == null) {
            throw new IllegalArgumentException("Invalid user data");
        }

        // Check for duplicate user
        if (repo.findByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException("Username already exists");
        }

        // If no roles assigned, add default role
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            Role userRole = roleRepository.findByName("USER");
            if (userRole != null) {
                user.addRole(userRole);
            }
        }
        
        // Save user to the database
        repo.save(user);
    }

    public void addRoleToUser(Long userId, String roleName) {
        User user = repo.findById(userId).orElse(null);
        Role role = roleRepository.findByName(roleName);
        
        if (user != null && role != null) {
            user.addRole(role);
            repo.save(user);
        }
    }

    public User findByUsername(String username) {
        return repo.findByUsername(username);
    }

    public void createAdminUser() {
        User adminUser = repo.findByUsername("admin");
        if (adminUser == null) {
            Role adminRole = roleRepository.findByName("ADMIN");
            
            if (adminRole != null) {
                adminUser = new User("admin", "admin123", "Admin", "User");
                adminUser.addRole(adminRole);
                repo.save(adminUser);
            }
        }
    }

    // For testing purposes
    public User getFirstUser() {
        List<User> users = repo.findAll();
        if (!users.isEmpty()) {
            return users.get(0);
        }
        return null;
    }
}
