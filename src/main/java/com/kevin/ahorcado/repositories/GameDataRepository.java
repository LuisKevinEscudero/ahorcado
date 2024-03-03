package com.kevin.ahorcado.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kevin.ahorcado.models.GameData;

@Repository
public interface GameDataRepository extends JpaRepository<GameData, Long> {

}
