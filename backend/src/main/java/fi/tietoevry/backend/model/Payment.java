package fi.tietoevry.backend.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import fi.tietoevry.backend.model.Payment.PaymentId;

@Entity
@IdClass(PaymentId.class) // Specify that our entity has a composite primary key that is implemented by PaymentId class
@Table(name = "payments") // Name of the table in database is actually "payments"
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // We don't need these Hibernate guys in the JSON output
public class Payment {

    @Id // part of the composite primary key
    @ManyToOne(fetch = FetchType.LAZY) // Many Payments were paid by One Customer; LAZY - don't load Customer data straight away, only when explicitly requested
    @JoinColumn(name = "customerNumber") // we need to specify how we map the object entity
    @JsonIgnore // No need to show the object in the JSON string
    private Customer customerNumber;

    @Id // part of the composite primary key
    @Column(length = 50)
    private String checkNumber;

    private Date paymentDate;

    @Column(precision = 10, scale = 2)
    private BigDecimal amount;

    public Payment() {
    }

    public Customer getCustomerNumber() {
        return customerNumber;
    }

    @JsonGetter("customerNumber") // For Jackson serialization (creation of a JSON-string) show this key and value from the methods return
    public Long getJsonCustomerNumber() {
        return Objects.nonNull(customerNumber) ? customerNumber.getCustomerNumber() : null;
    }

    public void setCustomerNumber(Customer customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(String checkNumber) {
        this.checkNumber = checkNumber;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "customerNumber=" + customerNumber +
                ", checkNumber='" + checkNumber + '\'' +
                ", paymentDate=" + paymentDate +
                ", amount=" + amount +
                '}';
    }

    /*
        Here PaymentId is an inner static class to ensure that Hibernate can actually create an instance of PaymentId object.
        However, the class can be defined independently (not as an inner class).
        An independent class for describing a composite key does not need to be static!
        Still, all composite key classes must:
         - be public;
         - have a no-argument constructor;
         - implement Serializable;
         - define equals() and hashCode();
         */
    public static class PaymentId implements Serializable {
        private Customer customerNumber;
        private String checkNumber;

        public PaymentId() {
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            PaymentId paymentId = (PaymentId) o;
            return Objects.equals(customerNumber, paymentId.customerNumber) &&
                    Objects.equals(checkNumber, paymentId.checkNumber);
        }

        @Override
        public int hashCode() {
            return Objects.hash(customerNumber, checkNumber);
        }
    }
}
