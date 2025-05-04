package com.example.dashboard;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity to display quiz results
 */
public class WhosWhoResultActivity extends AppCompatActivity {

    private TextView scoreTextView;
    private TextView feedbackTextView;
    private TextView pointsEarnedTextView;
    private Button playAgainButton;
    private Button returnHomeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whos_who_result);

        // Initialize views
        scoreTextView = findViewById(R.id.scoreTextView);
        feedbackTextView = findViewById(R.id.feedbackTextView);
        pointsEarnedTextView = findViewById(R.id.pointsEarnedTextView);
        playAgainButton = findViewById(R.id.playAgainButton);
        returnHomeButton = findViewById(R.id.returnHomeButton);

        // Get scores from intent
        int correctAnswers = getIntent().getIntExtra("correctAnswers", 0);
        int totalQuestions = getIntent().getIntExtra("totalQuestions", 0);

        // Calculate percentage
        int percentage = 0;
        if (totalQuestions > 0) {
            percentage = (correctAnswers * 100) / totalQuestions;
        }

        // Display score
        String scoreText = getString(R.string.score_display, correctAnswers, totalQuestions, percentage);
        scoreTextView.setText(scoreText);
        // Display feedback based on score
        String feedback;
        if (percentage >= 90) {
            feedback = "Excellent! Great job remembering everyone!";
        } else if (percentage >= 70) {
            feedback = "Good job! You remembered most people!";
        } else if (percentage >= 50) {
            feedback = "Nice effort! Keep practicing to improve!";
        } else {
            feedback = "Keep practicing! You'll get better with time.";
        }
        feedbackTextView.setText(feedback);

        // Display points earned
        int pointsEarned = correctAnswers * 10; // 10 points per correct answer
        pointsEarnedTextView.setText(getString(R.string.points_earned, pointsEarned));

        // Set button click listeners
        playAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Return to quiz activity
                finish();
            }
        });

        returnHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Return to welcome screen
                finish();
            }
        });
    }
}

