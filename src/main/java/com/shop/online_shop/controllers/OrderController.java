package com.shop.online_shop.controllers;

import com.shop.online_shop.dto.OrderDto;
import com.shop.online_shop.entities.Order;
import com.shop.online_shop.repositories.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("api/v1/orders")
public class OrderController {
    private final OrderRepository orderRepository;

    public OrderController(OrderRepository orderRepository){
        this.orderRepository = orderRepository;
    }

    @GetMapping
    public ResponseEntity<List<Order>> getOrders(){
        // implement logic here
        return new ResponseEntity<>(this.orderRepository.findAll(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> addOrder(@RequestBody OrderDto order){
        // implement logic here
        Order newOrder = new Order();
        newOrder.setItem_id(order.item_id);
        newOrder.setUser_id(order.user_id);
        newOrder.setQuantity(order.quantity);
        newOrder.setStatus("Order received");

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
        return new ResponseEntity(HttpStatus.NOT_IMPLEMENTED);
    }


}
