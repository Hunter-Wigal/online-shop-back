package com.shop.online_shop.controllers;

import com.shop.online_shop.entities.UserEntity;
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
    public List<UserEntity> getAllUsers(){
        return this.userRepository.findAll();
    }

    public record NewUserRequest(
            String name,
            String email,
            Integer age
    ){}
    @PostMapping
    public void addUser(@RequestBody NewUserRequest request){
        UserEntity userEntity = new UserEntity();
        userEntity.setName(request.name());
        userEntity.setEmail(request.email());
        userEntity.setAge(request.age());

        this.userRepository.save(userEntity);
    }

    public record UserResponse(
            String email,
            String name,
            Integer age
    ){}

    // TODO make this use body instead of param
    @GetMapping("user")
    public ResponseEntity<UserResponse> getUser(@RequestParam("username") String username){
        Optional<UserEntity> check = this.userRepository.findByEmail(username);

        UserResponse response;
        if(check.isPresent()){
            response = new UserResponse(check.get().getEmail(), check.get().getName(), check.get().getAge());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("user_id")
    public ResponseEntity<UserResponse> getUser(@RequestBody Integer id){
        Optional<UserEntity> check = this.userRepository.findById(id);

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
        Optional<UserEntity> check = this.userRepository.findByEmail(request.email());

        if(check.isEmpty()){
            return new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
        }
        UserEntity user = check.get();
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
    public ResponseEntity delete(@PathVariable("username") String username){
        // implement logic here
        return new ResponseEntity(false, HttpStatus.NOT_IMPLEMENTED);
    }

}
