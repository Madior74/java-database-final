package com.project.code.Model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


@Entity
public class Store {

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
    @NotBlank
    @NotNull(message = "Name cannot be null")
    private String name;

    // 3. Add 'address' field:
    // - Type: private String
    // - This field cannot be empty, use the @NotNull and @NotBlank annotations to
    // enforce this rule.
    @NotBlank
    @NotNull(message = "Adress cannot be null")
    private String adress;


    // 4. Add relationships:
    // - **Inventory**: A store can have multiple inventory entries.
    // - Use @OneToMany(mappedBy = "store") to reflect the one-to-many relationship
    // with Inventory.
    // - Use @JsonManagedReference("inventory-store") to manage bidirectional
    // relationships and avoid circular references.

    @OneToMany(mappedBy = "store")
    @JsonManagedReference("inventory-store")
    private List<Inventory> inventories = new ArrayList<>();


    public Store(@NotBlank @NotNull(message = "Name cannot be null") String name,
            @NotBlank @NotNull(message = "Adress cannot be null") String adress) {
        this.name = name;
        this.adress = adress;
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


    public String getAdress() {
        return adress;
    }


    public void setAdress(String adress) {
        this.adress = adress;
    }


    public List<Inventory> getInventories() {
        return inventories;
    }


    public void setInventories(List<Inventory> inventories) {
        this.inventories = inventories;
    }

    
    
    public Store() {
    }


    // 5. Add constructor:
    // - Create a constructor that accepts name and address as parameters to
    // initialize the Store object.

    // 6. Add @Entity annotation:
    // - Use @Entity above the class name to mark it as a JPA entity.

    // 7. Add Getters and Setters:
    // - Add getter and setter methods for all fields (id, name, address).

}
