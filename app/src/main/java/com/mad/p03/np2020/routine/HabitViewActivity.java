package com.mad.p03.np2020.routine;

import android.content.Intent;
import android.icu.math.MathContext;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.mad.p03.np2020.routine.DAL.HabitDBHelper;
import com.mad.p03.np2020.routine.DAL.HabitRepetitionDBHelper;
import com.mad.p03.np2020.routine.helpers.IntegerFormatter;
import com.mad.p03.np2020.routine.models.Habit;
import com.mad.p03.np2020.routine.models.HabitRepetition;
import com.mad.p03.np2020.routine.models.User;

import java.util.ArrayList;
import java.util.Calendar;

public class HabitViewActivity extends AppCompatActivity {

    private static final String TAG = "HabitViewActivity";

    // Widgets
    private TextView title, goal_text, curr_streak_text, best_streak_text, progress_text, curr_period, best_period, progress_text_period;
    private CardView habit_card;
    private ImageView back_btn, editBtn;
    private ProgressBar progressBar;
    private BarChart habit_barChart;
    // Habit
    private Habit habit;

    // HabitDBHandler
    private HabitDBHelper habit_dbHandler;
    private HabitRepetitionDBHelper habitRepetitionDBHelper;

    // User
    private User user;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_habit);

        // set the layout in full screen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // initialise widgets
        title = findViewById(R.id.habit_view_title);
        habit_card = findViewById(R.id.habit_card);
        back_btn = findViewById(R.id.habit_back);
        editBtn = findViewById(R.id.habit_view_edit);
        goal_text = findViewById(R.id.habit_goal);
        curr_streak_text = findViewById(R.id.current_streak);
        best_streak_text = findViewById(R.id.best_streak);
        progressBar = findViewById(R.id.habit_progressBar);
        progress_text = findViewById(R.id.progress_text);
        curr_period = findViewById(R.id.current_streak_period);
        best_period = findViewById(R.id.best_streak_period);
        progress_text_period = findViewById(R.id.progress_text_period);
        habit_barChart = findViewById(R.id.habit_barChart);

        habit_dbHandler = new HabitDBHelper(this);
        habitRepetitionDBHelper = new HabitRepetitionDBHelper(this);

        // set user
        user = new User();
        user.setUID(FirebaseAuth.getInstance().getCurrentUser().getUid());

        // This is to get the habit object from intent bundle
        Intent intent = getIntent();
        if (intent.hasExtra("recorded_habit")){
            habit = deserializeFromJson(intent.getExtras().getString("recorded_habit"));
        }else{
            Log.d(TAG, "LOADING HABIT ERROR ");
        }

        // set text on input fields based on the habit object
        title.setText(capitalise(habit.getTitle()));
        habit_card.setCardBackgroundColor(getResources().getColor(habit.returnColorID(habit.getHolder_color())));
        setGoalText();
        calculateStreak();
        displayBarChart();

        // set onClickListener on close button
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activityName = new Intent(HabitViewActivity.this, HabitActivity.class);
                activityName.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(activityName);
                finish();
            }
        });

        // set onClickListener on edit habit button
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go the edit habit activity
                Intent activityName = new Intent(HabitViewActivity.this, HabitEditActivity.class);
                Bundle extras = new Bundle();
                extras.putString("recorded_habit", habit_serializeToJson(habit));
                activityName.putExtras(extras);
                startActivity(activityName);
                finish();
            }
        });
    }

    /**
     *
     * This method is used to serialize a single object. (into Json String)
     *
     * @param habit This parameter is used to get the habit object
     *
     * @return String This returns the serialized object.
     *
     * */
    public String habit_serializeToJson(Habit habit) {
        Gson gson = new Gson();
        Log.i(TAG,"Object serialize");
        return gson.toJson(habit);
    }

    /**
     *
     * This method is used to deserialize to single object. (from Json)
     *
     * @param jsonString This parameter is used to get json string
     *
     * @return String This returns the deserialized Habit object.
     *
     * */
    private Habit deserializeFromJson(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, Habit.class);
    }


    /**
     *
     * This method is used to format text by capitalising the first text of each split text
     *
     * @param text This parameter is used to get the text
     *
     * @return String This returns the formatted text
     * */
    public String capitalise(String text){
        String txt = "";
        String[] splited = text.split("\\s+");
        for (String s: splited){
            txt += s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase() + " ";
        }
        return txt;

//        return text.substring(0,1).toUpperCase() + text.substring(1).toLowerCase();
    }

    public String getPeriodText(int periodCnt){
        String period;
        switch (periodCnt){
            case 7:
                period = "Week";
                break;

            case 30:
                period = "Month";
                break;

            default:
                period = "Day";
                break;
        }

        return period;
    }

    public void setGoalText(){
        int occurrence = habit.getOccurrence();
        String period = getPeriodText(habit.getPeriod());

        goal_text.setText(String.format("%d Times a %s",occurrence,period));
    }

    public void calculateStreak(){
        int occurrence = habit.getOccurrence();
        int period = habit.getPeriod();
        long habitID = habit.getHabitID();

        ArrayList<HabitRepetition> habitRepetitionArrayList = habitRepetitionDBHelper.getAllHabitRepetitionsByHabitID(habitID);

        int curr_streak = 0, max_streak = 0;
        int max_cycle = 0, completion = 0;
        switch (period){
            case 1:
                for (HabitRepetition hr : habitRepetitionArrayList){
                    int count = hr.getCount();
                    curr_streak = (count >= occurrence) ? ++curr_streak : 0;
                    max_streak = Math.max(max_streak, curr_streak);
                    ++max_cycle;
                    completion = (count >= occurrence) ? ++completion : completion;
                }
                break;

            case 7:

            case 30:
                max_cycle = habitRepetitionDBHelper.getMaxCycle(habitID);
                for (int i = 1; i < max_cycle+1; i++){
                    int max_count = habitRepetitionDBHelper.getMaxCountByCycle(habitID, i);
                    curr_streak = (max_count >= occurrence) ? ++ curr_streak : 0;
                    max_streak = Math.max(max_streak, curr_streak);
                    completion = (max_count >= occurrence) ? ++completion : completion;
                }
                break;
        }

        curr_streak_text.setText(String.valueOf(curr_streak));
        best_streak_text.setText(String.valueOf(max_streak));

        String period_text = getPeriodText(period).toLowerCase();
        String curr_period_text = period_text;
        String best_period_text = period_text;
        if (curr_streak > 1){
            curr_period_text += "s";
        }

        if (max_streak > 1){
            best_period_text += "s";
        }

        curr_period.setText(curr_period_text);
        best_period.setText(best_period_text);

        int completeProgress = Math.min((int) ((completion/(double)max_cycle) * 100),100);
        progressBar.setProgress(completeProgress);
        progress_text.setText(String.format("%d",completeProgress)+"%");
        String goal_period_text = period_text;
        if (max_cycle > 1){
            goal_period_text += "s";
        }
        progress_text_period.setText(String.format("%d/%d %s",completion,max_cycle,goal_period_text));
    }

    public void displayBarChart(){
        ArrayList<HabitRepetition> habitRepetitionArrayList = habitRepetitionDBHelper.getAllHabitRepetitionsByHabitID(habit.getHabitID());

        ArrayList<String> timeStampList = new ArrayList<>();
        timeStampList.add("dummy");
        ArrayList<BarEntry> barEntries = new ArrayList<>();

        int x = 1;
        for (HabitRepetition hr : habitRepetitionArrayList){
            barEntries.add(new BarEntry(x, hr.getCount()));
            timeStampList.add(getDateByTimeStamp(hr.getTimestamp()));
            ++x;
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "Habit Bar Chart");
        barDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        barDataSet.setValueTextSize(14);


        BarData data = new BarData(barDataSet);
        data.setBarWidth(0.7f);

        habit_barChart.setData(data);
        habit_barChart.animateY(1000);
        habit_barChart.setDrawBarShadow(false);
        habit_barChart.setDrawValueAboveBar(true);
        habit_barChart.setVisibleXRangeMaximum(5);
        habit_barChart.moveViewToX(x);
        habit_barChart.setPinchZoom(false);
        habit_barChart.setDrawGridBackground(true);
        habit_barChart.getAxisRight().setEnabled(false);
        habit_barChart.getLegend().setEnabled(false);

        Description description = new Description();
        description.setText("");
        habit_barChart.setDescription(description);

        XAxis xAxis = habit_barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(timeStampList));


    }

    public String getDateByTimeStamp(long timeStamp){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);

        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        return mMonth+"/"+mDay;
    }


}
