package com.kevin.ahorcado.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kevin.ahorcado.models.GameData;
import com.kevin.ahorcado.services.GameDataService;
/*
{
  "id": 1,
  "cookie": "abcdef123456",
  "correctLetters": "a, e, h",
  "incorrectLetters": "t, q, s",
  "correctPositions": "1, 2, 4",
  "incorrectPositions": "5, 6, 7",
  "numErrors": 3,
  "numHintsUsed": 2,
  "hintsGiven": "type, region"
}
*/
@RestController
@RequestMapping("/gameData")
@CrossOrigin({"http://localhost:3000", "https://luiskevinescudero.github.io"})
public class GameDataController {

	private GameDataService gameDataService;
	
	public GameDataController(GameDataService gameDataService) {
		this.gameDataService = gameDataService;
	}

	@PostMapping("/saveData")
	public ResponseEntity<GameData> saveData(@RequestBody GameData data){
		
		 if (data == null || data.getCookie() == null || data.getCorrectLetters() == null || data.getIncorrectLetters() == null || data.getCorrectPositions() == null || data.getIncorrectPositions() == null || data.getNumErrors() < 0 || data.getNumHintsUsed() < 0 || data.getHintsGiven() == null) {
		            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		 }
		GameData response = gameDataService.processData(data);
		return ResponseEntity.ok(response);
		
	}
	
}
