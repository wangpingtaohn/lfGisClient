package com.gis.client.common;

import java.text.NumberFormat;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gis.client.R;

/**
 * User: liujun
 */
public class CommonProgressDialog extends AlertDialog {
	/**
	 * Creates a ProgressDialog with a circular, spinning progress bar. This is
	 * the default.
	 */
	public static final int STYLE_SPINNER = 0;

	/**
	 * Creates a ProgressDialog with a horizontal progress bar.
	 */
	public static final int STYLE_HORIZONTAL = 1;

	private static final Formatter DEFAULT_FORMATTER = new Formatter() {
		public String format(int progress, int max) {
			return String.format("%d/%d", progress, max);
		}
	};

	private ProgressBar mProgress;
	private TextView mMessageView;
	private Context mContext;

	private int mProgressStyle = STYLE_SPINNER;
	private TextView mProgressNumber;
	private Formatter mProgressNumberFormatter;
	private TextView mProgressPercent;
	private NumberFormat mProgressPercentFormat;

	private int mMax;
	private int mProgressVal;
	private int mSecondaryProgressVal;
	private int mIncrementBy;
	private int mIncrementSecondaryBy;
	private Drawable mProgressDrawable;
	private Drawable mIndeterminateDrawable;
	private CharSequence mMessage;
	private boolean mIndeterminate;

	private boolean mHasStarted;
	private Handler mViewUpdateHandler;

	public CommonProgressDialog(Context context) {
		this(context, R.style.Theme_Dialog_Alert);
	}

	public CommonProgressDialog(Context context, int theme) {
		super(context, theme);
		mContext = context;
		mProgressNumberFormatter = DEFAULT_FORMATTER;

	}

	public static CommonProgressDialog show(Context context,
			CharSequence title, CharSequence message) {
		return show(context, title, message, false);
	}

	public static CommonProgressDialog show(Context context,
			CharSequence title, CharSequence message, boolean indeterminate) {
		return show(context, title, message, indeterminate, false, null);
	}

	public static CommonProgressDialog show(Context context,
			CharSequence title, CharSequence message, boolean indeterminate,
			boolean cancelable) {
		return show(context, title, message, indeterminate, cancelable, null);
	}

	public static CommonProgressDialog show(Context context,
			CharSequence title, CharSequence message, boolean indeterminate,
			boolean cancelable, OnCancelListener cancelListener) {
		CommonProgressDialog dialog = new CommonProgressDialog(context);
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.setIndeterminate(indeterminate);
		dialog.setCancelable(cancelable);
		dialog.setOnCancelListener(cancelListener);
		dialog.show();
		return dialog;
	}

	public static CommonProgressDialog show(Context context, int titleId,
			CharSequence message) {
		return show(context, titleId, message, false);
	}

	public static CommonProgressDialog show(Context context, int titleId,
			CharSequence message, boolean indeterminate) {
		return show(context, titleId, message, indeterminate, false, null);
	}

	public static CommonProgressDialog show(Context context, int titleId,
			CharSequence message, boolean indeterminate, boolean cancelable) {
		return show(context, titleId, message, indeterminate, cancelable, null);
	}

