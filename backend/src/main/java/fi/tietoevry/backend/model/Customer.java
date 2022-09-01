package fi.tietoevry.backend.model;

import java.math.BigDecimal;
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
@Table(name = "customers") // Name of the table in database is actually "customers"
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // We don't need these Hibernate guys in the JSON output
public class Customer {

    @Id
    @GeneratedValue
    private Long customerNumber;

    @Column(length = 50)
    private String customerName;

    @Column(length = 50)
    private String contactLastName;

    @Column(length = 50)
    private String contactFirstName;

    @Column(length = 50)
    private String phone;

    @Column(name = "addressLine1", length = 50)
    private String addressLineOne;

    @Column(name = "addressLine2", length = 50)
    private String addressLineTwo;

    @Column(length = 50)
    private String city;

    @Column(length = 50)
    private String state;

    @Column(length = 15)
    private String postalCode;

    @Column(length = 50)
    private String country;

    @ManyToOne(fetch = FetchType.LAZY) // Many Customers are represented by One sales-Employee; LAZY - don't load Employee data straight away, only when explicitly requested
    @JoinColumn(name = "salesRepEmployeeNumber") // We have ManyToOne, so name value must be Customer.salesRepEmployeeNumber
    @JsonIgnore // No need to show the object in the JSON string
    private Employee salesRepEmployeeNumber;

    @Column(precision = 10, scale = 2)
    private BigDecimal creditLimit;

    public Customer() {
    }

    public Long getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(Long customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getContactLastName() {
        return contactLastName;
    }

    public void setContactLastName(String contactLastName) {
        this.contactLastName = contactLastName;
    }

    public String getContactFirstName() {
        return contactFirstName;
    }

    public void setContactFirstName(String contactFirstName) {
        this.contactFirstName = contactFirstName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddressLineOne() {
        return addressLineOne;
    }

    public void setAddressLineOne(String addressLineOne) {
        this.addressLineOne = addressLineOne;
    }

    public String getAddressLineTwo() {
        return addressLineTwo;
    }

    public void setAddressLineTwo(String addressLineTwo) {
        this.addressLineTwo = addressLineTwo;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Employee getSalesRepEmployeeNumber() {
        return salesRepEmployeeNumber;
    }

    @JsonGetter("salesRepEmployeeNumber") // For Jackson serialization (creation of a JSON-string) show this key and value from the methods return
    public Long getJsonSalesRepEmployeeNumber() {
        return Objects.nonNull(salesRepEmployeeNumber) ? salesRepEmployeeNumber.getEmployeeNumber() : null;
    }

    public void setSalesRepEmployeeNumber(Employee salesRepEmployeeNumber) {
        this.salesRepEmployeeNumber = salesRepEmployeeNumber;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerNumber=" + customerNumber +
                ", customerName='" + customerName + '\'' +
                ", contactLastName='" + contactLastName + '\'' +
                ", contactFirstName='" + contactFirstName + '\'' +
                ", phone='" + phone + '\'' +
                ", addressLineOne='" + addressLineOne + '\'' +
                ", addressLineTwo='" + addressLineTwo + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", country='" + country + '\'' +
                ", salesRepEmployeeNumber=" + salesRepEmployeeNumber +
                ", creditLimit=" + creditLimit +
                '}';
    }
}
