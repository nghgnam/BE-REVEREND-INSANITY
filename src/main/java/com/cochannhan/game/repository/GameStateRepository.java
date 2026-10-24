package com.cochannhan.game.repository;

import com.cochannhan.game.model.GameStateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameStateRepository extends JpaRepository<GameStateEntity, Long> {
    Optional<GameStateEntity> findByCharacterId(Long characterId);
}
