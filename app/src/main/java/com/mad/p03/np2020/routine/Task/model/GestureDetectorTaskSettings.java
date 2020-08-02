package com.mad.p03.np2020.routine.Task.model;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.Task.Fragment.TaskSettings;

/**
 *
 * This is used to capture the swipe action on the task settings page
 *
 * @author Jeyavishnu
 * @since 02-08-2020
 *
 */
public class GestureDetectorTaskSettings extends GestureDetector.SimpleOnGestureListener {
    // Minimal x and y axis swipe distance.
    private static int MIN_SWIPE_DISTANCE_X = 100;
    private static int MIN_SWIPE_DISTANCE_Y = 100;

    // Maximal x and y axis swipe distance.
    private static int MAX_SWIPE_DISTANCE_X = 1000;
    private static int MAX_SWIPE_DISTANCE_Y = 1000;

    // Source activity that display message in text view.
    private TaskSettings activity = null;

    public TaskSettings getActivity() {
        return activity;
    }

    public void setActivity(TaskSettings activity) {
        this.activity = activity;
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

        //Views
        View view1 = activity.getView().findViewById(R.id.view1);
        View view2 = activity.getView().findViewById(R.id.view2);

        // Only when swipe distance between minimal and maximal distance value then we treat it as effective swipe
        if((deltaXAbs >= MIN_SWIPE_DISTANCE_X) && (deltaXAbs <= MAX_SWIPE_DISTANCE_X))
        {

            if(deltaX > 0)
            {
                Log.d("GestureTaskSettings", "onFling: Swipe to the left");

                Animation in = AnimationUtils.loadAnimation(activity.getContext(), R.anim.slide_in_right);
                activity.viewSwitcher.setInAnimation(in);


                if(activity.viewSwitcher.getNextView() == view1){
                    activity.switchView();
                }

            }
            else
            {
                Log.d("GestureTaskSettings", "onFling: Swipe to the right");

                Animation in = AnimationUtils.loadAnimation(activity.getContext(), android.R.anim.slide_in_left);
                activity.viewSwitcher.setInAnimation(in);

                if(activity.viewSwitcher.getNextView() == view2){
                    activity.switchView();
                }
            }
        }

        return true;
    }
}
