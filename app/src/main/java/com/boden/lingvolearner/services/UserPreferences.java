package com.boden.lingvolearner.services;

public interface UserPreferences {

	float getFloat(String key, float defaultValue);

	int getInt(String key, int defaultValue);

	String getString(String key, String defaultValue);

	boolean getBoolean(String key, boolean defaultValue);

	boolean contains(String key);

	void saveString(String key, String value);

	void saveFloat(String key, float value);

	void saveInt(String key, int value);

	void saveBoolean(String key, boolean value);

}
