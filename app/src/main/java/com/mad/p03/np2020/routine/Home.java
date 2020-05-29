package com.mad.p03.np2020.routine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mad.p03.np2020.routine.Adapter.HomePageAdapter;
import com.mad.p03.np2020.routine.Adapter.MySpinnerApater;
import com.mad.p03.np2020.routine.Adapter.MySpinnerBackgroundAdapter;
import com.mad.p03.np2020.routine.Adapter.OnSectionListener;
import com.mad.p03.np2020.routine.Class.Section;
import com.mad.p03.np2020.routine.Class.User;
import com.mad.p03.np2020.routine.background.UploadDataWorker;
import com.mad.p03.np2020.routine.background.UploadSectionWorker;
import com.mad.p03.np2020.routine.database.MyDatabaseListener;
import com.mad.p03.np2020.routine.database.SectionDBHelper;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity implements OnSectionListener, MyDatabaseListener {

    //Declare Constants
    final String TAG = "Home Activity";


    //Declare member variables
    RecyclerView mGridView;
    HomePageAdapter mHomePageAdapter;
    ImageButton mImgAdd;
    EditText mEditAddList;
    Spinner mSpinnerColor, mSpinnerBackground;
    CardView mCardViewPopUp;
    Button mBtnAdd;
    List<Section> mSectionList;
    SectionDBHelper mSectionDBHelper;
    Integer[] mColors, mBackgrounds;
    String mUID;
    User mUser;


    void Home(User user){
        mUser = user;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "UI is being created");

        //TODO: Get from the intent
        mUID = "aRbjnh0WpNe8gGga1PkDfOiJLH03";
//        mUID = mUser.getUID();


        //Find view by ID
        mGridView = findViewById(R.id.section_grid_view);
        mImgAdd = findViewById(R.id.imgBtnTodo);
        mEditAddList = findViewById(R.id.txtAddList);
        mSpinnerColor = findViewById(R.id.spinnerColor);
        mSpinnerBackground = findViewById(R.id.spinnerImg);
        mCardViewPopUp = findViewById(R.id.cardViewPopUp);
        mBtnAdd = findViewById(R.id.btnAdd);

        //Get all the section data from firebase
        mSectionDBHelper = new SectionDBHelper(this);
        mSectionList = mSectionDBHelper.getAllSections(mUID);
        Log.d(TAG, "onCreate(): Data received from SQL");

        //A list of possible colors to be user
        mColors = new Integer[]{getResources().getColor(R.color.superiorityBlue), getResources().getColor(R.color.rosyBrown), getResources().getColor(R.color.mandarin), getResources().getColor(R.color.green_yellow), getResources().getColor(R.color.turquoise)};

        //A list of possible background to be user
        mBackgrounds = new Integer[] {R.drawable.amazon, R.drawable.android, R.drawable.laptop, R.drawable.code, R.drawable.bookmark};


        //TODO: Remove this
        tempLogin();
        getMessagingToken();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: GUI is ready");


        //Recycler view setup
        mGridView.setLayoutManager(new GridLayoutManager(Home.this,2)); //Setting the layout manager with the column of 2
        mGridView.addItemDecoration(new DividerItemDecoration(10));


        // Initialize any value
        mHomePageAdapter = new HomePageAdapter(this,mSectionList, this);
        mGridView.setAdapter(mHomePageAdapter);

        //Declaring a custom adapter
        mSpinnerColor.setAdapter(new MySpinnerApater( mColors)); // For the color
        mSpinnerBackground.setAdapter(new MySpinnerBackgroundAdapter(mBackgrounds)); //For the background

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

        SectionDBHelper.setMyDatabaseListener(this);

    }



    /**
     *
     * Triggered to add to the current adapter list
     * when it is added to the sql
     *
     * @param section given from the SQL when triggered
     */
    @Override
    public void onSectionAdd(final Section section) {
        Log.d(TAG, "A new section has been added: " + section.toString());

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Add to List<Section>
                mHomePageAdapter.addItem(section);
            }
        });
    }

    @Override
    public void onSectionDelete(String ID) {
        //Remove item from the data in adapter/local list


        for (int position = 0; position < mSectionList.size(); position++) {

            if(mSectionList.get(position).getID().equals(ID)){
                mHomePageAdapter.removeItem(position);
                break;
            }
        }

    }

    /********************** Section Helper **********************/
    @Override
    public void onSectionClick(int position) {
        Log.d(TAG, "onClick(): You have clicked on " + mSectionList.get(position).getName() + " list");
    }

    @Override
    public void onSectionLongClick(int position) {
        Log.d(TAG, "onLongClick(): " + position + " has been longed");
        Log.d(TAG, "onLongClick(): Alert dialog triggered");

        //Show a dialog to confirm the delete
        confirmDelete(position);

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
        Section section = new Section(textView.getText().toString().trim(), mColors[mSpinnerColor.getSelectedItemPosition()], mBackgrounds[mSpinnerBackground.getSelectedItemPosition()]);

        //Save to SQL
        String id  = section.addSection(this, mUID);
        Log.d(TAG, "updateCardUI(): Added to SQL, this ID is " + id);

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



    private void confirmDelete(final int position){
        Log.d(TAG, "Deletion prompt is building");
        
        final Section section = mHomePageAdapter.getSection(position);

        //Create a alert builder
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //Inflate the custom layout
        View dialogLayout = LayoutInflater.from(this).inflate(R.layout.alert_dialog_cfm_delete, null);

        //Set the message in the view
        TextView txtMessage = dialogLayout.findViewById(R.id.txtMessage);// Find id in the custom dialog
        //Setting the message using HTML format so I can have a bold and normal text
        txtMessage.setText(Html.fromHtml( "<div>Are you sure you want to delete<br/>"+ "<b>" + section.getName() + "?</b></div>"));

        //Set trash in image view
        ImageView imgTrash = dialogLayout.findViewById(R.id.imgTrash); //Find the image view in the custom dialog
        imgTrash.setImageResource(android.R.drawable.ic_menu_delete); //Set the image from the android library delete


        builder.setTitle(R.string.delete); //Set the title of the dialog

        //Set a positive button: Yes
        //Method should remove the  item
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG, section.getName() + " task is going to be deleted");

                //Remove from firebase
                mSectionList.get(position).executeFirebaseSectionDelete(Home.this);

                //Remove from SQL
                section.deleteSection(Home.this);


            }
        });

        //Set negative button: No
        //Method should close the dialog
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG, section.getName() + " task not getting deleted");
            }
        });

        builder.setCancelable(false); //To prevent user from existing when clicking outside of the dialog
        builder.setView(dialogLayout);//Set the custom view
        builder.show();//Show the view to the user
        Log.d(TAG, "Deletion prompt is shown");
    }




    private void getMessagingToken(){

        FirebaseDatabase.getInstance().getReference().child("users").child(mUID).child("messagingToken").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String key = (String) dataSnapshot.getValue();
                Log.d(TAG, "getMessagingToken: " + key);

                FirebaseMessaging.getInstance().subscribeToTopic(mUID)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                
                                if (!task.isSuccessful()) {
                                    Log.d(TAG, "onComplete: Done Running");
                                }

                                Toast.makeText(Home.this, key, Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void tempLogin(){
       final FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.signInWithEmailAndPassword("routine@gmail.com", "JqHp@2020")
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = auth.getCurrentUser();
                        Log.i(TAG, "onComplete: "+user.getUid());
//                        retrieveFCMToken();
                    }
                });
    }


}

