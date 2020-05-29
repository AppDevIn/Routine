package com.mad.p03.np2020.routine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
    Button notesFragment;
    Button stepFragment;
    boolean stepStatus = false;
    boolean noteStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_layout);

        stepFragment = findViewById(R.id.stepFragment);
        notesFragment = findViewById(R.id.notesFragment);

        stepFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                if (!stepStatus){
                    StepsFragment stepFrag = new StepsFragment();
                    fragmentTransaction.add(R.id.fragmentContainer, stepFrag);
                    fragmentTransaction.commit();
                }
            }
        });

        notesFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                if (!noteStatus){
                    NotesFragment noteFrag = new NotesFragment();
                    fragmentTransaction.add(R.id.fragmentContainer, noteFrag);
                    fragmentTransaction.commit();
                }
            }
        });
    }
}
