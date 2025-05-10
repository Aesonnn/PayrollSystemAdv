package individ.site.repo;

import org.springframework.data.repository.CrudRepository;
import individ.site.models.PayrollTax;
import java.util.List;

public interface payrolltaxRepository extends CrudRepository<PayrollTax, Long>{
    List<PayrollTax> findByPayrollId(Long payrollId);
    List<PayrollTax> findByTaxId(Long taxId);
    List<PayrollTax> deleteByPayrollId(Long id);
}
