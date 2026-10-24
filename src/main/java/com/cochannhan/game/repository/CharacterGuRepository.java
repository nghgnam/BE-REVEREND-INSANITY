package com.cochannhan.game.repository;

import com.cochannhan.game.model.CharacterGu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CharacterGuRepository extends JpaRepository<CharacterGu, Long> {
    List<CharacterGu> findByCharacterId(Long characterId);
    List<CharacterGu> findByCharacterIdAndIsEquippedTrue(Long characterId);
}
