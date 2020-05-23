package com.mad.p03.np2020.routine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
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
import android.widget.Button;
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
import com.mad.p03.np2020.routine.database.SectionDBHelper;

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
    CardView mCardViewPopUp;
    Button mBtnAdd;
    List<Section> mSectionList;
    SectionDBHelper mSectionDBHelper;
    Integer[] mColors;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "UI is being created");

        //TODO: Find view by ID
        mGridView = findViewById(R.id.section_grid_view);
        mImgAdd = findViewById(R.id.imgBtnTodo);
        mEditAddList = findViewById(R.id.txtAddList);
        mSpinnerColor = findViewById(R.id.spinnerColor);
        mCardViewPopUp = findViewById(R.id.cardViewPopUp);
        mBtnAdd = findViewById(R.id.btnAdd);

        //Get all the section data from firebase
        mSectionDBHelper = new SectionDBHelper(this);
        mSectionList = mSectionDBHelper.getAllSections();
        Log.d(TAG, "onCreate(): Data received from SQL");

        //A list of possible colors to be user
        mColors = new Integer[]{getResources().getColor(R.color.colorFocus), getResources().getColor(R.color.colorHistory), getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimaryDark)};
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: GUI is ready");

//        //TODO: Initialize any value
        mHomePageAdapter = new HomePageAdapter(this,R.layout.home_grid_view_items, mSectionList);
        mGridView.setAdapter(mHomePageAdapter);

        //Declaring a custom adapter
        mSpinnerColor.setAdapter(new MySpinnerApater( mColors));

        //Set the cursor to the start
        mEditAddList.setSelection(0);

        //Make sure the card view is not visible
        mCardViewPopUp.setVisibility(View.INVISIBLE);
        Log.d(TAG, "onStart(): Card view is set to invisible");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: GUI is in the Foreground and Interactive");

        //Setting each item in the listener clickable
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //TODO: Logging the name of the list clicked
                Log.d(TAG, "onItemClick(): " + mSectionList.get(i).getName() + " has been clicked");
            }
        });

        //When the add button is clicked make the card view visible
        mImgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick(): Adding new list clicked");

                //Make the card view visible to the user
                mCardViewPopUp.setVisibility(View.VISIBLE);

            }
        });

        mEditAddList.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    Log.d(TAG, "onEditorAction(): User eneted \"ENTER\" in keyboard ");
                    updateCardUI(textView);

                    return true;
                }

                return false;
            }
        });

        //When the add button is clicked
        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick(): Add button is pressed ");
                updateCardUI(mEditAddList);
            }
        });

    }

    /**
     *
     * To add the data into firebase and SQL
     * Create a section object.
     * Set the cardview invisible and hide the keyboard
     *
     * @param textView the is the edittext
     */
    private void updateCardUI(TextView textView){
        String listName = textView.getText().toString();
        Log.i(TAG, "onEditorAction: " + listName);

        //Create a Section Object
        Section section = new Section(textView.getText().toString().trim(), mColors[mSpinnerColor.getSelectedItemPosition()]);

        //Add to List<Section>
        mSectionList.add(section);

        //TODO: Save to SQL
        mSectionDBHelper.insertSection(section);

        //TODO: Save to firebase

        //The card view will disappear
        mCardViewPopUp.setVisibility(View.INVISIBLE);

        //Hide the soft keyboard
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert mgr != null;
        mgr.hideSoftInputFromWindow(mEditAddList.getWindowToken(), 0);
    }
}

