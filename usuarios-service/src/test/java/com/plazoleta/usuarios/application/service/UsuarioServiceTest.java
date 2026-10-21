package com.plazoleta.usuarios.application.service;

import com.plazoleta.usuarios.application.port.output.PasswordHashPort;
import com.plazoleta.usuarios.application.port.output.TokenPort;
import com.plazoleta.usuarios.application.port.output.UsuarioRepositoryPort;
import com.plazoleta.usuarios.domain.exception.ReglaNegocioException;
import com.plazoleta.usuarios.domain.exception.CredencialesInvalidasException;
import com.plazoleta.usuarios.domain.model.Rol;
import com.plazoleta.usuarios.domain.model.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {
    @Mock UsuarioRepositoryPort usuarios;
    @Mock PasswordHashPort passwordHash;
    @Mock TokenPort tokens;
    @InjectMocks UsuarioService service;

    @Test
    void creaPropietarioAdultoYGuardaClaveCodificada() {
        when(usuarios.existeCorreo("propietario@ejemplo.com")).thenReturn(false);
        when(usuarios.existeDocumento("123456")).thenReturn(false);
        when(passwordHash.codificar("Clave123!")).thenReturn("bcrypt-hash");
        when(usuarios.guardar(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario creado = service.crearPropietario(new com.plazoleta.usuarios.application.port.input.UsuarioUseCase.CrearPropietario(
                "Ana", "Pérez", "123456", "+573001234567",
                LocalDate.now().minusYears(25), "Propietario@Ejemplo.com", "Clave123!"));

        assertEquals(Rol.PROPIETARIO, creado.rol());
        assertEquals("propietario@ejemplo.com", creado.correo());
        assertEquals("bcrypt-hash", creado.passwordHash());
    }

    @Test
    void rechazaPropietarioMenorDeEdad() {
        assertThrows(ReglaNegocioException.class, () -> service.crearPropietario(
                new com.plazoleta.usuarios.application.port.input.UsuarioUseCase.CrearPropietario(
                        "Ana", "Pérez", "123456", "3001234567",
                        LocalDate.now().minusYears(17), "ana@ejemplo.com", "Clave123!")));
        verify(usuarios, never()).guardar(any());
    }

    @Test
    void creaClienteConRolCliente() {
        when(usuarios.existeCorreo("cliente@ejemplo.com")).thenReturn(false);
        when(usuarios.existeDocumento("789012")).thenReturn(false);
        when(passwordHash.codificar("Clave123!")).thenReturn("hash-cliente");
        when(usuarios.guardar(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario creado = service.crearCliente(
                new com.plazoleta.usuarios.application.port.input.UsuarioUseCase.CrearCliente(
                        "Juan", "Díaz", "789012", "3011234567",
                        "cliente@ejemplo.com", "Clave123!"));

        assertEquals(Rol.CLIENTE, creado.rol());
        assertNull(creado.fechaNacimiento());
    }

    @Test
    void loginCorrectoGeneraJwt() {
        Usuario existente = new Usuario(4L, "Carlos", "Díaz", "789012", "3011234567",
                null, "cliente@ejemplo.com", "hash", Rol.CLIENTE, null);
        when(usuarios.porCorreo("cliente@ejemplo.com")).thenReturn(Optional.of(existente));
        when(passwordHash.coincide("Clave123!", "hash")).thenReturn(true);
        when(tokens.generar(4L, "cliente@ejemplo.com", "CLIENTE")).thenReturn("jwt-de-prueba");

        var login = service.iniciarSesion(
                new com.plazoleta.usuarios.application.port.input.UsuarioUseCase.Login(
                        "Cliente@Ejemplo.com", "Clave123!"));

        assertEquals("jwt-de-prueba", login.token());
        assertEquals(Rol.CLIENTE, login.rol());
    }

    @Test
    void loginRechazaClaveIncorrecta() {
        Usuario existente = new Usuario(4L, "Carlos", "Díaz", "789012", "3011234567",
                null, "cliente@ejemplo.com", "hash", Rol.CLIENTE, null);
        when(usuarios.porCorreo("cliente@ejemplo.com")).thenReturn(Optional.of(existente));
        when(passwordHash.coincide("incorrecta", "hash")).thenReturn(false);

        assertThrows(CredencialesInvalidasException.class, () -> service.iniciarSesion(
                new com.plazoleta.usuarios.application.port.input.UsuarioUseCase.Login(
                        "cliente@ejemplo.com", "incorrecta")));
    }

}
