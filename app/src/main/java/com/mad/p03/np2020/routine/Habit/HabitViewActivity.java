package com.mad.p03.np2020.routine.Habit;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
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
import androidx.core.content.res.ResourcesCompat;

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
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.helpers.IntegerFormatter;
import com.mad.p03.np2020.routine.models.Habit;
import com.mad.p03.np2020.routine.models.HabitRepetition;
import com.mad.p03.np2020.routine.models.User;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class HabitViewActivity extends AppCompatActivity {

    private static final String TAG = "HabitViewActivity";

    public static final int[] chart_buttonIDS = new int[]{R.id.habit_week_barChart,R.id.habit_month_barChart,R.id.habit_year_barChart};

    // Widgets
    private TextView title, goal_text, curr_streak_text, best_streak_text, progress_text, curr_period, best_period, progress_text_period, range_indicator;
    private CardView habit_card;
    private ImageView back_btn, editBtn, chart_left, chart_right;
    private ProgressBar progressBar;
    private BarChart habit_barChart;
    private Typeface tf;
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
        range_indicator = findViewById(R.id.range_indicator);
        chart_left = findViewById(R.id.chart_left);
        chart_right = findViewById(R.id.chart_right);

        tf = ResourcesCompat.getFont(this, R.font.montserrat_regular);

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
                    curr_streak = (max_count >= occurrence) ? ++curr_streak : 0;
                    max_streak = Math.max(max_streak, curr_streak);
                    completion = (max_count >= occurrence) ? ++completion : completion;
                }
                break;
        }

        curr_streak_text.setText(String.valueOf(curr_streak));
        best_streak_text.setText(String.valueOf(max_streak));

        String period_text = getPeriodText(period).toLowerCase();
        String curr_period_text = (curr_streak > 1) ? period_text + "s" : period_text;
        String best_period_text = (max_streak > 1) ? period_text + "s" : period_text;

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

    public void displayWeekBarChart(){
        ArrayList<HabitRepetition> habitRepetitionArrayList = habitRepetitionDBHelper.getAllHabitRepetitionsByHabitID(habit.getHabitID());

        ArrayList<ArrayList<String>> timeStampList = new ArrayList<>();
        ArrayList<ArrayList<BarEntry>> barEntries = new ArrayList<>();
        ArrayList<BarEntry> first_barEntries = new ArrayList<>();

        int x = 0;
        final long[] initial_timestamp ={habitRepetitionArrayList.get(habitRepetitionArrayList.size()-1).getTimestamp()};
        Log.d(TAG, "displayWeekBarChart: initial_timestamp"+initial_timestamp);
        String range = String.format("%s - %s",getStartOfTheWeek(initial_timestamp[0]), getEndOfTheWeek(initial_timestamp[0]));
        range_indicator.setText(range);

        // initial the day before the habit
        addDaysToTimeStampForWeek(timeStampList);
        long firstTimeStamp = habitRepetitionArrayList.get(0).getTimestamp();
        int firstDay = getDayOfWeekFromMs(firstTimeStamp);
        for (int i = 1; i < firstDay; i++){
            first_barEntries.add(new BarEntry(++x, 0));
        }

        for (HabitRepetition hr : habitRepetitionArrayList){
            // to reset week
            if (getDayOfWeekFromMs(hr.getTimestamp()) == 1 && hr.getTimestamp() != firstTimeStamp){
                barEntries.add(first_barEntries);
                first_barEntries = new ArrayList<>();
                x = 0;
                addDaysToTimeStampForWeek(timeStampList);
            }

            first_barEntries.add(new BarEntry(++x, hr.getCount()));
            // set a star sign to indicate today
            if (hr.getTimestamp() == getTodayTimestamp()){
                String ts = timeStampList.get(timeStampList.size()-1).get(x);
                timeStampList.get(timeStampList.size()-1).set(x, ts+"*");
            }
        }


        // add dummy data
        while (first_barEntries.size() % 7 != 0){
            first_barEntries.add(new BarEntry(++x, 0));
        }

        barEntries.add(first_barEntries);
        
        ArrayList<BarEntry> lastEntry = barEntries.get(barEntries.size()-1);
        ArrayList<String> lastTimeStamp = timeStampList.get(timeStampList.size()-1);
        final int[] eIndex = {barEntries.size() - 1};

        if (barEntries.size() <=1){
            chart_left.setVisibility(View.INVISIBLE);
        }else{
            chart_left.setVisibility(View.VISIBLE);
        }
        chart_right.setVisibility(View.INVISIBLE);

        displayWeekBarChartHelper(lastEntry, lastTimeStamp);

        chart_left.setOnClickListener(v -> {
            resetChart();
            if (eIndex[0] - 1 >= 0){
                --eIndex[0];
                initial_timestamp[0] = getLastWeekMs(initial_timestamp[0]);
                range_indicator.setText(String.format("%s - %s",getStartOfTheWeek(initial_timestamp[0]), getEndOfTheWeek(initial_timestamp[0])));
                displayWeekBarChartHelper(barEntries.get(eIndex[0]), timeStampList.get(eIndex[0]));
                if (eIndex[0] - 1 >= 0){
                    chart_left.setVisibility(View.VISIBLE);
                }else{
                    chart_left.setVisibility(View.INVISIBLE);
                }
                chart_right.setVisibility(View.VISIBLE);
            }
        });


        chart_right.setOnClickListener(v -> {
            resetChart();
            if (eIndex[0] + 1 < barEntries.size()){
                ++eIndex[0];
                initial_timestamp[0] = getNextWeekMs(initial_timestamp[0]);
                range_indicator.setText(String.format("%s - %s",getStartOfTheWeek(initial_timestamp[0]), getEndOfTheWeek(initial_timestamp[0])));
                displayWeekBarChartHelper(barEntries.get(eIndex[0]), timeStampList.get(eIndex[0]));
                if (eIndex[0] + 1 < barEntries.size()){
                    chart_right.setVisibility(View.VISIBLE);
                }else{
                    chart_right.setVisibility(View.INVISIBLE);
                }
                chart_left.setVisibility(View.VISIBLE);
            }
        });

    }

    public void displayWeekBarChartHelper(ArrayList<BarEntry> lastEntry, ArrayList<String> lastTimeStamp){
        BarDataSet barDataSet = new BarDataSet(lastEntry, "Habit Bar Chart");
//        barDataSet.setColors(getResources().getColor(habit.returnColorID(habit.getHolder_color())));
        barDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        barDataSet.setValueTextSize(14);
        barDataSet.setDrawValues(false);
        barDataSet.setValueTypeface(tf);

        BarData data = new BarData(barDataSet);
        data.setBarWidth(0.7f);
        data.setValueFormatter(new IntegerFormatter());

        habit_barChart.setData(data);
        habit_barChart.animateY(750);
        habit_barChart.setDrawBarShadow(false);
        habit_barChart.setDrawValueAboveBar(false);
        habit_barChart.setVisibleXRangeMaximum(lastEntry.size());
        habit_barChart.moveViewToX(lastEntry.size());
        habit_barChart.setPinchZoom(false);
        habit_barChart.setDrawGridBackground(true);
        habit_barChart.getAxisRight().setEnabled(false);
        habit_barChart.getLegend().setEnabled(false);
        habit_barChart.setClickable(false);
        habit_barChart.setDoubleTapToZoomEnabled(false);
        habit_barChart.setTouchEnabled(false);

        Description description = new Description();
        description.setText("");
        habit_barChart.setDescription(description);

        XAxis xAxis = habit_barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(lastTimeStamp));
        xAxis.setTextSize(11);
        xAxis.setTypeface(tf);

        YAxis yAxis = habit_barChart.getAxisLeft();
        yAxis.setTextSize(11);
        yAxis.setTypeface(tf);
        yAxis.setLabelCount(5);

        habit_barChart.getXAxis().resetAxisMinimum();
        habit_barChart.getXAxis().setAvoidFirstLastClipping(false);
        habit_barChart.notifyDataSetChanged();
        habit_barChart.invalidate();


        if (isRangeSumZero(lastEntry)){
            Log.d(TAG, "displayWeekBarChart: zero");
            habit_barChart.getAxisLeft().resetAxisMinimum();
        }else{
            yAxis.setAxisMinimum(0f);
            double rangeMax = calculateRangeMaximum(lastEntry) * 1.0;
            float max_y = (float) (5*(Math.ceil(Math.abs(rangeMax/5))));
            yAxis.setAxisMaximum(max_y);
        }

    }

    public void displayMonthBarChart(){
        ArrayList<HabitRepetition> habitRepetitionArrayList = habitRepetitionDBHelper.getAllHabitRepetitionsByHabitID(habit.getHabitID());

        ArrayList<ArrayList<String>> timeStampList = new ArrayList<>();
        ArrayList<ArrayList<BarEntry>> barEntries = new ArrayList<>();
        ArrayList<BarEntry> first_barEntries = new ArrayList<>();

        int x = 0;
        final long[] initial_timestamp ={habitRepetitionArrayList.get(habitRepetitionArrayList.size()-1).getTimestamp()};
        String range = String.format("%s - %s",getStartOfTheMonth(initial_timestamp[0]), getEndOfTheMonth(initial_timestamp[0]));
        range_indicator.setText(range);

        // initial the month before the habit
        long firstTimeStamp = habitRepetitionArrayList.get(0).getTimestamp();
        addDaysToTimeStampForMonth(timeStampList, getMaxDayOfTheMonth(firstTimeStamp), getMonthString(firstTimeStamp));
        long conTimeStamp = firstTimeStamp;
        int firstDay = getDayOfMonthFromMs(firstTimeStamp);
        for (int i = 1; i < firstDay; i++){
            first_barEntries.add(new BarEntry(++x, 0));
        }

        for (HabitRepetition hr : habitRepetitionArrayList){
            // to reset week
            if (getDayOfMonthFromMs(hr.getTimestamp()) == 1 && hr.getTimestamp() != firstTimeStamp){
                barEntries.add(first_barEntries);
                first_barEntries = new ArrayList<>();
                x = 0;
                conTimeStamp = getNextMonthFromMs(conTimeStamp);
                Log.d(TAG, "displayWeekBarChart: "+conTimeStamp);
                addDaysToTimeStampForMonth(timeStampList, getMaxDayOfTheMonth(conTimeStamp),getMonthString(conTimeStamp));
            }

            first_barEntries.add(new BarEntry(++x, hr.getCount()));
        }

        int lastDay = getDayOfMonthFromMs(initial_timestamp[0]);
        int maxDay = getMaxDayOfTheMonth(initial_timestamp[0]);

        while (lastDay < maxDay){
            first_barEntries.add(new BarEntry(++x, 0));
            ++lastDay;
        }

        barEntries.add(first_barEntries);

        ArrayList<BarEntry> lastEntry = barEntries.get(barEntries.size()-1);
        ArrayList<String> lastTimeStamp = timeStampList.get(timeStampList.size()-1);
        final int[] eIndex = {barEntries.size() - 1};

        if (barEntries.size() <= 1){
            chart_left.setVisibility(View.INVISIBLE);
        }else{
            chart_left.setVisibility(View.VISIBLE);
        }
        chart_right.setVisibility(View.INVISIBLE);

        displayMonthBarChartHelper(lastEntry, lastTimeStamp);

        chart_left.setOnClickListener(v -> {
            resetChart();
            if (eIndex[0] - 1 >= 0){
                --eIndex[0];
                initial_timestamp[0] = getLastMonthMs(initial_timestamp[0]);
                range_indicator.setText(String.format("%s - %s",getStartOfTheMonth(initial_timestamp[0]), getEndOfTheMonth(initial_timestamp[0])));
                displayMonthBarChartHelper(barEntries.get(eIndex[0]), timeStampList.get(eIndex[0]));
                if (eIndex[0] - 1 >= 0){
                    chart_left.setVisibility(View.VISIBLE);
                }else{
                    chart_left.setVisibility(View.INVISIBLE);
                }
                chart_right.setVisibility(View.VISIBLE);
            }

        });

        chart_right.setOnClickListener(v ->{
            resetChart();
            if (eIndex[0] + 1 < barEntries.size()){
                ++eIndex[0];
                initial_timestamp[0] = getNextMonthMs(initial_timestamp[0]);
                range_indicator.setText(String.format("%s - %s",getStartOfTheMonth(initial_timestamp[0]), getEndOfTheMonth(initial_timestamp[0])));
                displayMonthBarChartHelper(barEntries.get(eIndex[0]), timeStampList.get(eIndex[0]));
                if (eIndex[0] + 1 < barEntries.size()){
                    chart_right.setVisibility(View.VISIBLE);
                }else{
                    chart_right.setVisibility(View.INVISIBLE);
                }
                chart_left.setVisibility(View.VISIBLE);

            }
        });

    }

    public void displayMonthBarChartHelper(ArrayList<BarEntry> lastEntry, ArrayList<String> lastTimeStamp){
        BarDataSet barDataSet = new BarDataSet(lastEntry, "Habit Month Bar Chart");
//        barDataSet.setColors(getResources().getColor(habit.returnColorID(habit.getHolder_color())));
        barDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        barDataSet.setValueTextSize(14);
        barDataSet.setDrawValues(false);
        barDataSet.setValueTypeface(tf);

        BarData data = new BarData(barDataSet);
        data.setBarWidth(0.7f);
        data.setValueFormatter(new IntegerFormatter());

        habit_barChart.setData(data);
        habit_barChart.animateY(750);
        habit_barChart.setDrawBarShadow(false);
        habit_barChart.setDrawValueAboveBar(false);
        habit_barChart.setVisibleXRangeMaximum(lastEntry.size());
        habit_barChart.moveViewToX(lastEntry.size());
        habit_barChart.setPinchZoom(false);
        habit_barChart.setDrawGridBackground(true);
        habit_barChart.getAxisRight().setEnabled(false);
        habit_barChart.getLegend().setEnabled(false);
        habit_barChart.setClickable(false);
        habit_barChart.setDoubleTapToZoomEnabled(false);
        habit_barChart.setTouchEnabled(false);

        Description description = new Description();
        description.setText("");
        habit_barChart.setDescription(description);

        XAxis xAxis = habit_barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(5);
        xAxis.setLabelCount(lastEntry.size());
        xAxis.setValueFormatter(new IndexAxisValueFormatter(lastTimeStamp));
        xAxis.setTextSize(11);
        xAxis.setTypeface(tf);

        YAxis yAxis = habit_barChart.getAxisLeft();
        yAxis.setTextSize(11);
        yAxis.setTypeface(tf);
        yAxis.setLabelCount(5);

        habit_barChart.getXAxis().setAxisMinimum(0f);
        habit_barChart.getXAxis().setAvoidFirstLastClipping(true);
        habit_barChart.notifyDataSetChanged();
        habit_barChart.invalidate();

        if (isRangeSumZero(lastEntry)){
            Log.d(TAG, "displayMonthBarChartHelper: zero");
        }else{
            yAxis.setAxisMinimum(0f);
            double rangeMax = calculateRangeMaximum(lastEntry) * 1.0;
            float max_y = (float) (5*(Math.ceil(Math.abs(rangeMax/5))));
            yAxis.setAxisMaximum(max_y);
        }
    }

    public void displayYearBarChart(){
        ArrayList<HabitRepetition> habitRepetitionArrayList = habitRepetitionDBHelper.getAllHabitRepetitionsByHabitID(habit.getHabitID());

        ArrayList<ArrayList<String>> timeStampList = new ArrayList<>();
        ArrayList<ArrayList<BarEntry>> barEntries = new ArrayList<>();
        ArrayList<BarEntry> first_barEntries = new ArrayList<>();

        int x = 0;
        final long[] initial_timestamp ={habitRepetitionArrayList.get(habitRepetitionArrayList.size()-1).getTimestamp()};
        String range = String.format("%s - %s",getStartOfTheYear(initial_timestamp[0]), getEndOfTheYear(initial_timestamp[0]));
        range_indicator.setText(range);

        // initial the month before the habit
        addMonthsToTimeStampForYear(timeStampList);
        long firstTimeStamp = habitRepetitionArrayList.get(0).getTimestamp();
        int firstMonth = getMonthOfYearFromMs(firstTimeStamp);
        for (int i = 0; i < firstMonth; i++){
            first_barEntries.add(new BarEntry(++x, 0));
        }


        long initial_ms = getMonth(habit.getTime_created());
        long next_ms = getNextMonthFromString(habit.getTime_created());

        boolean isNextMonth;

        do{
            int count = habitRepetitionDBHelper.getCountBetweenMonth(habit.getHabitID(), initial_ms, next_ms);
            first_barEntries.add(new BarEntry(++x, count));
            if (getMonthFromMs(next_ms) == 0 && next_ms > firstTimeStamp){
                barEntries.add(first_barEntries);
                first_barEntries = new ArrayList<>();
                x = 0;
                addMonthsToTimeStampForYear(timeStampList);
            }

            initial_ms = next_ms;
            next_ms = getNextMonthFromMs(next_ms);
            isNextMonth = habitRepetitionDBHelper.isNextMonth(habit.getHabitID(), initial_ms);

        }while(isNextMonth);

        int lastMonth = getMonthFromMs(initial_timestamp[0]);
        int maxMonth = 11;

        while (lastMonth < maxMonth){
            first_barEntries.add(new BarEntry(++x, 0));
            ++lastMonth;
        }

        barEntries.add(first_barEntries);

        ArrayList<BarEntry> lastEntry = barEntries.get(barEntries.size()-1);
        ArrayList<String> lastTimeStamp = timeStampList.get(timeStampList.size()-1);
        final int[] eIndex = {barEntries.size() - 1};

        if (barEntries.size() <= 1){
            chart_left.setVisibility(View.INVISIBLE);
        }else{
            chart_left.setVisibility(View.VISIBLE);
        }
        chart_right.setVisibility(View.INVISIBLE);

        displayYearBarChartHelper(lastEntry, lastTimeStamp);

        chart_left.setOnClickListener(v -> {
            resetChart();
            if (eIndex[0] - 1 >= 0){
                --eIndex[0];
                initial_timestamp[0] = getLastYearMs(initial_timestamp[0]);
                range_indicator.setText(String.format("%s - %s",getStartOfTheYear(initial_timestamp[0]), getEndOfTheYear(initial_timestamp[0])));
                displayYearBarChartHelper(barEntries.get(eIndex[0]), timeStampList.get(eIndex[0]));
                if (eIndex[0] - 1 >= 0){
                    chart_left.setVisibility(View.VISIBLE);
                }else{
                    chart_left.setVisibility(View.INVISIBLE);
                }
                chart_right.setVisibility(View.VISIBLE);
            }

        });

        chart_right.setOnClickListener(v ->{
            resetChart();
            if (eIndex[0] + 1 < barEntries.size()){
                ++eIndex[0];
                initial_timestamp[0] = getNextYearMs(initial_timestamp[0]);
                range_indicator.setText(String.format("%s - %s",getStartOfTheYear(initial_timestamp[0]), getEndOfTheYear(initial_timestamp[0])));
                displayYearBarChartHelper(barEntries.get(eIndex[0]), timeStampList.get(eIndex[0]));
                if (eIndex[0] + 1 < barEntries.size()){
                    chart_right.setVisibility(View.VISIBLE);
                }else{
                    chart_right.setVisibility(View.INVISIBLE);
                }
                chart_left.setVisibility(View.VISIBLE);

            }
        });
    }

    public void displayYearBarChartHelper(ArrayList<BarEntry> lastEntry, ArrayList<String> lastTimeStamp){
        BarDataSet barDataSet = new BarDataSet(lastEntry, "Habit Year Bar Chart");
//        barDataSet.setColors(getResources().getColor(habit.returnColorID(habit.getHolder_color())));
        barDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        barDataSet.setValueTextSize(14);
        barDataSet.setDrawValues(false);
        barDataSet.setValueTypeface(tf);

        BarData data = new BarData(barDataSet);
        data.setBarWidth(0.7f);
        data.setValueFormatter(new IntegerFormatter());

        habit_barChart.setData(data);
        habit_barChart.animateY(750);
        habit_barChart.setDrawBarShadow(false);
        habit_barChart.setDrawValueAboveBar(false);
        habit_barChart.setVisibleXRangeMaximum(lastEntry.size());
        habit_barChart.moveViewToX(lastEntry.size());
        habit_barChart.setPinchZoom(false);
        habit_barChart.setDrawGridBackground(true);
        habit_barChart.getAxisRight().setEnabled(false);
        habit_barChart.getLegend().setEnabled(false);
        habit_barChart.setClickable(false);
        habit_barChart.setDoubleTapToZoomEnabled(false);
        habit_barChart.setTouchEnabled(false);

        Description description = new Description();
        description.setText("");
        habit_barChart.setDescription(description);

        XAxis xAxis = habit_barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1);
        xAxis.setLabelCount(lastEntry.size());
        xAxis.setValueFormatter(new IndexAxisValueFormatter(lastTimeStamp));
        xAxis.setTextSize(11);
        xAxis.setTypeface(tf);

        YAxis yAxis = habit_barChart.getAxisLeft();
        yAxis.setTextSize(11);
        yAxis.setTypeface(tf);
        yAxis.setLabelCount(5);

        habit_barChart.getXAxis().resetAxisMinimum();
        habit_barChart.getXAxis().setAvoidFirstLastClipping(false);
        habit_barChart.notifyDataSetChanged();
        habit_barChart.invalidate();

        if (isRangeSumZero(lastEntry)){
            Log.d(TAG, "displayYearBarChartHelper: zero");
            habit_barChart.getAxisLeft().resetAxisMinimum();
        }else{
            yAxis.setAxisMinimum(0f);
            double rangeMax = calculateRangeMaximum(lastEntry) * 1.0;
            float max_y = (float) (5*(Math.ceil(Math.abs(rangeMax/5))));
            yAxis.setAxisMaximum(max_y);
        }
    }

    public void populateChartButtons(){
        Button button = findViewById(chart_buttonIDS[0]);
        button.setBackgroundColor(getResources().getColor(R.color.colorWhiteGrey));
        displayWeekBarChart();

        for (final int iD : chart_buttonIDS){
            final Button btn = findViewById(iD);

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = btn.getId();

                    for (int i = 0; i < 3; i++){
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
                Log.d(TAG, "displayCharts: Week");
                resetChart();
                displayWeekBarChart();
                break;

            case 1:
                Log.d(TAG, "displayCharts: Month");
                resetChart();
                displayMonthBarChart();
                break;

            case 2:
                Log.d(TAG, "displayCharts: Year");
                resetChart();
                displayYearBarChart();
                break;
        }
    }

    public void resetChart(){
        Log.d(TAG, "resetChart: ");
        range_indicator.setText("");
        habit_barChart.fitScreen();
        habit_barChart.clearValues();
        habit_barChart.getData().clearValues();
        habit_barChart.getXAxis().resetAxisMinimum();
        habit_barChart.getXAxis().resetAxisMaximum();
        habit_barChart.getAxisLeft().resetAxisMaximum();
        habit_barChart.getAxisLeft().resetAxisMaximum();
        habit_barChart.clear();
        habit_barChart.invalidate();
        habit_barChart.notifyDataSetChanged();
    }

    public long getNextWeekMs(long ms){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ms);
        calendar.add(Calendar.WEEK_OF_YEAR,1);

        return calendar.getTimeInMillis();
    }

    public long getLastWeekMs(long ms){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ms);
        calendar.add(Calendar.WEEK_OF_YEAR,-1);

        return calendar.getTimeInMillis();
    }

    public String getStartOfTheWeek(long ms){
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(ms);
        Log.d(TAG, "getStartOfTheWeek: "+ms);
        cal.set(Calendar.DAY_OF_WEEK, 1);
        Date d = cal.getTime();
        String date = dateFormat.format(d);
        return date;
    }

    public String getEndOfTheWeek(long ms){
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(ms);
        Log.d(TAG, "getEndOfTheWeek: "+ms);
        cal.set(Calendar.DAY_OF_WEEK, 7);
        Date d = cal.getTime();
        String date = dateFormat.format(d);
        return date;
    }

    public int getDayOfWeekFromMs(long ms){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ms);
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        return day;
    }

    public long getMonth(String time) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date;
        long ms = 0;
        try {
            date = dateFormat.parse(time);
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

    public int getMonthFromMs(long ms){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ms);


        return calendar.get(Calendar.MONTH);
    }

    public long getNextMonthFromMs(long ms){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ms);

        calendar.add(Calendar.MONTH, 1);

        return calendar.getTimeInMillis();
    }

    public void addDaysToTimeStampForWeek(ArrayList<ArrayList<String>> arr){
        ArrayList<String> timestampList = new ArrayList<>();
        Log.d(TAG, "addDaysToTimeStamp: next week");
        timestampList.add("0");
        timestampList.add("SUN");
        timestampList.add("MON");
        timestampList.add("TUE");
        timestampList.add("WED");
        timestampList.add("THU");
        timestampList.add("FRI");
        timestampList.add("SAT");
        arr.add(timestampList);
    }

    public long getTodayTimestamp(){
        Calendar cal = Calendar.getInstance();
        int year  = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int date  = cal.get(Calendar.DATE);
        cal.clear();
        cal.set(year, month, date);

        return cal.getTimeInMillis();
    }

    public int calculateRangeMaximum(ArrayList<BarEntry> arr){
        int max = 0;
        for (int i = 0; i < arr.size(); i++){
            max = (int) Math.max(max, arr.get(i).getY());
        }

        return max;
    }

    public boolean isRangeSumZero(ArrayList<BarEntry> arr){
        int sum = 0;
        for (int i = 0; i < arr.size(); i++){
            sum += arr.get(i).getY();
        }

        return sum == 0;
    }

    public String getStartOfTheMonth(long ms){
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(ms);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date d = cal.getTime();
        String date = dateFormat.format(d);
        return date;
    }

    public String getEndOfTheMonth(long ms){
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(ms);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date d = cal.getTime();
        String date = dateFormat.format(d);
        return date;
    }

    public int getMaxDayOfTheMonth(long ms){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(ms);

        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public int getMonthString(long ms){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(ms);

        return cal.get(Calendar.MONTH)+1;
    }

    public void addDaysToTimeStampForMonth(ArrayList<ArrayList<String>> timestampList, int max_of_month, int month){
        ArrayList<String> arr = new ArrayList<>();
        for (int i = 1; i <= max_of_month; i++){
            String x = String.format("%d/%d",i,month);
            arr.add(x);
        }
        timestampList.add(arr);

    }

    public int getDayOfMonthFromMs(long ms){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ms);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return day;
    }

    public long getNextMonthMs(long ms){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ms);
        calendar.add(Calendar.MONTH,1);

        return calendar.getTimeInMillis();
    }

    public long getLastMonthMs(long ms){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ms);
        calendar.add(Calendar.MONTH,-1);

        return calendar.getTimeInMillis();
    }

    public String getStartOfTheYear(long ms){
        DateFormat dateFormat = new SimpleDateFormat("MMM yyyy");
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(ms);
        cal.set(Calendar.DAY_OF_YEAR, 1);
        Date d = cal.getTime();
        String date = dateFormat.format(d);
        return date;
    }

    public String getEndOfTheYear(long ms){
        DateFormat dateFormat = new SimpleDateFormat("MMM yyyy");
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(ms);
        cal.set(Calendar.MONTH,11);
        Date d = cal.getTime();
        String date = dateFormat.format(d);
        return date;
    }

    public void addMonthsToTimeStampForYear(ArrayList<ArrayList<String>> arr){
        Log.d(TAG, "addMonthsToTimeStampForYear: ");
        ArrayList<String> timestampList = new ArrayList<>();
        timestampList.add("0");
        timestampList.add("J");
        timestampList.add("F");
        timestampList.add("M");
        timestampList.add("A");
        timestampList.add("M");
        timestampList.add("J");
        timestampList.add("J");
        timestampList.add("A");
        timestampList.add("S");
        timestampList.add("O");
        timestampList.add("N");
        timestampList.add("D");

        arr.add(timestampList);

    }

    public int getMonthOfYearFromMs(long ms){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ms);
        int day = calendar.get(Calendar.MONTH);

        return day;
    }

    public long getNextYearMs(long ms){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ms);
        calendar.add(Calendar.YEAR,1);

        return calendar.getTimeInMillis();
    }

    public long getLastYearMs(long ms){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ms);
        calendar.add(Calendar.YEAR,-1);

        return calendar.getTimeInMillis();
    }

}
