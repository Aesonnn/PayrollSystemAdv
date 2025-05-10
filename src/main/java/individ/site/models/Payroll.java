package individ.site.models;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;
import java.sql.Date;
import java.util.ArrayList;
import jakarta.persistence.OneToMany;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Payroll {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "comments")
    private String comments;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.SET_NULL) // investigate this
    @JoinColumn(name = "employee_id", nullable = true)
    private Employee employee;

    @Column(name = "gross_pay", nullable = true)
    private Double grossPay;

    @Column(name = "net_pay", nullable = true)
    private Double netPay;

    @Column(name = "Issue date", nullable = true)
    private Date issueDate;

    @OneToMany(mappedBy = "payroll")
    private List<PayrollTax> paytax = new ArrayList<>();

    @OneToMany(mappedBy = "payroll")
    private List<PayrollBonus> paybonuses = new ArrayList<>();

    public void delete_all_paytax() {
        paytax.clear();
    }

    public void delete_all_paybonuses() {
        paybonuses.clear();
    }

    // Constructor
    public Payroll() {
    }

    public Payroll(String comments, Employee employee) {
        this.comments = comments;
        this.employee = employee;
        this.grossPay = 0.;
        this.netPay = 0.;
    }

    public Payroll(String comments, Employee employee, Double grossPay, Double netPay) {
        this.comments = comments;
        this.employee = employee;
        this.grossPay = grossPay;
        this.netPay = netPay;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public String getComments() {
        return comments;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Double getGrossPay() {
        return grossPay;
    }

    public void setGrossPay(Double grossPay) {
        this.grossPay = grossPay;
    }

    public Double getNetPay() {
        return netPay;
    }

    public void setNetPay(Double netPay) {
        this.netPay = netPay;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public List<PayrollTax> getPaytax() {
        return paytax;
    }

    public void setPaytax(PayrollTax pt) {
        if (pt.getPayroll() != null) {
            pt.getPayroll().getPaytax().remove(pt);
        }

        pt.setPayroll(this);
        paytax.add(pt);
    }

    public List<PayrollBonus> getPaybonuses() {
        return paybonuses;
    }

    public void setPaybonus(PayrollBonus pb) {
        if (pb.getPayroll() != null) {
            pb.getPayroll().getPaybonuses().remove(pb);
        }

        pb.setPayroll(this);
        paybonuses.add(pb);
    }
}
