package com.kevin.ahorcado.services.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kevin.ahorcado.exceptions.PokemonException;
import com.kevin.ahorcado.models.Pokemon;
import com.kevin.ahorcado.repositories.PokemonRepository;
import com.kevin.ahorcado.services.PokemonService;

@Service
public class PokemonServiceImpl implements PokemonService {

    private static final String POKEAPI_URL = "https://pokeapi.co/api/v2/pokemon/?limit=1017"; // URL de la API de Pokémon
    private static final String POKEAPI_POKEMON_URL = "https://pokeapi.co/api/v2/pokemon/";
    private static final String POKEAPI_POKEMON_SPECIES_URL = "https://pokeapi.co/api/v2/pokemon-species/";

    private final Map<String, String> dailyPokemon = new ConcurrentHashMap<>();

    private final RestTemplate restTemplate;
    
    private PokemonRepository pokemonRepository;

    public PokemonServiceImpl(RestTemplate restTemplate, PokemonRepository pokemonRepository) {
        this.restTemplate = restTemplate;
        this.pokemonRepository = pokemonRepository;
    }

    /**
     * Obtiene el Pokémon diario, ya sea de la caché o generando uno nuevo.
     *
     * @return Objeto Pokémon correspondiente al día actual.
     * @throws PokemonException Si hay un error al procesar el Pokémon en caché o al generar uno nuevo.
     * @throws JsonProcessingException Sí hay un error al procesar la representación en forma de String del Pokémon.
     */
    @Override
    public Pokemon getDailyPokemon() throws PokemonException, JsonProcessingException {
        LocalDate today = LocalDate.now();
        String cachedPokemon = dailyPokemon.get(today.toString());

        if (cachedPokemon != null) {
            // Si ya hay un Pokemon en caché para hoy, lo deserializamos y lo devolvemos
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(cachedPokemon, Pokemon.class);
        } else {
            // Si no hay un Pokémon para hoy, generamos uno nuevo
            Pokemon newPokemon = getRandomPokemon();

            // Convertimos el objeto Pokemon a su representación en forma de String y lo guardamos en la caché
            ObjectMapper objectMapper = new ObjectMapper();
            String newPokemonDetails = objectMapper.writeValueAsString(newPokemon);
            dailyPokemon.put(today.toString(), newPokemonDetails);

            return newPokemon;
        }
    }

