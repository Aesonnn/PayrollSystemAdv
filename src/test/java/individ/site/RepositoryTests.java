package individ.site;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import individ.site.models.Employee;
import individ.site.repo.employeeRepository;
import static org.assertj.core.api.Assertions.assertThat;
import java.sql.Date;
import static org.junit.jupiter.api.Assertions.*;


// @DataJpaTest
// public class RepositoryTests {
    
//     private employeeRepository employeeRepository;

//     @Autowired
//     public void setEmployeeRepository(employeeRepository employeeRepository) {
//         this.employeeRepository = employeeRepository;
//     }

//     @Test
//     public void EmployeeRepository_Save() {
//         Date sqlDate = java.sql.Date.valueOf("2022-01-01");
//         Employee employee = new Employee("John", "Doe", sqlDate, "mail");
        
//         Employee savedEmployee = employeeRepository.save(employee);
//         assertThat(savedEmployee).isNotNull();
//         assertThat(savedEmployee.getId()).isGreaterThan(0);
//     }

//     @Test
// 	public void Save_Employee(){

// 		Employee emp1 = new Employee("UnitTest", "Test", null, "mail");
// 		emp1.setDob(new Date(System.currentTimeMillis()));
		

// 		Employee saved_Employee = employeeRepository.save(emp1);

// 		assertNotNull(saved_Employee);

// 		employeeRepository.delete(saved_Employee); // Cleaning up after testing

// 	}

// 	@Test
// 	public void Repository_Find(){
// 		Employee emp1 = new Employee("John", "Test", null, "mail");
// 		Employee emp2 = new Employee("Lukas", "Unit", null, "mail");
// 		emp1.setDob(new Date(System.currentTimeMillis()));
// 		emp2.setDob(new Date(System.currentTimeMillis()));
// 		employeeRepository.save(emp1);
// 		employeeRepository.save(emp2);

// 		Iterable<Employee> employeeList = employeeRepository.findAll();
//     	ArrayList<Employee> res = new ArrayList<>();
// 		employeeList.forEach(res::add);

// 		assertThat(res).isNotNull();
// 		assertThat(res.size()).isEqualTo(2);

// 		employeeRepository.delete(emp1);
// 		employeeRepository.delete(emp2);
// 	}

// 	@Test
// 	public void Repository_Delete() {
// 		Employee emp1 = new Employee("John", "Test", null, "mail");
// 		emp1.setDob(new Date(System.currentTimeMillis()));
// 		Employee saved_Employee = employeeRepository.save(emp1);
// 		employeeRepository.delete(saved_Employee);

// 		Employee result = employeeRepository.findById(saved_Employee.getId()).orElse(null);
// 		assertNull(result);
	
// 	}



// 	@Test
// 	public void findByFirstNameContaining() {
// 		// Test data
// 		String firstName = "John";
// 		Employee emp1 = new Employee(firstName, "Test", null, "mail");
// 		emp1.setDob(new Date(System.currentTimeMillis()));
// 		employeeRepository.save(emp1);

// 		// Call the repository method
// 		List<Employee> result = employeeRepository.findByFirstNameContaining(firstName);

// 		// Assertions
// 		assertThat(result).isNotNull();
// 		assertThat(result.size()).isEqualTo(1);
// 		assertThat(result.get(0).getFirstName()).isEqualTo(firstName);

// 		employeeRepository.delete(emp1);

		
// 	}
// }
