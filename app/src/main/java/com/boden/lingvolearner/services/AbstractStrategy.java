package com.boden.lingvolearner.services;

import com.boden.lingvolearner.pojo.WordCard;

public abstract class AbstractStrategy {

	public abstract Stage getPreviousStage();

	public abstract Stage getNextStage();

	public abstract String getWordToDisplay(WordCard wordCard);

	public abstract String getWordTranscription(WordCard wordCard);

	public abstract String getWordToCheck(WordCard wordCard);
	
	public boolean needPlayOnClick() {
		return false;
	}
}
