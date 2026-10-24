package com.cochannhan.game.repository;

import com.cochannhan.game.model.GuRecipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuRecipeRepository extends JpaRepository<GuRecipe, Long> {
    List<GuRecipe> findByIsUnlockedTrue();
    Optional<GuRecipe> findByPrimaryGuCodeAndSecondaryGuCode(String primary, String secondary);
}
