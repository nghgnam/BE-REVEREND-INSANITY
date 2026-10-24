package com.cochannhan.game.repository;

import com.cochannhan.game.model.GuDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GuDefinitionRepository extends JpaRepository<GuDefinition, Long> {
    Optional<GuDefinition> findByCode(String code);
}
