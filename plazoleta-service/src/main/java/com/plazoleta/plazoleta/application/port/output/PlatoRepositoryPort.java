package com.plazoleta.plazoleta.application.port.output;

import com.plazoleta.plazoleta.domain.model.Plato;

import java.util.Optional;

public interface PlatoRepositoryPort {
    Plato guardar(Plato plato);
    Optional<Plato> porId(Long id);
}
