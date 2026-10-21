package com.plazoleta.usuarios.application.port.output;

import com.plazoleta.usuarios.domain.model.Usuario;

import java.util.Optional;

public interface UsuarioRepositoryPort {
    Usuario guardar(Usuario usuario);
    Optional<Usuario> porId(Long id);
    Optional<Usuario> porCorreo(String correo);
    boolean existeCorreo(String correo);
    boolean existeDocumento(String documentoIdentidad);
}
