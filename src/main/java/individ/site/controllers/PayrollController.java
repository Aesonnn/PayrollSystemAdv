package individ.site.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// import individ.site.models.Department;
// import individ.site.models.Employee;
import java.sql.Date;
import individ.site.models.Payroll;
import individ.site.models.PayrollTax;
import individ.site.models.Tax;
import individ.site.models.User;
import individ.site.models.Employee;
import individ.site.repo.payrollRepository;
import individ.site.repo.taxRepository;
import jakarta.servlet.http.HttpSession;
import individ.site.repo.payrolltaxRepository;
import individ.site.repo.payrollbonusRepository;
import individ.site.repo.bonusRepository;
import java.util.Optional;
import java.util.stream.Collectors;
import individ.site.models.Bonus;
import individ.site.models.PayrollBonus;

import individ.site.repo.employeeRepository;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.transaction.annotation.Transactional;
import java.util.stream.StreamSupport;

import individ.site.service.UserService;


@Controller
public class PayrollController {
    
    @Autowired
    private payrollRepository payrollRepository;

    @Autowired
    private payrolltaxRepository payrolltaxRepository;

    @Autowired
    private payrollbonusRepository payrollbonusRepository;

    @Autowired
    private taxRepository taxRepository;
    
    @Autowired
    private bonusRepository bonusRepository;

    @Autowired
    private employeeRepository employeeRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/payrolls")
    public String getAllPayrolls(Model model, @RequestParam(required = false) String sortField, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if(loggedInUser == null){
            return "redirect:/login";
        }

        if (!userService.hasAccess(loggedInUser, "ADMIN")) {
            model.addAttribute("error", "You don't have permission to access this page");
            return "403";
        }

        Iterable<Payroll> payrolls;
        if (sortField != null) {
            switch (sortField) {
                case "id":
                    payrolls = payrollRepository.findAllByOrderById();
                    break;
                case "grossPay":
                    payrolls = payrollRepository.findAllByOrderByGrossPayDesc();
                    break;
                case "netPay":
                    payrolls = payrollRepository.findAllByOrderByNetPayDesc();
                    break;
                case "empfname":
                    payrolls = payrollRepository.findAllByOrderByEmployeeDesc();
                    break;
                case "taxesnum": 
                    payrolls = payrollRepository.findAll();
                    List<Payroll> payrollList = new ArrayList<>();
                    payrolls.forEach(payrollList::add);
                    Collections.sort(payrollList, (p1, p2) -> Integer.compare(p2.getPaytax().size(), p1.getPaytax().size()));
                    payrolls = payrollList;
                    break;
                default:
                    payrolls = payrollRepository.findAll();
            }
        } else {
            payrolls = payrollRepository.findAll();
        }
        model.addAttribute("payrolls", payrolls);
        return "payrolls";
    }

    @GetMapping("/payroll/{id}")
    public String payroll_details(@PathVariable(value = "id") long prId, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if(loggedInUser == null){
            return "redirect:/login";
        }

        // Check if user is accessing their own payroll data or has ADMIN role
        boolean isAuthorized = false;
        boolean isAdmin = userService.hasAccess(loggedInUser, "ADMIN");
        
        if (isAdmin) {
            isAuthorized = true;
        } else {
            Optional<Payroll> payroll = payrollRepository.findById(prId);
            if (payroll.isPresent() && loggedInUser.getEmployee() != null && 
            payroll.get().getEmployee() != null && 
            payroll.get().getEmployee().getId().equals(loggedInUser.getEmployee().getId())) {
            isAuthorized = true;
            }
        }
        
        if (!isAuthorized) {
            model.addAttribute("error", "You don't have permission to access this page");
            return "403"; // Access forbidden page
        }
        try {
            if (payrollRepository.existsById(prId)) {
                Payroll payroll = payrollRepository.findById(prId).orElseThrow();
                model.addAttribute("payrolls", payroll);
                model.addAttribute("isAdmin", isAdmin);

                List<PayrollTax> payrollTaxes = payrolltaxRepository.findByPayrollId(prId);
                List<Tax> taxFields = new ArrayList<>();
                for (PayrollTax payrollTax : payrollTaxes) {
                    taxFields.add(payrollTax.getTax());
                }

                List<PayrollBonus> payrollBonuses = payrollbonusRepository.findByPayrollId(prId);
                List<Bonus> bonusFields = new ArrayList<>();
                for (PayrollBonus payrollBonus : payrollBonuses) {
                    bonusFields.add(payrollBonus.getBonus());
                }
                model.addAttribute("bonuses", bonusFields);

                model.addAttribute("taxes", taxFields);
                return "payroll-detail";
            } else {
                throw new Exception("Record does not exist");
            }
        } catch (Exception e) {
            return "redirect:/payrolls";
        }
    }

