package com.gis.client.activity.query.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.gis.client.R;
import com.gis.client.activity.query.tree.adapter.TreeListAdatper;
import com.gis.client.activity.query.tree.util.InMemoryTreeStateManager;
import com.gis.client.activity.query.tree.util.TreeNodeInfo;
import com.gis.client.activity.query.tree.util.TreeStateManager;
import com.gis.client.common.CommonActivity;
import com.gis.client.common.CommonProgressDialog;
import com.gis.client.model.CommonObject;
import com.gis.client.model.Line;
import com.gis.client.model.Switch;
import com.gis.client.model.Transformer;

public class TreeLineActivity extends CommonActivity implements OnClickListener {

	private TreeListView mListView;
	private TreeStateManager<CommonObject> mTreeManager;
	private TreeListAdatper mAdatper;
	private CommonObject currentObject;

	private Button openAllBtn;
	private Button clossAllBtn;
	private Button clearLabelBtn;
	private boolean collapsible = true;

	private Context mContext;

	private CommonProgressDialog mProgressDialog;

	private List<Line> mLineList = new ArrayList<Line>();

	private List<Transformer> mTransformerList = new ArrayList<Transformer>();

	private List<Switch> mSwitchList = new ArrayList<Switch>();

	private boolean mSelDevices;

	private Map<CommonObject, String> objectMap = new HashMap<CommonObject, String>();
	
