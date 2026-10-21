package com.plazoleta.plazoleta.infrastructure.output.persistence.adapter;

import com.plazoleta.plazoleta.application.port.output.PlatoRepositoryPort;
import com.plazoleta.plazoleta.domain.model.Plato;
import com.plazoleta.plazoleta.infrastructure.output.persistence.entity.PlatoEntity;
import com.plazoleta.plazoleta.infrastructure.output.persistence.repository.PlatoJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PlatoPersistenceAdapter implements PlatoRepositoryPort {
    private final PlatoJpaRepository repository;

    public PlatoPersistenceAdapter(PlatoJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Plato guardar(Plato plato) {
        PlatoEntity e = new PlatoEntity();
        e.setId(plato.id());
        e.setNombre(plato.nombre());
        e.setPrecio(plato.precio());
        e.setDescripcion(plato.descripcion());
        e.setUrlImagen(plato.urlImagen());
        e.setCategoria(plato.categoria());
        e.setActivo(plato.activo());
        e.setRestauranteId(plato.restauranteId());
        return convertir(repository.save(e));
    }

    @Override
    public Optional<Plato> porId(Long id) {
        return repository.findById(id).map(this::convertir);
    }

    private Plato convertir(PlatoEntity e) {
        return new Plato(e.getId(), e.getNombre(), e.getPrecio(), e.getDescripcion(),
                e.getUrlImagen(), e.getCategoria(), e.isActivo(), e.getRestauranteId());
    }
}
