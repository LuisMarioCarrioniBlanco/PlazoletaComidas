package com.plazoleta.usuarios.application.service;

import com.plazoleta.usuarios.application.port.input.UsuarioUseCase;
import com.plazoleta.usuarios.application.port.output.PasswordHashPort;
import com.plazoleta.usuarios.application.port.output.TokenPort;
import com.plazoleta.usuarios.application.port.output.UsuarioRepositoryPort;
import com.plazoleta.usuarios.domain.exception.ConflictoException;
import com.plazoleta.usuarios.domain.exception.CredencialesInvalidasException;
import com.plazoleta.usuarios.domain.exception.RecursoNoEncontradoException;
import com.plazoleta.usuarios.domain.exception.ReglaNegocioException;
import com.plazoleta.usuarios.domain.model.Rol;
import com.plazoleta.usuarios.domain.model.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.Locale;

@Service
public class UsuarioService implements UsuarioUseCase {
    private final UsuarioRepositoryPort usuarios;
    private final PasswordHashPort passwordHash;
    private final TokenPort tokens;

    public UsuarioService(UsuarioRepositoryPort usuarios, PasswordHashPort passwordHash, TokenPort tokens) {
        this.usuarios = usuarios;
        this.passwordHash = passwordHash;
        this.tokens = tokens;
    }

    @Override
    @Transactional
    public Usuario crearPropietario(CrearPropietario c) {
        validarCampos(c.nombre(), c.apellido(), c.documentoIdentidad(), c.celular(), c.correo(), c.clave());
        if (c.fechaNacimiento() == null ||
                Period.between(c.fechaNacimiento(), LocalDate.now()).getYears() < 18) {
            throw new ReglaNegocioException("El propietario debe ser mayor de edad");
        }
        validarIdentidadDisponible(c.correo(), c.documentoIdentidad());
        return usuarios.guardar(new Usuario(null, c.nombre().trim(), c.apellido().trim(),
                c.documentoIdentidad(), c.celular(), c.fechaNacimiento(), normalizarCorreo(c.correo()),
                passwordHash.codificar(c.clave()), Rol.PROPIETARIO, null));
    }

    @Override
    @Transactional
    public Usuario crearCliente(CrearCliente c) {
        validarCampos(c.nombre(), c.apellido(), c.documentoIdentidad(), c.celular(), c.correo(), c.clave());
        validarIdentidadDisponible(c.correo(), c.documentoIdentidad());
        return usuarios.guardar(new Usuario(null, c.nombre().trim(), c.apellido().trim(),
                c.documentoIdentidad(), c.celular(), null, normalizarCorreo(c.correo()),
                passwordHash.codificar(c.clave()), Rol.CLIENTE, null));
    }

    @Override
    @Transactional
    public Usuario crearEmpleado(CrearEmpleado c) {
        validarCampos(c.nombre(), c.apellido(), c.documentoIdentidad(), c.celular(), c.correo(), c.clave());
        if (c.restauranteId() == null || c.restauranteId() <= 0) {
            throw new ReglaNegocioException("El empleado debe estar asociado a un restaurante");
        }
        validarIdentidadDisponible(c.correo(), c.documentoIdentidad());
        return usuarios.guardar(new Usuario(null, c.nombre().trim(), c.apellido().trim(),
                c.documentoIdentidad(), c.celular(), null, normalizarCorreo(c.correo()),
                passwordHash.codificar(c.clave()), Rol.EMPLEADO, c.restauranteId()));
    }

    @Override
    public LoginResult iniciarSesion(Login c) {
        String correo = normalizarCorreo(c.correo());
        Usuario usuario = usuarios.porCorreo(correo)
                .orElseThrow(CredencialesInvalidasException::new);
        if (!passwordHash.coincide(c.clave(), usuario.passwordHash())) {
            throw new CredencialesInvalidasException();
        }
        return new LoginResult(tokens.generar(usuario.id(), usuario.correo(), usuario.rol().name()),
                usuario.id(), usuario.correo(), usuario.rol());
    }

    @Override
    public Usuario buscarPorId(Long id) {
        return usuarios.porId(id).orElseThrow(() ->
                new RecursoNoEncontradoException("No existe un usuario con id " + id));
    }

    @Override
    public UsuarioInterno consultarInterno(Long id) {
        Usuario u = buscarPorId(id);
        return new UsuarioInterno(u.id(), u.correo(), u.rol(), u.restauranteId());
    }

    @Override
    @Transactional
    public void inicializarAdministrador(String correo, String clave) {
        String normalizado = normalizarCorreo(correo);
        if (usuarios.porCorreo(normalizado).isPresent()) {
            return;
        }
        usuarios.guardar(new Usuario(null, "Administrador", "Sistema", "999999999999",
                "+570000000000", LocalDate.of(1990, 1, 1), normalizado,
                passwordHash.codificar(clave), Rol.ADMINISTRADOR, null));
    }

    private void validarIdentidadDisponible(String correo, String documento) {
        if (usuarios.existeCorreo(normalizarCorreo(correo))) {
            throw new ConflictoException("Ya existe una cuenta con ese correo");
        }
        if (usuarios.existeDocumento(documento)) {
            throw new ConflictoException("Ya existe una cuenta con ese documento");
        }
    }

    private void validarCampos(String nombre, String apellido, String documento,
                               String celular, String correo, String clave) {
        if (esVacio(nombre) || esVacio(apellido) || esVacio(documento) ||
                esVacio(celular) || esVacio(correo) || esVacio(clave)) {
            throw new ReglaNegocioException("Todos los campos obligatorios deben tener valor");
        }
        if (!documento.matches("\\d+")) {
            throw new ReglaNegocioException("El documento de identidad debe ser numérico");
        }
        if (!celular.matches("^(?:\\+\\d{1,12}|\\d{1,13})$")) {
            throw new ReglaNegocioException("El celular debe tener máximo 13 caracteres y solo admite un + inicial");
        }
        if (clave.length() < 8) {
            throw new ReglaNegocioException("La clave debe tener al menos 8 caracteres");
        }
    }

    private boolean esVacio(String valor) {
        return valor == null || valor.isBlank();
    }

    private String normalizarCorreo(String correo) {
        return correo == null ? "" : correo.trim().toLowerCase(Locale.ROOT);
    }
}
