package com.mad.p03.np2020.routine.Home.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.mad.p03.np2020.routine.R;


/**
 *
 *
 * This is a program that glue between the
 * list (data) and the view to populate the spinner
 * (dropdown) with imageview which represents different
 * images
 *
 * @author Jeyavishnu
 * @since 02-06-2020
 *
 */
public class MySpinnerIconsAdapter extends BaseAdapter {

    private Integer[] mIcons;

    /**
     *
     * This is a program that glue between the
     * list (data) and the view to populate the spinner
     * (dropdown) with image which represents the color
     *
     * @param icons This is a list of images that
     *              the user will see
     */
    public MySpinnerIconsAdapter(Integer[] icons) {
        mIcons = icons;
    }

    /**
     * This is to give back the size
     * of the list passed from the user
     * @return This return size of the adapter
     */
    @Override
    public int getCount() {
        return mIcons.length;
    }

    /**
     *
     * This method is used get the image associated with
     * the position in the data set
     *
     * @param i The position of the list you want get
     * @return The image at the specific position
     */
    @Override
    public Object getItem(int i) {
        return mIcons[i];
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
     * Get the view that displays different images based of the data
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
        View layout = inflater.inflate(R.layout.spinner_background_items, parent, false);

        ImageView imageView = layout.findViewById(R.id.imgIcon);
        imageView.setImageResource(mIcons[position]);


        return layout;
    }
}
