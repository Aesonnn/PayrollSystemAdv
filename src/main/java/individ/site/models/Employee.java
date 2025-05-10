package individ.site.models;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Employee extends Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL) // investigate this
    @JsonBackReference
    private Department department;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.SET_NULL) // investigate this
    @JoinColumn(name = "position_id", nullable = true)
    private Position position;

    @OneToMany(mappedBy = "employee")
    private List<Payroll> payrolls = new ArrayList<>();


    @Column(name = "dob", nullable = false)
    private Date dob;
    
    @Column(name = "email", nullable = true)
    private String email;
    

    public Employee() {
    }

    public Employee(String firstName, String lastName, Date dob, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.email = email;
        this.department = null;
        this.position = null;
    }

    public Employee(String firstName, String lastName, Date dob, String email, Department department, Position position) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.email = email;
        this.department = department;
        this.position = position;
    }

    // Getters and setters

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Department getDepartment() {
        return department;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public void setPayroll(Payroll payroll) {
        if (payroll.getEmployee() != null) {
            payroll.getEmployee().getPayrolls().remove(payroll);
        }
        payroll.setEmployee(this);
        payrolls.add(payroll);
    }

    public List<Payroll> getPayrolls() {
        return payrolls;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
