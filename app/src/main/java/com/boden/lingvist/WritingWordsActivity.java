package com.boden.lingvist;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Random;

import android.annotation.TargetApi;
//import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class WritingWordsActivity extends GeneralMainActivity {
	public static final int IDM_OPEN = 101;
	public static final int IDM_NEXT = 102;
	public static final int IDM_PREVIOUS = 103;
	public static final int IDM_OPTIONS = 104;
	public static final int IDM_HELP = 105;
	public static final int IDM_EXIT = 106;
	public static final int IDM_LASTOPEND = 107;

	TextView Vword;
	EditText Venword;
	public static final String EXT_SLOVNIK1 = "slovnik[0]";
	// public static final String EXT_SLOVNIK2 = "slovnik[1]";
	public static final String EXT_SLOVNIK3 = "slovnik[2]";
	// public static final String EXT_POCH_NOM = "poch_nom";
	public static final String EXT_KROK_POVTORY = "krok_povtory";
	public static final String EXT_ROZM_MAS = "rozmir_mas";

	private String[][] slovnik = new String[2][10];
	// private int poch_nom;

	private int rozmir_mas, kilk_povt, krok_povtory, k_zal_sliv, k_zapis,
			vprava;
	private int kilk[] = new int[10];
	private Random rnd = new Random();
	private boolean useTtsToSay, useFilesToSay;
	private String pathToSoundFiles;

	private WordSpeaker speaker;

	@TargetApi(14)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.form3);

		SharedPreferences settings = getSharedPreferences(APP_PREFERENCES,
				MODE_PRIVATE);
		// poch_nom = 0;
		// kilk_povt = settings.getInt(KILK_POVT, 5);
		useTtsToSay = settings.getBoolean(USE_TTS_TO_SAY, true);
		useFilesToSay = settings.getBoolean(USE_FILES_TO_SAY, true);
		pathToSoundFiles = settings.getString(PATH_TO_SOUND_FILES, "");

		Bundle extras = getIntent().getExtras();
		String[] vocab = extras.getStringArray(EXT_SLOVNIK1);
		for (int i = 0; i < 10; i++) {
			slovnik[0][i] = vocab[i];
		}
		// vocab=extras.getStringArray(EXT_SLOVNIK2);
		// for (int i=0;i<10;i++){slovnik[1][i]=vocab[i];}
		String[] vocab2 = extras.getStringArray(EXT_SLOVNIK3);
		for (int i = 0; i < 10; i++) {
			slovnik[1][i] = vocab2[i];
		}
		// poch_nom=extras.getInt(EXT_POCH_NOM);
		kilk_povt = extras.getInt(EXT_KROK_POVTORY);
		krok_povtory = kilk_povt;
		rozmir_mas = extras.getInt(EXT_ROZM_MAS);

		k_zal_sliv = 10;
		for (int i = 0; i <= 9; i++) {
			kilk[i] = 0;
		}

		Vword = (TextView) findViewById(R.id.word3);
		Venword = (EditText) findViewById(R.id.word2);
		Functions3_1();

		speaker = new WordSpeaker(this, settings);

		final Button button1 = (Button) findViewById(R.id.but0);
		button1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (Venword.getText().toString().equals(slovnik[0][k_zapis])) {
					speaker.speakText(slovnik[0][k_zapis]);
					k_zal_sliv--;
					kilk[k_zapis]++;
					// -----відтворення звуку-----
					// if (jCheckBox1.isSelected()){
					// slovo=slovnik[k_zapis][0];
					// t.start();
					/*
					 * File f=new
					 * File(LesonDir+"/Sounds/"+slovnik[k_zapis][0]+".wav"); if
					 * (f.exists()){ PlayWord(f); }
					 */
					// -------кінець відтворення звуку------
					// ---------зупинка------------
					// try {
					// Thread.sleep(500);
					// } catch(InterruptedException ae) {
					// System.err.println("Interrupted");
					// }
					// }
					// ---------кінець зупинки------------
					if (k_zal_sliv == 0) {
						k_zal_sliv = 10;
						for (int i = 0; i <= 9; i++) {
							kilk[i] = 0;
						}
						krok_povtory--;
					}
					;
					if (krok_povtory > 0) {
						Venword.setText("");
						Functions3_1();
					} else {

						Intent intent = new Intent();
						intent.putExtra(MainFormActivity.EXT_RESULT, "1");
						setResult(RESULT_OK, intent);
						finish();
					}
					;
				} else {
					k_zal_sliv++;
					kilk[k_zapis]--;
					Context context = getApplicationContext();
					Toast toast = Toast.makeText(context, slovnik[1][k_zapis]
							+ " - " + slovnik[0][k_zapis], Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
			}
		});
	}

	public void Functions3_1() {
		do {
			k_zapis = rnd.nextInt(100) % 10;
		} while (kilk[k_zapis] >= 1);

		Vword.setText(slovnik[1][k_zapis]);
		Venword.setText("");
	}

	@Override
	public void onDestroy() {
		speaker.destroy();
		super.onDestroy();
	}

	// Menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, IDM_PREVIOUS, Menu.NONE, R.string.prev_step)
				.setAlphabeticShortcut('p').setIcon(R.drawable.go_previous);
		menu.add(Menu.NONE, IDM_NEXT, Menu.NONE, R.string.next_step)
				.setAlphabeticShortcut('n').setIcon(R.drawable.go_next);
	//	menu.add(Menu.NONE, IDM_OPEN, Menu.NONE, "Вибрати словник")
	//			.setAlphabeticShortcut('s').setIcon(R.drawable.folder);
		// menu.add(Menu.NONE, IDM_OPTIONS, Menu.NONE, "Налаштування")
		// .setAlphabeticShortcut('o').setIcon(R.drawable.preferences_system);

		menu.add(Menu.NONE, IDM_HELP, Menu.NONE, R.string.help)
		.setAlphabeticShortcut('h').setIcon(R.drawable.system_help);
		menu.add(Menu.NONE, IDM_EXIT, Menu.NONE, R.string.exit)
				.setAlphabeticShortcut('x')
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
}
