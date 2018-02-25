package com.tylersuehr.googlishdatetimepicker;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Copyright © 2017 Tyler Suehr
 *
 * @author Tyler Suehr
 * @version 1.0
 */
public class DateTimePickerLayout extends ViewGroup {
    /* Stores 16dp measurement */
    private final int mSixteenDp;

    /* Stores the display format for when user chooses a date */
    private DateFormat mDateFormat =
            new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.US);
    /* Stores the display format for when user chooses a time */
    private DateFormat mTimeFormat =
            new SimpleDateFormat("h:mm aa", Locale.US);

    private Typeface mTypeface = Typeface.DEFAULT;
    private TextView mDateView;
    private TextView mTimeView;

    private int mIconSize;
    private Drawable mIcon;
    private boolean mShowIcon = true;

    /* Stores the date and time chosen */
    private Calendar mChosenDate;
    private boolean mDateChosen;
    private boolean mTimeChosen;


    public DateTimePickerLayout(Context context) {
        this(context, null);
    }

    public DateTimePickerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DateTimePickerLayout(Context c, AttributeSet attrs, int defStyleAttr) {
        super(c, attrs, defStyleAttr);
        final DisplayMetrics dm = getResources().getDisplayMetrics();
        mSixteenDp = (int)(16f * dm.density);

        // Defaults for date
        mDateView = createTextView();
        mDateView.setText("Choose Date");
        addView(mDateView);

        // Defaults for time
        mTimeView = createTextView();
        mTimeView.setText("Choose Time");
        addView(mTimeView);

        // Defaults for icon
        mIconSize = (int)(24f * dm.density);
        mIcon = ContextCompat.getDrawable(c, R.drawable.ic_default_datetime_picker_24dp);
        mIcon.setBounds(0, 0, mIconSize, mIconSize);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Measure children first... allow text views to do their
        // measurements as needed
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        // Account for the needed height only
        int heightSize = getNeededHeight() + getPaddingTop() + getPaddingBottom();

        // If no specific size is given, just use the needed width
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = 0;
        switch (widthMode) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                widthSize = getNeededWidth()
                        + ViewCompat.getPaddingStart(this)
                        + ViewCompat.getPaddingEnd(this);
                break;
            case MeasureSpec.EXACTLY:
                widthSize = MeasureSpec.getSize(widthMeasureSpec);
                break;
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // We want to specifically layout the date view to
        // the right of the drawn icon
        int left = ViewCompat.getPaddingStart(this);
        if (mShowIcon) {
            left += mIconSize + (mSixteenDp << 1);
        }
        int top = getPaddingTop();
        int right = left + mDateView.getMeasuredWidth();
        int bottom = top + mDateView.getMeasuredHeight();
        mDateView.layout(left, top, right, bottom);

        // We want to specifically layout the time view to
        // the far right of the parent
        left = getMeasuredWidth() - mTimeView.getMeasuredWidth() -
                ViewCompat.getPaddingEnd(this);
        right = left + mTimeView.getMeasuredWidth();
        bottom = top + mTimeView.getMeasuredHeight();
        mTimeView.layout(left, top, right, bottom);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mShowIcon) {
            drawIcon(canvas);
        }
    }

    private void drawIcon(final Canvas canvas) {
        canvas.save();
        canvas.translate(
                ViewCompat.getPaddingStart(this),
                getPaddingTop());
        mIcon.draw(canvas);
        canvas.restore();
    }

    private int getNeededWidth() {
        int neededSize = 0;

        // If we're showing the icon, we want to adhere to Material
        // Design spacing principles... iconSize + 24dp
        if (mShowIcon) {
            neededSize += mIconSize + (mSixteenDp << 1);
        }

        // We need to account for the needed width to display
        // the current text for the date and time views with
        // some space in between them
        neededSize += mDateView.getMeasuredWidth() + mSixteenDp +
                mTimeView.getMeasuredWidth();

        return neededSize;
    }

    private int getNeededHeight() {
        return Math.max(mIconSize, mDateView.getMeasuredHeight());
    }

    private TextView createTextView() {
        // Get the 'selectable item' background from attributes
        final Drawable dr;
        TypedArray a = getContext().obtainStyledAttributes(
                new int[] { R.attr.selectableItemBackground });
        dr = a.getDrawable(0);
        a.recycle();

        // Create the text view and set it up to be clickable
        final TextView tv = new TextView(getContext());
        tv.setLayoutParams(new ViewGroup.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        ));

//        tv.setPadding(mEightDp, mEightDp, mEightDp, mEightDp);

        tv.setClickable(true);
        tv.setBackground(dr);
        tv.setOnClickListener(mClickHandler);

        tv.setTypeface(mTypeface);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
        tv.setTextColor(Color.BLACK);

        return tv;
    }



    private final OnClickListener mClickHandler = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mChosenDate == null) {
                mChosenDate = Calendar.getInstance();
            }

            if (v == mDateView) { // Show the date picker dialog
                new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int month, int dayOfMonth) {
                                // Store the chosen date in the calendar
                                mChosenDate = Calendar.getInstance();
                                mChosenDate.set(Calendar.YEAR, year);
                                mChosenDate.set(Calendar.MONTH, month);
                                mChosenDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                mDateChosen = true;

                                // Display the formatted chosen date
                                mDateView.setText(mDateFormat.format(mChosenDate.getTime()));
                            }
                        },
                        mChosenDate.get(Calendar.YEAR),
                        mChosenDate.get(Calendar.MONTH),
                        mChosenDate.get(Calendar.DAY_OF_MONTH))
                        .show();
            } else { // Show the time picker
                new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // Store the chosen time in the calendar
                                mChosenDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                mChosenDate.set(Calendar.MINUTE, minute);
                                mTimeChosen = true;

                                // Display the formatted chosen time
                                mTimeView.setText(mTimeFormat.format(mChosenDate.getTime()));
                            }
                        },
                        mChosenDate.get(Calendar.HOUR),
                        mChosenDate.get(Calendar.MINUTE),
                        false).show();
            }
        }
    };
}