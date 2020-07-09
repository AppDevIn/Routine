package com.mad.p03.np2020.routine.Card;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mad.p03.np2020.routine.R;

public class ScheduleDialog extends BottomSheetDialogFragment {

    private final String TAG = "ScheduleDialog";

    private ScheduleDialogListener scheduleDialogListener;

    //Date Button
    Button dateButton;

    //Time Button
    Button timeButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_schedule, container, false);

        dateButton = v.findViewById(R.id.dateButton);
        timeButton = v.findViewById(R.id.timeButton);

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduleDialogListener.DatePicker();
            }
        });

        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduleDialogListener.TimePicker();
            }
        });

        return v;
    }

    public interface ScheduleDialogListener {
        void DatePicker();
        void TimePicker();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try
        {
            scheduleDialogListener = (ScheduleDialogListener) context;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString() + " must implement ScheduleDialogListener");
        }

    }
}
