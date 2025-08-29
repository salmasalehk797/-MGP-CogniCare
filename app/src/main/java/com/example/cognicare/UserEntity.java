package com.example.cognicare;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "User")
public class UserEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String fullName;
    //private int age;
    private int numSiblings;
    private String motherName;
    private String fatherName;
    private String highschoolName;
    private String favoriteColor;
    private String birthdate;
    private int numChildren;
    private int numGrandchildren;
    private String currentLocation;

    private String caretakerName;

    private String email;

    private int phoneNumber;

    public UserEntity(){}

    public UserEntity(String fullName, int numSiblings, String motherName, String fatherName) {
        this.fullName = fullName;
        //this.age = age;
        this.numSiblings = numSiblings;
        this.motherName = motherName;
        this.fatherName = fatherName;
    }

    public void setId(long id){
        this.id = id;
    }
    public long getId(){
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /*public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }*/

    public int getNumSiblings() {
        return numSiblings;
    }

    public void setNumSiblings(int numSiblings) {
        this.numSiblings = numSiblings;
    }

    public String getMotherName() {
        return motherName;
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getHighschoolName() {
        return highschoolName;
    }

    public void setHighschoolName(String highschoolName) { this.highschoolName = highschoolName; }

    public String getFavoriteColor() {
        return favoriteColor;
    }

    public void setFavoriteColor(String favoriteColor) { this.favoriteColor = favoriteColor; }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) { this.birthdate = birthdate; }

    public int getNumChildren() {
        return numChildren;
    }

    public void setNumChildren(int numChildren) { this.numChildren = numChildren; }

    public int getNumGrandchildren() {
        return numGrandchildren;
    }

    public void setNumGrandchildren(int numGrandchildren) { this.numGrandchildren = numGrandchildren; }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) { this.currentLocation = currentLocation; }

    public String getCaretakerName() {
        return caretakerName;
    }

    public void setCaretakerName(String caretakerName) { this.caretakerName = caretakerName; }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) { this.email = email; }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) { this.phoneNumber = phoneNumber; }
}
