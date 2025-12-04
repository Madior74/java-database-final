package com.project.code.Service;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.code.Model.Customer;
import com.project.code.Model.Inventory;
import com.project.code.Model.OrderDetails;
import com.project.code.Model.OrderItem;
import com.project.code.Model.PlaceOrderRequestDTO;
import com.project.code.Model.Product;
import com.project.code.Model.PurchaseProductDTO;
import com.project.code.Model.Store;
import com.project.code.Repo.CustomerRepository;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.OrderDetailsRepository;
import com.project.code.Repo.OrderItemRepository;
import com.project.code.Repo.ProductRepository;
import com.project.code.Repo.StoreRepository;

@Service
public class OrderService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    /**
     * Traite et enregistre une commande client : création/récupération du client,
     * récupération du magasin, création de l'OrderDetails et des OrderItem
     * associés, et mise à jour des stocks.
     *
     * @param placeOrderRequest données de la requête de commande
     */
    public void saveOrder(PlaceOrderRequestDTO placeOrderRequest) {
        if (placeOrderRequest == null) {
            throw new IllegalArgumentException("La requête de commande ne peut pas être nulle");
        }

        // 1. Récupérer ou créer le client à partir de l'email
        String email = placeOrderRequest.getCustomerEmail();
        Customer customer = customerRepository.findByEmail(email);

        if (customer == null) {
            customer = new Customer();
            // Utilisation de la réflexion car les accesseurs peuvent ne pas encore être définis dans l'entité Customer
            setCustomerField(customer, "name", placeOrderRequest.getCustomerName());
            setCustomerField(customer, "email", placeOrderRequest.getCustomerEmail());
            setCustomerField(customer, "telephone", placeOrderRequest.getCustomerPhone());
            customer = customerRepository.save(customer);
        }

        // 2. Récupérer le magasin
        Store store = storeRepository.findById(placeOrderRequest.getStoreId())
                .orElseThrow(() -> new RuntimeException("Magasin introuvable avec l'id : " + placeOrderRequest.getStoreId()));

        // 3. Créer les détails de la commande
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setCustomer(customer);
        orderDetails.setStore(store);
        orderDetails.setTotalPrice(placeOrderRequest.getTotalPrice());
        orderDetails.setDate(LocalDateTime.now());

        orderDetails = orderDetailsRepository.save(orderDetails);

        // 4. Pour chaque produit acheté : vérifier l'inventaire, mettre à jour le stock
        // et créer un OrderItem
        List<PurchaseProductDTO> purchasedProducts = placeOrderRequest.getPurchaseProduct();
        if (purchasedProducts == null || purchasedProducts.isEmpty()) {
            return; // rien à traiter
        }

        for (PurchaseProductDTO purchase : purchasedProducts) {
            // Récupérer l'inventaire pour ce produit et ce magasin
            Inventory inventory = inventoryRepository.findByProductIdandStoreId(
                    purchase.getId(),
                    placeOrderRequest.getStoreId())
                    .orElseThrow(() -> new RuntimeException(
                            "Inventaire introuvable pour le produit " + purchase.getId() + " dans le magasin " + placeOrderRequest.getStoreId()));
            Integer currentStock = inventory.getStockLevel();
            Integer quantity = purchase.getQuantity();

            if (currentStock == null || currentStock < quantity) {
                throw new RuntimeException("Stock insuffisant pour le produit " + purchase.getId());
            }

            // Mettre à jour le stock
            inventory.setStockLevel(currentStock - quantity);
            inventoryRepository.save(inventory);

            // Récupérer le produit
            Product product = productRepository.findById(purchase.getId().longValue());
            if (product == null) {
                throw new RuntimeException("Produit introuvable avec l'id : " + purchase.getId());
            }

            // Créer et sauvegarder l'OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(orderDetails);
            orderItem.setProduct(product);
            orderItem.setQuantity(quantity);
            // On utilise le prix venant du DTO pour garder la trace du prix au moment de la commande
            orderItem.setPrice(purchase.getPrice());

            orderItemRepository.save(orderItem);
        }
    }

    /**
     * Définit une propriété privée de l'entité Customer via réflexion.
     */
    private void setCustomerField(Customer customer, String fieldName, String value) {
        try {
            Field field = Customer.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(customer, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Impossible de définir le champ '" + fieldName + "' pour Customer", e);
        }
    }

}
