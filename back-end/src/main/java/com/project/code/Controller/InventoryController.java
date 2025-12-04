package com.project.code.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.code.Model.CombinedRequest;
import com.project.code.Model.Inventory;
import com.project.code.Model.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;
import com.project.code.Service.ServiceClass;

@RestController
@RequestMapping("/inventory")
public class InventoryController {
  

    @Autowired
    private ServiceClass serviceClass;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

   
    @PutMapping("/update")
    public Map<String, String> updateInventory(@RequestBody CombinedRequest combinedRequest) {
        Map<String, String> response = new HashMap<>();

        try {
            Product incomingProduct = combinedRequest.getProduct();
            Inventory incomingInventory = combinedRequest.getInventory();

            if (incomingProduct == null || incomingProduct.getId() == 0) {
                response.put("message", "Product ID is required");
                return response;
            }

            // Valider l'ID du produit via ServiceClass
            boolean productExists = serviceClass.validateProductId(incomingProduct.getId());
            if (!productExists) {
                response.put("message", "Product does not exist");
                return response;
            }

            // Récupérer le produit existant et le mettre à jour
            Product existingProduct = productRepository.findById(incomingProduct.getId());
            if (existingProduct == null) {
                response.put("message", "Product does not exist");
                return response;
            }
            // Mettre à jour les champs pertinents (éviter d'écraser l'ID)
            if (incomingProduct.getName() != null)
                existingProduct.setName(incomingProduct.getName());
            if (incomingProduct.getCategory() != null)
                existingProduct.setCategory(incomingProduct.getCategory());
            if (incomingProduct.getPrice() != null)
                existingProduct.setPrice(incomingProduct.getPrice());
            if (incomingProduct.getSku() != null)
                existingProduct.setSku(incomingProduct.getSku());

            productRepository.save(existingProduct);

            // Vérifier si l'inventory existe pour le couple product/store
            if (incomingInventory == null || incomingInventory.getStore() == null) {
                response.put("message", "Aucune donnée disponible");
                return response;
            }

            Long productId = existingProduct.getId();
            Long storeId = incomingInventory.getStore().getId();

            var inventoryOptional = inventoryRepository.findByProductIdandStoreId(productId, storeId);
            if (inventoryOptional.isPresent()) {
                Inventory existingInv = inventoryOptional.get();
                if (incomingInventory.getStockLevel() != null) {
                    existingInv.setStockLevel(incomingInventory.getStockLevel());
                }
                inventoryRepository.save(existingInv);
                response.put("message", "Produit mis à jour avec succès");
            } else {
                response.put("message", "Aucune donnée disponible");
            }

            return response;

        } catch (DataIntegrityViolationException e) {
            response.put("message", "Contrainte d'intégrité violée: " + e.getMostSpecificCause().getMessage());
            return response;
        } catch (Exception e) {
            response.put("message", "Erreur: " + e.getMessage());
            return response;
        }
    }
  
    @PostMapping("/save")
    public Map<String, String> saveInventory(@RequestBody Inventory inventory) {
        Map<String, String> response = new HashMap<>();

        try {
            // Vérifier que l'inventaire et ses relations ne sont pas null
            if (inventory == null || inventory.getProduct() == null || inventory.getStore() == null) {
                response.put("message", "L'inventaire, le produit et le magasin ne peuvent pas être null");
                return response;
            }

            // Utiliser validateInventory pour vérifier si l'inventaire existe déjà
            boolean isValid = serviceClass.validateInventory(inventory);

            if (!isValid) {
                // L'inventaire existe déjà
                response.put("message", "L'inventaire existe déjà pour ce produit et magasin");
                return response;
            }

            // L'inventaire n'existe pas, donc on l'enregistre
            inventoryRepository.save(inventory);
            response.put("message", "Données enregistrées avec succès");
            return response;

        } catch (DataIntegrityViolationException e) {
            response.put("message", "Contrainte d'intégrité violée: " + e.getMostSpecificCause().getMessage());
            return response;
        } catch (Exception e) {
            response.put("message", "Erreur: " + e.getMessage());
            return response;
        }
    }

