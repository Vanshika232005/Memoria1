package com.example.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int TASK_REQUEST_CODE = 100;
    private ImageButton viewAllButton;
    private ArrayList<String> allTasks;
    private ListView taskPreviewList;
    private CustomAdapter adapter;
    private ImageView bellIcon;
    private TextView notificationBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        taskPreviewList = findViewById(R.id.taskPreviewList);
        viewAllButton = findViewById(R.id.btnViewAllTasks);
        bellIcon = findViewById(R.id.bellIcon);

        // Retrieve saved tasks from ItemStorage
        allTasks = ItemStorage.getItemList(this);
        if (allTasks == null) {
            allTasks = new ArrayList<>();
        }

        // Use CustomAdapter instead of ArrayAdapter
        adapter = new CustomAdapter(this, allTasks);
        taskPreviewList.setAdapter(adapter);

        // View all tasks button opens AddItemActivity
        viewAllButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddItemActivity.class);
            intent.putStringArrayListExtra("itemList", allTasks);
            startActivityForResult(intent, TASK_REQUEST_CODE);
        });

        // Update notification count for Who's Who game points
        updateNotificationCount();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh notification count when returning to main activity
        updateNotificationCount();
    }

    private void updateNotificationCount() {
        // Get daily points from PointsManager
        int dailyPoints = PointsManager.getDailyPoints(this);

        // Only show badge if there are points
        if (dailyPoints > 0) {
            // If we have a badge view, update it
            if (bellIcon.getTag() == null) {
                // Create a badge dynamically if it doesn't exist
                TextView badge = new TextView(this);
                badge.setText(String.valueOf(dailyPoints));
                badge.setBackgroundResource(android.R.drawable.ic_notification_overlay);
                badge.setTextColor(getResources().getColor(android.R.color.white));
                badge.setPadding(5, 5, 5, 5);

                // Add badge to layout
                // Note: This is a simplified representation. In a real app, we would add
                // the badge to the proper parent layout with proper positioning
                bellIcon.setTag(badge);
            } else {
                // Update existing badge
                TextView badge = (TextView) bellIcon.getTag();
                badge.setText(String.valueOf(dailyPoints));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TASK_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> updated = data.getStringArrayListExtra("itemList");
            if (updated != null) {
                allTasks.clear();
                allTasks.addAll(updated);
                ItemStorage.saveItemList(this, allTasks);
                adapter.notifyDataSetChanged();
            }
        }
    }

    // Navigation methods for footer buttons
    public void openHome(View view) {
        // Already on home screen
    }

    public void openCalendar(View view) {
        // Calendar implementation would go here
    }

    public void openGames(View view) {
        // Games section implementation would go here
    }

    public void triggerSOS(View view) {
        // SOS functionality would go here
    }

    // Methods for explore section buttons
    public void openYogaClass(View view) {
        // Yoga class implementation would go here
    }

    public void openWhosWho(View view) {
        // Launch Who's Who welcome activity
        Intent intent = new Intent(MainActivity.this, WhosWhoWelcomeActivity.class);
        startActivity(intent);
    }

    public void openGuessVoice(View view) {
        // Guess the voice implementation would go here
    }

    public void openTodoList(View view) {
        // Open the to-do list
        Intent intent = new Intent(MainActivity.this, AddItemActivity.class);
        intent.putStringArrayListExtra("itemList", allTasks);
        startActivityForResult(intent, TASK_REQUEST_CODE);
    }
}
