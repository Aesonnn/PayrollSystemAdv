package individ.site;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import individ.site.repo.employeeRepository;
import individ.site.controllers.EmployeeController;
import individ.site.models.Employee;
import java.sql.Date;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;


// @SpringBootTest
// @AutoConfigureMockMvc(addFilters = false)
// public class EmployeeControllerTest {
    
//     @Autowired
//     private MockMvc mockMvc;

//     @Autowired
//     private employeeRepository employeeRepository;

//     @InjectMocks 
//     private EmployeeController employeeController;


//     @Test
//     public void EmployeeController_CreateEmployee() throws Exception {
//     String fname = "John";
//     String lname = "Doe";
//     String dob = "2022-01-01";
//     String email = "john.doe@example.com";

//     // Act & Assert
//     mockMvc.perform(post("/employees/add")
//             .param("fname", fname)
//             .param("lname", lname)
//             .param("dob", dob)
//             .param("email", email))
//             .andExpect(status().is3xxRedirection())
//             .andExpect(redirectedUrl("/employees"));
//     }

//     @Test
//     void testFilterByFirstName() throws Exception {
//         // Sample employees
//         String dob = "2022-01-01";
//         String email = "john.doe@example.com";

//         Employee employee1 = new Employee("Jane", "Doe", Date.valueOf(dob), email);

//         employeeRepository.save(employee1);

//         // Perform the simulated POST request
//         mockMvc.perform(post("/employees/filter")
//                     .param("filterBy", "firstName")
//                     .param("keyword", "Ja"))
//             .andExpect(status().isOk())  // Expect HTTP 200
//             .andExpect(view().name("employees-filter"))
//             // Indicate that the model attribute "filteredEmployees" is present and not empty
//             // Hence, the filtered employees are displayed
//             .andExpect(model().attribute("filteredEmployees", hasSize(greaterThan(0))));
//             employeeRepository.delete(employee1);
//     }


// }
