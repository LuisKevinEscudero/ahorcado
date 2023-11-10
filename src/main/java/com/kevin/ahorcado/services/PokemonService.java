package com.kevin.ahorcado.services;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.kevin.ahorcado.exceptions.PokemonException;
import com.kevin.ahorcado.models.Pokemon;

public interface PokemonService {
    Pokemon getRandomPokemon() throws PokemonException, JsonProcessingException;
    Pokemon getDailyPokemon() throws PokemonException, JsonProcessingException;

}
