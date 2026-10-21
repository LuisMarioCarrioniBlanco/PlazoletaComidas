package com.plazoleta.usuarios.infrastructure.input.rest;

import com.plazoleta.usuarios.application.port.input.UsuarioUseCase;
import com.plazoleta.usuarios.infrastructure.input.rest.dto.UsuarioRequests;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UsuarioUseCase usuarios;

    public AuthController(UsuarioUseCase usuarios) {
        this.usuarios = usuarios;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody UsuarioRequests.LoginRequest request) {
        UsuarioUseCase.LoginResult result = usuarios.iniciarSesion(
                new UsuarioUseCase.Login(request.correo(), request.clave()));
        return new LoginResponse(result.token(), "Bearer", result.id(), result.correo(), result.rol().name());
    }

    public record LoginResponse(String token, String tipo, Long id, String correo, String rol) {}
}
