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

import android.app.Activity;
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
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

public class PlayerActivity extends GeneralMainActivity {

	private static final int REQUEST_CODE_SELECT_DB = 4;
	private List<String> names = new ArrayList<>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player);
		ListView listView = (ListView) findViewById(R.id.playlist);
		registerForContextMenu(listView);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View itemClicked, int position, long id) {
				if (names.isEmpty()) {
					Intent intent = new Intent();
					intent.setClass(PlayerActivity.this, SelectDbDictionaryActivity.class);
					// intent.putExtra(HelpActivity.CONTENT,
					// getDictViewContent());
					// startActivity(intent);
					intent.putExtra(Constants.NAME_AND_TYPE_ONLY, true);
					startActivityForResult(intent, REQUEST_CODE_SELECT_DB);
				}
			}
		});
		String[] items = new String[]{"select"};
		listView.setAdapter(new ArrayAdapter<String>(PlayerActivity.this, R.layout.list_item,
				items));		
	}

}
