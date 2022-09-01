package fi.tietoevry.backend.model;

import java.util.Date;
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
@Table(name = "orders") // Name of the table in database is actually "orders"
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // We don't need these Hibernate guys in the JSON output
public class Order {
    @Id
    @GeneratedValue
    private Long orderNumber;

    private Date orderDate;

    private Date requiredDate;

    private Date shippedDate;

    @Column(length = 15)
    private String status;

    @Column(columnDefinition = "TEXT")
    private String comments;

    @ManyToOne(fetch = FetchType.LAZY) // Many Orders were placed by One Customer; LAZY - don't load Customer data straight away, only when explicitly requested
    @JoinColumn(name = "customerNumber") // we need to specify how we map the object entity
    @JsonIgnore // No need to show the object in the JSON string
    private Customer customer;

    public Order() {
    }

    public Long getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Long orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Date getRequiredDate() {
        return requiredDate;
    }

    public void setRequiredDate(Date requiredDate) {
        this.requiredDate = requiredDate;
    }

    public Date getShippedDate() {
        return shippedDate;
    }

    public void setShippedDate(Date shippedDate) {
        this.shippedDate = shippedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Customer getCustomer() {
        return customer;
    }

    @JsonGetter("customerNumber") // For Jackson serialization (creation of a JSON-string) show this key and value from the methods return
    public Long getJsonCustomerNumber() {
        return Objects.nonNull(customer) ? customer.getCustomerNumber() : null;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderNumber=" + orderNumber +
                ", orderDate=" + orderDate +
                ", requiredDate=" + requiredDate +
                ", shippedDate=" + shippedDate +
                ", status='" + status + '\'' +
                ", comments='" + comments + '\'' +
                ", customer=" + customer +
                '}';
    }
}
