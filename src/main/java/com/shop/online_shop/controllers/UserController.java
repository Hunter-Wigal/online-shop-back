package com.shop.online_shop.controllers;

import com.shop.online_shop.entities.User;
import com.shop.online_shop.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/v1/user")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository){
        this.userRepository = userRepository;
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

    //TODO change this to a patch mapping with a pathvariable for the username
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
        return new ResponseEntity(false, HttpStatus.NOT_IMPLEMENTED);
    }

}
