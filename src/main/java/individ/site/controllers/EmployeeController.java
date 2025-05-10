package individ.site.controllers;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;

// import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

// import com.fasterxml.jackson.annotation.JsonCreator.Mode;

import individ.site.models.Department;
import individ.site.models.Employee;
// import individ.site.models.Payroll;
import individ.site.repo.departmentRepository;
import individ.site.repo.employeeRepository;
import java.util.List;
import java.util.Optional;

import individ.site.models.Position;
import individ.site.models.User;
import individ.site.repo.positionRepository;
import individ.site.repo.UserRepository;
import jakarta.servlet.http.HttpSession;

import individ.site.service.UserService;
import individ.site.dto.EmployeeDTO;
import java.util.stream.Collectors;

@Controller
public class EmployeeController {
    
    @Autowired
    private employeeRepository employeeRepository;

    @Autowired
    private departmentRepository departmentRepository;

    @Autowired
    private positionRepository positionRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    // API endpoint secured by API key
    @GetMapping("/api/employees")
    @ResponseBody
    public List<EmployeeDTO> getAllEmployeesJson() {
        // The request has already been authenticated by the interceptor
        // No need for session checks here as we're using API key
        
        // Return all employees as DTOs to prevent recursion issues
        List<Employee> employees = new ArrayList<>();
        employeeRepository.findAll().forEach(employees::add);
        return employees.stream()
                .map(EmployeeDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/employees")
    public String employees_main(Model model, @RequestParam(required = false) String sortField, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if(loggedInUser == null){
            return "redirect:/login"; // Redirect to login page if user not authenticated
        }

        if (!userService.hasAccess(loggedInUser, "ADMIN")) {
            model.addAttribute("error", "You don't have permission to access this page");
            return "403";
        }

        Iterable<Employee> employees;
        if (sortField != null) {
            switch (sortField) {
                case "departmentNumber":
                    employees = employeeRepository.findAllByOrderByDepartmentDesc();
                    break;
                case "firstName":
                    employees = employeeRepository.findAllByOrderByFirstName();
                    break;
                case "payr":
                    // employees = employeeRepository.findAllByOrderByPayrollsDesc();
                    employees = employeeRepository.findAll();
                    List<Employee> employeeList = new ArrayList<>();
                    employees.forEach(employeeList::add);
                    Collections.sort(employeeList, (p1, p2) -> Integer.compare(p2.getPayrolls().size(), p1.getPayrolls().size()));
                    employees = employeeList;
                    break;
                default:
                    employees = employeeRepository.findAll();
            }
        } else {
            employees = employeeRepository.findAll();
        }
        model.addAttribute("employees", employees);
        return "employees";
    }


    @GetMapping("/employees/add")
    public String employees_add(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if(loggedInUser == null){
            return "redirect:/login"; // Redirect to login page if user not authenticated
        }
        
        if (!userService.hasAccess(loggedInUser, "ADMIN")) {
            model.addAttribute("error", "You don't have permission to access this page");
            return "403";
        }
        
        return "employees-add";
    }

    @PostMapping("/employees/add")
    public String employee_post_add(@RequestParam String fname,
                                    @RequestParam String lname,
                                    @RequestParam Date dob, 
                                    @RequestParam String email, // Changed to required
                                    @RequestParam(required = false) Long depid,
                                    @RequestParam(required = false) Long posid,
                                    Model model,
                                    HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if(loggedInUser == null){
            return "redirect:/login"; // Redirect to login page if user not authenticated
        }
        
        if (!userService.hasAccess(loggedInUser, "ADMIN")) {
            model.addAttribute("error", "You don't have permission to access this page");
            return "403";
        }

        // Basic validation
        if (fname.isBlank() || lname.isBlank()) {
            model.addAttribute("nameError", "First and last name are required");
            return "employees-add";
        }
        
        // Email validation - check if required field is provided
        if (email == null || email.isBlank()) {
            model.addAttribute("emailError", "Email is required");
            return "employees-add";
        }
        
        // Email uniqueness validation
        if (employeeRepository.existsByEmail(email)) {
            model.addAttribute("emailError", "An employee with this email already exists");
            return "employees-add";
        }

        Employee employee = new Employee(fname, lname, dob, email, null, null); // Initially set to null

        if (depid != null && !departmentRepository.existsById(depid)) {
            model.addAttribute("departmentError", "Department with this ID does not exist");
            return "employees-add";  
        }
    
        if (posid != null && !positionRepository.existsById(posid)) {
            model.addAttribute("positionError", "Position with this ID does not exist");
            return "employees-add"; 
        } 
    
        // Load Department and Position if IDs are provided
        if (depid != null) {
            Optional<Department> department = departmentRepository.findById(depid); 
            department.ifPresent(employee::setDepartment);
        }

        if (posid != null) {
            Optional<Position> position = positionRepository.findById(posid);
            position.ifPresent(employee::setPosition);
        }

        employeeRepository.save(employee); 
        return "redirect:/employees"; // Redirect to a page listing employees
    }


    @GetMapping("/employees/filter")
    public String filterEmployees(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if(loggedInUser == null){
            return "redirect:/login"; // Redirect to login page if user not authenticated
        }
        
        if (!userService.hasAccess(loggedInUser, "ADMIN")) {
            model.addAttribute("error", "You don't have permission to access this page");
            return "403";
        }
        
        return "employees-filter";
    }

    @PostMapping("/employees/filter")
    public String filterEmployees(@RequestParam String filterBy, 
                                @RequestParam(required = false) String keyword, 
                                Model model,
                                HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if(loggedInUser == null){
            return "redirect:/login"; // Redirect to login page if user not authenticated
        }
        
        if (!userService.hasAccess(loggedInUser, "ADMIN")) {
            model.addAttribute("error", "You don't have permission to access this page");
            return "403";
        }

        List<Employee> filteredEmployees = new ArrayList<>();
        switch (filterBy) {
            case "firstName":
                filteredEmployees = employeeRepository.findByFirstNameContaining(keyword);
                break;
            case "lastName":
                filteredEmployees = employeeRepository.findByLastNameContaining(keyword); 
                break;
            case "departmentId":
                if (keyword != null) {
                    try {
                        long departmentId = Long.parseLong(keyword);
                        filteredEmployees.addAll(employeeRepository.findByDepartmentId(departmentId));
                    } catch (NumberFormatException e) {
                        // Handle the case when the keyword is not a valid integer
                        model.addAttribute("keywordError", "Keyword must be an integer");
                        return "employees-filter";
                    }
                }
                break;
            case "employeeId":
                if (keyword != null) {
                    try {
                        long employeeId = Long.parseLong(keyword);
                        Optional<Employee> employee = employeeRepository.findById(employeeId);
                        employee.ifPresent(filteredEmployees::add); // Add if found
                    } catch (NumberFormatException e) {
                        // Handle the case when the keyword is not a valid integer
                        model.addAttribute("keywordError", "Keyword must be an integer");
                        return "employees-filter";
                    }
                }
                break; 
            case "positionId":
                if (keyword != null) {
                    
                    try {
                        long positionId = Long.parseLong(keyword); 
                        filteredEmployees.addAll(employeeRepository.findByPositionId(positionId));
                    } catch (NumberFormatException e) {
                        // Handle the case when the keyword is not a valid integer
                        model.addAttribute("keywordError", "Keyword must be an integer");
                        return "employees-filter";
                    }
                }
                break; 
            case "numPay":
                if (keyword != null) {
                    try {
                        int numPay = Integer.parseInt(keyword);
                        if (numPay < 0) {
                            model.addAttribute("keywordError", "Keyword must be a positive integer");
                            return "employees-filter";
                        }
                        Iterable<Employee> employees = employeeRepository.findAll();
                        for (Employee employee : employees) {
                            if (employee.getPayrolls().size() >= numPay) {
                                filteredEmployees.add(employee);
                            }
                        }
                    } catch (NumberFormatException e) {
                        // Handle the case when the keyword is not a valid integer
                        model.addAttribute("keywordError", "Keyword must be an integer");
                        return "employees-filter";
                    }
                }
                break;
            }

        model.addAttribute("filteredEmployees", filteredEmployees);
        return "employees-filter"; 
    }


    @GetMapping("/employees/{id}")
    public String department_details(@PathVariable(value = "id") long empId, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if(loggedInUser == null){
            return "redirect:/login"; // Redirect to login page if user not authenticated
        }
        
        if (!userService.hasAccess(loggedInUser, "ADMIN")) {
            model.addAttribute("error", "You don't have permission to access this page");
            return "403";
        }
        
        try {
            if (employeeRepository.existsById(empId)) {
                Optional<Employee> department = employeeRepository.findById(empId);
                ArrayList<Employee> res = new ArrayList<>();
                department.ifPresent(res::add);
                model.addAttribute("employee", res);
                
                // Check if employee is linked to a user
                User linkedUser = null;
                if (department.isPresent()) {
                    linkedUser = userRepository.findByEmployeeId(empId);
                }
                model.addAttribute("linkedUser", linkedUser);

                return "employee-detail";
            } else {
                throw new Exception("Record does not exist");
            }
        } catch (Exception e) {
            return "redirect:/employees";
        }
    }

    @GetMapping("/employee/{id}/edit")
    public String employee_edit(@PathVariable(value = "id") long empId, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if(loggedInUser == null){
            return "redirect:/login"; // Redirect to login page if user not authenticated
        }
        
        if (!userService.hasAccess(loggedInUser, "ADMIN")) {
            model.addAttribute("error", "You don't have permission to access this page");
            return "403";
        }
        
        try{
            if (employeeRepository.existsById(empId)) {
                Employee employee = employeeRepository.findById(empId).orElseThrow();
                model.addAttribute("employee", employee);
                return "employee-edit";
            } else {
                return "redirect:/employees";
            }
        } catch (Exception e) {
            return "redirect:/employees";
        }
    }

    @PostMapping("/employee/{id}/edit")
    public String employee_post_edit(@PathVariable(value = "id") long empId, 
                                    @RequestParam String fname, 
                                    @RequestParam String lname, 
                                    @RequestParam Date dob, 
                                    @RequestParam String email, // Changed to required
                                    @RequestParam(required = false) Long depid, 
                                    @RequestParam(required = false) Long posid, 
                                    Model model,
                                    HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if(loggedInUser == null){
            return "redirect:/login"; // Redirect to login page if user not authenticated
        }
        
        if (!userService.hasAccess(loggedInUser, "ADMIN")) {
            model.addAttribute("error", "You don't have permission to access this page");
            return "403";
        }
        
        // Email validation - check if required field is provided
        if (email == null || email.isBlank()) {
            model.addAttribute("emailError", "Email is required");
            model.addAttribute("emp", employeeRepository.findById(empId).orElseThrow());
            return "employee-edit";
        }
        
        Employee employee = employeeRepository.findById(empId).orElseThrow();
        
        // Email uniqueness validation - only if email is changed
        if (!email.equals(employee.getEmail()) && employeeRepository.existsByEmail(email)) {
            model.addAttribute("emailError", "An employee with this email already exists");
            model.addAttribute("emp", employee);
            return "employee-edit";
        }
        
        employee.setFirstName(fname);
        employee.setLastName(lname);
        employee.setDob(dob);
        employee.setEmail(email);

        if (depid != null && !departmentRepository.existsById(depid)) {
            model.addAttribute("departmentError", "Department with this ID does not exist");
            model.addAttribute("emp", employee);
            return "employee-edit";  
        }
    
        if (posid != null && !positionRepository.existsById(posid)) {
            model.addAttribute("positionError", "Position with this ID does not exist");
            model.addAttribute("emp", employee);
            return "employee-edit"; 
        } 
    
        // Load Department and Position if IDs are provided
        if (depid != null) {
            Optional<Department> department = departmentRepository.findById(depid); 
            department.ifPresent(employee::setDepartment);
        }

        if (posid != null) {
            Optional<Position> position = positionRepository.findById(posid);
            position.ifPresent(employee::setPosition);
        }

        employeeRepository.save(employee);
        return "redirect:/employees";
    }

    @PostMapping("/employee/{id}/delete")
    public String employee_delete(@PathVariable(value = "id") long empId, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if(loggedInUser == null){
            return "redirect:/login"; // Redirect to login page if user not authenticated
        }
        
        if (!userService.hasAccess(loggedInUser, "ADMIN")) {
            model.addAttribute("error", "You don't have permission to access this page");
            return "403";
        }
        
        // Check if employee is linked to a user
        User linkedUser = userRepository.findByEmployeeId(empId);
        if (linkedUser != null) {
            // Employee is linked to a user, can't delete
            model.addAttribute("error", "Cannot delete employee that is linked to a user");
            return "redirect:/employees/" + empId;
        }
        
        Employee employee = employeeRepository.findById(empId).orElseThrow();
        employeeRepository.delete(employee);
        return "redirect:/employees";
    }

    @GetMapping("/available-employees")
    public String availableEmployees(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if(loggedInUser == null){
            return "redirect:/login"; // Redirect to login page if user not authenticated
        }
        
        if (!userService.hasAccess(loggedInUser, "ADMIN")) {
            model.addAttribute("error", "You don't have permission to access this page");
            return "403";
        }
        
        // Get all employees
        List<Employee> allEmployees = (List<Employee>) employeeRepository.findAll();
        
        // Filter out employees that are already linked to users
        List<Employee> availableEmployees = new ArrayList<>();
        for (Employee employee : allEmployees) {
            User linkedUser = userRepository.findByEmployeeId(employee.getId());
            if (linkedUser == null) {
                availableEmployees.add(employee);
            }
        }
        
        model.addAttribute("employees", availableEmployees);
        return "available-employees";
    }
}
