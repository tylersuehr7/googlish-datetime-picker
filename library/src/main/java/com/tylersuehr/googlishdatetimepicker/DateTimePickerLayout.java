package com.tylersuehr.googlishdatetimepicker;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
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
 * This is a view widget, like the one in the Google Calendar app, that allows
 * the current user to choose both a date and time (datetime) and stores it as
 * a {@link Calendar} object.
 *
 * By default, this adheres to Google Material Design principles, but it can be
 * adjusted to whatever and is very customizable. It also includes cool features like
 * validation and a datetime choose listener!
 *
 * @author Tyler Suehr
 * @version 1.0
 */
public class DateTimePickerLayout extends ViewGroup {
    /* Stores 16dp measurement */
    private final int mSixteenDp;

    /* Stores the display format for when user chooses a date */
    private DateFormat mDateFormat;
    /* Stores the display format for when user chooses a time */
    private DateFormat mTimeFormat;

    private TextView mDateView;
    private TextView mTimeView;

    private CharSequence mDefaultDateText;
    private CharSequence mDefaultTimeText;
    private CharSequence mDefaultDateErrorText;
    private CharSequence mDefaultTimeErrorText;

    private Typeface mTypeface = Typeface.DEFAULT;
    private int mTextPadding;
    private int mTextColor;

    private int mIconSize;
    private int mIconColor;
    private Drawable mIcon;
    /* 1 = show icon, 2 = use icon spacing */
    private byte mIconFlags = 0;

    /* Stores the date and time chosen */
    private Calendar mChosenDate;
    private boolean mDateChosen;
    private boolean mTimeChosen;

    @NonNull
    private DateTimeValidator mValidator =
            new DefaultDateTimeValidator();
    private boolean mAutoValidate;

    /* Store a listener for datetime events */
    private OnDateTimeChooseListener mListener;


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

