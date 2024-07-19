package net.springboot.backend.Controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.springboot.backend.DTO.RequestResponseDTO;
import net.springboot.backend.Entity.OurUsers;
import net.springboot.backend.Service.UserManagementService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserManagementController {

    private final UserManagementService service;

    @PostMapping("/auth/register")
    public ResponseEntity<RequestResponseDTO> register(@RequestBody RequestResponseDTO registrationRequest) {
        return ResponseEntity.ok(service.register(registrationRequest));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<RequestResponseDTO> login(@RequestBody RequestResponseDTO loginRequest) {
        return ResponseEntity.ok(service.login(loginRequest));
    }

    @GetMapping("/admin/get-all-users")
    public ResponseEntity<RequestResponseDTO> getAllUsers() {
        return ResponseEntity.ok(service.getAllUsers());
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<RequestResponseDTO> refreshToken(@RequestBody RequestResponseDTO refreshTokenRequest) {
        return ResponseEntity.ok(service.refreshToken(refreshTokenRequest));
    }

    @DeleteMapping("/admin/delete-user/{id}")
    public ResponseEntity<RequestResponseDTO> deleteUser(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(service.deleteUser(id));
    }

    @GetMapping("/admin/get-user/{id}")
    public ResponseEntity<RequestResponseDTO> getUserById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(service.getUserById(id));
    }

    @PutMapping("/admin/update-user/{id}")
    public ResponseEntity<RequestResponseDTO> updateUser(@PathVariable("id") Integer id, @RequestBody OurUsers updatedUser) {
        return ResponseEntity.ok(service.updateUser(id,updatedUser));
    }

//    @GetMapping("/admin/get-info/{email}")
//    public ResponseEntity<RequestResponseDTO> getUserByEmail(@PathVariable("email") String email) {
//        return ResponseEntity.ok(service.getMyInfo(email));
//    }

    @GetMapping("/adminuser/get-profile")
    public ResponseEntity<RequestResponseDTO> getMyProfile() {
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String email=authentication.getName();
        RequestResponseDTO response=service.getMyInfo(email);
        return ResponseEntity.status(response.getStatuscode()).body(response);
    }
}
