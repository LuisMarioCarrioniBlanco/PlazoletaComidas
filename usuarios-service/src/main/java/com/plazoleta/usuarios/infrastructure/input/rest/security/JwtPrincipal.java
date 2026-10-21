package com.plazoleta.usuarios.infrastructure.input.rest.security;

public record JwtPrincipal(Long id, String correo, String rol) {}
