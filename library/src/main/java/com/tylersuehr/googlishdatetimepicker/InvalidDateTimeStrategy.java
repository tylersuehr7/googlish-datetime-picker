package com.tylersuehr.googlishdatetimepicker;

import android.widget.TextView;

/**
 * Copyright © 2017 Tyler Suehr
 *
 * Strategy to handle performing operations for invalid dates and times
 * used by the {@link DateTimePickerLayout}.
 *
 * @author Tyler Suehr
 * @version 1.0
 */
public interface InvalidDateTimeStrategy {
    void onInvalidDate(DateTimePickerLayout parent, TextView view);
    void onInvalidTime(DateTimePickerLayout parent, TextView view);
}