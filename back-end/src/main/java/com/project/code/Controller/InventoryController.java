package com.project.code.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import com.project.code.Repo.*;
import com.project.code.Service.*;
import com.project.code.Model.*;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryController {
    // 1. Set Up the Controller Class:
    //    - Annotate the class with `@RestController` to indicate that this is a REST controller, which handles HTTP requests and responses.
    //    - Use `@RequestMapping("/inventory")` to set the base URL path for all methods in this controller. All endpoints related to inventory will be prefixed with `/inventory`.


    // 2. Autowired Dependencies:
    //    - Autowire necessary repositories and services:
    //      - `ProductRepository` will be used to interact with product data (i.e., finding, updating products).
    //      - `InventoryRepository` will handle CRUD operations related to the inventory.
    //      - `ServiceClass` will help with the validation logic (e.g., validating product IDs and inventory data).
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ServiceClass service;

    // 3. Define the `updateInventory` Method:
    //    - This method handles HTTP PUT requests to update inventory for a product.
    //    - It takes a `CombinedRequest` (containing `Product` and `Inventory`) in the request body.
    //    - The product ID is validated, and if valid, the inventory is updated in the database.
    //    - If the inventory exists, update it and return a success message. If not, return a message indicating no data available.
    @PutMapping
    public Map<String, String> updateInventory(@RequestBody CombinedRequest combinedRequest) {
        Product product = combinedRequest.getProduct();
        Inventory inventory = combinedRequest.getInventory();
        Map<String, String> response = new HashMap<>();
        
        System.out.println("Stock Level: " + inventory.getStockLevel());
        if (!service.ValidateProductId(product.getId()))
        {
            return Map.of("message", "Product ID " + product.getId() + " does not present.");
        }
        // Save the product
        productRepository.save(product);
        response.put("message", "Product updated successfully");

        if (inventory != null) {
            try {
                Inventory inv = inventoryRepository.findById(inventory.getId()).orElse(null);
                if (inv != null) {
                    inventory.setId(inv.getId());
                    inventoryRepository.save(inventory);
                    response.put("message", "Inventory updated successfully");
                }
                else {
                    response.put("message", "No inventory data available for the given product and store.");
                }
            } catch (DataIntegrityViolationException e) {
                System.out.println(e);
                response.put("message", "Inventory data integrity violation: " + e.getMessage());
            } catch (Exception e) {
                System.out.println(e);
                response.put("message", "DB Error: " + e.getMessage());
            }
        }
        return response;
    }

    // 4. Define the `saveInventory` Method:
    //    - This method handles HTTP POST requests to save a new inventory entry.
    //    - It accepts an `Inventory` object in the request body.
    //    - It first validates whether the inventory already exists. If it exists, it returns a message stating so. If it doesn’t exist, it saves the inventory and returns a success message.
    @PostMapping
    public Map<String, String> saveInventory(@RequestBody Inventory inventory) {
        // Response type to http response
        Map<String, String> response = new HashMap<>();

        try
        {
            if (inventoryRepository.findByProductIdandStoreId(inventory.getProduct().getId(), inventory.getStore().getId()) != null) {
                response.put("message", "Inventory already exists for the given product and store.");
            } 
            else 
            {
                inventoryRepository.save(inventory);
                response.put("message", "Inventory saved successfully");
            }
        } catch (DataIntegrityViolationException e)
        {
            System.out.println(e);
            response.put("message", "Inventory data integrity violation: " + e.getMessage());
        }
        catch (Exception e)
        {
            System.out.println(e);
            response.put("message", "DB Error: " + e.getMessage());
        }                    
        return response;
    }

    // 5. Define the `getAllProducts` Method:
    //    - This method handles HTTP GET requests to retrieve products for a specific store.
    //    - It uses the `storeId` as a path variable and fetches the list of products from the database for the given store.
    //    - The products are returned in a `Map` with the key `"products"`.
    @GetMapping("/{storeId}")
    public Map<String, Object> getAllProducts(@PathVariable Long storeId) {
        Map<String, Object> response = new HashMap<>();
        List<Product> products = productRepository.findProductsByStoreId(storeId);
        response.put("products", products);
        return response;
    }

    // 6. Define the `getProductName` Method:
    //    - This method handles HTTP GET requests to filter products by category and name.
    //    - If either the category or name is `"null"`, adjust the filtering logic accordingly.
    //    - Return the filtered products in the response with the key `"product"`.
    @GetMapping("filter/{category}/{name}/{storeid}")
    public Map<String, Object> getProductName(@PathVariable String category, @PathVariable String name, @PathVariable Long storeId)
    {
        Map<String, Object> response = new HashMap<>();
        List<Product> products = new ArrayList<>();
        if (category.equals("null") && name.equals("null"))
        {
            products = productRepository.findProductsByStoreId(storeId);
        }
        else if (category.equals("null"))
        {
            products = productRepository.findByNameLike(storeId, name);
        }
        else if (name.equals("null"))
        {
            products = productRepository.findProductByCategory(storeId, category);
        }
        else
        {
            products = productRepository.findByNameAndCategory(storeId, name, category);
        }
        response.put("product", products);
        return response;
    }

    // 7. Define the `searchProduct` Method:
    //    - This method handles HTTP GET requests to search for products by name within a specific store.
    //    - It uses `name` and `storeId` as parameters and searches for products that match the `name` in the specified store.
    //    - The search results are returned in the response with the key `"product"`.
    @GetMapping("search/{name}/{storeId}")
    public Map<String, Object> searchProduct(@PathVariable String name, @PathVariable Long storeId)
    {
        Map<String, Object> response = new HashMap<>();
        List<Product> products = productRepository.findByNameLike(storeId, name);
        response.put("product", products);
        return response;
    }

    // 8. Define the `removeProduct` Method:
    //    - This method handles HTTP DELETE requests to delete a product by its ID.
    //    - It first validates if the product exists. If it does, it deletes the product from the `ProductRepository` and also removes the related inventory entry from the `InventoryRepository`.
    //    - Returns a success message with the key `"message"` indicating successful deletion.
    @DeleteMapping("/{id}")
    public Map<String, String> removeProduct(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        if (inventoryRepository.deleteByProductId(id) > 0)
        {
            response.put("message", "Related inventory entries deleted successfully.");
        }
        else
        {
            response.put("message", "No related inventory entries found.");
        }
        return response;
    }

    // 9. Define the `validateQuantity` Method:
    //    - This method handles HTTP GET requests to validate if a specified quantity of a product is available in stock for a given store.
    //    - It checks the inventory for the product in the specified store and compares it to the requested quantity.
    //    - If sufficient stock is available, return `true`; otherwise, return `false`.
    @GetMapping("validate/{quantity}/{storeId}/{productId}")
    public boolean validateQuantity(@PathVariable int quantity, @PathVariable Long storeId, @PathVariable Long productId)
    {
        Inventory inventory = inventoryRepository.findByProductIdandStoreId(productId, storeId);
        if (inventory != null && inventory.getStockLevel() >= quantity)
        {
            return true;
        }
        return false;
    }
}
