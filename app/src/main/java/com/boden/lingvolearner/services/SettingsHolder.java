package com.boden.lingvolearner.services;

import java.util.ArrayList;
import java.util.Locale;

import com.boden.lingvolearner.Dict;

import android.content.SharedPreferences;
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

	private SharedPreferences sharedPreferences;
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

	public SettingsHolder(SharedPreferences sharedPreferences) {
		this.sharedPreferences = sharedPreferences;
		init();
	}

	private void init() {
		textSize = sharedPreferences.getFloat(TEXT_SIZE, 0);
		textPadding = sharedPreferences.getInt(TEXT_PADDING, 0);
		repeatCount = sharedPreferences.getInt(REPEAT_COUNT, 5);
		language = sharedPreferences.getString(LANGUAGE, Locale.US.getLanguage());
		useTtsToSay = sharedPreferences.getBoolean(USE_TTS_TO_SAY, true);
		usedTts = sharedPreferences.getInt(USED_TTS, 1);
		useFilesToSay = sharedPreferences.getBoolean(USE_FILES_TO_SAY, true);
		pathToSoundFiles = sharedPreferences.getString(PATH_TO_SOUND_FILES, "");

		getDictsFromSettings();
		if (sharedPreferences.contains(DICTIONARIES) == true) {
			String s = sharedPreferences.getString(DICTIONARIES, "");
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
		if (sharedPreferences.contains(DICTIONARIES) == true) {
			String s = sharedPreferences.getString(DICTIONARIES, "");
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

	public void updateLastDictionary(Uri uri) {
		String[] segments = uri.getLastPathSegment().split("/");
		Dict newDict = new Dict(uri.toString(), segments[segments.length - 1]);
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

		SharedPreferences.Editor prefEditor = sharedPreferences.edit();
		prefEditor.putString(DICTIONARIES, dictionaries.toString());
		// Log.i("DEBUG_LastOpend", "saved dictionary: " + dictionaries.toString());
		prefEditor.commit();
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
		SharedPreferences.Editor prefEditor = sharedPreferences.edit();
		prefEditor.putFloat(TEXT_SIZE, textSize);
		prefEditor.commit();
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
		SharedPreferences.Editor prefEditor = sharedPreferences.edit();
		prefEditor.putInt(TEXT_PADDING, textPadding);
		prefEditor.commit();
	}

	public boolean isUseFilesToSay() {
		return useFilesToSay;
	}

	public void setUseFilesToSay(boolean useFilesToSay) {
		this.useFilesToSay = useFilesToSay;
		SharedPreferences.Editor prefEditor = sharedPreferences.edit();
		prefEditor.putBoolean(USE_FILES_TO_SAY, useFilesToSay);
		prefEditor.commit();
	}

	public boolean isUseTtsToSay() {
		return useTtsToSay;
	}

	public void setUseTtsToSay(boolean useTtsToSay) {
		this.useTtsToSay = useTtsToSay;
		SharedPreferences.Editor prefEditor = sharedPreferences.edit();
		prefEditor.putBoolean(USE_TTS_TO_SAY, useTtsToSay);
		prefEditor.commit();
	}

	public String getPathToSoundFiles() {
		return pathToSoundFiles;
	}

	public void setPathToSoundFiles(String pathToSoundFiles) {
		this.pathToSoundFiles = pathToSoundFiles;
		SharedPreferences.Editor prefEditor = sharedPreferences.edit();
		prefEditor.putString(PATH_TO_SOUND_FILES, pathToSoundFiles);
		prefEditor.commit();
	}

	public SharedPreferences getSharedPreferences() {
		return sharedPreferences;
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
		SharedPreferences.Editor prefEditor = sharedPreferences.edit();
		prefEditor.putInt(REPEAT_COUNT, repeatCount);
		prefEditor.commit();
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
		SharedPreferences.Editor prefEditor = sharedPreferences.edit();
		prefEditor.putString(LANGUAGE, languageTag);
		prefEditor.commit();
	}

	public int getUsedTts() {
		return usedTts;
	}

	public void setUsedTts(int usedTts) {
		this.usedTts = usedTts;
		SharedPreferences.Editor prefEditor = sharedPreferences.edit();
		prefEditor.putInt(USED_TTS, usedTts);
		prefEditor.commit();
	}

}
