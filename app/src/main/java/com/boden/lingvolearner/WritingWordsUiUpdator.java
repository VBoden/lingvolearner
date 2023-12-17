package com.boden.lingvolearner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.boden.lingvolearner.services.ContextHolder;
import com.boden.lingvolearner.services.Stage;
import com.boden.lingvolearner.services.UiUpdator;

import static com.boden.lingvolearner.services.ContextHolder.getLearningManager;

public class WritingWordsUiUpdator implements UiUpdator {

	private TextView Vword;
	private EditText Venword;
	private GeneralMainActivity mainActivity;

	public WritingWordsUiUpdator(GeneralMainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

	@Override
	public void updateWord() {
		Vword.setText(getLearningManager().getWordToDisplay());
		Venword.setText(getLearningManager().getWordTranscription());
	}

	@Override
	public void updateUiOnNewPortionStarted() {
		int startFromNumber = ContextHolder.getSettingsHolder().getStartFromNumber();
		Context context = mainActivity.getApplicationContext();
		Toast toast = Toast.makeText(context,
				mainActivity.getResources().getString(R.string.words) + getLearningManager().getWordCard(startFromNumber).getWord()
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
		mainActivity.setContentView(R.layout.form3);

		Vword = (TextView) mainActivity.findViewById(R.id.word3);
		Venword = (EditText) mainActivity.findViewById(R.id.word2);

		final Button button1 = (Button) mainActivity.findViewById(R.id.but0);
		button1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ContextHolder.getLearningManager().checkAnswer(Venword.getText().toString());
			}
		});
		updateOnStageStart();
//		Intent intent = new Intent();
//		intent.putExtra(MainFormActivity.EXT_RESULT, "1");
//		setResult(RESULT_OK, intent);
//		finish();
	}

	@Override
	public void updateOnStageEnd() {
		ContextHolder.getUiUpdator(ContextHolder.getLearningManager().getCurrentStage().getNext()).createNewActivity();
//		Intent intent = new Intent();
//		intent.putExtra(MainFormActivity.EXT_RESULT, "10");
//		setResult(RESULT_OK, intent);
//		finish();
	}

	@Override
	public void showHint(String word, String answer) {
		Context context = mainActivity.getApplicationContext();
		Toast toast = Toast.makeText(context, word + " - " + answer, Toast.LENGTH_SHORT);
		toast.show();
	}
}
