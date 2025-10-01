package com.project.code.Model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "product", uniqueConstraints = @UniqueConstraint(columnNames = "sku"))
public class Product {

    // 1. Add 'id' field:
    // - Type: private long
    // - This field will be auto-incremented.
    // - Use @Id to mark it as the primary key.
    // - Use @GeneratedValue(strategy = GenerationType.IDENTITY) to auto-increment
    // it.\
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 2. Add 'name' field:
    // - Type: private String
    // - This field cannot be empty, use the @NotNull annotation to enforce this
    // rule.
    @NotNull(message = "Name cannot be null")
    private String name;

    // 3. Add 'category' field:
    // - Type: private String
    // - This field cannot be empty, use the @NotNull annotation to enforce this
    // rule.
    @NotNull(message = "Category cannot be null")
    private String category;

    // 4. Add 'price' field:
    // - Type: private Double
    // - This field cannot be empty, use the @NotNull annotation to enforce this
    // rule.
    @NotNull(message = "Price cannot be null")
    private Double price;

    // 5. Add 'sku' field:
    // - Type: private String
    // - This field cannot be empty, must be unique, use the @NotNull annotation to
    // enforce this rule.
    // - Use the @Table annotation with uniqueConstraints to ensure the 'sku' column
    // is unique.

    // Example: @Table(name = "product", uniqueConstraints =
    // @UniqueConstraint(columnNames = "sku"))
    @NotNull(message = "Sku cannot be null")
    // @Table(name = "Product", uniqueConstraints = @UniqueConstraint(columnNames =
    // "sku"))
    private String sku;

    // 6. Add relationships:
    // - **Inventory**: A product can have multiple inventory entries.
    // - Use @OneToMany(mappedBy = "product") to reflect the one-to-many
    // relationship with Inventory.
    // - Use @JsonManagedReference("inventory-product") to manage bidirectional
    // relationships and avoid circular references.
    @OneToMany(mappedBy = "product", fetch = FetchType.EAGER)
    @JsonManagedReference("inventory-product")
    private List<Inventory> inventory;

    // 7. Add @Entity annotation:
    // - Use @Entity above the class name to mark it as a JPA entity.

    // 8. Add Getters and Setters:
    // - Add getter and setter methods for all fields (id, name, category, price,
    // sku).

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getPrice() {
        return this.price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getSku() {
        return this.sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public List<Inventory> getInventory() {
        return inventory;
    }

    public void setInventory(List<Inventory> inventory) {
        this.inventory = inventory;
    }

    public Product() {

    }

    public Product(String name, String category, Double price, String sku) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.sku = sku;
    }

    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", price=" + price +
                ", sku='" + sku + '\'' +
                '}';
    }
}
