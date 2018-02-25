package com.tylersuehr.googlishdatetimepicker;

import android.app.TimePickerDialog;
import android.content.Context;

/**
 * Copyright © 2017 Tyler Suehr
 *
 * Strategy to handle creating the time picker dialog used by the
 * {@link DateTimePickerLayout}.
 *
 * @author Tyler Suehr
 * @version 1.0
 */
public interface TimePickerCreateStrategy {
    TimePickerDialog createPicker(Context c, TimePickerDialog.OnTimeSetListener listener);
}