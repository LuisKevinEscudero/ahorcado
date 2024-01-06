# Pokemon API Service

Este proyecto Java, implementado como un servicio Spring, utiliza la PokeAPI para obtener información sobre Pokémon. Proporciona métodos para obtener detalles sobre Pokémon aleatorios y un Pokémon diario que se guarda en caché.

## Requisitos

- Java 8 o superior
- Maven para construir y gestionar dependencias
- Spring Boot
- Jackson para el procesamiento de JSON
- RestTemplate para realizar solicitudes HTTP a la PokeAPI

## Configuración

Antes de ejecutar la aplicación, asegúrate de configurar correctamente la URL de la PokeAPI en la clase `PokemonServiceImpl`. Puedes encontrar la URL en la constante `POKEAPI_URL`.

# Uso

El servicio ofrece dos principales métodos:

- **`getDailyPokemon`**: Devuelve un Pokémon diario, ya sea recuperándolo de la caché o generando uno nuevo.
- **`getRandomPokemon`**: Obtiene un Pokémon aleatorio de la PokeAPI.

Además, se proporcionan métodos auxiliares para obtener información específica de un Pokémon, como su nombre, descripción, habilidades, tipos, región y URL de imagen frontal.

# Excepciones

El proyecto maneja excepciones personalizadas (`PokemonException`) para informar sobre posibles errores durante la obtención de datos de la PokeAPI.

# Contribuciones

¡Las contribuciones son bienvenidas! Si encuentras algún problema o tienes sugerencias de mejora, no dudes en abrir un [issue](https://github.com/tu_usuario/tu_proyecto/issues) o enviar un [pull request](https://github.com/tu_usuario/tu_proyecto/pulls).

# Licencia

Este proyecto está bajo la licencia MIT.


