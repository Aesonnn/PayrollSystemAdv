package individ.site.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.annotation.PathVariable;

import individ.site.models.Employee;
import individ.site.models.User;
import individ.site.repo.UserRepository;
import individ.site.repo.employeeRepository;
import individ.site.repo.RoleRepository;
import individ.site.service.UserService;
import individ.site.models.Role;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.ui.Model;

import java.util.Optional;
import java.util.Set;


@Controller
@SessionAttributes("loggedInUser")
public class UserController {

    @Autowired
    private UserRepository userRepositoryrepo;
    
    @Autowired
    private employeeRepository employeeRepository;
    
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login(Model model) {
        return "login"; // Display login page
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        Model model){

        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return "redirect:/login";
        }

        User logged_user = userRepositoryrepo.findByUsernameAndPassword(username, password);

        if(logged_user != null){
            model.addAttribute("loggedInUser", logged_user);
            
            // Check if the user has ADMIN role
            if (logged_user.hasRole("ADMIN")) {
                return "redirect:/"; // Admins go to main page
            } else {
                // Regular users with employee association go to filtered payrolls page
                if (logged_user.getEmployee() != null) {
                    return "redirect:/payrolls/filter-" + logged_user.getEmployee().getId();
                } else {
                    // Users without employee association go to main page
                    return "redirect:/";
                }
            }
        } else {
            // add error atributte to the model
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if(loggedInUser == null){
            return "redirect:/login";
        }
        if (!userService.hasAccess(loggedInUser, "ADMIN")) {
            model.addAttribute("error", "You don't have permission to access this page");
            return "403";
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                                    @RequestParam String password,
                                    @RequestParam String firstName,
                                    @RequestParam String lastName,
                                    @RequestParam(required = false) Long employeeId,
                                    @RequestParam(required = false) String role,
                                    Model model) {
        // if any field is null or empty, return to the registration page
        if (username == null || username.isEmpty() || password == null || password.isEmpty() || firstName == null || firstName.isEmpty() || lastName == null || lastName.isEmpty()) {
            return "redirect:/register";
        }

        // Check if user with such username already exists
        User existingUser = userRepositoryrepo.findByUsername(username);

        if (existingUser != null) {
            // add error atributte to the model
            model.addAttribute("error", "Username already exists");
            return "register";
        }
        
        User user = new User(username, password, firstName, lastName);

        // Check if user with such employeeId already exists
        if (employeeId != null) {
            Employee existingEmployee = employeeRepository.findById(employeeId).orElse(null);
            if (existingEmployee == null) {
            model.addAttribute("error", "Employee with ID " + employeeId + " does not exist");
            return "register";
            }

            // Check if a user is already associated with the employee
            User employeeUser = userRepositoryrepo.findByEmployeeId(employeeId);
            if (employeeUser != null) {
            model.addAttribute("error", "Employee with ID " + employeeId + " is already associated with another user");
            return "register";
            }
            
            // Associate the employee with the user
            user.setEmployee(existingEmployee);
        }
        
        // Get the role from database instead of creating a new one
        Role userRole = roleRepository.findByName(role);
        if (userRole == null) {
            model.addAttribute("error", "Invalid role selected");
            return "register";
        }
        
        user.addRole(userRole);
        userRepositoryrepo.save(user);
        return "redirect:/"; // Redirect to login page after registration
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, Model model){
        HttpSession session = request.getSession(false);
        if(session != null){
            session.removeAttribute("loggedInUser");
            session.invalidate();
        }
        // Set loggedInUser attribute to null in the model
        model.addAttribute("loggedInUser", null);
        return "redirect:/"; // Redirect to the login page after logging out
    }
    
    @GetMapping("/users")
    public String users(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login"; // Redirect to login page if user not authenticated
        }
        if (!userService.hasAccess(loggedInUser, "ADMIN")) {
            model.addAttribute("error", "You don't have permission to access this page");
            return "403";
        }
        
        // Fetch all users from the database
        Iterable<User> users = userRepositoryrepo.findAll();
        
        model.addAttribute("users", users);
        
        return "users"; // Display user info page
    }

