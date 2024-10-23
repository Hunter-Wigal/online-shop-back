package com.shop.online_shop.controllers;

import com.shop.online_shop.entities.Product;
import com.shop.online_shop.entities.User;
import com.shop.online_shop.repositories.ProductRepository;
import com.shop.online_shop.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/v1/user")
public class UserController {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public UserController(UserRepository userRepository, ProductRepository productRepository){
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @GetMapping("/")
    public List<User> getAllUsers(){
        return this.userRepository.findAll();
    }

    public record NewUserRequest(
            String name,
            String email,
            Integer age
    ){}
    @PostMapping
    public void addUser(@RequestBody NewUserRequest request){
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setAge(request.age());

        this.userRepository.save(user);
    }

    public record UserResponse(
            String email,
            String name,
            Integer age
    ){}

    // TODO make this use body instead of param
    @GetMapping("user")
    public ResponseEntity<UserResponse> getUser(@RequestParam("username") String username){
        Optional<User> check = this.userRepository.findByEmail(username);

        UserResponse response;
        if(check.isPresent()){
            response = new UserResponse(check.get().getEmail(), check.get().getName(), check.get().getAge());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("user_id")
    public ResponseEntity<UserResponse> getUser(@RequestBody Integer id){
        Optional<User> check = this.userRepository.findById(id);

        UserResponse response;
        if(check.isPresent()){
            response = new UserResponse(check.get().getEmail(), check.get().getName(), check.get().getAge());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    //TODO change this to a patch mapping with a path variable for the username
    @PutMapping("user")
    public ResponseEntity<String> updateUser(@RequestBody NewUserRequest request){
        Optional<User> check = this.userRepository.findByEmail(request.email());

        if(check.isEmpty()){
            return new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
        }
        User user = check.get();
        if(request.age() != null){
            user.setAge(request.age());
        }
        if(request.name() != null){
            user.setName(request.name());
        }
        this.userRepository.save(user);
        return new ResponseEntity<>("Successfully updated user", HttpStatus.OK);
    }

    @DeleteMapping("{username}")
    public ResponseEntity<Boolean> delete(@PathVariable("username") String username){
        // implement logic here
        System.out.println(username);
        return new ResponseEntity<>(false, HttpStatus.NOT_IMPLEMENTED);
    }

    public record CartRequest(
            int product_id,
            int quantity
    ){}

    @PostMapping("{username}/cart")
    public ResponseEntity<Boolean> addToCart(@PathVariable("username") String username, @RequestBody CartRequest request){
        Optional<User> user = this.userRepository.findByEmail(username);

        if(user.isEmpty()){
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }

        List<Product> cart = user.get().getCart();
        cart.add(productRepository.getReferenceById(request.product_id));
        user.get().setCart(cart);

        List<Integer> cartQuantities = user.get().getCartItemQuantities();
        cartQuantities.add(request.quantity());
        user.get().setCartItemQuantities(cartQuantities);

        this.userRepository.save(user.get());
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @DeleteMapping("{username}/cart")
    public ResponseEntity<Boolean> clearCart(@PathVariable String username){
        // Change to make sure that only the owner of the account can clear the cart
        Optional<User> user = this.userRepository.findByEmail(username);
        if(user.isEmpty()){
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }

        user.get().setCartItemQuantities(new ArrayList<>());
        user.get().setCart(new ArrayList<>());

        this.userRepository.save(user.get());

        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    public record Cart(
            List<Product> products,
            List<Integer> quantities
    ){}
    @GetMapping("{username}/cart")
    public ResponseEntity<Cart> getCart(@PathVariable String username){

        Optional<User> user = this.userRepository.findByEmail(username);

        if(user.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Cart cart = new Cart(user.get().getCart(), user.get().getCartItemQuantities());

        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

    @DeleteMapping("{username}/cart/{index}")
    public ResponseEntity<Boolean> deleteFromCart(@PathVariable String username, @PathVariable int index){
        Optional<User> user = this.userRepository.findByEmail(username);

        if(user.isEmpty()){
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }

        List<Product> cart = user.get().getCart();
        List<Integer> quantities = user.get().getCartItemQuantities();

        if(index > cart.size() || index < 0){
            return new ResponseEntity<>(false, HttpStatus.NOT_ACCEPTABLE);
        }

        cart.remove(index);
        quantities.remove(index);

        user.get().setCart(cart);
        user.get().setCartItemQuantities(quantities);
        this.userRepository.save(user.get());
        return new ResponseEntity<>(true, HttpStatus.OK);
    }
}
