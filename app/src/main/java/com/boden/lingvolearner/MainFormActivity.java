package com.boden.lingvolearner;

import static com.boden.lingvolearner.services.ContextHolder.getLearningManager;

import java.util.List;

import com.boden.lingvolearner.pojo.WordCard;
import com.boden.lingvolearner.services.ContextHolder;
import com.boden.lingvolearner.services.Stage;
import com.boden.lingvolearner.services.UiUpdator;

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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@TargetApi(30)
public class MainFormActivity extends GeneralMainActivity implements UiUpdator {
	private TextView Vword, Vtransc;
	private ListView listView;
	private Menu menu;

	private List<WordCard> allWordCards;

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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_form);
		setTitle(R.string.app_name2);

		Vword = (TextView) findViewById(R.id.word);
		Vword.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				getLearningManager().playOnClick();
			}
		});
		Vtransc = (TextView) findViewById(R.id.transcription);
		listView = (ListView) findViewById(R.id.list);
		registerForContextMenu(listView);

		ContextHolder.getInstance().createSettingsHolder(getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE));
		ContextHolder.registerUiUpdator(Stage.FOREIGN_TO_NATIVE, this);
		ContextHolder.registerUiUpdator(Stage.NATIVE_TO_FOREIGN, this);
		WordPlayerTTS player = new WordPlayerTTS(this);
		ContextHolder.setWordPlayer(player);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View itemClicked, int position, long id) {
				boolean isCorrect = getLearningManager().checkAnswer(getLearningManager().getWordChoices()[position]);
				if (!isCorrect) {
					Context context = MainFormActivity.this.getApplicationContext();
					Toast toast = Toast.makeText(context,
							getLearningManager().getWordToDisplay() + " - " + getLearningManager().getWordAnswer(),
							Toast.LENGTH_SHORT);
					toast.show();
				}

			}
		});

		// Log.i("DEBUG_INFO_MY", "now loaded settings");

		startDictFileSelection();

	}

	private void startDictFileSelection() {
		Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("*/*");
		try {
			startActivityForResult(intent, REQUEST_CODE_SELECTDICT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void startDictFileSelectionForSamsung() {
		Intent intent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
		intent.putExtra("CONTENT_TYPE", "*/*");
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		try {
			startActivityForResult(intent, REQUEST_CODE_SELECTDICT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		ContextHolder.getWordSpeaker().destroy();
		super.onDestroy();
	}

	private int getStartFromNumber() {
		return ContextHolder.getSettingsHolder().getStartFromNumber();
	}

	public void chooseAndFillNativeWord() {
		Vword.setText(getLearningManager().getWordToDisplay());
		Vtransc.setText(getLearningManager().getWordTranscription());
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

					boolean endReached = getLearningManager().startNextStage();
					if (endReached) {
						Context context = getApplicationContext();
						Toast toast = Toast.makeText(context, getResources().getString(R.string.end_of_dict),
								Toast.LENGTH_SHORT);
						toast.show();
					}
					break;
				case 2:
					getLearningManager().startPreviousStage();
					break;
				case 3:
					finish();
					break;
				}
			}
			break;
		case REQUEST_CODE_OPTION_ACTIVITY:
			getLearningManager().startLearning();
			break;
		case REQUEST_CODE_SELECTDICT:
			if (resultCode == RESULT_OK) {
				loadDictionary(data.getData());
			}
			break;

		}
	}
	private boolean loadDictionary(Uri uri) {
		System.out.println("uri="+uri);
		try {
//			getContentResolver().takePersistableUriPermission(uri, 0);
			allWordCards = DictionaryFileManipulator.loadDictionaryByLines(uri, getContentResolver());
			ContextHolder.getSettingsHolder().updateLastDictionary(uri);
			ContextHolder.getInstance().createLearningManager(allWordCards);
			getLearningManager().startLearning();
		} catch (Exception e) {
			e.printStackTrace();
			Toast toast = Toast.makeText(getApplicationContext(), "Не вдалось відкрити словник!", Toast.LENGTH_SHORT);
			toast.show();
			return false;
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		this.menu = menu;
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
			getLearningManager().startNextStage();
			return true;
		case R.id.menu_prev_step:
			getLearningManager().startPreviousStage();
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
		startActivityForResult(intent, REQUEST_CODE_OPTION_ACTIVITY);

		System.out.println("setting closed");// ???????????
	}

	private void lastOpened() {
		Intent theIntent = new Intent();
		theIntent.setClass(MainFormActivity.this, LastOpendActivity.class);
		try {
			startActivityForResult(theIntent, REQUEST_CODE_LAST_OPEND_ACTIVITY);
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
		setResult(RESULT_OK, intent);
		finish();
	}

	public void listSetAdapter() {
		ArrayAdapter<String> adapt = new ArrayAdapter<String>(this, R.layout.list_item,
				getLearningManager().getWordChoices()) {

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

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.setHeaderTitle(R.string.parameters);
		menu.add(R.string.smaller_size).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				ContextHolder.getSettingsHolder().decreaseTextSize();
				listSetAdapter();
				return true;
			}
		});
		menu.add(R.string.bigger_size).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				ContextHolder.getSettingsHolder().increaseTextSize();
				listSetAdapter();
				return true;
			}
		});
		menu.add(R.string.smaller_distance).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				ContextHolder.getSettingsHolder().decreaseTextPadding();
				listSetAdapter();
				return true;
			}
		});
		menu.add(R.string.bigger_distance).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				ContextHolder.getSettingsHolder().increaseTextPadding();
				listSetAdapter();
				return true;
			}
		});

		menu.add(R.string.start_from).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
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
			builder.setMessage(R.string.start_from_number);
			final EditText inputStartNumber = new EditText(this);
			inputStartNumber.setText("" + (getStartFromNumber() + 1));
			inputStartNumber.setFocusable(true);
			inputStartNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
			inputStartNumber.setSelection(inputStartNumber.getText().toString().length());
			builder.setView(inputStartNumber);

			builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					String newStartNumber = inputStartNumber.getText().toString();
					// Log.i("DEBUG_lAST","Name1="+newStartNumber);
					// Log.i("DEBUG_lAST","Name2="+newStartNumber);
					if (newStartNumber.length() > 0) {
						try {
							int startFromNumber = Integer.parseInt(inputStartNumber.getText().toString()) - 1;
							if (startFromNumber < 0) {
								throw new Exception();
							}
							// Log.i("DEBUG_lAST", "startFromNumber=" + startFromNumber);

							ContextHolder.getSettingsHolder().updateStartNumber(startFromNumber);
							getLearningManager().startLearning();

						} catch (Exception e) {
							Toast toast = Toast.makeText(MainFormActivity.this,
									getResources().getString(R.string.wrong_number), Toast.LENGTH_SHORT);
							toast.show();
						}
					}
					dialog.dismiss();
					MainFormActivity.this.removeDialog(IDD_SET_START_NUMBER);
				}
			});
			builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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

	@Override
	public void updateUiOnNewPortionStarted() {
		int startFromNumber = getStartFromNumber();
		Context context = getApplicationContext();
		Toast toast = Toast.makeText(context,
				getResources().getString(R.string.words) + allWordCards.get(startFromNumber).getWord() + "-"
						+ allWordCards.get(startFromNumber + 9).getWord() + " (" + (startFromNumber + 1) + "-"
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
		menu.getItem(0).setEnabled(getLearningManager().hasPreviousStep());
	}

	@Override
	public void createNewActivity() {
		Intent intent = new Intent();
		intent.setClass(MainFormActivity.this, WritingWordsActivity.class);
		startActivityForResult(intent, REQUEST_CODE_FORM3_ACTIVITY);
	}

	@Override
	public void updateOnStageEnd() {
		// TODO Auto-generated method stub
	}
}
