# Googlish Datetime Picker
A highly customizable Android ViewGroup that allows you to choose a date and time... similar to the picker in Google Calendar. This is a widget that I had made for one of my personal applications that I had decided to make open-source :D


<img src="https://github.com/tylersuehr7/googlish-datetime-picker/blob/master/img_screen1.png" width="200"> <img src="https://github.com/tylersuehr7/googlish-datetime-picker/blob/master/img_screen2.png" width="200"> <img src="https://github.com/tylersuehr7/googlish-datetime-picker/blob/master/img_screen3.png" width="200"> <img src="https://github.com/tylersuehr7/googlish-datetime-picker/blob/master/img_screen4.png" width="200">

## Usage
*Step 1: Add to root build.gradle file*
```java
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

*Step 2: Add to dependencies*
```java
dependencies {
    compile 'com.github.tylersuehr7:googlish-datetime-picker:1.1'
}
```

## Using the `DateTimePickerLayout`
The purpose of this library is to afford an easy-to-use datetime picker view widget that allows the user to chose a date and time respectively while being able to validate the chosen date or time. To achieve this functionality, the `DateTimePickerLayout` `ViewGroup` is used.

### Using in XML layout
`DateTimePickerLayout` can be used in any ViewGroup and supports all width and height attributes. Simple usage is shown here:
```xml
<com.tylersuehr.googlishdatetimepicker.DateTimePickerLayout
        android:id="@+id/datetime_picker"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:textColor="#212121"
        app:dtp_enableAutoValidation="true"/>
```

Here is a table of all the XML attributes available for this view:

Attribute | Type | Summary
--- | :---: | ---
`android:textColor` | `color` | Text color of the date and time displays.
`android:textSize` | `dimen` | Text size of the date and time displays.
`dtp_enableAutoValidation` | `boolean` | Enables validation as soon as a date or time is chosen.
`dtp_textMargin` | `dimen` | The spacing around the date and time displays.
`dtp_dateFormat` | `string` | The simple date format string used by the date display.
`dtp_timeFormat` | `string` | The simple date format string used by the time display.
`dtp_defaultDate` | `string` | The initial text of the date display. (*i.e.* "Choose Date").
`dtp_defaultTime` | `string` | The initial text of the time display. (*i.e.* "Choose Time").
`dtp_defaultDateError` | `string` | The text shown when a date is invalid after validation.
`dtp_defaultTimeError` | `string` | The text shown when a time is invalid after validation.
`dtp_icon` | `reference` | The drawable icon drawn on the left of the view.
`dtp_iconSize` | `dimen` | The size of the icon drawn on the left of the view.
`dtp_iconColor` | `color` | The color of the icon drawn on the left of the view.
`dtp_showIcon` | `boolean` | True if the icon should be shown.
`dtp_showIconSpacing` | `boolean` | True if displays should be aligned for icon.

### Using in Java code
`DateTimePickerLayout` can be programmatically added into any ViewGroup. Simple usage in an Activity is shown here:
```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    DateTimePickerLayout picker = new DateTimePickerLayout(this);
    // Set any properties for picker
    
    setContentView(picker);
}
```

Here is a table of all the accessible attributes available for this view:

Method | Summary
--- | ---
`setDateTimeValidator(DateTimeValidator)` | Sets the validator used for date and time validation by the picker.
`getDateTimeValidator()` | Returns the validator used for date and time validation by the picker.
`setOnDateTimeChooseListener(OnDateTimeChooseListener)` | Sets the listener for date and time choosing events.
`getOnDateTimeChooseListener()` | Returns the listener used for date and time choosing events.
`setDatePickerCreateStrategy(DatePickerCreateStrategy)` | Sets the strategy used to create the date picker dialog for user.
`getDatePickerCreateStrategy()` | Returns the strategy used to create the date picker dialog for user.
`setTimePickerCreateStrategy(TimePickerCreateStrategy)` | Sets the strategy used to create the time picker dialog for user.
`getTimePickerCreateStrategy()` | Returns the strategy used to create the time picker dialog for user.
`setInvalidDateTimeStrategy()` | Sets the strategy used when a date or time is invalid after validation.
`getInvalidDateTimeStrategy()` | Returns the strategy used when a date or time is invalid after validation.
`isValidDateTime()` | Validate the chosen date and time using the `DateTimeValidator`. True if valid.
`getChosenDateTime()` | Gets the chosen date and time.
`clearChosenDateTime()` | Clears the currently chosen date and time.
`setDefaultDateTime(Calendar)` | Sets the default chosen date and time used by the picker.
`setShowIcon(boolean)` | True if icon should be shown.
`setShowIconSpacing(boolean)` | True if displays should be aligned as if icon were shown.
`setAutoValidate(boolean)` | True if the date and time should be validated as soon as values are chosen.
`isShowIcon()` | Returns true if icon is shown.
`isShowIconSpacing()` | Returns true if the spacing for the icon is aligned.
`isAutoValidate()` | Returns true if auto validation is enabled.
`getDateFormat()` | Returns the `DateFormat` used by the date display.
`setDateFormat(DateFormat)` | Sets the `DateFormat` used by the date display.
`getTimeFormat()` | Returns the `DateFormat` used by the time display.
`setTimeFormat(DateFormat)` | Sets the `DateFormat` used by the time display.
`setDefaultDateText(string)` | Sets the default date text shown on the date display.
`setDefaultTimeText(string)` | Sets the default time text shown on the time display.
`setDefaultDateErrorText(string)` | Sets the default error date text shown when invalid.
`setDefaultTimeErrorText(string)` | Sets the default error time text shown when invalid.
`getTypeface()` | Returns the typeface used by the date and time displays.
`setTypeface(Typeface)` | Sets the typeface used by the date and time displays.
`getTextPadding()` | Returns the padding around the date and time displays.
`setTextPadding(int)` | Sets the padding around the date and time displays.
`getTextColor()` | Returns the text color of the date and time displays.
`setTextColor(int)` | Sets the text color of the date and time displays.
`getTextSize()` | Returns the text size of the date and time displays.
`setTextSize(float)` | Sets the text size of the date and time displays.
`getIconSize()` | Returns the size of the icon.
`setIconSize(int)` | Sets the size of the icon.
`getIcon()` | Returns the icon drawable.
`setIcon(Drawable)` | Sets the icon drawable.

## Picking the Datetime
This library tries to make choosing a date and time as simple as possible. To do that, it stores both the date and time in a single `Calendar` object. Calling the `getChosenDateTime()` method will return the currently chosen date and time.

### Listening to date and time choosing events
You can also choose to observe when a date or time is chosen by setting a valid `OnDateTimeChooseListener` on the `DateTimePickerLayout`. The listener will be notified for every date or time choosing event that happens.

## Datetime Validation
This library affords extensive abilities to validate chosen dates and times respectively.

### How it works
You can explicitly invoke validation by calling the `isValidDateTime()` method or set auto validation, which will invoke validation every time the user chooses a date or time, by calling the `setAutoValidation(...)` method. By default, `DateTimePickerLayout`, performs auto validation.

`DateTimePickerLayout` aggregates a `DateTimeValidator` object that it uses to validate both the date and time as needed appropriately. There is a useful default implementation afforded already by `DateTimePickerLayout`, but it can be changed by calling the `setDateTimeValidator(...)` method.

### Creating a custom validator
Creating your own validator is easy: implement the `DateTimeValidator` interface. A simple example is shown below:
```java
public class ExampleDateTimeValidator implements DateTimeValidator {
    @Override
    public boolean validateDate(@Nullable Calendar chosenDate) {
        // ...Really cool validation logic
        return true;
    }
    
