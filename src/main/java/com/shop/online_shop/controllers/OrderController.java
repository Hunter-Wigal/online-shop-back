package com.shop.online_shop.controllers;

import com.shop.online_shop.dto.OrderDto;
import com.shop.online_shop.entities.Transaction;
import com.shop.online_shop.entities.Product;
import com.shop.online_shop.entities.User;
import com.shop.online_shop.repositories.TransactionRepository;
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
    private final TransactionRepository transactionRepository;
    private final ProductRepository productRepository;
    private final UserRepository customerRepository;

    public OrderController(TransactionRepository transactionRepository, ProductRepository productRepository, UserRepository customerRepository){
        this.transactionRepository = transactionRepository;
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
    public record SafeTransaction(
            Integer transaction_id,
            Product product,
            SafeUser user,
            Integer quantity,
            String status
    ){}

    @GetMapping
    public ResponseEntity<List<SafeTransaction>> getTransactions(){
        // implement logic here
        List<Transaction> transactions = this.transactionRepository.findAll();
        List<SafeTransaction> safeTransactions = new ArrayList<>();

        for(Transaction transaction : transactions){
            User user = transaction.getUser_id();
            SafeUser safeUser = new SafeUser(user.getUser_id(), user.getName(), user.getEmail(), user.getAge());
            SafeTransaction safeTransaction = new SafeTransaction(transaction.getTransaction_id(), transaction.getProduct(), safeUser, transaction.getQuantity(), transaction.getStatus());
            safeTransactions.add(safeTransaction);
        }

        return new ResponseEntity<>(safeTransactions, HttpStatus.OK);
    }

    private record Customer(String name){}
    public record tempOrderDto(String itemName, float price, int quantity, Customer customer) {
    }

    @GetMapping("orders")
    public ResponseEntity<List<tempOrderDto>>getOrders(){
        List<tempOrderDto> orderDtos = new ArrayList<>();

        return new ResponseEntity<>(orderDtos, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> addOrder(@RequestBody OrderDto order){
        // implement logic here
        Transaction newTransaction = new Transaction();
        newTransaction.setProduct(this.productRepository.getReferenceById(order.item_id));
        newTransaction.setUser_id(this.customerRepository.findById(order.user_id).get());
        newTransaction.setQuantity(order.quantity);
        newTransaction.setStatus("Order received");


        this.transactionRepository.save(newTransaction);
        return new ResponseEntity<>("Successfully placed order", HttpStatus.OK);
    }

    @GetMapping(path="{transaction_id}")
    public ResponseEntity<Transaction> getOrder(@PathVariable("transaction_id") int id){
        // implement logic for not found orders
        return new ResponseEntity<>(this.transactionRepository.getReferenceById(id), HttpStatus.OK);

    }

    @PatchMapping(path="{transaction_id}")
    public ResponseEntity<Object> updateOrder(@PathVariable("transaction_id") String id){
        // implement logic here
        System.out.println(id);
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


}
