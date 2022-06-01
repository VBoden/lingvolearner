package com.boden.lingvist;

import java.util.Locale;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class OptionActivity extends GeneralMainActivity {
	public static final String EXT_POCH_NOM = "poch_nom";
	public static final String EXT_KILK_POVT = "kilk_povt";
	public static final String EXT_NAME_DIR = "dirName";
	protected static final int REQUEST_CODE_SELECT_DIR = 0;

	private int kilk_povt, usedTts;
	EditText edit1, edit2;
	RadioButton radioButton1;
	RadioButton radioButton2;
	Button button;
	boolean useTtsToSay, useFilesToSay;
	String pathToSoundFiles;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.options);
		kilk_povt = 5;
		SharedPreferences settings = getSharedPreferences(APP_PREFERENCES,
				MODE_PRIVATE);
		// if (settings.contains(KILK_POVT) == true) {
		kilk_povt = settings.getInt(KILK_POVT, 5);
		// }
		useTtsToSay = settings.getBoolean(USE_TTS_TO_SAY, true);
		usedTts = settings.getInt(USED_TTS, 1);
		useFilesToSay = settings.getBoolean(USE_FILES_TO_SAY, true);
		pathToSoundFiles = settings.getString(PATH_TO_SOUND_FILES, "");

		// Bundle extras = getIntent().getExtras();
		// startFromNumber=Integer.parseInt(extras.getString(EXT_POCH_NOM));
		// kilk_povt=Integer.parseInt(extras.getString(EXT_KILK_POVT));

		TextView text1 = (TextView) findViewById(R.id.text1);
		edit1 = (EditText) findViewById(R.id.edit1);
		edit1.setText(pathToSoundFiles);
		edit1.addTextChangedListener(new TextWatcher() {			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub				
				SharedPreferences settings = getSharedPreferences(
						APP_PREFERENCES, MODE_PRIVATE);
				SharedPreferences.Editor prefEditor = settings.edit();				
				prefEditor.putString(PATH_TO_SOUND_FILES,
						edit1.getText().toString());								
				prefEditor.commit();
			}
		});

		TextView text2 = (TextView) findViewById(R.id.text2);
		edit2 = (EditText) findViewById(R.id.edit2);
		edit2.setText("" + kilk_povt);
		edit2.setInputType(InputType.TYPE_CLASS_NUMBER);
		// Log.i("DEBUG_OPTION_ACTIVITY","1");
		edit2.setSelection(edit2.getText().toString().length());
		edit2.addTextChangedListener(new TextWatcher() {			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub				
				SharedPreferences settings = getSharedPreferences(
						APP_PREFERENCES, MODE_PRIVATE);
				SharedPreferences.Editor prefEditor = settings.edit();
				if (edit2.getText().length()>0){
				prefEditor.putInt(KILK_POVT,
						Integer.parseInt(edit2.getText().toString()));}
				else{prefEditor.putInt(KILK_POVT,5);}				
				prefEditor.commit();
			}
		});

		OnClickListener radioButtonOnClick = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int number = 1;
				if (v.getId() == R.id.radioButton1) {
					number = USE_GOOGLE_TTS;
					Toast toast = Toast.makeText(OptionActivity.this,
							"Vibrano 1", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				} else {
					number = USE_FLITE_TTS;
					Toast toast = Toast.makeText(OptionActivity.this,
							"Vibrano 2", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
				SharedPreferences settings = getSharedPreferences(
						APP_PREFERENCES, MODE_PRIVATE);
				SharedPreferences.Editor prefEditor = settings.edit();
				prefEditor.putInt(USED_TTS, number);
				// Log.i("DEBUG_INFO_MY","edit2.getText()="+Integer.parseInt(edit2.getText().toString()));
				prefEditor.commit();
			}
		};
		radioButton1 = (RadioButton) findViewById(R.id.radioButton1);
		radioButton2 = (RadioButton) findViewById(R.id.radioButton2);
		if (usedTts == USE_GOOGLE_TTS) {
			radioButton1.setChecked(true);
		} else {
			radioButton2.setChecked(true);
		}
		radioButton1.setOnClickListener(radioButtonOnClick);
		radioButton2.setOnClickListener(radioButtonOnClick);

		button = (Button) findViewById(R.id.but);
		button.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(OptionActivity.this, SelectDirActivity.class);
			//	intent.putExtra(SelectDirActivity.EXT_NAME_DIR, pathToSoundFiles);				
				startActivityForResult(intent, REQUEST_CODE_SELECT_DIR);
				
			}
		});

		Spinner dropdown = findViewById(R.id.languageSelector);
		String[] items = new String[]{"English (UK)", "English (US)", "Espanol", "French (France)", "German", "Polish", "Українська"};
		final Locale[] languageCodes = new Locale[]{Locale.UK, Locale.US, new Locale("es", "ES"),
				Locale.FRANCE, Locale.GERMAN, new Locale("pl","PL"), new Locale("uk", "UA")};
		ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
		dropdown.setAdapter(adapter);
		String language = settings.getString(LANGUAGE, Locale.US.getLanguage());
		int pos = 0;
		for (Locale lang : languageCodes) {
			if (lang.toLanguageTag().equalsIgnoreCase(language)) {
				break;
			}
			pos++;
		}
		if(pos<languageCodes.length) {
			dropdown.setSelection(pos);
		}
		dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
				SharedPreferences settings = getSharedPreferences(
						APP_PREFERENCES, MODE_PRIVATE);
				SharedPreferences.Editor prefEditor = settings.edit();
				prefEditor.putString(LANGUAGE,
						languageCodes[position].toLanguageTag());
				prefEditor.commit();
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});
		
		
		final CheckBox checkBox1 = (CheckBox) findViewById(R.id.checkBox1);
		if (useTtsToSay) {
			checkBox1.setChecked(true);
			setRadioButtonsEnabled(true);
		} else {
			checkBox1.setChecked(false);
			setRadioButtonsEnabled(false);
		}
		checkBox1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences settings = getSharedPreferences(
						APP_PREFERENCES, MODE_PRIVATE);
				SharedPreferences.Editor prefEditor = settings.edit();
				if (checkBox1.isChecked()) {
					prefEditor.putBoolean(USE_TTS_TO_SAY, true);
					setRadioButtonsEnabled(true);					
				} else {
					prefEditor.putBoolean(USE_TTS_TO_SAY, false);
					setRadioButtonsEnabled(false);
				}
				prefEditor.commit();
			}
		});

		final CheckBox checkBox2 = (CheckBox) findViewById(R.id.checkBox2);
		if (useFilesToSay) {
			checkBox2.setChecked(true);
			setForChB2Enabled(true);			
		} else {
			checkBox2.setChecked(false);
			setForChB2Enabled(false);			
		}
		checkBox2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences settings = getSharedPreferences(
						APP_PREFERENCES, MODE_PRIVATE);
				SharedPreferences.Editor prefEditor = settings.edit();
				if (checkBox2.isChecked()) {
					prefEditor.putBoolean(USE_FILES_TO_SAY, true);
					setForChB2Enabled(true);
				} else {
					prefEditor.putBoolean(USE_FILES_TO_SAY, false);
					setForChB2Enabled(false);
				}
				prefEditor.commit();
			}
		});

	}

	private void setRadioButtonsEnabled(boolean b) {
		radioButton1.setEnabled(b);
		radioButton2.setEnabled(b);
	}
	private void setForChB2Enabled(boolean b) {
		edit1.setEnabled(b);
		button.setEnabled(b);
	}

	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_CODE_SELECT_DIR:
			if (resultCode == RESULT_OK) {
				Bundle extras = data.getExtras();
				String result = extras.getString(EXT_NAME_DIR)+"/";
				if (result!=null){
					edit1.setText(result);
					SharedPreferences settings = getSharedPreferences(
							APP_PREFERENCES, MODE_PRIVATE);
					SharedPreferences.Editor prefEditor = settings.edit();				
					prefEditor.putString(PATH_TO_SOUND_FILES,
							edit1.getText().toString());								
					prefEditor.commit();	
				}
				}
				break;
			}
		}
}
