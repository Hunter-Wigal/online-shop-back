package com.shop.online_shop.controllers;

import com.shop.online_shop.dto.auth.AuthResponseDto;
import com.shop.online_shop.dto.auth.LoginDto;
import com.shop.online_shop.dto.auth.RegisterDto;
import com.shop.online_shop.entities.Roles;
import com.shop.online_shop.entities.User;
import com.shop.online_shop.repositories.RoleRepository;
import com.shop.online_shop.repositories.UserRepository;
import com.shop.online_shop.security.JWTGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Collections;

@RestController
//@CrossOrigin(origins = "${ALLOWED_ORIGINS}") //http://localhost:5173"
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTGenerator jwtGenerator;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository,
                          RoleRepository roleRepository, PasswordEncoder passwordEncoder, JWTGenerator jwtGenerator) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtGenerator = jwtGenerator;
    }


    @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody RegisterDto request) {
        if (userRepository.existsByEmail(request.email)) {
            return new ResponseEntity<>("Username is taken", HttpStatus.BAD_REQUEST);
        }

        // Create new user and save to database
        User user = new User();
        user.setEmail(request.email);
        user.setPassword(passwordEncoder.encode(request.password));

        Roles roles = roleRepository.findByName("USER").get();
        user.setRoles(Collections.singletonList(roles));

        userRepository.save(user);

        return new ResponseEntity<>("User registered success", HttpStatus.OK);
    }

    @PostMapping("login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDto request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email, request.password)
            );
        } catch (Exception e) {
            return new ResponseEntity<>(new AuthResponseDto(null, "Username or password is incorrect"), HttpStatus.UNAUTHORIZED);
        }
        // Set current context to auth provided by logging in
        SecurityContextHolder.getContext().setAuthentication(authentication);


        // Return a token for keeping the session
        String token = jwtGenerator.generateToken(authentication);

        return new ResponseEntity<>(new AuthResponseDto(token, "Successfully logged in"), HttpStatus.OK);
    }

    @PostMapping("logout")
    public ResponseEntity<String> logout() {
        SecurityContextHolder.clearContext();

        return new ResponseEntity<>("Successfully logged out", HttpStatus.ACCEPTED);
    }

    @GetMapping("valid")
    public ResponseEntity<Boolean> valid() {
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        SimpleGrantedAuthority user = new SimpleGrantedAuthority(roleRepository.findByName("USER").get().getName());
        try {
            if (authorities.contains(user))
                return new ResponseEntity<>(true, HttpStatus.OK);
            else
                return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("admin_check")
    public ResponseEntity<Boolean> isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean hasAdminRole = auth.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ADMIN"));
        return new ResponseEntity<>(hasAdminRole, HttpStatus.OK);
    }
}