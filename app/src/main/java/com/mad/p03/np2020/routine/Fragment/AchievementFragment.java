package com.mad.p03.np2020.routine.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.p03.np2020.routine.Adapter.AchievementAdapter;
import com.mad.p03.np2020.routine.Adapter.FocusAdapter;
import com.mad.p03.np2020.routine.DAL.FocusDBHelper;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.helpers.DividerItemDecoration;
import com.mad.p03.np2020.routine.models.User;

public class AchievementFragment extends Fragment implements View.OnClickListener {

    private static final String USER_GET = "User Get";
    private static final String FOCUS_DATABASE = "FocusDatabase";

    private FocusDBHelper focusDBHelper;
    private HistoryFragment.OnFragmentInteractionListener mListener;
    private final String TAG = "Achievement Page";
    private User user;

    private AchievementAdapter achievementAdapter;

    private TextView badgeIndicator;

    public AchievementFragment() {
        // Required empty public constructor
    }

    public static AchievementFragment newInstance(User user, FocusDBHelper focusDBHelper) {
        AchievementFragment fragment = new AchievementFragment();
        Bundle args = new Bundle();
        args.putParcelable(USER_GET, user);
        args.putParcelable(FOCUS_DATABASE, focusDBHelper);
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
            Log.v(TAG, "Created fragment");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_achievementfocus, container, false);
        badgeIndicator = view.findViewById(R.id.badgesAchieve);

        //RecyclerView for display history
        //Recycler View
        RecyclerView recyclerView = view.findViewById(R.id.recyclerHistory);

        //recycler adapter
        achievementAdapter = new AchievementAdapter(user, getActivity(), focusDBHelper, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this.getActivity(), 5); //Declare layoutManager

        recyclerView.addItemDecoration(new DividerItemDecoration(15)); //Add Custom Spacing
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(achievementAdapter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onClick(View view) {

    }



    private void sendBack() {
        if (mListener != null) {
            mListener.onFragmentInteraction();
            Log.v(TAG, "Sending back");
        }
    }
}
