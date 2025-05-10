package individ.site.models;
// import java.util.List;
// import java.util.ArrayList;
// import jakarta.persistence.OneToMany;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
public class Tax implements Deduction{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "percent_rate", nullable = false)
    private Double percentRate;

    @Column(name = "tax_name", nullable = false)
    private String taxName;

    @Column(name = "description", nullable = true)
    private String description;

    @Override
    public void deduct(Payroll payroll) {
        BigDecimal grossPay = BigDecimal.valueOf(payroll.getGrossPay());

        BigDecimal percentRate = BigDecimal.valueOf(this.percentRate);

        BigDecimal deductionAmount = grossPay.multiply(percentRate)
                                    .divide(BigDecimal.valueOf(100.0))
                                    .setScale(2, RoundingMode.HALF_UP);

        double finalDeductionAmount = deductionAmount.doubleValue();
        payroll.setNetPay(payroll.getGrossPay() - finalDeductionAmount);
    }

    public Tax() {
    }
    
    public Tax(Double percentRate, String name, String description) {
        this.percentRate = percentRate;
        this.taxName = name;
        this.description = description;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Double getPercentRate() {
        return percentRate;
    }
    
    public void setPercentRate(Double percentRate) {
        this.percentRate = percentRate;
    }

    public String getTaxName() {
        return taxName;
    }

    public void setTaxName(String taxName) {
        this.taxName = taxName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}
