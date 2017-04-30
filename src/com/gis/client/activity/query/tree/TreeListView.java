package com.gis.client.activity.query.tree;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.gis.client.R;
import com.gis.client.activity.query.tree.adapter.AbstractTreeViewAdapter;
import com.gis.client.activity.query.tree.util.TreeConfigurationException;


/**
 * Tree view, expandable multi-level.
 * 
 * <pre>
 * attr ref pl.polidea.treeview.R.styleable#TreeViewList_collapsible
 * attr ref pl.polidea.treeview.R.styleable#TreeViewList_src_expanded
 * attr ref pl.polidea.treeview.R.styleable#TreeViewList_src_collapsed
 * attr ref pl.polidea.treeview.R.styleable#TreeViewList_indent_width
 * attr ref pl.polidea.treeview.R.styleable#TreeViewList_handle_trackball_press
 * attr ref pl.polidea.treeview.R.styleable#TreeViewList_indicator_gravity
 * attr ref pl.polidea.treeview.R.styleable#TreeViewList_indicator_background
 * attr ref pl.polidea.treeview.R.styleable#TreeViewList_row_background
 * </pre>
 */
public class TreeListView extends ListView {
  private static final int DEFAULT_COLLAPSED_RESOURCE = R.drawable.tree_out_img_pressed;
  private static final int DEFAULT_EXPANDED_RESOURCE = R.drawable.tree_in_img_pressed;
  private static final int DEFAULT_INDENT = 0;
  private static final int DEFAULT_GRAVITY = Gravity.LEFT | Gravity.CENTER_VERTICAL;
  private Drawable expandedDrawable;
  private Drawable collapsedDrawable;
  private int indentWidth = 0;
  private int indicatorGravity = 0;
  private AbstractTreeViewAdapter<?> treeAdapter;
  private boolean collapsible;
  private boolean handleTrackballPress;
  private boolean isAutoAdapt;

  public TreeListView(final Context context, final AttributeSet attrs) {
    // this(context, attrs, 0);
    super(context, attrs);
    parseAttributes(context, attrs);
    // this(context, attrs, R.style.treeViewListStyle);
  }

  public TreeListView(final Context context) {
    this(context, null);
  }

  public TreeListView(final Context context, final AttributeSet attrs, final int defStyle) {
    super(context, attrs, defStyle);
    parseAttributes(context, attrs);
  }

  private void parseAttributes(final Context context, final AttributeSet attrs) {
    final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TreeListView);
    expandedDrawable = a.getDrawable(R.styleable.TreeListView_src_expanded);
    if (expandedDrawable == null) {
      expandedDrawable = context.getResources().getDrawable(DEFAULT_EXPANDED_RESOURCE);
    }
    collapsedDrawable = a.getDrawable(R.styleable.TreeListView_src_collapsed);
    if (collapsedDrawable == null) {
      collapsedDrawable = context.getResources().getDrawable(DEFAULT_COLLAPSED_RESOURCE);
    }
    indentWidth = a.getDimensionPixelSize(R.styleable.TreeListView_indent_width, DEFAULT_INDENT);
    indicatorGravity = a.getInteger(R.styleable.TreeListView_indicator_gravity, DEFAULT_GRAVITY);
    collapsible = a.getBoolean(R.styleable.TreeListView_collapsible, true);
    handleTrackballPress = a.getBoolean(R.styleable.TreeListView_handle_trackball_press, true);
    isAutoAdapt = a.getBoolean(R.styleable.TreeListView_autoAdapt, false);
  }

  @Override
  public void setAdapter(final ListAdapter adapter) {
    if (!(adapter instanceof AbstractTreeViewAdapter)) {
      throw new TreeConfigurationException("The adapter is not of TreeViewAdapter type");
    }
    treeAdapter = (AbstractTreeViewAdapter<?>) adapter;
    syncAdapter();
    super.setAdapter(treeAdapter);
  }

  private void syncAdapter() {
    treeAdapter.setCollapsedDrawable(collapsedDrawable);
    treeAdapter.setExpandedDrawable(expandedDrawable);
    treeAdapter.setIndicatorGravity(indicatorGravity);
    treeAdapter.setIndentWidth(indentWidth);
    treeAdapter.setCollapsible(collapsible);
    treeAdapter.setAutoAdapt(isAutoAdapt);
    if (handleTrackballPress) {
      setOnItemClickListener(new OnItemClickListener() {
        @Override
        public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
          treeAdapter.handleItemClick(view, view.getTag());
        }
      });
    } else {
      setOnItemClickListener(null);
    }

  }

  public void setExpandedDrawable(final Drawable expandedDrawable) {
    this.expandedDrawable = expandedDrawable;
    syncAdapter();
    treeAdapter.refresh();
  }

  public void setCollapsedDrawable(final Drawable collapsedDrawable) {
    this.collapsedDrawable = collapsedDrawable;
    syncAdapter();
    treeAdapter.refresh();
  }

  public void setIndentWidth(final int indentWidth) {
    this.indentWidth = indentWidth;
    syncAdapter();
    treeAdapter.refresh();
  }

  public void setIndicatorGravity(final int indicatorGravity) {
    this.indicatorGravity = indicatorGravity;
    syncAdapter();
    treeAdapter.refresh();
  }

  public void setCollapsible(final boolean collapsible) {
    this.collapsible = collapsible;
    syncAdapter();
    treeAdapter.refresh();
  }

  public void setHandleTrackballPress(final boolean handleTrackballPress) {
    this.handleTrackballPress = handleTrackballPress;
    syncAdapter();
    treeAdapter.refresh();
  }

  public Drawable getExpandedDrawable() {
    return expandedDrawable;
  }

  public Drawable getCollapsedDrawable() {
    return collapsedDrawable;
  }

  public int getIndentWidth() {
    return indentWidth;
  }

  public int getIndicatorGravity() {
    return indicatorGravity;
  }

  public boolean isCollapsible() {
    return collapsible;
  }

  public boolean isHandleTrackballPress() {
    return handleTrackballPress;
  }

}
