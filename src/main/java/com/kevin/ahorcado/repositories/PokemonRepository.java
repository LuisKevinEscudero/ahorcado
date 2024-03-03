package com.kevin.ahorcado.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kevin.ahorcado.models.Pokemon;

@Repository
public interface PokemonRepository extends JpaRepository<Pokemon, Long>{

	Pokemon findByName(String name);
}
