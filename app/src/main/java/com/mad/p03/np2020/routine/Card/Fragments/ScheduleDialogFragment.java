package com.mad.p03.np2020.routine.Card.Fragments;

import android.annotation.SuppressLint;
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
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mad.p03.np2020.routine.Card.CardActivity;
import com.mad.p03.np2020.routine.DAL.ScheduleDBHelper;
import com.mad.p03.np2020.routine.DAL.TaskDBHelper;
import com.mad.p03.np2020.routine.Profile.ProfileActivity;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.models.CardNotification;
import com.mad.p03.np2020.routine.models.Schedule;
import com.mad.p03.np2020.routine.models.Task;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
/**
 *
 * CardNotification Class for setting notification
 *
 * @author Pritheev
 * @since 02-06-2020
 *
 */
public class ScheduleDialogFragment extends BottomSheetDialogFragment
{

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

    String taskID;

    //Calender to set according to reminder time set
    Calendar currentCal;

    //Second calendar for final reminder time
    Calendar selectedCal;

    //Third Calendar for validation
    Calendar validationCalendar;

    //Fourth Calendar for validating if a time ahs been previously selected
    Calendar previousSelected;

    //Variables for current time
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

    //Variables for notification channel creation
    String ChannelName;
    String ChannelDescription;
    int ChannelImportance;
    String ChannelID;

    //Task variables
    String CardName;
    int LatestID;
    Task mTask;

    //Firebase vvariables
    DatabaseReference mDatabase;
    DatabaseReference notificationRef;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    String UID;

    //Schedule DB variables
    Schedule schedule;
    ScheduleDBHelper scheduleDBHelper;

