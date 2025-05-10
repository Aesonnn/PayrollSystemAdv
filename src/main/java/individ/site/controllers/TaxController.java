package individ.site.controllers;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

// import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import individ.site.models.Tax;

// import individ.site.repo.payrollRepository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import individ.site.models.User;
import jakarta.servlet.http.HttpSession;
import individ.site.repo.taxRepository;

@Controller
public class TaxController {
    
    @Autowired
    private individ.site.repo.taxRepository taxRepository;

    @Autowired
    private individ.site.service.UserService userService;

    @GetMapping("/taxes")
    public String taxes_main(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if(loggedInUser == null){
            return "redirect:/login";
        }
        if (!userService.hasAccess(loggedInUser, "ADMIN")) {
            model.addAttribute("error", "You don't have permission to access this page");
            return "403";
        }
        
        Iterable<Tax> taxes = taxRepository.findAll();

        model.addAttribute("taxes", taxes);
        return "taxes";
    }

    @GetMapping("/taxes/list") 
    @ResponseBody // Important for returning JSON directly
    public List<Tax> listTaxes() {
        return StreamSupport.stream(taxRepository.findAll().spliterator(), false)
                        .collect(Collectors.toList());
    }

    @GetMapping("/taxes/add")
    public String taxes_add(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if(loggedInUser == null){
            return "redirect:/login";
        }
        if (!userService.hasAccess(loggedInUser, "ADMIN")) {
            model.addAttribute("error", "You don't have permission to access this page");
            return "403";
        }
        return "taxes-add";
    }


    @PostMapping("/taxes/add")
    public String taxes_post_add(@RequestParam Double percentRate, 
                                 @RequestParam String taxName,
                                 @RequestParam(required = false) String description, 
                                 Model model) {
         //  Validation
        if (taxName.isBlank()) {
            model.addAttribute("taxNameError", "Tax name is required");
            return "taxes-add"; 
        }

        if (percentRate <= 0) { // Check if percentRate is empty or invalid
            model.addAttribute("percentRateError", "Percent rate is required and must be greater than 0");
            return "taxes-add"; 
        }

        Tax tax = new Tax(percentRate, taxName, description);
        taxRepository.save(tax);
        return "redirect:/taxes";
        
    }

    @GetMapping("/tax/{id}")
    public String taxes_details(@PathVariable(value = "id") long taxId, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if(loggedInUser == null){
            return "redirect:/login";
        }
        if (!userService.hasAccess(loggedInUser, "ADMIN")) {
            model.addAttribute("error", "You don't have permission to access this page");
            return "403";
        }

        try {
            if (taxRepository.existsById(taxId)) {
                Tax tax = taxRepository.findById(taxId).orElseThrow();
                model.addAttribute("tax", tax);
                return "tax-detail";
            } else {
                throw new Exception("Record does not exist");
            }
        } catch (Exception e) {
            return "redirect:/taxes";
        }
    }

    @GetMapping("/tax/{id}/edit")
    public String taxes_edit(@PathVariable(value = "id") long taxId, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if(loggedInUser == null){
            return "redirect:/login";
        }
        if (!userService.hasAccess(loggedInUser, "ADMIN")) {
            model.addAttribute("error", "You don't have permission to access this page");
            return "403";
        }
        
        try {
            if (taxRepository.existsById(taxId)) {
                Tax tax = taxRepository.findById(taxId).orElseThrow();
                model.addAttribute("tax", tax);
                return "tax-edit";
            } else {
                throw new Exception("Record does not exist");
            }
        } catch (Exception e) {
            return "redirect:/taxes";
        }
    }

    @PostMapping("/tax/{id}/edit")
    public String taxes_post_edit(@PathVariable(value = "id") long taxId, 
                                 @RequestParam Double percentRate, 
                                 @RequestParam String taxName,
                                 @RequestParam(required = false) String description, 
                                 Model model) {
        try {
            if (taxName.isBlank()) {
                model.addAttribute("taxNameError", "Tax name is required");
                return "taxes-add"; 
            }
    
            if (percentRate <= 0) { // Check if percentRate is empty or invalid
                model.addAttribute("percentRateError", "Percent rate is required and must be greater than 0");
                return "taxes-add"; 
            }

            if (taxRepository.existsById(taxId)) {
                Tax tax = taxRepository.findById(taxId).orElseThrow();
                tax.setPercentRate(percentRate);
                tax.setTaxName(taxName);
                tax.setDescription(description);
                taxRepository.save(tax);
                return "redirect:/taxes";
            } else {
                throw new Exception("Record does not exist");
            }
        } catch (Exception e) {
            return "redirect:/taxes";
        }
    }

    @PostMapping("/tax/{id}/delete")
    public String taxes_delete(@PathVariable(value = "id") long taxId, Model model) {
        try {
            if (taxRepository.existsById(taxId)) {
                Tax tax = taxRepository.findById(taxId).orElseThrow();
                taxRepository.delete(tax);
                return "redirect:/taxes";
            } else {
                throw new Exception("Record does not exist");
            }
        } catch (Exception e) {
            return "redirect:/taxes";
        }
    }

}
