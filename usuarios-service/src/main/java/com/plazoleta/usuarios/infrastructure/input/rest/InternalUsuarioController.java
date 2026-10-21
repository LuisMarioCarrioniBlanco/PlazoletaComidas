package com.plazoleta.usuarios.infrastructure.input.rest;

import com.plazoleta.usuarios.application.port.input.UsuarioUseCase;
import com.plazoleta.usuarios.domain.model.Usuario;
import com.plazoleta.usuarios.infrastructure.input.rest.dto.UsuarioRequests;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Hidden;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@RestController
@Hidden
@RequestMapping("/internal/usuarios")
public class InternalUsuarioController {
    private final UsuarioUseCase usuarios;
    private final String internalToken;

    public InternalUsuarioController(UsuarioUseCase usuarios,
                                     @Value("${plazoleta.internal-token}") String internalToken) {
        this.usuarios = usuarios;
        this.internalToken = internalToken;
    }

    @GetMapping("/{id}")
    public UsuarioInternoResponse consultar(@PathVariable Long id,
                                            @RequestHeader("X-Internal-Token") String token) {
        validarToken(token);
        return UsuarioInternoResponse.desde(usuarios.consultarInterno(id));
    }

    @PostMapping("/empleados")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioInternoResponse crearEmpleado(@RequestHeader("X-Internal-Token") String token,
            @Valid @RequestBody UsuarioRequests.CrearEmpleadoInternoRequest r) {
        validarToken(token);
        Usuario u = usuarios.crearEmpleado(new UsuarioUseCase.CrearEmpleado(
                r.nombre(), r.apellido(), r.documentoIdentidad(), r.celular(),
                r.correo(), r.clave(), r.restauranteId()));
        return UsuarioInternoResponse.desde(usuarios.consultarInterno(u.id()));
    }

    private void validarToken(String token) {
        boolean valido = MessageDigest.isEqual(
                internalToken.getBytes(StandardCharsets.UTF_8),
                (token == null ? "" : token).getBytes(StandardCharsets.UTF_8));
        if (!valido) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token interno inválido");
        }
    }

    public record UsuarioInternoResponse(Long id, String correo, String rol, Long restauranteId) {
        static UsuarioInternoResponse desde(UsuarioUseCase.UsuarioInterno u) {
            return new UsuarioInternoResponse(u.id(), u.correo(), u.rol().name(), u.restauranteId());
        }
    }
}
