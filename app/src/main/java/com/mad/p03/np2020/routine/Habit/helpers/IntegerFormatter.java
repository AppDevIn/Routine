package com.mad.p03.np2020.routine.Habit.helpers;

import com.github.mikephil.charting.formatter.ValueFormatter;

/**
 *
 * This created to format the values as integer in habit bar chart
 *
 * @author Hou Man
 * @since 02-08-2020
 */

public class IntegerFormatter extends ValueFormatter {

    @Override
    public String getFormattedValue(float value) {
        return "" + ((int) value);
    }
}
