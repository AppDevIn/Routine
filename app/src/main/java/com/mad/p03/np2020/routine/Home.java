package com.mad.p03.np2020.routine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mad.p03.np2020.routine.Adapter.HomePageAdapter;
import com.mad.p03.np2020.routine.Adapter.MyHomeItemTouchHelper;
import com.mad.p03.np2020.routine.Adapter.MySpinnerColorAdapter;
import com.mad.p03.np2020.routine.Adapter.MySpinnerIconsAdapter;
import com.mad.p03.np2020.routine.Class.Section;
import com.mad.p03.np2020.routine.Class.User;
import com.mad.p03.np2020.routine.database.MyDatabaseListener;
import com.mad.p03.np2020.routine.database.SectionDBHelper;

import java.util.List;



/**
 *
 * The controller class for xml layout activity_home
 * This will manage things that are on the home activity
 *
 * @author Jeyavishnu
 * @since 04-06-2020
 *
 */
public class Home extends AppCompatActivity implements MyDatabaseListener {

    //Declare Constants
    final String TAG = "Home Activity";


    //Declare member variables
    RecyclerView mGridView;
    HomePageAdapter mHomePageAdapter;

    EditText mEditAddList;
    Spinner mSpinnerColor, mSpinnerIcons;
    CardView mCardViewPopUp;
    Button mBtnAdd, mBtnCancel;
    FloatingActionButton mImgAdd;
    List<Section> mSectionList;
    SectionDBHelper mSectionDBHelper;
    Integer[] mColors, mBackgrounds;
    String mUID;
    User mUser;



    /**
     *
     * This is used to get the ID of for the view and initialize the recycler
     * view for the tasks. Setting the onclick lister too and also setting 2 custom
     * spinner adapter one for color and the other for icon
     *
     * @param savedInstanceState will be null at first as
     *                           the orientation changes it will get
     *                           in use
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "UI is being created");

        //TODO: Get user from the intent
        mUser = getIntent().getParcelableExtra("user");
//        mUID = mUser.getUID();

        mUID = "aRbjnh0WpNe8gGga1PkDfOiJLH03";



        //Find view by ID
        mGridView = findViewById(R.id.section_grid_view);
        mImgAdd = findViewById(R.id.imgBtnTodo);
        mEditAddList = findViewById(R.id.txtAddList);
        mSpinnerColor = findViewById(R.id.spinnerColor);
        mSpinnerIcons = findViewById(R.id.spinnerImg);
        mCardViewPopUp = findViewById(R.id.cardViewPopUp);
        mBtnAdd = findViewById(R.id.btnAdd);
        mBtnCancel = findViewById(R.id.btnCancel);



        //Get all the section data from SQL
        mSectionDBHelper = new SectionDBHelper(this);
        mSectionList = mSectionDBHelper.getAllSections(mUID);
        Log.d(TAG, "onCreate(): Data received from SQL");

        //A list of possible colors to be user
        mColors = new Integer[]{getResources().getColor(R.color.superiorityBlue), getResources().getColor(R.color.rosyBrown), getResources().getColor(R.color.mandarin), getResources().getColor(R.color.green_yellow), getResources().getColor(R.color.turquoise)};

        //A list of possible background to be user
        mBackgrounds = new Integer[] {R.drawable.amazon, R.drawable.android, R.drawable.laptop, R.drawable.code, R.drawable.bookmark};


        //To set to Full screen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        //Recycler view setup
        mGridView.setLayoutManager(new GridLayoutManager(Home.this,2)); //Setting the layout manager with the column of 2
        mGridView.addItemDecoration(new DividerItemDecoration(25));


        // Initialize any value
        mHomePageAdapter = new HomePageAdapter(mSectionList, this);
        mGridView.setAdapter(mHomePageAdapter);

        //Declaring a custom adapter
        mSpinnerColor.setAdapter(new MySpinnerColorAdapter( mColors)); // For the color
        mSpinnerIcons.setAdapter(new MySpinnerIconsAdapter(mBackgrounds)); //For the background

        //Set the cursor to the start
        mEditAddList.setSelection(0);

        //Setting up touchhelper
        ItemTouchHelper.Callback callback = new MyHomeItemTouchHelper(mHomePageAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        mHomePageAdapter.setTouchHelper(itemTouchHelper);
        itemTouchHelper.attachToRecyclerView(mGridView);

        //Subscribing to the topic to listen to
        FirebaseMessaging.getInstance().subscribeToTopic(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Done Running");
                            Toast.makeText(Home.this, FirebaseAuth.getInstance().getCurrentUser().getUid().toString(), Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(Home.this, "Never subscribe yet", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Home.this, "Gone", Toast.LENGTH_SHORT).show();

            }
        });


        //Bottom Nav
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavViewBar);
        bottomNavInit(bottomNavigationView);



    }

    /**
     * Make the card view disappear and the set the image resource
     * the floating button
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: GUI is ready");


        //Make sure the card view is not visible
        mCardViewPopUp.setVisibility(View.INVISIBLE);
        Log.d(TAG, "onStart(): Card view is set to invisible");


        mImgAdd.setImageResource(R.drawable.ic_add_black_24dp);
    }

    /**
     *
     * Used when interacting with the user
     * This when the onClickListener for the buttons are at and
     * an OnEditorActionListener
     *
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: GUI is in the Foreground and Interactive");

        //When the add button is clicked make the card view visible
        mImgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick(): Adding new list clicked");

                //Make the card view visible to the user
                mCardViewPopUp.setVisibility(View.VISIBLE);
                Log.d(TAG, "onClick(): Cardview is visible");

            }
        });

        //Checks for the enter action from the keyboard
        mEditAddList.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    Log.d(TAG, "onEditorAction(): User eneted \"ENTER\" in keyboard ");
                    addSection(textView);

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
                addSection(mEditAddList);
            }
        });


        //If the cancel button is pressed
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCard();
            }
        });

        SectionDBHelper.setMyDatabaseListener(this);

    }

    /**
     * When the activity stops it will update the position
     * in the database
     */
    @Override
    protected void onStop() {
        super.onStop();

        SectionDBHelper sectionDBHelper = new SectionDBHelper(this);
        for (int i = 0; i < mSectionList.size(); i++) {
            mSectionList.get(i).setPosition(i);
            sectionDBHelper.updatePosition(mSectionList.get(i));

        }
    }

