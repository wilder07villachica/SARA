package com.diplomado.ms_auth.controller;

import com.diplomado.ms_auth.dto.SessionStatusDTO;
import com.diplomado.ms_auth.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username,
                                   @RequestParam String password,
                                   HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        return ResponseEntity.ok(authService.login(username, password, ip));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam String username) {
        return ResponseEntity.ok(authService.logout(username));
    }

    @GetMapping("/session")
    public ResponseEntity<SessionStatusDTO> session(@RequestParam String username) {
        return ResponseEntity.ok(authService.sessionStatus(username));
    }
}
