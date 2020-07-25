package com.mad.p03.np2020.routine.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.p03.np2020.routine.Adapter.AchievementAdapter;
import com.mad.p03.np2020.routine.DAL.AchievementDBHelper;
import com.mad.p03.np2020.routine.DAL.FocusDBHelper;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.helpers.DividerItemDecoration;
import com.mad.p03.np2020.routine.models.Achievement;
import com.mad.p03.np2020.routine.models.User;

public class AchievementFragment extends Fragment implements View.OnClickListener {

    private static final String USER_GET = "User Get";
    private static final String FOCUS_DATABASE = "FocusDatabase";

    private FocusDBHelper focusDBHelper;
    private AchievementDBHelper achievementDBHelper;

    private OnFragmentInteractionListener mListener;
    private final String TAG = "Achievement Page";
    private User user;
    public TextView badgeIndicator;

    public AchievementFragment() {
        // Required empty public constructor
    }

    public static AchievementFragment newInstance(User user, FocusDBHelper focusDBHelper, AchievementDBHelper achievementDBHelper) {
        AchievementFragment fragment = new AchievementFragment();
        Log.v("AchievementFragment", "Fragment Called");

        Bundle args = new Bundle();
        args.putParcelable(USER_GET, user);
        args.putParcelable(FOCUS_DATABASE, focusDBHelper);
        args.putParcelable(Achievement.TABLE_NAME, achievementDBHelper);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * OnCreate the achievement fragment
     * Retrieve USER_GET data passed in to parcelable format
     *
     * @param savedInstanceState Set the bundle data to this content
     **/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable(USER_GET);
            focusDBHelper = getArguments().getParcelable("FocusDatabase");
            achievementDBHelper = getArguments().getParcelable(Achievement.TABLE_NAME);
            Log.v(TAG, "Created fragment");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_achievementfocus, container, false);
        badgeIndicator = view.findViewById(R.id.badgesAchieve);
        ImageButton buttonFragment = view.findViewById(R.id.closeAchievement);
        buttonFragment.setOnClickListener(this);

        //RecyclerView for display history
        //Recycler View
        RecyclerView recyclerView = view.findViewById(R.id.recyclerAch);

        //recycler adapter
        AchievementAdapter achievementAdapter = new AchievementAdapter(user, getActivity(), focusDBHelper, this, achievementDBHelper);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this.getActivity(), 1); //Declare layoutManager
        recyclerView.addItemDecoration(new DividerItemDecoration(15)); //Add Custom Spacing
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        achievementAdapter.setHasStableIds(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(achievementAdapter);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.closeAchievement:
                sendBack();
                break;
        }
    }


    private void sendBack() {
        if (mListener != null) {
            mListener.onFragmentInteraction();
            Log.v(TAG, "Sending back");
        }
    }

    /**
     * Method to be called once the fragment is associated with its activity.
     *
     * @param context set the context to this content
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HistoryFragment.OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    /**
     * Callback interface
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }
}
