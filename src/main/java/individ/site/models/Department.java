package individ.site.models;

import java.util.List;
import java.util.ArrayList;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class Department {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "department_id")
    private Long id;
    
    @Column(nullable = false, name = "title")
    private String title;

    @Column(name = "description", nullable = true)
    private String description;

    @OneToMany(mappedBy = "department")
    @JsonManagedReference
    private List<Employee> employees = new ArrayList<>();

    // Constructors
    public Department() {
    }

    public Department(String title, String desc) {
        this.title = title;
        this.description = desc;
    }

    // Getters and setters
    public void setEmployee(Employee employee) {
        if (employee.getDepartment() != null) {
            employee.getDepartment().getEmployees().remove(employee);
        }
        employee.setDepartment(this);
        employees.add(employee);
    }

    public List<Employee> getEmployees() {
        return employees;
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String desc) {
        this.description = desc;
    }

}
