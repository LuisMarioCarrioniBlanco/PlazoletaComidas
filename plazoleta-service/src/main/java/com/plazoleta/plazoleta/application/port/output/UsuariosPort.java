package com.plazoleta.plazoleta.application.port.output;

import com.plazoleta.plazoleta.domain.model.UsuarioRemoto;

public interface UsuariosPort {
    UsuarioRemoto buscarPorId(Long id);
    UsuarioRemoto crearEmpleado(CrearEmpleadoData empleado, Long restauranteId);

    record CrearEmpleadoData(String nombre, String apellido, String documentoIdentidad,
                             String celular, String correo, String clave) {}
}
