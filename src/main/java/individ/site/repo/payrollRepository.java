package individ.site.repo;

import java.util.List;


import org.springframework.data.repository.CrudRepository;

import individ.site.models.Employee;
import individ.site.models.Payroll;

public interface payrollRepository extends CrudRepository<Payroll, Long>{
    List<Payroll> findByEmployeeId(Long employeeId);
    List<Payroll> findByNetPay(Double id);
    List<Payroll> findAllByOrderById();
    List<Payroll> findAllByOrderByGrossPayDesc();
    List<Payroll> findAllByOrderByNetPayDesc();
    List<Payroll> findAllByOrderByEmployeeDesc();
    List<Payroll> findAllByOrderByPaytaxDesc();
    List<Payroll> findByEmployee(Employee employee);
}


