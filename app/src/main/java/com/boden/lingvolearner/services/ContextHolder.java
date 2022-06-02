package com.boden.lingvolearner.services;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.boden.lingvolearner.WordPlayer;
import com.boden.lingvolearner.WordSpeaker;
import com.boden.lingvolearner.pojo.WordCard;

import android.content.SharedPreferences;

public class ContextHolder {

	private static ContextHolder instance;

	private static final Map<Stage, UiUpdator> STAGES_UI_UPDATORS = new HashMap<>();
	private static final Map<Stage, WordPlayer> STAGES_PLAYER = new HashMap<>();
	private LearningManager learningManager;
	private SettingsHolder settingsHolder;
	private WordSpeaker wordSpeaker;

	private ContextHolder() {
		wordSpeaker = new WordSpeaker();
	}

	public static ContextHolder getInstance() {
		if (Objects.isNull(instance)) {
			instance = new ContextHolder();
		}
		return instance;
	}

	public LearningManager createLearningManager(List<WordCard> allWordCards) {
		this.learningManager = new LearningManager(allWordCards);
		return learningManager;
	}

	public SettingsHolder createSettingsHolder(SharedPreferences sharedPreferences) {
		this.settingsHolder = new SettingsHolder(sharedPreferences);
		return settingsHolder;
	}

	public static LearningManager getLearningManager() {
		return getInstance().learningManager;
	}

	public static SettingsHolder getSettingsHolder() {
		return getInstance().settingsHolder;
	}

	public static void registerUiUpdator(Stage stage, UiUpdator updator) {
		STAGES_UI_UPDATORS.put(stage, updator);
	}
	
	public static UiUpdator getUiUpdator(Stage stage) {
		return STAGES_UI_UPDATORS.get(stage);
	}
	public static void registerWordPlayer(Stage stage, WordPlayer player) {
		STAGES_PLAYER.put(stage, player);
	}
	
	public static WordPlayer getWordPlayer() {
		return STAGES_PLAYER.get(getLearningManager().getCurrentStage());
	}
		
	public static Collection<WordPlayer> getAllWordPlayer() {
		return STAGES_PLAYER.values();
	}

	public static WordSpeaker getWordSpeaker() {
		return getInstance().wordSpeaker;
	}
	
}
