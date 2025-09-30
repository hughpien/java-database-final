package com.project.code.Controller;

import org.springframework.web.bind.annotation.RestController;

import com.project.code.Repo.StoreRepository;
import com.project.code.Service.OrderService;
import com.project.code.Model.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/store")
public class StoreController {
    // 1. Set Up the Controller Class:
    // - Annotate the class with `@RestController` to designate it as a REST
    // controller for handling HTTP requests.
    // - Map the class to the `/store` URL using `@RequestMapping("/store")`.

    // 2. Autowired Dependencies:
    // - Inject the following dependencies via `@Autowired`:
    // - `StoreRepository` for managing store data.
    // - `OrderService` for handling order-related functionality.
    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private OrderService orderService;

    // 3. Define the `addStore` Method:
    // - Annotate with `@PostMapping` to create an endpoint for adding a new store.
    // - Accept `Store` object in the request body.
    // - Return a success message in a `Map<String, String>` with the key `message`
    // containing store creation confirmation.
    @PostMapping
    public Map<String, String> addStore(@RequestBody Store store) {
        Map<String, String> response = new HashMap<>();
        try {
            if (storeRepository.existsById(store.getId())) {
                response.put("message", "Store with ID " + store.getId() + " already exists.");
            } else {
                storeRepository.save(store);
                response.put("message", "Add Store successfully with ID: " + store.getId());
            }
        } catch (Exception e) {
            response.put("error", "Error: " + e);
        }
        return response;
    }

    // 4. Define the `validateStore` Method:
    // - Annotate with `@GetMapping("validate/{storeId}")` to check if a store
    // exists by its `storeId`.
    // - Return a **boolean** indicating if the store exists.
    @GetMapping("validate/{storeId}")
    public boolean validateStore(@PathVariable Long storeId) {
        return storeRepository.existsById(storeId);
    }

    // 5. Define the `placeOrder` Method:
    // - Annotate with `@PostMapping("/placeOrder")` to handle order placement.
    // - Accept `PlaceOrderRequestDTO` in the request body.
    // - Return a success message with key `message` if the order is successfully
    // placed.
    // - Return an error message with key `Error` if there is an issue processing
    // the order.
    @PostMapping("/placeOrder")
    public Map<String, String> placeOrder(@RequestBody PlaceOrderRequestDTO orderRequest) {
        Map<String, String> response = new HashMap<>();
        try {
            orderService.saveOrder(orderRequest);
            response.put("message", "Order placed successfully");
        } catch (Exception e) {
            response.put("error", "Error placing order: " + e);
        }
        return response;
    }
}
