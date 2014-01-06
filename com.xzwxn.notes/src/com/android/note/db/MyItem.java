package com.android.note.db;

import android.content.Context;

public class MyItem {
	private String id = null;
	private String title = null;
	private String content = null;
	private String date = null;
	private Context mContext;

	public MyItem(Context context) {
		this.mContext = context;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Context getmContext() {
		return mContext;
	}

	public void setmContext(Context mContext) {
		this.mContext = mContext;
	}

}
