package individ.site.dto;

import java.sql.Date;
import individ.site.models.Employee;

public class EmployeeDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private Date dob;
    private String email;
    
    // Constructor that converts from Entity to DTO
    public EmployeeDTO(Employee employee) {
        this.id = employee.getId();
        this.firstName = employee.getFirstName();
        this.lastName = employee.getLastName();
        this.dob = employee.getDob();
        this.email = employee.getEmail();
    }
    
    // Getters
    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public Date getDob() { return dob; }
    public String getEmail() { return email; }
}
