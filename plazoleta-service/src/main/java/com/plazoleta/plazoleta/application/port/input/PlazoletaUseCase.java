package com.plazoleta.plazoleta.application.port.input;

import com.plazoleta.plazoleta.domain.model.Pagina;
import com.plazoleta.plazoleta.domain.model.Plato;
import com.plazoleta.plazoleta.domain.model.Restaurante;
import com.plazoleta.plazoleta.domain.model.UsuarioRemoto;

public interface PlazoletaUseCase {
    Restaurante crearRestaurante(CrearRestaurante command);
    Plato crearPlato(Long restauranteId, Long usuarioId, CrearPlato command);
    Plato modificarPlato(Long restauranteId, Long platoId, Long usuarioId, ModificarPlato command);
    Plato cambiarEstadoPlato(Long restauranteId, Long platoId, Long usuarioId, boolean activo);
    Pagina<Restaurante> listarRestaurantes(int pagina, int tamanio);
    UsuarioRemoto crearEmpleado(Long restauranteId, Long propietarioId, CrearEmpleado command);

    record CrearRestaurante(String nombre, String nit, String direccion, String telefono,
                            String urlLogo, Long propietarioId) {}
    record CrearPlato(String nombre, Integer precio, String descripcion,
                      String urlImagen, String categoria) {}
    record ModificarPlato(Integer precio, String descripcion) {}
    record CrearEmpleado(String nombre, String apellido, String documentoIdentidad,
                         String celular, String correo, String clave) {}
}
