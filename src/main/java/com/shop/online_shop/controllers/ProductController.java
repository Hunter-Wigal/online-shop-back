package com.shop.online_shop.controllers;

import com.shop.online_shop.repositories.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.shop.online_shop.entities.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
//@CrossOrigin(origins = "${ALLOWED_ORIGINS}")
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    public record NewProductRequest(
            String item_name,
            String description,
            Double price,
            String image_url
    ){}

    @PostMapping
    public void addProduct(@RequestBody NewProductRequest request){
        Product newProduct = new Product();
        newProduct.setItem_name(request.item_name);
        newProduct.setDescription(request.description);
        newProduct.setPrice(request.price);
        newProduct.setImage_url(request.image_url);

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

    @GetMapping(path="count")
    public ResponseEntity<Integer> getCount(){
        int count = this.productRepository.findAll().size();

        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @GetMapping(path="search")
    public ResponseEntity<List<Product>> productSearch(@RequestParam("keyword") String keyword){
        List<Product> allProducts = this.productRepository.findAll();
        List<Product> matchingProducts = new ArrayList<>();

        for(Product product: allProducts){
            if(product.getItem_name().contains(keyword)){
                matchingProducts.add(product);
            }
        }

        return new ResponseEntity<>(matchingProducts, HttpStatus.OK);
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
    public ResponseEntity<String> deleteProduct(@PathVariable("product_id") int id){
        // implement logic here
        Optional<Product> toDelete = this.productRepository.findById(id);

        if(toDelete.isEmpty()){
            return new ResponseEntity<>("Product with id '" + id + "' not found", HttpStatus.NOT_FOUND);
        }

        this.productRepository.delete(toDelete.get());
        return new ResponseEntity<>("Successfully deleted product", HttpStatus.OK);
    }
}
