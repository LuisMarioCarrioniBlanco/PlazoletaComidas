package com.plazoleta.usuarios.infrastructure.output.persistence.adapter;

import com.plazoleta.usuarios.application.port.output.UsuarioRepositoryPort;
import com.plazoleta.usuarios.domain.model.Usuario;
import com.plazoleta.usuarios.infrastructure.output.persistence.entity.UsuarioEntity;
import com.plazoleta.usuarios.infrastructure.output.persistence.repository.UsuarioJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UsuarioPersistenceAdapter implements UsuarioRepositoryPort {
    private final UsuarioJpaRepository repository;

    public UsuarioPersistenceAdapter(UsuarioJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        UsuarioEntity entity = new UsuarioEntity();
        entity.setId(usuario.id());
        entity.setNombre(usuario.nombre());
        entity.setApellido(usuario.apellido());
        entity.setDocumentoIdentidad(usuario.documentoIdentidad());
        entity.setCelular(usuario.celular());
        entity.setFechaNacimiento(usuario.fechaNacimiento());
        entity.setCorreo(usuario.correo());
        entity.setPasswordHash(usuario.passwordHash());
        entity.setRol(usuario.rol());
        entity.setRestauranteId(usuario.restauranteId());
        return convertir(repository.save(entity));
    }

    @Override
    public Optional<Usuario> porId(Long id) {
        return repository.findById(id).map(this::convertir);
    }

    @Override
    public Optional<Usuario> porCorreo(String correo) {
        return repository.findByCorreo(correo).map(this::convertir);
    }

    @Override
    public boolean existeCorreo(String correo) {
        return repository.existsByCorreo(correo);
    }

    @Override
    public boolean existeDocumento(String documentoIdentidad) {
        return repository.existsByDocumentoIdentidad(documentoIdentidad);
    }

    private Usuario convertir(UsuarioEntity e) {
        return new Usuario(e.getId(), e.getNombre(), e.getApellido(), e.getDocumentoIdentidad(),
                e.getCelular(), e.getFechaNacimiento(), e.getCorreo(), e.getPasswordHash(),
                e.getRol(), e.getRestauranteId());
    }
}
