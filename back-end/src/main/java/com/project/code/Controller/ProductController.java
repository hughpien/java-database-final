package com.project.code.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.dao.DataIntegrityViolationException;

import com.project.code.Model.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.OrderItemRepository;
import com.project.code.Repo.ProductRepository;
import com.project.code.Service.ServiceClass;

@RestController
@RequestMapping("/product")
public class ProductController {
    // 1. Set Up the Controller Class:
    // - Annotate the class with `@RestController` to designate it as a REST
    // controller for handling HTTP requests.
    // - Map the class to the `/product` URL using `@RequestMapping("/product")`.

    // 2. Autowired Dependencies:
    // - Inject the following dependencies via `@Autowired`:
    // - `ProductRepository` for CRUD operations on products.
    // - `ServiceClass` for product validation and business logic.
    // - `InventoryRepository` for managing the inventory linked to products.
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ServiceClass service;

    // 3. Define the `addProduct` Method:
    // - Annotate with `@PostMapping` to handle POST requests for adding a new
    // product.
    // - Accept `Product` object in the request body.
    // - Validate product existence using `validateProduct()` in `ServiceClass`.
    // - Save the valid product using `save()` method of `ProductRepository`.
    // - Catch exceptions (e.g., `DataIntegrityViolationException`) and return
    // appropriate error message.
    @PostMapping
    public Map<String, String> addProduct(@RequestBody Product product) {
        Map<String, String> response = new HashMap<>();
        try {
            if (service.validateProduct(product)) {
                productRepository.save(product);
                response.put("message", "Product added successfully");
            } else {
                response.put("message", "Product with ID " + product.getId() + " already exists.");
            }
        } catch (DataIntegrityViolationException e) {
            response.put("message", "Error: " + e);
        } catch (Exception e) {
            response.put("message", "DB error: " + e);
        }
        return response;
    }

    // 4. Define the `getProductbyId` Method:
    // - Annotate with `@GetMapping("/product/{id}")` to handle GET requests for
    // retrieving a product by ID.
    // - Accept product ID via `@PathVariable`.
    // - Use `findById(id)` method from `ProductRepository` to fetch the product.
    // - Return the product in a `Map<String, Object>` with key `products`.
    @GetMapping("/product/{id}")
    public Map<String, Object> getProductbyId(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Product product = productRepository.findById(id).orElse(null);
            response.put("products", product);
        } catch (Exception e) {
            response.put("error", "DB error: " + e);
        }
        return response;
    }

    // 5. Define the `updateProduct` Method:
    // - Annotate with `@PutMapping` to handle PUT requests for updating an existing
    // product.
    // - Accept updated `Product` object in the request body.
    // - Use `save()` method from `ProductRepository` to update the product.
    // - Return a success message with key `message` after updating the product.
    @PutMapping
    public Map<String, String> updateProduct(@RequestBody Product product) {
        Map<String, String> response = new HashMap<>();
        try {
            productRepository.save(product);
            response.put("message", "Product updated successfully");
        } catch (Exception e) {
            response.put("error", "DB error: " + e);
        }
        return response;
    }

    // 6. Define the `filterbyCategoryProduct` Method:
    // - Annotate with `@GetMapping("/category/{name}/{category}")` to handle GET
    // requests for filtering products by `name` and `category`.
    // - Use conditional filtering logic if `name` or `category` is `"null"`.
    // - Fetch products based on category using methods like `findByCategory()` or
    // `findProductBySubNameAndCategory()`.
    // - Return filtered products in a `Map<String, Object>` with key `products`.
    @GetMapping("/category/{name}/{category}")
    public Map<String, Object> filterbyCategoryProduct(@PathVariable String name, @PathVariable String category) {
        Map<String, Object> response = new HashMap<>();
        List<Product> products = new ArrayList<>();
        if (!category.equals("null") && !name.equals("null")) {
            products = productRepository.findProductBySubNameAndCategory(name, category);
        } else if (!category.equals("null")) {
            products = productRepository.findByCategory(category);
        } else if (!name.equals("null")) {
            products = productRepository.findProductBySubName(name);
        }
        response.put("products", products);
        return response;
    }

    // 7. Define the `listProduct` Method:
    // - Annotate with `@GetMapping` to handle GET requests to fetch all products.
    // - Fetch all products using `findAll()` method from `ProductRepository`.
    // - Return all products in a `Map<String, Object>` with key `products`.
    @GetMapping
    public Map<String, Object> listProduct() {
        Map<String, Object> response = new HashMap<>();
        List<Product> products = productRepository.findAll();
        response.put("products", products);
        return response;
    }

    // 8. Define the `getProductbyCategoryAndStoreId` Method:
    // - Annotate with `@GetMapping("filter/{category}/{storeid}")` to filter
    // products by `category` and `storeId`.
    // - Use `findProductByCategory()` method from `ProductRepository` to retrieve
    // products.
    // - Return filtered products in a `Map<String, Object>` with key `product`.
    @GetMapping("filter/{category}/{storeid}")
    public Map<String, Object> getProductbyCategoryAndStoreId(@PathVariable String category,
            @PathVariable Long storeid) {
        Map<String, Object> response = new HashMap<>();
        List<Product> products = productRepository.findProductByCategory(storeid, category);
        response.put("product", products);
        return response;
    }

    // 9. Define the `deleteProduct` Method:
    // - Annotate with `@DeleteMapping("/{id}")` to handle DELETE requests for
    // removing a product by its ID.
    // - Validate product existence using `ValidateProductId()` in `ServiceClass`.
    // - Remove product from `Inventory` first using `deleteByProductId(id)` in
    // `InventoryRepository`.
    // - Remove product from `Product` using `deleteById(id)` in
    // `ProductRepository`.
    // - Return a success message with key `message` indicating product deletion.
    @DeleteMapping("/{id}")
    public Map<String, String> deleteProduct(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        try {
            if (!service.ValidateProductId(id)) {
                System.out.println("Product with ID " + id + " does not exist.");
                response.put("message", "Product with ID " + id + " does not exist.");
            }
            inventoryRepository.deleteByProductId(id);
            orderItemRepository.deleteByProductId(id);
            productRepository.deleteById(id);
            System.out.println("Product with ID " + id + " deleted successfully.");
            response.put("message", "Product deleted successfully");
        } catch (Exception e) {
            System.out.println("DB error: " + e);
            response.put("error", "DB error: " + e);
        }
        return response;
    }

    // 10. Define the `searchProduct` Method:
    // - Annotate with `@GetMapping("/searchProduct/{name}")` to search for products
    // by `name`.
    // - Use `findProductBySubName()` method from `ProductRepository` to search
    // products by name.
    // - Return search results in a `Map<String, Object>` with key `products`.
    @GetMapping("/searchProduct/{name}")
    public Map<String, Object> searchProduct(@PathVariable String name) {
        Map<String, Object> response = new HashMap<>();
        List<Product> products = productRepository.findProductBySubName(name);
        response.put("products", products);
        return response;
    }
}
