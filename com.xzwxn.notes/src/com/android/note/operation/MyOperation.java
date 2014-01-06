package com.android.note.operation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.android.note.db.MyItem;
import com.android.note.db.MySQLiteDatabaseHelper;
import com.android.note.views.MyCursorAdapter;
import com.android.note.views.MyCustomDialog;
import com.xzwxn.notes.R;
import android.content.Context;
import android.view.View;

public class MyOperation {
	private Context mContext;
	public boolean long_flag = false;
	public boolean onPause = false;
	public List<Boolean> checkStates;
	public List<Boolean> visableStates;
	public MyCursorAdapter mAdapter;
	public MySQLiteDatabaseHelper dbHelper;
	private View rootMain;
	public MyCustomDialog popupDialog;

	public MyOperation(Context context) {
		this.mContext = context;
		this.rootMain = View.inflate(mContext, R.layout.activity_main, null);
		checkStates = new ArrayList<Boolean>();
		visableStates = new ArrayList<Boolean>();
		popupDialog = new MyCustomDialog(mContext, rootMain);
		dbHelper = new MySQLiteDatabaseHelper(mContext);
	}

	//全部选择
	public void setAllSelected(boolean allSelected) {
		if (allSelected) {
			//先清空原来的状态集合
			mAdapter.mCheckStates.clear();
			mAdapter.mCheckStates.addAll(mAdapter.mSetCheckStates);
		} else {
			mAdapter.mCheckStates.clear();
			mAdapter.mCheckStates.addAll(mAdapter.mResetCheckStates);
		}
		//通知Adapter有数据更新
		mAdapter.notifyDataSetChanged();
	}

	//反向选择
	public void invertSelected() { 
		if (long_flag) {
			SetFactory.getInstance().clear();
			for (int i = 0; i < mAdapter.mCheckStates.size(); i++) {
				if (mAdapter.mCheckStates.get(i) == false) {
					MyItem mItem = mAdapter.mList.get(i);
					mAdapter.mCheckStates.set(i, true);
					SetFactory.getInstance().addData(mItem.getId());
				} else {
					mAdapter.mCheckStates.set(i, false);
				}
			}
			mAdapter.notifyDataSetChanged();
		}
	}
//将cursor转换成set集合
	public Set<String> cursor_idToSet() { 
		Set<String> cSet = new HashSet<String>();
		List<MyItem> list = dbHelper.findAll();
		for (int i = 0; i < list.size(); i++) {
			MyItem mItem = list.get(i);
			cSet.add(mItem.getId());
		}
		return cSet;
	}

}
