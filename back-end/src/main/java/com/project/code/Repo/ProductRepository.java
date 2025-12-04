package com.project.code.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.project.code.Model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAll();

    List<Product> findByCategory(String category);

    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);

    // findById
    Product findById(long id);

    Product findBySku(String sku);

    Product findByName(String name);

    @Query("SELECT i.product FROM Inventory i WHERE i.store.id = :storeId AND i.product.category = :category")
    List<Product> findProductsByStoreId(Long storeId);

    @Query("SELECT i.product FROM Inventory i WHERE i.store.id = :storeId AND i.product.category = :category")
    List<Product> findByNameLike(Long storeId, String pname);

    // findByNameAndCategory
    @Query("SELECT p FROM Product p WHERE p.name = :name AND p.category = :category")
    List<Product> findByNameAndCategory(String name, String category);

    @Query("SELECT DISTINCT i.product FROM Inventory i WHERE i.store.id = :storeId AND i.product.category = :category")
    List<Product> findByCategoryAndStoreId(Long storeId, String category);

    @Query("SELECT i FROM Product i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :pname, '%')) AND i.category = :category")
    List<Product> findProductBySubNameAndCategory(String pname, String category);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :pname, '%'))")
    List<Product> findProductBySubName(String pname);

    // 1. Add the repository interface:
    // - Extend JpaRepository<Product, Long> to inherit basic CRUD functionality.
    // - This allows the repository to perform operations like save, delete, update,
    // and find without having to implement these methods manually.

    // Example: public interface ProductRepository extends JpaRepository<Product,
    // Long> {}

    // 2. Add custom query methods:
    // - **findAll**:
    // - This method will retrieve all products.
    // - Return type: List<Product>

    // Example: public List<Product> findAll();

    // - **findByCategory**:
    // - This method will retrieve products by their category.
    // - Return type: List<Product>
    // - Parameter: String category

    // Example: public List<Product> findByCategory(String category);

    // - **findByPriceBetween**:
    // - This method will retrieve products within a price range.
    // - Return type: List<Product>
    // - Parameters: Double minPrice, Double maxPrice

    // Example: public List<Product> findByPriceBetween(Double minPrice, Double
    // maxPrice);

    // - **findBySku**:
    // - This method will retrieve a product by its SKU.
    // - Return type: Product
    // - Parameter: String sku

    // Example: public Product findBySku(String sku);

    // - **findByName**:
    // - This method will retrieve a product by its name.
    // - Return type: Product
    // - Parameter: String name

    // Example: public Product findByName(String name);

    // - **findByNameLike**:
    // - This method will retrieve products by a name pattern for a specific store.
    // - Return type: List<Product>
    // - Parameters: Long storeId, String pname
    // - Use @Query annotation to write a custom query.

}
