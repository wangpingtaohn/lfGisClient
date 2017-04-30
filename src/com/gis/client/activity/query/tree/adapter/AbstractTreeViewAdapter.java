package com.gis.client.activity.query.tree.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

import com.gis.client.R;
import com.gis.client.activity.query.tree.util.TreeNodeInfo;
import com.gis.client.activity.query.tree.util.TreeStateManager;


/**
 * Adapter used to feed the table view.
 * 
 * @param <T>
 *          class for ID of the tree
 */
@SuppressLint("NewApi")
public abstract class AbstractTreeViewAdapter<T> extends BaseAdapter implements ListAdapter {
  private static final String TAG = AbstractTreeViewAdapter.class.getSimpleName();
  private final TreeStateManager<T> treeStateManager;
  private final int numberOfLevels;
  private final LayoutInflater layoutInflater;
  private int itemPadding; // 列表单元左右两端的padding;

  private int indentWidth = 0;
  private int indicatorGravity = 0;
  private Drawable collapsedDrawable;
  private Drawable expandedDrawable;
  private Drawable backgroundDrawable;
  private boolean isAutoAdapt = false;
  private int oldMaxLevel = 0;
  protected int maxLevel = 0;
  private OnClickListener mIndicatorClickListener;

  private final OnClickListener indicatorClickListener = new OnClickListener() {
    @Override
    public void onClick(final View v) {
      @SuppressWarnings("unchecked")
      final T id = (T) v.getTag();
      expandCollapse(id);
    }
  };

  private boolean collapsible;
  private final Activity activity;

  public Activity getActivity() {
    return activity;
  }

  protected TreeStateManager<T> getManager() {
    return treeStateManager;
  }

  protected void expandCollapse(final T id) {
    final TreeNodeInfo<T> info = treeStateManager.getNodeInfo(id);
    if (!info.isWithChildren()) {
      // ignore - no default action
      return;
    }
    if (info.isExpanded()) {
      treeStateManager.collapseChildren(id);
    } else {
      treeStateManager.expandDirectChildren(id);
    }
  }

  protected void getMaxLevel() {
    List<T> list = treeStateManager.getVisibleList();
    maxLevel = 0;
    for (int i = 0, len = list.size(); i < len; i++) {
      TreeNodeInfo<T> info = treeStateManager.getNodeInfo(list.get(i));
      int level = info.getLevel();
      if (level > maxLevel) {
        maxLevel = level;
      }
    }
  }

  public boolean isAutoAdapt() {
    return isAutoAdapt;
  }

  public void setAutoAdapt(boolean isAutoAdapt) {
    this.isAutoAdapt = isAutoAdapt;
  }

  protected int getItemPadding() {
    return itemPadding;
  }

  private void calculateIndentWidth() {
    if (expandedDrawable != null) {
      indentWidth = Math.max(getIndentWidth(), expandedDrawable.getIntrinsicWidth());
    }
    if (collapsedDrawable != null) {
      indentWidth = Math.max(getIndentWidth(), collapsedDrawable.getIntrinsicWidth());
    }
  }

