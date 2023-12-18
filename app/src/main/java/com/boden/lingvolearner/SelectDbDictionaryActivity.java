package com.boden.lingvolearner;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
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

		dbManager = new DBManager(this);
		dbManager.open();
		Cursor cursor= dbManager.fetchCategories();
		String defaultTitle= "Без категорії";
		ContextHolder.getInstance().setCategories(fetchList(defaultTitle, cursor));
		cursor = dbManager.fetchDictionaries();
		defaultTitle = "Без словника";
		ContextHolder.getInstance().setDictionaries(fetchList(defaultTitle, cursor));

		categoryOrDictionary = findViewById(R.id.categoryOrDictionary);
		categoryOrDictionary.setTextOff(categoryOrDictionary.getContext().getResources().getText(R.string.dictionary));
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

//		String[] items = fetchList("Без категорії", dbManager.fetchCategories());
//		listView.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item, items));

		// SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
		// R.layout.list_item, cursor, from, to, 0);
		// adapter.notifyDataSetChanged();

		// listView.setAdapter(adapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View itemClicked, int position, long id) {
				Cursor cursor;
				if (categoryOrDictionary.isChecked()) {
					if (position == 0) {
						cursor = dbManager.fetchEntitiesWithoutCategory();
					} else {
						String selected = ContextHolder.getInstance().getCategories()[position];
						String selectionId = selected.split("=id=")[1];
						cursor = dbManager.fetchEntitiesByCategory(selectionId);
					}
				} else {
					if (position == 0) {
						cursor = dbManager.fetchEntitiesWithoutDictionary();
					} else {
						String selected = ContextHolder.getInstance().getDictionaries()[position];
						String selectionId = selected.split("=id=")[1];
						cursor = dbManager.fetchEntitiesByDictionary(selectionId);
					}
				}
				boolean shuffleSelected = shuffleCheckBox.isChecked();
				ContextHolder.getSettingsHolder().setShuffleWords(shuffleSelected);
				boolean isLoaded = loadWordsTranslation(cursor, shuffleSelected);
				if (!isLoaded) {
					Toast toast = Toast.makeText(getApplicationContext(),
							getResources().getString(R.string.noWordsFound), Toast.LENGTH_SHORT);
					toast.show();
				} else {

					Intent returnIntent = new Intent();
					setResult(Activity.RESULT_OK, returnIntent);
					finish();
				}
			}
		});
	}
	private void updateListAdaptor(boolean isChecked) {
		if (isChecked) {
			listView.setAdapter(new ArrayAdapter<String>(SelectDbDictionaryActivity.this, R.layout.list_item,
					ContextHolder.getInstance().getCategories()));					
		} else {
			listView.setAdapter(new ArrayAdapter<String>(SelectDbDictionaryActivity.this, R.layout.list_item,
					ContextHolder.getInstance().getDictionaries()));
		}
	}
	private boolean loadWordsTranslation(Cursor cursor, boolean shuffleSelected) {
		List<WordCard> allWordCards = new ArrayList<>();
		if (cursor.getCount() > 0) {
			int i = 0;
			while (cursor.moveToNext()) {
				allWordCards.add(new WordCard(cursor.getString(0), cursor.getString(1), cursor.getString(2)));
				i++;
			}
			if (allWordCards.size()>9) {
				if (shuffleSelected) {
					Collections.shuffle(allWordCards);
				}
				ContextHolder.getInstance().createLearningManager(allWordCards);
				ContextHolder.getSettingsHolder().setStartFromNumber(0);
				return true;
			}
		}
		return false;
	}

	private String[] fetchList(String firstElement, Cursor cursor) {
		String[] items = new String[] {};
		if (cursor.getCount() > 0) {
			// id = new int[cursor.getCount()];
			// fname = new String[cursor.getCount()];
			// lname = new String[cursor.getCount()];
			int i = 0;
			List<String> itemsList = new ArrayList<>();

			while (cursor.moveToNext()) {
				itemsList.add(cursor.getString(1) + "=id=" + cursor.getInt(0));
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
