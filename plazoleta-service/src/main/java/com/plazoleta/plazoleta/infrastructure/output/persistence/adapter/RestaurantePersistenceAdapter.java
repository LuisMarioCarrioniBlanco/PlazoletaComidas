package com.plazoleta.plazoleta.infrastructure.output.persistence.adapter;

import com.plazoleta.plazoleta.application.port.output.RestauranteRepositoryPort;
import com.plazoleta.plazoleta.domain.model.Pagina;
import com.plazoleta.plazoleta.domain.model.Restaurante;
import com.plazoleta.plazoleta.infrastructure.output.persistence.entity.RestauranteEntity;
import com.plazoleta.plazoleta.infrastructure.output.persistence.repository.RestauranteJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RestaurantePersistenceAdapter implements RestauranteRepositoryPort {
    private final RestauranteJpaRepository repository;

    public RestaurantePersistenceAdapter(RestauranteJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Restaurante guardar(Restaurante restaurante) {
        RestauranteEntity e = new RestauranteEntity();
        e.setId(restaurante.id());
        e.setNombre(restaurante.nombre());
        e.setNit(restaurante.nit());
        e.setDireccion(restaurante.direccion());
        e.setTelefono(restaurante.telefono());
        e.setUrlLogo(restaurante.urlLogo());
        e.setPropietarioId(restaurante.propietarioId());
        return convertir(repository.save(e));
    }

    @Override
    public Optional<Restaurante> porId(Long id) {
        return repository.findById(id).map(this::convertir);
    }

    @Override
    public boolean existeNit(String nit) {
        return repository.existsByNit(nit);
    }

    @Override
    public Pagina<Restaurante> listar(int pagina, int tamanio) {
        Page<RestauranteEntity> result = repository.findAll(PageRequest.of(
                pagina, tamanio, Sort.by(Sort.Direction.ASC, "nombre")));
        return new Pagina<>(result.getContent().stream().map(this::convertir).toList(),
                result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
    }

    private Restaurante convertir(RestauranteEntity e) {
        return new Restaurante(e.getId(), e.getNombre(), e.getNit(), e.getDireccion(),
                e.getTelefono(), e.getUrlLogo(), e.getPropietarioId());
    }
}
