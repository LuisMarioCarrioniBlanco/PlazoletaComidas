package com.plazoleta.plazoleta.infrastructure.input.rest.dto;

import jakarta.validation.constraints.*;

public final class ApiRequests {
    private ApiRequests() {}

    public record CrearRestauranteRequest(
            @NotBlank @Size(max = 120) @Pattern(regexp = ".*[A-Za-zÁÉÍÓÚáéíóúÑñ].*")
            String nombre,
            @NotBlank @Pattern(regexp = "\\d+") @Size(max = 30) String nit,
            @NotBlank @Size(max = 200) String direccion,
            @NotBlank @Pattern(regexp = "^(?:\\+\\d{1,12}|\\d{1,13})$") String telefono,
            @NotBlank @Pattern(regexp = "https?://.+") @Size(max = 500) String urlLogo,
            @NotNull @Positive Long propietarioId
    ) {}

    public record CrearPlatoRequest(
            @NotBlank @Size(max = 120) String nombre,
            @NotNull @Positive Integer precio,
            @NotBlank @Size(max = 1000) String descripcion,
            @NotBlank @Pattern(regexp = "https?://.+") @Size(max = 500) String urlImagen,
            @NotBlank @Size(max = 80) String categoria
    ) {}

    public record ModificarPlatoRequest(
            @Positive Integer precio,
            @Size(min = 1, max = 1000) String descripcion
    ) {}

    public record EstadoPlatoRequest(@NotNull Boolean activo) {}

    public record CrearEmpleadoRequest(
            @NotBlank @Size(max = 80) String nombre,
            @NotBlank @Size(max = 80) String apellido,
            @NotBlank @Pattern(regexp = "\\d+") @Size(max = 30) String documentoIdentidad,
            @NotBlank @Pattern(regexp = "^(?:\\+\\d{1,12}|\\d{1,13})$") String celular,
            @NotBlank @Email @Size(max = 180) String correo,
            @NotBlank @Size(min = 8, max = 72) String clave
    ) {}
}
