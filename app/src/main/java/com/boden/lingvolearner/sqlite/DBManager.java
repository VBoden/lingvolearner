package com.boden.lingvolearner.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {

	private static final String READ_CATEGORIES = "SELECT id, name FROM words_category;";
	// SELECT id, name, language_from_id, language_to_id
	// FROM words_dictionary;
	private static final String TABLE_CATEGORIES = "words_category";

	private static final String TABLE_DICTIONARIES = "words_dictionary";
	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String DESC = "description";

	private DatabaseHelper dbHelper;

	private Context context;

	private SQLiteDatabase database;

	public DBManager(Context c) {
		context = c;
	}

	public DBManager open() throws SQLException {
		dbHelper = new DatabaseHelper(context);
		database = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		dbHelper.close();
	}

//	public void insert(String name, String desc) {
//		ContentValues contentValue = new ContentValues();
//		contentValue.put(DatabaseHelper.SUBJECT, name);
//		contentValue.put(DatabaseHelper.DESC, desc);
//		database.insert(DatabaseHelper.TABLE_NAME, null, contentValue);
//	}

	public Cursor fetchCategories() {
		String[] columns = new String[] { ID, NAME };
		Cursor cursor = database.query(TABLE_CATEGORIES, columns, null, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	public Cursor fetchDictionaries() {
		String[] columns = new String[] { ID, NAME };
		Cursor cursor = database.query(TABLE_DICTIONARIES, columns, null, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	public Cursor fetchEntitiesByCategory(String categoryId) {

		String query = "SELECT w.word,e.transcription,t.word FROM words_dictionaryentry as e join words_word as w on w.id=e.word_id join words_word as t on t.id=e.translation_id "
				+ "join words_word_category as wc on w.id=wc.word_id " + "where wc.category_id=?;";
		Cursor cursor = database.rawQuery(query, new String[] { categoryId });
		// String[] columns = new String[] { DatabaseHelper._ID,
		// DatabaseHelper.SUBJECT, DatabaseHelper.DESC };
		// Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns,
		// null, null, null, null, null);

		// Hibernate: select
		// d3_0.dictionaryentry_id,d3_1.id,l1_0.code,l1_0.name,l2_0.code,l2_0.name,d3_1.name
		// from words_dictionaryentry_dictionary d3_0 join words_dictionary d3_1
		// on d3_1.id=d3_0.dictionary_id left join words_language l1_0 on
		// l1_0.code=d3_1.language_from_id left join words_language l2_0 on
		// l2_0.code=d3_1.language_to_id where d3_0.dictionaryentry_id in(select
		// d1_0.id from words_dictionaryentry d1_0 left join
		// words_dictionaryentry_dictionary d2_0 on
		// d1_0.id=d2_0.dictionaryentry_id where d2_0.dictionary_id in (?))
		// Hibernate: select
		// d1_0.id,l1_0.code,l1_0.name,l2_0.code,l2_0.name,d1_0.name from
		// words_dictionary d1_0 left join words_language l1_0 on
		// l1_0.code=d1_0.language_from_id left join words_language l2_0 on
		// l2_0.code=d1_0.language_to_id where d1_0.id=?
		// Hibernate: select
		// d1_0.id,d1_0.transcription,d1_0.translation_id,d1_0.word_id from
		// words_dictionaryentry d1_0 left join words_dictionaryentry_dictionary
		// d2_0 on d1_0.id=d2_0.dictionaryentry_id where d2_0.dictionary_id in
		// (?)

		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	public Cursor fetchEntitiesWithoutCategory() {

		String query = "SELECT w.word,e.transcription,t.word FROM words_dictionaryentry as e join words_word as w on w.id=e.word_id join words_word as t on t.id=e.translation_id "
				+ "left join words_word_category as wc on w.id=wc.word_id " + "where wc.category_id is null;";
		Cursor cursor = database.rawQuery(query, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	public Cursor fetchEntitiesByDictionary(String dictionaryId) {

		String query = "SELECT w.word,e.transcription,t.word FROM words_dictionaryentry as e join words_word as w on w.id=e.word_id join words_word as t on t.id=e.translation_id "
				+ "left join words_dictionaryentry_dictionary ed on e.id=ed.dictionaryentry_id "
				+ "where ed.dictionary_id=?;";
		Cursor cursor = database.rawQuery(query, new String[] { dictionaryId });
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	public Cursor fetchEntitiesWithoutDictionary() {

		String query = "SELECT w.word,e.transcription,t.word FROM words_dictionaryentry as e join words_word as w on w.id=e.word_id join words_word as t on t.id=e.translation_id "
				+ "left join words_dictionaryentry_dictionary as ed on e.id=ed.dictionaryentry_id "
				+ "where ed.dictionary_id is null;";
		Cursor cursor = database.rawQuery(query, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}

//	public int update(long _id, String name, String desc) {
//		ContentValues contentValues = new ContentValues();
//		contentValues.put(DatabaseHelper.SUBJECT, name);
//		contentValues.put(DatabaseHelper.DESC, desc);
//		int i = database.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper._ID + " = " + _id, null);
//		return i;
//	}
//
//	public void delete(long _id) {
//		database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper._ID + "=" + _id, null);
//	}
}