	private boolean mExit;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tree_main_line_layout);

		mContext = TreeLineActivity.this;
		mSelDevices = getIntent().getBooleanExtra("selectFlg", false);
		initView();
		getTreeInfo();
	}

	private void initView() {
		mListView = (TreeListView) findViewById(R.id.tree_main_list);
		/*openAllBtn = (Button) findViewById(R.id.tree_main_open_all_btn);
		clossAllBtn = (Button) findViewById(R.id.tree_main_close_all_btn);
		clearLabelBtn = (Button) findViewById(R.id.tree_main_clear_label_btn);*/
		/*openAllBtn.setOnClickListener(this);
		clossAllBtn.setOnClickListener(this);
		clearLabelBtn.setOnClickListener(this);*/

	}

	private void fillTreeData() {
		mTreeManager = new InMemoryTreeStateManager<CommonObject>();
		if (mLineList != null && mLineList.size() > 0) {
//			int size = mLineList.size();
			for (int i = 0; i < mLineList.size(); i++) {
				Line line = mLineList.get(i);
				String name = line.getName();
				if (name == null || "".equals(name.trim())) {
//					mLineList.remove(i);
					continue;
				}
				int lineNumber = line.getSuperId();
				if (-1 == lineNumber) {
					mTreeManager.addAfterChild(null, line, null);
				}
			}
		}
	}

	private void fillData() {

		mTreeManager = new InMemoryTreeStateManager<CommonObject>();
		if (mLineList != null && mLineList.size() > 0) {
			for (int i = 0; i < mLineList.size(); i++) {
				Line line = mLineList.get(i);
				int superId = line.getSuperId();
				int number = line.getLineNumber();
				if (superId == -1) {// 判断是否为根节点
					mTreeManager.addAfterChild(null, line, null);
				} else {// 如果不为根节点，再循环判断是否还有父节点
					for (int j = 0; j < mLineList.size(); j++) {
						Line superLine = mLineList.get(j);
						int superNumber = superLine.getLineNumber();
						if (superId == superNumber) {
							if (!mTreeManager.isInTree(superLine)) {
								checkSuperLine(superLine);
							}
							if (!mTreeManager.isInTree(line)) {
								mTreeManager.addAfterChild(superLine, line,
										null);
							}
							break;
						}
					}
				}
				if (mTransformerList != null && mTransformerList.size() > 0) {
					for (int j = 0; j < mTransformerList.size(); j++) {
						Transformer voltage = mTransformerList.get(j);
						int vSuperId = voltage.getSuperId();
						if (vSuperId == number) {
							mTreeManager.addAfterChild(line, voltage, null);
						}
					}
				}
				if (mSwitchList != null && mSwitchList.size() > 0) {
					for (int j = 0; j < mSwitchList.size(); j++) {
						Switch switch1 = mSwitchList.get(j);
						int vSuperId = switch1.getSuperId();
						if (vSuperId == number) {
							Log.i("wpt", "size=" + i);
							mTreeManager.addAfterChild(line, switch1, null);
						}
					}
				}
			}
		}

	}

	private void checkSuperLine(Line line) {
		int lineNumber = line.getSuperId();
		for (int j = 0; j < mLineList.size(); j++) {
			Line superLine = mLineList.get(j);
			int superNumber = superLine.getNumber();
			if (lineNumber == superNumber) {
				if (mTreeManager.isInTree(superLine)
						&& !mTreeManager.isInTree(line)) {
					mTreeManager.addAfterChild(superLine, line, null);
				} else {
					checkSuperLine(superLine);
				}
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {
		/*switch (v.getId()) {
		case R.id.tree_main_open_all_btn:
			mTreeManager.expandEverythingBelow(null);
			// startActivity(new Intent(mContext, TreeResultActivity.class));
			break;
		case R.id.tree_main_close_all_btn:
			mTreeManager.collapseChildren(null);
			break;
		case R.id.tree_main_clear_label_btn:
			if (collapsible) {
				clearLabelBtn.setText("有折叠标签");
				mListView.setCollapsible(false);
			} else {
				clearLabelBtn.setText("无折叠标签");
				mListView.setCollapsible(true);
			}
			collapsible = !collapsible;
			break;
		default:
			break;
		}
*/
	}

	@SuppressWarnings("unchecked")
	private void getTreeInfo() {
		Intent intent = getIntent();
		if (intent != null) {
			mLineList = (List<Line>) intent.getSerializableExtra("lineList");
			mTransformerList = (List<Transformer>) intent
					.getSerializableExtra("transformerList");
			mSwitchList = (List<Switch>) intent
					.getSerializableExtra("switchList");
			new getTreeInfoTask().execute();
		}
	}

	class getTreeInfoTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (!mExit) {
				mProgressDialog = CommonProgressDialog.show(mContext,
						R.string.str_tip,
						getResources().getString(R.string.str_msg_query_ing));
			}
		}

		@Override
		protected Void doInBackground(Void... params) {
//			fillData();
			fillTreeData();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (!mExit) {
				mProgressDialog.dismiss();
			}
			mAdatper = new TreeListAdatper(TreeLineActivity.this, mTreeManager,
					10, mSelDevices, indicatorClickListener);
			mListView.setAdapter(mAdatper);
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mExit = true;
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}

	private OnClickListener indicatorClickListener = new OnClickListener() {
		@Override
		public void onClick(final View v) {
			CommonObject object = (CommonObject) v.getTag();
			boolean exist = objectMap.containsKey(object);
			if (object instanceof Line) {
				Line line = (Line) object;
				int lineNumber = line.getLineNumber();
				TreeNodeInfo<CommonObject> info = mTreeManager
						.getNodeInfo(object);
				if (!info.isWithChildren() && !exist) {
					fillChildData(line, lineNumber);
					objectMap.put(object, "true");
				}
				boolean expand = info.isExpanded();
				if (expand) {
					mTreeManager.collapseChildren(object);
				} else {
					mTreeManager.expandDirectChildren(object);
				}
			}

		}
	};

	private boolean fillChildData(Line superLine, int number) {
		boolean isChild = false;
		if (mLineList != null && mLineList.size() > 0) {
			for (int i = 0; i < mLineList.size(); i++) {
				Line line = mLineList.get(i);
				int superId = line.getSuperId();
				String name = line.getName();
				if (name == null || "".equals(name.trim())) {
//					mLineList.remove(i);
					continue;
				}
				if (number == superId && !mTreeManager.isInTree(line)) {
					mTreeManager.addAfterChild(superLine, line, null);
					isChild = true;
				}
				if (mTransformerList != null && mTransformerList.size() > 0) {
					for (int j = 0; j < mTransformerList.size(); j++) {
						Transformer voltage = mTransformerList.get(j);
						int vSuperId = voltage.getSuperId();
						if (vSuperId == number
								&& !mTreeManager.isInTree(voltage)) {
							mTreeManager
									.addAfterChild(superLine, voltage, null);
							isChild = true;
						}
					}
				}
				if (mSwitchList != null && mSwitchList.size() > 0) {
					for (int j = 0; j < mSwitchList.size(); j++) {
						Switch switch1 = mSwitchList.get(j);
						int vSuperId = switch1.getSuperId();
						if (vSuperId == number
								&& !mTreeManager.isInTree(switch1)) {
							mTreeManager
									.addAfterChild(superLine, switch1, null);
							isChild = true;
						}
					}
				}
			}
//			mAdatper.notifyDataSetChanged();
		}
		return isChild;
	}
}
