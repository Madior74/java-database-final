package com.project.code.Model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Table(name = "product", uniqueConstraints = @jakarta.persistence.UniqueConstraint(columnNames = "sku"))
@Entity
public class Product {

    // 1. Add 'id' field:
    // - Type: private long
    // - This field will be auto-incremented.
    // - Use @Id to mark it as the primary key.
    // - Use @GeneratedValue(strategy = GenerationType.IDENTITY) to auto-increment
    // it.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

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

    @NotNull(message = "category cannot be null")
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

    @NotNull(message = "Sku cannot be null")
    private String sku;

    @OneToMany(mappedBy = "product")
    @JsonManagedReference("inventory-product")
    private List<Inventory> inventories = new ArrayList<>();



        
    public Product(@NotNull(message = "Name cannot be null") String name,
            @NotNull(message = "category cannot be null") String category,
            @NotNull(message = "Price cannot be null") Double price,
            @NotNull(message = "Sku cannot be null") String sku) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.sku = sku;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public List<Inventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<Inventory> inventories) {
        this.inventories = inventories;
    }

    public Product() {
    }

    // Example: @Table(name = "product", uniqueConstraints =
    // @UniqueConstraint(columnNames = "sku"))

    // 6. Add relationships:
    // - **Inventory**: A product can have multiple inventory entries.
    // - Use @OneToMany(mappedBy = "product") to reflect the one-to-many
    // relationship with Inventory.
    // - Use @JsonManagedReference("inventory-product") to manage bidirectional
    // relationships and avoid circular references.

    // 7. Add @Entity annotation:
    // - Use @Entity above the class name to mark it as a JPA entity.

    // 8. Add Getters and Setters:
    // - Add getter and setter methods for all fields (id, name, category, price,
    // sku).


    
}
