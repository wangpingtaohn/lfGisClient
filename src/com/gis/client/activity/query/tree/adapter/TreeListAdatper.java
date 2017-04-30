package com.gis.client.activity.query.tree.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.gis.client.R;
import com.gis.client.activity.query.tree.TreeResultActivity;
import com.gis.client.activity.query.tree.util.TreeNodeInfo;
import com.gis.client.activity.query.tree.util.TreeStateManager;
import com.gis.client.model.CommonObject;
import com.gis.client.model.Switch;

public class TreeListAdatper extends AbstractTreeViewAdapter<CommonObject> {

	private int minItemW = 0;
	private int layoutW = 0;
	private Drawable drawable1;
	private Drawable drawable2;
	private Activity mContext;
	private boolean mSelDevices;

	/**
	 * @param activity
	 * @param treeStateManager
	 * @param numberOfLevels
	 */
	public TreeListAdatper(Activity activity,
			TreeStateManager<CommonObject> treeStateManager,
			int numberOfLevels, boolean selDevices,OnClickListener indicatorClickListener) {
		super(activity, treeStateManager, numberOfLevels, indicatorClickListener);
		this.mContext = activity;
		this.mSelDevices = selDevices;
	}

	@Override
	public long getItemId(int position) {
		return getTreeId(position).hashCode();
	}

	@Override
	protected int getItemWidth() {
		if (minItemW == 0) {
			minItemW = mContext.getResources().getDisplayMetrics().widthPixels;
		}
		int itemW = super.getItemWidth();
		return itemW < minItemW ? minItemW : itemW;
	}

	@Override
	protected int getLayoutWidth() {
		if (layoutW == 0) {
			layoutW = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 330, getActivity()
							.getResources().getDisplayMetrics());
		}
		return layoutW;
	}

	@Override
	public View getNewChildView(TreeNodeInfo<CommonObject> treeNodeInfo) {
		View view = getActivity().getLayoutInflater().inflate(
				R.layout.tree_lv_item, null);
		ViewHolder holder = new ViewHolder();
		holder.tv1 = (TextView) view.findViewById(R.id.tv1);
		// holder.tv2 = (TextView) view.findViewById(R.id.tv2);
		view.setTag(holder);
		return updateView(view, treeNodeInfo);
	}

	@Override
	public View updateView(View view, TreeNodeInfo<CommonObject> treeNodeInfo) {
		ViewHolder holder = (ViewHolder) view.getTag();
		CommonObject node = treeNodeInfo.getId();
		String name = "未知";
		if (node.getName() != null && !" ".equals(node.getName())) {
			name = node.getName();
		}
		holder.tv1.setText(name);
		// holder.tv2.setText(node.getCode());
		return view;
	}

	@Override
	public Drawable getBackgroundDrawable(
			TreeNodeInfo<CommonObject> treeNodeInfo) {

		if (treeNodeInfo.getLevel() == 0) {
			if (null == drawable1) {
				drawable1 = new ColorDrawable(Color.GRAY);
			}
			return drawable1;
		} else if (treeNodeInfo.getLevel() == 1) {
			if (null == drawable2) {
				drawable2 = new ColorDrawable(Color.LTGRAY);
			}
			return drawable2;
		}
		return null;
	}

	@Override
	public void handleItemClick(View view, Object id) {
		if (!isCollapsible()) {
			super.handleItemClick(view, id);
		} else {
			CommonObject node = (CommonObject) id;
			if (mSelDevices) {
				if (node instanceof Switch) {
					Switch switch1 = (Switch) node;
					int boardId = switch1.getBoard();
					String name = switch1.getName();
					Intent intent = new Intent();
					intent.putExtra("devicesName", name);
					intent.putExtra("boardId", boardId);
					mContext.setResult(Activity.RESULT_OK, intent);
					mContext.finish();
				}
			} else {
				Intent intent = new Intent(mContext, TreeResultActivity.class);
				Bundle bundle = new Bundle();
//				bundle.putParcelable("result", (Parcelable) node);
				bundle.putSerializable("result", node);
				intent.putExtras(bundle);
				mContext.startActivity(intent);

			}
			
		}
	}

	class ViewHolder {
		TextView tv1;
		// TextView tv2;
	}

}
