package com.mad.p03.np2020.routine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.mad.p03.np2020.routine.Class.Steps;

import java.util.ArrayList;
import java.util.List;

public class CardActivity extends AppCompatActivity {

    final String TAG = "Card Layout";
    ArrayList<String> data = new ArrayList<>();
    ArrayList<Steps> StepItem = new ArrayList<>();
    RecyclerView cardRecycler;
    Button stepAddButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_layout);

        stepAddButton = findViewById(R.id.stepAddButton);

    }
}
