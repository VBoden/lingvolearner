package com.boden.lingvolearner;

import static com.boden.lingvolearner.services.ContextHolder.getLearningManager;
import static com.boden.lingvolearner.services.ContextHolder.getSettingsHolder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.boden.lingvolearner.pojo.WordCard;
import com.boden.lingvolearner.services.ContextHolder;
import com.boden.lingvolearner.services.DictionaryFileManipulator;
import com.boden.lingvolearner.services.SettingsHolder;
import com.boden.lingvolearner.sqlite.DBManager;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
import android.widget.Toast;

public class OptionActivity extends GeneralMainActivity {
	public static final String EXT_NAME_DIR = "dirName";
//	private static final int REQUEST_CODE_SELECT_DIR = 0;
	private static final int REQUEST_CODE_SELECT_DB = 1;
//	private static final int USE_GOOGLE_TTS = 1;
	private static final int USE_FLITE_TTS = 2;

//	private EditText pathToSoundFilesField;
//	private RadioButton radioButton1;
//	private RadioButton radioButton2;
//	private Button button;
	private EditText edit2;
	private List<String> languages = new ArrayList<>();
	private List<String> languageCodes = new ArrayList<>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.options);
		setUpLanguagesSelectors();
//		setUpPathToSoundFiles();
		setUpRepeatCountField();
//		setUpTTSSelection();
		setUpCheckBoxUseTts();

//		button = (Button) findViewById(R.id.but);
//		button.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent();
//				intent.setClass(OptionActivity.this, SelectDirActivity.class);
//				// intent.putExtra(SelectDirActivity.EXT_NAME_DIR,
//				// pathToSoundFiles);
//				startActivityForResult(intent, REQUEST_CODE_SELECT_DIR);
//
//			}
//		});
		setUpUpdateDB();
//		setUpCheckBoxUseFiles();
	}

	private void setUpUpdateDB() {
		Button updateDB = findViewById(R.id.updateDB);
		updateDB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
				intent.addCategory(Intent.CATEGORY_OPENABLE);
				intent.setType("*/*");
				try {
					startActivityForResult(intent, REQUEST_CODE_SELECT_DB);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
	}

	private void setUpLanguagesSelectors() {
		DBManager dbManager = new DBManager(this);
		try {
			dbManager.open();
			Cursor cursor = dbManager.fetchLanguages();
			fetchLanguages(cursor);
			setUpLanguageSelector(R.id.languageFrom, getSettingsHolder()::setLanguageFrom,
					getSettingsHolder()::getLanguageFrom, true);
			setUpLanguageSelector(R.id.languageTo, getSettingsHolder()::setLanguageTo,
					getSettingsHolder()::getLanguageTo, false);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbManager.close();
		}
	}

	private void setUpLanguageSelector(int id, Consumer<String> languageSetter, Supplier<String> languageGetter,
			boolean isFromLanguage) {
		Spinner dropdown = (Spinner) findViewById(id);
		ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
				languages.toArray(new String[] {}));
		dropdown.setAdapter(adapter);
		String language = languageGetter.get();
		int pos = 0;
		for (String lang : languageCodes) {
			if (lang.equalsIgnoreCase(language)) {
				break;
			}
			pos++;
		}
		if (pos < languageCodes.size()) {
			dropdown.setSelection(pos);
		}
		dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
				languageSetter.accept(languageCodes.get(position));
				if (isFromLanguage) {
					ContextHolder.getWordSpeaker().updateLanguageSelection(languageCodes.get(position));
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});
	}

	private void fetchLanguages(Cursor cursor) {
		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				languageCodes.add(cursor.getString(0));
				languages.add(cursor.getString(1));
			}
		}
	}

//	private void setUpPathToSoundFiles() {
//		pathToSoundFilesField = (EditText) findViewById(R.id.edit1);
//		pathToSoundFilesField.setText(getSettingsHolder().getPathToSoundFiles());
//		pathToSoundFilesField.addTextChangedListener(new TextWatcher() {
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before, int count) {
//			}
//
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//			}
//
//			@Override
//			public void afterTextChanged(Editable s) {
//				getSettingsHolder().setPathToSoundFiles(pathToSoundFilesField.getText().toString());
//			}
//		});
//	}

	private void setUpRepeatCountField() {
		edit2 = (EditText) findViewById(R.id.edit2);
		edit2.setText("" + getSettingsHolder().getRepeatCount());
		edit2.setInputType(InputType.TYPE_CLASS_NUMBER);
		// Log.i("DEBUG_OPTION_ACTIVITY","1");
		edit2.setSelection(edit2.getText().toString().length());
		edit2.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (edit2.getText().length() > 0) {
					getSettingsHolder().setRepeatCount(Integer.parseInt(edit2.getText().toString()));
				}
			}
		});
	}

