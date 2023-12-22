package com.boden.lingvolearner;

import static com.boden.lingvolearner.services.ContextHolder.getLearningManager;
import static com.boden.lingvolearner.services.ContextHolder.getSettingsHolder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.boden.lingvolearner.pojo.WordCard;
import com.boden.lingvolearner.services.ContextHolder;
import com.boden.lingvolearner.services.DictionaryFileManipulator;
import com.boden.lingvolearner.services.SettingsHolder;
import com.boden.lingvolearner.sqlite.DBManager;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class PlayerActivity extends Activity implements TextToSpeech.OnInitListener {

	private static final int REQUEST_CODE_SELECT_DB = 4;

	private static Map<Integer, Integer> REPEAT_ICON = Map.of(0, R.drawable.repeat_none, 1, R.drawable.repeat1, 2, R.drawable.repeat_all);

	private TextView wordLabel;
	private TextView translationLabel;
	private TextView playing;
	private ImageButton playButton;
	private ImageButton repeatButton;
	private Switch reverse;
	private TextToSpeech mTts;
	private boolean finishedSpeak;
	private boolean paused = true;
	private int repeat = 2;
	private int lastIndex;
	private int selectedIndex;
	private List<String> names = new ArrayList<>();
	private List<WordCard> wordCards = ContextHolder.getAllWordCards();

	private ListView playList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player);
		ContextHolder.getInstance().setLoadedWordCards(new ArrayList<>());
		wordLabel = findViewById(R.id.word);
		translationLabel = findViewById(R.id.translation);
		playing = findViewById(R.id.playing);
		reverse = findViewById(R.id.reverse);

		mTts = new TextToSpeech(getApplicationContext(), this);

		setUpPlayButton();
		setUpRepeatButton();
		setUpAddButton();

		setUpPreviousButton();
		setUpNextButton();

		playList = (ListView) findViewById(R.id.playlist);
		registerForContextMenu(playList);
		playList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View itemClicked, int position, long id) {
				if (!names.isEmpty()) {
					selectedIndex = position;
					playing.setText(names.get(position));
					wordCards = ContextHolder.getInstance().getLoadedWordCards().get(position);
					play();
				}
			}
		});
		String[] items = new String[] { "not selected yet" };
		playList.setAdapter(new ArrayAdapter<String>(PlayerActivity.this, R.layout.list_item, items));
	}

	private void setUpAddButton() {
		ImageButton button = (ImageButton) findViewById(R.id.add);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(PlayerActivity.this, SelectDbDictionaryActivity.class);
				intent.putExtra(Constants.NAME_AND_TYPE_ONLY, true);
				startActivityForResult(intent, REQUEST_CODE_SELECT_DB);
			}
		});
	}

	private void setUpPlayButton() {
		playButton = (ImageButton) findViewById(R.id.playPause);
		playButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				play();
			}
		});
	}

	private void play() {
		paused = !paused;
		setPlayButtonIcon();
		if (!paused) {
			// runOnUiThread(new Speaker());
			new Thread(new Speaker()).start();
		}
	}

	private void setPlayButtonIcon() {
		if (!paused) {
			playButton.setImageResource(R.drawable.pause);
		} else {
			playButton.setImageResource(R.drawable.play);
		}
	}

	private void setUpRepeatButton() {
		repeatButton = (ImageButton) findViewById(R.id.repeat);
		repeatButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				repeat = (repeat + 1) % 3;
				setRepeatButtonIcon();
			}
		});
	}

	private void setRepeatButtonIcon() {
		repeatButton.setImageResource(REPEAT_ICON.get(repeat));
	}

	private void setUpNextButton() {
		ImageButton nextWordCard = (ImageButton) findViewById(R.id.nextWordCard);
		nextWordCard.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (lastIndex < wordCards.size() - 1) {
					lastIndex++;
					WordCard card = wordCards.get(lastIndex);
					updateCardDisplay(card);
				}
			}
		});
	}

	private void setUpPreviousButton() {
		ImageButton prevWordCard = (ImageButton) findViewById(R.id.prevWordCard);
		prevWordCard.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (lastIndex > 0) {
					lastIndex--;
					WordCard card = wordCards.get(lastIndex);
					updateCardDisplay(card);
				}
			}
		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (REQUEST_CODE_SELECT_DB == requestCode) {
			if (resultCode == RESULT_OK) {
				Bundle extras = intent.getExtras();
				boolean isCategory = extras.getBoolean("isCategory");
				String returnName = extras.getString("name");
				String prefix = isCategory ? getResources().getString(R.string.category)
						: getResources().getString(R.string.dictionary);
				names.add(prefix + ": " + returnName);
				playList.setAdapter(new ArrayAdapter<String>(PlayerActivity.this, R.layout.list_item,
						names.toArray(new String[] {})));
			}
		}
	}

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			updateLanguageSelection(getSettingsHolder().getLanguageFrom());
		} else {
			// Log.e(TAG, "Could not initialize TextToSpeech.");
		}
		// status =mTts.setEngineByPackageName("edu.cmu.cs.speech.tts.flite");
		mTts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
			@Override
			public void onStart(String utteranceId) {

			}

			@Override
			public void onDone(String utteranceId) {

				// System.out.println("finished ");
				finishedSpeak = true;
			}

			@Override
			public void onError(String utteranceId) {

			}
		});
	}

	public void updateLanguageSelection(String language) {
		// System.out.println("=====LLocale.forLanguageTag(language):
		// "+Locale.forLanguageTag(language));
		// System.out.println("=======Language is not : "+language);
		// System.out.println("=======Languages :
		// "+mTts.getAvailableLanguages());
		int result = mTts.setLanguage(Locale.forLanguageTag(language));
		if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
			System.out.println("=-=-Language is not available: " + language);
			// Log.e(TAG, "Language is not available.");
		} else {
			// soundButton.setEnabled(true);
		}
	}

	private void updateCardDisplay(WordCard card) {
		wordLabel.setText(card.getWord());
		translationLabel.setText(card.getTranslation());
	}

	@Override
	public void onDestroy() {
		if (mTts != null) {
			mTts.stop();
			mTts.shutdown();
		}
		super.onDestroy();
	}

	class Speaker implements Runnable {

		@Override
		public void run() {
			do {
				if (lastIndex >= wordCards.size()) {
					lastIndex = 0;
				}
				for (int i = lastIndex; i < wordCards.size(); i++) {
					WordCard card = wordCards.get(i);
					if (paused) {
						lastIndex = i - 1;
						return;
					}
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							updateCardDisplay(card);
						}
					});
					if (reverse.isChecked()) {
						updateLanguageSelection(getSettingsHolder().getLanguageTo());
						play(card.getTranslation());
						updateLanguageSelection(getSettingsHolder().getLanguageFrom());
						play(card.getWord());
					} else {
						updateLanguageSelection(getSettingsHolder().getLanguageFrom());
						play(card.getWord());
						updateLanguageSelection(getSettingsHolder().getLanguageTo());
						play(card.getTranslation());
					}
					makePause(200);
				}
				if (repeat > 0) {
					makePause(1000);
					List<List<WordCard>> loadedWordCards = ContextHolder.getInstance().getLoadedWordCards();
					if (repeat > 1) {
						selectedIndex = (selectedIndex + 1) % loadedWordCards.size();
					}
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							playing.setText(names.get(selectedIndex));
						}
					});
					wordCards = loadedWordCards.get(selectedIndex);
				}
			} while (!paused && repeat > 0);
			paused = true;
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					setPlayButtonIcon();
				}
			});
		}

		private void makePause(int time) {
			try {
				Thread.sleep(time);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		private void play(String word) {
			finishedSpeak = false;
			mTts.speak(word, TextToSpeech.QUEUE_FLUSH, null, TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID);
			while (!finishedSpeak) {
				makePause(10);
			}
		}

	}
}
