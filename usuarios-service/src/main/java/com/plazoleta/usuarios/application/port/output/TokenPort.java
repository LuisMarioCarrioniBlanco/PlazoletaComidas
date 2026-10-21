package com.plazoleta.usuarios.application.port.output;

public interface TokenPort {
    String generar(Long usuarioId, String correo, String rol);
    DatosToken validar(String token);

    record DatosToken(Long usuarioId, String correo, String rol) {}
}
