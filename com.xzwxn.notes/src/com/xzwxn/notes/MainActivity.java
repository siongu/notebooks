package com.xzwxn.notes;

import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.note.db.MyItem;
import com.android.note.operation.MyOperation;
import com.android.note.operation.SetFactory;
import com.android.note.views.MyCursorAdapter;
import com.xzwxn.notes.R;

public class MainActivity extends Activity {
	private Handler mhandler;
	private GridView gv;
	private MyOperation mOperation;
	private Button butt_del;
	private TextView tv_add;
	private TextView selected_count;
	private ImageButton add;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Log.e("Main+++++", "onCreate");
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.title_main);
		gv = (GridView) findViewById(R.id.gv);
		tv_add = (TextView) findViewById(R.id.tv_add);
		selected_count = (TextView) findViewById(R.id.selected_count);
		add = (ImageButton) findViewById(R.id.add);
		butt_del = (Button) findViewById(R.id.delete);
		mhandler = new Handler();
		mOperation = new MyOperation(this);
		inflateList(mOperation.dbHelper.findAll());
		// 新建文件
		add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						OtherActivity.class);
				startActivity(intent);
			}
		});
		// 删除按键的单击事件
		butt_del.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				deleteSelectedItems(SetFactory.getInstance().getData());
			}
		});
		// 设置GridView的长按事件
		gv.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				mOperation.long_flag = true;
				LinearLayout fl = (LinearLayout) view;
				CheckBox check = (CheckBox) fl.findViewById(R.id.check);
				check.setChecked(true);
				mOperation.mAdapter.mCheckStates.set(position,
						check.isChecked());
				MyItem mItem = mOperation.mAdapter.mList.get(position);
				SetFactory.getInstance().addData(mItem.getId());
				// ���߳�
				refreshTitleBar();
				setAllCheckBoxVisiable(true);
				return true;
			}
		});
		// 设置列表项单击事件
		gv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				LinearLayout linear = (LinearLayout) view;
				CheckBox check = (CheckBox) linear.findViewById(R.id.check);
				TextView _id = (TextView) linear.findViewById(R.id._id);
				if (mOperation.long_flag) {
					if (check.isChecked()) {
						check.setChecked(false);
						mOperation.mAdapter.mCheckStates.set(position,
								check.isChecked());
						SetFactory.getInstance().delItem(
								_id.getText().toString());
					} else {
						check.setChecked(true);
						mOperation.mAdapter.mCheckStates.set(position,
								check.isChecked());
						SetFactory.getInstance().addData(
								_id.getText().toString());
					}
					mOperation.mAdapter.notifyDataSetChanged();
				} else {
					String note_id = _id.getText().toString();
					// Log.e("id=",note_id);
					Intent intent = new Intent(MainActivity.this,
							OtherActivity.class);
					intent.putExtra("_id", note_id);
					startActivity(intent);
				}
			}
		});

	}

	// 启动子线程更新标题栏的数据
	private void refreshTitleBar() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (mOperation.long_flag) {
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					mhandler.post(new Runnable() {
						@Override
						public void run() {
							if (SetFactory.getInstance().getData().size() > 0) {
								setAddButTextVisiable(false);
								setButtDelStates(true);

							} else
								setButtDelStates(false);
						}
					});
				}
			}
		}).start();
	}

	// 设置删除按键的状态
	private void setButtDelStates(boolean state) {
		if (state == true) {
			butt_del.setVisibility(View.VISIBLE);
			butt_del.setClickable(true);
			butt_del.setBackgroundResource(R.drawable.delete_files);
			selected_count.setVisibility(View.VISIBLE);
			selected_count.setText("已选择\n"
					+ SetFactory.getInstance().getData().size() + "/"
					+ mOperation.mAdapter.getCount() + "项");
		} else {
			butt_del.setBackgroundResource(R.drawable.ic_alarm_remove_n);
			butt_del.setVisibility(View.INVISIBLE);
			butt_del.setClickable(false);
			if (mOperation.long_flag) {
				selected_count.setVisibility(View.VISIBLE);
				selected_count.setText("已选择\n"
						+ SetFactory.getInstance().getData().size() + "/"
						+ mOperation.mAdapter.getCount() + "项");
			} else {
				selected_count.setVisibility(View.INVISIBLE);
			}
		}
	}

	// 设置新建按键是否可见
	private void setAddButTextVisiable(boolean visiable) {
		int v = visiable == true ? View.VISIBLE : View.INVISIBLE;
		add.setVisibility(v);
		tv_add.setVisibility(v);
	}

	// ���ø�ѡ��Ŀɼ�״̬
	private void setAllCheckBoxVisiable(boolean visiable) {
		for (int i = 0; i < mOperation.mAdapter.getCount(); i++) {
			mOperation.mAdapter.mVisableStates.set(i, visiable);
		}
		// 通知Adapter进行数据更新
		mOperation.mAdapter.notifyDataSetChanged();
	}

	// 删除已选择的列表项
	public void deleteSelectedItems(Set<String> set) {
		mOperation.popupDialog
				.setTitle(set.size() == 0 ? "未选择" : "删除")
				.setIcon(R.drawable.status_danger)
				.setLayoutParamas(0.9, 0.25)
				.setMessage(
						set.size() == 0 ? "选择删除项？" : "您要删除" + set.size()
								+ "项吗？")
				.setPositiveButton(set.size() == 0 ? "确定" : "删除",
						new OnClickListener() {
							@Override
							public void onClick(View v) {
								if (SetFactory.getInstance().getData().size() > 0) {
									String ids[] = SetFactory.getInstance()
											.getData().toArray(new String[] {});
									mOperation.dbHelper.deleteByIds(ids);
									Toast.makeText(
											MainActivity.this,
											"已删除"
													+ SetFactory.getInstance()
															.getData().size()
													+ "项", Toast.LENGTH_SHORT)
											.show();
									SetFactory.getInstance().clear();
									mOperation.long_flag = false;
									// 更新界面
									inflateList(mOperation.dbHelper.findAll());
									butt_del.setVisibility(View.INVISIBLE);
									setAddButTextVisiable(true);
									mOperation.popupDialog.dismiss();
								} else {
									mOperation.long_flag = true;
									setAllCheckBoxVisiable(true);
									setButtDelStates(false);
									refreshTitleBar();
									setAddButTextVisiable(false);
									mOperation.popupDialog.dismiss();
								}
							}
						}).setNegativeButton("取消", new OnClickListener() {
					@Override
					public void onClick(View v) {
						mOperation.popupDialog.dismiss();
					}
				});
		mOperation.popupDialog.show();
	}

	//更新界面要显示的内容
	public void inflateList(List<MyItem> list) { 
		mOperation.mAdapter = new MyCursorAdapter(this, list);
		if (mOperation.onPause) { 
			/**
			 * 如果有增加新的数据的话，就相应的增加一个默认的状态
			 * list的长度大于checkStates的长度时表示增加了新的条目
			 * 否则没有增加新的条目（即是否有新建记事本项目）。
			 */
			if (list.size() > mOperation.checkStates.size()) { 
				mOperation.mAdapter.mCheckStates.clear();
				//恢复原来的状态并增加一个新的默认状态
				mOperation.mAdapter.mCheckStates.addAll(mOperation.checkStates);
				mOperation.mAdapter.mCheckStates.add(false);
				mOperation.mAdapter.mVisableStates.clear(); 
				//恢复原来的状态
				mOperation.mAdapter.mVisableStates
						.addAll(mOperation.visableStates);
				mOperation.mAdapter.mVisableStates.add(false);
			} else {
				//恢复原来的状态
				mOperation.mAdapter.mCheckStates.clear(); 
				mOperation.mAdapter.mCheckStates.addAll(mOperation.checkStates); 
				mOperation.mAdapter.mVisableStates.clear(); 
				mOperation.mAdapter.mVisableStates
						.addAll(mOperation.visableStates);
			}
		}
		gv.setAdapter(mOperation.mAdapter); // 设置Adapter
	}

	//Activity恢复运行时调用此方法
	@Override
	protected void onResume() { 
	// Log.e("Main+++++", "onResume");
		inflateList(mOperation.dbHelper.findAll());
		super.onResume();
	}

	@Override
	protected void onPause() {
		// Log.e("Main+++++", "onPause");
		mOperation.onPause = true;
		mOperation.checkStates.clear(); 
		//保存onPause之前的选择状态
		mOperation.checkStates.addAll(mOperation.mAdapter.mCheckStates); 
		mOperation.visableStates.clear(); 
		//保存onPause之前复选框的可见状态
		mOperation.visableStates.addAll(mOperation.mAdapter.mVisableStates); 
		super.onPause();
	}

	/*
	 * @Override protected void onStart() { Log.e("Main+++++", "onStart");
	 * super.onStart(); }
	 * 
	 * @Override protected void onRestart() { Log.e("Main+++++", "onRestart");
	 * super.onRestart(); }
	 * 
	 * @Override protected void onStop() { Log.e("Main+++++", "onStop");
	 * super.onStop(); }
	 */
	@Override
	protected void onDestroy() {
		mOperation.dbHelper.closeDB();
		mOperation = null;
		// Log.e("Main+++++", "onDestroy");
		super.onDestroy();
	}

	/*
	 * @Override public void onConfigurationChanged(Configuration newConfig) {
	 * super.onConfigurationChanged(newConfig);
	 * if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
	 */
