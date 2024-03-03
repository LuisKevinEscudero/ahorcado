package com.kevin.ahorcado.services.impl;

import org.springframework.stereotype.Service;

import com.kevin.ahorcado.models.GameData;
import com.kevin.ahorcado.repositories.GameDataRepository;
import com.kevin.ahorcado.services.GameDataService;

@Service
public class GameDataServiceImpl implements GameDataService {

	private GameDataRepository gameDataRepository;
	
	public GameDataServiceImpl(GameDataRepository gameDataRepository) {
		this.gameDataRepository = gameDataRepository;
	}

	@Override
	public GameData processData(GameData data) {
		
		return gameDataRepository.save(data);
	}

}