    @Override
    public boolean validateTime(@Nullable Calendar chosenTime) {
        // ...Really cool validation logic
        return true;
    }
}
```

### Handling invalid chosen date or time
`DateTimePickerLayout` handles performing operations when a date or time is invalid, after being explicitly validated, by using an `InvalidDateTimeStrategy`. There is a useful default implementation afforded already by `DateTimePickerLayout`, but it can be changed by calling the `setInvalidDateTimeStrategy(...)` method.

#### Creating a custom invalid datetime strategy
Creating your own invalid datetime strategy is easy: implement the `InvalidDateTimeStrategy` interface. A simple example is shown below:
```java
public class ExampleInvalidDateTimeStrategy implements InvalidDateTimeStrategy {
    @Override
    public void onInvalidDate(DateTimePickerLayout parent, TextView view) {
        // ...Really cool invalid date handling
    }
    
    @Override
    public void onInvalidTime(DateTimePickerLayout parent, TextView view) {
        // ...Really cool invalid time handling
    }
}
```
## Dialog Creation Strategies
This library affords customizability over the date and time picker dialogs.

### How it works
`DateTimePickerLayout` uses aggregated strategies to create both the date and time picker dialogs respectively. Those dialogs are then used as a user-interface to allow the user to actually choose a date or time.

The two strategies it uses to create these picker dialogs are `DatePickerCreateStrategy` and `TimePickerCreateStrategy`. Useful default implementations of both strategies are afforded already by `DateTimePickerLayout`, but can be changed by calling either the `setTimePickerCreateStrategy(...)` or `setDatePickerCreateStrategy(...)` method respectively.

### Custom date picker create strategy
Creating your own date picker create strategy is easy: implement the `DatePickerCreateStrategy` interface. A simple example is shown below:
```java
public class DatePickerCreateStrategy implements DatePickerCreateStrategy {
    @Override
    public DatePickerDialog createPicker(Context c, DatePickerDialog.OnDateSetListener listener) {
        // ...Really cool date picker dialog creation
    }
}
```

### Custom time picker create strategy
Creating your own time picker create strategy is easy: implement the `TimePickerCreateStrategy` interface. A simple example is shown below:
```java
public class TimePickerCreateStrategy implements TimePickerCreateStrategy {
    @Override
    public TimePickerDialog createPicker(Context c, TimePickerDialog.OnTimeSetListener listener) {
        // ...Really cool time picker dialog creation
    }
}
```
