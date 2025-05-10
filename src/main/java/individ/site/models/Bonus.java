package individ.site.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
public class Bonus implements Benefit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "percent_rate", nullable = false)
    private Double percentRate;

    @Column(name = "bonus_name", nullable = false)
    private String bonusName;

    @Column(name = "description", nullable = true)
    private String description;

    @Override
    public void apply(Payroll payroll) {
        BigDecimal grossPay = BigDecimal.valueOf(payroll.getGrossPay());
        BigDecimal percentRate = BigDecimal.valueOf(this.percentRate);
        
        BigDecimal bonusAmount = grossPay.multiply(percentRate)
                                .divide(BigDecimal.valueOf(100.0))
                                .setScale(2, RoundingMode.HALF_UP);
        
        double finalBonusAmount = bonusAmount.doubleValue();
        // This method won't be used anymore as we calculate all bonuses in the controller
    }

    public Bonus() {
    }
    
    public Bonus(Double percentRate, String name, String description) {
        this.percentRate = percentRate;
        this.bonusName = name;
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

    public String getBonusName() {
        return bonusName;
    }

    public void setBonusName(String bonusName) {
        this.bonusName = bonusName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}
