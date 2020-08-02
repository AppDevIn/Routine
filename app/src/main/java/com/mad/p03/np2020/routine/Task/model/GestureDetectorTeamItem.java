package com.mad.p03.np2020.routine.Task.model;

import android.animation.Animator;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.Task.Fragment.TaskSettings;
import com.mad.p03.np2020.routine.Task.ViewHolder.TeamViewHolder;
import com.mad.p03.np2020.routine.Task.adapter.TeamAdapter;

/**
 *
 * This class is used to capture the gesture of the swipe
 * action on the team members profiled
 *
 * @author Jeyavishnu
 * @since 02-08-2020
 *
 */
public class GestureDetectorTeamItem extends GestureDetector.SimpleOnGestureListener {
    // Minimal x and y axis swipe distance.
    private static int MIN_SWIPE_DISTANCE_X = 100;
    private static int MIN_SWIPE_DISTANCE_Y = 100;

    // Maximal x and y axis swipe distance.
    private static int MAX_SWIPE_DISTANCE_X = 1000;
    private static int MAX_SWIPE_DISTANCE_Y = 1000;

    // Source activity that display message in text view.

    private TeamAdapter mTeamAdapter;
    private int position;
    TeamViewHolder mTeamViewHolder;

    public GestureDetectorTeamItem(TeamAdapter teamAdapter, int position, TeamViewHolder teamViewHolder) {
        mTeamAdapter = teamAdapter;
        this.position = position;
        mTeamViewHolder = teamViewHolder;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        // Get swipe delta value in x axis.
        float deltaX = e1.getX() - e2.getX();

        // Get swipe delta value in y axis.
        float deltaY = e1.getY() - e2.getY();

        // Get absolute value.
        float deltaXAbs = Math.abs(deltaX);
        float deltaYAbs = Math.abs(deltaY);



        // Only when swipe distance between minimal and maximal distance value then we treat it as effective swipe
        if((deltaXAbs >= MIN_SWIPE_DISTANCE_X) && (deltaXAbs <= MAX_SWIPE_DISTANCE_X))
        {

            if(deltaX > 0)
            {
                Log.d("GestureTeamItem", "onFling: Swipe to the left");
            }
            else
            {
                Log.d("GestureTeamItem", "onFling: Swipe to the right");
                mTeamViewHolder.mView.
                        animate()
                        .translationX(mTeamViewHolder.mView.getWidth())
                        .alpha(0.0f)
                        .setDuration(300)
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                mTeamAdapter.deleteEmail(position);

                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        });

            }
        }

        return true;
    }
}
