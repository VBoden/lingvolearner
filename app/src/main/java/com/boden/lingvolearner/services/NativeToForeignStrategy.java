package com.boden.lingvolearner.services;

import com.boden.lingvolearner.pojo.WordCard;

public class NativeToForeignStrategy extends AbstractStrategy {

	@Override
	public Stage getPreviousStage() {
		return Stage.FOREIGN_TO_NATIVE;
	}

	@Override
	public Stage getNextStage() {
		return Stage.WRITING_WORDS;
	}

	@Override
	public String getWordToCheck(WordCard wordCard) {
		return wordCard.getWord();
	}

	@Override
	public String getWordToDisplay(WordCard wordCard) {
		return wordCard.getTranslation();
	}

	@Override
	public String getWordTranscription(WordCard wordCard) {
		return "";
	}

}
