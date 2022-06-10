package com.boden.lingvolearner;

import static com.boden.lingvolearner.services.ContextHolder.getLearningManager;

import java.util.List;

import com.boden.lingvolearner.pojo.WordCard;
import com.boden.lingvolearner.services.ContextHolder;
import com.boden.lingvolearner.services.Stage;
import com.boden.lingvolearner.services.UiUpdator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class WritingWordsActivity extends GeneralMainActivity implements UiUpdator {
	public static final int IDM_OPEN = 101;
	public static final int IDM_NEXT = 102;
	public static final int IDM_PREVIOUS = 103;
	public static final int IDM_OPTIONS = 104;
	public static final int IDM_HELP = 105;
	public static final int IDM_EXIT = 106;
	public static final int IDM_LASTOPEND = 107;

	TextView Vword;
	EditText Venword;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ContextHolder.registerUiUpdator(Stage.WRITING_WORDS, this);
//		ContextHolder.registerWordPlayer(Stage.WRITING_WORDS, new WordPlayerTTS(this));
		setContentView(R.layout.form3);

		Vword = (TextView) findViewById(R.id.word3);
		Venword = (EditText) findViewById(R.id.word2);

		final Button button1 = (Button) findViewById(R.id.but0);
		button1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				boolean isCorrect = ContextHolder.getLearningManager().checkAnswer(Venword.getText().toString());
				if (!isCorrect) {
					Context context = getApplicationContext();
					Toast toast = Toast.makeText(context,
							getLearningManager().getWordToDisplay() + " - " + getLearningManager().getWordAnswer(),
							Toast.LENGTH_SHORT);
					toast.show();
				}
			}
		});
		updateOnStageStart();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	// Menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, IDM_PREVIOUS, Menu.NONE, R.string.prev_step).setAlphabeticShortcut('p')
				.setIcon(R.drawable.go_previous);
		menu.add(Menu.NONE, IDM_NEXT, Menu.NONE, R.string.next_step).setAlphabeticShortcut('n')
				.setIcon(R.drawable.go_next);
		// menu.add(Menu.NONE, IDM_OPEN, Menu.NONE, "Вибрати словник")
		// .setAlphabeticShortcut('s').setIcon(R.drawable.folder);
		// menu.add(Menu.NONE, IDM_OPTIONS, Menu.NONE, "Налаштування")
		// .setAlphabeticShortcut('o').setIcon(R.drawable.preferences_system);

		menu.add(Menu.NONE, IDM_HELP, Menu.NONE, R.string.help).setAlphabeticShortcut('h')
				.setIcon(R.drawable.system_help);
		menu.add(Menu.NONE, IDM_EXIT, Menu.NONE, R.string.exit).setAlphabeticShortcut('x')
				.setIcon(R.drawable.application_exit);

		return (super.onCreateOptionsMenu(menu));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case IDM_NEXT:
			Intent intent = new Intent();
			intent.putExtra(MainFormActivity.EXT_RESULT, "1");
			setResult(RESULT_OK, intent);
			finish();
			break;

		case IDM_PREVIOUS:
			intent = new Intent();
			intent.putExtra(MainFormActivity.EXT_RESULT, "2");
			setResult(RESULT_OK, intent);
			finish();
			break;
		case IDM_HELP:
			intent = new Intent();
			intent.setClass(WritingWordsActivity.this, HelpActivity.class);
			startActivity(intent);
			break;
		// case IDM_OPTIONS:
		// break;
		// case IDM_HELP:
		// break;
		case IDM_EXIT:
			intent = new Intent();
			intent.putExtra(MainFormActivity.EXT_RESULT, "0");
			setResult(RESULT_OK, intent);
			finish();
			break;
		default:
			return false;
		}
		return true;
	}

	@Override
	public void updateWord() {
		Vword.setText(getLearningManager().getWordToDisplay());
		Venword.setText(getLearningManager().getWordTranscription());
	}

	@Override
	public void updateUiOnNewPortionStarted() {
		int startFromNumber = ContextHolder.getSettingsHolder().getStartFromNumber();
		Context context = getApplicationContext();
		Toast toast = Toast.makeText(context,
				getResources().getString(R.string.words) + getLearningManager().getWordCard(startFromNumber).getWord()
						+ "-" + getLearningManager().getWordCard(startFromNumber + 9).getWord() + " ("
						+ (startFromNumber + 1) + "-" + (startFromNumber + 10) + ")",
				Toast.LENGTH_SHORT);
		toast.show();
	}

	@Override
	public void updateOnStageStart() {
		updateWord();
	}

	@Override
	public void createNewActivity() {
		Intent intent = new Intent();
		intent.putExtra(MainFormActivity.EXT_RESULT, "1");
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	public void updateOnStageEnd() {
		finish();
	}
}
