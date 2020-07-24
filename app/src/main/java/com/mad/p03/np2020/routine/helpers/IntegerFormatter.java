package com.mad.p03.np2020.routine.helpers;

import com.github.mikephil.charting.formatter.ValueFormatter;

public class IntegerFormatter extends ValueFormatter {

    @Override
    public String getFormattedValue(float value) {
        return "" + ((int) value);
    }
}
