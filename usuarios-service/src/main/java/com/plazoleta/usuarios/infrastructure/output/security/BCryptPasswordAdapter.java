package com.plazoleta.usuarios.infrastructure.output.security;

import com.plazoleta.usuarios.application.port.output.PasswordHashPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordAdapter implements PasswordHashPort {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public String codificar(String clave) {
        return encoder.encode(clave);
    }

    @Override
    public boolean coincide(String clave, String hash) {
        return hash != null && encoder.matches(clave, hash);
    }
}
