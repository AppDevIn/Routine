package com.mad.p03.np2020.routine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mad.p03.np2020.routine.Adapter.HomePageAdapter;
import com.mad.p03.np2020.routine.Adapter.MySpinnerApater;
import com.mad.p03.np2020.routine.Class.Section;
import com.mad.p03.np2020.routine.Class.Task;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {

    //Declare Constants
    final String TAG = "Home Activity";


    //Declare member variables
    GridView mGridView;
    HomePageAdapter mHomePageAdapter;
    ImageButton mImgAdd;
    EditText mEditAddList;
    Spinner mSpinnerColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //TODO: Find view by ID
        mGridView = findViewById(R.id.section_grid_view);
        mImgAdd = findViewById(R.id.imgBtnTodo);
        mEditAddList = findViewById(R.id.txtAddList);
        mSpinnerColor = findViewById(R.id.spinnerColor);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //TODO: Initialize any value
        mHomePageAdapter = new HomePageAdapter(this,R.layout.home_grid_view_items, hardCodedList());
        mGridView.setAdapter(mHomePageAdapter);

        Integer[] colors = { getResources().getColor(R.color.colorFocus), getResources().getColor(R.color.colorHistory), getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimaryDark)};
        //Declaring a custom adapter
        mSpinnerColor.setAdapter(new MySpinnerApater( colors));

        //Set the cursor to the start
        mEditAddList.setSelection(0);





    }

    @Override
    protected void onResume() {
        super.onResume();

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Logging the name of the list clicked
                Log.i("Home Activity", "onItemClick: "+hardCodedList().get(i).getName());
            }
        });
        
        mImgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: Adding new list clicked");

                //Make the edit text visible for the use to type
                mEditAddList.setVisibility(View.VISIBLE);


            }
        });

        mEditAddList.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    String listName = textView.getText().toString();
                    Log.i(TAG, "onEditorAction: " + listName);

                    addSection(listName);

                    //The Textview disappear after entered is pressed
                    mEditAddList.setVisibility(View.INVISIBLE);

                    //Empty the edit text
                    mEditAddList.setText("");

                    //Hide the soft keyboard
                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert mgr != null;
                    mgr.hideSoftInputFromWindow(mEditAddList.getWindowToken(), 0);

                    return true; // consume.
                }

                return false;
            }
        });




    }


    void addSection(String name){

        Section section = new Section(name, new ArrayList<Task>(),"#CAF4F4");

        //Add new section object into the adapter
        mHomePageAdapter.add(section);

        //TODO: Add the object the user class
    }




    //Temp hard code for fake user data
    List<Section> hardCodedList(){
        List<Section> sectionList = new ArrayList<>();
        List<Task> taskList = new ArrayList<>();

        //TODO: Add in fake data
        taskList.add(new Task("Kill myself"));
        taskList.add(new Task("Kill myself"));
        taskList.add(new Task("Kill myself"));
        taskList.add(new Task("Kill myself"));
        taskList.add(new Task("Kill myself"));
        taskList.add(new Task("Kill myself"));


        sectionList.add(new Section("MAD", taskList,"#CAF4F4"));
        sectionList.add(new Section("Home", taskList,"#B8EFEF"));
        sectionList.add(new Section("Web", taskList, "#FFEAAC"));
        sectionList.add(new Section("School", taskList, "#FFC2B4"));


        return sectionList;

    }
}

