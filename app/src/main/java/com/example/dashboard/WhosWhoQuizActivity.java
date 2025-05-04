package com.example.dashboard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Activity for the Who's Who quiz game
 * Shows a photo and 4 name options for user to choose
 */
public class WhosWhoQuizActivity extends AppCompatActivity {

    private static final int NUM_QUESTIONS = 5;
    private static final int NUM_OPTIONS = 4;

    private WhosWhoDbHelper dbHelper;
    private List<Person> allPeople;
    private List<Person> quizPeople;
    private List<Integer> questionOrder;

    private ImageView photoImageView;
    private TextView questionNumberTextView;
    private RadioGroup optionsRadioGroup;
    private RadioButton[] optionRadioButtons;
    private Button nextButton;
    private Button cancelButton;

    private int currentQuestionIndex = 0;
    private int correctAnswers = 0;
    private Person currentPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whos_who_quiz);

        // Initialize database helper
        dbHelper = WhosWhoDbHelper.getInstance(this);

        // Initialize views
        photoImageView = findViewById(R.id.photoImageView);
        questionNumberTextView = findViewById(R.id.questionNumberTextView);
        optionsRadioGroup = findViewById(R.id.optionsRadioGroup);
        nextButton = findViewById(R.id.nextButton);
        cancelButton = findViewById(R.id.cancelButton);

        // Get the radio buttons
        optionRadioButtons = new RadioButton[NUM_OPTIONS];
        optionRadioButtons[0] = findViewById(R.id.option1RadioButton);
        optionRadioButtons[1] = findViewById(R.id.option2RadioButton);
        optionRadioButtons[2] = findViewById(R.id.option3RadioButton);
        optionRadioButtons[3] = findViewById(R.id.option4RadioButton);

        // Set click listeners
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Load people from database
        allPeople = dbHelper.getAllPeople();

        if (allPeople.size() < NUM_OPTIONS) {
            Toast.makeText(this, "Not enough people for a quiz. Please add more people.",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Setup quiz
        setupQuiz();
    }

    /**
     * Set up the quiz with random questions
     */
    private void setupQuiz() {
        // Shuffle people and select a subset for the quiz
        Collections.shuffle(allPeople);
        int numQuestions = Math.min(NUM_QUESTIONS, allPeople.size());
        quizPeople = allPeople.subList(0, numQuestions);

        // Create question order
        questionOrder = new ArrayList<>();
        for (int i = 0; i < numQuestions; i++) {
            questionOrder.add(i);
        }
        Collections.shuffle(questionOrder);

        // Start first question
        currentQuestionIndex = 0;
        showQuestion(currentQuestionIndex);
    }

    /**
     * Show the current question
     */
    private void showQuestion(int index) {
        if (index >= questionOrder.size()) {
            finishQuiz();
            return;
        }

        // Get the current person for this question
        int personIndex = questionOrder.get(index);
        currentPerson = quizPeople.get(personIndex);

        // Update question number display
        questionNumberTextView.setText(getString(R.string.question_number, index + 1, questionOrder.size()));

        // Set the person's photo
        photoImageView.setImageURI(Uri.parse(currentPerson.getPhotoUri()));

        // Create answer options - 1 correct and 3 wrong
        List<String> optionNames = new ArrayList<>();
        optionNames.add(currentPerson.getName());

        // Add wrong options
        List<Person> wrongOptions = new ArrayList<>(allPeople);
        wrongOptions.remove(currentPerson);
        Collections.shuffle(wrongOptions);

        for (int i = 0; i < NUM_OPTIONS - 1 && i < wrongOptions.size(); i++) {
            optionNames.add(wrongOptions.get(i).getName());
        }

        // Shuffle options so correct answer isn't always first
        Collections.shuffle(optionNames);

        // Set radio button texts
        for (int i = 0; i < NUM_OPTIONS; i++) {
            optionRadioButtons[i].setText(optionNames.get(i));
        }

        // Clear selection
        optionsRadioGroup.clearCheck();

        // Update button text
        if (index == questionOrder.size() - 1) {
            nextButton.setText(R.string.finish_quiz);
        } else {
            nextButton.setText(R.string.next_question);
        }
    }

    /**
     * Check if the selected answer is correct
     */
    private void checkAnswer() {
        int selectedId = optionsRadioGroup.getCheckedRadioButtonId();

        if (selectedId == -1) {
            Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedButton = findViewById(selectedId);
        String selectedName = selectedButton.getText().toString();

        // Check if correct
        boolean isCorrect = selectedName.equals(currentPerson.getName());

        if (isCorrect) {
            correctAnswers++;
            PointsManager.addPoints(this);
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Incorrect. The correct answer is " + currentPerson.getName(),
                    Toast.LENGTH_SHORT).show();
        }

        // Go to next question
        currentQuestionIndex++;
        showQuestion(currentQuestionIndex);
    }

    /**
     * Finish the quiz and show results
     */
    private void finishQuiz() {
        Intent intent = new Intent(this, WhosWhoResultActivity.class);
        intent.putExtra("correctAnswers", correctAnswers);
        intent.putExtra("totalQuestions", questionOrder.size());
        startActivity(intent);
        finish();
    }
}
