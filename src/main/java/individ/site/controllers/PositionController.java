package individ.site.controllers;

import org.springframework.stereotype.Controller;
import individ.site.repo.positionRepository;
import jakarta.servlet.http.HttpSession;
import individ.site.repo.employeeRepository;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import individ.site.models.Employee;
import individ.site.models.Position;
import individ.site.models.User;
import individ.site.service.UserService;



@Controller
public class PositionController {
    
    @Autowired
    private positionRepository positionRepository;

    @Autowired
    private employeeRepository employeeRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/positions")
    public String position_main(Model model, @RequestParam(required = false) String sortField, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if(loggedInUser == null){
            return "redirect:/login";
        }

        if (!userService.hasAccess(loggedInUser, "ADMIN")) {
            model.addAttribute("error", "You don't have permission to access this page");
            return "403";
        }

        Iterable<Position> positions;
        if (sortField != null) {
            switch (sortField) {
                case "id":
                    positions = positionRepository.findAllByOrderById();
                    break;
                case "title":
                    positions = positionRepository.findAllByOrderByTitle();
                    break;
                case "salaryMin":
                    positions = positionRepository.findAllByOrderBySalaryMinDesc();
                    break;
                case "salaryMax":
                    positions = positionRepository.findAllByOrderBySalaryMaxDesc();
                    break;
                case "empnum": 
                    positions = positionRepository.findAll();
                    List<Position> positionList = new ArrayList<>();
                    positions.forEach(positionList::add);
                    Collections.sort(positionList, (p1, p2) -> Integer.compare(p2.getEmployees().size(), p1.getEmployees().size()));
                    positions = positionList;
                    break; 

                default:
                    positions = positionRepository.findAll();
            }
        } else {
            positions = positionRepository.findAll();
        }
        model.addAttribute("positions", positions);
        return "positions";
    }

    @GetMapping("/positions/summary")
    public String positions_summary(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if(loggedInUser == null){
            return "redirect:/login";
        }
        if (!userService.hasAccess(loggedInUser, "ADMIN")) {
            model.addAttribute("error", "You don't have permission to access this page");
            return "403";
        }
        
        return "positions-summary";
    }
    @PostMapping("/positions/summary")
    public String filterPositionsByEmployeeCount(@RequestParam(required = false) Integer employeeCount, Model model) {
        
        if (employeeCount == null ||employeeCount < 0) {
            model.addAttribute("employeeCountError", "Employee count must be a positive number");
            return "positions-summary";
        }
        

        List<Position> filteredPositions = StreamSupport.stream(positionRepository.findAll().spliterator(), false)
                                        .filter(p -> p.getEmployees().size() == employeeCount)
                                        .collect(Collectors.toList());

        model.addAttribute("positionRepositorySize", positionRepository.count());
        model.addAttribute("filteredPositions", filteredPositions);
        model.addAttribute("employeeCount", employeeCount);
        return "positions-summary"; 
    }


    @GetMapping("/positions/add")
    public String positions_add(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if(loggedInUser == null){
            return "redirect:/login";
        }
        if (!userService.hasAccess(loggedInUser, "ADMIN")) {
            model.addAttribute("error", "You don't have permission to access this page");
            return "403";
        }
        
        return "positions-add";
    }

    @PostMapping("/positions/add")
    public String position_post_add(@RequestParam String title, 
                                    @RequestParam(required = false) String description, 
                                    @RequestParam(required = false) Double salaryMin, 
                                    @RequestParam(required = false) Double salaryMax, Model model) {
        
        if (title.isBlank()) {
            model.addAttribute("titleError", "Title is required");
            return "positions-add"; // Re-render the form 
        }
        Position position = new Position(title, description, salaryMin, salaryMax);
        positionRepository.save(position);
        return "redirect:/positions";
    }

    @GetMapping("/positions/{id}")
    public String position_details(@PathVariable(value = "id") long posId, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if(loggedInUser == null){
            return "redirect:/login";
        }
        if (!userService.hasAccess(loggedInUser, "ADMIN")) {
            model.addAttribute("error", "You don't have permission to access this page");
            return "403";
        }
        
        try {
            if (positionRepository.existsById(posId)) {
                Optional<Position> position = positionRepository.findById(posId);
                ArrayList<Position> res = new ArrayList<>();
                position.ifPresent(res::add);
                model.addAttribute("position", res);

                List<Employee> employees = employeeRepository.findByPositionId(posId);
                model.addAttribute("employees", employees);
                return "position-detail";
            } else {
                throw new Exception("Record does not exist");
            }
        } catch (Exception e) {
            return "redirect:/positions";
        }
    }