  public AbstractTreeViewAdapter(final Activity activity, final TreeStateManager<T> treeStateManager,
      final int numberOfLevels,OnClickListener indicatorClickListener) {
    this.activity = activity;
    this.treeStateManager = treeStateManager;
    this.layoutInflater = LayoutInflater.from(activity);
    this.numberOfLevels = numberOfLevels;
    this.collapsedDrawable = null;
    this.expandedDrawable = null;
    this.itemPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, activity.getResources()
        .getDisplayMetrics());
    this.mIndicatorClickListener = indicatorClickListener;
  }

  @Override
  public void registerDataSetObserver(final DataSetObserver observer) {
    treeStateManager.registerDataSetObserver(observer);
  }

  @Override
  public void unregisterDataSetObserver(final DataSetObserver observer) {
    treeStateManager.unregisterDataSetObserver(observer);
  }

  @Override
  public int getCount() {
    return treeStateManager.getVisibleCount();
  }

  @Override
  public Object getItem(final int position) {
    return getItemId(position);
  }

  public T getTreeId(final int position) {
    return treeStateManager.getVisibleList().get(position);
  }

  public TreeNodeInfo<T> getTreeNodeInfo(final int position) {
    return treeStateManager.getNodeInfo(getTreeId(position));
  }

  @Override
  public boolean hasStableIds() { // NOPMD
    return true;
  }

  @Override
  public int getItemViewType(final int position) {
    return getTreeNodeInfo(position).getLevel();
  }

  @Override
  public int getViewTypeCount() {
    return numberOfLevels;
  }

  @Override
  public boolean isEmpty() {
    return getCount() == 0;
  }

  @Override
  public boolean areAllItemsEnabled() { // NOPMD
    return true;
  }

  @Override
  public boolean isEnabled(final int position) { // NOPMD
    return true;
  }

  protected int getTreeListItemWrapperId() {
    return R.layout.tree_list_item_wrapper;
  }

  /**
   * 你的item布局宽度
   * 
   * @return
   */
  protected abstract int getLayoutWidth();

  protected int getItemWidth() {
    return getIndentWidth() * (isCollapsible() ? (maxLevel + 1) : maxLevel) + getLayoutWidth() + itemPadding * 2;
  }

  @Override
  public final View getView(final int position, View convertView, ViewGroup parent) {
    Log.d(TAG, "Creating a view based on " + convertView + " with position " + position);
    final TreeNodeInfo<T> nodeInfo = getTreeNodeInfo(position);
    if (position == 0 && isAutoAdapt) {
      getMaxLevel();
    }
    if (convertView == null || (isAutoAdapt && maxLevel != oldMaxLevel)) {
      Log.d(TAG, "Creating the view a new");
      convertView = layoutInflater.inflate(getTreeListItemWrapperId(), null);
      oldMaxLevel = maxLevel;
      return populateTreeItem(convertView, getNewChildView(nodeInfo), nodeInfo, true);
    } else {
      Log.d(TAG, "Reusing the view");
      FrameLayout frameLayout = (FrameLayout) convertView.findViewById(R.id.treeview_list_item_frame);
      View childView = frameLayout.getChildAt(0);
      updateView(childView, nodeInfo);
      return populateTreeItem(convertView, childView, nodeInfo, false);
    }
  }

  /**
   * Called when new view is to be created.
   * 
   * @param treeNodeInfo
   *          node info
   * @return view that should be displayed as tree content
   */
  public abstract View getNewChildView(TreeNodeInfo<T> treeNodeInfo);

  /**
   * Called when new view is going to be reused. You should update the view and
   * fill it in with the data required to display the new information. You can
   * also create a new view, which will mean that the old view will not be
   * reused.
   * 
   * @param view
   *          view that should be updated with the new values
   * @param treeNodeInfo
   *          node info used to populate the view
   * @return view to used as row indented content
   */
  public abstract View updateView(View view, TreeNodeInfo<T> treeNodeInfo);

  /**
   * Retrieves background drawable for the node.
   * 
   * @param treeNodeInfo
   *          node info
   * @return drawable returned as background for the whole row. Might be null,
   *         then default background is used
   */
  public abstract Drawable getBackgroundDrawable(final TreeNodeInfo<T> treeNodeInfo);

  public final View populateTreeItem(View view, View childView, final TreeNodeInfo<T> nodeInfo,
      final boolean newChildView) {
    backgroundDrawable = getBackgroundDrawable(nodeInfo);
    if(null != backgroundDrawable){
//      view.setBackground(backgroundDrawable);
    	view.setBackgroundDrawable(backgroundDrawable);
    }else {
//      view.setBackground(null);
    	view.setBackgroundDrawable(null);
    }
    LinearLayout layout = (LinearLayout) view.findViewById(R.id.layout1);
    layout.setPadding(itemPadding, 0, itemPadding, 0);
    if (isAutoAdapt) {
      LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) layout.getLayoutParams();
      layoutParams.width = getItemWidth();
    }
    LinearLayout indicatorLayout = (LinearLayout) view.findViewById(R.id.treeview_list_item_image_layout);
    LinearLayout.LayoutParams indicatorLayoutParams = (android.widget.LinearLayout.LayoutParams) indicatorLayout
        .getLayoutParams();
    indicatorLayoutParams.width = calculateIndentation(nodeInfo);
    indicatorLayout.setGravity(indicatorGravity);
    ImageView image = (ImageView) view.findViewById(R.id.treeview_list_item_image);
    image.setImageDrawable(getDrawable(nodeInfo));
    image.setScaleType(ScaleType.CENTER);
    image.setTag(nodeInfo.getId());
//    if (nodeInfo.isWithChildren() && collapsible) {
//      image.setOnClickListener(indicatorClickListener);
//    } else {
//      image.setOnClickListener(null);
//    }
    image.setOnClickListener(mIndicatorClickListener);
    view.setTag(nodeInfo.getId());
    FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.treeview_list_item_frame);
    if (newChildView) {
      FrameLayout.LayoutParams childParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
          FrameLayout.LayoutParams.WRAP_CONTENT);
      frameLayout.addView(childView, childParams);
    }
    frameLayout.setTag(nodeInfo.getId());
    return view;
  }

  protected int calculateIndentation(final TreeNodeInfo<T> nodeInfo) {
    return getIndentWidth() * (nodeInfo.getLevel() + (collapsible ? 1 : 0));
  }

  protected Drawable getDrawable(final TreeNodeInfo<T> nodeInfo) {
//    if (!nodeInfo.isWithChildren() || !collapsible) {
//      return null;
//    }
    if (nodeInfo.isExpanded()) {
      return expandedDrawable;
    } else {
      return collapsedDrawable;
    }
  }

  public void setIndicatorGravity(final int indicatorGravity) {
    this.indicatorGravity = indicatorGravity;
  }

  public void setCollapsedDrawable(final Drawable collapsedDrawable) {
    this.collapsedDrawable = collapsedDrawable;
    calculateIndentWidth();
  }

  public void setExpandedDrawable(final Drawable expandedDrawable) {
    this.expandedDrawable = expandedDrawable;
    calculateIndentWidth();
  }

  public void setIndentWidth(final int indentWidth) {
    this.indentWidth = indentWidth;
    calculateIndentWidth();
  }

  public void setCollapsible(final boolean collapsible) {
    this.collapsible = collapsible;
  }

  public boolean isCollapsible() {
    return collapsible;
  }

  public void refresh() {
    treeStateManager.refresh();
  }

  protected int getIndentWidth() {
    return indentWidth;
  }

  @SuppressWarnings("unchecked")
  public void handleItemClick(final View view, final Object id) {
    expandCollapse((T) id);

  }

}
