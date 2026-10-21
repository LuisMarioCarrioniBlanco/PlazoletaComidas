package com.plazoleta.plazoleta.infrastructure.output.persistence.repository;

import com.plazoleta.plazoleta.infrastructure.output.persistence.entity.RestauranteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestauranteJpaRepository extends JpaRepository<RestauranteEntity, Long> {
    boolean existsByNit(String nit);
}
