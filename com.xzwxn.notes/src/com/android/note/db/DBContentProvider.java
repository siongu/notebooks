package com.android.note.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class DBContentProvider extends ContentProvider {
	private SQLiteDatabase db;
	private MySQLiteDatabaseHelper dbHelper;
	private final String TABLE = "users";
	public static final int PERSONS = 1;
	public static final int PERSON = 2;
	public static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		matcher.addURI("com.android.note.db", "persons", PERSONS);
		matcher.addURI("com.android.note.db", "person/#", PERSON);
	}

	@Override
	public boolean onCreate() {
		dbHelper = new MySQLiteDatabaseHelper(this.getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		db = dbHelper.getWritableDatabase();
		switch (matcher.match(uri)) {
		case PERSONS: {
			Cursor cursor = db.query(TABLE, projection, selection,
					selectionArgs, null, null, sortOrder);
			return cursor;
		}
		case PERSON:
			long id = ContentUris.parseId(uri);
			Cursor cursor = db.query(TABLE, null, "_id=?", new String[] { id
					+ "" }, null, null, sortOrder);
			return cursor;
		default:
			throw new IllegalArgumentException("û��ƥ�����޷���ѯ");
		}
	}

	@Override
	public String getType(Uri uri) {
		switch (matcher.match(uri)) {
		case PERSONS: {
			return "vnd.android.cursor.dir/com.android.note.db";
		}
		case PERSON: {
			return "vnd.android.cursor.item/com.android.note.db";
		}
		default:
			throw new IllegalArgumentException("MIME���ʹ����޷��ҵ�ƥ�������");
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		db = dbHelper.getWritableDatabase();
		db.insert(TABLE, null, values);
		return uri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		db = dbHelper.getWritableDatabase();
		switch (matcher.match(uri)) {
		case PERSONS: {
			int num = db.delete(TABLE, selection, selectionArgs);
			return num;
		}
		case PERSON: {
			long id = ContentUris.parseId(uri);
			int num = db.delete(TABLE, "_id=?", new String[] { id + "" });
			return num;
		}
		default:
			throw new IllegalArgumentException("�޷�ƥ�䣬�Ҳ���ɾ���ѡ��");
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		db = dbHelper.getWritableDatabase();
		switch (matcher.match(uri)) {
		case PERSONS: {
			int num = db.update(TABLE, values, selection, selectionArgs);
			return num;
		}
		case PERSON: {
			long id = ContentUris.parseId(uri);
			int num = db.update(TABLE, values, "_id=?",
					new String[] { id + "" });
			return num;
		}
		default:
			throw new IllegalArgumentException("�������ޣ�����ʧ��");
		}
	}

}
