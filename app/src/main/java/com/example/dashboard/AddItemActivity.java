package com.example.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class AddItemActivity extends AppCompatActivity {

    private EditText editTextItem;
    private ImageView backArrow;
    private ListView itemListView;
    private ArrayList<String> itemList;
    private CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        // UI references
        editTextItem = findViewById(R.id.editTextItem);
        backArrow = findViewById(R.id.backArrow);
        itemListView = findViewById(R.id.itemListView);

        findViewById(R.id.btnSave).setOnClickListener(v -> {
            String newItem = editTextItem.getText().toString().trim();
            if (!newItem.isEmpty()) {
                itemList.add(newItem);
                adapter.notifyDataSetChanged();
                editTextItem.setText("");
            }
        });

        // Load saved items
        itemList = ItemStorage.getItemList(this);
        if (itemList == null) {
            itemList = new ArrayList<>();
        }

        adapter = new CustomAdapter(this, itemList);
        itemListView.setAdapter(adapter);

        // Handle back arrow
        backArrow.setOnClickListener(v -> saveAndReturn());

        // Quick Add setup for ImageButtons
        setupQuickAdd(R.id.btnMedicines, "Take Medicines");
        setupQuickAdd(R.id.btnFood, "Have Food");
        setupQuickAdd(R.id.btnConsultation, "Doctor Consultation");
        setupQuickAdd(R.id.btnExercise, "Exercise Routine");
        setupQuickAdd(R.id.btnCallFamily, "Call Family");
        setupQuickAdd(R.id.btnDrinkWater, "Drink Water");
        setupQuickAdd(R.id.btnNapTime, "Nap Time");
    }

    private void setupQuickAdd(int buttonId, String taskName) {
        ImageButton button = findViewById(buttonId);
        button.setOnClickListener(v -> {
            itemList.add(taskName);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onBackPressed() {
        saveAndReturn();
        super.onBackPressed();
    }

    private void saveAndReturn() {
        ItemStorage.saveItemList(this, itemList);
        Intent resultIntent = new Intent();
        resultIntent.putStringArrayListExtra("itemList", itemList);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
