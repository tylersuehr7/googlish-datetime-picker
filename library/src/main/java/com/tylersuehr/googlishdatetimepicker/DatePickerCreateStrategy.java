package com.tylersuehr.googlishdatetimepicker;

import android.app.DatePickerDialog;
import android.content.Context;

/**
 * Copyright © 2017 Tyler Suehr
 *
 * Strategy to handle creating the date picker dialog used by the
 * {@link DateTimePickerLayout}.
 *
 * @author Tyler Suehr
 * @version 1.0
 */
public interface DatePickerCreateStrategy {
    DatePickerDialog createPicker(Context c, DatePickerDialog.OnDateSetListener listener);
}