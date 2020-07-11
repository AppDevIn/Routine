package com.mad.p03.np2020.routine.helpers;

import com.mad.p03.np2020.routine.models.Check;

public interface CheckDataListener {

    public void onDataAdd(Check check);
    public void onDataUpdate(Check check);
    public void onDataDelete(String ID);
}