	public static CommonProgressDialog show(Context context, int titleId,
			CharSequence message, boolean indeterminate, boolean cancelable,
			OnCancelListener cancelListener) {
		CommonProgressDialog dialog = new CommonProgressDialog(context);
		dialog.setTitle(titleId);
		dialog.setMessage(message);
		dialog.setIndeterminate(indeterminate);
		dialog.setCancelable(cancelable);
		dialog.setOnCancelListener(cancelListener);
		dialog.show();
		return dialog;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		if (mProgressStyle == STYLE_HORIZONTAL) {

			/*
			 * Use a separate handler to update the text views as they must be
			 * updated on the same thread that created them.
			 */
			mViewUpdateHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);

					/* Update the number and percent */
					int progress = mProgress.getProgress();
					int max = mProgress.getMax();
					if (max == 0)
						max = 100;
					double percent = (double) progress / (double) max;
					mProgressNumber.setText(mProgressNumberFormatter.format(
							progress, max));
					SpannableString tmp = new SpannableString(
							mProgressPercentFormat.format(percent));
					tmp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
							0, tmp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					mProgressPercent.setText(tmp);
				}
			};
			View view = inflater.inflate(R.layout.alert_dialog_progress, null);
			mProgress = (ProgressBar) view.findViewById(R.id.progress);
			mProgressNumber = (TextView) view
					.findViewById(R.id.progress_number);
			mProgressPercent = (TextView) view
					.findViewById(R.id.progress_percent);
			mProgressPercentFormat = NumberFormat.getPercentInstance();
			mProgressPercentFormat.setMaximumFractionDigits(0);
			setView(view, 0, 0, 0, 0);
		} else {
			View view = inflater.inflate(R.layout.progress_dialog, null);
			mProgress = (ProgressBar) view.findViewById(R.id.progress);
			mMessageView = (TextView) view.findViewById(R.id.message);
			setView(view, 0, 0, 0, 0);
		}
		if (mMax > 0) {
			setMax(mMax);
		}
		if (mProgressVal > 0) {
			setProgress(mProgressVal);
		}
		if (mSecondaryProgressVal > 0) {
			setSecondaryProgress(mSecondaryProgressVal);
		}
		if (mIncrementBy > 0) {
			incrementProgressBy(mIncrementBy);
		}
		if (mIncrementSecondaryBy > 0) {
			incrementSecondaryProgressBy(mIncrementSecondaryBy);
		}
		if (mProgressDrawable != null) {
			setProgressDrawable(mProgressDrawable);
		}
		if (mIndeterminateDrawable != null) {
			setIndeterminateDrawable(mIndeterminateDrawable);
		}
		if (mMessage != null) {
			setMessage(mMessage);
		}
		setIndeterminate(mIndeterminate);
		onProgressChanged();
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
		mHasStarted = true;
	}

	@Override
	protected void onStop() {
		super.onStop();
		mHasStarted = false;
	}

	public void setProgress(int value) {
		if (mHasStarted) {
			mProgress.setProgress(value);
			onProgressChanged();
		} else {
			mProgressVal = value;
		}
	}

	public void setSecondaryProgress(int secondaryProgress) {
		if (mProgress != null) {
			mProgress.setSecondaryProgress(secondaryProgress);
			onProgressChanged();
		} else {
			mSecondaryProgressVal = secondaryProgress;
		}
	}

	public int getProgress() {
		if (mProgress != null) {
			return mProgress.getProgress();
		}
		return mProgressVal;
	}

	public int getSecondaryProgress() {
		if (mProgress != null) {
			return mProgress.getSecondaryProgress();
		}
		return mSecondaryProgressVal;
	}

	public int getMax() {
		if (mProgress != null) {
			return mProgress.getMax();
		}
		return mMax;
	}

	public void setMax(int max) {
		if (mProgress != null) {
			mProgress.setMax(max);
			onProgressChanged();
		} else {
			mMax = max;
		}
	}

	public void incrementProgressBy(int diff) {
		if (mProgress != null) {
			mProgress.incrementProgressBy(diff);
			onProgressChanged();
		} else {
			mIncrementBy += diff;
		}
	}

	public void incrementSecondaryProgressBy(int diff) {
		if (mProgress != null) {
			mProgress.incrementSecondaryProgressBy(diff);
			onProgressChanged();
		} else {
			mIncrementSecondaryBy += diff;
		}
	}

	public void setProgressDrawable(Drawable d) {
		if (mProgress != null) {
			mProgress.setProgressDrawable(d);
		} else {
			mProgressDrawable = d;
		}
	}

	public void setIndeterminateDrawable(Drawable d) {
		if (mProgress != null) {
			mProgress.setIndeterminateDrawable(d);
		} else {
			mIndeterminateDrawable = d;
		}
	}

	public void setIndeterminate(boolean indeterminate) {
		if (mProgress != null) {
			mProgress.setIndeterminate(indeterminate);
		} else {
			mIndeterminate = indeterminate;
		}
	}

	public boolean isIndeterminate() {
		if (mProgress != null) {
			return mProgress.isIndeterminate();
		}
		return mIndeterminate;
	}

	@Override
	public void setMessage(CharSequence message) {
		if (mProgress != null) {
			if (mProgressStyle == STYLE_HORIZONTAL) {
				super.setMessage(message);
			} else {
				mMessageView.setText(message);
			}
		} else {
			mMessage = message;
		}
	}

	public void setProgressStyle(int style) {
		mProgressStyle = style;
	}

	/**
	 * Change the format of Progress Number. The default is "current/max".
	 * Should not be called during the number is progressing. and the second is
	 * used for the maximum.
	 * 
	 * @param formatter
	 */
	public void setProgressNumberFormatter(Formatter formatter) {
		mProgressNumberFormatter = formatter;
	}

	private void onProgressChanged() {
		if (mProgressStyle == STYLE_HORIZONTAL) {
			mViewUpdateHandler.sendEmptyMessage(0);
		}
	}

	public static interface Formatter {
		public String format(int progress, int max);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (this != null && this.isShowing()) {
			this.dismiss();
		}
	}
}
