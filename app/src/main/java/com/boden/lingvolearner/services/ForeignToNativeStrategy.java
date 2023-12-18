package com.boden.lingvolearner.services;

import com.boden.lingvolearner.pojo.WordCard;

public class ForeignToNativeStrategy extends AbstractStrategy {

	@Override
	public Stage getPreviousStage() {
		return Stage.WRITING_WORDS;
	}

	@Override
	public Stage getNextStage() {
		return Stage.NATIVE_TO_FOREIGN;
	}

	@Override
	public String getWordToCheck(WordCard wordCard) {
		return wordCard.getTranslation();
	}

	@Override
	public String getWordToDisplay(WordCard wordCard) {
		return wordCard.getWord();
	}

	@Override
	public String getWordTranscription(WordCard wordCard) {
		return wordCard.getTranscription();
	}

	@Override
	public boolean needPlayOnClick() {
		return true;
	}
}
