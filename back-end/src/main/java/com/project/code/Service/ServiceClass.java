package com.project.code.Service;

import org.springframework.stereotype.Service;

import com.project.code.Model.Inventory;
import com.project.code.Model.Product;
import com.project.code.Repo.*;

@Service
public class ServiceClass {

    private ProductRepository productRepository;
    private InventoryRepository inventoryRepository;

    // 1. **validateInventory Method**:
    // - Checks if an inventory record exists for a given product and store
    // combination.
    // - Parameters: `Inventory inventory`
    // - Return Type: `boolean` (Returns `false` if inventory exists, otherwise
    // `true`)
    public boolean validateInventory(Inventory inventory) {
        Inventory inv = inventoryRepository.findByProductIdandStoreId(inventory.getProduct().getId(),
                inventory.getStore().getId());
        return inv == null;
    }

    // 2. **validateProduct Method**:
    // - Checks if a product exists by its name.
    // - Parameters: `Product product`
    // - Return Type: `boolean` (Returns `false` if a product with the same name
    // exists, otherwise `true`)
    public boolean validateProduct(Product product) {
        Product prod = productRepository.findByName(product.getName());
        return prod == null;
    }

    // 3. **ValidateProductId Method**:
    // - Checks if a product exists by its ID.
    // - Parameters: `long id`
    // - Return Type: `boolean` (Returns `false` if the product does not exist with
    // the given ID, otherwise `true`)
    public boolean ValidateProductId(Long id) {
        return productRepository.findById(id).isPresent();
    }

    // 4. **getInventoryId Method**:
    // - Fetches the inventory record for a given product and store combination.
    // - Parameters: `Inventory inventory`
    // - Return Type: `Inventory` (Returns the inventory record for the
    // product-store combination)
    public Inventory getInventoryId(Inventory inventory) {
        return inventoryRepository.findByProductIdandStoreId(inventory.getProduct().getId(),
                inventory.getStore().getId());
    }
}
