package com.plazoleta.plazoleta.application.port.output;

import com.plazoleta.plazoleta.domain.model.Pagina;
import com.plazoleta.plazoleta.domain.model.Restaurante;

import java.util.Optional;

public interface RestauranteRepositoryPort {
    Restaurante guardar(Restaurante restaurante);
    Optional<Restaurante> porId(Long id);
    boolean existeNit(String nit);
    Pagina<Restaurante> listar(int pagina, int tamanio);
}
