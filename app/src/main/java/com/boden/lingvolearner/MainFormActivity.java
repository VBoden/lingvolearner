package com.boden.lingvolearner;

//import android.app.Activity;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;



//import com.vboden.lingvolearner.R;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.text.Html;
import android.text.InputType;
//import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

@TargetApi(14)
public class MainFormActivity extends GeneralMainActivity {
	private TextView Vword, Vtransc;
	private ListView listView;
	private String vocab;
	private String[] spisok2 = { "1", "2", "3", "4", "5", "6", "7", "8", "9",
			"0" };
	// String[][] spisok0=new String[2][20];
	private String[][] slovnik = new String[3][20000];
	private int rozmir_mas, startFromNumber, kilk_povt, krok_povtory,
			k_zal_sliv, k_zapis, vprava;
	private int kilk[] = new int[10];
	private Random rnd = new Random();

	private WordSpeaker speaker;

	private int k;
	private Dict dict;
	private ArrayList<Dict> listOfDicts = new ArrayList<>();
	private float textSize = 0;
	private int textPadding = 0;

	public static final String EXT_NAME_VOC = "voc_name";
	public static final String EXT_POCH_NOM = "poch_nom";
	public static final String EXT_KILK_POVT = "kilk_povt";
	public static final String EXT_RESULT = "result";

	public static final int IDM_OPEN = 101;
	public static final int IDM_NEXT = 102;
	public static final int IDM_PREVIOUS = 103;
	public static final int IDM_OPTIONS = 104;
	public static final int IDM_HELP = 105;
	public static final int IDM_EXIT = 106;
	public static final int IDM_LASTOPEND = 107;

	private static final int REQUEST_CODE_FORM3_ACTIVITY = 1;
	private static final int REQUEST_CODE_OPTION_ACTIVITY = 2;
	private static final int REQUEST_CODE_SELECTDICT = 3;
	private static final int REQUEST_CODE_LAST_OPEND_ACTIVITY = 3;

	private static final int IDD_SET_START_NUMBER = 1;

	// @TargetApi(14)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_form);
		setTitle(R.string.app_name2);

		// ActionBar actionBar = getActionBar();
		// actionBar.show();
		// actionBar.setDisplayHomeAsUpEnabled(true);

