package com.mad.p03.np2020.routine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.mad.p03.np2020.routine.Adapter.HomePageAdapter;
import com.mad.p03.np2020.routine.Adapter.MySpinnerApater;
import com.mad.p03.np2020.routine.Adapter.MySpinnerBackgroundAdapter;
import com.mad.p03.np2020.routine.Class.Section;
import com.mad.p03.np2020.routine.Class.User;
import com.mad.p03.np2020.routine.background.UploadDataWorker;
import com.mad.p03.np2020.routine.background.UploadSectionWorker;
import com.mad.p03.np2020.routine.database.MyDatabaseListener;
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
        mUID = "pXIeuenKaGWjEU5ruEQ6ahiS8FK2";
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

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: GUI is ready");

        // Initialize any value
        mHomePageAdapter = new HomePageAdapter(this,R.layout.home_grid_view_items, mSectionList);
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

        //Setting each item in the listener clickable
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Logging the name of the list clicked
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


        //Listen to the database update
        //When new section add to database it will be
        //Add to the list
        SectionDBHelper.setMyDatabaseListener(new MyDatabaseListener() {
            @Override
            public void onSectionAdd(Section section) {
                Log.d(TAG, "A new section has been added: " + section.toString());

                //Add to List<Section>
                mSectionList.add(section);
                mHomePageAdapter.notifyDataSetChanged();
                Log.d(TAG, "updateCardUI(): Adding to the local list");
            }
        });



    }

    /**
     *
     * To add the data into firebase and SQL
     * Create a section object.
     *
     * @param textView the is the edittext
     */
    private void updateCardUI(TextView textView){
        String listName = textView.getText().toString();
        Log.i(TAG, "onEditorAction: " + listName);

        //Create a Section Object for the user input
        Section section = new Section(textView.getText().toString().trim(), mColors[mSpinnerColor.getSelectedItemPosition()], mBackgrounds[mSpinnerBackground.getSelectedItemPosition()]);

        //Save to SQL
        long id  = mSectionDBHelper.insertSection(section, mUID);
        Log.d(TAG, "updateCardUI(): Added to SQL ");

        //Save to firebase
        executeFirebaseSectionUpload(section, id);

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
     * Upload the section info to firebase
     * when internet connectivity is present
     *
     * It will be done in the background
     *
     * @param section is object that will be uploaded to firebase
     * @param ID is a the id of row in the SQL database
     */
    private void executeFirebaseSectionUpload(Section section, long ID){

        Log.d(TAG, "executeFirebaseSectionUpload(): Preparing the upload");

        //Setting condition
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        //Adding data which will be received from the worker
        @SuppressLint("RestrictedApi") Data firebaseSectionData = new Data.Builder()
                .putLong("ID", ID)
                .putString("UID", mUID)
                .putString("Name", section.getName())
                .putInt("Color", section.getBackgroundColor())
                .putInt("Image", section.getBmiIcon()) //TODO: Change after to image
                .build();

        //Create the request
        OneTimeWorkRequest uploadTask = new OneTimeWorkRequest.
                Builder(UploadSectionWorker.class)
                .setConstraints(constraints)
                .setInputData(firebaseSectionData)
                .build();

        //Enqueue the request
        WorkManager.getInstance(this).enqueue(uploadTask);


        Log.d(TAG, "executeFirebaseSectionUpload(): Put in queue");

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(uploadTask.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        Log.d(TAG, "Section upload state: " + workInfo.getState());
                    }
                });


    }


    //TODO: Recorrect this portion
    private void retrieveFCMToken(){
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "getInstanceId failed", task.getException());
                    return;
                }

                // Get new Instance ID token
                String token = task.getResult().getToken();

                // Log and toast
//                String msg = getString(R.string.msg_token_fmt, token);
//                Log.d(TAG, msg);
                sendRegistrationToServer(token);
                Toast.makeText(Home.this, token, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.

        Log.d(TAG, "sendRegistrationToServer: sending token to server: " + token);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.child("messagingToken").setValue(token);
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
                        retrieveFCMToken();
                    }
                });
    }

}

