package com.shop.online_shop.controllers;

import com.shop.online_shop.repositories.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.shop.online_shop.entities.Product;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    public record NewProductRequest(
            String item_name,
            String description,
            Double price
    ){}

    @PostMapping
    public void addProduct(@RequestBody ProductController.NewProductRequest request){
        Product newProduct = new Product();
        newProduct.setItem_name(request.item_name);
        newProduct.setDescription(request.description);
        newProduct.setPrice(request.price);

        this.productRepository.save(newProduct);
        //TODO add response entity to all responses
    }

    @GetMapping
    public List<Product> getAllProducts(){
        return this.productRepository.findAll();
    }

    @GetMapping(path="{product_id}")
    public ResponseEntity<Product> getProduct(@PathVariable("product_id") int id){
        // implement logic here
        Optional<Product> product = this.productRepository.findById(id);
        return product.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    public record UpdateRequest(
            String item_name,
            String item_description
    ){}
    @PatchMapping(path="{product_id}")
    public ResponseEntity<String> updateProduct(@PathVariable("product_id") String id, @RequestBody UpdateRequest request){
        // implement logic here
        Product product = this.productRepository.getReferenceById(Integer.parseInt(id));
        product.setItem_name(request.item_name);
        product.setDescription(request.item_description);

        this.productRepository.save(product);

        return new ResponseEntity<>("Successfully updated",HttpStatus.OK);
    }

    @DeleteMapping(path="{product_id}")
    public ResponseEntity deleteProduct(@PathVariable("product_id") String id){
        // implement logic here
        return new ResponseEntity(HttpStatus.NOT_IMPLEMENTED);
    }
}
