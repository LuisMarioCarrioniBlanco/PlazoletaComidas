package com.plazoleta.usuarios.infrastructure.input.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public final class UsuarioRequests {
    private UsuarioRequests() {}

    public record CrearPropietarioRequest(
            @NotBlank @Size(max = 80) String nombre,
            @NotBlank @Size(max = 80) String apellido,
            @NotBlank @Pattern(regexp = "\\d+") @Size(max = 30) String documentoIdentidad,
            @NotBlank @Pattern(regexp = "^(?:\\+\\d{1,12}|\\d{1,13})$") String celular,
            @NotNull LocalDate fechaNacimiento,
            @NotBlank @Email @Size(max = 180) String correo,
            @NotBlank @Size(min = 8, max = 72) String clave
    ) {}

    public record CrearClienteRequest(
            @NotBlank @Size(max = 80) String nombre,
            @NotBlank @Size(max = 80) String apellido,
            @NotBlank @Pattern(regexp = "\\d+") @Size(max = 30) String documentoIdentidad,
            @NotBlank @Pattern(regexp = "^(?:\\+\\d{1,12}|\\d{1,13})$") String celular,
            @NotBlank @Email @Size(max = 180) String correo,
            @NotBlank @Size(min = 8, max = 72) String clave
    ) {}

    public record CrearEmpleadoInternoRequest(
            @NotBlank @Size(max = 80) String nombre,
            @NotBlank @Size(max = 80) String apellido,
            @NotBlank @Pattern(regexp = "\\d+") @Size(max = 30) String documentoIdentidad,
            @NotBlank @Pattern(regexp = "^(?:\\+\\d{1,12}|\\d{1,13})$") String celular,
            @NotBlank @Email @Size(max = 180) String correo,
            @NotBlank @Size(min = 8, max = 72) String clave,
            @NotNull Long restauranteId
    ) {}

    public record LoginRequest(
            @NotBlank @Email String correo,
            @NotBlank String clave
    ) {}
}
