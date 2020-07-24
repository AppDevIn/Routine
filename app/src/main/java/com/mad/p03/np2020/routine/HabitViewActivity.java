package com.mad.p03.np2020.routine;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.mad.p03.np2020.routine.DAL.HabitDBHelper;
import com.mad.p03.np2020.routine.DAL.HabitRepetitionDBHelper;
import com.mad.p03.np2020.routine.helpers.IntegerFormatter;
import com.mad.p03.np2020.routine.models.Habit;
import com.mad.p03.np2020.routine.models.HabitRepetition;
import com.mad.p03.np2020.routine.models.User;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class HabitViewActivity extends AppCompatActivity {

    private static final String TAG = "HabitViewActivity";

    public static final int[] chart_buttonIDS = new int[]{R.id.habit_day_barChart,R.id.habit_week_barChart,R.id.habit_month_barChart,R.id.habit_year_barChart};

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
        populateChartButtons();

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

    public void displayDayBarChart(){
        ArrayList<HabitRepetition> habitRepetitionArrayList = habitRepetitionDBHelper.getAllHabitRepetitionsByHabitID(habit.getHabitID());

        ArrayList<String> timeStampList = new ArrayList<>();
        timeStampList.add("dummy");
        ArrayList<BarEntry> barEntries = new ArrayList<>();

        int x = 0;
        for (HabitRepetition hr : habitRepetitionArrayList){
            barEntries.add(new BarEntry(++x, hr.getCount()));
            timeStampList.add(getDayMonthByTimeStamp(hr.getTimestamp()));
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "Habit Bar Chart");
        barDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        barDataSet.setValueTextSize(14);

        BarData data = new BarData(barDataSet);
        if (barEntries.size() >= 3){
            data.setBarWidth(0.7f);
        }else if (barEntries.size() == 2){
            data.setBarWidth(0.5f);
        }else{
            data.setBarWidth(0.3f);
        }

        data.setValueFormatter(new IntegerFormatter());

        habit_barChart.setData(data);
        habit_barChart.animateY(750);
        habit_barChart.setDrawBarShadow(false);
        habit_barChart.setDrawValueAboveBar(true);
        habit_barChart.setVisibleXRangeMaximum(5);
        habit_barChart.moveViewToX(x);
        habit_barChart.setPinchZoom(false);
        habit_barChart.setDrawGridBackground(true);
        habit_barChart.getAxisRight().setEnabled(false);
        habit_barChart.getLegend().setEnabled(false);
        habit_barChart.setClickable(false);
        habit_barChart.setDoubleTapToZoomEnabled(false);

        Description description = new Description();
        description.setText("");
        habit_barChart.setDescription(description);

        XAxis xAxis = habit_barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(timeStampList));
        xAxis.setTextSize(12);

        YAxis yAxis = habit_barChart.getAxisLeft();
        yAxis.setTextSize(12);
        yAxis.setAxisMinimum(0f);
        float max_y = yAxis.getAxisMaximum();
        Log.d(TAG, "displayDayBarChart: "+max_y);
//        yAxis.setAxisMaximum(max_y+2);
        yAxis.setLabelCount(5);
    }
//5
    public void displayWeekBarChart(){
        ArrayList<HabitRepetition> habitRepetitionArrayList = habitRepetitionDBHelper.getAllHabitRepetitionsByHabitID(habit.getHabitID());

        ArrayList<String> timeStampList = new ArrayList<>();
        timeStampList.add("dummy");
        ArrayList<BarEntry> barEntries = new ArrayList<>();

        int n = habitRepetitionArrayList.size();
        int x = 0, count = 0;
        long c_timestamp = 0;
        long initial_timestamp = habitRepetitionArrayList.get(0).getTimestamp();

        for (int i = 0; i < n; i++){
            HabitRepetition hr = habitRepetitionArrayList.get(i);
            c_timestamp += 86400000;
            count += hr.getCount();

            if (i+1 >= n){
                barEntries.add(new BarEntry(++x, count));
                timeStampList.add(getDayMonthByTimeStamp(initial_timestamp));
                break;
            }

            if (c_timestamp == 604800000){
                barEntries.add(new BarEntry(++x, count));
                timeStampList.add(getDayMonthByTimeStamp(initial_timestamp));
                count = 0;
                c_timestamp = 0;
                initial_timestamp = habitRepetitionArrayList.get(i+1).getTimestamp();
            }

        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "Habit Bar Chart");
        barDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        barDataSet.setValueTextSize(14);

        BarData data = new BarData(barDataSet);
        if (barEntries.size() >= 3){
            data.setBarWidth(0.7f);
        }else if (barEntries.size() == 2){
            data.setBarWidth(0.5f);
        }else{
            data.setBarWidth(0.3f);
        }

        data.setValueFormatter(new IntegerFormatter());

        habit_barChart.setData(data);
        habit_barChart.animateY(750);
        habit_barChart.setDrawBarShadow(false);
        habit_barChart.setDrawValueAboveBar(true);
        habit_barChart.setVisibleXRangeMaximum(5);
        habit_barChart.moveViewToX(x);
        habit_barChart.setPinchZoom(false);
        habit_barChart.setDrawGridBackground(true);
        habit_barChart.getAxisRight().setEnabled(false);
        habit_barChart.getLegend().setEnabled(false);
        habit_barChart.setClickable(false);
        habit_barChart.setDoubleTapToZoomEnabled(false);

        Description description = new Description();
        description.setText("");
        habit_barChart.setDescription(description);

        XAxis xAxis = habit_barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(timeStampList));
        xAxis.setTextSize(12);

        YAxis yAxis = habit_barChart.getAxisLeft();
        yAxis.setTextSize(12);
        yAxis.setAxisMinimum(0f);
