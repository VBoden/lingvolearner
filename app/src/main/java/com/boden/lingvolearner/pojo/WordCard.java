package com.boden.lingvolearner.pojo;

public class WordCard {
	private String word;
	private String transcription;
	private String translation;

	public WordCard() {
		super();
	}

	public WordCard(String word, String transcription, String translation) {
		super();
		this.word = word;
		this.transcription = transcription;
		this.translation = translation;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getTranscription() {
		return transcription;
	}

	public void setTranscription(String transcription) {
		this.transcription = transcription;
	}

	public String getTranslation() {
		return translation;
	}

	public void setTranslation(String translation) {
		this.translation = translation;
	}

}