//当有按键按下时的响应事件
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK: {
			if (mOperation.long_flag) {
				mOperation.long_flag = false;
				SetFactory.getInstance().clear();
				butt_del.setVisibility(View.INVISIBLE);
				mOperation.setAllSelected(false);
				setAllCheckBoxVisiable(false);
				setAddButTextVisiable(true);
				return false;
			} else {
				mOperation.popupDialog.setTitle("退出").setMessage("您要退出吗？")
						.setIcon(R.drawable.status_danger)
						.setLayoutParamas(0.9, 0.25)
						.setPositiveButton("确定", new OnClickListener() {
							@Override
							public void onClick(View v) {
								mOperation.popupDialog.dismiss();
								finish();
							}
						}).setNegativeButton("取消", new OnClickListener() {
							@Override
							public void onClick(View v) {
								mOperation.popupDialog.dismiss();
							}
						});
				mOperation.popupDialog.show();
			}
			return false;
		}
		default:
			return super.onKeyDown(keyCode, event);
		}
	}

	// 创建选项菜单
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// 取消选择
		case R.id.action_cancel: {
			mOperation.long_flag = false;
			SetFactory.getInstance().clear();
			setAllCheckBoxVisiable(false);
			mOperation.setAllSelected(false);
			setButtDelStates(false);
			setAddButTextVisiable(true);
		}
			break;
		// 全选
		case R.id.action_selectall: {
			mOperation.long_flag = true;
			mOperation.setAllSelected(true);
			setAllCheckBoxVisiable(true);
			SetFactory.getInstance().clear();
			SetFactory.getInstance().addAll(mOperation.cursor_idToSet());
			refreshTitleBar();
		}
			break;
		// 删除选择项
		case R.id.action_delete: {
			deleteSelectedItems(SetFactory.getInstance().getData());
		}
			break;
		// 反选
		case R.id.action_invert_select: {
			mOperation.invertSelected();

		}
		default:
		}
		return super.onOptionsItemSelected(item);
	}
}
