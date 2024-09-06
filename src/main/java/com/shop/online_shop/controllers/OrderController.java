package com.shop.online_shop.controllers;

import com.shop.online_shop.dto.OrderDto;
import com.shop.online_shop.entities.Order;
import com.shop.online_shop.entities.Product;
import com.shop.online_shop.entities.UserEntity;
import com.shop.online_shop.repositories.OrderRepository;
import com.shop.online_shop.repositories.ProductRepository;
import com.shop.online_shop.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("api/v1/orders")
public class OrderController {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository customerRepository;

    public OrderController(OrderRepository orderRepository, ProductRepository productRepository, UserRepository customerRepository){
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }

    // User type without unsafe information
    public record SafeUser(
            Integer user_id,
            String name,
            String email,
            Integer age
    ){}

    // Type of order to replace standard order entity with unsafe information
    public record SafeOrder(
            Integer transaction_id,
            Product product,
            SafeUser user,
            Integer quantity,
            String status
    ){}

    @GetMapping
    public ResponseEntity<List<SafeOrder>> getTransactions(){
        // implement logic here
        List<Order> orders = this.orderRepository.findAll();
        List<SafeOrder> safeOrders = new ArrayList<>();

        for(Order order: orders){
            UserEntity user = order.getUser_id();
            SafeUser safeUser = new SafeUser(user.getUser_id(), user.getName(), user.getEmail(), user.getAge());
            SafeOrder safeOrder = new SafeOrder(order.getTransaction_id(), order.getProduct(), safeUser, order.getQuantity(), order.getStatus());
            safeOrders.add(safeOrder);
        }

        return new ResponseEntity<>(safeOrders, HttpStatus.OK);
    }

    private record Customer(String name){}
    public record tempOrderDto(String itemName, float price, int quantity, Customer customer) {
    }

    @GetMapping("orders")
    public ResponseEntity<List<tempOrderDto>>getOrders(){
        List<tempOrderDto> orderDtos = new ArrayList<>();

        //TODO check if a join table would make this easier
        // actually, it would be easier. Figure out how join tables work in Spring Boot

        // Need to return item name, price, quantity, customer info
//        List<Order> transactions = this.orderRepository.findAll();
//        List<UserEntity> customers = this.customerRepository.findAll();
//        List<Product> products = this.productRepository.findAll();



//        tempOrderDto order = new tempOrderDto();
        return new ResponseEntity<>(orderDtos, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> addOrder(@RequestBody OrderDto order){
        // implement logic here
        Order newOrder = new Order();
        newOrder.setProduct(this.productRepository.getReferenceById(order.item_id));
        newOrder.setUser_id(this.customerRepository.findById(order.user_id).get());
        newOrder.setQuantity(order.quantity);
        newOrder.setStatus("Order received");


        this.orderRepository.save(newOrder);
        return new ResponseEntity<>("Successfully placed order", HttpStatus.OK);
    }

    @GetMapping(path="{transaction_id}")
    public ResponseEntity<Order> getOrder(@PathVariable("transaction_id") int id){
        // implement logic for not found orders
        return new ResponseEntity<>(this.orderRepository.getReferenceById(id), HttpStatus.OK);

    }

    @PatchMapping(path="{transaction_id}")
    public ResponseEntity updateOrder(@PathVariable("transaction_id") String id){
        // implement logic here
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


}
