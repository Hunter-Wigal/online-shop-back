package com.shop.online_shop.controllers;

import com.shop.online_shop.dto.transaction.TransactionDto;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
//@CrossOrigin(origins = "${ALLOWED_ORIGINS}")
@RequestMapping("api/v1/orders")
public class TransactionController {
    private final TransactionRepository transactionRepository;
    private final ProductRepository productRepository;
    private final UserRepository customerRepository;

    public TransactionController(TransactionRepository transactionRepository, ProductRepository productRepository, UserRepository customerRepository){
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
            List<Product> products,
            SafeUser user,
            Integer[] quantities,
            String status
    ){}

    @GetMapping
    public ResponseEntity<List<SafeTransaction>> getTransactions(){
        List<Transaction> transactions = this.transactionRepository.findAll();
        List<SafeTransaction> safeTransactions = new ArrayList<>();

        for(Transaction transaction : transactions){
            User user = transaction.getUser_id();
            SafeUser safeUser = new SafeUser(user.getUser_id(), user.getName(), user.getEmail(), user.getAge());
            SafeTransaction safeTransaction = new SafeTransaction(transaction.getTransaction_id(), transaction.getProducts(), safeUser, transaction.getQuantities(), transaction.getStatus());
            safeTransactions.add(safeTransaction);
        }

        return new ResponseEntity<>(safeTransactions, HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity<String> addOrder(@RequestBody TransactionDto orders){
        Product[] products = new Product[orders.product_ids.length];
        int count = 0;
        for(int id: orders.product_ids){
            products[count++] = this.productRepository.getReferenceById(id);
        }

        Transaction newTransaction = new Transaction();
        newTransaction.setProducts(Arrays.asList(products));
        Optional<User> user = this.customerRepository.findByEmail(orders.user_email);
        if(user.isPresent()){
            newTransaction.setUser_id(user.get());
        }
        else{
            return new ResponseEntity<>("User not logged in", HttpStatus.UNAUTHORIZED);
        }

        newTransaction.setQuantities(orders.quantities);
        newTransaction.setStatus("Order received");


        this.transactionRepository.save(newTransaction);
        return new ResponseEntity<>("Successfully placed order", HttpStatus.OK);
    }

    @GetMapping(path="{transaction_id}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable("transaction_id") int id){
        // implement logic for not found orders
        Optional<Transaction> transaction = this.transactionRepository.findById(id);

        if(transaction.isEmpty()){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(transaction.get(), HttpStatus.OK);

    }

    @PatchMapping(path="{transaction_id}")
    public ResponseEntity<Object> updateTransaction(@PathVariable("transaction_id") int id, SafeTransaction transaction){
        Transaction toUpdate = this.transactionRepository.getReferenceById(id);

        // Assume toUpdate exists
        toUpdate.setProducts(transaction.products);
        toUpdate.setQuantities(transaction.quantities);
        toUpdate.setStatus(transaction.status);

        this.transactionRepository.save(toUpdate);

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }


    @DeleteMapping(path="{transaction_id}")
    public ResponseEntity<Boolean> deleteTransaction(@PathVariable int transaction_id){
        Optional<Transaction> toDelete = this.transactionRepository.findById(transaction_id);

        if(toDelete.isEmpty()){
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }

        this.transactionRepository.delete(toDelete.get());
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @PatchMapping(path="{transaction_id}/status")
    public ResponseEntity<Boolean> updateStatus(@PathVariable int transaction_id){



        return new ResponseEntity<>(true, HttpStatus.OK);
    }


}
