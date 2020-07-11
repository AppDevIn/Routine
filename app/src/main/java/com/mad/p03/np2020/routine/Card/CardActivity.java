package com.mad.p03.np2020.routine.Card;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import com.mad.p03.np2020.routine.HistoryFragment;
import com.mad.p03.np2020.routine.NotesFragment;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.StepsFragment;
import com.mad.p03.np2020.routine.models.PopUp;
import com.mad.p03.np2020.routine.models.Task;

import java.util.Calendar;
import java.util.Date;

/**
*
* CardActivity class used to manage card activities
*
* @author Pritheev
* @since 02-06-2020
*
 */

public class CardActivity extends AppCompatActivity implements View.OnClickListener, ScheduleDialog.ScheduleDialogListener {

    private final String TAG = "CardActivity";

    private DatePickerDialog.OnDateSetListener dateSetListener;

    private TimePickerDialog.OnTimeSetListener timeSetListener;

    //Member Variable
    Task mTask;

    Button dateButton;
    Button timeButton;
    String date;
    String time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_layout);

        //Get task object from extra
        mTask = (Task) getIntent().getSerializableExtra("task");

        dateButton = findViewById(R.id.dateButton);
        timeButton = findViewById(R.id.timeButton);

        //IDs
        EditText edTitle = findViewById(R.id.title);
        LinearLayout check = findViewById(R.id.ll_check);
        LinearLayout focus = findViewById(R.id.ll_focus);
        LinearLayout schedule = findViewById(R.id.ll_schedule);
        LinearLayout notes = findViewById(R.id.ll_notes);

        //Set onclick listeners
        check.setOnClickListener(this);
        focus.setOnClickListener(this);
        schedule.setOnClickListener(this);
        notes.setOnClickListener(this);

        //Set the title text the task name
        edTitle.setText(mTask.getName());
        
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {

        //Doesn't have to be visible if a tool has been selected
        findViewById(R.id.toolMessage).setVisibility(View.GONE);

        switch (view.getId()){
            case R.id.ll_check:
                Log.i(TAG, "onClick: Check List is clicked");
                checkList();
                break;            
            case R.id.ll_focus:
                Log.i(TAG, "onClick: Focus is clicked");
                focus();
                break; 
            case R.id.ll_schedule:
                Log.i(TAG, "onClick: Schedule is clicked");
                schedule();
                break;
            case R.id.ll_notes:
                Log.i(TAG, "onClick: Notes is clicked");
                notes();
                break;
                
        }
    }

    private void schedule() {
        ScheduleDialog scheduleDialog = new ScheduleDialog();
        scheduleDialog.show(getSupportFragmentManager(),"Schedule Dialog");
    }

    private void notes() {
    }

    private void focus() {
    }

    private void checkList() {
    }

    @Override
    public String DatePicker() {
        Log.v(TAG, "Date Button Pressed!");

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetListener, year, month, day);
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month += 1;

                date = day + "/" + month + "/" + year;

                //dateButton.setText(date);
                Log.v(TAG, "Date Set: dd/mm/yyyy: " + date);

            }
        };

        return date;
    }

    @Override
    public String TimePicker() {
        Log.v(TAG, "Time Button Pressed");

        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, timeSetListener, hour, minute, false);
        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timePickerDialog.show();

        timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                time = hour + ":" + minute;

                //timeButton.setText(time);

                Log.v(TAG, "Time set: " + time);
            }
        };

        return time;
    }

    //Reference
//    public void openHistory() { //Open history tab
//        HistoryFragment fragmentFocus = HistoryFragment.newInstance(user, focusDBHelper);
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_bottom, R.anim.enter_from_bottom, R.anim.exit_to_bottom);
//        fragmentTransaction.addToBackStack(null);
//        fragmentTransaction.add(R.id.fragment_container, fragmentFocus, "HISTORY FRAGMENT").commit();
//
//    }
}