    @GetMapping("/payrolls/add")
    public String payroll_add(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if(loggedInUser == null){
            return "redirect:/login";
        }
        if (!userService.hasAccess(loggedInUser, "ADMIN")) {
            model.addAttribute("error", "You don't have permission to access this page");
            return "403";
        }
        return "payrolls-add";
    }


    Double net_pay;

    @PostMapping("/payrolls/add")
    public String payroll_post_add(@RequestParam String comments,
                                @RequestParam(required = false) List<Long> taxIds,
                                @RequestParam(required = false) List<Long> bonusIds,
                                @RequestParam Long employeeId,
                                @RequestParam Double grossPay,
                                Model model) {

        // Define new payroll object
        Payroll payroll = new Payroll(comments, null); 
        
        if (employeeId == null) {
            model.addAttribute("employeeError", "Employee ID is required");
            return "payrolls-add"; 
        }

        if (!employeeRepository.existsById(employeeId)) {
            model.addAttribute("employeeError", "Employee with this ID does not exist");
            return "payrolls-add"; 
        }

        if(employeeId != null) {
            Optional<Employee> employee = employeeRepository.findById(employeeId);
            employee.ifPresent(payroll::setEmployee);
        }

        payrollRepository.save(payroll); 

        if (taxIds != null) {
            for (Long taxId : taxIds) {
                Optional<Tax> tax = taxRepository.findById(taxId);
                tax.ifPresent(t -> {
                    PayrollTax payrollTax = new PayrollTax(payroll, t);
                    payrolltaxRepository.save(payrollTax);
                });
            }
        }
        
        // Add bonuses to payroll
        if (bonusIds != null) {
            for (Long bonusId : bonusIds) {
                Optional<Bonus> bonus = bonusRepository.findById(bonusId);
                bonus.ifPresent(b -> {
                    PayrollBonus payrollBonus = new PayrollBonus(payroll, b);
                    payrollbonusRepository.save(payrollBonus);
                });
            }
        }

        // Set initial gross pay 
        payroll.setGrossPay(grossPay);
        net_pay = grossPay;
        
        // Calculate total bonus amount first, based on original gross pay
        double totalBonusAmount = 0.0;
        if (bonusIds != null) {
            for (Long bonusId : bonusIds) {
                Optional<Bonus> bonus = bonusRepository.findById(bonusId);
                if (bonus.isPresent()) {
                    double bonusAmount = grossPay * bonus.get().getPercentRate() / 100.0;
                    totalBonusAmount += bonusAmount;
                }
            }
        }
        
        // Apply total bonus amount to gross pay
        double increasedGrossPay = grossPay + totalBonusAmount;
        payroll.setGrossPay(increasedGrossPay);
        payroll.setNetPay(increasedGrossPay);
        
        // Apply tax deductions - they reduce the net pay
        if (taxIds != null) {
            for (Long taxId : taxIds) {
                Optional<Tax> tax = taxRepository.findById(taxId);
                tax.ifPresent(t -> {    
                    t.deduct(payroll);
                });
            }
        }
        
        java.util.Date now = new java.util.Date();
        Date sqlDate = new java.sql.Date(now.getTime());

        payroll.setIssueDate(sqlDate);

        payrollRepository.save(payroll);

        return "redirect:/payrolls"; // Redirect to a page listing payrolls
    }

