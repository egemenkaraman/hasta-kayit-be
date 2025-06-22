package com.hastane.controller;

import com.hastane.security.JwtUtil;
import com.hastane.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.hastane.model.User;
import com.hastane.model.UserRole;
import com.hastane.repository.DoctorRepository;
import com.hastane.repository.PatientRepository;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        try {
            String username = loginRequest.get("username");
            String password = loginRequest.get("password");
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            Long userId = null;
            String role = null;
            if (userDetails instanceof com.hastane.security.CustomUserDetails customUserDetails) {
                role = customUserDetails.getRole();
                com.hastane.model.User user = customUserDetails.getUser();
                if ("DOCTOR".equals(role)) {
                    userId = doctorRepository.findByUserId(user.getId()).map(d -> d.getId()).orElse(null);
                } else if ("PATIENT".equals(role)) {
                    userId = patientRepository.findByUserId(user.getId()).map(p -> p.getId()).orElse(null);
                } else {
                    userId = user.getId();
                }
            }
            String token = jwtUtil.generateToken(userDetails.getUsername(), userDetails.getAuthorities().iterator().next().getAuthority(), userId);
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("userId", userId);
            response.put("role", role);
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Kullanıcı adı veya şifre hatalı");
        }
    }

    @PostMapping("/register-staff")
    public ResponseEntity<?> registerStaff(@RequestBody Map<String, String> registerRequest) {
        String username = registerRequest.get("username");
        String password = registerRequest.get("password");
        String name = registerRequest.get("name");
        String surname = registerRequest.get("surname");
        String email = registerRequest.get("email");
        String phone = registerRequest.get("phone");

        if (username == null || password == null || name == null || surname == null) {
            return ResponseEntity.badRequest().body("Zorunlu alanlar eksik");
        }
        if (userDetailsService.getUserRepository().existsByUsername(username)) {
            return ResponseEntity.badRequest().body("Bu kullanıcı adı zaten alınmış");
        }
        if (email != null && userDetailsService.getUserRepository().existsByEmail(email)) {
            return ResponseEntity.badRequest().body("Bu email zaten alınmış");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(UserRole.STAFF);
        user.setName(name);
        user.setSurname(surname);
        user.setEmail(email);
        user.setPhone(phone);
        userDetailsService.getUserRepository().save(user);
        return ResponseEntity.ok("Personel kaydı başarılı");
    }
} 