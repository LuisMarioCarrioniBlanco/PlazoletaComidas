package com.plazoleta.plazoleta.domain.model;

public record Plato(
        Long id,
        String nombre,
        Integer precio,
        String descripcion,
        String urlImagen,
        String categoria,
        boolean activo,
        Long restauranteId
) {}
