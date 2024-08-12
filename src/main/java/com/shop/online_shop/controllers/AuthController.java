package com.shop.online_shop.controllers;

import com.shop.online_shop.dto.AuthResponseDto;
import com.shop.online_shop.entities.Roles;
import com.shop.online_shop.entities.UserEntity;
import com.shop.online_shop.repositories.RoleRepository;
import com.shop.online_shop.repositories.UserRepository;
import com.shop.online_shop.security.JWTGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@CrossOrigin(origins = "*") //http://localhost:5173"
@RequestMapping("/api/v1/auth")
public class AuthController {
    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private JWTGenerator jwtGenerator;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository,
                          RoleRepository roleRepository, PasswordEncoder passwordEncoder, JWTGenerator jwtGenerator) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtGenerator = jwtGenerator;
    }


    @PostMapping("test")
    public ResponseEntity<String> test(){
        return new ResponseEntity<>("Successful post", HttpStatus.OK);
    }

    public record RegisterDto(
        String email,
        String password
    ){}

    public record LoginDto(
            String email,
            String password
    ){}

    @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody RegisterDto request){
        if(userRepository.existsByEmail(request.email)){
            return new ResponseEntity<>("Username is taken", HttpStatus.BAD_REQUEST);
        }

        UserEntity user = new UserEntity();
        user.setEmail(request.email);
        user.setPassword(passwordEncoder.encode(request.password));

        Roles roles = roleRepository.findByName("ADMIN").get();
        user.setRoles(Collections.singletonList(roles));

        userRepository.save(user);

        return new ResponseEntity<>("User registered success", HttpStatus.OK);
    }

    @PostMapping("login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDto request){
        Authentication authentication = null;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email, request.password)
            );
        }
        catch(Exception e){
            return new ResponseEntity<>(new AuthResponseDto(null,"Username or password is incorrect"), HttpStatus.UNAUTHORIZED);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtGenerator.generateToken(authentication);

        return new ResponseEntity<>(new AuthResponseDto(token, "Successfully logged in"), HttpStatus.OK);
    }

    @GetMapping("login")
    public ResponseEntity<String> testLogin(@RequestBody String request){
        System.out.println(request);
        return new ResponseEntity<>("Get operation works", HttpStatus.OK);
    }

    @GetMapping("register")
    public ResponseEntity<String> getRegister(){
        return new ResponseEntity<>("Guess it's working", HttpStatus.OK);
    }
}