        // Set XML attributes
        final TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.DateTimePickerLayout);
        String dateFormStr = a.getString(R.styleable.DateTimePickerLayout_dtp_dateFormat);
        if (TextUtils.isEmpty(dateFormStr)) {
            dateFormStr = "EEE, MMM dd, yyyy";
        }
        mDateFormat = new SimpleDateFormat(dateFormStr, Locale.US);

        String timeFormStr = a.getString(R.styleable.DateTimePickerLayout_dtp_timeFormat);
        if (TextUtils.isEmpty(timeFormStr)) {
            timeFormStr = "h:mm aa";
        }
        mTimeFormat = new SimpleDateFormat(timeFormStr, Locale.US);

        mTextPadding = a.getDimensionPixelSize(R.styleable.DateTimePickerLayout_dtp_textMargin, mSixteenDp);
        mDefaultDateText = a.hasValue(R.styleable.DateTimePickerLayout_dtp_defaultDate)
                ? a.getText(R.styleable.DateTimePickerLayout_dtp_defaultDate)
                : "Choose Date";
        mDefaultTimeText = a.hasValue(R.styleable.DateTimePickerLayout_dtp_defaultTime)
                ? a.getText(R.styleable.DateTimePickerLayout_dtp_defaultTime)
                : "Choose Time";
        mDefaultDateErrorText = a.hasValue(R.styleable.DateTimePickerLayout_dtp_defaultDateError)
                ? a.getText(R.styleable.DateTimePickerLayout_dtp_defaultDateError)
                : "Invalid Date!";
        mDefaultTimeErrorText = a.hasValue(R.styleable.DateTimePickerLayout_dtp_defaultTimeError)
                ? a.getText(R.styleable.DateTimePickerLayout_dtp_defaultTimeError)
                : "Invalid Time!";
        mTextColor = a.getColor(R.styleable.DateTimePickerLayout_android_textColor, Color.BLACK);

        mIconSize = a.getDimensionPixelSize(R.styleable.DateTimePickerLayout_dtp_iconSize, (int)(24f * dm.density));
        mIconColor = a.getColor(R.styleable.DateTimePickerLayout_dtp_iconColor, Color.BLACK);
        mIcon = a.getDrawable(R.styleable.DateTimePickerLayout_dtp_icon);
        if (mIcon == null) {
            mIcon = ContextCompat.getDrawable(c, R.drawable.ic_default_datetime_picker_24dp);
        }
        mIcon.setBounds(0, 0, mIconSize, mIconSize);
        mIcon.setColorFilter(mIconColor, PorterDuff.Mode.SRC_ATOP);

        mAutoValidate = a.getBoolean(R.styleable.DateTimePickerLayout_dtp_enableAutoValidate, false);
        setShowIcon(a.getBoolean(R.styleable.DateTimePickerLayout_dtp_showIcon, true));
        setShowIconSpacing(a.getBoolean(R.styleable.DateTimePickerLayout_dtp_showIconSpacing, true));
        a.recycle();

        // Setup the date text view
        mDateView = createTextView();
        mDateView.setText(mDefaultDateText);
        mDateView.setTextColor(mTextColor);
        addView(mDateView);

        // Setup the time text view
        mTimeView = createTextView();
        mTimeView.setText(mDefaultTimeText);
        mTimeView.setTextColor(mTextColor);
        addView(mTimeView);
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
        if (adjustForIcon()) {
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
        if ((mIconFlags&1)==1) {
            drawIcon(canvas);
        }
    }

    /**
     * Checks if the current chose datetime is valid using
     * the validator {@link #mValidator}.
     *
     * @return True if chosen datetime is valid
     */
    public boolean isValidDateTime() {
        // Use the validator to validate date and time
        if (!mValidator.validateDate(mChosenDate)) {
            mValidator.onDateInvalid(this);
            return false;
        } else if (!mValidator.validateTime(mChosenDate)) {
            mValidator.onTimeInvalid(this);
            return false;
        }
        return true;
    }

    /**
     * Gets the chosen datetime.
     * @return {@link Calendar}
     */
    public Calendar getChosenDateTime() {
        return mChosenDate;
    }

    /**
     * Clears both the chosen date and time and resets
     * all the values set by them.
     */
    public void clearChosenDateTime() {
        mChosenDate = null;
        mDateChosen = false;
        mTimeChosen = false;

        mDateView.setText(mDefaultDateText);
        mTimeView.setText(mDefaultTimeText);

        mDateView.setError(null);
        mTimeView.setError(null);
    }

    /**
     * Sets a pre-selected default datetime on this widget.
     * @param defaultDateTime {@link Calendar}
     */
    public void setDefaultDateTime(Calendar defaultDateTime) {
        if (defaultDateTime == null) { return; }
        mChosenDate = defaultDateTime;

        mDateView.setText(mDateFormat.format(mChosenDate.getTime()));
        mTimeView.setText(mTimeFormat.format(mChosenDate.getTime()));

        mDateChosen = true;
        mTimeChosen = true;
    }

    /**
     * Visibly shows the icon {@link #mIcon}.
     * Note: True will cause adjustment for the needed spacing.
     *
     * @param show True if icon show be visibly shown
     */
    public void setShowIcon(boolean show) {
        if (show) {
            mIconFlags |= 1;
        } else {
            mIconFlags &= ~1;
        }
    }

    /**
     * Makes adjustment for the needed spacing for the icon to
     * be shown, but does NOT make the icon visible.
     *
     * @param show True if should adjust for icon spacing
     */
    public void setShowIconSpacing(boolean show) {
        if (show) {
            mIconFlags |= 2;
        } else {
            mIconFlags &= ~2;
        }
    }

    /**
     * Performs validation as soon as the user chooses a date
     * or time.
     *
     * @param autoValidate True if should auto validate
     */
    public void setAutoValidate(boolean autoValidate) {
        mAutoValidate = autoValidate;
    }

    public boolean isShowIcon() {
        return (mIconFlags&1)==1;
    }

    public boolean isShowIconSpacing() {
        return (mIconFlags & 2) == 2;
    }

    public boolean isAutoValidate() {
        return mAutoValidate;
    }

    public DateFormat getDateFormat() {
        return mDateFormat;
    }

    public void setDateFormat(DateFormat format) {
        mDateFormat = format;
    }

    public DateFormat getTimeFormat() {
        return mTimeFormat;
    }

    public void setTimeFormat(DateFormat format) {
        mTimeFormat = format;
    }

    public void setDefaultDateText(String text) {
        mDefaultDateText = text;
        if (!mDateChosen) {
            mDateView.setText(mDefaultDateText);
        }
    }

    public void setDefaultTimeText(String text) {
        mDefaultTimeText = text;
        if (!mTimeChosen) {
            mTimeView.setText(mDefaultTimeText);
        }
    }

    public void setDefaultDateErrorText(String text) {
        mDefaultDateErrorText = text;
    }

    public void setDefaultTimeErrorText(String text) {
        mDefaultTimeErrorText = text;
    }

    public Typeface getTypeface() {
        return mTypeface;
    }

    public void setTypeface(Typeface typeface) {
        mTypeface = typeface;
        mDateView.setTypeface(mTypeface);
        mTimeView.setTypeface(mTypeface);
    }

    public int getTextPadding() {
        return mTextPadding;
    }

    public void setTextPadding(int textPadding) {
        mTextPadding = textPadding;
        mDateView.setPadding(mTextPadding, mTextPadding,
                mTextPadding, mTextPadding);
        mTimeView.setPadding(mTextPadding, mTextPadding,
                mTextPadding, mTextPadding);
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(@ColorInt int color) {
        mTextColor = color;
        mDateView.setTextColor(color);
        mTimeView.setTextColor(color);
    }

    public int getIconSize() {
        return mIconSize;
    }

    public void setIconSize(int iconSize) {
        mIconSize = iconSize;
        mIcon.setBounds(0, 0, mIconSize, mIconSize);
        invalidate();
    }

    public Drawable getIcon() {
        return mIcon;
    }

    public void setIcon(Drawable icon) {
        mIcon = icon;
        mIcon.setBounds(0, 0, mIconSize, mIconSize);
        mIcon.setColorFilter(mIconColor, PorterDuff.Mode.SRC_ATOP);
        invalidate();
    }

    public void setDateTimeValidator(@NonNull DateTimeValidator validator) {
        mValidator = validator;
    }

    public DateTimeValidator getDateTimeValidator() {
        return mValidator;
    }

    public void setOnDateTimeChooseListener(OnDateTimeChooseListener listener) {
        mListener = listener;
    }

    public OnDateTimeChooseListener getOnDateTimeChooseListener() {
        return mListener;
    }

    private void drawIcon(final Canvas canvas) {
        canvas.save();
        canvas.translate(
                ViewCompat.getPaddingStart(mDateView),
                mDateView.getPaddingTop());
        mIcon.draw(canvas);
        canvas.restore();
    }

    private int getNeededWidth() {
        int neededSize = 0;

        // If we're showing the icon, we want to adhere to Material
        // Design spacing principles... iconSize + 24dp
        if (adjustForIcon()) {
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

    private boolean adjustForIcon() {
        return (mIconFlags&1)==1 || (mIconFlags&2)==2;
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
        tv.setPadding(mTextPadding, mTextPadding,
                mTextPadding, mTextPadding);
        tv.setLayoutParams(new ViewGroup.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        ));

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

                                // Validate chosen date, if possible
                                if (mAutoValidate) {
                                    if (!mValidator.validateDate(mChosenDate)) {
                                        mDateChosen = false;
                                        mValidator.onDateInvalid(DateTimePickerLayout.this);
                                        return;
                                    }
                                }

                                mDateView.setError(null);

                                // Display the formatted chosen date
                                mDateView.setText(mDateFormat.format(mChosenDate.getTime()));

                                // Invoke listener, if possible
                                if (mListener != null) {
                                    mListener.onDateChosen(mChosenDate);
                                }
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

                                // Validate chosen time, if possible
                                if (mAutoValidate) {
                                    if (!mValidator.validateTime(mChosenDate)) {
                                        mTimeChosen = false;
                                        mValidator.onTimeInvalid(DateTimePickerLayout.this);
                                        return;
                                    }
                                }

                                mTimeView.setError(null);

                                // Display the formatted chosen time
                                mTimeView.setText(mTimeFormat.format(mChosenDate.getTime()));

                                // Invoke listener, if possible
                                if (mListener != null) {
                                    mListener.onTimeChosen(mChosenDate);
                                }
                            }
                        },
                        mChosenDate.get(Calendar.HOUR),
                        mChosenDate.get(Calendar.MINUTE),
                        false).show();
            }
        }
    };



    /**
     * Nested inner-class implementation of {@link DateTimeValidator}
     * that affords basic validation.
     */
    private final class DefaultDateTimeValidator implements DateTimeValidator {
        @Override
        public boolean validateDate(@Nullable Calendar chosenDate) {
            if (!mDateChosen || chosenDate == null) {
                return false;
            }

            // Make sure to only compare date and not time
            // so that user can pick the current day
            final Calendar now = Calendar.getInstance();
            now.clear(Calendar.HOUR_OF_DAY);
            now.clear(Calendar.MINUTE);
            now.clear(Calendar.SECOND);

            final Calendar temp = (Calendar)mChosenDate.clone();
            temp.clear(Calendar.HOUR_OF_DAY);
            temp.clear(Calendar.MINUTE);
            temp.clear(Calendar.SECOND);

            return temp.after(now) || temp.equals(now);
        }

        @Override
        public boolean validateTime(@Nullable Calendar chosenTime) {
            if (!mTimeChosen || chosenTime == null) {
                return false;
            }

            final Calendar now = Calendar.getInstance();
            return mChosenDate.after(now);
        }

        @Override
        public void onDateInvalid(@NonNull DateTimePickerLayout view) {
            // Display error on the date text view
            mDateView.setError("Date is invalid!");
            mDateView.setText(mDefaultDateErrorText);
        }

        @Override
        public void onTimeInvalid(@NonNull DateTimePickerLayout view) {
            // Display error on the time text view
            mTimeView.setError("Time is invalid!");
            mTimeView.setText(mDefaultTimeErrorText);
        }
    }
}