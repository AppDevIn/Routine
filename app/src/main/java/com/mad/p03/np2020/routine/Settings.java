package com.mad.p03.np2020.routine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

/**
 *
 * Settings Class for managing setting
 *
 * @author Pritheev
 * @since 02-06-2020
 *
 */
public class Settings extends AppCompatActivity {

    /*
    Button systemButton;

     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        systemButton = findViewById(R.id.systemButton);

        systemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
            }
        });

         */
    }
}
