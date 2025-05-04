package com.example.dashboard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity for adding or editing a person
 */
public class AddItemActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private WhosWhoDbHelper dbHelper;
    private Person existingPerson;
    private String photoUri;

    private ImageView photoImageView;
    private EditText nameEditText;
    private EditText relationEditText;
    private RadioGroup categoryRadioGroup;
    private RadioButton familyRadioButton;
    private RadioButton friendsRadioButton;
    private RadioButton relativesRadioButton;
    private Spinner categorySpinner;
    private Button selectPhotoButton;
    private Button saveButton;
    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        // Initialize database helper
        dbHelper = WhosWhoDbHelper.getInstance(this);

        // Initialize views
        photoImageView = findViewById(R.id.photoImageView);
        nameEditText = findViewById(R.id.nameEditText);
        relationEditText = findViewById(R.id.relationEditText);
        categoryRadioGroup = findViewById(R.id.categoryRadioGroup);
        familyRadioButton = findViewById(R.id.familyRadioButton);
        friendsRadioButton = findViewById(R.id.friendsRadioButton);
        relativesRadioButton = findViewById(R.id.relativesRadioButton);
        categorySpinner = findViewById(R.id.categorySpinner);
        selectPhotoButton = findViewById(R.id.selectPhotoButton);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);

        // Set up category spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        // Check if we're editing an existing person
        long personId = getIntent().getLongExtra("personId", -1);
        if (personId != -1) {
            existingPerson = dbHelper.getPerson(personId);
            if (existingPerson != null) {
                loadPersonData();
            }
        }

        // Set click listeners
        selectPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePerson();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Load existing person data into the form
     */
    private void loadPersonData() {
        nameEditText.setText(existingPerson.getName());
        relationEditText.setText(existingPerson.getRelation());
        photoUri = existingPerson.getPhotoUri();

        // Set photo if available
        if (photoUri != null && !photoUri.isEmpty()) {
            photoImageView.setImageURI(Uri.parse(photoUri));
        }

        // Set category radio button
        String category = existingPerson.getCategory();
        if (category != null) {
            if (category.equals("Family")) {
                familyRadioButton.setChecked(true);
                categorySpinner.setSelection(0);
            } else if (category.equals("Friends")) {
                friendsRadioButton.setChecked(true);
                categorySpinner.setSelection(1);
            } else if (category.equals("Relatives")) {
                relativesRadioButton.setChecked(true);
                categorySpinner.setSelection(2);
            }
        }
    }

    /**
     * Open gallery to select a photo
     */
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            photoUri = imageUri.toString();
            photoImageView.setImageURI(imageUri);
        }
    }

    /**
     * Save person to database
     */
    private void savePerson() {
        String name = nameEditText.getText().toString().trim();
        String relation = relationEditText.getText().toString().trim();

        // Validate inputs
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (photoUri == null || photoUri.isEmpty()) {
            Toast.makeText(this, "Please select a photo", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedRadioButtonId = categoryRadioGroup.getCheckedRadioButtonId();
        if (selectedRadioButtonId == -1) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get selected category (using spinner value instead of radio buttons)
        String category = categorySpinner.getSelectedItem().toString();

        // Update or create person
        if (existingPerson != null) {
            existingPerson.setName(name);
            existingPerson.setPhotoUri(photoUri);
            existingPerson.setCategory(category);
            existingPerson.setRelation(relation);

            dbHelper.updatePerson(existingPerson);
            Toast.makeText(this, "Person updated", Toast.LENGTH_SHORT).show();
        } else {
            Person newPerson = new Person(name, photoUri, category, relation);
            long id = dbHelper.addPerson(newPerson);
            if (id != -1) {
                Toast.makeText(this, "Person added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error adding person", Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }
}
