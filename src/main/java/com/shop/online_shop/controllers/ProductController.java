package com.shop.online_shop.controllers;

import com.shop.online_shop.repositories.ProductRepository;
import org.springframework.web.bind.annotation.*;
import com.shop.online_shop.entities.Product;

import java.util.List;

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
        newProduct.setItemName(request.item_name);
        newProduct.setDescription(request.description);
        newProduct.setPrice(request.price);

        this.productRepository.save(newProduct);
        //TODO add response entity to all responses
    }

    @GetMapping
    public List<Product> getAllProducts(){
        return this.productRepository.findAll();
    }
}