    @GetMapping("/user/{id}")
    public String userDetail(@PathVariable("id") Long id, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login"; // Redirect to login page if user not authenticated
        }
        
        if (!userService.hasAccess(loggedInUser, "ADMIN")) {
            model.addAttribute("error", "You don't have permission to access this page");
            return "403";
        }
        
        Optional<User> userOptional = userRepositoryrepo.findById(id);
        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get());
            return "user-detail";
        } else {
            return "redirect:/users";
        }
    }

    @GetMapping("/user/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login"; // Redirect to login page if user not authenticated
        }
        
        if (!userService.hasAccess(loggedInUser, "ADMIN")) {
            model.addAttribute("error", "You don't have permission to access this page");
            return "403";
        }
        
        Optional<User> userOptional = userRepositoryrepo.findById(id);
        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get());
            return "user-edit";
        } else {
            return "redirect:/users";
        }
    }

    @PostMapping("/user/{id}/edit")
    public String updateUser(@PathVariable("id") Long id,
                           @RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String firstName,
                           @RequestParam String lastName,
                           @RequestParam String role,
                           @RequestParam(required = false) Long employeeId,
                           Model model, HttpSession session) {
        
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        
        if (!userService.hasAccess(loggedInUser, "ADMIN")) {
            model.addAttribute("error", "You don't have permission to access this page");
            return "403";
        }
        
        // Check if any required field is empty
        if (username == null || username.isEmpty() || 
            password == null || password.isEmpty() || 
            firstName == null || firstName.isEmpty() || 
            lastName == null || lastName.isEmpty() ||
            role == null || role.isEmpty()) {
            model.addAttribute("error", "All fields are required");
            return "redirect:/user/" + id + "/edit";
        }
        
        Optional<User> userOptional = userRepositoryrepo.findById(id);
        if (!userOptional.isPresent()) {
            return "redirect:/users";
        }
        
        User user = userOptional.get();
        
        // Check if username already exists (and it's not the current user's username)
        User existingUser = userRepositoryrepo.findByUsername(username);
        if (existingUser != null && !existingUser.getId().equals(id)) {
            model.addAttribute("error", "Username already exists");
            model.addAttribute("user", user);
            return "user-edit";
        }
        
        // Update user information
        user.setUsername(username);
        user.setPassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        
        // Update role
        Set<Role> roles = user.getRoles();
        roles.clear();
        
        // Get the role from database instead of creating a new one
        Role userRole = roleRepository.findByName(role);
        if (userRole == null) {
            model.addAttribute("error", "Invalid role selected");
            model.addAttribute("user", user);
            return "user-edit";
        }
        
        roles.add(userRole);
        user.setRoles(roles);
        
        // Update employee association
        if (role.equals("USER")) {
            if (employeeId == null) {
                model.addAttribute("error", "Employee ID is required for User role");
                model.addAttribute("user", user);
                return "user-edit";
            }
            
            Optional<Employee> employeeOptional = employeeRepository.findById(employeeId);
            if (!employeeOptional.isPresent()) {
                model.addAttribute("error", "Employee with ID " + employeeId + " does not exist");
                model.addAttribute("user", user);
                return "user-edit";
            }
            
            user.setEmployee(employeeOptional.get());
        } else {
            user.setEmployee(null);
        }
        
        userRepositoryrepo.save(user);
        
        return "redirect:/users";
    }

    @PostMapping("/user/{id}/delete")
    public String deleteUser(@PathVariable("id") Long id, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        
        if (!userService.hasAccess(loggedInUser, "ADMIN")) {
            model.addAttribute("error", "You don't have permission to access this page");
            return "403";
        }
        
        // Don't allow the currently logged-in user to delete themselves
        if (loggedInUser.getId().equals(id)) {
            model.addAttribute("error", "You cannot delete your own account while logged in");
            return "redirect:/user/" + id;
        }
        
        Optional<User> userOptional = userRepositoryrepo.findById(id);
        if (userOptional.isPresent()) {
            userRepositoryrepo.deleteById(id);
        }
        
        return "redirect:/users";
    }
}
