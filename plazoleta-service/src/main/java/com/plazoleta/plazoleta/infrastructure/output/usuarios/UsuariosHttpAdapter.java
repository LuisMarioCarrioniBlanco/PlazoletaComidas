package com.plazoleta.plazoleta.infrastructure.output.usuarios;

import com.plazoleta.plazoleta.application.port.output.UsuariosPort;
import com.plazoleta.plazoleta.domain.exception.ConflictoException;
import com.plazoleta.plazoleta.domain.exception.RecursoNoEncontradoException;
import com.plazoleta.plazoleta.domain.exception.ReglaNegocioException;
import com.plazoleta.plazoleta.domain.model.UsuarioRemoto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class UsuariosHttpAdapter implements UsuariosPort {
    private final RestClient client;

    public UsuariosHttpAdapter(RestClient.Builder builder,
                               @Value("${servicios.usuarios.url}") String baseUrl,
                               @Value("${plazoleta.internal-token}") String token) {
        this.client = builder.baseUrl(baseUrl)
                .defaultHeader("X-Internal-Token", token)
                .build();
    }

    @Override
    public UsuarioRemoto buscarPorId(Long id) {
        try {
            UsuarioInternoResponse r = client.get()
                    .uri("/internal/usuarios/{id}", id)
                    .retrieve()
                    .body(UsuarioInternoResponse.class);
            if (r == null) {
                throw new ReglaNegocioException("El servicio de usuarios no devolvió información");
            }
            return new UsuarioRemoto(r.id(), r.correo(), r.rol(), r.restauranteId());
        } catch (HttpClientErrorException.NotFound e) {
            throw new RecursoNoEncontradoException("No existe el usuario propietario indicado");
        } catch (RestClientException e) {
            throw new ReglaNegocioException("No fue posible validar el usuario en el servicio de usuarios");
        }
    }

    @Override
    public UsuarioRemoto crearEmpleado(UsuariosPort.CrearEmpleadoData empleado, Long restauranteId) {
        CrearEmpleadoInterno request = new CrearEmpleadoInterno(
                empleado.nombre(), empleado.apellido(), empleado.documentoIdentidad(),
                empleado.celular(), empleado.correo(), empleado.clave(), restauranteId);
        try {
            UsuarioInternoResponse r = client.post()
                    .uri("/internal/usuarios/empleados")
                    .body(request)
                    .retrieve()
                    .body(UsuarioInternoResponse.class);
            if (r == null) {
                throw new ReglaNegocioException("El servicio de usuarios no devolvió información");
            }
            return new UsuarioRemoto(r.id(), r.correo(), r.rol(), r.restauranteId());
        } catch (HttpClientErrorException.Conflict e) {
            throw new ConflictoException("El correo o documento del empleado ya están registrados");
        } catch (RestClientException e) {
            if (e instanceof HttpClientErrorException.NotFound) {
                throw new RecursoNoEncontradoException("No se encontró el recurso de usuarios");
            }
            throw new ReglaNegocioException("No fue posible crear la cuenta del empleado");
        }
    }

    private record UsuarioInternoResponse(Long id, String correo, String rol, Long restauranteId) {}
    private record CrearEmpleadoInterno(String nombre, String apellido, String documentoIdentidad,
                                        String celular, String correo, String clave, Long restauranteId) {}
}
