package com.example.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Activity for selecting and managing photos for the Who's Who game
 */
public class WhosWhoPhotoSelectActivity extends AppCompatActivity {

    private WhosWhoDbHelper dbHelper;
    private RecyclerView peopleRecyclerView;
    private PersonAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whos_who_photo_select);

        // Initialize database helper
        dbHelper = WhosWhoDbHelper.getInstance(this);

        // Initialize views
        peopleRecyclerView = findViewById(R.id.peopleRecyclerView);
        Button addButton = findViewById(R.id.addButton);
        Button doneButton = findViewById(R.id.doneButton);

        // Setup recycler view
        peopleRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadPeopleList();

        // Set button click listeners
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddPersonActivity();
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload people list when returning to this screen
        loadPeopleList();
    }

    /**
     * Load the list of people from the database
     */
    private void loadPeopleList() {
        List<Person> people = dbHelper.getAllPeople();
        adapter = new PersonAdapter(people);
        peopleRecyclerView.setAdapter(adapter);
    }

    /**
     * Start activity to add a new person
     */
    private void startAddPersonActivity() {
        Intent intent = new Intent(this, AddItemActivity.class);
        startActivity(intent);
    }

    /**
     * Adapter for the people recycler view
     */
    private class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.PersonViewHolder> {

        private List<Person> people;

        public PersonAdapter(List<Person> people) {
            this.people = people;
        }

        @NonNull
        @Override
        public PersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_person, parent, false);
            return new PersonViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PersonViewHolder holder, int position) {
            Person person = people.get(position);
            holder.bind(person);
        }

        @Override
        public int getItemCount() {
            return people.size();
        }

        class PersonViewHolder extends RecyclerView.ViewHolder {
            private ImageView photoImageView;
            private TextView nameTextView;
            private TextView categoryTextView;

            public PersonViewHolder(@NonNull View itemView) {
                super(itemView);
                photoImageView = itemView.findViewById(R.id.photoImageView);
                nameTextView = itemView.findViewById(R.id.nameTextView);
                categoryTextView = itemView.findViewById(R.id.categoryTextView);

                // Set click listener for item
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            Person person = people.get(position);
                            // Start edit activity
                            Intent intent = new Intent(WhosWhoPhotoSelectActivity.this, AddItemActivity.class);
                            intent.putExtra("personId", person.getId());
                            startActivity(intent);
                        }
                    }
                });
            }

            public void bind(Person person) {
                nameTextView.setText(person.getName());
                categoryTextView.setText(person.getCategory());
                // Load image
                if (person.getPhotoUri() != null && !person.getPhotoUri().isEmpty()) {
                    photoImageView.setImageURI(android.net.Uri.parse(person.getPhotoUri()));
                }
            }
        }
    }
}