//        float max_y = yAxis.getAxisMaximum();
//        Log.d(TAG, "displayWeekBarChart: "+max_y);
//        yAxis.setAxisMaximum(max_y+2);
        yAxis.setLabelCount(5);
    }

    public void displayMonthBarChart(){
        ArrayList<String> timeStampList = new ArrayList<>();
        timeStampList.add("dummy");
        ArrayList<BarEntry> barEntries = new ArrayList<>();

        int x = 0;

        long initial_ms = getMonth(habit.getTime_created());
        long next_ms = getNextMonthFromString(habit.getTime_created());

        boolean isNextMonth;

        do{
            int count = habitRepetitionDBHelper.getCountBetweenMonth(habit.getHabitID(), initial_ms, next_ms);
            barEntries.add(new BarEntry(++x, count));
            timeStampList.add(getMonthYearByTimeStamp(initial_ms));

            initial_ms = next_ms;
            next_ms = getNextMonthFromMs(next_ms);
            isNextMonth = habitRepetitionDBHelper.isNextMonth(habit.getHabitID(), initial_ms);

        }while(isNextMonth);

        BarDataSet barDataSet = new BarDataSet(barEntries, "Habit Bar Chart");
        barDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        barDataSet.setValueTextSize(14);

        BarData data = new BarData(barDataSet);
        if (barEntries.size() >= 3){
            data.setBarWidth(0.7f);
        }else if (barEntries.size() == 2){
            data.setBarWidth(0.5f);
        }else{
            data.setBarWidth(0.3f);
        }

        data.setValueFormatter(new IntegerFormatter());

        habit_barChart.setData(data);
        habit_barChart.animateY(750);
        habit_barChart.setDrawBarShadow(false);
        habit_barChart.setDrawValueAboveBar(true);
        habit_barChart.setVisibleXRangeMaximum(5);
        habit_barChart.moveViewToX(x);
        habit_barChart.setPinchZoom(false);
        habit_barChart.setDrawGridBackground(true);
        habit_barChart.getAxisRight().setEnabled(false);
        habit_barChart.getLegend().setEnabled(false);
        habit_barChart.setClickable(false);
        habit_barChart.setDoubleTapToZoomEnabled(false);

        Description description = new Description();
        description.setText("");
        habit_barChart.setDescription(description);

        XAxis xAxis = habit_barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(timeStampList));
        xAxis.setTextSize(12);

        YAxis yAxis = habit_barChart.getAxisLeft();
        yAxis.setTextSize(12);
        yAxis.setAxisMinimum(0f);
//        float max_y = yAxis.getAxisMaximum();
//        Log.d(TAG, "displayWeekBarChart: "+max_y);
//        yAxis.setAxisMaximum(max_y+2);
        yAxis.setLabelCount(5);
    }

    public void displayYearBarChart(){

    }

    public String getMonthYearByTimeStamp(long timeStamp){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);

        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        String[] shortMonths = new DateFormatSymbols().getShortMonths();

        return shortMonths[mMonth];
    }


    public String getDayMonthByTimeStamp(long timeStamp){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);

        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        return mDay+"/"+mMonth;
    }


    public void populateChartButtons(){
        Button button = findViewById(chart_buttonIDS[0]);
        button.setBackgroundColor(getResources().getColor(R.color.colorWhiteGrey));
        displayDayBarChart();

        for (final int iD : chart_buttonIDS){
            final Button btn = findViewById(iD);

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = btn.getId();

                    for (int i = 0; i < 4; i++){
                        Button _btn = findViewById(chart_buttonIDS[i]);

                        if (id == chart_buttonIDS[i]){
                            _btn.setBackgroundColor(getResources().getColor(R.color.colorWhiteGrey));
                            displayCharts(i);
                        }else{
                            _btn.setBackgroundColor(Color.TRANSPARENT);
                        }
                    }
                }
            });
        }
    }

    public void displayCharts(int i){
        switch (i){
            case 0:
                Log.d(TAG, "displayCharts: Day");
                resetChart();
                displayDayBarChart();
                break;

            case 1:
                Log.d(TAG, "displayCharts: Week");
                resetChart();
                displayWeekBarChart();
                break;

            case 2:
                Log.d(TAG, "displayCharts: Month");
                resetChart();
                displayMonthBarChart();
                break;

            case 3:
                displayYearBarChart();
                break;
        }
    }

    public void resetChart(){
        habit_barChart.clear();
        habit_barChart.invalidate();
        habit_barChart.notifyDataSetChanged();
    }

    public long getMonth(String time) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date;
        long ms = 0;
        try {
            date = dateFormat.parse(time);
            Log.d(TAG, "getMonth: "+date);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            int year  = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            c.clear();
            c.set(year,month,1);
            return c.getTimeInMillis();
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return ms;
    }

    public long getNextMonthFromString(String time) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date;
        long ms = 0;
        try {
            date = dateFormat.parse(time);
            Log.d(TAG, "getMonth: "+date);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            int year  = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            c.clear();
            c.set(year,month,1);
            c.add(Calendar.MONTH,1);
            return c.getTimeInMillis();
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return ms;
    }

    public long getNextMonthFromMs(long ms){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ms);

        calendar.add(Calendar.MONTH, 1);

        return calendar.getTimeInMillis();
    }



}
