package com.plazoleta.plazoleta.application.service;

import com.plazoleta.plazoleta.application.port.input.PlazoletaUseCase;
import com.plazoleta.plazoleta.application.port.output.PlatoRepositoryPort;
import com.plazoleta.plazoleta.application.port.output.RestauranteRepositoryPort;
import com.plazoleta.plazoleta.application.port.output.UsuariosPort;
import com.plazoleta.plazoleta.domain.model.Pagina;
import com.plazoleta.plazoleta.domain.model.UsuarioRemoto;
import com.plazoleta.plazoleta.domain.exception.ReglaNegocioException;
import com.plazoleta.plazoleta.domain.model.Plato;
import com.plazoleta.plazoleta.domain.model.Restaurante;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlazoletaServiceTest {
    @Mock RestauranteRepositoryPort restaurantes;
    @Mock PlatoRepositoryPort platos;
    @Mock UsuariosPort usuarios;
    @InjectMocks PlazoletaService service;

    @BeforeEach
    void prepararRestaurante() {
        lenient().when(restaurantes.porId(10L)).thenReturn(Optional.of(
                new Restaurante(10L, "Sazón", "12345", "Calle 1", "3001234567",
                        "https://ejemplo.com/logo.png", 7L)));
    }

    @Test
    void platoNuevoQuedaActivo() {
        when(platos.guardar(any(Plato.class))).thenAnswer(inv -> inv.getArgument(0));

        Plato plato = service.crearPlato(10L, 7L,
                new PlazoletaUseCase.CrearPlato("Arroz", 15000, "Arroz especial",
                        "https://ejemplo.com/arroz.png", "Almuerzo"));

        assertTrue(plato.activo());
        assertEquals(10L, plato.restauranteId());
    }

    @Test
    void propietarioNoPuedeCrearPlatoEnRestauranteAjeno() {
        assertThrows(ReglaNegocioException.class, () -> service.crearPlato(10L, 99L,
                new PlazoletaUseCase.CrearPlato("Arroz", 15000, "Arroz especial",
                        "https://ejemplo.com/arroz.png", "Almuerzo")));
        verify(platos, never()).guardar(any());
    }

    @Test
    void creaRestauranteSoloConPropietarioExistente() {
        when(restaurantes.existeNit("900123456")).thenReturn(false);
        when(usuarios.buscarPorId(7L)).thenReturn(
                new UsuarioRemoto(7L, "propietario@ejemplo.com", "PROPIETARIO", null));
        when(restaurantes.guardar(any(Restaurante.class))).thenAnswer(inv -> inv.getArgument(0));

        Restaurante creado = service.crearRestaurante(new PlazoletaUseCase.CrearRestaurante(
                "Restaurante Caribe", "900123456", "Calle 10", "3001234567",
                "https://ejemplo.com/logo.png", 7L));

        assertEquals("Restaurante Caribe", creado.nombre());
        assertEquals(7L, creado.propietarioId());
    }

    @Test
    void modificarPlatoSoloActualizaPrecioYDescripcion() {
        Plato actual = new Plato(20L, "Arroz", 15000, "Descripción original",
                "https://ejemplo.com/arroz.png", "Almuerzo", true, 10L);
        when(platos.porId(20L)).thenReturn(Optional.of(actual));
        when(platos.guardar(any(Plato.class))).thenAnswer(inv -> inv.getArgument(0));

        Plato actualizado = service.modificarPlato(10L, 20L, 7L,
                new PlazoletaUseCase.ModificarPlato(18000, null));

        assertEquals(18000, actualizado.precio());
        assertEquals("Descripción original", actualizado.descripcion());
        assertEquals("Arroz", actualizado.nombre());
        assertEquals(actual.urlImagen(), actualizado.urlImagen());
        assertTrue(actualizado.activo());
    }

    @Test
    void propietarioPuedeDesactivarUnPlatoPropio() {
        Plato actual = new Plato(20L, "Arroz", 15000, "Descripción",
                "https://ejemplo.com/arroz.png", "Almuerzo", true, 10L);
        when(platos.porId(20L)).thenReturn(Optional.of(actual));
        when(platos.guardar(any(Plato.class))).thenAnswer(inv -> inv.getArgument(0));

        Plato actualizado = service.cambiarEstadoPlato(10L, 20L, 7L, false);

        assertFalse(actualizado.activo());
        assertEquals(actual.nombre(), actualizado.nombre());
    }

    @Test
    void propietarioCreaEmpleadoAsociadoAlRestaurante() {
        when(usuarios.crearEmpleado(any(UsuariosPort.CrearEmpleadoData.class), eq(10L)))
                .thenReturn(new UsuarioRemoto(15L, "empleado@ejemplo.com", "EMPLEADO", 10L));

        UsuarioRemoto empleado = service.crearEmpleado(10L, 7L,
                new PlazoletaUseCase.CrearEmpleado("Luis", "Pérez", "12345",
                        "3001234567", "empleado@ejemplo.com", "Clave123!"));

        assertEquals("EMPLEADO", empleado.rol());
        assertEquals(10L, empleado.restauranteId());
    }

    @Test
    void listarRestaurantesSolicitaPaginaConParametros() {
        when(restaurantes.listar(1, 5)).thenReturn(new Pagina<>(
                java.util.List.of(
                        new Restaurante(2L, "Bistro", "123", "Calle B", "3001234567",
                                "https://ejemplo.com/b.png", 7L)),
                1, 5, 6, 2));

        Pagina<Restaurante> pagina = service.listarRestaurantes(1, 5);

        assertEquals(1, pagina.pagina());
        assertEquals(5, pagina.tamanio());
        assertEquals(6, pagina.totalElementos());
        assertEquals("Bistro", pagina.contenido().get(0).nombre());
    }

    @Test
    void noPermiteQueOtroPropietarioCambieUnPlato() {
        assertThrows(
        ReglaNegocioException.class,
                () -> service.cambiarEstadoPlato(10L, 20L, 99L, false)
        );

        verify(platos, never()).guardar(any());
    }

}
