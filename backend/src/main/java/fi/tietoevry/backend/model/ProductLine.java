package fi.tietoevry.backend.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "productlines") // Name of the table in database is actually "productlines"
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // We don't need these Hibernate guys in the JSON output
public class ProductLine {

    @Id
    private String productLine;
    private String textDescription;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String htmlDescription;

    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] image;

    public ProductLine() {
    }

    public String getProductLine() {
        return productLine;
    }

    public void setProductLine(String productLine) {
        this.productLine = productLine;
    }

    public String getTextDescription() {
        return textDescription;
    }

    public void setTextDescription(String textDescription) {
        this.textDescription = textDescription;
    }

    public String getHtmlDescription() {
        return htmlDescription;
    }

    public void setHtmlDescription(String htmlDescription) {
        this.htmlDescription = htmlDescription;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "ProductLine{" +
                "productLine='" + productLine + '\'' +
                ", textDescription='" + textDescription + '\'' +
                ", htmlDescription='" + htmlDescription + '\'' +
                ", image (size)=" + image.length + " byte" +
                '}';
    }
}