    @GetMapping("/position/{id}/edit")
    public String position_edit(@PathVariable(value = "id") long posId, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if(loggedInUser == null){
            return "redirect:/login";
        }
        if (!userService.hasAccess(loggedInUser, "ADMIN")) {
            model.addAttribute("error", "You don't have permission to access this page");
            return "403";
        }
        
        try {
            if (positionRepository.existsById(posId)) {
                Position position = positionRepository.findById(posId).orElseThrow();
                model.addAttribute("position", position);
                return "position-edit";
            } else {
                throw new Exception("Record does not exist");
            }
        } catch (Exception e) {
            return "redirect:/positions";
        }
    }

    @PostMapping("/position/{id}/edit")
    public String position_post_edit(@PathVariable(value = "id") long posId, 
                                    @RequestParam String title, 
                                    @RequestParam(required = false) String description, 
                                    @RequestParam(required = false) Double salaryMin, 
                                    @RequestParam(required = false) Double salaryMax, Model model) {
        try {
            if (positionRepository.existsById(posId)) {
                Position position = positionRepository.findById(posId).orElseThrow();
                position.setTitle(title);
                position.setDescription(description);
                position.setSalaryMin(salaryMin);
                position.setSalaryMax(salaryMax);
                positionRepository.save(position);
                return "redirect:/positions";
            } else {
                throw new Exception("Record does not exist");
            }
        } catch (Exception e) {
            return "redirect:/positions";
        }
    }

    @PostMapping("/position/{id}/delete")
    public String position_delete(@PathVariable(value = "id") long posId, Model model) {
        try {
            if (positionRepository.existsById(posId)) {
                Position position = positionRepository.findById(posId).orElseThrow();
                positionRepository.delete(position);
                return "redirect:/positions";
            } else {
                throw new Exception("Record does not exist");
            }
        } catch (Exception e) {
            return "redirect:/positions";
        }
    }

    @GetMapping("/positions/filter")
    public String filterPositions(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if(loggedInUser == null){
            return "redirect:/login";
        }
        if (!userService.hasAccess(loggedInUser, "ADMIN")) {
            model.addAttribute("error", "You don't have permission to access this page");
            return "403";
        }
        
        return "position-filter";
    }
    

    @PostMapping("/positions/filter")
    public String filter_post_Positions(@RequestParam String filterBy, 
                                @RequestParam(required = false) String filterValue, 
                                Model model) {

        List<Position> filteredPositions = new ArrayList<>();
        switch (filterBy) {
            case "title":
                filteredPositions = positionRepository.findByTitleContaining(filterValue);  // More flexible search
                break;
            case "Salary":
                if (filterValue != null) {
                    try {
                        double salary = Double.parseDouble(filterValue);
                        for (Position position : positionRepository.findAll()) {
                            if (position.getSalaryMin() <= salary && position.getSalaryMax() >= salary) {
                                filteredPositions.add(position);
                            }
                        }
                    } catch (NumberFormatException e) {
                        // Handle the case when the keyword is not a valid double
                        model.addAttribute("keywordError", "Keyword must be a number");
                        return "position-filter";
                    }
                }
                break;
            case "numEmp":
                if (filterValue != null) {
                    try {
                        int numEmp = Integer.parseInt(filterValue);
                        if (numEmp < 0) {
                            model.addAttribute("keywordError", "Keyword must be a positive integer");
                            return "position-filter";
                        }
                        Iterable<Position> positions1 = positionRepository.findAll();
                        // List<Position> positionListEmp = new ArrayList<>();
                        for (Position position : positions1) {
                            if (position.getEmployees().size() >= numEmp) {
                                filteredPositions.add(position);
                            }
                        }
                        // filteredPositions = positionListEmp;       
                    } catch (NumberFormatException e) {
                        // Handle the case when the keyword is not a valid integer
                        model.addAttribute("keywordError", "Keyword must be an integer");
                        return "position-filter";
                    }
                }
        }

        model.addAttribute("filteredPositions", filteredPositions);
        return "position-filter";
 
    }

}
