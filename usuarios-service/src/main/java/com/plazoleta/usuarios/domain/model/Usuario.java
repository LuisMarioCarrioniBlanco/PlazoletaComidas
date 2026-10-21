package com.plazoleta.usuarios.domain.model;

import java.time.LocalDate;

public record Usuario(
        Long id,
        String nombre,
        String apellido,
        String documentoIdentidad,
        String celular,
        LocalDate fechaNacimiento,
        String correo,
        String passwordHash,
        Rol rol,
        Long restauranteId
) {}
