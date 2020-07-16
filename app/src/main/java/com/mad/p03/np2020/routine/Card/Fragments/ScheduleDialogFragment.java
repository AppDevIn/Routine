package com.mad.p03.np2020.routine.Card.Fragments;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaSession2Service;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
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
import com.mad.p03.np2020.routine.Card.CardActivity;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.models.CardNotification;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ScheduleDialogFragment extends BottomSheetDialogFragment {

    private final String TAG = "ScheduleDialog";

    //Initialize listener for DatePicker
    private DatePickerDialog.OnDateSetListener dateSetListener;

    //Initialize listener for TimePicker
    private TimePickerDialog.OnTimeSetListener timeSetListener;

    //Date Button
    Button dateButton;

    //Time Button
    Button timeButton;

    //Reminder Button
    Button reminderButton;

    //Initialize date storing variable
    String currentDate;
    String selectedDate;

    //Initialize date storing variable
    String currentTime;
    String selectedTime;

    //Calender to set according to reminder time set
    Calendar currentCal;

    //Second calendar for final reminder time
    Calendar selectedCal;

    int currentYear;
    int currentMonth;
    int currentDay;
    int currentHour;
    int currentMinute;
    int currentSecond;

    //Boolean to decide if if time has been set
    Boolean isTimeSet = false;

    //Boolean to decide if if date has been set
    Boolean isDateSet = false;

    //Boolean to decide if if reminder has been set
    Boolean isReminderSet = false;

    String ChannelName;
    String ChannelDescription;
    int ChannelImportance;
    String ChannelID;

    String CardName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Inflate view
        View v = inflater.inflate(R.layout.fragment_schedule, container, false);

        ChannelID = "CardNotification";
        ChannelName = "CardNotificationChannel";
        ChannelDescription = "Channel for card notifications";
        ChannelImportance = NotificationManager.IMPORTANCE_DEFAULT;
        //Initialize notification channel
        initialiseHabitNotificationChannel();

        //Initializing buttons
        dateButton = v.findViewById(R.id.dateButton);
        timeButton = v.findViewById(R.id.timeButton);
        reminderButton = v.findViewById(R.id.reminderButton);

        dateButton.setText("Date: ");
        timeButton.setText("Time: ");

        //Getting current calendar
        currentCal = Calendar.getInstance();

        //Initializing setTimeCal to cal
        selectedCal = Calendar.getInstance();

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseDate();
            }
        });


        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseTime();
            }
        });


        reminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onReminderClicked();
            }
        });


        return v;
    }

    public void onReminderClicked()
    {
        if (isDateSet.equals(true) && isTimeSet.equals(true))
        {
            setNotification();
            isReminderSet = true;
            dismiss();
        }
        else
        {
            Toast reminderValidity = Toast.makeText(getContext(), "Set a date and time first!", Toast.LENGTH_SHORT);
            reminderValidity.setGravity(Gravity.CENTER, 0, 0);
            reminderValidity.show();
        }
    }

    public String ChooseDate() {
        Log.v(TAG, "Date Button Pressed!");

        int year = currentCal.get(Calendar.YEAR);
        int month = currentCal.get(Calendar.MONTH);
        int day = currentCal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetListener, year, month, day);
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();

        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "SET", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                currentDay = datePickerDialog.getDatePicker().getDayOfMonth();
                currentMonth = datePickerDialog.getDatePicker().getMonth();
                currentYear = datePickerDialog.getDatePicker().getYear();

                selectedDate = currentDay + "/" + currentMonth + "/" + currentYear;

                selectedCal.set(Calendar.YEAR, year);
                selectedCal.set(Calendar.MONTH, month);
                selectedCal.set(Calendar.DAY_OF_MONTH, day);

                isDateSet = true;

                dateButton.setText("Date: " + selectedDate);

                Log.v(TAG, "Date Set: dd/mm/yyyy: " + selectedDate);

            }
        });

        /*
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month += 1;

                selectedDate = day + "/" + month + "/" + year;

                selectedCal.set(Calendar.YEAR, year);
                selectedCal.set(Calendar.MONTH, month);
                selectedCal.set(Calendar.DAY_OF_MONTH, day);

                isDateSet = true;

                dateButton.setText("Date: " + selectedDate);

                Log.v(TAG, "Date Set: dd/mm/yyyy: " + selectedDate);
            }

        };
         */
        return selectedDate;
    }



    public String ChooseTime() {
        Log.v(TAG, "Time Button Pressed");

        int hour = currentCal.get(Calendar.HOUR_OF_DAY);
        int minute = currentCal.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, timeSetListener, hour, minute, false);
        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timePickerDialog.show();

        timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                currentHour = hourOfDay;
                currentMinute = minute;

                selectedTime = currentHour + ":" + currentMinute;


                selectedCal.set(Calendar.HOUR_OF_DAY, currentHour);
                selectedCal.set(Calendar.MINUTE, currentMinute);
                selectedCal.set(Calendar.SECOND, 0);

                isTimeSet = true;

                timeButton.setText("Time: " + selectedTime);

                Log.v(TAG, "Time Set: " + selectedTime);

                timePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "SET", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedTime = currentHour + ":" + currentMinute;

                        selectedCal.set(Calendar.HOUR_OF_DAY, currentHour);
                        selectedCal.set(Calendar.MINUTE, currentMinute);

                        isTimeSet = true;

                        timeButton.setText("Time: " + selectedTime);

                        Log.v(TAG, "OnCLik Time Set: " + selectedTime);
                    }
                });
            }
        };

        /*
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

         */

        return selectedTime;
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


    public void initialiseHabitNotificationChannel(){
        // if api > 28, create a notification channel named "HabitTracker"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = "HabitTracker";
            int importance = NotificationManager.IMPORTANCE_HIGH; // set as high importance
        }
        createNotificationChannel(ChannelID, ChannelName, ChannelDescription, ChannelImportance);
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void createNotificationChannel(String channelId, String channelName, String channelDescription, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);

        channel.setDescription(channelDescription);

        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(getActivity().NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }


    public void setNotification(){

        Intent intent = new Intent(getActivity(), CardNotification.class);
        intent.setAction("CardNotification");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(getContext().ALARM_SERVICE);

        long timeSet = selectedCal.getTimeInMillis();

        String DateTimeSet = selectedCal.get(Calendar.DAY_OF_MONTH) + "/" + selectedCal.get(Calendar.MONTH)+1 + "/" + selectedCal.get(Calendar.YEAR) + " " +  selectedCal.get(Calendar.HOUR_OF_DAY) + "-" + selectedCal.get(Calendar.MINUTE) + "-" + selectedCal.get(Calendar.SECOND);

        Log.v(TAG, "Time in millis set and now: " + timeSet + "-"  + Calendar.getInstance().getTimeInMillis());

        Log.v(TAG, "Date time set is: " + DateTimeSet);

        alarmManager.set(AlarmManager.RTC_WAKEUP, timeSet, pendingIntent);


        /*
        long timeSet = selectedCal.getTimeInMillis();

        Toast reminderSet = Toast.makeText(getActivity(), "Reminder Set!", Toast.LENGTH_SHORT);
        reminderSet.setGravity(Gravity.CENTER, 0, 0);
        reminderSet.show();

        String DateTimeSet = selectedCal.get(Calendar.DAY_OF_MONTH) + "/" + selectedCal.get(Calendar.MONTH)+1 + "/" + selectedCal.get(Calendar.YEAR) + " " +  selectedCal.get(Calendar.HOUR_OF_DAY) + "-" + selectedCal.get(Calendar.MINUTE) + "-" + selectedCal.get(Calendar.SECOND);
        Log.v(TAG, "Time in millis set and now: " + timeSet + "---"  + Calendar.getInstance().getTimeInMillis());
        Log.v(TAG, "Date time set is: " + DateTimeSet);

        Intent intent = new Intent(getContext(), CardActivity.class);
        intent.setAction("ScheduleNotification");
        intent.putExtra("CalendarMillis", timeSet);
        intent.putExtra("DateTimeSet", DateTimeSet);
        startActivity(intent);

         */
    }

    @Override
    public void onStop() {
        super.onStop();

        if (isReminderSet.equals(true))
        {
            long finalTime = selectedCal.getTimeInMillis();
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