//	private void setUpTTSSelection() {
//		OnClickListener radioButtonOnClick = new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				int number = 1;
//				if (v.getId() == R.id.radioButton1) {
//					number = USE_GOOGLE_TTS;
//					Toast toast = Toast.makeText(OptionActivity.this, "Vibrano 1", Toast.LENGTH_SHORT);
//					toast.setGravity(Gravity.CENTER, 0, 0);
//					toast.show();
//				} else {
//					number = USE_FLITE_TTS;
//					Toast toast = Toast.makeText(OptionActivity.this, "Vibrano 2", Toast.LENGTH_SHORT);
//					toast.setGravity(Gravity.CENTER, 0, 0);
//					toast.show();
//				}
//				getSettingsHolder().setUsedTts(number);
//			}
//		};
//		radioButton1 = (RadioButton) findViewById(R.id.radioButton1);
//		radioButton2 = (RadioButton) findViewById(R.id.radioButton2);
//		if (getSettingsHolder().getUsedTts() == USE_GOOGLE_TTS) {
//			radioButton1.setChecked(true);
//		} else {
//			radioButton2.setChecked(true);
//		}
//		radioButton1.setOnClickListener(radioButtonOnClick);
//		radioButton2.setOnClickListener(radioButtonOnClick);
//	}

	private void setUpCheckBoxUseTts() {
		final CheckBox checkBoxUseTts = (CheckBox) findViewById(R.id.checkBox1);
		checkBoxUseTts.setChecked(getSettingsHolder().isUseTtsToSay());
//		setRadioButtonsEnabled(getSettingsHolder().isUseTtsToSay());
		checkBoxUseTts.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getSettingsHolder().setUseTtsToSay(checkBoxUseTts.isChecked());
//				setRadioButtonsEnabled(checkBoxUseTts.isChecked());
			}
		});
	}

//	private void setUpCheckBoxUseFiles() {
//		final CheckBox checkBoxUseFiles = (CheckBox) findViewById(R.id.checkBox2);
//		checkBoxUseFiles.setChecked(getSettingsHolder().isUseFilesToSay());
//		setForChB2Enabled(getSettingsHolder().isUseFilesToSay());
//		checkBoxUseFiles.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				getSettingsHolder().setUseFilesToSay(checkBoxUseFiles.isChecked());
//				setForChB2Enabled(checkBoxUseFiles.isChecked());
//			}
//		});
//	}

//	private void setRadioButtonsEnabled(boolean b) {
//		radioButton1.setEnabled(b);
//		radioButton2.setEnabled(b);
//	}
//
//	private void setForChB2Enabled(boolean b) {
//		pathToSoundFilesField.setEnabled(b);
//		button.setEnabled(b);
//	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
//		case REQUEST_CODE_SELECT_DIR:
//			if (resultCode == RESULT_OK) {
//				Bundle extras = data.getExtras();
//				String result = extras.getString(EXT_NAME_DIR) + "/";
//				if (result != null) {
//					pathToSoundFilesField.setText(result);
//					getSettingsHolder().setPathToSoundFiles(result);
//				}
//			}
//			break;
		case REQUEST_CODE_SELECT_DB:
			if (resultCode == RESULT_OK) {
				Uri uri = data.getData();
				DBManager dbManager = new DBManager(this);
				dbManager.close();
				try {
					dbManager.copyDBfile(getContentResolver().openInputStream(uri));
					Toast toast = Toast.makeText(getApplicationContext(), "Базу успішно оновлено.", Toast.LENGTH_SHORT);
					toast.show();
				} catch (IOException e) {
					e.printStackTrace();
					Toast toast = Toast.makeText(getApplicationContext(), "Проблеми із читанням/записом файлу!",
							Toast.LENGTH_SHORT);
					toast.show();
				} catch (Exception e) {
					e.printStackTrace();
					Toast toast = Toast.makeText(getApplicationContext(), "Не вдалось оновити базу!",
							Toast.LENGTH_SHORT);
					toast.show();
				} finally {
					dbManager.close();
				}
			}
			break;
		}
	}
}