    @GetMapping("/payroll/{id}/edit")
    public String payroll_get_edit(@PathVariable("id") Long id, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if(loggedInUser == null){
            return "redirect:/login";
        }
        if (!userService.hasAccess(loggedInUser, "ADMIN")) {
            model.addAttribute("error", "You don't have permission to access this page");
            return "403";
        }
        Optional<Payroll> optionalPayroll = payrollRepository.findById(id);
        if (optionalPayroll.isPresent()) {
            Payroll payroll = optionalPayroll.get();
            List<PayrollTax> payrollTaxes = payrolltaxRepository.findByPayrollId(payroll.getId()); // Fetch associated PayrollTax records
            List<PayrollBonus> payrollBonuses = payrollbonusRepository.findByPayrollId(payroll.getId());
            model.addAttribute("payroll", payroll);
            model.addAttribute("taxes", taxRepository.findAll());  
            model.addAttribute("bonuses", bonusRepository.findAll());  
            model.addAttribute("payrollTaxes", payrollTaxes); 
            model.addAttribute("payrollBonuses", payrollBonuses);
            return "payroll-edit";
        } else {
            return "redirect:/payrolls"; 
        }
    }
    
    @Transactional
    @PostMapping("/payroll/{id}/edit") 
    public String payroll_post_edit(@PathVariable("id") Long id,
                                    @RequestParam String comments,
                                    @RequestParam(required = false) List<Long> taxIds,
                                    @RequestParam(required = false) List<Long> bonusIds,
                                    @RequestParam Long employeeId,
                                    @RequestParam Double grossPay,
                                    Model model) {
        Payroll payroll = payrollRepository.findById(id).orElseThrow();
        
        if (employeeId == null) {
            model.addAttribute("employeeError", "Employee ID is required");
            return "payrolls-add"; 
        }

        if (!employeeRepository.existsById(employeeId)) {
            model.addAttribute("employeeError", "Employee with this ID does not exist");
            return "payrolls-add"; 
        }

        if(employeeId != null) {
            Employee employee = employeeRepository.findById(employeeId).orElseThrow();
            payroll.setEmployee(employee);
            
        }

        payroll.setComments(comments);
        payroll.delete_all_paytax();
        payroll.delete_all_paybonuses();
        payrolltaxRepository.deleteByPayrollId(payroll.getId()); // Delete all associated PayrollTax records
        payrollbonusRepository.deleteByPayrollId(payroll.getId());
        
        if (taxIds != null) {
            for (Long taxId : taxIds) {
                Optional<Tax> tax = taxRepository.findById(taxId);
                tax.ifPresent(t -> {
                    PayrollTax payrollTax = new PayrollTax(payroll, t);
                    payrolltaxRepository.save(payrollTax);
                });
            }
        }
        
        if (bonusIds != null) {
            for (Long bonusId : bonusIds) {
                Optional<Bonus> bonus = bonusRepository.findById(bonusId);
                bonus.ifPresent(b -> {
                    PayrollBonus payrollBonus = new PayrollBonus(payroll, b);
                    payrollbonusRepository.save(payrollBonus);
                });
            }
        }

        // Set initial gross pay
        payroll.setGrossPay(grossPay);
        net_pay = grossPay;
        
        // Calculate total bonus amount first, based on original gross pay
        double totalBonusAmount = 0.0;
        if (bonusIds != null) {
            for (Long bonusId : bonusIds) {
                Optional<Bonus> bonus = bonusRepository.findById(bonusId);
                if (bonus.isPresent()) {
                    double bonusAmount = grossPay * bonus.get().getPercentRate() / 100.0;
                    totalBonusAmount += bonusAmount;
                }
            }
        }
        
        // Apply total bonus amount to gross pay
        double increasedGrossPay = grossPay + totalBonusAmount;
        payroll.setGrossPay(increasedGrossPay);
        payroll.setNetPay(increasedGrossPay);
        
        // Apply tax deductions - they reduce the net pay
        if (taxIds != null) {
            for (Long taxId : taxIds) {
                Optional<Tax> tax = taxRepository.findById(taxId);
                tax.ifPresent(t -> {    
                    t.deduct(payroll);
                });
            }
        }
    
        payrollRepository.save(payroll);
        return "redirect:/payrolls"; // Redirect to a page listing payrolls
    }

