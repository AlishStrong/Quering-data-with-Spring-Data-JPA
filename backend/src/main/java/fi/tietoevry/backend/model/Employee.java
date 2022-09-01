package fi.tietoevry.backend.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "employees") // Name of the table in database is actually "employees"
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // We don't need these Hibernate guys in the JSON output
public class Employee {
    @Id
    @GeneratedValue
    private Long employeeNumber;

    @Column(length = 50)
    private String lastName;

    @Column(length = 50)
    private String firstName;

    @Column(length = 10)
    private String extension;

    @Column(length = 100)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY) // Many Employees belong to One Office; LAZY - don't load Office data straight away, only when explicitly requested
    @JoinColumn(name="officeCode") // We have ManyToOne, so name value must be Employee.officeCode
    @JsonIgnore // No need to show the object in the JSON string
    private Office officeCode;

    @ManyToOne(fetch = FetchType.LAZY) // Many Employees report to One Employee-manager; LAZY - don't load Employee-manager data straight away, only when explicitly requested
    @JoinColumn(name="reportsTo")
    @JsonIgnore // No need to show the object in the JSON string
    private Employee reportsTo;

    @Column(length = 50)
    private String jobTitle;

    public Employee() {}

    public Long getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(Long employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Office getOfficeCode() {
        return officeCode;
    }

    @JsonGetter("officeCode") // For Jackson serialization (creation of a JSON-string) show this key and value from the methods return
    public String getJsonOfficeCode() {
        return Objects.nonNull(officeCode) ? officeCode.getOfficeCode() : null;
    }

    public void setOfficeCode(Office officeCode) {
        this.officeCode = officeCode;
    }

    public Employee getReportsTo() {
        return reportsTo;
    }

    @JsonGetter("officeCode") // For Jackson serialization (creation of a JSON-string) show this key and value from the methods return
    public Long getJsonReportsTo() {
        return Objects.nonNull(reportsTo) ? reportsTo.employeeNumber : null;
    }

    public void setReportsTo(Employee reportsTo) {
        this.reportsTo = reportsTo;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "employeeNumber=" + employeeNumber +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", extension='" + extension + '\'' +
                ", email='" + email + '\'' +
                ", officeCode='" + officeCode + '\'' +
                ", reportsTo=" + reportsTo +
                ", jobTitle='" + jobTitle + '\'' +
                '}';
    }
}
