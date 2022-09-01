package fi.tietoevry.backend.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import fi.tietoevry.backend.model.OrderDetail.OrderDetailId;

@Entity
@Table(name = "orderdetails") // Name of the table in database is actually "orderdetails"
@IdClass(OrderDetailId.class) // Specify that our entity has a composite primary key that is implemented by OrderDetailId class
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // We don't need these Hibernate guys in the JSON output
public class OrderDetail {

    @Id // part of the composite primary key
    private Long orderNumber;

    @Id // part of the composite primary key
    private String productCode;

    private Long quantityOrdered;

    @Column(precision = 10, scale = 2)
    private BigDecimal priceEach;

    @Column(columnDefinition = "SMALLINT")
    private Integer orderLineNumber;

    @OneToOne(fetch = FetchType.LAZY) // One OrderDetail belongs to One Order; LAZY - don't load Order data straight away, only when explicitly requested
    @MapsId("orderNumber") // OrderDetail has a composite primary key, that is we need to indicate to which part of the key this field maps
    @JoinColumn(name = "orderNumber") // we need to specify how we map the object entity
    @JsonIgnore // No need to show the object in the JSON string
    private Order order;

    @OneToOne(fetch = FetchType.LAZY) // One OrderDetail belongs to One Product; LAZY - don't load Product data straight away, only when explicitly requested
    @MapsId("productCode") // OrderDetail has a composite primary key, that is we need to indicate to which part of the key this field maps
    @JoinColumn(name = "productCode") // we need to specify how we map the object entity
    @JsonIgnore // No need to show the object in the JSON string
    private Product product;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public OrderDetail() {
    }

    public Long getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Long orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public Long getQuantityOrdered() {
        return quantityOrdered;
    }

    public void setQuantityOrdered(Long quantityOrdered) {
        this.quantityOrdered = quantityOrdered;
    }

    public BigDecimal getPriceEach() {
        return priceEach;
    }

    public void setPriceEach(BigDecimal priceEach) {
        this.priceEach = priceEach;
    }

    public Integer getOrderLineNumber() {
        return orderLineNumber;
    }

    public void setOrderLineNumber(Integer orderLineNumber) {
        this.orderLineNumber = orderLineNumber;
    }

    @Override
    public String toString() {
        return "OrderDetail{" +
                "orderNumber=" + orderNumber +
                ", productCode='" + productCode + '\'' +
                ", quantityOrdered=" + quantityOrdered +
                ", priceEach=" + priceEach +
                ", orderLineNumber=" + orderLineNumber +
                ", order=" + order +
                ", product=" + product +
                '}';
    }

    /*
    Here OrderDetailId is an inner static class to ensure that Hibernate can actually create an instance of OrderDetailId object.
    However, the class can be defined independently (not as an inner class).
    An independent class for describing a composite key does not need to be static!
    Still, all composite key classes must:
     - be public;
     - have a no-argument constructor;
     - implement Serializable;
     - define equals() and hashCode();
    */
    public static class OrderDetailId implements Serializable {
        private Long orderNumber;
        private String productCode;

        public OrderDetailId() {
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            OrderDetailId that = (OrderDetailId) o;
            return Objects.equals(orderNumber, that.orderNumber) &&
                    Objects.equals(productCode, that.productCode);
        }

        @Override
        public int hashCode() {
            return Objects.hash(orderNumber, productCode);
        }
    }

}
