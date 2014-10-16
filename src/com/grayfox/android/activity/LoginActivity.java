package com.grayfox.android.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.grayfox.android.R;

/**
 * 
 * @author Daniel Pedraza-Arcega
 */
public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button connectToFoursquareButton = (Button) findViewById(R.id.connect_to_foursquare_button);
        connectToFoursquareButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Login
            }
        });
    }
}