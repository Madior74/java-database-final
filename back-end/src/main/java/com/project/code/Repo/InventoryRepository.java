package com.project.code.Repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.project.code.Model.Inventory;


@Repository
public interface InventoryRepository extends JpaRepository<Inventory,Long>{

    //findByProductIdandStoreId
    @Query("SELECT i FROM Inventory i WHERE i.product.id = :productId AND i.store.id = :storeId")
      Optional<Inventory> findByProductIdandStoreId(Long productId, Long storeId);


     //findByStoreId
     List<Inventory> findByStoreId(Long storeId);


     //deleteByProductId
     void deleteByProductId(Long productId);

}