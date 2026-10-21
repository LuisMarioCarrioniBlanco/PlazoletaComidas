package com.plazoleta.usuarios.infrastructure.configuration;

import com.plazoleta.usuarios.application.port.input.UsuarioUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InicializadorAdministrador {
    @Bean
    CommandLineRunner crearAdministradorInicial(
            UsuarioUseCase usuarios,
            @Value("${app.admin.email}") String correo,
            @Value("${app.admin.password}") String clave) {
        return args -> usuarios.inicializarAdministrador(correo, clave);
    }
}
