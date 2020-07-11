package com.mad.p03.np2020.routine.Card;

import android.content.Context;
import android.content.Intent;
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
                String date = scheduleDialogListener.DatePicker();
                Log.v(TAG, "Date:" + date);
                onDateClicked(date);
            }
        });

        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time = scheduleDialogListener.TimePicker();
                Log.v(TAG, "Time: " + time);
                onTimeClicked(time);
            }
        });



        return v;
    }

    void onDateClicked(String dateText){
        dateButton.setText("Date Set: " + dateText);
    }

    void onTimeClicked(String timeText){
        timeButton.setText("Time Set: " + timeText);
    }

    public interface ScheduleDialogListener {
        String DatePicker();
        String TimePicker();
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
