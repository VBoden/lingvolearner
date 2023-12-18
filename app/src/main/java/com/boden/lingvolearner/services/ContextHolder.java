package com.boden.lingvolearner.services;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.boden.lingvolearner.pojo.WordCard;

public class ContextHolder {

	private static ContextHolder instance;

	private static final Map<Stage, UiUpdator> STAGES_UI_UPDATORS = new HashMap<>();
	private LearningManager learningManager;
	private SettingsHolder settingsHolder;
	private WordSpeaker wordSpeaker;
	private WordPlayer player;
	private MediaFilesPlayer mediaFilesPlayer;
	private String[] categories;
	private String[] dictionaries;

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

	public SettingsHolder createSettingsHolder(UserPreferences userPreferences) {
		this.settingsHolder = new SettingsHolder(userPreferences);
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

	public static void setWordPlayer(WordPlayer player) {
		getInstance().player = player;
	}

	public static WordPlayer getWordPlayer() {
		return getInstance().player;
	}

	public static Collection<WordPlayer> getAllWordPlayer() {
		return Collections.singletonList(getInstance().player);
	}

	public static WordSpeaker getWordSpeaker() {
		return getInstance().wordSpeaker;
	}

	public static void setMediaFilesPlayer(MediaFilesPlayer mediaFilesPlayer) {
		getInstance().mediaFilesPlayer = mediaFilesPlayer;
	}

	public static MediaFilesPlayer getMediaFilesPlayer() {
		return getInstance().mediaFilesPlayer;
	}

	public static List<WordCard> getAllWordCards() {
		return getLearningManager().getAllWordCards();
	}

	public String[] getCategories() {
		return categories;
	}

	public void setCategories(String[] categories) {
		this.categories = categories;
	}

	public String[] getDictionaries() {
		return dictionaries;
	}

	public void setDictionaries(String[] dictionaries) {
		this.dictionaries = dictionaries;
	}

}
