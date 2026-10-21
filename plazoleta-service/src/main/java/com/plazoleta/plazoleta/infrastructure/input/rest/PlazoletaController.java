package com.plazoleta.plazoleta.infrastructure.input.rest;

import com.plazoleta.plazoleta.application.port.input.PlazoletaUseCase;
import com.plazoleta.plazoleta.domain.model.Pagina;
import com.plazoleta.plazoleta.domain.model.Plato;
import com.plazoleta.plazoleta.domain.model.Restaurante;
import com.plazoleta.plazoleta.domain.model.UsuarioRemoto;
import com.plazoleta.plazoleta.infrastructure.input.rest.dto.ApiRequests;
import com.plazoleta.plazoleta.infrastructure.input.rest.security.JwtPrincipal;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class PlazoletaController {
    private final PlazoletaUseCase plazoleta;

    public PlazoletaController(PlazoletaUseCase plazoleta) {
        this.plazoleta = plazoleta;
    }

    @PostMapping("/restaurantes")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @SecurityRequirement(name = "bearerAuth")
    @ResponseStatus(HttpStatus.CREATED)
    public RestauranteResponse crearRestaurante(@Valid @RequestBody ApiRequests.CrearRestauranteRequest r) {
        Restaurante restaurante = plazoleta.crearRestaurante(new PlazoletaUseCase.CrearRestaurante(
                r.nombre(), r.nit(), r.direccion(), r.telefono(), r.urlLogo(), r.propietarioId()));
        return RestauranteResponse.desde(restaurante);
    }

    @GetMapping("/restaurantes")
    @PreAuthorize("hasRole('CLIENTE')")
    @SecurityRequirement(name = "bearerAuth")
    public RespuestaPagina<RestauranteResumenResponse> listarRestaurantes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pagina<Restaurante> resultado = plazoleta.listarRestaurantes(page, size);
        List<RestauranteResumenResponse> contenido = resultado.contenido().stream()
                .map(r -> new RestauranteResumenResponse(r.nombre(), r.urlLogo()))
                .toList();
        return new RespuestaPagina<>(contenido, resultado.pagina(), resultado.tamanio(),
                resultado.totalElementos(), resultado.totalPaginas());
    }

    @PostMapping("/restaurantes/{restauranteId}/platos")
    @PreAuthorize("hasRole('PROPIETARIO')")
    @SecurityRequirement(name = "bearerAuth")
    @ResponseStatus(HttpStatus.CREATED)
    public PlatoResponse crearPlato(@PathVariable Long restauranteId,
                                    @AuthenticationPrincipal JwtPrincipal principal,
                                    @Valid @RequestBody ApiRequests.CrearPlatoRequest r) {
        Plato plato = plazoleta.crearPlato(restauranteId, principal.id(),
                new PlazoletaUseCase.CrearPlato(r.nombre(), r.precio(), r.descripcion(),
                        r.urlImagen(), r.categoria()));
        return PlatoResponse.desde(plato);
    }

    @PatchMapping("/restaurantes/{restauranteId}/platos/{platoId}")
    @PreAuthorize("hasRole('PROPIETARIO')")
    @SecurityRequirement(name = "bearerAuth")
    public PlatoResponse modificarPlato(@PathVariable Long restauranteId,
                                        @PathVariable Long platoId,
                                        @AuthenticationPrincipal JwtPrincipal principal,
                                        @Valid @RequestBody ApiRequests.ModificarPlatoRequest r) {
        Plato plato = plazoleta.modificarPlato(restauranteId, platoId, principal.id(),
                new PlazoletaUseCase.ModificarPlato(r.precio(), r.descripcion()));
        return PlatoResponse.desde(plato);
    }

    @PatchMapping("/restaurantes/{restauranteId}/platos/{platoId}/estado")
    @PreAuthorize("hasRole('PROPIETARIO')")
    @SecurityRequirement(name = "bearerAuth")
    public PlatoResponse cambiarEstadoPlato(@PathVariable Long restauranteId,
                                             @PathVariable Long platoId,
                                             @AuthenticationPrincipal JwtPrincipal principal,
                                             @Valid @RequestBody ApiRequests.EstadoPlatoRequest r) {
        Plato plato = plazoleta.cambiarEstadoPlato(restauranteId, platoId, principal.id(), r.activo());
        return PlatoResponse.desde(plato);
    }

    @PostMapping("/restaurantes/{restauranteId}/empleados")
    @PreAuthorize("hasRole('PROPIETARIO')")
    @SecurityRequirement(name = "bearerAuth")
    @ResponseStatus(HttpStatus.CREATED)
    public EmpleadoResponse crearEmpleado(@PathVariable Long restauranteId,
                                          @AuthenticationPrincipal JwtPrincipal principal,
                                          @Valid @RequestBody ApiRequests.CrearEmpleadoRequest r) {
        UsuarioRemoto empleado = plazoleta.crearEmpleado(restauranteId, principal.id(),
                new PlazoletaUseCase.CrearEmpleado(r.nombre(), r.apellido(), r.documentoIdentidad(),
                        r.celular(), r.correo(), r.clave()));
        return new EmpleadoResponse(empleado.id(), empleado.correo(), empleado.rol(), empleado.restauranteId());
    }

    public record RestauranteResponse(Long id, String nombre, String nit, String direccion,
                                     String telefono, String urlLogo, Long propietarioId) {
        static RestauranteResponse desde(Restaurante r) {
            return new RestauranteResponse(r.id(), r.nombre(), r.nit(), r.direccion(),
                    r.telefono(), r.urlLogo(), r.propietarioId());
        }
    }

    public record RestauranteResumenResponse(String nombre, String urlLogo) {}

    public record RespuestaPagina<T>(List<T> content, int page, int size,
                                     long totalElements, int totalPages) {}

    public record PlatoResponse(Long id, String nombre, Integer precio, String descripcion,
                                String urlImagen, String categoria, boolean activo, Long restauranteId) {
        static PlatoResponse desde(Plato p) {
            return new PlatoResponse(p.id(), p.nombre(), p.precio(), p.descripcion(),
                    p.urlImagen(), p.categoria(), p.activo(), p.restauranteId());
        }
    }

    public record EmpleadoResponse(Long id, String correo, String rol, Long restauranteId) {}
}