    public ScheduleDialogFragment(Task task)
    {
        mTask = task;
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {

        //Inflate view
        View v = inflater.inflate(R.layout.fragment_schedule, container, false);

        //Initializing notification variables
        ChannelID = "CardNotification";
        ChannelName = "CardNotificationChannel";
        ChannelDescription = "Channel for card notifications";
        ChannelImportance = NotificationManager.IMPORTANCE_DEFAULT;
        //Initialize notification channel
        initialiseNotificationChannel();

        //Initializing Task variables
        taskID = mTask.getTaskID();
        CardName = mTask.getName();

        //Initializing Firebase variables
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        UID = firebaseUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        notificationRef = mDatabase.child("users").child(UID);

        //Initializing buttons
        dateButton = v.findViewById(R.id.dateButton);
        timeButton = v.findViewById(R.id.timeButton);
        reminderButton = v.findViewById(R.id.reminderButton);

        scheduleDBHelper = new ScheduleDBHelper(getContext());


        if(mTask.getRemindDate() != null){
            String time, date;

            date = mTask.getStringRemindDate().split(" ")[0];
            time = mTask.getStringRemindDate().split(" ")[1];

            dateButton.setText("Date: " + date);
            timeButton.setText("Time: " + time);
        }
        else {
            dateButton.setText("Date: Click to select!");
            timeButton.setText("Time: Click to select!");
        }

        //Getting current calendar
        currentCal = Calendar.getInstance();

        //Initializing setTimeCal to cal
        selectedCal = Calendar.getInstance();

        //Initializing validationCalendar to cal
        validationCalendar = Calendar.getInstance();

        //Initializing previousSelected to cal
        previousSelected = Calendar.getInstance();

        //Validation for if a schedules had already been previously set
        if (mTask.getRemindDate() != null)
        {
            previousSelected.setTime(mTask.getRemindDate());

            if (previousSelected.getTimeInMillis() < currentCal.getTimeInMillis())
            {
                Log.v(TAG, "Resetting schedule timers!");
                dateButton.setText("Date: Click to select!");
                timeButton.setText("Time: Click to select!");

                isReminderSet = false;
                isTimeSet = false;
                isDateSet = false;
            }
        }

        //Date button listener
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Date Button Pressed!");
                ChooseDate();
            }
        });

        //Time button listener
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Time Button Pressed!");
                ChooseTime();
            }
        });

        //Schedule button listener
        reminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Schedule Button Pressed!");
                onReminderClicked();
            }
        });

        return v;
    }

    /**
     *
     * Function for when schedule is clicked
     *
     * */
    public void onReminderClicked()
    {
        //Ensuring date and time variables have been set
        if (isDateSet.equals(true) && isTimeSet.equals(true))
        {
            //Calls set notification function to set notification
            setNotification();
            //Sets isReminderSet to true to disable setting notification until current one has passed
            isReminderSet = true;
            dismiss();

            //Gets date and time set from selected cal
            long finalTime = selectedCal.getTimeInMillis();
            Date setTime = new Date(finalTime);

            //Initializing a Date format
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateString = dateFormat.format(setTime);

            //Setting up values for Task
            mTask.setRemindDate(dateString);
            mTask.executeUpdateFirebase(this);
            new TaskDBHelper(getContext()).update(mTask.getTaskID(), mTask.getStringRemindDate());
        }
        else
        {
            //Sends a toast if date and time has not been set
            Toast reminderValidity = Toast.makeText(getContext(), "Set a date and time first!", Toast.LENGTH_SHORT);
            reminderValidity.setGravity(Gravity.CENTER, 0, 0);
            reminderValidity.show();
        }
    }

    /**
     *
     * Function for when date button is clicked
     *
     * */
    public String ChooseDate()
    {
        Log.v(TAG, "Date Button Pressed!");

        //Gets current day, month, year from cal to set to Date picker dialog
        int year = currentCal.get(Calendar.YEAR);
        int month = currentCal.get(Calendar.MONTH);
        int day = currentCal.get(Calendar.DAY_OF_MONTH);

        //Initializing date picker dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetListener, year, month, day);
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.getDatePicker().setMinDate(validationCalendar.getTimeInMillis());
        datePickerDialog.show();

        //Listener for positive button of date picker dialog
        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "SET", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //Gets current day, month, year from selected date
                currentDay = datePickerDialog.getDatePicker().getDayOfMonth();
                currentMonth = datePickerDialog.getDatePicker().getMonth();
                currentYear = datePickerDialog.getDatePicker().getYear();

                int modifiedMonth = currentMonth + 1;

                selectedDate = currentDay + "/" + modifiedMonth + "/" + currentYear;

                //Sets selected date into selectedCal
                selectedCal.set(Calendar.YEAR, currentYear);
                selectedCal.set(Calendar.MONTH, currentMonth);
                selectedCal.set(Calendar.DAY_OF_MONTH, currentDay);

                isDateSet = true;

                //Sets text of button to selected date
                dateButton.setText("Date: " + selectedDate);

                Log.v(TAG, "Date Set: dd/mm/yyyy: " + selectedDate);

            }
        });

        /*
        //Old code for listening to date picker dialog
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

    /***
     *
     * Function for when time button is selected
     *
     */
    public String ChooseTime() {
        Log.v(TAG, "Time Button Pressed");

        //Gets current hour and minute
        int hour = currentCal.get(Calendar.HOUR_OF_DAY);
        int minute = currentCal.get(Calendar.MINUTE);

        //Initializing time picker dialog
        TimePickerDialog timePickerDialog;
        timePickerDialog = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth,new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                currentHour = hourOfDay;
                currentMinute = minute;

                //Adds current hour and minute to string
                selectedTime = currentHour + ":" + currentMinute;

                //Ensures values are 2 digits
                if (currentHour < 10)
                {
                    selectedTime = "0" + currentHour + ":" + currentMinute;
                }
                if (currentMinute < 10)
                {
                    selectedTime = currentHour + ":" + "0" + currentMinute;
                }

                //Sets selectedCal with selected time
                selectedCal.set(Calendar.HOUR_OF_DAY, currentHour);
                selectedCal.set(Calendar.MINUTE, currentMinute);
                selectedCal.set(Calendar.SECOND, 0);

                isTimeSet = true;

                //Sets button with selected time
                timeButton.setText("Time: " + selectedTime);

                Log.v(TAG, "Time Set: " + selectedTime);
            }
        }, hour, minute, false);
        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timePickerDialog.show();

        /*
        //Old time picker dialog code
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, timeSetListener, hour, minute, false);
        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timePickerDialog.show();

        timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                currentHour = hourOfDay;
                currentMinute = minute;

                selectedTime = currentHour + ":" + currentMinute;

                if (currentHour < 10)
                {
                    selectedTime = "0" + currentHour + ":" + currentMinute;
                }

                if (currentMinute < 10)
                {
                    selectedTime = currentHour + ":" + "0" + currentMinute;
                }

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

         */

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

    /**
     *
     * Function for creating a notification channel
     *
     * */
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

    /**
     *
     * Initializes notification channel base don api level
     *
     * */
    public void initialiseNotificationChannel(){
        // if api > 28, create a notification channel named "Card Notification"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ChannelName = "CardNotification";
            ChannelImportance = NotificationManager.IMPORTANCE_HIGH; // set as high importance
            createNotificationChannel(ChannelID, ChannelName, ChannelDescription, ChannelImportance);
        }
    }

    /**
     *
     * Simple notification channel creator
     *
     * */
    @TargetApi(Build.VERSION_CODES.O)
    public void createNotificationChannel(String channelId, String channelName, String channelDescription, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);

        channel.setDescription(channelDescription);

        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(getActivity().NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

    /**
     *
     * Function for setting notification
     * based on selected date and time
     *
     * */
    public void setNotification()
    {
        //Creating intent to broadcast receiver
        Intent intent = new Intent(getActivity(), CardNotification.class);
        intent.setAction("CardNotification");
        intent.putExtra("TaskID", taskID);
        intent.putExtra("CardName", CardName);

        //Creating a unique ID to be used as a request code for pending intent
        //using millis
        int uniqueID = (int) System.currentTimeMillis();
        schedule = new Schedule(taskID, uniqueID);
        scheduleDBHelper.insertSchedule(schedule);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), uniqueID, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(getContext().ALARM_SERVICE);

        long timeSet = selectedCal.getTimeInMillis();

        String DateTimeSet = selectedCal.get(Calendar.DAY_OF_MONTH) + "/" + selectedCal.get(Calendar.MONTH)+1 + "/" + selectedCal.get(Calendar.YEAR) + " " +  selectedCal.get(Calendar.HOUR_OF_DAY) + "-" + selectedCal.get(Calendar.MINUTE) + "-" + selectedCal.get(Calendar.SECOND);

        Log.v(TAG, "Time in millis set and now: " + timeSet + "-"  + Calendar.getInstance().getTimeInMillis());

        Log.v(TAG, "Date time set is: " + DateTimeSet);

        alarmManager.set(AlarmManager.RTC_WAKEUP, timeSet, pendingIntent);

        /*
        if (timeSet < currentCal.getTimeInMillis())
        {
            MakeToast("Schedule must be set after current time!");
        }
        else
        {
        }
         */

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

    /**
     *
     * Function to make a toast with message as parameter
     *
     * */
    public void MakeToast(String info)
    {
        Toast toast = Toast.makeText(getContext(), info, Toast.LENGTH_LONG);
        toast.getView().setBackgroundColor(Color.GRAY);
        TextView text = (TextView) toast.getView().findViewById(android.R.id.message);
        text.setTextColor(Color.WHITE);
        toast.show();
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