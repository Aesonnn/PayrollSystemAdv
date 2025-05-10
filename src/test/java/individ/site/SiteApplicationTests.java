package individ.site;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

import static org.mockito.Mockito.doAnswer;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.Model;


import individ.site.models.Employee;
import individ.site.models.Payroll;
import individ.site.models.Tax;
import individ.site.repo.employeeRepository;
import individ.site.repo.payrollRepository;
import individ.site.repo.taxRepository;
import individ.site.repo.payrolltaxRepository;
import individ.site.controllers.EmployeeController;
import individ.site.controllers.MainController;
import individ.site.controllers.PayrollController;
import static org.assertj.core.api.Assertions.assertThat;


// @SpringBootTest
// @ExtendWith(MockitoExtension.class)
// class SiteApplicationTests {

// 	// @Test
// 	// void contextLoads() {
// 	// }

// 	@Mock
//     private Model model;

// 	@Mock
//     private employeeRepository employeeRepository;

//     @Mock
//     private payrollRepository payrollRepository;

// 	@Mock
// 	private payrolltaxRepository payrolltaxRepository;

//     @Mock
//     private taxRepository taxRepository;

//     @InjectMocks
//     private PayrollController payrollController;

// 	@InjectMocks
// 	private EmployeeController employeeController;

// 	@Autowired
// 	private MainController controller;

// 	@Test
// 	void contextLoads() throws Exception {
// 		assertThat(controller).isNotNull();
// 	}

// 	@Test 
//     public void shouldCheckPayrollNetPay() {
//         // Test data
//         double grossPay = 1000.0; 
//         double taxRate = 10.0; 

//         Employee employee = new Employee(); 
//         employee.setId(1L); 

//         Tax tax = new Tax(taxRate, "Tax A", "");

//         // Mocking behavior
//         when(employeeRepository.existsById(anyLong())).thenReturn(true); 
//         when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(employee)); 
//         when(taxRepository.findById(anyLong())).thenReturn(Optional.of(tax));

//         // Simulate payroll saving
//         List<Payroll> savedPayrolls = new ArrayList<>(); 
//         doAnswer(invocation -> { 
//             Payroll savedPayroll = invocation.getArgument(0);
//             savedPayrolls.add(savedPayroll); 
//             return savedPayroll; 
//         }).when(payrollRepository).save(any(Payroll.class)); 

//         // Call the controller method
//         payrollController.payroll_post_add("Testpayroll", Collections.singletonList(1L), Collections.singletonList(1L), employee.getId(), grossPay, null);

//         // Assertions
// 		// We keep the size as 2 since adding a payroll requires saving it twice
// 		// which is not a problem, since on the second save, the payroll is already
// 		// in the database and the save method will just update it. 
// 		// But it causes the savedPayrolls list to have 2 elements.
//         assertThat(savedPayrolls).hasSize(2); 
//         Payroll savedPayroll = savedPayrolls.get(0);

//         double expectedNetPay = grossPay * (1 - (taxRate / 100));
//         assertThat(savedPayroll.getNetPay()).isEqualTo(expectedNetPay);
//     }

// }

