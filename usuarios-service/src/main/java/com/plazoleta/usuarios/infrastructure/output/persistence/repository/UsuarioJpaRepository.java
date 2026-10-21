package com.plazoleta.usuarios.infrastructure.output.persistence.repository;

import com.plazoleta.usuarios.infrastructure.output.persistence.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, Long> {
    Optional<UsuarioEntity> findByCorreo(String correo);
    boolean existsByCorreo(String correo);
    boolean existsByDocumentoIdentidad(String documentoIdentidad);
}
