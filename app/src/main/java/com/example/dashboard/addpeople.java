package com.example.dashboard;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Activity for adding or editing a person
 */
public class AddPeople extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 2;

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
        setContentView(R.layout.activity_add_people);

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
                checkPermissionAndOpenGallery();
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

    private void checkPermissionAndOpenGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        } else {
            openGallery();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Permission denied to access storage", Toast.LENGTH_SHORT).show();
            }
        }
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
