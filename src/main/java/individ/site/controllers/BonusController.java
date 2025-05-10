package individ.site.controllers;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import individ.site.models.Bonus;
import individ.site.repo.bonusRepository;
import individ.site.service.UserService;

@Controller
public class BonusController {
    
    @Autowired
    private bonusRepository bonusRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/bonuses/list") 
    @ResponseBody
    public List<Bonus> listBonuses() {
        return StreamSupport.stream(bonusRepository.findAll().spliterator(), false)
                        .collect(Collectors.toList());
    }

    @PostMapping("/bonuses/add-ajax")
    @ResponseBody
    public Bonus bonus_add_ajax(@RequestParam Double percentRate, 
                              @RequestParam String bonusName,
                              @RequestParam(required = false) String description) {
        // Validate percentage rate
        if (percentRate < 0 || percentRate > 100) {
            throw new IllegalArgumentException("Percentage rate must be between 0 and 100");
        }
        
        Bonus bonus = new Bonus(percentRate, bonusName, description);
        bonusRepository.save(bonus);
        return bonus;
    }

    @PostMapping("/bonuses/delete-ajax/{id}")
    @ResponseBody
    public Map<String, String> bonus_delete_ajax(@PathVariable(value = "id") long bonusId) {
        Map<String, String> response = new HashMap<>();
        
        try {
            if (bonusRepository.existsById(bonusId)) {
                bonusRepository.deleteById(bonusId);
                response.put("status", "success");
                response.put("message", "Bonus deleted successfully");
            } else {
                response.put("status", "error");
                response.put("message", "Bonus not found");
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error deleting bonus: " + e.getMessage());
        }
        
        return response;
    }
}
