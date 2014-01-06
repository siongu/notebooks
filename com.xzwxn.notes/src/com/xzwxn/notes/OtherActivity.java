package com.xzwxn.notes;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.note.db.MyItem;
import com.android.note.db.MySQLiteDatabaseHelper;
import com.android.note.operation.MyOperation;
import com.xzwxn.notes.R;

public class OtherActivity extends Activity {
	private MySQLiteDatabaseHelper dbHelper;
	private MyOperation myOperation;
	private EditText et_title;
	private EditText et_content;
	private boolean browse = false;
	private String index = null;
	private String title;
	private String content;
	private String date;
	private MyItem mItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_other);
		// Log.e("Other+++++", "onCreate");
		// 设置自定义标题
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.title_other);
		// 设置开始不弹出软键盘
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		Button save = (Button) findViewById(R.id.save);
		et_content = (EditText) findViewById(R.id.content);
		et_title = (EditText) findViewById(R.id.title);
		dbHelper = new MySQLiteDatabaseHelper(this);
		// 获取有MainActivity传递过来的intent
		Intent intent = getIntent();
		Bundle bundle;
		bundle = intent.getExtras();
		// 如果Bundle中的数据不为空表示当前操作为浏览某一个列表项内容
		if (bundle != null) {
			browse = true;
			index = bundle.getString("_id");
			mItem = dbHelper.findById(index);
			title = mItem.getTitle();
			content = mItem.getContent();
			et_title.setText(title);
			et_content.setText(content);
		}
		save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				title = et_title.getText().toString().trim();
				content = et_content.getText().toString().trim();
				date = date();
				if (index != null) {
					if (!"".equals(title) && title != null
							&& !"".equals(content) && content != null) {
						dbHelper.update(index, title, content, date);
						Toast.makeText(OtherActivity.this, "已保存",
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(OtherActivity.this, "标题或内容不能为空",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					if (!"".equals(title) && title != null
							&& !"".equals(content) && content != null) {
						index = dbHelper.save(title, content, date);
						Toast.makeText(OtherActivity.this, "保存成功",
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(OtherActivity.this, "标题或内容不能为空",
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			checkQuit();
		}
		return super.onKeyDown(keyCode, event);
	}

	@SuppressLint("SimpleDateFormat")
	// 获取当前的时间
	public String date() {
		Date d = new Date();
		SimpleDateFormat adf = new SimpleDateFormat("yyyy/MM/dd  HH:mm:ss ");
		String str = adf.format(d);
		return str;
	}

	// 退出前检查
	private void checkQuit() {
		title = et_title.getText().toString().trim();
		content = et_content.getText().toString().trim();
		date = date();
		//index为空表示之前没有保存过
		if (index == null) {
			// 如果标题和内容都不为空则保存否则不保存
			if (!"".equals(title) && title != null && !"".equals(content)
					&& content != null) {
				index = dbHelper.save(title, content, date);
			}
		}
		// 如果之前已经有过保存并且当前处于浏览状态
		else if (browse) {
			String preTitle = mItem.getTitle();
			String preContent = mItem.getContent();
			// 如果标题或内容有修改则保存修改后的内容
			if ((!preTitle.equals(title) || !preContent.equals(content))
					&& !"".equals(title) && title != null
					&& !"".equals(content) && content != null) {
				dbHelper.update(index, title, content, date);
			} else if ("".equals(title) || title == null || "".equals(content)
					|| content == null) {
				myOperation = new MyOperation(this);
				myOperation.popupDialog.setTitle("提示")
						.setMessage("标题或内容为空\n 是否退出？")
						.setIcon(R.drawable.status_danger)
						.setLayoutParamas(0.9, 0.25)
						// .setNegativeButtonVisiable(false)
						.setPositiveButton("是", new OnClickListener() {
							@Override
							public void onClick(View v) {
								dbHelper.deleteByIds(new String[] { index });
								myOperation.popupDialog.dismiss();
								finish();
							}
						}).setNegativeButton("否", new OnClickListener() {
							@Override
							public void onClick(View v) {
								myOperation.popupDialog.dismiss();
							}
						});
				myOperation.popupDialog.show();
			}
		}
		// 之前有保存过，并处于新建状态
		else {
			// 如果内容退出前内容和标题都不为空则保存，否则不保存并删除当前文件
			if (!"".equals(title) && title != null && !"".equals(content)
					&& content != null) {
				dbHelper.update(index, title, content, date);
			} else {
				dbHelper.deleteByIds(new String[] { index });
			}
		}
	}

	/*
	 * @Override protected void onStart() { Log.e("Other+++++", "onStart");
	 * super.onStart(); }
	 * 
	 * @Override protected void onRestart() { Log.e("Other+++++", "onRestart");
	 * super.onRestart(); }
	 * 
	 * @Override protected void onResume() { Log.e("Other+++++", "onResume");
	 * super.onResume(); }
	 * 
	 * @Override protected void onPause() { super.onPause(); Log.e("Other+++++",
	 * "onPause"); }
	 * 
	 * @Override protected void onStop() { Log.e("Other+++++", "onStop");
	 * super.onStop(); }
	 */
	@Override
	protected void onDestroy() {
		dbHelper.closeDB();
		myOperation = null;
		// Log.e("Other+++++", "onDestroy");
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.other, menu);
		return true;
	}

}
