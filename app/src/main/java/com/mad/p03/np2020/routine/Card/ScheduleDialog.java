package com.mad.p03.np2020.routine.Card;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.models.CardNotification;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ScheduleDialog extends BottomSheetDialogFragment {

    private final String TAG = "ScheduleDialog";

    private DatePickerDialog.OnDateSetListener dateSetListener;

    private TimePickerDialog.OnTimeSetListener timeSetListener;

    //Date Button
    Button dateButton;

    //Time Button
    Button timeButton;

    Button reminderButton;

    String date;
    String time;

    Calendar cal;

    Calendar setTimeCal;

    Boolean timeSet = false;
    Boolean dateSet = false;

    Boolean reminderSet  =false;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_schedule, container, false);
        createNotificationChannel();

        dateButton = v.findViewById(R.id.dateButton);
        timeButton = v.findViewById(R.id.timeButton);
        reminderButton = v.findViewById(R.id.setReminderButton);

        cal = Calendar.getInstance();

        setTimeCal = cal;

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date = DatePicker();
                Log.v(TAG, "Date:" + date);
                //onDateClicked(date);
                dateSet = true;
            }
        });

        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time = TimePicker();
                Log.v(TAG, "Time: " + time);
                //onTimeClicked(time);
                timeSet = true;
            }
        });

        reminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dateSet.equals(false) && timeSet.equals(false))
                {
                    Toast.makeText(getActivity(), "Set a date and time first!", Toast.LENGTH_SHORT);
                }
                else
                {
                    setNotification();
                    reminderSet = true;
                }
            }
        });

        return v;
    }

    public void onDateClicked(String dateText){
        dateButton.setText("Date Set: " + dateText);
    }

    public void onTimeClicked(String timeText){
        timeButton.setText("Time Set: " + timeText);
    }

    public String DatePicker() {
        Log.v(TAG, "Date Button Pressed!");

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetListener, year, month, day);
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month += 1;

                date = day + "/" + month + "/" + year;

                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DAY_OF_MONTH, day);

                onDateClicked(date);

                Log.v(TAG, "Date Set: dd/mm/yyyy: " + date);
            }
        };

        return date;
    }

    public String TimePicker() {
        Log.v(TAG, "Time Button Pressed");

        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, timeSetListener, hour, minute, false);
        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timePickerDialog.show();

        timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {

                if (minute < 10)
                {
                    time = hour + ":0" + minute;
                }
                else
                {
                    time = hour + ":" + minute;
                }

                cal.set(Calendar.HOUR_OF_DAY, hour);
                cal.set(Calendar.MINUTE, minute);
                cal.set(Calendar.SECOND, 0);

                onTimeClicked(time);

                Log.v(TAG, "Time set: " + time);
            }
        };

        return time;
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel()
    {
        CharSequence name = "CardNotificationChannel";
        String description = "Channel for card notifications";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel("CardNotification", name, importance);

        channel.setDescription(description);

        NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    public void setNotification(){
        Intent intent = new Intent(getActivity(), CardNotification.class);
        intent.setAction("CardNotification");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(getContext().ALARM_SERVICE);

        setTimeCal.set(Calendar.MONTH, cal.get(Calendar.MONTH)-1);

        long timeSet = setTimeCal.getTimeInMillis();

        String DateTimeSet = cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.YEAR) + " " +  cal.get(Calendar.HOUR_OF_DAY) + "-" + cal.get(Calendar.MINUTE) + "-" + cal.get(Calendar.SECOND);

        Log.v(TAG, "Time in millis set and now: " + timeSet + "-"  + Calendar.getInstance().getTimeInMillis());

        Log.v(TAG, "Date time set is: " + DateTimeSet);

        alarmManager.set(AlarmManager.RTC_WAKEUP, timeSet, pendingIntent);

        Toast.makeText(getActivity(), "Reminder Set!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (reminderSet.equals(true))
        {
            long finalTime = setTimeCal.getTimeInMillis();
            Date setTime = new Date(finalTime);

            DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mms:ss");

            String dateString = dateFormat.format(setTime);

            Intent intent = new Intent(getActivity(), CardActivity.class);
            intent.putExtra("ReminderDate", dateString);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            Log.v(TAG, "Intent with date sent! [" + setTime + "]");

            startActivity(intent);
        }
    }

    /*
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
    */
}
