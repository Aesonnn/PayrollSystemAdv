package individ.site.repo;

import org.springframework.data.repository.CrudRepository;
import individ.site.models.PayrollBonus;
import java.util.List;

public interface payrollbonusRepository extends CrudRepository<PayrollBonus, Long>{
    List<PayrollBonus> findByPayrollId(Long payrollId);
    List<PayrollBonus> findByBonusId(Long bonusId);
    List<PayrollBonus> deleteByPayrollId(Long id);
}
