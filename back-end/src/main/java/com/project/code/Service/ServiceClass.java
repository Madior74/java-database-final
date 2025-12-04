package com.project.code.Service;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.code.Model.Inventory;
import com.project.code.Model.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;

@Service
public class ServiceClass {
    @Autowired
    private  InventoryRepository inventoryRepository;

    @Autowired
    private  ProductRepository productRepository;

  

    
// 1. **validateInventory Method**:
//    - Checks if an inventory record exists for a given product and store combination.
//    - Parameters: `Inventory inventory`
//    - Return Type: `boolean` (Returns `false` if inventory exists, otherwise `true`)

  public boolean validateInventory(Inventory inventory) {
        var inv = inventoryRepository.findByProductIdandStoreId(
                inventory.getProduct().getId(),
                inventory.getStore().getId()
        );
        return inv.isEmpty();
    }

// 2. **validateProduct Method**:
//    - Checks if a product exists by its name.
//    - Parameters: `Product product`
//    - Return Type: `boolean` (Returns `false` if a product with the same name exists, otherwise `true`)
public boolean validateProduct(Product product) {
    String name = product.getName();
    Product existingProduct = productRepository.findByName(name);
    return existingProduct == null;
}

// 3. **ValidateProductId Method**:
//    - Checks if a product exists by its ID.
//    - Parameters: `long id`
//    - Return Type: `boolean` (Returns `false` if the product does not exist with the given ID, otherwise `true`)

public boolean validateProductId(long id) {
        return productRepository.findById(id) != null;
    }

// 4. **getInventoryId Method**:
//    - Fetches the inventory record for a given product and store combination.
//    - Parameters: `Inventory inventory`
//    - Return Type: `Inventory` (Returns the inventory record for the product-store combination)
public Inventory getInventoryId(Inventory inventory) {
    Long productId = inventory.getProduct().getId();
    Long storeId = inventory.getStore().getId();
    return inventoryRepository.findByProductIdandStoreId(productId, storeId).orElse(null);}
}
