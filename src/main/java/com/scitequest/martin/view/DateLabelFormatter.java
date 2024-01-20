package com.scitequest.martin.view;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JFormattedTextField.AbstractFormatter;

/**
 * This class defines the data format used in TimestampInput
 */
public final class DateLabelFormatter extends AbstractFormatter {

    private static final String dateFormat = "yyyy-MM-dd";
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat);

    @Override
    public Object stringToValue(String text) throws ParseException {
        return dateFormatter.parseObject(text);
    }

    @Override
    public String valueToString(Object value) {
        if (value == null) {
            return "";
        }
        Calendar cal = (Calendar) value;
        return dateFormatter.format(cal.getTime());
    }
}
