package com.kevin.ahorcado.models;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "GAME_DATA")
public class GameData implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long Id;
	
	@Column(name = "COOKIE")
	String cookie;
	
	 @Column(name = "CORRECT_LETTERS")
    private String correctLetters;

    @Column(name = "INCORRECT_LETTERS")
    private String incorrectLetters;

    @Column(name = "CORRECT_POSITIONS")
    private String correctPositions;

    @Column(name = "INCORRECT_POSITIONS")
    private String incorrectPositions;

    @Column(name = "NUM_ERRORS")
    private int numErrors;

    @Column(name = "NUM_HINTS_USED")
    private int numHintsUsed;

    @Column(name = "HINTS_GIVEN")
    private String hintsGiven;

	public GameData() {
		
	}

	public GameData(Long id, String cookie, String correctLetters, String incorrectLetters, String correctPositions, String incorrectPositions, int numErrors, int numHintsUsed, String hintsGiven) {
		Id = id;
		this.cookie = cookie;
		this.correctLetters = correctLetters;
		this.incorrectLetters = incorrectLetters;
		this.correctPositions = correctPositions;
		this.incorrectPositions = incorrectPositions;
		this.numErrors = numErrors;
		this.numHintsUsed = numHintsUsed;
		this.hintsGiven = hintsGiven;
	}

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public String getCookie() {
		return cookie;
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

	public String getCorrectLetters() {
		return correctLetters;
	}

	public void setCorrectLetters(String correctLetters) {
		this.correctLetters = correctLetters;
	}

	public String getIncorrectLetters() {
		return incorrectLetters;
	}

	public void setIncorrectLetters(String incorrectLetters) {
		this.incorrectLetters = incorrectLetters;
	}

	public String getCorrectPositions() {
		return correctPositions;
	}

	public void setCorrectPositions(String correctPositions) {
		this.correctPositions = correctPositions;
	}

	public String getIncorrectPositions() {
		return incorrectPositions;
	}

	public void setIncorrectPositions(String incorrectPositions) {
		this.incorrectPositions = incorrectPositions;
	}

	public int getNumErrors() {
		return numErrors;
	}

	public void setNumErrors(int numErrors) {
		this.numErrors = numErrors;
	}

	public int getNumHintsUsed() {
		return numHintsUsed;
	}

	public void setNumHintsUsed(int numHintsUsed) {
		this.numHintsUsed = numHintsUsed;
	}

	public String getHintsGiven() {
		return hintsGiven;
	}

	public void setHintsGiven(String hintsGiven) {
		this.hintsGiven = hintsGiven;
	}

	@Override
	public String toString() {
		return "GameData [Id=" + Id + ", cookie=" + cookie + ", correctLetters=" + correctLetters
				+ ", incorrectLetters=" + incorrectLetters + ", correctPositions=" + correctPositions
				+ ", incorrectPositions=" + incorrectPositions + ", numErrors=" + numErrors + ", numHintsUsed="
				+ numHintsUsed + ", hintsGiven=" + hintsGiven + "]";
	}
	
}
