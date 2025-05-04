package com.example.dashboard;

/**
 * Model class representing a person in the Who's Who game
 */
public class Person {
    private long id;
    private String name;
    private String photoUri;
    private String category; // "family", "friends", or "relatives"
    private String relation; // Optional: more specific relation

    // Default constructor
    public Person() {
    }

    // Constructor with all fields except id
    public Person(String name, String photoUri, String category, String relation) {
        this.name = name;
        this.photoUri = photoUri;
        this.category = category;
        this.relation = relation;
    }

    // Constructor with all fields
    public Person(long id, String name, String photoUri, String category, String relation) {
        this.id = id;
        this.name = name;
        this.photoUri = photoUri;
        this.category = category;
        this.relation = relation;
    }

    // Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", relation='" + relation + '\'' +
                '}';
    }
}

