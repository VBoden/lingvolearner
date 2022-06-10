package com.boden.lingvolearner.services;

import java.util.ArrayList;
import java.util.Locale;

import com.boden.lingvolearner.pojo.Dict;

import android.net.Uri;

public class SettingsHolder {
	public static final String REPEAT_COUNT = "repeatCount";
	public static final String DICTIONARIES = "dictionaries";
	public static final String TEXT_SIZE = "textSize";
	public static final String TEXT_PADDING = "textPadding";
	public static final String USE_TTS_TO_SAY = "useTtsToSay";
	public static final String USED_TTS = "usedTTS";
	public static final String USE_FILES_TO_SAY = "useFilesToSay";
	public static final String PATH_TO_SOUND_FILES = "pathToSoundFiles";
	public static final String LANGUAGE = "ttsLanguage";

	private UserPreferences userPrefs;
	private int textPadding;
	private float textSize;
	private int startFromNumber;
	private int repeatCount;
	private int usedTts;
	private boolean useFilesToSay;
	private boolean useTtsToSay;
	private String pathToSoundFiles;

	private String language;
	private Dict dict;
	private ArrayList<Dict> listOfDicts = new ArrayList<>();

	public SettingsHolder(UserPreferences userPrefs) {
		this.userPrefs = userPrefs;
		init();
	}

	private void init() {
		textSize = userPrefs.getFloat(TEXT_SIZE, 0);
		textPadding = userPrefs.getInt(TEXT_PADDING, 0);
		repeatCount = userPrefs.getInt(REPEAT_COUNT, 5);
		language = userPrefs.getString(LANGUAGE, Locale.US.getLanguage());
		useTtsToSay = userPrefs.getBoolean(USE_TTS_TO_SAY, true);
		usedTts = userPrefs.getInt(USED_TTS, 1);
		useFilesToSay = userPrefs.getBoolean(USE_FILES_TO_SAY, true);
		pathToSoundFiles = userPrefs.getString(PATH_TO_SOUND_FILES, "");

		getDictsFromSettings();
		if (userPrefs.contains(DICTIONARIES) == true) {
			String s = userPrefs.getString(DICTIONARIES, "");
			if (s.length() > 0) {
				// Log.i("DEBUG_INFO_MY", "length>0");
				dict = new Dict(s.substring(s.indexOf("<dict>") + 6, s.indexOf("</dict>")));
				// Log.i("DEBUG_INFO_MY", "was created Dict");

				startFromNumber = dict.getBeginFrom();
				// Log.i("DEBUG_INFO_MY", "now started loadDict");

			}
		}
	}

	private void getDictsFromSettings() {
		// Log.i("DEBUG_LastOpend", "getted settings");
		if (userPrefs.contains(DICTIONARIES) == true) {
			String s = userPrefs.getString(DICTIONARIES, "");
			StringBuffer sb = new StringBuffer(s);
			while (sb.length() > 0) {
				Dict dict = new Dict(sb.substring(sb.indexOf("<dict>") + 6, sb.indexOf("</dict>")));
				if (!listOfDicts.contains(dict)) {
					listOfDicts.add(dict);
				}
				sb.delete(0, sb.indexOf("</dict>") + 7);
			}
		}
	}

	public void updateLastDictionary(String uriPath, String uriString) {
		System.out.println("uri=" + uriString);
		String[] segments = uriPath.split("/");
		for (String segment : segments) {
			System.out.println("segment=" + segment);
		}
		System.out.println("==========");
		Dict newDict = new Dict(uriString, segments[segments.length - 1]);
		if (listOfDicts.contains(newDict)) {
			int index = listOfDicts.indexOf(newDict);
			startFromNumber = listOfDicts.get(index).getBeginFrom();
			newDict.setBeginFrom(startFromNumber);
			listOfDicts.remove(index);
		}
		listOfDicts.add(0, newDict);
		saveChangedDictsList();
	}

	public void updateDictionatyStartNumber(int startFromNumber) {
		listOfDicts.get(0).setBeginFrom(startFromNumber);
		saveChangedDictsList();
	}

	public void saveChangedDictsList() {
		StringBuffer dictionaries = new StringBuffer();
		for (int i = 0; i < listOfDicts.size(); i++) {
			dictionaries.append(listOfDicts.get(i).toString());
		}

		userPrefs.saveString(DICTIONARIES, dictionaries.toString());

	}

	public void updateStartNumber(int startFromNumber) {
		int totalWords = ContextHolder.getLearningManager().getTotalWordsCount();
		if (startFromNumber + 10 > totalWords) {
			startFromNumber = totalWords - 10;
		}
		this.startFromNumber = startFromNumber;
		updateDictionatyStartNumber(startFromNumber);
	}

	public void decreaseTextSize() {
		textSize -= 2;
		saveTextSize();
	}

	public void increaseTextSize() {
		textSize += 2;
		saveTextSize();
	}

	private void saveTextSize() {
		userPrefs.saveFloat(TEXT_SIZE, textSize);
	}

	public void decreaseTextPadding() {
		textPadding -= 1;
		saveTextPadding();
	}

	public void increaseTextPadding() {
		textPadding += 1;
		saveTextPadding();
	}

	private void saveTextPadding() {
		userPrefs.saveInt(TEXT_PADDING, textPadding);
	}

	public boolean isUseFilesToSay() {
		return useFilesToSay;
	}

	public void setUseFilesToSay(boolean useFilesToSay) {
		this.useFilesToSay = useFilesToSay;
		userPrefs.saveBoolean(USE_FILES_TO_SAY, useFilesToSay);

	}

	public boolean isUseTtsToSay() {
		return useTtsToSay;
	}

	public void setUseTtsToSay(boolean useTtsToSay) {
		this.useTtsToSay = useTtsToSay;
		userPrefs.saveBoolean(USE_TTS_TO_SAY, useTtsToSay);
	}

	public String getPathToSoundFiles() {
		return pathToSoundFiles;
	}

	public void setPathToSoundFiles(String pathToSoundFiles) {
		this.pathToSoundFiles = pathToSoundFiles;
		userPrefs.saveString(PATH_TO_SOUND_FILES, pathToSoundFiles);
	}

	public int getTextPadding() {
		return textPadding;
	}

	public void setTextPadding(int textPadding) {
		this.textPadding = textPadding;
	}

	public float getTextSize() {
		return textSize;
	}

	public void setTextSize(float textSize) {
		this.textSize = textSize;
	}

	public int getStartFromNumber() {
		return startFromNumber;
	}

	public int getRepeatCount() {
		return repeatCount;
	}

	public void setRepeatCount(int repeatCount) {
		this.repeatCount = repeatCount;
		userPrefs.saveInt(REPEAT_COUNT, repeatCount);
	}

	public Dict getDict() {
		return dict;
	}

	public ArrayList<Dict> getListOfDicts() {
		return listOfDicts;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String languageTag) {
		language = languageTag;
		userPrefs.saveString(LANGUAGE, languageTag);
	}

	public int getUsedTts() {
		return usedTts;
	}

	public void setUsedTts(int usedTts) {
		this.usedTts = usedTts;
		userPrefs.saveInt(USED_TTS, usedTts);
	}

}
