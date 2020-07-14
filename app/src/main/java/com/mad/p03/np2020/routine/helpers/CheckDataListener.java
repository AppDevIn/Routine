package com.mad.p03.np2020.routine.helpers;

import com.mad.p03.np2020.routine.models.Check;

public interface CheckDataListener {

    void onDataAdd(Check check);
    void onDataUpdate(Check check);
    void onDataDelete(String ID);
}
