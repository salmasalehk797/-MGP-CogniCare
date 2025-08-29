package com.example.cognicare;

public class FormData {

    private String fullName;
    private int age;
    private int numSiblings;
    private String motherName;
    private String fatherName;

    public FormData(){}

    public FormData(String fullName, int age, int numSiblings, String motherName, String fatherName) {
        this.fullName = fullName;
        this.age = age;
        this.numSiblings = numSiblings;
        this.motherName = motherName;
        this.fatherName = fatherName;
    }

    // Getters and setters
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

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
}
