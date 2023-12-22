package com.boden.lingvolearner;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.boden.lingvolearner.pojo.WordCard;
import com.boden.lingvolearner.services.ContextHolder;
import com.boden.lingvolearner.sqlite.DBManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.boden.lingvolearner.services.ContextHolder.getLearningManager;

public class SelectDbDictionaryActivity extends Activity {
	private static final String ID_SEP = "=id=";
	protected static final String CONTENT = "content";
	private ListView listView;
	private ToggleButton categoryOrDictionary;
	private DBManager dbManager;
	private List<String> itemsList;
	private CheckBox shuffleCheckBox;
	// public static final String ID = "id";
	// public static final String NAME = "name";
	// final String[] from = new String[] { ID,
	// NAME };
	//
	// final int[] to = new int[] { R.id.title, R.id.title };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_from_db);
		listView = (ListView) findViewById(R.id.list);

		shuffleCheckBox = findViewById(R.id.randomizeCheckBox);
		shuffleCheckBox.setChecked(ContextHolder.getSettingsHolder().isShuffleWords());

		try {
			dbManager = new DBManager(this);
			dbManager.open();
			Cursor cursor = dbManager.fetchCategories();
			String defaultTitle = "Без категорії";
			ContextHolder.getInstance().setCategories(fetchList(defaultTitle, cursor));
			cursor = dbManager.fetchDictionaries();
			defaultTitle = "Без словника";
			ContextHolder.getInstance().setDictionaries(fetchList(defaultTitle, cursor));

			categoryOrDictionary = findViewById(R.id.categoryOrDictionary);
			categoryOrDictionary
					.setTextOff(categoryOrDictionary.getContext().getResources().getText(R.string.dictionary));
			categoryOrDictionary.setTextOn(categoryOrDictionary.getContext().getResources().getText(R.string.category));
			categoryOrDictionary.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					ContextHolder.getSettingsHolder().setCategoriesDisplaySelected(isChecked);
					updateListAdaptor(isChecked);
				}

			});
			categoryOrDictionary.setChecked(ContextHolder.getSettingsHolder().isCategoriesDisplaySelected());
			updateListAdaptor(ContextHolder.getSettingsHolder().isCategoriesDisplaySelected());

			// String[] items = fetchList("Без категорії",
			// dbManager.fetchCategories());
			// listView.setAdapter(new ArrayAdapter<String>(this,
			// R.layout.list_item, items));

			// SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
			// R.layout.list_item, cursor, from, to, 0);
			// adapter.notifyDataSetChanged();

			// listView.setAdapter(adapter);

			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View itemClicked, int position, long id) {

					List<WordCard> allWordCards = loadFromDB(position);

					Bundle extras = getIntent().getExtras();
					boolean returnName = extras != null ? extras.getBoolean(Constants.NAME_AND_TYPE_ONLY) : false;
					System.out.println("return name condition: "+returnName);
					if (returnName) {
						Intent returnIntent = new Intent();
						returnIntent.putExtra("isCategory", categoryOrDictionary.isChecked());
						String selected = categoryOrDictionary.isChecked()
								? ContextHolder.getInstance().getCategories()[position]
								: ContextHolder.getInstance().getDictionaries()[position];
						String[] selection = selected.split(ID_SEP);
						returnIntent.putExtra("name", selection[0]);
						returnIntent.putExtra("id", selection[1]);
						ContextHolder.getInstance().getLoadedWordCards().add(allWordCards);
						setResult(Activity.RESULT_OK, returnIntent);
						System.out.println("returning");
						finish();
					} else {
						boolean isLoaded = allWordCards.size() > 9;
						if (!isLoaded) {
							Toast toast = Toast.makeText(getApplicationContext(),
									getResources().getString(R.string.noWordsFound), Toast.LENGTH_SHORT);
							toast.show();
						} else {
							ContextHolder.getInstance().createLearningManager(allWordCards);
							ContextHolder.getSettingsHolder().setStartFromNumber(0);
							Intent returnIntent = new Intent();
							setResult(Activity.RESULT_OK, returnIntent);
							finish();
						}
					}
				}

				private List<WordCard> loadFromDB(int position) {
					Cursor cursor;
					if (categoryOrDictionary.isChecked()) {
						if (position == 0) {
							cursor = dbManager.fetchEntitiesWithoutCategory();
						} else {
							String selected = ContextHolder.getInstance().getCategories()[position];
							String selectionId = selected.split(ID_SEP)[1];
							cursor = dbManager.fetchEntitiesByCategory(selectionId);
						}
					} else {
						if (position == 0) {
							cursor = dbManager.fetchEntitiesWithoutDictionary();
						} else {
							String selected = ContextHolder.getInstance().getDictionaries()[position];
							String selectionId = selected.split(ID_SEP)[1];
							cursor = dbManager.fetchEntitiesByDictionary(selectionId);
						}
					}
					boolean shuffleSelected = shuffleCheckBox.isChecked();
					ContextHolder.getSettingsHolder().setShuffleWords(shuffleSelected);
					List<WordCard> allWordCards = loadWordsTranslation(cursor, shuffleSelected);
					if (shuffleSelected) {
						Collections.shuffle(allWordCards);
					}
					return allWordCards;
				}
			});
		} catch (SQLiteException e) {
			e.printStackTrace();
			Intent returnIntent = new Intent();
			setResult(2, returnIntent);
			finish();
		}
	}

	private void updateListAdaptor(boolean isChecked) {
		if (isChecked) {
			String[] categories = removeIds(ContextHolder.getInstance().getCategories());
			listView.setAdapter(
					new ArrayAdapter<String>(SelectDbDictionaryActivity.this, R.layout.list_item, categories));
		} else {
			String[] dictionaries = removeIds(ContextHolder.getInstance().getDictionaries());
			listView.setAdapter(
					new ArrayAdapter<String>(SelectDbDictionaryActivity.this, R.layout.list_item, dictionaries));
		}
	}

	private String[] removeIds(String[] items) {
		String[] cleared = new String[items.length];
		for (int i = 0; i < items.length; i++) {
			cleared[i] = items[i].split(ID_SEP)[0];
		}
		return cleared;
	}

	private List<WordCard> loadWordsTranslation(Cursor cursor, boolean shuffleSelected) {
		List<WordCard> allWordCards = new ArrayList<>();
		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				String notesDB = cursor.getString(3);
				String notes = notesDB != null && notesDB.trim().length() > 0 ? "\n(" + notesDB + ")" : "";
				allWordCards.add(new WordCard(cursor.getString(0), cursor.getString(1), cursor.getString(2) + notes));
			}
		}
		return allWordCards;
	}

	private String[] fetchList(String firstElement, Cursor cursor) {
		String[] items = new String[] {};
		if (cursor != null && cursor.getCount() > 0) {
			// id = new int[cursor.getCount()];
			// fname = new String[cursor.getCount()];
			// lname = new String[cursor.getCount()];
			int i = 0;
			List<String> itemsList = new ArrayList<>();

			while (cursor.moveToNext()) {
				itemsList.add(cursor.getString(1) + ID_SEP + cursor.getInt(0));
				// id[i] = cursor.getInt(0);
				// fname[i] = cursor.getString(1);
				// lname[i] = cursor.getString(2);
				i++;
			}
			Collections.sort(itemsList);
			itemsList.add(0, firstElement + "=id=-1");
			items = itemsList.toArray(items);
			// CustAdapter custAdapter = new CustAdapter();
			// lv.setAdapter(custAdapter);
		}
		return items;
	}

	@Override
	public void finish() {
		dbManager.close();
		super.finish();
	}
}