		Vword = (TextView) findViewById(R.id.word);
		Vword.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (vprava == 1) {
					speaker.speakText(slovnik[0][k_zapis]);
				}
			}
		});
		// Vword.setText(slovnik[2][0]);
		Vtransc = (TextView) findViewById(R.id.transcription);
		// Vtransc.setText(slovnik[2][1]);
		listView = (ListView) findViewById(R.id.list);
		registerForContextMenu(listView);

		/*
		 * spisok2 = new String[10]; for (int i = 0; i < 10; i++) { spisok2[i] =
		 * "111"; }
		 */
		// listView.setAdapter(new ArrayAdapter<String>(this,
		// R.layout.list_item, spisok2));
	//	Log.i("DEBUG_INFO_MY", "adding onitemclicklistener");
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View itemClicked,
					int position, long id) {
				if (vprava == 1) {
					if (spisok2[position].equals(slovnik[2][k_zapis])) {
						k_zal_sliv--;
						kilk[k_zapis - startFromNumber]++;
						if (k_zal_sliv == 0) {
							k_zal_sliv = 10;
							for (int i = 0; i <= 9; i++) {
								kilk[i] = 0;
							}
							krok_povtory--;
						}
						;
						if (krok_povtory > 0) {
							Functions2();
						} else {
							vprava = 2;
							krok_povtory = kilk_povt;
							for (int i = 0; i <= 9; i++) {
								kilk[i] = 0;
							}
							// jButton13.setEnabled(true);
							Functions2_1();
						}
						;
					} else {
						k_zal_sliv++;
						kilk[k_zapis - startFromNumber]--;
						// showDialog(0);
						Context context = MainFormActivity.this
								.getApplicationContext();
						Toast toast = Toast.makeText(context,
								slovnik[0][k_zapis] + " - "
										+ slovnik[2][k_zapis],
								Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();						
					}
				}
	// —————————————————————————————————————————————————— Другий етап ——————————————————————————————————————————————————————
				else {
					if (spisok2[position].equals(slovnik[0][k_zapis])) {
						speaker.speakText(slovnik[0][k_zapis]);

						k_zal_sliv--;
						kilk[k_zapis - startFromNumber]++;
						if (k_zal_sliv == 0) {
							k_zal_sliv = 10;
							for (int i = 0; i <= 9; i++) {
								kilk[i] = 0;
							}
							krok_povtory--;
						}
						;
						if (krok_povtory > 0) {
							Functions2_2();
						} else {
							vprava = 3;
							krok_povtory = kilk_povt;
							for (int i = 0; i <= 9; i++) {
								kilk[i] = 0;
							}

							if (startFromNumber == rozmir_mas - 10) {
								// jButton14.setEnabled(false);
							}
							Intent intent = new Intent();
							intent.setClass(MainFormActivity.this,
									WritingWordsActivity.class);
							String[] vocab = new String[10];
							for (int i = 0; i < 10; i++) {
								vocab[i] = slovnik[0][i + startFromNumber];
							}
							intent.putExtra(WritingWordsActivity.EXT_SLOVNIK1, vocab);
							String[] vocab2 = new String[10];
							for (int i = 0; i < 10; i++) {
								vocab2[i] = slovnik[2][i + startFromNumber];
							}
							intent.putExtra(WritingWordsActivity.EXT_SLOVNIK3, vocab2);
							intent.putExtra(WritingWordsActivity.EXT_KROK_POVTORY,
									krok_povtory);
							intent.putExtra(WritingWordsActivity.EXT_ROZM_MAS,
									rozmir_mas);
							startActivityForResult(intent,
									REQUEST_CODE_FORM3_ACTIVITY);
							// finish();
						}
						;
					} else {
						k_zal_sliv++;
						kilk[k_zapis - startFromNumber]--;
						// showDialog(1);
						Context context = MainFormActivity.this
								.getApplicationContext();
						Toast toast = Toast.makeText(context,
								slovnik[2][k_zapis] + " - "
										+ slovnik[0][k_zapis],
								Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					}
				}

			}
		});

	//	Log.i("DEBUG_INFO_MY", "now loaded settings");
		SharedPreferences settings = getSharedPreferences(APP_PREFERENCES,
				MODE_PRIVATE);
		// poch_nom = 0;
		textSize = settings.getFloat(TEXT_SIZE, 0);
		textPadding = settings.getInt(TEXT_PADDING, 0);
		kilk_povt = settings.getInt(KILK_POVT, 5);

		speaker = new WordSpeaker(this, settings);

		boolean hasDict = false;
		if (settings.contains(DICTIONARIES) == true) {
			String s = settings.getString(DICTIONARIES, "");
			if (s.length() > 0) {
			//	Log.i("DEBUG_INFO_MY", "length>0");
				dict = new Dict(s.substring(s.indexOf("<dict>") + 6,
						s.indexOf("</dict>")));
			//	Log.i("DEBUG_INFO_MY", "was created Dict");
				vocab = dict.getPath();
				startFromNumber = dict.getBeginFrom();
			//	Log.i("DEBUG_INFO_MY", "now started loadDict");
				if ((new File(vocab)).exists()){
					loadDict(Uri.parse(vocab));
			//	Log.i("DEBUG_INFO_MY", "finished loadDict");
				hasDict = true;}
			}
		}
		
		if (!hasDict) {
			startDictFileSelection();


//			Intent theIntent = new Intent();
//			theIntent.setClass(MainFormActivity.this, SelectDict.class);
//			// startActivityForResult(new Intent(thi, SelectDict.class), 1);
//			// Intent theIntent = new Intent(Intent.ACTION_PICK);
//			// theIntent.setData(Uri.fromFile(new File("/mnt/sdcard/")));
//			// //default
//			// file / jump directly to this file/folder
//			// theIntent.putExtra(Intent.EXTRA_TITLE,"A Custom Title");
//			// //optional
//			// theIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//			// //optional
//			try {
//				startActivityForResult(theIntent, REQUEST_CODE_SELECTDICT);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
		}

	}

	private void startDictFileSelection() {
		Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("*/*");

//		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//		intent.addCategory(Intent.CATEGORY_OPENABLE);
//		intent.setType("file/*");
//		intent.putExtra(Intent.EXTRA_MIME_TYPES, Collections.singletonList("application/vcb").toArray());

		// Optionally, specify a URI for the file that should appear in the
		// system file picker when it loads.
//			intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);
		try {
			startActivityForResult(intent, REQUEST_CODE_SELECTDICT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void startDictFileSelectionForSamsung() {
		Intent intent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
		intent.putExtra("CONTENT_TYPE","*/*");
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		try {
			startActivityForResult(intent, REQUEST_CODE_SELECTDICT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void loadDict(Uri uri) {

		StringBuilder stringBuilder = new StringBuilder();
		try (InputStream inputStream =
					 getContentResolver().openInputStream(uri);
			 BufferedReader reader = new BufferedReader(
					 new InputStreamReader(Objects.requireNonNull(inputStream),"UTF8"))) {
			String line;
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
			}


//		FileInputStream iFile;
//			iFile = new FileInputStream(new File(vocab));
//			String strLine = null;

//			InputStreamReader tmp = new InputStreamReader(iFile, "UTF8");
//			BufferedReader dataIO = new BufferedReader(tmp);
//			StringBuffer sBuffer = new StringBuffer();
//			while ((strLine = dataIO.readLine()) != null) {
//				sBuffer.append(strLine);
//			}
//			dataIO.close();
//			iFile.close();
			int pos;
			int pos1 = 0;
			int pos2 = 0;
			int pos21 = 0;
			int i = 0;
			k = 0;
			String s = stringBuilder.toString();
			pos = -1;
			String s1 = "";
			while (s.indexOf("%", pos) > 0) {
				pos1 = pos;
				pos = s.indexOf("%", pos1 + 1);
				s1 = s.substring(pos1 + 1, pos + 1);
				pos2 = s1.indexOf("|", 0);
				slovnik[0][i] = s1.substring(0, pos2);
				pos21 = s1.indexOf("|", pos2 + 1);
				slovnik[1][i] = s1.substring(pos2 + 2, pos21 - 1);
				pos2 = s1.indexOf("%", pos21 + 1);
				slovnik[2][i] = s1.substring(pos21 + 1, pos2);
				i++;
				rozmir_mas = i;
			}
		} catch (Exception e) {
			e.printStackTrace();
			// text.setText("Error loading file: " + e.getMessage());
		}

		// ____________________
		// poch_nom = 0;
		SharedPreferences settings = getSharedPreferences(APP_PREFERENCES,
				MODE_PRIVATE);
		if (settings.contains(DICTIONARIES) == true) {
			String s = settings.getString(DICTIONARIES, "");
			if (s.length() > 0) {
				dict = new Dict(s.substring(s.indexOf("<dict>") + 6,
						s.indexOf("</dict>")));
				startFromNumber = dict.getBeginFrom();
			}
		}
		String[] segments = uri.getLastPathSegment().split("/");
		listOfDicts.add(new Dict(uri.getPath(), segments[segments.length-1]));
		saveChangedDictsList();
		// kilk_povt = 5;
		krok_povtory = kilk_povt;
		k_zal_sliv = 10;
		vprava = 1;
		for (int i1 = 0; i1 <= 9; i1++) {
			kilk[i1] = 0;
		}
		Functions();
		speaker.speakText(slovnik[0][k_zapis]);
		// spisok2=new String[10];
		// for (int i=0;i<10;i++){ spisok2[i]=slovnik[i][2]; }

		// setListAdapter(new ArrayAdapter<String>(
		// this,android.R.layout.simple_list_item_1,spisok2));
	}

	@Override
	public void onDestroy() {
		speaker.destroy();
		super.onDestroy();
	}

	// *************************
	public void Functions() {
		Context context = getApplicationContext();
		Toast toast = Toast.makeText(context, getResources().getString(R.string.words) 
				+ slovnik[0][startFromNumber] + "-"
				+ slovnik[0][startFromNumber + 9] + " ("
				+ (startFromNumber + 1) + "-" + (startFromNumber + 10) + ")",
				Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
		// statusBar.setText("Переклад з англійської на українську: слова "+slovnik[poch_nom][0]+"-"+slovnik[poch_nom
		// +9][0]+" ("+poch_nom+","+(poch_nom+9)+")");
		k_zapis = startFromNumber + rnd.nextInt(100) % 10;
		Vword.setText(slovnik[0][k_zapis]);
		if (slovnik[1][k_zapis].length() > 0) {
			Vtransc.setText("[" + slovnik[1][k_zapis] + "]");
		} else {
			Vtransc.setText(" ");
		}
		spisok2 = new String[10];
		for (int i = 0; i < 10; i++) {
			spisok2[i] = slovnik[2][startFromNumber + i];
		}
		// listView.setAdapter(new ArrayAdapter<String>(this,
		// android.R.layout.simple_list_item_1,spisok2));
		// listView.setAdapter(new ArrayAdapter<String>(this,
		// R.layout.list_item, spisok2));
		listSetAdapter();
	}

	public void Functions2() {
		do {
			k_zapis = startFromNumber + rnd.nextInt(100) % 10;
		} while (kilk[k_zapis - startFromNumber] >= 1);
		Vword.setText(slovnik[0][k_zapis]);
		speaker.speakText(slovnik[0][k_zapis]);
		if (slovnik[1][k_zapis].length() > 0) {
			Vtransc.setText("[" + slovnik[1][k_zapis] + "]");
		} else {
			Vtransc.setText(" ");
		}
	}

	public void Functions2_1() {
		// statusBar.setText("Переклад з української на англійську: слова "+slovnik[poch_nom][0]+"-"+slovnik[poch_nom
		// +9][0]+" ("+poch_nom+","+(poch_nom+9)+")");
		k_zapis = startFromNumber + rnd.nextInt(100) % 10;
		Vword.setText(slovnik[2][k_zapis]);
		Vtransc.setText(" ");
		spisok2 = new String[10];
		for (int i = 0; i < 10; i++) {
			spisok2[i] = slovnik[0][startFromNumber + i];
		}
		// listView.setAdapter(new ArrayAdapter<String>(this,
		// android.R.layout.simple_list_item_1,
		// spisok2));
		// listView.setAdapter(new ArrayAdapter<String>(this,
		// R.layout.list_item,
		// spisok2));
		listSetAdapter();
	}

	public void Functions2_2() {
		do {
			k_zapis = startFromNumber + rnd.nextInt(100) % 10;
		} while (kilk[k_zapis - startFromNumber] >= 1);
		Vword.setText(slovnik[2][k_zapis]);
		Vtransc.setText(" ");
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_CODE_FORM3_ACTIVITY:
			if (resultCode == RESULT_OK) {
				Bundle extras = data.getExtras();
				int result = Integer.parseInt(extras.getString(EXT_RESULT));

				switch (result) {
				case 0:
					Intent intent = new Intent();
					setResult(RESULT_OK, intent);
					finish();
					break;
				case 1:
					vprava = 1;
					krok_povtory = kilk_povt;
					for (int i = 0; i <= 9; i++) {
						kilk[i] = 0;
					}
					if (startFromNumber == rozmir_mas - 10) {
						startFromNumber = 0;
						// jButton13.setEnabled(false);
						// jButton14.setEnabled(true);
						Context context = getApplicationContext();
						Toast toast = Toast
								.makeText(
										context,
										getResources().getString(R.string.end_of_dict),
										Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					} else {
						if (startFromNumber + 19 <= rozmir_mas) {
							startFromNumber += 10;
						} else {
							startFromNumber = rozmir_mas - 10;
						}
					}
					getListFormSettings();
					listOfDicts.get(0).setBeginFrom(startFromNumber);
					saveChangedDictsList();

					Functions();
					speaker.speakText(slovnik[0][k_zapis]);
					break;
				case 2:
					vprava = 2;
					krok_povtory = kilk_povt;
					k_zal_sliv = 10;
					for (int i = 0; i <= 9; i++) {
						kilk[i] = 0;
					}
					// kilk[10]=1;
					// if (poch_nom==rozmir_mas-10)
					// {jButton14.setEnabled(true);}
					Functions2_1();
					break;
				case 3:
					finish();
					break;
				}
			}
			break;
		case REQUEST_CODE_OPTION_ACTIVITY:
		//	if (resultCode == RESULT_OK) {
				SharedPreferences settings = getSharedPreferences(
						APP_PREFERENCES, MODE_PRIVATE);
				kilk_povt = settings.getInt(KILK_POVT, 5);

			speaker = new WordSpeaker(this, settings);

				// Bundle extras = data.getExtras();
				// startFromNumber =
				// Integer.parseInt(extras.getString(EXT_POCH_NOM));
				// kilk_povt =
				// Integer.parseInt(extras.getString(EXT_KILK_POVT));

				// if (startFromNumber + 10 > rozmir_mas) {
				// startFromNumber = rozmir_mas - 10;
				// }

				// getListFormSettings();
				// listOfDicts.get(0).setBeginFrom(startFromNumber);
				// saveChangedDictsList();

				krok_povtory = kilk_povt;
				k_zal_sliv = 10;
				vprava = 1;
				for (int i = 0; i <= 9; i++) {
					kilk[i] = 0;
				}
				Functions();
			speaker.speakText(slovnik[0][k_zapis]);
			//}
			break;
		case REQUEST_CODE_SELECTDICT:
			if (resultCode == RESULT_OK) {
				vocab = data.getDataString();
				loadDict(data.getData());
			}
			break;

		}
	}

		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_exit:
			exit();
			return true;
		case R.id.menu_help:
			help();
			return true;
		case R.id.menu_last_opend:
			lastOpened();
			return true;
		case R.id.menu_next_step:
			nextStep();
			return true;
		case R.id.menu_prev_step:
			prevStep();
			return true;
		case R.id.menu_open:
			startDictFileSelection();
			return true;
		case R.id.menu_settings:
			settings();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void settings() {
		Intent intent = new Intent();
		intent.setClass(MainFormActivity.this, OptionActivity.class);
		intent.putExtra(OptionActivity.EXT_POCH_NOM, "" + startFromNumber);
		intent.putExtra(OptionActivity.EXT_KILK_POVT, "" + kilk_povt);
		startActivityForResult(intent, REQUEST_CODE_OPTION_ACTIVITY);

		System.out.println("setting closed");
		SharedPreferences settings = getSharedPreferences(APP_PREFERENCES,
				MODE_PRIVATE);
		String language = settings.getString(LANGUAGE,Locale.US.getLanguage());
		speaker.updateLanguageSelection(language);
	}

	private void prevStep() {
		switch (vprava) {
		// case 3: {
		// }
		// break;
		case 1: {
			vprava = 3;
			krok_povtory = kilk_povt;
			k_zal_sliv = 10;
			for (int i = 0; i <= 9; i++) {
				kilk[i] = 0;
			}
			// kilk[10]=1;

			// statusBar.setText("Написання англійських слів: слова "+slovnik[poch_nom][0]+"-"+slovnik[poch_nom
			// +9][0]+" ("+poch_nom+","+(poch_nom+9)+")");
			if (startFromNumber - 10 >= 0) {
				startFromNumber -= 10;
			} else {
				startFromNumber = 0;
			}

			getListFormSettings();
			listOfDicts.get(0).setBeginFrom(startFromNumber);
			saveChangedDictsList();

			// new Functions3_1();
			Intent intent = new Intent();
			intent.setClass(MainFormActivity.this, WritingWordsActivity.class);
			String[] vocab = new String[10];
			for (int i = 0; i < 10; i++) {
				vocab[i] = slovnik[0][i + startFromNumber];
			}
			intent.putExtra(WritingWordsActivity.EXT_SLOVNIK1, vocab);
			String[] vocab2 = new String[10];
			for (int i = 0; i < 10; i++) {
				vocab2[i] = slovnik[2][i + startFromNumber];
			}
			intent.putExtra(WritingWordsActivity.EXT_SLOVNIK3, vocab2);
			intent.putExtra(WritingWordsActivity.EXT_KROK_POVTORY, krok_povtory);
			intent.putExtra(WritingWordsActivity.EXT_ROZM_MAS, rozmir_mas);
			startActivityForResult(intent, REQUEST_CODE_FORM3_ACTIVITY);
		}
			break;
		case 2: {
			vprava = 1;
			krok_povtory = kilk_povt;
			k_zal_sliv = 10;
			for (int i = 0; i <= 9; i++) {
				kilk[i] = 0;
			}
			// kilk[10]=1;
			// if (poch_nom == 0) {// jButton13.setEnabled(false);
			// }
			Functions();
			// -----відтворення звуку-----
			// if (jCheckBox1.isSelected()){
			speaker.speakText(slovnik[0][k_zapis]);
			// }
			// -------кінець відтворення звуку------
		}
			break;
		default: // For any other number...
			break;
		}
	}

	private void nextStep() {
		switch (vprava) {
		case 1: {
			vprava = 2;
			krok_povtory = kilk_povt;
			k_zal_sliv = 10;
			for (int i = 0; i <= 9; i++) {
				kilk[i] = 0;
			}
			// if (poch_nom==0){jButton13.setEnabled(true);}
			Functions2_1();
		}
			break;
		case 2: {
			vprava = 3;
			krok_povtory = kilk_povt;
			k_zal_sliv = 10;
			for (int i = 0; i <= 9; i++) {
				kilk[i] = 0;
			}
			// if (poch_nom==rozmir_mas-10) {jButton14.setEnabled(false);}
			// statusBar.setText("Написання англійських слів: слова "+slovnik[poch_nom][0]+"-"+slovnik[poch_nom
			// +9][0]+" ("+poch_nom+","+(poch_nom+9)+")");
			// new Functions3_1();
			Intent intent = new Intent();
			intent.setClass(MainFormActivity.this, WritingWordsActivity.class);
			String[] vocab = new String[10];
			for (int i = 0; i < 10; i++) {
				vocab[i] = slovnik[0][i + startFromNumber];
			}
			intent.putExtra(WritingWordsActivity.EXT_SLOVNIK1, vocab);
			String[] vocab2 = new String[10];
			for (int i = 0; i < 10; i++) {
				vocab2[i] = slovnik[2][i + startFromNumber];
			}
			intent.putExtra(WritingWordsActivity.EXT_SLOVNIK3, vocab2);
			intent.putExtra(WritingWordsActivity.EXT_KROK_POVTORY, krok_povtory);
			intent.putExtra(WritingWordsActivity.EXT_ROZM_MAS, rozmir_mas);
			startActivityForResult(intent, REQUEST_CODE_FORM3_ACTIVITY);
		}
			break;
		default: // For any other number...
			break;
		}
	}

	private void lastOpened() {
		Intent theIntent = new Intent();
		theIntent.setClass(MainFormActivity.this, LastOpendActivity.class);
		try {
			startActivityForResult(theIntent,
					REQUEST_CODE_LAST_OPEND_ACTIVITY);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void help() {
		Intent intent = new Intent();
		intent.setClass(MainFormActivity.this, HelpActivity.class);
		startActivity(intent);
	}

	private void exit() {
		Intent intent = new Intent();
		// intent.putExtra(LingvistActivity.EXT_EX, "1");
		setResult(RESULT_OK, intent);
		finish();
	}

	public void getListFormSettings() {
		listOfDicts = new ArrayList<Dict>();
		SharedPreferences settings = getSharedPreferences(APP_PREFERENCES,
				MODE_PRIVATE);
	//	Log.i("DEBUG_LastOpend", "getted settings");
		if (settings.contains(DICTIONARIES) == true) {
			String s = settings.getString(DICTIONARIES, "");
			StringBuffer sb = new StringBuffer(s);
			while (sb.length() > 0) {
				Dict dict = new Dict(sb.substring(sb.indexOf("<dict>"),
						sb.indexOf("</dict>") + 7));
				listOfDicts.add(dict);
				sb.delete(0, sb.indexOf("</dict>") + 7);
			}
		}
	}

	public void saveChangedDictsList() {
		SharedPreferences settings = getSharedPreferences(APP_PREFERENCES,
				MODE_PRIVATE);
		StringBuffer dictionaries = new StringBuffer();
		for (int i = 0; i < listOfDicts.size(); i++) {
			dictionaries.append(listOfDicts.get(i).toString());
		}

		SharedPreferences.Editor prefEditor = settings.edit();
		prefEditor.putString(DICTIONARIES, dictionaries.toString());
	//	Log.i("DEBUG_LastOpend", "saved dictionary: " + dictionaries.toString());
		prefEditor.commit();
	}

	public void listSetAdapter() {
		ArrayAdapter<String> adapt = new ArrayAdapter<String>(this,
				R.layout.list_item, spisok2) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View row;

				if (null == convertView) {
					LayoutInflater mInflater = getLayoutInflater();
					row = mInflater.inflate(R.layout.list_item, null);
				} else {
					row = convertView;
				}

				TextView tv = (TextView) row.findViewById(R.id.textView1);
				if (textSize == 0) {
					textSize = tv.getTextSize();
				}
				tv.setTextSize(textSize);
				if (textPadding == 0) {
					textPadding = tv.getPaddingBottom();
				}
				tv.setPadding(0, textPadding, 0, textPadding);
				tv.setText(Html.fromHtml(getItem(position)));
				tv.setGravity(Gravity.CENTER);
				// tv.setText(getItem(position));

				return row;
			}
		};
		listView.setAdapter(adapt);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		AdapterContextMenuInfo aMenuInfo = (AdapterContextMenuInfo) menuInfo;
		// final int position = aMenuInfo.position;
		// final AdapterData data = adapter.getItem(aMenuInfo.position);

		menu.setHeaderTitle(R.string.parameters);
		menu.add(R.string.smaller_size).setOnMenuItemClickListener(
				new MenuItem.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						textSize -= 2;
						SharedPreferences settings = getSharedPreferences(
								APP_PREFERENCES, MODE_PRIVATE);
						SharedPreferences.Editor prefEditor = settings.edit();
						prefEditor.putFloat(TEXT_SIZE, textSize);
						prefEditor.commit();
						listSetAdapter();
						return true;
					}
				});
		menu.add(R.string.bigger_size).setOnMenuItemClickListener(
				new MenuItem.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						textSize += 2;
						SharedPreferences settings = getSharedPreferences(
								APP_PREFERENCES, MODE_PRIVATE);
						SharedPreferences.Editor prefEditor = settings.edit();
						prefEditor.putFloat(TEXT_SIZE, textSize);
						prefEditor.commit();
						listSetAdapter();
						return true;
					}
				});
		menu.add(R.string.smaller_distance).setOnMenuItemClickListener(
				new MenuItem.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						textPadding -= 1;
						SharedPreferences settings = getSharedPreferences(
								APP_PREFERENCES, MODE_PRIVATE);
						SharedPreferences.Editor prefEditor = settings.edit();
						prefEditor.putInt(TEXT_PADDING, textPadding);
						prefEditor.commit();
						listSetAdapter();
						return true;
					}
				});
		menu.add(R.string.bigger_distance).setOnMenuItemClickListener(
				new MenuItem.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						textPadding += 1;
						SharedPreferences settings = getSharedPreferences(
								APP_PREFERENCES, MODE_PRIVATE);
						SharedPreferences.Editor prefEditor = settings.edit();
						prefEditor.putInt(TEXT_PADDING, textPadding);
						prefEditor.commit();
						listSetAdapter();
						return true;
					}
				});

		menu.add(R.string.start_from).setOnMenuItemClickListener(
				new MenuItem.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						showDialog(IDD_SET_START_NUMBER);
						return true;
					}
				});
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case IDD_SET_START_NUMBER:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			// builder.setTitle("Редагування словника");
			builder.setMessage(R.string.start_from_number);
			final EditText inputStartNumber = new EditText(this);
			inputStartNumber.setText("" + (startFromNumber + 1));
			inputStartNumber.setFocusable(true);
			inputStartNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
			inputStartNumber.setSelection(inputStartNumber.getText().toString()
					.length());
			// inputStartNumber.requestFocusFromTouch();
			builder.setView(inputStartNumber);
			// final InputMethodManager inputMethodManager =
			// (InputMethodManager)
			// getSystemService(Context.INPUT_METHOD_SERVICE);
			// if (inputMethodManager != null) {
			// inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,
			// 0);
			// }

			// inputStartNumber.requestFocus();
			builder.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							String newStartNumber = inputStartNumber.getText()
									.toString();
							// Log.i("DEBUG_lAST","Name1="+newStartNumber);
							// Log.i("DEBUG_lAST","Name2="+newStartNumber);
							if (newStartNumber.length() > 0) {
								// Log.i("DEBUG_lAST","Name3="+newStartNumber);
								int oldStartFromNumber = startFromNumber;
								try {
									startFromNumber = Integer
											.parseInt(inputStartNumber
													.getText().toString()) - 1;
									if (startFromNumber < 0) {
										throw new Exception();
									}
								//	Log.i("DEBUG_lAST", "startFromNumber="
								//			+ startFromNumber);

									if (startFromNumber + 10 > rozmir_mas) {
										startFromNumber = rozmir_mas - 10;
									}

									getListFormSettings();
									listOfDicts.get(0).setBeginFrom(
											startFromNumber);
									saveChangedDictsList();

									krok_povtory = kilk_povt;
									k_zal_sliv = 10;
									vprava = 1;
									for (int i = 0; i <= 9; i++) {
										kilk[i] = 0;
									}
									Functions();
									speaker.speakText(slovnik[0][k_zapis]);
								} catch (Exception e) {
									startFromNumber = oldStartFromNumber;
									Toast toast = Toast
											.makeText(
													MainFormActivity.this,
													getResources().getString(R.string.wrong_number),
													Toast.LENGTH_SHORT);
									toast.setGravity(Gravity.CENTER, 0, 0);
									toast.show();
								}
							}
							// inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,
							// 0);
							// saveChangedDictsList();
							dialog.dismiss();
							MainFormActivity.this.removeDialog(IDD_SET_START_NUMBER);
							// return;
						}
					});
			builder.setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			builder.setCancelable(false);
			return builder.create();

		default:
			return null;
		}
	}
}
