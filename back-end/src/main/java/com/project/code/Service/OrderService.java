package com.project.code.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.code.Model.Customer;
import com.project.code.Model.Inventory;
import com.project.code.Model.PlaceOrderRequestDTO;
import com.project.code.Model.PurchaseProductDTO;
import com.project.code.Repo.OrderDetailsRepository;
import com.project.code.Repo.OrderItemRepository;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private StoreRepoitory storeRepository;

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    // 1. **saveOrder Method**:
    //    - Processes a customer's order, including saving the order details and associated items.
    //    - Parameters: `PlaceOrderRequestDTO placeOrderRequest` (Request data for placing an order)
    //    - Return Type: `void` (This method doesn't return anything, it just processes the order)
    public void saveOrder(PlaceOrderRequestDTO placeOrderRequest) {
        // 2. **Retrieve or Create the Customer**:
        //    - Check if the customer exists by their email using `findByEmail`.
        //    - If the customer exists, use the existing customer; otherwise, create and save a new customer using `customerRepository.save()`.
        Customer customer = customerRepository.findByEmail(placeOrderRequest.getCustomerEmail());
        if (customer == null)
        {
            customer = new Customer();
            customer.setEmail(placeOrderRequest.getCustomerEmail());
            customer.setName(placeOrderRequest.getCustomerName());;
            customer.SetPhone(placeOrderRequest.getCustomerPhone());
            customer = customerRepository.save(customer);
        }
        
        // 3. **Retrieve the Store**:
        //    - Fetch the store by ID from `storeRepository`.
        //    - If the store doesn't exist, throw an exception. Use `storeRepository.findById()`.
        Store store = storeRepository.findById(placeOrderRequest.getStoreId()).orElseThrow(() -> 
            new RuntimeException("Can't find the store"));

        // 4. **Create OrderDetails**:
        //    - Create a new `OrderDetails` object and set customer, store, total price, and the current timestamp.
        //    - Set the order date using `java.time.LocalDateTime.now()` and save the order with `orderDetailsRepository.save()`.
        OrderDetails orderDetails = new OrderDetails(
            customer,
            store,
            placeOrderReques.getTotalPrice(),
            java.time.LocalDateTime.now()
        );
        orderDetails = orderDetailsRepository.save(orderDetails);

        // 5. **Create and Save OrderItems**:
        //    - For each product purchased, find the corresponding inventory, update stock levels, and save the changes using `inventoryRepository.save()`.
        //    - Create and save `OrderItem` for each product and associate it with the `OrderDetails` using `orderItemRepository.save()`.
        List<PurchaseProductDTO> purchaseProductDTOs = placeOrderRequest.getPurchaseProduct();
        for (PurchaseProductDTO productDTO : purchaseProductDTOs)
        {
            Inventory inv = inventoryRepository.findByProductIdandStoreId (
                productDTO.getId(),
                placeOrderRequest.getStoreId()
            );
            inv.setStockLevel(inv.getStockLevel() - productDTO.getQuantity()(;
            inventoryRepository.save(inv););
 
            OrderItem orderItem = new OrderItem (
                orderDetails,
                productRepository.findByid(productDTO.getId()),
                productDTO.getQuantity(),
                productDTO.getPrice() * productDTO.getQuantity()
            );
        }

    }
}
