package com.example.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Main entry point for the Who's Who game
 * This activity redirects to the welcome screen
 */
public class WhosWhoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Directly launch the welcome activity
        Intent intent = new Intent(this, WhosWhoWelcomeActivity.class);
        startActivity(intent);
        finish();
    }
}