    @PostMapping("/payroll/{id}/delete")
    public String payroll_delete(@PathVariable("id") Long id) {
        payrollRepository.deleteById(id);
        return "redirect:/payrolls"; // Redirect to a page listing payrolls
    }

    @GetMapping("/payrolls/filter")
    public String filterEmployees(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if(loggedInUser == null){
            return "redirect:/login";
        }
        if (!userService.hasAccess(loggedInUser, "ADMIN")) {
            model.addAttribute("error", "You don't have permission to access this page");
            return "403";
        }
        return "payrolls-filter"; 
    }


    @GetMapping("/payrolls/filter-{id}")
    public String payroll_employee(@PathVariable("id") Long id, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if(loggedInUser == null){
            return "redirect:/login";
        }
        
        // Check if user is accessing their own payroll data or has ADMIN role
        boolean isAuthorized = false;
        
        if (loggedInUser.hasRole("ADMIN")) {
            isAuthorized = true;
        } else if (loggedInUser.getEmployee() != null && loggedInUser.getEmployee().getId().equals(id)) {
            isAuthorized = true;
        }
        
        if (!isAuthorized) {
            model.addAttribute("error", "You don't have permission to access this page");
            return "403"; // Access forbidden page
        }
        
        List<Payroll> filteredPayroll = payrollRepository.findByEmployeeId(id);
        model.addAttribute("filteredPayrolls", filteredPayroll);
        return "payrolls-filter"; 
    }

    @PostMapping("/payrolls/filter")
    public String filterEmployees(@RequestParam String filterBy, 
                                @RequestParam(required = false) Double filterValue, 
                                Model model) {

        
        if (filterValue == null) {
            Iterable<Payroll> filteredPayroll = payrollRepository.findAll();
            model.addAttribute("filteredPayrolls", filteredPayroll);
            return "payrolls-filter"; 
        }

        Long newFilterValue;
        List<Payroll> filteredPayroll = new ArrayList<>();
        switch (filterBy) {
            case "id":
                newFilterValue = filterValue.longValue();
                Optional<Payroll> payrolls = payrollRepository.findById(newFilterValue);
                payrolls.ifPresent(filteredPayroll::add); // Add if found
                break;
            case "employeeId":
                newFilterValue = filterValue.longValue();

                filteredPayroll = payrollRepository.findByEmployeeId(newFilterValue);
                break;
            case "netPay":
                filteredPayroll = payrollRepository.findByNetPay(filterValue);
                break;
            case "numTax":
                newFilterValue = filterValue.longValue();

                if (newFilterValue < 0) {
                    model.addAttribute("error", "Please enter a positive value");
                    return "payrolls-filter"; 
                }
                List<Payroll> filteredPayrolls = StreamSupport.stream(payrollRepository.findAll().spliterator(), false)
                                        .filter(p -> p.getPaytax().size() >= newFilterValue)
                                        .collect(Collectors.toList());
                
                filteredPayroll = filteredPayrolls;
        }
        model.addAttribute("filteredPayrolls", filteredPayroll);
        return "payrolls-filter"; 
    

    }

    @GetMapping("/payrolls/summary")
    public String payroll_summary(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if(loggedInUser == null){
            return "redirect:/login";
        }
        if (!userService.hasAccess(loggedInUser, "ADMIN")) {
            model.addAttribute("error", "You don't have permission to access this page");
            return "403";
        }
        return "payrolls-summary"; 
    }

    @PostMapping("/payrolls/summary")
    public String filterPayrollsByTaxCount(@RequestParam(required = false) Integer taxCount, Model model) {

        if (taxCount == null || taxCount < 0) {
            model.addAttribute("error", "Please enter a positive value");
            return "payrolls-summary"; 
        }

        List<Payroll> filteredPayrolls = StreamSupport.stream(payrollRepository.findAll().spliterator(), false)
                                        .filter(p -> p.getPaytax().size() == taxCount)
                                        .collect(Collectors.toList());

        model.addAttribute("payrollRepositorySize", payrollRepository.count());
        model.addAttribute("filteredPayrolls", filteredPayrolls); 
        model.addAttribute("taxCount", taxCount); 
        return "payrolls-summary"; // Name of your view file
    }
}
