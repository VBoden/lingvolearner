package com.boden.lingvolearner;

import com.boden.lingvolearner.services.UserPreferences;

import android.content.SharedPreferences;

public class UserPreferencesAdapter implements UserPreferences {
	SharedPreferences sharedPreferences;

	public UserPreferencesAdapter(SharedPreferences sharedPreferences) {
		this.sharedPreferences = sharedPreferences;
	}

	@Override
	public float getFloat(String key, float defaultValue) {
		return sharedPreferences.getFloat(key, defaultValue);
	}

	@Override
	public int getInt(String key, int defaultValue) {
		return sharedPreferences.getInt(key, defaultValue);
	}

	@Override
	public String getString(String key, String defaultValue) {
		return sharedPreferences.getString(key, defaultValue);
	}

	@Override
	public boolean getBoolean(String key, boolean defaultValue) {
		return sharedPreferences.getBoolean(key, defaultValue);
	}

	@Override
	public boolean contains(String key) {
		return sharedPreferences.contains(key);
	}

	@Override
	public void saveString(String key, String value) {
		SharedPreferences.Editor prefEditor = sharedPreferences.edit();
		prefEditor.putString(key, value);
		prefEditor.commit();
	}

	@Override
	public void saveFloat(String key, float value) {
		SharedPreferences.Editor prefEditor = sharedPreferences.edit();
		prefEditor.putFloat(key, value);
		prefEditor.commit();
	}

	@Override
	public void saveInt(String key, int value) {
		SharedPreferences.Editor prefEditor = sharedPreferences.edit();
		prefEditor.putInt(key, value);
		prefEditor.commit();
	}

	@Override
	public void saveBoolean(String key, boolean value) {
		SharedPreferences.Editor prefEditor = sharedPreferences.edit();
		prefEditor.putBoolean(key, value);
		prefEditor.commit();
	}
}