    /**
     * Obtiene un Pokémon aleatorio de la PokeAPI.
     *
     * @return Objeto Pokémon con información aleatoria.
     * @throws PokemonException Si no se encuentran Pokémon, o hay un error al procesar la respuesta de la PokeAPI.
     * @throws JsonProcessingException Si hay un error al procesar la respuesta JSON.
     */
    @Override
    public Pokemon getRandomPokemon() throws PokemonException, JsonProcessingException {

        Pokemon pokemon = new Pokemon();
        int randomPokemonNumber = getRandomPokemonNumber();
        //randomPokemonNumber=204;
        String pokemonDetailsJson = restTemplate.getForObject(POKEAPI_POKEMON_URL + randomPokemonNumber, String.class);
        String pokemonSpeciesJson = restTemplate.getForObject(POKEAPI_POKEMON_SPECIES_URL + randomPokemonNumber, String.class);

        if(pokemonDetailsJson == null) {
            throw new PokemonException("No se encontraron Pokémon.", HttpStatus.NOT_FOUND);
        }

        if(pokemonSpeciesJson == null) {
            throw new PokemonException("No se encontraron Pokémon.", HttpStatus.NOT_FOUND);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(pokemonDetailsJson);
        JsonNode descriptionRoot = objectMapper.readTree(pokemonSpeciesJson);

        pokemon.setName(getName(descriptionRoot));
        System.err.println(pokemon.getName());
        pokemon.setDescription(cleanPokedexDescription(getDescription(descriptionRoot), pokemon.getName()));
        pokemon.setAbility( fromListToString( getAbilities(rootNode)) );
        pokemon.setType( fromListToString( getTypes(rootNode)) );
        pokemon.setRegion(getRegion(descriptionRoot));
        pokemon.setImageUrl(getImageUrl(rootNode));
        
        pokemonRepository.save(pokemon);

        return pokemon;

    }

    /**
     * Obtiene el nombre de un Pokémon a partir de un nodo JSON proporcionado por la PokeAPI.
     *
     * @param rootNode El nodo JSON que contiene los datos del Pokémon.
     * @return El nombre del Pokémon.
     * @throws PokemonException Si los datos de nombre de Pokémon son nulos o incompletos.
     * @throws HttpStatus.INTERNAL_SERVER_ERROR Si se produce un error interno al procesar los datos.
     */
    private String getName(JsonNode rootNode) throws PokemonException {
        if (rootNode == null || !rootNode.has("names")) {
            throw new PokemonException("Datos de nombre de Pokémon incompletos o nulos.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        JsonNode namesNode = rootNode.get("names");
        
        // Buscar el nombre en español en la lista de nombres
        for (JsonNode nameEntry : namesNode) {
            if (nameEntry.has("language") && nameEntry.has("name")) {
                JsonNode languageNode = nameEntry.get("language");
                
                // Verificar si el idioma es español (es)
                if (languageNode.has("name") && languageNode.get("name").asText().equals("es")) {
                    return nameEntry.get("name").asText();
                }
            }
        }

        // Lanzar una excepción si no se encuentra el nombre en español
        throw new PokemonException("Nombre del Pokémon no encontrado en español.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Obtiene la descripción en inglés de un Pokémon a partir de un nodo JSON de descripción.
     *
     * @param descriptionRoot Nodo JSON que contiene la información de la descripción del Pokémon.
     * @return Descripción en inglés del Pokémon.
     * @throws PokemonException Si los datos de descripción de Pokémon son nulos, incompletos o no son un array,
     *                          o si la descripción en inglés no está presente en las entradas de texto.
     */

    private String getDescription(JsonNode descriptionRoot) throws PokemonException {
        if (descriptionRoot == null || !descriptionRoot.has("flavor_text_entries")) {
            throw new PokemonException("Datos de descripción de Pokémon incompletos o nulos.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        JsonNode flavorTextEntries = descriptionRoot.get("flavor_text_entries");

        // Almacenar descripciones disponibles en español e inglés
        List<String> spanishDescriptions = new ArrayList<>();
        List<String> englishDescriptions = new ArrayList<>();

        if (flavorTextEntries != null && flavorTextEntries.isArray()) {
            for (JsonNode entry : flavorTextEntries) {
                JsonNode languageNode = entry.path("language");
                JsonNode flavorTextNode = entry.path("flavor_text");

                if (languageNode != null && flavorTextNode != null &&
                        languageNode.has("name") && flavorTextNode.isTextual()) {
                    String language = languageNode.get("name").asText();

                    if ("es".equals(language)) {
                        spanishDescriptions.add(flavorTextNode.asText());
                    } else if ("en".equals(language)) {
                        englishDescriptions.add(flavorTextNode.asText());
                    }
                }
            }
        } else {
            throw new PokemonException("Datos de entradas de descripción de Pokémon no son un array.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Elegir aleatoriamente entre descripciones en español e inglés
        Random random = new Random();
        String selectedDescription;

        if (!spanishDescriptions.isEmpty()) {
            selectedDescription = spanishDescriptions.get(random.nextInt(spanishDescriptions.size()));
        } else if (!englishDescriptions.isEmpty()) {
            selectedDescription = englishDescriptions.get(random.nextInt(englishDescriptions.size()));
        } else {
            throw new PokemonException("Descripción no encontrada.", HttpStatus.NOT_FOUND);
        }

        return selectedDescription;
    }




    /**
     * Obtiene la lista de habilidades de un Pokémon a partir de un nodo JSON.
     *
     * @param rootNode Nodo JSON que contiene la información del Pokémon.
     * @return Lista de habilidades del Pokémon.
     * @throws PokemonException Si los datos de habilidades de Pokémon son nulos, incompletos, o no son un array, o si los datos de habilidad son incompletos.
     * @throws JsonProcessingException 
     * @throws JsonMappingException 
     */
    private List<String> getAbilities(JsonNode rootNode) throws PokemonException, JsonMappingException, JsonProcessingException {
        List<String> abilities = new ArrayList<>();

        if (rootNode == null || !rootNode.has("abilities")) {
            throw new PokemonException("Datos de habilidades de Pokémon incompletos o nulos.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        JsonNode abilitiesNode = rootNode.get("abilities");

        if (abilitiesNode != null && abilitiesNode.isArray()) {
            for (JsonNode abilityNode : abilitiesNode) {
                JsonNode abilityNameNode = abilityNode.path("ability");

                if (abilityNameNode != null && abilityNameNode.has("name")) {
                    String abilityName = abilityNameNode.get("name").asText();

                    // Obtener URL de la habilidad
                    String abilityUrl = abilityNameNode.get("url").asText();

                    // Obtener el nombre de la habilidad en español
                    String abilityNameSpanish = obtenerNombreHabilidadEspañol(abilityUrl);

                    // Si no se encuentra en español, usar el nombre en inglés
                    abilities.add(abilityNameSpanish != null ? abilityNameSpanish : abilityName );
                } else {
                    throw new PokemonException("Datos de habilidad de Pokémon incompletos.", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            throw new PokemonException("Datos de habilidades de Pokémon no son un array.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return abilities;
    }
    
    private String obtenerNombreHabilidadEspañol(String abilityUrl) throws PokemonException, JsonMappingException, JsonProcessingException {
        String nombreHabilidadEspanol = null;

        ObjectMapper objectMapper = new ObjectMapper();
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(abilityUrl, String.class);

        JsonNode jsonNode = objectMapper.readTree(response);
        JsonNode namesNode = jsonNode.path("names");

        for (JsonNode nameNode : namesNode) {
            if (nameNode.has("language") && nameNode.path("language").has("name")
                    && nameNode.path("language").path("name").asText().equals("es")) {
                nombreHabilidadEspanol = nameNode.get("name").asText();
                break;
            }
        }

        // Lanzar una excepción si no se encuentra el nombre en español
        if (nombreHabilidadEspanol == null) {
            throw new PokemonException("Nombre de la habilidad no encontrado en español.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return nombreHabilidadEspanol;
    }

    /**
     * Obtiene la lista de tipos de un Pokémon a partir de un nodo JSON.
     *
     * @param rootNode Nodo JSON que contiene la información del Pokémon.
     * @return Lista de tipos del Pokémon.
     * @throws PokemonException Si los datos de tipos de Pokémon son nulos, incompletos, o no son un array, o si los datos de tipo son incompletos.
     */
    private List<String> getTypes(JsonNode rootNode) throws PokemonException {
        List<String> types = new ArrayList<>();

        if (rootNode == null || !rootNode.has("types")) {
            throw new PokemonException("Datos de tipos de Pokémon incompletos o nulos.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        JsonNode typesNode = rootNode.get("types");

        if (typesNode != null && typesNode.isArray()) {
            for (JsonNode typeNode : typesNode) {
                JsonNode typeNameNode = typeNode.path("type");

                if (typeNameNode != null && typeNameNode.has("name")) {
                    types.add( typeNameNode.get("name").asText() );
                } else {
                    throw new PokemonException("Datos de tipo de Pokémon incompletos.", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            throw new PokemonException("Datos de tipos de Pokémon no son un array.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return types;
    }

    /**
     * Obtiene la región principal de un Pokémon a partir de un nodo JSON de especie.
     *
     * @param speciesNode Nodo JSON que contiene la información de la especie del Pokémon.
     * @return Nombre de la región principal del Pokémon.
     * @throws PokemonException Si los datos de especie de Pokémon son nulos, o si hay un error al obtener los detalles de la generación.
     * @throws JsonProcessingException Sí hay un error al procesar la respuesta JSON de la generación del Pokémon.
     */
    private String getRegion(JsonNode speciesNode) throws PokemonException, JsonProcessingException {
        if (speciesNode == null) {
            throw new PokemonException("Datos de especie de Pokémon nulos.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        JsonNode generationNode = speciesNode.path("generation");

        if (generationNode != null && generationNode.has("url")) {
            String generationURL = generationNode.get("url").asText();

            String generationDetailsJson = restTemplate.getForObject(generationURL, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode generationRoot = objectMapper.readTree(generationDetailsJson);
            JsonNode mainRegionNode = generationRoot.path("main_region");

            if (mainRegionNode != null && mainRegionNode.has("name")) {
                return mainRegionNode.get("name").asText();
            }
        }

        return "Desconocido";
    }

    /**
     * Obtiene la URL de la imagen frontal del Pokémon a partir de un nodo JSON.
     *
     * @param rootNode Nodo JSON que contiene la información del Pokémon.
     * @return URL de la imagen frontal del Pokémon.
     * @throws PokemonException Si los datos de sprites de Pokémon son nulos, incompletos o no contienen la clave 'front_default'.
     */
    private String getImageUrl(JsonNode rootNode) throws PokemonException {
        if (rootNode == null || !rootNode.has("sprites") || !rootNode.get("sprites").has("front_default")) {
            throw new PokemonException("Datos de sprites de Pokémon incompletos o nulos.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return rootNode.get("sprites").get("front_default").asText();
    }

    /**
     * Procesa la respuesta de la lista de Pokémon y extrae los nombres.
     *
     * @param pokemonListResponse Respuesta de la lista de Pokémon.
     * @return Arreglo de nombres de Pokémon.
     * @throws PokemonException Si la respuesta es nula o vacía, o si los datos de Pokémon son incompletos en la lista,
     *                          o si la respuesta no contiene la clave 'results'.
     * @throws JsonProcessingException Si hay un error al procesar la respuesta JSON.
     */
    private String[] processPokemonListResponse(String pokemonListResponse) throws PokemonException, JsonProcessingException {
        if (pokemonListResponse == null || pokemonListResponse.isEmpty()) {
            throw new PokemonException("La respuesta de la lista de Pokémon es nula o vacía.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode;

        rootNode = objectMapper.readTree(pokemonListResponse);

        JsonNode resultsNode = rootNode.get("results");

        if (resultsNode != null && resultsNode.isArray()) {
            String[] pokemonNames = new String[resultsNode.size()];

            for (int i = 0; i < resultsNode.size(); i++) {
                JsonNode pokemon = resultsNode.get(i);

                if (pokemon != null && pokemon.has("name")) {
                    pokemonNames[i] = pokemon.get("name").asText();
                } else {
                    throw new PokemonException("Datos de Pokémon incompletos en la lista.", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }

            return pokemonNames;

        } else {
            throw new PokemonException("La respuesta no contiene la clave 'results' esperada.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtiene aleatoriamente el nombre de un Pokémon a partir de una lista de nombres.
     *
     * @param pokemonNames Lista de nombres de Pokémon.
     * @return Nombre aleatorio de un Pokémon.
     * @throws PokemonException Si la lista de nombres es nula o vacía, o si el índice aleatorio está fuera de rango.
     */
    private String getRandomPokemonName(String[] pokemonNames) throws PokemonException {
        if (pokemonNames == null || pokemonNames.length == 0) {
            throw new PokemonException("Lista de nombres de Pokémon vacía o nula.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Random random = new Random();
        int randomIndex = random.nextInt(pokemonNames.length);

        if (randomIndex >= pokemonNames.length) {
            throw new PokemonException("Índice aleatorio fuera de rango.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return pokemonNames[randomIndex];
    }

    /**
     * Limpia la descripción de la Pokédex, eliminando los saltos de línea.
     *
     * @param pokedexDescription Descripción de la Pokédex que puede contener saltos de línea.
     * @return Descripción de la Pokédex sin saltos de línea.
     */
    private String cleanPokedexDescription(String pokedexDescription, String pokemonName) {
        if (pokedexDescription != null && pokemonName != null && !pokemonName.isEmpty()) {
            // Reemplazar el nombre del Pokémon con asteriscos en la descripción
            String cleanedDescription = pokedexDescription.replaceAll("\\s+", " ").trim();
            cleanedDescription = cleanedDescription.replaceAll("(?i)\\b" + Pattern.quote(pokemonName) + "\\b", "*".repeat(pokemonName.length()));
            return cleanedDescription;
        }
        return "";
    }


    /**
     * Genera un número aleatorio que representa un índice de Pokémon.
     *
     * @return Un número aleatorio en el rango [1, 1008] que representa el índice de un Pokémon.
     */
    private int getRandomPokemonNumber() {
        Random random = new Random();
        int randomIndex = random.nextInt(1008) + 1;
        return randomIndex;
    }
    
    private String fromListToString(List<String> lista) {
        if (lista.isEmpty()) {
            return ""; // O podrías devolver null u otra cadena según tus necesidades.
        } else if (lista.size() == 1) {
            return lista.get(0);
        } else {
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < lista.size() - 1; i++) {
                result.append(lista.get(i)).append(", ");
            }
            result.append(lista.get(lista.size() - 1));
            System.err.println(result.toString());
            return result.toString();
        }
    }


}
