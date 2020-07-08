package com.mad.p03.np2020.routine.Card;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import com.mad.p03.np2020.routine.NotesFragment;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.StepsFragment;
import com.mad.p03.np2020.routine.models.PopUp;

/**
*
* CardActivity class used to manage card activities
*
* @author Pritheev
* @since 02-06-2020
*
 */

public class CardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_layout);



    }

}
