package com.boden.lingvolearner.services;

import static com.boden.lingvolearner.services.ContextHolder.getSettingsHolder;

import java.io.File;

public class WordSpeaker {

	public void speakText(String message) {
		System.out.println("=======speaking : " + message);
		String pathToSoundFiles = getSettingsHolder().getPathToSoundFiles();
		String wordFilePath = pathToSoundFiles + message.charAt(0) + "/" + message + ".wav";
		boolean useFilesToSay = getSettingsHolder().isUseFilesToSay();
		if (useFilesToSay) {
			ContextHolder.getMediaFilesPlayer().play(wordFilePath);
		}
		boolean filePlayed = useFilesToSay && (new File(wordFilePath)).exists();
		boolean useTtsToSay = getSettingsHolder().isUseTtsToSay();
		if (useTtsToSay && !filePlayed) {
			ContextHolder.getWordPlayer().playWord(message);
		}
	}

	public void updateLanguageSelection(String language) {
		for (WordPlayer player : ContextHolder.getAllWordPlayer()) {
			player.updateLanguageSelection(language);
		}
	}

	public void destroy() {
		for (WordPlayer player : ContextHolder.getAllWordPlayer()) {
			if (player != null) {
				player.destroy();
			}
		}
	}
}
