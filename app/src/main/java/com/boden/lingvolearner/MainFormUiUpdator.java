package com.boden.lingvolearner;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.boden.lingvolearner.pojo.WordCard;
import com.boden.lingvolearner.services.ContextHolder;
import com.boden.lingvolearner.services.DictionaryFileManipulator;
import com.boden.lingvolearner.services.Stage;
import com.boden.lingvolearner.services.UiUpdator;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static com.boden.lingvolearner.services.ContextHolder.getLearningManager;

@TargetApi(30)
public class MainFormUiUpdator implements UiUpdator {
	private TextView Vtransc;
	private TextView Vword;
	private ListView listView;
	private GeneralMainActivity mainActivity;
	private ArrayAdapter<String> adapt;

	public MainFormUiUpdator(GeneralMainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

	private int getStartFromNumber() {
		return ContextHolder.getSettingsHolder().getStartFromNumber();
	}

	public void chooseAndFillNativeWord() {
		Vword.setText(getLearningManager().getWordToDisplay());
		Vtransc.setText(getLearningManager().getWordTranscription());
	}

	public void listSetAdapter() {
		adapt = new ArrayAdapter<String>(mainActivity, R.layout.list_item,
				getLearningManager().getWordChoices()) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View row;

				if (null == convertView) {
					LayoutInflater mInflater = mainActivity.getLayoutInflater();
					row = mInflater.inflate(R.layout.list_item, null);
				} else {
					row = convertView;
				}

				TextView tv = (TextView) row.findViewById(R.id.textView1);
				float textSize = ContextHolder.getSettingsHolder().getTextSize();
				if (textSize == 0) {
					textSize = tv.getTextSize();
				}
				tv.setTextSize(textSize);
				int textPadding = ContextHolder.getSettingsHolder().getTextPadding();
				if (textPadding == 0) {
					textPadding = tv.getPaddingBottom();
				}
				tv.setPadding(0, textPadding, 0, textPadding);
				tv.setText(Html.fromHtml(getItem(position)));
				return row;
			}
		};
		listView.setAdapter(adapt);
	}

	public void updateList() {
		adapt.notifyDataSetChanged();
	}

	@Override
	public void updateUiOnNewPortionStarted() {
		int startFromNumber = getStartFromNumber();
		Context context = mainActivity.getApplicationContext();
		Toast toast = Toast.makeText(context,
				mainActivity.getResources().getString(R.string.words) + ContextHolder.getAllWordCards().get(startFromNumber).getWord() + "-"
						+ ContextHolder.getAllWordCards().get(startFromNumber + 9).getWord() + " (" + (startFromNumber + 1) + "-"
						+ (startFromNumber + 10) + ")",
				Toast.LENGTH_SHORT);
		toast.show();
	}

	@Override
	public void updateWord() {
		Vword.setText(getLearningManager().getWordToDisplay());
		Vtransc.setText(getLearningManager().getWordTranscription());
	}

	@Override
	public void updateOnStageStart() {
		Vword.setText(getLearningManager().getWordToDisplay());
		Vtransc.setText(getLearningManager().getWordTranscription());
		listSetAdapter();
		mainActivity.getMenu().getItem(0).setEnabled(getLearningManager().hasPreviousStep());
	}

	@Override
	public void createNewActivity() {
		mainActivity.setContentView(R.layout.main_form);
		mainActivity.setTitle(R.string.app_name2);

		Vword = (TextView) mainActivity.findViewById(R.id.word);
		Vword.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				getLearningManager().playOnClick();
			}
		});
		Vtransc = (TextView) mainActivity.findViewById(R.id.transcription);
		listView = (ListView) mainActivity.findViewById(R.id.list);
		mainActivity.registerForContextMenu(listView);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View itemClicked, int position, long id) {
				getLearningManager().checkAnswer(getLearningManager().getWordChoices()[position]);
			}
		});
	}

	@Override
	public void updateOnStageEnd() {
		// TODO Auto-generated method stub
	}

	@Override
	public void showHint(String word, String answer) {
		Context context = mainActivity.getApplicationContext();
		Toast toast = Toast.makeText(context, word + " - " + answer, Toast.LENGTH_SHORT);
		toast.show();
	}
}