     @GetMapping("/{storeid}")
    public Map<String, Object> getAllProducts(@PathVariable Long storeid) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Product> products = productRepository.findProductsByStoreId(storeid);
            response.put("products", products);
            return response;

        } catch (Exception e) {
            response.put("error", "Erreur: " + e.getMessage());
            return response;
        }
    }

     @GetMapping("/filter/{category}/{name}/{storeid}")
    public Map<String, Object> getProductName(
            @PathVariable String category,
            @PathVariable String name,
            @PathVariable Long storeid) {
        
        Map<String, Object> response = new HashMap<>();

        try {
            List<Product> products = new ArrayList<>();

            // Vérifier si category et name sont "null"
            boolean isCategoryNull = "null".equalsIgnoreCase(category);
            boolean isNameNull = "null".equalsIgnoreCase(name);

            if (isCategoryNull && isNameNull) {
                // Les deux sont null, retourner liste vide
                response.put("product", products);
                return response;
            } else if (isCategoryNull) {
                // Seul le nom est fourni, filtrer par nom
                products = productRepository.findByNameLike(storeid, name);
            } else if (isNameNull) {
                // Seule la catégorie est fournie, filtrer par catégorie
                products = productRepository.findByCategoryAndStoreId(storeid, category);
            } else {
                // Les deux sont fournis, filtrer par les deux
                products = productRepository.findByNameAndCategory(name, category);
            }

            response.put("product", products);
            return response;

        } catch (Exception e) {
            response.put("error", "Erreur: " + e.getMessage());
            return response;
        }
    }

     @GetMapping("/search/{name}/{storeId}")
    public Map<String, Object> searchProduct(
            @PathVariable String name,
            @PathVariable Long storeId) {
        
        Map<String, Object> response = new HashMap<>();

        try {
            List<Product> products = productRepository.findByNameLike(storeId, name);
            response.put("product", products);
            return response;

        } catch (Exception e) {
            response.put("error", "Erreur: " + e.getMessage());
            return response;
        }
    }

    @DeleteMapping("/{id}")
    public Map<String, String> removeProduct(@PathVariable long id) {
        Map<String, String> response = new HashMap<>();

        try {
            // Valider si le produit existe
            boolean productExists = serviceClass.validateProductId(id);
            if (!productExists) {
                response.put("message", "Le produit n'est pas présent dans la base de données");
                return response;
            }

            // Supprimer l'inventaire associé au produit
            inventoryRepository.deleteByProductId(id);

            // Supprimer le produit
            productRepository.deleteById(id);

            response.put("message", "Produit supprimé avec succès");
            return response;

        } catch (Exception e) {
            response.put("message", "Erreur: " + e.getMessage());
            return response;
        }
    }

    // 5. Define the `getAllProducts` Method:
    // - This method handles HTTP GET requests to retrieve products for a specific
    // store.
    // - It uses the `storeId` as a path variable and fetches the list of products
    // from the database for the given store.
    // - The products are returned in a `Map` with the key `"products"`.

    // 6. Define the `getProductName` Method:
    // - This method handles HTTP GET requests to filter products by category and
    // name.
    // - If either the category or name is `"null"`, adjust the filtering logic
    // accordingly.
    // - Return the filtered products in the response with the key `"product"`.

    // 7. Define the `searchProduct` Method:
    // - This method handles HTTP GET requests to search for products by name within
    // a specific store.
    // - It uses `name` and `storeId` as parameters and searches for products that
    // match the `name` in the specified store.
    // - The search results are returned in the response with the key `"product"`.

    // 8. Define the `removeProduct` Method:
    // - This method handles HTTP DELETE requests to delete a product by its ID.
    // - It first validates if the product exists. If it does, it deletes the
    // product from the `ProductRepository` and also removes the related inventory
    // entry from the `InventoryRepository`.
    // - Returns a success message with the key `"message"` indicating successful
    // deletion.

    // 9. Define the `validateQuantity` Method:
    // - This method handles HTTP GET requests to validate if a specified quantity
    // of a product is available in stock for a given store.
    // - It checks the inventory for the product in the specified store and compares
    // it to the requested quantity.
    // - If sufficient stock is available, return `true`; otherwise, return `false`.

}
