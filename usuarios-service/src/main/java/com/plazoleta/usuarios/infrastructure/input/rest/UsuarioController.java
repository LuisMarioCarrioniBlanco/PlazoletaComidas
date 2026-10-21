package com.plazoleta.usuarios.infrastructure.input.rest;

import com.plazoleta.usuarios.application.port.input.UsuarioUseCase;
import com.plazoleta.usuarios.domain.model.Usuario;
import com.plazoleta.usuarios.infrastructure.input.rest.dto.UsuarioRequests;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {
    private final UsuarioUseCase usuarios;

    public UsuarioController(UsuarioUseCase usuarios) {
        this.usuarios = usuarios;
    }

    @PostMapping("/propietarios")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @SecurityRequirement(name = "bearerAuth")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse crearPropietario(@Valid @RequestBody UsuarioRequests.CrearPropietarioRequest r) {
        Usuario u = usuarios.crearPropietario(new UsuarioUseCase.CrearPropietario(
                r.nombre(), r.apellido(), r.documentoIdentidad(), r.celular(),
                r.fechaNacimiento(), r.correo(), r.clave()));
        return UsuarioResponse.desde(u);
    }

    @PostMapping("/clientes")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse crearCliente(@Valid @RequestBody UsuarioRequests.CrearClienteRequest r) {
        Usuario u = usuarios.crearCliente(new UsuarioUseCase.CrearCliente(
                r.nombre(), r.apellido(), r.documentoIdentidad(), r.celular(), r.correo(), r.clave()));
        return UsuarioResponse.desde(u);
    }

    public record UsuarioResponse(Long id, String nombre, String apellido, String documentoIdentidad,
                                  String celular, String correo, String rol, Long restauranteId) {
        static UsuarioResponse desde(Usuario u) {
            return new UsuarioResponse(u.id(), u.nombre(), u.apellido(), u.documentoIdentidad(),
                    u.celular(), u.correo(), u.rol().name(), u.restauranteId());
        }
    }
}