    /**
     *
     * Triggered to add to the current adapter list
     * when it is added to the sql
     *
     * @param object given from the SQL when triggered
     *               for this the object is section
     */
    @Override
    public void onDataAdd(final Object object) {
        Log.d(TAG, "A new section has been added: " + object.toString());

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Add to List<Section>
                mHomePageAdapter.addItem((Section) object);
            }
        });
    }

    @Override
    public void onDataDelete(String ID) {
        for (int position = 0; position < mSectionList.size(); position++) {


            if(mSectionList.get(position).getID().equals(ID)){
                final int finalPosition = position;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mHomePageAdapter.removeItem(finalPosition);
                    }
                });
                break;
            }
        }
    }



    /**
     *
     * To add the data into firebase and SQL from the card
     * Create a section object.
     *
     * @param textView the is the edittext
     */
    private void addSection(TextView textView){
        String listName = textView.getText().toString();
        Log.i(TAG, "adding confirmed for " + listName);

        //Create a Section Object for the user input
        Section section = new Section(textView.getText().toString().trim(), mColors[mSpinnerColor.getSelectedItemPosition()], mBackgrounds[mSpinnerIcons.getSelectedItemPosition()], mUID);

        //Save to SQL
        section.addSection(this);
        Log.d(TAG, "updateCardUI(): Added to SQL");

        //Save to firebase
        section.executeFirebaseSectionUpload(mUID, section.getID(), this);

        //Function that make the cardview invisible and hide keyboard
        updateCard();
    }



    /**
     *  Set the cardview invisible and hide the keyboard
     *
     */
    void updateCard(){
        //The card view will disappear
        mCardViewPopUp.setVisibility(View.INVISIBLE);
        Log.d(TAG, "updateCardUI(): Card view is invisible");

        //Hide the soft keyboard
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert mgr != null;
        mgr.hideSoftInputFromWindow(mEditAddList.getWindowToken(), 0);
    }

    /**
     *
     * To set the bottom nav to listen to item changes
     * and chose the item that have been selected
     *
     * @param bottomNavigationView The botomNav that needs to be set to listen
     */
    private void bottomNavInit( BottomNavigationView bottomNavigationView){

        //To have the highlight
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        //To set setOnNavigationItemSelectedListener
        NavBarHelper  navBarHelper = new NavBarHelper(this);
        bottomNavigationView.setOnNavigationItemSelectedListener(navBarHelper);
    }





}
