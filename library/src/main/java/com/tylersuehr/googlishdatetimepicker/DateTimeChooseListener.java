package com.tylersuehr.googlishdatetimepicker;

import android.support.annotation.NonNull;

import java.util.Calendar;

/**
 * Copyright © 2017 Tyler Suehr
 *
 * Callbacks for datetime choosing events.
 *
 * @author Tyler Suehr
 * @version 1.0
 */
public interface DateTimeChooseListener {
    void onDateChosen(@NonNull Calendar chosenDate);
    void onTimeChosen(@NonNull Calendar chosenTime);
}