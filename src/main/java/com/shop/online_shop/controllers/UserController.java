package com.shop.online_shop.controllers;

import com.shop.online_shop.dto.user.CartAddDto;
import com.shop.online_shop.dto.user.CartGetDto;
import com.shop.online_shop.dto.user.NewUserDto;
import com.shop.online_shop.dto.user.UserGetDto;
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
//@CrossOrigin(origins = "${ALLOWED_ORIGINS}")
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


    @PostMapping
    public void addUser(@RequestBody NewUserDto request){
        User user = new User();
        user.setName(request.name);
        user.setEmail(request.email);
        user.setAge(request.age);

        this.userRepository.save(user);
    }



    // TODO make this use body instead of param
    @GetMapping("user")
    public ResponseEntity<UserGetDto> getUser(@RequestParam("username") String username){
        Optional<User> check = this.userRepository.findByEmail(username);

        UserGetDto response;
        if(check.isPresent()){
            response = new UserGetDto(check.get().getEmail(), check.get().getName(), check.get().getAge());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("user_id")
    public ResponseEntity<UserGetDto> getUser(@RequestBody Integer id){
        Optional<User> check = this.userRepository.findById(id);

        UserGetDto response;
        if(check.isPresent()){
            response = new UserGetDto(check.get().getEmail(), check.get().getName(), check.get().getAge());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    //TODO change this to a patch mapping with a path variable for the username
    @PutMapping("user")
    public ResponseEntity<String> updateUser(@RequestBody NewUserDto request){
        Optional<User> check = this.userRepository.findByEmail(request.email);

        if(check.isEmpty()){
            return new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
        }
        User user = check.get();
        if(request.age != null){
            user.setAge(request.age);
        }
        if(request.name != null){
            user.setName(request.name);
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


    @PostMapping("{username}/cart")
    public ResponseEntity<Boolean> addToCart(@PathVariable("username") String username, @RequestBody CartAddDto request){
        Optional<User> user = this.userRepository.findByEmail(username);

        if(user.isEmpty()){
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }

        List<Product> cart = user.get().getCart();
        cart.add(productRepository.getReferenceById(request.product_id));
        user.get().setCart(cart);

        List<Integer> cartQuantities = user.get().getCartItemQuantities();
        cartQuantities.add(request.quantity);
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


    @GetMapping("{username}/cart")
    public ResponseEntity<CartGetDto> getCart(@PathVariable String username){

        Optional<User> user = this.userRepository.findByEmail(username);

        if(user.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        CartGetDto cartGetDto = new CartGetDto(user.get().getCart(), user.get().getCartItemQuantities());

        return new ResponseEntity<>(cartGetDto, HttpStatus.OK);
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
