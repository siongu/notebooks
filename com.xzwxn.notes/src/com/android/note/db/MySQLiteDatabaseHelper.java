package com.android.note.db;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

public class MySQLiteDatabaseHelper extends SQLiteOpenHelper {
	private Context mContext;
	private static final String DBNAME = "notes.db";
	private static final int VERSION = 1;
	private SQLiteDatabase db = null;

	public MySQLiteDatabaseHelper(Context context) {
		super(context, DBNAME, null, VERSION);
		this.mContext = context;
	}

	private SQLiteDatabase getInstance() {
		if (db == null || !db.isOpen()) {
			db = new MySQLiteDatabaseHelper(mContext).getWritableDatabase();
		}
		return db;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table users (_id integer primary key autoincrement,"
				+ "title varchar(50)," + "content varchar(255),"
				+ "date timestamp not null default CURRENT_TIMESTAMP)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exsits users");
		onCreate(db);
	}

	/**
	 * @param title
	 *            ��־�ı���
	 * @param content
	 *            ��־������
	 * @param date
	 *            �޸���־������
	 * @return the row ID of the newly inserted row, or -1 if an error occurred
	 */
	public String save(String title, String content, String date) {

		// SQLiteDatabase db=new
		// MySQLiteDatabaseHelper(mContext).getWritableDatabase();
		db = getInstance();
		ContentValues values = new ContentValues();
		values.put("title", title);
		values.put("content", content);
		values.put("date", date);
		db.insert("users", null, values);
		Cursor mCursor = db.rawQuery("select last_insert_rowid() from users",
				null); // �������²����е�id
		mCursor.moveToNext();
		String id = mCursor.getString(0);
		db.close();
		return id;
	}

	/**
	 * @param ids
	 *            Ҫɾ���е�����id
	 * @return the number of rows affected if a whereClause is passed in, 0
	 *         otherwise. To remove all rows
	 */
	public int deleteByIds(String ids[]) {
		// SQLiteDatabase db=new
		// MySQLiteDatabaseHelper(mContext).getWritableDatabase();
		db = getInstance();
		int n = 0;
		if (ids.length > 0) {
			String strIds = TextUtils.join(",", ids);
			n = db.delete("users", "_id IN(" + strIds + ")", null);
		}
		db.close();
		return n;
	}

	/**
	 * @param id
	 *            Ҫ�����е�id
	 * @param title
	 *            ���º�ı���
	 * @param content
	 *            ���º������
	 * @param date
	 *            ����ʱ��
	 * @return the number of rows affected
	 */
	public int update(String id, String title, String content, String date) {
		// SQLiteDatabase db=new
		// MySQLiteDatabaseHelper(mContext).getWritableDatabase();
		db = getInstance();
		ContentValues values = new ContentValues();
		values.put("title", title);
		values.put("content", content);
		values.put("date", date);
		int i = db.update("users", values, "_id=?", new String[] { id });
		db.close();
		return i;

	}

	public MyItem findById(String id) {
		db = getInstance();
		Cursor cursor = db.query("users", null, "_id=?", new String[] { id },
				null, null, null);
		MyItem mItem = new MyItem(mContext);
		cursor.moveToNext();
		mItem.setId(cursor.getString(0));
		mItem.setTitle(cursor.getString(1));
		mItem.setContent(cursor.getString(2));
		mItem.setDate(cursor.getString(3));
		db.close();
		return mItem;
	}

	/**
	 * @return ����һ��list<Map<String,String>>����
	 */
	public List<MyItem> findAll() {
		db = getInstance();
		Cursor cursor = db.query("users", null, null, null, null, null,
				"date desc");
		List<MyItem> list = new ArrayList<MyItem>();
		while (cursor.moveToNext()) {
			MyItem mItem = new MyItem(mContext);
			mItem.setId(cursor.getString(0));
			mItem.setTitle(cursor.getString(1));
			mItem.setContent(cursor.getString(2));
			mItem.setDate(cursor.getString(3));
			list.add(mItem);
		}
		db.close();
		return list;
	}

	public void closeDB() {
		if (db != null && db.isOpen()) {
			db.close();
		}
	}
}
