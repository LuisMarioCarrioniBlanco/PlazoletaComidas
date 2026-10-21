package com.plazoleta.usuarios.application.port.input;

import com.plazoleta.usuarios.domain.model.Rol;
import com.plazoleta.usuarios.domain.model.Usuario;

import java.time.LocalDate;

public interface UsuarioUseCase {
    Usuario crearPropietario(CrearPropietario command);
    Usuario crearCliente(CrearCliente command);
    Usuario crearEmpleado(CrearEmpleado command);
    LoginResult iniciarSesion(Login command);
    Usuario buscarPorId(Long id);
    UsuarioInterno consultarInterno(Long id);
    void inicializarAdministrador(String correo, String clave);

    record CrearPropietario(
            String nombre, String apellido, String documentoIdentidad,
            String celular, LocalDate fechaNacimiento, String correo, String clave
    ) {}
    record CrearCliente(
            String nombre, String apellido, String documentoIdentidad,
            String celular, String correo, String clave
    ) {}
    record CrearEmpleado(
            String nombre, String apellido, String documentoIdentidad,
            String celular, String correo, String clave, Long restauranteId
    ) {}
    record Login(String correo, String clave) {}
    record LoginResult(String token, Long id, String correo, Rol rol) {}
    record UsuarioInterno(Long id, String correo, Rol rol, Long restauranteId) {}
}
