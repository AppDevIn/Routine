package com.mad.p03.np2020.routine.Task.Fragment;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.ViewSwitcher;


import com.google.firebase.auth.FirebaseAuth;
import com.mad.p03.np2020.routine.DAL.SectionDBHelper;
import com.mad.p03.np2020.routine.DAL.TaskDBHelper;
import com.mad.p03.np2020.routine.Fragment.HistoryFragment;
import com.mad.p03.np2020.routine.Home.adapters.MySpinnerColorAdapter;
import com.mad.p03.np2020.routine.Home.adapters.MySpinnerIconsAdapter;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.Task.ViewHolder.TeamViewHolder;
import com.mad.p03.np2020.routine.Task.adapter.TaskAdapter;
import com.mad.p03.np2020.routine.Task.adapter.TeamAdapter;
import com.mad.p03.np2020.routine.Task.model.GestureDetectorTaskSettings;
import com.mad.p03.np2020.routine.Task.model.MyTaskTouchHelper;
import com.mad.p03.np2020.routine.helpers.HomeIcon;
import com.mad.p03.np2020.routine.models.Section;
import com.mad.p03.np2020.routine.models.Task;
import com.mad.p03.np2020.routine.models.Team;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TaskSettings extends Fragment implements TextView.OnEditorActionListener, View.OnClickListener {

    final private String TAG = "TaskSettings";


    TeamAdapter mTeamAdapter;
    RecyclerView mRecyclerView;
    List<String> emailList;
    String mSectionID;
    Team team;
    Section mSection;
    EditText editText;
    Spinner mColors;
    Spinner mBackgrounds;
    List<Integer> mBackgroundsList;
    Integer[] mColorsList;
    public ViewSwitcher viewSwitcher;



    public TaskSettings(String id) {
        mSectionID = id;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {



        View view = inflater.inflate(R.layout.fragment_task_settings, container, false);

        CardView cardView = view.findViewById(R.id.mainCard);

        //Get the section object
        mSection = new SectionDBHelper(getContext()).getSection(mSectionID);

        //Set the background of the cardview
        float radius[] = {50f,50f,50f,50f,0f,0f,0f,0f};

        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setColor(getResources().getColor(R.color.white));
        shape.setCornerRadii(radius);

        cardView.setBackground(shape);


        //Set click listener for the image
        ImageView imageView = view.findViewById(R.id.downArrow);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateOut();
            }
        });

        editText = view.findViewById(R.id.addUser);
        editText.setOnEditorActionListener(this);


        if(!mSection.isAdmin())
            editText.setVisibility(View.GONE);


        view.findViewById(R.id.btnSubmit).setOnClickListener(this);

        //Move to another view
        viewSwitcher = view.findViewById(R.id.fragmentSwitcher);

        //Setup the editview

        //Get the id of the spinner
        mColors = view.findViewById(R.id.spinnerColor);
        mBackgrounds = view.findViewById(R.id.spinnerImg);

        setUpEdit(mColors, mBackgrounds, view.findViewById(R.id.txtAddList));



        //Create the detector
        GestureDetectorTaskSettings detectorTaskSettings = new GestureDetectorTaskSettings();
        detectorTaskSettings.setActivity(this);

        //Set the detector in the compat
        GestureDetectorCompat gestureDetectorCompat = new GestureDetectorCompat(getContext(), detectorTaskSettings);


        //Have a swipe action
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gestureDetectorCompat.onTouchEvent(motionEvent);
                return true;
            }
        });

        KeyboardVisibilityEvent.setEventListener(
                getActivity(),
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        // write your code
                        mTeamAdapter.notifyDataSetChanged();
                    }
                });



        return view;



    }


    @Override
    public void onStart() {
        super.onStart();

        Team team = new Team();
        team.setSectionID(mSectionID);




        initRecyclerView(getView(), team);


    }

    /**
     *
     * The action is being performed on the keyboard
     * when the the Enter key is pressed add the task into
     * the adapter and hide the keyboard
     *
     * @param textView The view that was clicked.
     * @param actionId  Identifier of the action. This will be either the identifier you supplied, or
     *                  EditorInfo#IME_NULL if being called due to the enter key being pressed.
     * @param event  If triggered by an enter key, this is the event; otherwise, this is null.
     * @return Return true if you have consumed the action, else false.
     */
    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {


        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE ||
                event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {




            //Add this object to the list
            mTeamAdapter.addEmail(textView.getText().toString());

            editText.setText("");


            //Hide and scroll the last task
            showNewEntry();


            return true;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btnSubmit:addSection(getView().findViewById(R.id.txtAddList));break;

        }
    }

    private void initRecyclerView(View view, Team team) {
        mRecyclerView = view.findViewById(R.id.recyclerViewEmail);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        mTeamAdapter = new TeamAdapter(team,getContext(), mSection);
        mRecyclerView.setAdapter(mTeamAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

    }


    /**
     * Upon calling this method, the keyboard will retract
     * and the recyclerview will scroll to the last item
     */
    private void showNewEntry(){

        //auto hide keyboard after entry
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mRecyclerView.getWindowToken(), 0);


    }

    private void setUpEdit(Spinner color, Spinner backgrounds, EditText editText){
        //A list of possible colors to be user
         mColorsList = new Integer[]{getResources().getColor(R.color.superiorityBlue), getResources().getColor(R.color.rosyBrown), getResources().getColor(R.color.mandarin), getResources().getColor(R.color.green_yellow), getResources().getColor(R.color.turquoise)};

        //A list of possible background to be user
        mBackgroundsList = HomeIcon.getAllBackgrounds();

        //Declaring a custom adapter
        color.setAdapter(new MySpinnerColorAdapter( mColorsList)); // For the color
        backgrounds.setAdapter(new MySpinnerIconsAdapter(mBackgroundsList)); //For the background

        //Setting the value of the section view into the edittext
        editText.setText(mSection.getName());


        backgrounds.setSelection(mSection.getIconValue());


        for (int c = 0; c < mColorsList.length; c++) {

            if(mColorsList[c] == mSection.getBackgroundColor()){
                color.setSelection(c);
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
        mSection.setName(textView.getText().toString().trim());
        mSection.setBackgroundColor(mColorsList[mColors.getSelectedItemPosition()]);
        mSection.setBmiIcon(HomeIcon.getValue(mBackgroundsList.get(mBackgrounds.getSelectedItemPosition())));

        //Save to SQL
        mSection.editSection(getContext());
        Log.d(TAG, "updateCardUI(): Added to SQL");

        //Save to firebase
        mSection.executeFirebaseSectionUpload(FirebaseAuth.getInstance().getUid(), mSection.getID(), this,true);

    }


    public void switchView(){

        viewSwitcher.showNext();
    }



    public void animateOut()
    {
        TranslateAnimation trans=new TranslateAnimation(0,0, 0,300*getContext().getResources().getDisplayMetrics().density);
        trans.setDuration(150);
        trans.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {


            }

            @Override
            public void onAnimationRepeat(Animation animation) {


            }

            @Override
            public void onAnimationEnd(Animation animation) {

                Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag("Settings");


                if(fragment != null)
                    getActivity().getSupportFragmentManager().
                            beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_in_bottom, R.anim.slide_in_bottom, R.anim.slide_in_bottom)
                            .replace(R.id.fragmentContainer, new Fragment())
                            .commit();
            }
        });
        getView().startAnimation(trans);
    }

    /**
     *
     * Show the keyboard the the focused view
     *
     * @param view The view that wants to receive the soft keyboard input
     */
    private void showKeyboard(View view) {
        Log.i(TAG, "Show soft keyboard");

        view.requestFocus();

        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        assert mgr != null;
        mgr.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);

    }


}
