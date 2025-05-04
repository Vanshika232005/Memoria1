package com.example.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Welcome screen for the Who's Who game
 * Displays game intro and current points
 */
public class WhosWhoWelcomeActivity extends AppCompatActivity {

    private TextView pointsTextView;
    private TextView streakTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whos_who_welcome);

        // Initialize views
        pointsTextView = findViewById(R.id.pointsTextView);
        streakTextView = findViewById(R.id.streakTextView);
        Button startButton = findViewById(R.id.startButton);
        Button managePhotosButton = findViewById(R.id.managePhotosButton);

        // Set start button click listener
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if we have enough people in the database
                WhosWhoDbHelper dbHelper = WhosWhoDbHelper.getInstance(WhosWhoWelcomeActivity.this);
                int peopleCount = dbHelper.getPeopleCount();

                if (peopleCount >= 4) {
                    // We have enough people to start a quiz
                    startQuiz();
                } else {
                    // Not enough people, go to photo selection
                    Intent intent = new Intent(WhosWhoWelcomeActivity.this, WhosWhoPhotoSelectActivity.class);
                    startActivity(intent);
                }
            }
        });

        // Set manage photos button click listener
        managePhotosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WhosWhoWelcomeActivity.this, WhosWhoPhotoSelectActivity.class);
                startActivity(intent);
            }
        });

        // Update points and streak display
        updatePointsDisplay();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePointsDisplay();
    }

    /**
     * Updates the points and streak text views
     */
    private void updatePointsDisplay() {
        int totalPoints = PointsManager.getTotalPoints(this);
        int dailyPoints = PointsManager.getDailyPoints(this);
        int streak = PointsManager.getCurrentStreak(this);

        pointsTextView.setText(getString(R.string.points_display, totalPoints, dailyPoints));

        if (streak > 1) {
            streakTextView.setVisibility(View.VISIBLE);
            streakTextView.setText(getString(R.string.streak_display, streak));
        } else {
            streakTextView.setVisibility(View.GONE);
        }
    }

    /**
     * Start the quiz activity
     */
    private void startQuiz() {
        Intent intent = new Intent(WhosWhoWelcomeActivity.this, WhosWhoQuizActivity.class);
        startActivity(intent);
    }
}
