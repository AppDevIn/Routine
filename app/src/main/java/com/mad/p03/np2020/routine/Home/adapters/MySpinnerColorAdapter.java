package com.mad.p03.np2020.routine.Home.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.mad.p03.np2020.routine.R;
import androidx.cardview.widget.CardView;



/**
 *
 *
 * This is a program that glue between the
 * list (data) and the view to populate the spinner
 * (dropdown) with cardview which represents the color
 *
 * @author Jeyavishnu
 * @since 02-06-2020
 *
 */
public class MySpinnerColorAdapter extends BaseAdapter {

    private Integer[] mColors;

    /**
     *
     * This to give access to the methods and set the color
     * array of colors to the local one
     *
     * @param colors This is the list of the color you want to
     *               show the user
     */
    public MySpinnerColorAdapter(Integer[] colors) {
        mColors = colors;
    }


    /**
     * This is to give back the size
     * of the list passed from the user
     * @return This return size of the adapter
     */
    @Override
    public int getCount() {
        return mColors.length;
    }

    /**
     *
     * This method is used get the color associated with
     * the position in the data set
     *
     * @param i The position of the list you want get
     * @return The color at the specific position
     */
    @Override
    public Object getItem(int i) {
        return mColors[i];
    }

    /**
     * This not implemented
     * @param i
     * @return it will return 0
     */
    @Override
    public long getItemId(int i) {
        return 0;
    }


    /**
     *
     * Get the view that displays different color based of the data
     * at different positions by inflating a view
     *
     * @param position The position of the item within the adapter's
     *                 data set of the item whose view we want.
     * @param view This is the old view
     * @param parent The parent that this view will eventually be attached to
     * @return A view that will be based of the data set
     */
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        // Inflating the layout for the custom Spinner
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View layout = inflater.inflate(R.layout.custom_spinner_color_item, parent, false);


        // Declaring button in the inflated layout
        CardView btnColor =  layout.findViewById(R.id.cardView);
        btnColor.setBackgroundColor(mColors[position]);


        return layout;
    }
}
