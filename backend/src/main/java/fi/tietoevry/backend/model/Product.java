package fi.tietoevry.backend.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "products") // Name of the table in database is actually "products"
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // We don't need these Hibernate guys in the JSON output
public class Product {

    @Id
    @Column(length = 15)
    private String productCode;

    @Column(length = 70)
    private String productName;

    @ManyToOne(fetch = FetchType.LAZY) // Many Products belong to One ProductLine; LAZY - don't load ProductLine data straight away, only when explicitly requested
    @JoinColumn(name = "productLine") // we need to specify how we map the object entity
    @JsonIgnore // No need to show the object in the JSON string
    private ProductLine productLine;

    @Column(length = 10)
    private String productScale;

    @Column(length = 50)
    private String productVendor;

    @Column(columnDefinition = "TEXT")
    private String productDescription;

    @Column(columnDefinition = "SMALLINT")
    private Integer quantityInStock;

    @Column(precision = 10, scale = 2)
    private BigDecimal buyPrice;

    @Column(name = "MSRP", precision = 10, scale = 2)
    private BigDecimal msrp;

    public Product() {
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public ProductLine getProductLine() {
        return productLine;
    }

    @JsonGetter("productLine") // For Jackson serialization (creation of a JSON-string) show this key and value from the methods return
    public String getJsonProductLine() {
        return productLine.getProductLine();
    }

    public void setProductLine(ProductLine productLine) {
        this.productLine = productLine;
    }

    public String getProductScale() {
        return productScale;
    }

    public void setProductScale(String productScale) {
        this.productScale = productScale;
    }

    public String getProductVendor() {
        return productVendor;
    }

    public void setProductVendor(String productVendor) {
        this.productVendor = productVendor;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public Integer getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(Integer quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

    public BigDecimal getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(BigDecimal buyPrice) {
        this.buyPrice = buyPrice;
    }

    public BigDecimal getMsrp() {
        return msrp;
    }

    public void setMsrp(BigDecimal msrp) {
        this.msrp = msrp;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productCode='" + productCode + '\'' +
                ", productName='" + productName + '\'' +
                ", productLine=" + productLine +
                ", productScale='" + productScale + '\'' +
                ", productVendor='" + productVendor + '\'' +
                ", productDescription='" + productDescription + '\'' +
                ", quantityInStock=" + quantityInStock +
                ", buyPrice=" + buyPrice +
                ", msrp=" + msrp +
                '}';
    }
}
