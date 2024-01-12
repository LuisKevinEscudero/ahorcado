package com.kevin.ahorcado.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kevin.ahorcado.exceptions.PokemonException;
import com.kevin.ahorcado.models.Pokemon;
import com.kevin.ahorcado.services.PokemonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping("/pokemon")
@CrossOrigin(origins = {"http://localhost:3000", "https://luiskevinescudero.github.io" })
public class pokemonController {

    private final PokemonService pokemonService;

    public pokemonController(PokemonService pokemonService) {
        this.pokemonService = pokemonService;
    }

    @GetMapping("/random")
    public ResponseEntity<Pokemon> getRandomPokemon() throws PokemonException, JsonProcessingException {
        return ResponseEntity.ok(pokemonService.getRandomPokemon());
    }

    @GetMapping("/startGame")
    public ResponseEntity<Pokemon> startGameForUser() throws PokemonException, JsonProcessingException {
        Pokemon pokemon = pokemonService.getDailyPokemon();

        // Aquí podrías devolver el Pokémon o realizar otras operaciones del juego
        return ResponseEntity.ok(pokemon);
    }

}
