package com.plazoleta.plazoleta.application.service;

import com.plazoleta.plazoleta.application.port.input.PlazoletaUseCase;
import com.plazoleta.plazoleta.application.port.output.PlatoRepositoryPort;
import com.plazoleta.plazoleta.application.port.output.RestauranteRepositoryPort;
import com.plazoleta.plazoleta.application.port.output.UsuariosPort;
import com.plazoleta.plazoleta.domain.exception.ConflictoException;
import com.plazoleta.plazoleta.domain.exception.RecursoNoEncontradoException;
import com.plazoleta.plazoleta.domain.exception.ReglaNegocioException;
import com.plazoleta.plazoleta.domain.model.Pagina;
import com.plazoleta.plazoleta.domain.model.Plato;
import com.plazoleta.plazoleta.domain.model.Restaurante;
import com.plazoleta.plazoleta.domain.model.UsuarioRemoto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlazoletaService implements PlazoletaUseCase {
    private final RestauranteRepositoryPort restaurantes;
    private final PlatoRepositoryPort platos;
    private final UsuariosPort usuarios;

    public PlazoletaService(RestauranteRepositoryPort restaurantes,
                            PlatoRepositoryPort platos,
                            UsuariosPort usuarios) {
        this.restaurantes = restaurantes;
        this.platos = platos;
        this.usuarios = usuarios;
    }

    @Override
    @Transactional
    public Restaurante crearRestaurante(CrearRestaurante c) {
        validarNoVacio(c.nombre(), "El nombre del restaurante es obligatorio");
        validarNoVacio(c.nit(), "El NIT es obligatorio");
        validarNoVacio(c.direccion(), "La dirección es obligatoria");
        validarNoVacio(c.telefono(), "El teléfono es obligatorio");
        validarNoVacio(c.urlLogo(), "La URL del logo es obligatoria");
        if (c.nombre().trim().matches("\\d+")) {
            throw new ReglaNegocioException("El nombre del restaurante no puede contener solo números");
        }
        if (!c.nit().matches("\\d+")) {
            throw new ReglaNegocioException("El NIT debe ser numérico");
        }
        if (!c.telefono().matches("^(?:\\+\\d{1,12}|\\d{1,13})$")) {
            throw new ReglaNegocioException("El teléfono debe tener máximo 13 caracteres y solo admite un + inicial");
        }
        if (c.propietarioId() == null || c.propietarioId() <= 0) {
            throw new ReglaNegocioException("Debe indicar el propietario del restaurante");
        }
        if (restaurantes.existeNit(c.nit())) {
            throw new ConflictoException("Ya existe un restaurante con ese NIT");
        }
        UsuarioRemoto propietario = usuarios.buscarPorId(c.propietarioId());
        if (!"PROPIETARIO".equals(propietario.rol())) {
            throw new ReglaNegocioException("El usuario indicado no tiene el rol PROPIETARIO");
        }
        return restaurantes.guardar(new Restaurante(null, c.nombre().trim(), c.nit(),
                c.direccion().trim(), c.telefono(), c.urlLogo().trim(), c.propietarioId()));
    }

    @Override
    @Transactional
    public Plato crearPlato(Long restauranteId, Long usuarioId, CrearPlato c) {
        Restaurante restaurante = obtenerRestaurante(restauranteId);
        validarPropietario(restaurante, usuarioId);
        validarNoVacio(c.nombre(), "El nombre del plato es obligatorio");
        validarNoVacio(c.descripcion(), "La descripción es obligatoria");
        validarNoVacio(c.urlImagen(), "La URL de imagen es obligatoria");
        validarNoVacio(c.categoria(), "La categoría es obligatoria");
        if (c.precio() == null || c.precio() <= 0) {
            throw new ReglaNegocioException("El precio debe ser un entero positivo mayor que cero");
        }
        return platos.guardar(new Plato(null, c.nombre().trim(), c.precio(),
                c.descripcion().trim(), c.urlImagen().trim(), c.categoria().trim(), true, restauranteId));
    }

    @Override
    @Transactional
    public Plato modificarPlato(Long restauranteId, Long platoId, Long usuarioId, ModificarPlato c) {
        Restaurante restaurante = obtenerRestaurante(restauranteId);
        validarPropietario(restaurante, usuarioId);
        Plato actual = obtenerPlato(platoId);
        if (!actual.restauranteId().equals(restauranteId)) {
            throw new RecursoNoEncontradoException("El plato no pertenece a ese restaurante");
        }
        if (c.precio() == null && c.descripcion() == null) {
            throw new ReglaNegocioException("Solo se puede modificar el precio o la descripción; indique al menos uno");
        }
        if (c.precio() != null && c.precio() <= 0) {
            throw new ReglaNegocioException("El precio debe ser un entero positivo mayor que cero");
        }
        if (c.descripcion() != null && c.descripcion().isBlank()) {
            throw new ReglaNegocioException("La descripción no puede estar vacía");
        }
        Plato actualizado = new Plato(actual.id(), actual.nombre(),
                c.precio() != null ? c.precio() : actual.precio(),
                c.descripcion() != null ? c.descripcion().trim() : actual.descripcion(),
                actual.urlImagen(), actual.categoria(), actual.activo(), actual.restauranteId());
        return platos.guardar(actualizado);
    }

    @Override
    @Transactional
    public Plato cambiarEstadoPlato(Long restauranteId, Long platoId, Long usuarioId, boolean activo) {
        Restaurante restaurante = obtenerRestaurante(restauranteId);
        validarPropietario(restaurante, usuarioId);
        Plato actual = obtenerPlato(platoId);
        if (!actual.restauranteId().equals(restauranteId)) {
            throw new RecursoNoEncontradoException("El plato no pertenece a ese restaurante");
        }
        Plato actualizado = new Plato(actual.id(), actual.nombre(), actual.precio(), actual.descripcion(),
                actual.urlImagen(), actual.categoria(), activo, actual.restauranteId());
        return platos.guardar(actualizado);
    }

    @Override
    public Pagina<Restaurante> listarRestaurantes(int pagina, int tamanio) {
        if (pagina < 0) {
            throw new ReglaNegocioException("La página no puede ser negativa");
        }
        if (tamanio < 1 || tamanio > 100) {
            throw new ReglaNegocioException("El tamaño de página debe estar entre 1 y 100");
        }
        return restaurantes.listar(pagina, tamanio);
    }

    @Override
    @Transactional
    public UsuarioRemoto crearEmpleado(Long restauranteId, Long propietarioId, CrearEmpleado command) {
        Restaurante restaurante = obtenerRestaurante(restauranteId);
        validarPropietario(restaurante, propietarioId);
        validarNoVacio(command.nombre(), "El nombre es obligatorio");
        validarNoVacio(command.apellido(), "El apellido es obligatorio");
        validarNoVacio(command.documentoIdentidad(), "El documento es obligatorio");
        validarNoVacio(command.celular(), "El celular es obligatorio");
        validarNoVacio(command.correo(), "El correo es obligatorio");
        validarNoVacio(command.clave(), "La clave es obligatoria");
        return usuarios.crearEmpleado(new UsuariosPort.CrearEmpleadoData(
                command.nombre(), command.apellido(), command.documentoIdentidad(),
                command.celular(), command.correo(), command.clave()), restauranteId);
    }

    private Restaurante obtenerRestaurante(Long id) {
        return restaurantes.porId(id).orElseThrow(() ->
                new RecursoNoEncontradoException("No existe el restaurante con id " + id));
    }

    private Plato obtenerPlato(Long id) {
        return platos.porId(id).orElseThrow(() ->
                new RecursoNoEncontradoException("No existe el plato con id " + id));
    }

    private void validarPropietario(Restaurante restaurante, Long usuarioId) {
        if (usuarioId == null || !restaurante.propietarioId().equals(usuarioId)) {
            throw new ReglaNegocioException("Solo el propietario de este restaurante puede realizar esta acción");
        }
    }

    private void validarNoVacio(String valor, String mensaje) {
        if (valor == null || valor.isBlank()) {
            throw new ReglaNegocioException(mensaje);
        }
    }
}
