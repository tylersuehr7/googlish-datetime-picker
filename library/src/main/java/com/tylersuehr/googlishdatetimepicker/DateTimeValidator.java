package com.tylersuehr.googlishdatetimepicker;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Calendar;

/**
 * Copyright © 2017 Tyler Suehr
 *
 * Defines a way to valid the date and time.
 *
 * @author Tyler Suehr
 * @version 1.0
 */
public interface DateTimeValidator {
    boolean validateDate(@Nullable Calendar chosenDate);
    boolean validateTime(@Nullable Calendar chosenTime);
    void onDateInvalid(@NonNull DateTimePickerLayout view);
    void onTimeInvalid(@NonNull DateTimePickerLayout view);
}