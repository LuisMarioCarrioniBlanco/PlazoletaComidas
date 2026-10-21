package com.plazoleta.plazoleta.infrastructure.output.persistence.repository;

import com.plazoleta.plazoleta.infrastructure.output.persistence.entity.PlatoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlatoJpaRepository extends JpaRepository<PlatoEntity, Long> {}
