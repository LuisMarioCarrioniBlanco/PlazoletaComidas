package com.plazoleta.usuarios.application.port.output;

public interface PasswordHashPort {
    String codificar(String clave);
    boolean coincide(String clave, String hash);
}
